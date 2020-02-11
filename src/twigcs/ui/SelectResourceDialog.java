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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Dialog to select a single resource.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class SelectResourceDialog extends ElementTreeSelectionDialog {

	/**
	 * Creates a new instance of this class.
	 *
	 * @param parent
	 *            the parent shell.
	 * @param project
	 *            the root project.
	 */
	public SelectResourceDialog(Shell parent, IProject project) {
		super(parent, new WorkbenchLabelProvider(),
				new WorkbenchContentProvider());
		// DrillDownComposite composite = null;
		addFilter(new ResourceViewerFilter());
		setAllowMultiple(false);
		setInput(project);
	}

	/**
	 * Gets the selected resource.
	 *
	 * @return the resource, if any; <code>null</code> otherwise.
	 */
	public IResource getSelection() {
		final Object[] result = super.getResult();
		if (result.length > 0 && result[0] instanceof IResource) {
			return (IResource) result[0];
		}
		return null;
	}

}
