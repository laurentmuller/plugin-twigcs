/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.core;

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

import twigcs.TwigcsPlugin;

/**
 * Resource listener to update include and exclude paths.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceListener
		implements IResourceChangeListener, IResourceDeltaVisitor {

	/**
	 * Creates a new instance of this class.
	 */
	public ResourceListener() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		// post change?
		if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
			return;
		}

		try {
			final IResourceDelta delta = event.getDelta();
			delta.accept(this);
		} catch (final CoreException e) {
			TwigcsPlugin.handleError(e.getStatus());
		}
	}

	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		// get children delta
		final IResourceDelta[] deltas = delta.getAffectedChildren(
				IResourceDelta.ADDED | IResourceDelta.REMOVED);
		if (deltas.length != 2) {
			return true;
		}

		// find the move from and the move to paths.
		final IPath fromPath = getFromPath(deltas);
		final IPath toPath = getToPath(deltas);
		if (fromPath == null || toPath == null) {
			return true;
		}

		// find project
		final IProject project = getProject(delta);
		if (project == null) {
			return true;
		}

		// update
		process(project, fromPath, toPath);

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
	private IPath getFromPath(final IResourceDelta[] deltas) {
		for (final IResourceDelta delta : deltas) {
			if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
				return delta.getMovedFromPath();
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
		if (resource == null || !resource.exists()
				|| !(resource instanceof IContainer)) {
			return null;
		}

		// validate project
		final IProject project = resource.getProject();
		if (project == null || !project.exists()
				|| !project.hasNature(IConstants.NATURE_ID)) {
			return null;
		}

		return project;
	}

	/**
	 * Gets the moved to path.
	 *
	 * @param deltas
	 *            the resource deltas to get path.
	 * @return the moved to path, if found; <code>null</code> otherwise.
	 * @see IResourceDelta#getMovedToPath()
	 */
	private IPath getToPath(final IResourceDelta[] deltas) {
		for (final IResourceDelta delta : deltas) {
			if ((delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
				return delta.getMovedToPath();
			}
		}
		return null;
	}

	/**
	 * Update preferences paths for the given project.
	 *
	 * @param project
	 *            the project to update.
	 * @param fromPath
	 *            the from path (old path).
	 * @param toPath
	 *            the to path (new path).
	 * @throws CoreException
	 *             if an error occurs while updating the project.
	 */
	private void process(final IProject project, IPath fromPath, IPath toPath)
			throws CoreException {
		// get paths
		final ProjectPreferences preferences = new ProjectPreferences(project);
		final List<IPath> includes = preferences.getIncludeRawPaths();
		final List<IPath> excludes = preferences.getExcludeRawPaths();

		// make relative
		final IPath fullPath = project.getFullPath();
		fromPath = fromPath.makeRelativeTo(fullPath);
		toPath = toPath.makeRelativeTo(fullPath);

		// update
		boolean changed = false;
		if (includes.contains(fromPath)) {
			includes.remove(fromPath);
			includes.add(toPath);
			preferences.setIncludePaths(includes);
			changed = true;
		}
		if (excludes.contains(fromPath)) {
			excludes.remove(fromPath);
			excludes.add(toPath);
			preferences.setExcludePaths(excludes);
			changed = true;
		}

		System.out.format("'%s' -> '%s'%n%n", //
				fromPath.toPortableString(), //
				toPath.toPortableString());

		// changed?
		if (changed) {
			System.out.println("Updated");
		}
	}
}
