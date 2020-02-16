/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import nu.bibi.twigcs.core.IConstants;

/**
 * Dialog to select a single {@link IFolder} from an {@link IProject}.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class FolderSelectionDialog extends ElementTreeSelectionDialog
		implements ISelectionStatusValidator, IConstants {

	/*
	 * the excluded resources
	 */
	private List<IResource> excludeResources = new ArrayList<>();

	/**
	 * Creates a new instance of this class. The files are not displayed.
	 *
	 * @param parent
	 *            the parent shell.
	 * @param project
	 *            the root project.
	 * @param excludeResources
	 *            the resources to exclude.
	 * @param selection
	 *            the initial selection.
	 */
	public FolderSelectionDialog(final Shell parent, final IProject project,
			final List<IResource> excludeResources, final Object selection) {
		super(parent, new WorkbenchLabelProvider(),
				new FolderContentProvider(project));
		setComparator(new ViewerComparator());
		setDoubleClickSelects(false);
		setHelpAvailable(false);
		setAllowMultiple(false);
		setValidator(this);

		// selection
		if (selection != null) {
			setInitialSelection(selection);
		} else {
			setInitialSelection(project);
		}

		// copy
		this.excludeResources = excludeResources;

		// input
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot root = workspace.getRoot();
		setInput(root);

		setTitle("Resource selection");
	}

	/**
	 * Gets the resources to excludes.
	 *
	 * @return the resources to excludes.
	 */
	public List<IResource> getExcludeResources() {
		return excludeResources;
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
	public IStatus validate(final Object[] selection) {
		if (!isValid(selection)) {
			return createErrorStatus("A folder must be selected.", null);
		} else if (isAlreadySelected(selection)) {
			return createErrorStatus("The folder is already selected.", null);
		}
		return Status.OK_STATUS;
	}

	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// protected TreeViewer createTreeViewer(final Composite parent) {
	// final TreeViewer viewer = super.createTreeViewer(parent);
	// provider = (ResourceContentProvider) viewer.getContentProvider();
	// provider.setExcludeResources(excludeResources);
	// viewer.refresh();
	//
	// return viewer;
	// }

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

	/**
	 * Returns if the given selection is contained within the exclude resources.
	 *
	 * @param selection
	 *            the selection to validate.
	 * @return <code>true</code> if match; <code>false</code> otherwise.
	 */
	private boolean isAlreadySelected(final Object[] selection) {
		for (final Object object : selection) {
			if (excludeResources.contains(object)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns if the given selection is valid.
	 *
	 * @param selection
	 *            the selection to validate.
	 * @return <code>true</code> if valid; <code>false</code> otherwise.
	 */
	private boolean isValid(final Object[] selection) {
		if (selection != null && selection.length != 0
				&& selection[0] instanceof IResource) {
			final Object element = selection[0];
			return element instanceof IFolder;
		}
		return false;
	}
}
