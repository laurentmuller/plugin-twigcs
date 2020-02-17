/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import java.util.Arrays;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import nu.bibi.twigcs.internal.Messages;

/**
 * Handler to add or remove Twigcs nature.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsNatureHandler extends AbstractHandler implements IConstants {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object execute(final ExecutionEvent event)
			throws ExecutionException {
		final IProject project = findProject(event);
		if (project != null) {
			try {
				toggleNature(project);
			} catch (final CoreException e) {
				throw new ExecutionException(
						Messages.TwigcsNatureHandler_Error_Execute, e);
			}
		}

		return null;
	}

	/**
	 * Find the project
	 *
	 * @param event
	 *            the event where to search in.
	 * @return the project, if found; <code>null</code> otherwise.
	 */
	private IProject findProject(final ExecutionEvent event) {
		final IStructuredSelection selection = HandlerUtil
				.getCurrentStructuredSelection(event);

		for (final Object element : selection) {
			if (element instanceof IProject) {
				return (IProject) element;
			} else if (element instanceof IAdaptable) {
				final IProject project = ((IAdaptable) element)
						.getAdapter(IProject.class);
				if (project != null) {
					return project;
				}
			}
		}
		return null;
	}

	/**
	 * Toggles Twigcs nature of the given project
	 *
	 * @param project
	 *            the project to update.
	 * @throws CoreException
	 *             if an exception occurs while updating the project nature.
	 */
	private void toggleNature(final IProject project) throws CoreException {
		final IProjectDescription description = project.getDescription();
		final String[] natures = description.getNatureIds();

		// check if exist
		for (int i = 0; i < natures.length; ++i) {
			if (NATURE_ID.equals(natures[i])) {
				// remove the nature
				final String[] newNatures = new String[natures.length - 1];
				System.arraycopy(natures, 0, newNatures, 0, i);
				System.arraycopy(natures, i + 1, newNatures, i,
						natures.length - i - 1);
				description.setNatureIds(newNatures);
				project.setDescription(description, null);
				return;
			}
		}

		// add the nature
		final String[] newNatures = Arrays.copyOf(natures, natures.length + 1);
		newNatures[natures.length] = NATURE_ID;
		description.setNatureIds(newNatures);
		project.setDescription(description, null);
	}
}