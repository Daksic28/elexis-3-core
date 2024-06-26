package ch.elexis.core.ui.locks;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.core.data.service.StoreToStringServiceHolder;
import ch.elexis.core.lock.types.LockResponse;
import ch.elexis.core.model.Identifiable;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.data.PersistentObject;

public class LockResponseHelper {

	public static void showInfo(LockResponse lr, Object object, Logger log) {
		if (object == null) {
			if (log == null) {
				log = LoggerFactory.getLogger(LockResponseHelper.class);
			}
			log.warn("showInfo for null object", new Throwable()); //$NON-NLS-1$
		}
		if (LockResponse.Status.NOINFO == lr.getStatus()) {
			return;
		}

		if (LockResponse.Status.DENIED_PERMANENT == lr.getStatus()) {
			SWTHelper.showError(Messages.DenyLockPermanent_Title, Messages.DenyLockPermanent_Message);
		} else {

			if (log != null) {
				log.warn("Unable to " + lr.getLockRequestType() + " lock for " //$NON-NLS-1$ //$NON-NLS-2$
						+ ((object != null) ? getStoreToString(object) : "null") + " - " + lr.getLockInfo().getUser() //$NON-NLS-1$ //$NON-NLS-2$
						+ "@" + lr.getLockInfo().getSystemUuid()); //$NON-NLS-1$
			}
			String format = MessageFormat.format(Messages.DenyLock_Message, getStoreToString(object),
					lr.getLockInfo().getUser() + "@" + lr.getLockInfo().getSystemUuid()); //$NON-NLS-1$
			SWTHelper.showError(Messages.DenyLock_Title, format);
		}
	}

	private static String getStoreToString(Object object) {
		if (object instanceof PersistentObject) {
			return ((PersistentObject) object).storeToString();
		} else if (object instanceof Identifiable) {
			return StoreToStringServiceHolder.get().storeToString((Identifiable) object)
					.orElseThrow(() -> new IllegalStateException("No storeToString for [" + object + "]")); //$NON-NLS-1$ //$NON-NLS-2$
		}
		throw new IllegalStateException("No storeToString for [" + object + "]"); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
