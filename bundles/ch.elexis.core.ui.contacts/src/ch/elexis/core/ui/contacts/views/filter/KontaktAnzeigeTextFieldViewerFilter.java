/*******************************************************************************
 * Copyright (c) 2012 MEDEVIT <office@medevit.at>.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     MEDEVIT <office@medevit.at> - initial API and implementation
 ******************************************************************************/
package ch.elexis.core.ui.contacts.views.filter;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.elexis.core.model.IContact;

public class KontaktAnzeigeTextFieldViewerFilter extends ViewerFilter {

	private String searchString;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (searchString == null)
			return true;
		IContact k = (IContact) element;

		if (searchString.startsWith("$")) { //$NON-NLS-1$
			// Nothing to do here, we have a formula evaluation
			return true;
		} else if (searchString.startsWith("#")) { //$NON-NLS-1$
			// direct patient number lookup
			if (k.isPatient()) {
				String patNr = k.getCode();
				if (patNr.toLowerCase().equalsIgnoreCase(searchString.substring(1).trim()))
					return true;
			}
		} else {
			String desc1 = (k.getDescription1() != null) ? k.getDescription1().toLowerCase() : StringUtils.EMPTY;
			String desc2 = (k.getDescription2() != null) ? k.getDescription2().toLowerCase() : StringUtils.EMPTY;

			String[] searchListComma = searchString.split(","); //$NON-NLS-1$
			for (String string : searchListComma) {
				if (string.contains(StringUtils.SPACE)) {
					String searchA = desc1 + StringUtils.SPACE + desc2;
					String searchB = desc2 + StringUtils.SPACE + desc1;
					if (searchA.matches(".*" + string + ".*") || searchB.matches(".*" + string + ".*")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						return true;
				} else if (desc1.matches(".*" + string + ".*") || desc2.matches(".*" + string + ".*")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					return true;
				}
			}
		}

		return false;
	}

	public void setSearchText(String s) {
		if (s == null || s.length() == 0 || s.startsWith("#")) //$NON-NLS-1$
			searchString = s;
		else
			searchString = s.toLowerCase(); // $NON-NLS-1$ //$NON-NLS-2$
		// filter "dirty" characters
		if (searchString != null)
			searchString = searchString.replaceAll("[^#$, a-zA-Z0-9]", StringUtils.EMPTY); //$NON-NLS-1$
	}

}
