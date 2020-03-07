/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import nu.bibi.twigcs.TwigcsPlugin;
import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.preferences.ProjectPreferences;

/**
 * Resource listener to update include and exclude paths.
 * <p>
 * This listener must register with the {@link IResourceChangeEvent#POST_CHANGE}
 * event mask like the following example:
 * </p>
 *
 * <pre>
 * ResourceListener listener = new ResourceListener();
 * IWorkspace workspace = ResourcesPlugin.getWorkspace();
 * workspace.addResourceChangeListener(listener,
 * 		IResourceChangeEvent.POST_CHANGE);
 * </pre>
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceListener implements IResourceChangeListener,
		IResourceDeltaVisitor, ICoreException {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		try {
			event.getDelta().accept(this);
		} catch (final CoreException e) {
			handleStatus(e.getStatus());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		// get children delta
		final IResourceDelta[] deltas = delta.getAffectedChildren(
				IResourceDelta.ADDED | IResourceDelta.REMOVED);
		if (deltas.length != 2) {
			return true;
		}

		// find paths.
		final IPath pathFrom = getPathFrom(deltas);
		final IPath pathTo = getPathTo(deltas);
		if (pathFrom == null || pathTo == null) {
			return true;
		}

		// find project
		final IProject project = getProject(delta);
		if (project == null) {
			return true;
		}

		// update
		process(project, pathFrom, pathTo);

		return true;
	}

	/**
	 * Gets the moved from path.
	 *
	 * @param deltas
	 *            the resource deltas to get path.
	 * @return the moved from path, if found; <code>null</code> otherwise.
	 * @see IResourceDelta#getMovedFromPath()
	 */
	private IPath getPathFrom(final IResourceDelta[] deltas) {
		for (final IResourceDelta delta : deltas) {
			if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
				return delta.getMovedFromPath();
			}
		}
		return null;
	}

	/**
	 * Gets the moved to path.
	 *
	 * @param deltas
	 *            the resource deltas to get path.
	 * @return the moved to path, if found; <code>null</code> otherwise.
	 * @see IResourceDelta#getMovedToPath()
	 */
	private IPath getPathTo(final IResourceDelta[] deltas) {
		for (final IResourceDelta delta : deltas) {
			if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
				return delta.getMovedToPath();
			}
		}
		return null;
	}

	/**
	 * Gets the project from the given resource delta.
	 *
	 * @param project
	 *            the project to verify.
	 * @return the project, if found and valid; <code>null</code> otherwise.
	 * @throws CoreException
	 *             if an error occurs while searching the project.
	 */
	private IProject getProject(final IResourceDelta delta)
			throws CoreException {
		// validate resource
		final IResource resource = delta.getResource();
		if (resource == null || !resource.isAccessible()
				|| !(resource instanceof IContainer)) {
			return null;
		}

		// validate project
		final IProject project = resource.getProject();
		if (project == null || !project.isAccessible()
				|| !project.hasNature(IConstants.NATURE_ID)) {
			return null;
		}

		return project;
	}

	/**
	 * Update preferences paths for the given project.
	 *
	 * @param project
	 *            the project to update.
	 * @param pathFrom
	 *            the from path (old path).
	 * @param pathTo
	 *            the to path (new path).
	 * @throws CoreException
	 *             if an error occurs while updating the project.
	 */
	private void process(final IProject project, final IPath pathFrom,
			final IPath pathTo) throws CoreException {
		// get paths
		final ProjectPreferences preferences = new ProjectPreferences(project);
		final List<IPath> includes = preferences.getIncludeRawPaths();
		final List<IPath> excludes = preferences.getExcludeRawPaths();

		// make relative
		final IPath fullPath = project.getFullPath();
		final IPath relativeFrom = pathFrom.makeRelativeTo(fullPath);
		final IPath relativeTo = pathTo.makeRelativeTo(fullPath);

		// update
		if (includes.contains(relativeFrom)) {
			includes.remove(relativeFrom);
			includes.add(relativeTo);
			preferences.setIncludePaths(includes);
		}
		if (excludes.contains(relativeFrom)) {
			excludes.remove(relativeFrom);
			excludes.add(relativeTo);
			preferences.setExcludePaths(excludes);
		}

		// if (preferences.isDirty()) {
		// preferences.flush();
		// }

		final String message = String.format(Messages.ResourceListener_Update, //
				pathFrom.toPortableString(), //
				pathTo.toPortableString());
		TwigcsPlugin.logInfo(message);
	}
}
