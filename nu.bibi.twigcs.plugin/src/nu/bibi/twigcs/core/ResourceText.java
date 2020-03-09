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
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;

import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.io.IOStream;

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
	 * the number of lines
	 */
	private int count;

	/*
	 * the content
	 */
	private byte[] content;

	/*
	 * the content length
	 */
	private int contentLength;

	/*
	 * the lines offset
	 */
	private int[] offsets = new int[200];

	/**
	 * Creates a new instance of this class.
	 *
	 * @param file
	 *            the file to get contents.
	 * @throws CoreException
	 *             if the get contents method fails.
	 */
	public ResourceText(final IFile file) throws CoreException {
		try (InputStreamReader reader = new InputStreamReader(
				file.getContents(), file.getCharset())) {

			read(reader);

		} catch (final IOException e) {
			final String msg = NLS.bind(Messages.ResourceText_Error_Read,
					file.getName());
			throw createCoreException(msg, e);
		}
	}

	/**
	 * Gets the number of lines.
	 *
	 * @return the number of lines.
	 */
	public int count() {
		return count;
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
	 * Returns the length of the given line, including line break characters.
	 *
	 * @param index
	 *            the zero-relative line index.
	 * @return the line length in characters
	 * @throws IndexOutOfBoundsException
	 *             if the index is smaller than 0 or greater or equal to the
	 *             lines count.
	 */
	public int getLinelength(final int index) {
		checkLineIndex(index);

		int nextOffset = contentLength;
		if (index < count) {
			nextOffset = offsets[index + 1];
		}
		return nextOffset - offsets[index];
	}

	/**
	 * Returns the offset of the given line's first character.
	 *
	 * @param index
	 *            the zero-relative line index.
	 * @return the line offset.
	 * @throws IndexOutOfBoundsException
	 *             if the index is smaller than 0 or greater or equal to the
	 *             lines count.
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
	 */
	private void addOffset(final int offset) {
		final int len = offsets.length;
		if (count >= len) {
			offsets = Arrays.copyOf(offsets, len + 100);
		}
		offsets[count++] = offset;
	}

	/**
	 * Check if the given line is within the line offsets.
	 *
	 * @param index
	 *            the zero-relative line index.
	 * @throws IndexOutOfBoundsException
	 *             if the index is smaller than 0 or greater or equal to the
	 *             lines count.
	 */
	private void checkLineIndex(final int index) {
		if (index < 0 || index >= count) {
			throw new IndexOutOfBoundsException(
					NLS.bind(Messages.ResourceText_Error_Index, index));
		}
	}

	/**
	 * Reads all the content.
	 *
	 * @param reader
	 *            the reader to read data from.
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	private void read(final Reader reader) throws IOException {
		// reset
		count = 1;
		contentLength = 0;
		content = new byte[] {};

		int ch;
		int previous = 0;
		final StringBuffer buffer = new StringBuffer(IOStream.BUFFER_SIZE);

		while ((ch = reader.read()) != -1) {
			if (ch == LINE_FEED) {
				// handle Linux (LF) and Windows (CR+LF)
				addOffset(contentLength + 1);
			} else if (previous == CARRIAGE_RETURN && ch != LINE_FEED) {
				// handle Mac (CR)
				addOffset(contentLength);
			}
			previous = ch;
			contentLength++;
			buffer.append((char) ch);
		}

		// check last character (Mac)
		if (previous == CARRIAGE_RETURN) {
			addOffset(contentLength);
		}

		// content?
		if (contentLength == 0) {
			count = 0;
		} else {
			content = buffer.toString().getBytes();
		}
	}
}
