package ch.elexis.core.findings.util.internal;

import org.apache.commons.lang3.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public abstract class FindingsFormat {

	protected HashMap<String, Map<String, JsonStructuralFeature>> resourceFieldsMap = new HashMap<>();

	protected GsonBuilder gsonBuilder = new GsonBuilder();

	protected Gson getGson() {
		return gsonBuilder.create();
	}

	protected JsonObject getJsonObject(String content) {
		return getGson().fromJson(content, JsonObject.class);
	}

	public HashMap<String, Map<String, JsonStructuralFeature>> getResourceFieldsMap() {
		return resourceFieldsMap;
	}

	public abstract int isFindingsFormat(String rawContent);

	public abstract Optional<String> convertToCurrentFormat(String rawContent);

	protected int checkFields(Map<String, JsonStructuralFeature> structuralFeatureMap, JsonObject jsonObject) {
		int matches = 0;
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			JsonStructuralFeature structuralFeature = structuralFeatureMap.get(entry.getKey());
			if (structuralFeature != null && structuralFeature.isSameType(entry.getValue())) {
				matches++;
			}
		}
		return matches;
	}

	protected boolean checkRequiredField(JsonStructuralFeature jsonStructuralFeature, JsonObject jsonObject) {
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			if (jsonStructuralFeature != null && jsonStructuralFeature.getName().equals(entry.getKey())
					&& jsonStructuralFeature.isSameType(entry.getValue())) {
				return true;
			}
		}
		return false;
	}

	protected Optional<String> convert(Map<String, JsonStructuralFeatureTransformation> transformMap,
			JsonObject jsonObject) {
		JsonObject newObject = new JsonObject();

		Map<String, JsonStructuralFeatureTransformation> addAfterMap = transformMap.entrySet().stream()
				.filter(e -> e.getKey().startsWith("!addAfter!"))
				.collect(Collectors.toMap(e -> e.getKey().replace("!addAfter!", StringUtils.EMPTY), e -> e.getValue()));

		transformMap = transformMap.entrySet().stream().filter(e -> !e.getKey().startsWith("!addAfter!"))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			JsonStructuralFeatureTransformation transformation = transformMap.get(entry.getKey());
			if (transformation != null) {
				newObject.add(transformation.transformKey(entry.getKey()),
						transformation.transformValue(entry.getValue()));
			} else {
				newObject.add(entry.getKey(), entry.getValue());
			}
			JsonStructuralFeatureTransformation addAfterTransformation = addAfterMap.get(entry.getKey());
			if (addAfterTransformation != null) {
				addAfterTransformation.transformValue(newObject);
			}
		}
		return Optional.of(getGson().toJson(newObject));
	}
}
