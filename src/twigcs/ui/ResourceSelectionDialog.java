/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Dialog to select a single folder or file.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceSelectionDialog extends ElementTreeSelectionDialog {

	/**
	 * Creates a new instance of this class.
	 *
	 * @param parent
	 *            the parent shell.
	 * @param project
	 *            the root project.
	 * @param selection
	 *            the initial selection.
	 */
	public ResourceSelectionDialog(final Shell parent, final IProject project,
			final Object selection) {
		super(parent, new WorkbenchLabelProvider(),
				new ResourceContentProvider(project));
		setComparator(new ResourceViewerComparator());
		setValidator(new ResourceValidator());
		setDoubleClickSelects(false);
		setHelpAvailable(false);
		setAllowMultiple(false);

		// selection
		if (selection != null) {
			setInitialSelection(selection);
		} else {
			setInitialSelection(project);
		}

		// input
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot root = workspace.getRoot();
		setInput(root);

		setMessage("&Choose a folder or a file:");
		setTitle("Resource selection");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IResource getFirstResult() {
		final Object result = super.getFirstResult();
		return result instanceof IResource ? (IResource) result : null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TreeViewer doCreateTreeViewer(final Composite parent,
			final int style) {
		final TreeViewer viewer = super.doCreateTreeViewer(parent, style);
		viewer.setAutoExpandLevel(2);
		return viewer;
	}
}
