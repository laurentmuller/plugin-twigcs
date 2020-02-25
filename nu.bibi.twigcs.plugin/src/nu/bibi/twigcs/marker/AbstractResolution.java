/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.marker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IMarkerResolution;

import nu.bibi.twigcs.core.IConstants;
import nu.bibi.twigcs.core.ICoreException;
import nu.bibi.twigcs.internal.Messages;

/**
 * Abstract marker resolution.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public abstract class AbstractResolution
		implements IMarkerResolution, IConstants, ICoreException {

	/*
	 * the invalid position for start and end character attributes.
	 */
	private final int INVALID_POS = -1;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(final IMarker marker) {
		// get file
		final IFile file = getFile(marker);
		if (file == null) {
			return;
		}

		// get positions
		final int start = getCharStart(marker);
		final int end = getCharEnd(marker);
		if (start == INVALID_POS || end == INVALID_POS) {
			return;
		}

		// resolve
		try {
			resolve(file, marker, start, end);
		} catch (final CoreException e) {
			handleStatus(e.getStatus());
		}
	}

	/**
	 * Gets the contents of the file.
	 *
	 * @param file
	 *            the file to read from.
	 * @return the file contents.
	 * @throws CoreException
	 *             if this method fails.
	 */
	protected byte[] getFileContentsAsByte(final IFile file)
			throws CoreException {
		int len;
		final byte[] buffer = new byte[8192];
		final ByteArrayOutputStream output = new ByteArrayOutputStream(8192);

		try (InputStream contents = file.getContents()) {
			while ((len = contents.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
			return output.toByteArray();
		} catch (final IOException e) {
			final String msg = NLS.bind(Messages.Resolution_Error_Read,
					file.getName());
			throw createCoreException(msg, e);
		}
	}

	/**
	 * Gets the contents of the file.
	 *
	 * @param file
	 *            the file to read from.
	 * @return the file contents.
	 * @throws CoreException
	 *             if this method fails.
	 */
	protected String getFileContentsAsString(final IFile file)
			throws CoreException {
		try {
			final byte[] buffer = getFileContentsAsByte(file);
			return new String(buffer, file.getCharset());
		} catch (final UnsupportedEncodingException e) {
			final String msg = NLS.bind(Messages.Resolution_Error_Read,
					file.getName());
			throw createCoreException(msg, e);
		}
	}

	/**
	 * Returns if the byte at the given index is the given character.
	 *
	 * @param content
	 *            the content to get character for.
	 * @param index
	 *            the index to validate.
	 * @param ch
	 *            the character to compare to.
	 * @return <code>true</code> if same character.
	 */
	protected boolean isEqualsChar(final byte[] content, final int index,
			final char ch) {
		if (index >= 0 && index < content.length) {
			return content[index] == ch;
		}
		return false;
	}

	/**
	 * Returns if the byte at the given index is a new line or a carriage return
	 * character.
	 *
	 * @param content
	 *            the content to get character for.
	 * @param index
	 *            the index to validate.
	 * @return <code>true</code> if new line or carriage return character.
	 */
	protected boolean isNewLine(final byte[] content, final int index) {
		return isEqualsChar(content, index, '\n')
				|| isEqualsChar(content, index, '\r');
	}

	/**
	 * Returns if the byte at the given index is a space character.
	 *
	 * @param content
	 *            the content to get character for.
	 * @param index
	 *            the index to validate.
	 * @return <code>true</code> if space character.
	 */
	protected boolean isWhitespace(final byte[] content, final int index) {
		return isEqualsChar(content, index, ' ');
	}

	/**
	 * Resolves the problem.
	 *
	 * @param file
	 *            the marker's file.
	 * @param marker
	 *            the marker.
	 * @param start
	 *            the start character attribute.
	 * @param end
	 *            the end character attribute.
	 * @throws CoreException
	 *             if the resolution fails.
	 */
	protected abstract void resolve(IFile file, IMarker marker, int start,
			int end) throws CoreException;

	/**
	 * Sets contents of the given file.
	 *
	 * @param file
	 *            the file to update.
	 * @param contents
	 *            the contents to set.
	 * @throws CoreException
	 *             if this method fails.
	 */
	protected void setFileContents(final IFile file, final byte[] contents)
			throws CoreException {
		final ByteArrayInputStream source = new ByteArrayInputStream(contents);
		file.setContents(source, true, true, null);
	}

	/**
	 * Sets contents of the given file.
	 *
	 * @param file
	 *            the file to update.
	 * @param contents
	 *            the contents to set.
	 * @throws CoreException
	 *             if this method fails.
	 */
	protected void setFileContents(final IFile file, final String contents)
			throws CoreException {
		try {
			setFileContents(file, contents.getBytes(file.getCharset()));
		} catch (final UnsupportedEncodingException e) {
			final String msg = NLS.bind(Messages.Resolution_Error_Write,
					file.getName());
			throw createCoreException(msg, e);
		}
	}

	/**
	 * Gets the end character attribute for the given marker.
	 *
	 * @param marker
	 *            the marker to read attribute from.
	 * @return the start character attribute, if found; <code>-1</code>
	 *         otherwise
	 */
	private int getCharEnd(final IMarker marker) {
		return marker.getAttribute(IMarker.CHAR_END, INVALID_POS);
	}

	/**
	 * Gets the start character attribute for the given marker.
	 *
	 * @param marker
	 *            the marker to read attribute from.
	 * @return the start character attribute, if found; <code>-1</code>
	 *         otherwise
	 */
	private int getCharStart(final IMarker marker) {
		return marker.getAttribute(IMarker.CHAR_START, INVALID_POS);
	}

	/**
	 * Gets the marker's file.
	 *
	 * @param marker
	 *            the marker to get file for.
	 * @return the marker's file, if applicable; <code>null</code> otherwise.
	 */
	private IFile getFile(final IMarker marker) {
		final IResource resource = marker.getResource();
		if (resource != null && resource.exists()
				&& resource instanceof IFile) {
			return (IFile) resource;
		}
		return null;
	}
}
