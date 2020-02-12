package twigcs.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import twigcs.TwigcsPlugin;
import twigcs.core.IConstants;

/**
 * A validator to ensure that a folder or a file is selected.
 *
 * @author Laurent Muller
 */
public class ResourceValidator
		implements ISelectionStatusValidator, IConstants {

	@Override
	public IStatus validate(final Object[] selection) {
		final IResource resource = getResource(selection);
		if (resource == null) {
			return TwigcsPlugin
					.createErrorStatus("A folder or a file must be selected.");
		}
		return Status.OK_STATUS;
	}

	private IResource getResource(final Object[] selection) {
		if (selection != null && selection.length != 0
				&& selection[0] instanceof IResource) {
			final IResource resource = (IResource) selection[0];
			if (resource instanceof IFolder || resource instanceof IFile) {
				return resource;
			}
		}
		return null;
	}
}
