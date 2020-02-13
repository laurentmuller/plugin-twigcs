/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.core;

import java.util.Arrays;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
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

		// already set?
		if (Arrays.stream(cmds).anyMatch(this::match)) {
			return;
		}

		// add
		cmds = Arrays.copyOf(cmds, cmds.length + 1);
		cmds[cmds.length - 1] = createCommand(desc);
		desc.setBuildSpec(cmds);
		project.setDescription(desc, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deconfigure() throws CoreException {
		final IProjectDescription description = getProject().getDescription();
		final ICommand[] oldCmds = description.getBuildSpec();
		final ICommand[] newCmds = Arrays.stream(oldCmds).filter(c -> !match(c))
				.toArray(ICommand[]::new);

		// set?
		if (oldCmds.length != newCmds.length) {
			description.setBuildSpec(newCmds);
			project.setDescription(description, null);
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
	 * Creates a command with the Twigcs builder-
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
	 * Checks if the given command builder name is equal to the Twigcs builder
	 * identifier.
	 *
	 * @param command
	 *            the command to validate.
	 * @return <code>true</code> if equal.
	 */
	private boolean match(final ICommand command) {
		return command.getBuilderName().equals(BUILDER_ID);
	}
}
