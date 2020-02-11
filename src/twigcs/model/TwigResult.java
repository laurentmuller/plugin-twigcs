package twigcs.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Represents the result of parsing Twig files.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigResult implements Iterable<TwigFile> {

	/*
	 *  the number of violations
	 */
	@SerializedName("failures")
	private int violations;

	private String path;

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
	 * Gets the parsed files.
	 *
	 * @return the parsed files.
	 */
	public List<TwigFile> getFiles() {
		return files;
	}

	public String getPath() {
		return path;
	}

	/**
	 * Gets the number of violations.
	 *
	 * @return the number of violations.
	 */
	public int getViolations() {
		return violations;
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

	public void setPath(final String path) {
		this.path = path;
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
		Collections.sort(files);
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
		return String.format("%s{files: %d, violations: %d}", // $NON-NLS-1$
				name, size(), violations);
	}
}
