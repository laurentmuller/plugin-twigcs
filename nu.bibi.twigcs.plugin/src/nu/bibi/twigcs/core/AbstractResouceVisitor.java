/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

/**
 * Abstract resource visitor. Subclass must only implements the
 * {@link AbstractResouceVisitor#doVisit(org.eclipse.core.resources.IResource)}
 * function.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public abstract class AbstractResouceVisitor
		implements IResourceVisitor, IResourceDeltaVisitor {

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation of <code>AbstractResouceVisitor</code> checks that
	 * the given resource is not <code>null</code> and exists.
	 * </p>
	 */
	@Override
	public final boolean visit(final IResource resource) throws CoreException {
		if (resource != null && resource.isAccessible()) {
			return doVisit(resource);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation of <code>AbstractResouceVisitor</code> delegates to
	 * the {@link IResourceVisitor#visit(org.eclipse.core.resources.IResource)}
	 * function with the <code>delta</code> resource.
	 * </p>
	 *
	 * @see IResourceDelta#getResource()
	 */
	@Override
	public final boolean visit(final IResourceDelta delta)
			throws CoreException {
		return visit(delta.getResource());
	}

	/**
	 * Visits the given resource. When this function is called, the given
	 * resource is not <code>null</code> and exists. Subclass must override.
	 *
	 * @param resource
	 *            the resource to visit.
	 * @return <code>true</code> if the resource's members should be visited;
	 *         <code>false</code> if they should be skipped.
	 * @exception CoreException
	 *                if the visit fails for some reason.
	 */
	protected abstract boolean doVisit(IResource resource) throws CoreException;
}
