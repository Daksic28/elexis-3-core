package ch.elexis.core.ui.e4.locks;

public interface ILockHandler {
	/**
	 * Is called on lock acquired.
	 */
	public void lockAcquired();

	/**
	 * Is called on lock failed.
	 */
	public void lockFailed();
}
