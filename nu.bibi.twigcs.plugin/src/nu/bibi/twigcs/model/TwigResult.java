/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the result of parsing Twig files.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigResult implements Iterable<TwigFile> {

	/*
	 * the number of failures (violations)
	 */
	private int failures;

	/*
	 * the parsed files
	 */
	private final List<TwigFile> files;

	/**
	 * Creates a new instance of this class.
	 */
	public TwigResult() {
		files = new ArrayList<>();
	}

	/**
	 * Appends the specified file to the end of this list of files.
	 *
	 * @param file
	 *            the file to be appended.
	 * @return <tt>true</tt> if this list of files changed as a result of the
	 *         call.
	 */
	public boolean addFile(final TwigFile file) {
		return file != null && files.add(file);
	}

	/**
	 * Gets the first file result.
	 *
	 * @return the first file, if not empty; <code>null</code> otherwise.
	 */
	public TwigFile first() {
		if (!files.isEmpty()) {
			return files.get(0);
		}
		return null;
	}

	/**
	 * Gets the number of failures (violations).
	 *
	 * @return the number of failures.
	 */
	public int getFailures() {
		return failures;
	}

	/**
	 * Gets the parsed files.
	 *
	 * @return the parsed files.
	 */
	public List<TwigFile> getFiles() {
		return files;
	}

	/**
	 * Returns if this list contains no files.
	 *
	 * @return <tt>true</tt> if empty.
	 */
	public boolean isEmpty() {
		return files.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<TwigFile> iterator() {
		return files.iterator();
	}

	/**
	 * Sets the number of failures (violations).
	 *
	 * @param failures
	 *            the number of failures to set.
	 */
	public void setFailures(final int failures) {
		this.failures = failures;
	}

	/**
	 * Returns the number of files.
	 *
	 * @return the number of files.
	 */
	public int size() {
		return files.size();
	}

	/**
	 * Sort files and violations.
	 */
	public void sort() {
		if (files.size() > 1) {
			Collections.sort(files);
		}
		for (final TwigFile file : files) {
			file.sort();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final String name = getClass().getSimpleName();
		return String.format("%s{files: %d, failures: %d}", //$NON-NLS-1$
				name, size(), failures);
	}
}
