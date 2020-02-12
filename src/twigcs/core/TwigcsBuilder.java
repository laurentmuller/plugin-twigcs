package twigcs.core;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
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
