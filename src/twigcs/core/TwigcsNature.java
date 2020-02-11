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
		for (final ICommand command : cmds) {
			if (match(command)) {
				return;
			}
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
		final ICommand[] cmds = description.getBuildSpec();
		for (int i = 0; i < cmds.length; ++i) {
			if (match(cmds[i])) {
				final ICommand[] newCmds = new ICommand[cmds.length - 1];
				System.arraycopy(cmds, 0, newCmds, 0, i);
				System.arraycopy(cmds, i + 1, newCmds, i, cmds.length - i - 1);
				description.setBuildSpec(newCmds);
				project.setDescription(description, null);
				return;
			}
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

	private ICommand createCommand(final IProjectDescription desc) {
		final ICommand command = desc.newCommand();
		command.setBuilderName(BUILDER_ID);
		return command;
	}

	private boolean match(final ICommand command) {
		return command.getBuilderName().equals(BUILDER_ID);
	}
}
