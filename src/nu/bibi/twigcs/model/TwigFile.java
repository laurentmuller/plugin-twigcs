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
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a parsed Twig file.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigFile implements Iterable<TwigViolation>, Comparable<TwigFile> {

	/*
	 * the parsed file
	 */
	@SerializedName("file")
	private String path;

	/*
	 * the violations
	 */
	private final List<TwigViolation> violations;

	/**
	 * Creates a new instance of this class.
	 */
	public TwigFile() {
		violations = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final TwigFile o) {
		return getPath().compareToIgnoreCase(o.getPath());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TwigFile other = (TwigFile) obj;
		return Objects.equals(path, other.path);
	}

	/**
	 * Gets the path.
	 *
	 * @return the path.
	 */
	public String getPath() {
		return path == null ? "" : path;
	}

	/**
	 * Gets the violations.
	 *
	 * @return the violations.
	 */
	public List<TwigViolation> getViolations() {
		return violations;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return Objects.hash(path);
	}

	/**
	 * Returns if this list contains no violations.
	 *
	 * @return <tt>true</tt> if empty.
	 */
	public boolean isEmpty() {
		return violations.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<TwigViolation> iterator() {
		return violations.iterator();
	}

	/**
	 * Sets the path.
	 *
	 * @param path
	 *            the path to set.
	 */
	public void setPath(final String path) {
		this.path = path;
	}

	/**
	 * Returns the number of violations.
	 *
	 * @return the number of violations.
	 */
	public int size() {
		return violations.size();
	}

	/**
	 * Sort violations.
	 */
	public void sort() {
		Collections.sort(violations);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final String name = getClass().getSimpleName();
		return String.format("%s{path: \"%s\", violations: %d}", // $NON-NLS-1$
				name, path, size());
	}
}
