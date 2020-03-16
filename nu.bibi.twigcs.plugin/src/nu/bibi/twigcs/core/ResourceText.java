/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;

import nu.bibi.twigcs.internal.Messages;

/**
 * Wrapper class for the {@link IFile} content. This class is used to read the
 * content and to track line offsets.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceText implements ICoreException {

	/*
	 * the line feed character
	 */
	private static final int LINE_FEED = '\n';

	/*
	 * the carriage return character
	 */
	private static final int CARRIAGE_RETURN = '\r';

	/*
	 * the content
	 */
	private byte[] content = {};

	/*
	 * the line offsets
	 */
	private int[] offsets = {};

	/**
	 * Creates a new instance of this class.
	 *
	 * @param file
	 *            the file to get contents.
	 * @throws CoreException
	 *             if the get contents method fails.
	 */
	public ResourceText(final IFile file) throws CoreException {
		final int contentLength = (int) file.getLocation().toFile().length();
		if (contentLength > 0) {
			try (InputStream stream = file.getContents()) {
				readContent(stream, contentLength);
			} catch (final IOException e) {
				final String msg = NLS.bind(Messages.ResourceText_Error_Read,
						file.getName());
				throw createCoreException(msg, e);
			}
		}
	}

	/**
	 * Gets the number of lines.
	 *
	 * @return the number of lines.
	 */
	public int count() {
		return offsets.length;
	}

	/**
	 * Gets the file content.
	 *
	 * @return the file content.
	 */
	public byte[] getContent() {
		return content;
	}

	/**
	 * Returns the length for the given line index, including line break
	 * characters.
	 *
	 * @param index
	 *            the zero-relative line index.
	 * @return the line length.
	 * @throws IndexOutOfBoundsException
	 *             if the index is less than 0 or greater than or equal to the
	 *             number of this lines.
	 * @see #count()
	 */
	public int getLinelength(final int index) {
		checkLineIndex(index);

		int nextOffset = content.length;
		if (index < count()) {
			nextOffset = offsets[index + 1];
		}
		return nextOffset - offsets[index];
	}

	/**
	 * Returns the offset for the given line index.
	 *
	 * @param index
	 *            the zero-relative line index.
	 * @return the line offset.
	 * @throws IndexOutOfBoundsException
	 *             if the index is less than 0 or greater than or equal to the
	 *             number of this lines.
	 * @see #count()
	 */
	public int getOffset(final int index) {
		checkLineIndex(index);
		return offsets[index];
	}

	/**
	 * Add a line offset.
	 *
	 * @param offset
	 *            the the zero-relative line offset.
	 * @return the number of lines.
	 */
	private void addOffset(final int offset, final int count) {
		final int len = offsets.length;
		if (count >= len) {
			offsets = Arrays.copyOf(offsets, len + 100);
		}
		offsets[count] = offset;
	}

	/**
	 * Check if the given line index is within this number of lines.
	 *
	 * @param index
	 *            the zero-relative line index to validate.
	 * @throws IndexOutOfBoundsException
	 *             if the index is less than 0 or greater than or equal to the
	 *             number of lines.
	 */
	private void checkLineIndex(final int index) {
		if (index < 0 || index >= count()) {
			throw new IndexOutOfBoundsException(
					NLS.bind(Messages.ResourceText_Error_Index, index));
		}
	}

	/**
	 * Reads all the content.
	 *
	 * @param stream
	 *            the input stream to read data from.
	 * @param contentLength
	 *            the content length.
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	private void readContent(final InputStream stream, final int contentLength)
			throws IOException {
		// read all
		content = new byte[contentLength];
		stream.read(content);

		// compute lines
		byte ch;
		byte previous = 0;
		int count = 1;

		for (int i = 0; i < contentLength; i++) {
			ch = content[i];
			if (ch == LINE_FEED) {
				// handle Linux (LF) and Windows (CR+LF)
				addOffset(i + 1, count++);
			} else if (previous == CARRIAGE_RETURN && ch != LINE_FEED) {
				// handle Mac (CR)
				addOffset(i, count++);
			}
			previous = ch;
		}

		// check last character (Mac)
		if (previous == CARRIAGE_RETURN) {
			addOffset(contentLength, count++);
		}

		// trim
		offsets = Arrays.copyOf(offsets, count);
	}
}
