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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import nu.bibi.twigcs.internal.Messages;

/**
 * Twigcs project builder.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsBuilder extends IncrementalProjectBuilder
		implements IConstants {

	/**
	 * Gets the accessible projects which exist under the workspace.
	 *
	 * @return the projects.
	 * @see IProject#isAccessible()
	 */
	public static List<IProject> getProjects() {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject[] projects = root.getProjects();
		return Arrays.stream(projects).filter(IProject::isAccessible)
				.collect(Collectors.toList());
	}

	/**
	 * Trigger a clean build for all accessible projects. The build is invoked
	 * only for projects that have this nature.
	 *
	 * @throws CoreException
	 *             if the build fails.
	 * @see IncrementalProjectBuilder#CLEAN_BUILD
	 */
	public static void triggerCleanBuild() throws CoreException {
		final List<IProject> projects = getProjects();
		for (final IProject project : projects) {
			triggerCleanBuild(project);
		}
	}

	/**
	 * Trigger a clean build for the given project. The build is invoked only if
	 * the project has this nature.
	 *
	 * @param project
	 *            the project to build.
	 * @throws CoreException
	 *             if the build fails.
	 * @see IncrementalProjectBuilder#CLEAN_BUILD
	 */
	public static void triggerCleanBuild(final IProject project)
			throws CoreException {
		if (project.isAccessible() && project.hasNature(NATURE_ID)) {
			project.build(IncrementalProjectBuilder.CLEAN_BUILD, BUILDER_ID,
					null, null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected IProject[] build(final int kind, final Map<String, String> args,
			final IProgressMonitor monitor) throws CoreException {
		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			final IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return new IProject[] {};
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation of <code>TwigcsBuilder</code> delete all Twigcs
	 * markers.
	 * </p>
	 */
	@Override
	protected void clean(final IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	/**
	 * Gets the number of Twig files.
	 *
	 * @param project
	 *            the project to visit.
	 * @return the number of files.
	 * @throws CoreException
	 *             if the visit fails.
	 */
	private int countFiles(final IProject project) throws CoreException {
		final TwigCounterVisitor counter = new TwigCounterVisitor();
		project.accept(counter);
		return counter.getFiles();
	}

	/**
	 * Gets the number of Twig files.
	 *
	 * @param delta
	 *            the resource delta to visit.
	 * @return the number of files.
	 * @throws CoreException
	 *             if the visit fails.
	 */
	private int countFiles(final IResourceDelta delta) throws CoreException {
		final TwigCounterVisitor counter = new TwigCounterVisitor();
		delta.accept(counter);
		return counter.getFiles();
	}

	/**
	 * Runs the full build.
	 *
	 * @param monitor
	 *            the progress monitor.
	 * @throws CoreException
	 *             if the build fails.
	 */
	private void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		final IProject project = getProject();
		final int totalWork = countFiles(project);
		try {
			monitor.beginTask(Messages.TwigcsBuilder_Process_Files, totalWork);
			project.accept(new TwigcsValidationVisitor(project, monitor));
		} finally {
			monitor.done();
		}
	}

	/**
	 * Runs the incremental build.
	 *
	 * @param monitor
	 *            the progress monitor.
	 * @throws CoreException
	 *             if the build fails.
	 */
	private void incrementalBuild(final IResourceDelta delta,
			final IProgressMonitor monitor) throws CoreException {
		final IProject project = getProject();
		final int totalWork = countFiles(delta);
		try {
			monitor.beginTask(Messages.TwigcsBuilder_Process_Files, totalWork);
			delta.accept(new TwigcsValidationVisitor(project, monitor));
		} finally {
			monitor.done();
		}
	}
}
