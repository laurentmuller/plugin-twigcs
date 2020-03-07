/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

import java.util.Arrays;
import java.util.Objects;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Tree content provider to display the given project and the children folders.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class FolderContentProvider implements ITreeContentProvider {

	/*
	 * the empty resource array
	 */
	private static final IResource[] EMPTY_ARRAY = {};

	/*
	 * the root project
	 */
	private final IProject project;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param project
	 *            the root project.
	 */
	public FolderContentProvider(final IProject project) {
		this.project = project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(final Object element) {
		if (element instanceof IContainer) {
			final IResource[] children = getMembers((IContainer) element);
			if (children.length > 0) {
				return Arrays.stream(children).filter(this::select).toArray();
			}
		}
		return EMPTY_ARRAY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(final Object element) {
		// return getChildren(element);
		if (element instanceof IWorkspaceRoot) {
			return new IResource[] { project };
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(final Object element) {
		if (element instanceof IResource) {
			return ((IResource) element).getParent();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof IContainer) {
			final IResource[] children = getMembers((IContainer) element);
			if (children.length > 0) {
				for (final IResource child : children) {
					if (select(child)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns a list of existing member resources.
	 *
	 * @param container
	 *            the container to get members for.
	 * @return an array, maybe empty, of members of the container.
	 */
	private IResource[] getMembers(final IContainer container) {
		try {
			return container.members();
		} catch (final CoreException e) {
			return EMPTY_ARRAY;
		}
	}

	/**
	 * Returns if the given resource can be displayed.
	 *
	 * @param resource
	 *            the resource to check.
	 * @return <code>true</code> to display.
	 */
	private boolean select(final IResource resource) {
		return Objects.equals(resource, project) || resource instanceof IFolder;
	}
}
