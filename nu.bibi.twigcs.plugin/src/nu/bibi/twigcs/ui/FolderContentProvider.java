/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

import nu.bibi.twigcs.core.IConstants;

/**
 * Extends the workbench content provider by displaying the {@link IFolder}
 * elements only for the given {@link IProject}.
 *
 * @author Laurent Muller
 */
public class FolderContentProvider extends BaseWorkbenchContentProvider
		implements IConstants {

	/*
	 * the root project
	 */
	private final IProject project;

	/*
	 * the excluded resources
	 */
	private List<IResource> excludeResources = new ArrayList<>();

	/**
	 * Creates a new instance of this class.
	 * <p>
	 * By default, the files are not displayed and no resources are excluded.
	 * </p>
	 *
	 * @param project
	 *            the root project
	 */
	public FolderContentProvider(final IProject project) {
		this.project = project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(final Object element) {
		final Object[] children = super.getChildren(element);
		if (children.length > 0) {
			return Arrays.stream(children).filter(this::select).toArray();
		}

		return children;
	}

	/**
	 * Gets the resources to excludes.
	 *
	 * @return the resources to excludes.
	 */
	public List<IResource> getExcludeResources() {
		return excludeResources;
	}

	/**
	 * Sets the resources to exclude.
	 *
	 * @param excludeResources
	 *            the resources to exclude.
	 */
	public void setExcludeResources(final List<IResource> excludeResources) {
		if (excludeResources == null) {
			this.excludeResources = new ArrayList<>();
		} else {
			this.excludeResources = excludeResources;
		}
	}

	/**
	 * Checks that the given element can be displayed.
	 *
	 * @param element
	 *            the element to verify.
	 * @return <code>true</code> if selected.
	 */
	private boolean select(final Object element) {
		// current project?
		if (element instanceof IProject) {
			return project == element;
		} else if (element instanceof IFolder) {
			// ignore hidden folders
			// final IFolder container = (IFolder) element;
			// return container.getName().charAt(0) != '.';
			return true;
		} else {
			return false;
		}

		// exclude?
		// if (excludeResources.contains(element)) {
		// return false;
		// }
	}
}
