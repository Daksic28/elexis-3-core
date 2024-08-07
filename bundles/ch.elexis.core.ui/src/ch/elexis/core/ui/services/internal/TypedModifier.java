package ch.elexis.core.ui.services.internal;

import java.util.Optional;
import java.util.Set;

import ch.elexis.core.events.MessageEvent;
import ch.elexis.core.model.IContact;
import ch.elexis.core.model.ICoverage;
import ch.elexis.core.model.IEncounter;
import ch.elexis.core.model.IMandator;
import ch.elexis.core.model.IPatient;
import ch.elexis.core.model.IUser;
import ch.elexis.core.services.IContext;
import ch.elexis.core.services.ICoverageService;
import ch.elexis.core.services.IEncounterService;
import ch.elexis.core.services.IUserService;
import ch.elexis.core.services.holder.ContextServiceHolder;
import ch.elexis.core.utils.OsgiServiceUtil;

public class TypedModifier {

	private IUserService userService;

	private IEncounterService encounterService;

	private ICoverageService coverageService;

	private Context context;

	public TypedModifier(Context context) {
		this.context = context;
	}

	public void modifyFor(Object object) {
		if (object instanceof IPatient) {
			Optional<IEncounter> latestEncounter = getEncounterService().getLatestEncounter((IPatient) object);
			if (latestEncounter.isPresent()) {
				context.setTyped(latestEncounter.get(), true);
				context.setTyped(latestEncounter.get().getCoverage(), true);
			} else {
				context.removeTyped(IEncounter.class);
				context.removeTyped(ICoverage.class);
			}
		}
		if (object instanceof ICoverage) {
			Optional<IEncounter> latestEncounter = getCoverageService().getLatestEncounter((ICoverage) object);
			if (latestEncounter.isPresent()) {
				context.setTyped(latestEncounter.get(), true);
			} else {
				context.removeTyped(IEncounter.class);
			}
		}
		if (object instanceof IEncounter) {
			context.setTyped(((IEncounter) object).getCoverage(), true);
		}
		if (object instanceof IUser) {
			IUser user = (IUser) object;

			// also set active user contact
			IContact userContact = ((IUser) object).getAssignedContact();
			context.setNamed(IContext.ACTIVE_USERCONTACT, userContact);

			Optional<IMandator> defaultWorkingFor = getUserService().getDefaultExecutiveDoctorWorkingFor(user);
			if (defaultWorkingFor.isPresent()) {
				ContextServiceHolder.get().setActiveMandator(defaultWorkingFor.get());
			} else {
				Set<IMandator> workingFor = getUserService().getExecutiveDoctorsWorkingFor(user);
				if (!workingFor.isEmpty()) {
					ContextServiceHolder.get().setActiveMandator(workingFor.iterator().next());
				} else {
					MessageEvent.fireError("Kein Mandant definiert",
							"Sie können Elexis erst normal benutzen, wenn Sie für den Benutzer einen Mandanten definiert haben");
				}
			}
		}
	}

	public void modifyRemove(Class<?> clazz) {
		if (clazz.equals(IUser.class)) {
			ContextServiceHolder.get().setActivePatient(null);
			ContextServiceHolder.get().setActiveMandator(null);
		}
	}

	private IUserService getUserService() {
		if (userService == null) {
			userService = OsgiServiceUtil.getService(IUserService.class).get();
		}
		return userService;
	}

	private IEncounterService getEncounterService() {
		if (encounterService == null) {
			encounterService = OsgiServiceUtil.getService(IEncounterService.class).get();
		}
		return encounterService;
	}

	private ICoverageService getCoverageService() {
		if (coverageService == null) {
			coverageService = OsgiServiceUtil.getService(ICoverageService.class).get();
		}
		return coverageService;
	}
}
