package ch.elexis.core.ui.util;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class DelegatingSelectionProvider implements ISelectionProvider {
	private final ListenerList selectionListeners = new ListenerList();

	private final ListenerList postSelectionListeners = new ListenerList();

	private ISelectionProvider delegate;

	private ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelectionProvider() == delegate) {
				fireSelectionChanged(event.getSelection());
			}
		}
	};

	private ISelectionChangedListener postSelectionListener = new ISelectionChangedListener() {
		public void selectionChanged(SelectionChangedEvent event) {
			if (event.getSelectionProvider() == delegate) {
				firePostSelectionChanged(event.getSelection());
			}
		}
	};

	/**
	 * Sets a new selection provider to delegate to. Selection listeners registered
	 * with the previous delegate are removed before.
	 *
	 * @param newDelegate new selection provider
	 */
	public void setSelectionProviderDelegate(ISelectionProvider newDelegate) {
		if (delegate == newDelegate) {
			return;
		}
		if (delegate != null) {
			delegate.removeSelectionChangedListener(selectionListener);
			if (delegate instanceof IPostSelectionProvider) {
				((IPostSelectionProvider) delegate).removePostSelectionChangedListener(postSelectionListener);
			}
		}
		delegate = newDelegate;
		if (newDelegate != null) {
			newDelegate.addSelectionChangedListener(selectionListener);
			if (newDelegate instanceof IPostSelectionProvider) {
				((IPostSelectionProvider) newDelegate).addPostSelectionChangedListener(postSelectionListener);
			}
			fireSelectionChanged(newDelegate.getSelection());
			firePostSelectionChanged(newDelegate.getSelection());
		}
	}

	protected void fireSelectionChanged(ISelection selection) {
		fireSelectionChanged(selectionListeners, selection);
	}

	protected void firePostSelectionChanged(ISelection selection) {
		fireSelectionChanged(postSelectionListeners, selection);
	}

	private void fireSelectionChanged(ListenerList list, ISelection selection) {
		SelectionChangedEvent event = new SelectionChangedEvent(delegate, selection);
		Object[] listeners = list.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
			listener.selectionChanged(event);
		}
	}

	// IPostSelectionProvider Implementation

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
		postSelectionListeners.add(listener);
	}

	public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
		postSelectionListeners.remove(listener);
	}

	public ISelection getSelection() {
		return delegate == null ? null : delegate.getSelection();
	}

	public void setSelection(ISelection selection) {
		if (delegate != null) {
			delegate.setSelection(selection);
		}
	}
}
