/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

import twigcs.core.IConstants;

/**
 * Extends the workbench content provider by displaying only folders and
 * optionally Twig files.
 *
 * @author Laurent Muller
 */
public class ResourceContentProvider extends BaseWorkbenchContentProvider
		implements IConstants {

	/*
	 * the root project
	 */
	private final IProject project;

	/*
	 * the display files option
	 */
	private boolean displayFiles = false;

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
	public ResourceContentProvider(final IProject project) {
		this.project = project;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(final Object element) {
		final Object[] children = super.getChildren(element);
		if (children.length > 0) {
			final List<Object> result = new ArrayList<>(children.length);
			for (final Object child : children) {
				if (select(child)) {
					result.add(child);
				}
			}
			return result.toArray();
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
	 * Returns if the files are displayed. The default value <code>false</code>.
	 *
	 * @return <code>true</code> to display files; <code>false</code> to hide.
	 */
	public boolean isDisplayFiles() {
		return displayFiles;
	}

	/**
	 * Sets if the files are display.
	 *
	 * @param displayFiles
	 *            <code>true</code> to display files; <code>false</code> to
	 *            hide.
	 */
	public void setDisplayFiles(final boolean displayFiles) {
		this.displayFiles = displayFiles;
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
	 * Checks that the given element can be selected.
	 *
	 * @param element
	 *            the element to verify.
	 * @return <code>true</code> if selected.
	 */
	private boolean select(final Object element) {
		// current project?
		if (element instanceof IProject) {
			return project == element;
		}

		// exclude?
		if (excludeResources.contains(element)) {
			return false;
		}

		if (element instanceof IFolder) {
			final IFolder container = (IFolder) element;
			return container.getName().charAt(0) != '.';
		} else if (element instanceof IFile && displayFiles) {
			final IFile file = (IFile) element;
			return TWIG_EXTENSION.equals(file.getFileExtension());
		} else {
			return false;
		}
	}

}
