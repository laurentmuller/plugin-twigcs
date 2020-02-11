/******************************************************************************
 * Copyright (c) 2020 HaslerRail AG. All rights reserved.
 *
 * This computer code is protected by copyright law and international
 * treaties. Unauthorised reproduction or distribution of this code, or
 * any portion of it, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * All additional information on our web site: http://www.haslerrail.com
 *
 * Contributors:
 *     Laurent Muller - initial API and implementation
 *******************************************************************************/
package twigcs.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import twigcs.core.IConstants;

/**
 * A viewer filter for {@link IResource}.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceViewerFilter extends ViewerFilter {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IContainer) {
			return true;
		} else if (element instanceof IFile) {
			final IFile file = (IFile) element;
			return IConstants.TWIG_EXTENSION.equals(file.getFileExtension());
		} else {
			return false;
		}
	}
}
