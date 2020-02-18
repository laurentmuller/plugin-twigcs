/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
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
import nu.bibi.twigcs.internal.Messages;

/**
 * Dialog to select a single folder within a project.
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
	 * Creates a new instance of this class.
	 *
	 * @param parent
	 *            the parent shell.
	 * @param project
	 *            the root project.
	 * @param excludeResources
	 *            the resources to exclude.
	 * @param selection
	 *            the initial selection or <code>null</code> to select the
	 *            project.
	 */
	public FolderSelectionDialog(final Shell parent, final IProject project,
			final List<IResource> excludeResources, final IResource selection) {
		super(parent, new WorkbenchLabelProvider(),
				new FolderContentProvider(project));
		setInitialSelection(selection == null ? project : selection);
		setComparator(new ViewerComparator(Collator.getInstance()));
		setTitle(Messages.FolderSelectionDialog_Title);
		setDoubleClickSelects(false);
		setHelpAvailable(false);
		setAllowMultiple(false);
		setValidator(this);

		// copy
		this.excludeResources = excludeResources;

		// input
		setInput(ResourcesPlugin.getWorkspace().getRoot());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation cast the first result, if any, to an
	 * {@link IResource}.
	 * </p>
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
		if (selection.length == 0) {
			return createErrorStatus(
					Messages.FolderSelectionDialog_Error_No_Selection, null);
		}
		for (final Object element : selection) {
			if (!(element instanceof IFolder)) {
				return createInvalidStatus(
						Messages.FolderSelectionDialog_Error_Not_A_Folder,
						element);
			} else if (excludeResources.contains(element)) {
				return createInvalidStatus(
						Messages.FolderSelectionDialog_Error_Already_Selected,
						element);
			}
		}
		return Status.OK_STATUS;
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

	/**
	 * Creates an error status for the given format and element.
	 *
	 * @param format
	 *            the format string.
	 * @param Object
	 *            the element.
	 * @return the error status.
	 */
	private IStatus createInvalidStatus(final String format,
			final Object element) {
		String argument;
		if (element instanceof IResource) {
			argument = ((IResource) element).getName();
		} else {
			argument = element.toString();
		}
		final String message = String.format(format, argument);
		return createErrorStatus(message, null);
	}
}
