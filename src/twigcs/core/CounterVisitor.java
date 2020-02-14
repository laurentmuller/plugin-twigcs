/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.core;

import java.util.function.Predicate;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Resource visitor to count folders and files.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class CounterVisitor extends AbstractResouceVisitor {

	/*
	 * the visited files
	 */
	private int files = 0;

	/*
	 * the visited folders
	 */
	private int folders = 0;

	private final Predicate<IFile> filter;

	/**
	 * Creates a new instance of this class.
	 */
	public CounterVisitor() {
		this(null);
	}

	/**
	 * Creates a new instance of this class.
	 *
	 * @param filter
	 *            the file filter or <code>null</code> to count all files.
	 */
	public CounterVisitor(final Predicate<IFile> filter) {
		this.filter = filter;
	}

	/**
	 * Gets the number of visited files.
	 *
	 * @return the number of files.
	 */
	public int getFiles() {
		return files;
	}

	/**
	 * Gets the number of visited folders.
	 *
	 * @return the number of folders.
	 */
	public int getFolders() {
		return folders;
	}

	/**
	 * Gets the total number of visited folders and files
	 *
	 * @return the total number.
	 */
	public int getTotal() {
		return folders + files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(final IResource resource) throws CoreException {
		if (resource.exists()) {
			if (resource instanceof IFolder) {
				folders++;
			} else if (resource instanceof IFile) {
				if (filter == null || filter.test((IFile) resource)) {
					files++;
				}
			}
		}
		return true;
	}
}
