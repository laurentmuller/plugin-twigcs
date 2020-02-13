/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import twigcs.core.IConstants;

/**
 * A selection status validator to ensure that an {@link IFolder} or an
 * {@link IFile} is selected.
 *
 * @author Laurent Muller
 */
public class ResourceValidator
		implements ISelectionStatusValidator, IConstants {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IStatus validate(final Object[] selection) {
		if (!isValid(selection)) {
			return createErrorStatus("A folder or a file must be selected.",
					null);
		}
		return Status.OK_STATUS;
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
			return element instanceof IFolder || element instanceof IFile;
		}
		return false;
	}
}
