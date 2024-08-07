package ch.elexis.core.data.service;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import ch.elexis.core.model.Identifiable;
import ch.elexis.core.services.IModelService;

@Component
public class CoreModelServiceHolder {

	private static IModelService modelService;

	@Reference(target = "(" + IModelService.SERVICEMODELNAME + "=ch.elexis.core.model)")
	public void setModelService(IModelService modelService) {
		CoreModelServiceHolder.modelService = modelService;
	}

	public static IModelService get() {
		if (modelService == null) {
			throw new IllegalStateException("No IModelService available");
		}
		return modelService;
	}

	public static <T> T reloadAs(Identifiable identifiable, Class<T> clazz) {
		return modelService.load(identifiable.getId(), clazz).get();
	}
}
