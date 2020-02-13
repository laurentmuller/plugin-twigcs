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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

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
		final IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		return Arrays.stream(projects).filter(IProject::isAccessible)
				.collect(Collectors.toList());
	}

	/**
	 * Invokes the build method of the this builder for all accessible project.
	 *
	 * @param kind
	 *            the kind of build being requested. Valid values are:
	 *            <ul>
	 *            <li>{@link IncrementalProjectBuilder#FULL_BUILD}- indicates a
	 *            full build.</li>
	 *            <li>{@link IncrementalProjectBuilder#INCREMENTAL_BUILD}-
	 *            indicates a incremental build.</li>
	 *            <li>{@link IncrementalProjectBuilder#CLEAN_BUILD}- indicates a
	 *            clean request. Clean does not actually build anything, but
	 *            rather discards all problems and build states.</li>
	 *            </ul>
	 * @throws CoreException
	 *             if the build fails.
	 */
	public static void triggerBuild(final int kind) throws CoreException {
		final List<IProject> projects = getProjects();
		for (final IProject project : projects) {
			project.build(kind, BUILDER_ID, null, null);
		}
	}

	/**
	 * Trigger a clean build for all accessible project.
	 *
	 * @throws CoreException
	 *             if the build fails.
	 * @see IncrementalProjectBuilder#CLEAN_BUILD
	 */
	public static void triggerCleanBuild() throws CoreException {
		triggerBuild(IncrementalProjectBuilder.CLEAN_BUILD);
	}

	/**
	 * Trigger a full build for all accessible project.
	 *
	 * @throws CoreException
	 *             if the build fails.
	 * @see IncrementalProjectBuilder#FULL_BUILD
	 */
	public static void triggerFullBuild() throws CoreException {
		triggerBuild(IncrementalProjectBuilder.FULL_BUILD);
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
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void clean(final IProgressMonitor monitor) throws CoreException {
		getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
	}

	/**
	 * Create a full build.
	 *
	 * @param monitor
	 *            the progress monitor.
	 * @throws CoreException
	 *             if an exception occurs.
	 */
	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		getProject().accept(new ResourceVisitor(getPreferences()));
	}

	/**
	 * Create a incremental build.
	 *
	 * @param monitor
	 *            the progress monitor.
	 * @throws CoreException
	 *             if an exception occurs.
	 */
	protected void incrementalBuild(final IResourceDelta delta,
			final IProgressMonitor monitor) throws CoreException {
		delta.accept(new ResourceVisitor(getPreferences()));
	}

	/**
	 * Gets the project preferences.
	 *
	 * @return the project preferences.
	 */
	private ProjectPreferences getPreferences() {
		final IProject project = getProject();
		return new ProjectPreferences(project);
	}
}
