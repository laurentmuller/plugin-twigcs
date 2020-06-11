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

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Twigcs project nature.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsNature implements IProjectNature, IConstants {

	/*
	 * the selected project
	 */
	private IProject project;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure() throws CoreException {
		final IProjectDescription desc = project.getDescription();
		ICommand[] cmds = desc.getBuildSpec();

		// add if not already set?
		if (Arrays.stream(cmds).noneMatch(this::isBuilderId)) {
			final int len = cmds.length;
			cmds = Arrays.copyOf(cmds, len + 1);
			cmds[len] = createCommand(desc);
			desc.setBuildSpec(cmds);
			project.setDescription(desc, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deconfigure() throws CoreException {
		final IProjectDescription description = project.getDescription();
		final ICommand[] oldCmds = description.getBuildSpec();
		final ICommand[] newCmds = Arrays.stream(oldCmds)
				.filter(this::isNotBuilderId).toArray(ICommand[]::new);

		// remove if set
		if (oldCmds.length != newCmds.length) {
			description.setBuildSpec(newCmds);
			project.setDescription(description, null);
			project.deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IProject getProject() {
		return project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProject(final IProject project) {
		this.project = project;
	}

	/**
	 * Creates a command with the Twigcs builder identifier.
	 *
	 * @param desc
	 *            the project description used to build command.
	 * @return the newly created command.
	 */
	private ICommand createCommand(final IProjectDescription desc) {
		final ICommand command = desc.newCommand();
		command.setBuilderName(BUILDER_ID);
		return command;
	}

	/**
	 * Checks if the builder name of the given command is equal to the Twigcs
	 * builder identifier.
	 *
	 * @param command
	 *            the command to validate.
	 * @return <code>true</code> if equal.
	 */
	private boolean isBuilderId(final ICommand command) {
		return BUILDER_ID.equals(command.getBuilderName());
	}

	/**
	 * Checks if the builder name of then given command is not equal to the
	 * Twigcs builder identifier.
	 *
	 * @param command
	 *            the command to validate.
	 * @return <code>true</code> if not equal.
	 */
	private boolean isNotBuilderId(final ICommand command) {
		return !isBuilderId(command);
	}
}
