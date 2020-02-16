/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Link;

/**
 * Abstract resource visitor. Subclass must only implements the {@link Link
 * IResourceVisitor#visit(org.eclipse.core.resources.IResource)} function.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public abstract class AbstractResouceVisitor
		implements IResourceVisitor, IResourceDeltaVisitor {

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation delegate to the
	 * {@link IResourceVisitor#visit(org.eclipse.core.resources.IResource)}
	 * function with the <code>delta</code> resource.
	 * </p>
	 */
	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		return visit(delta.getResource());
	}
}
