/**
 *
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

/**
 * @author Laurent Muller
 *
 */
public abstract class AbstractResolution
		implements IMarkerResolution, IConstants, ICoreException {

	/**
	 * The invalid position for start and end character attributes.
	 */
	protected final int INVALID_POS = -1;

	@Override
	public void run(final IMarker marker) {
		final IFile file = getFile(marker);
		if (file == null) {
			return;
		}

		try {
			resolve(file, marker);
		} catch (final CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	protected int getCharEnd(final IMarker marker) {
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
	protected int getCharStart(final IMarker marker) {
		return marker.getAttribute(IMarker.CHAR_START, INVALID_POS);
	}

	/**
	 * Gets the marker's file.
	 *
	 * @param marker
	 *            the marker to get file for.
	 * @return the marker's file, if applicable; <code>null</code> otherwise.
	 */
	protected IFile getFile(final IMarker marker) {
		final IResource resource = marker.getResource();
		if (resource != null && resource.exists()
				&& resource instanceof IFile) {
			return (IFile) resource;
		}
		return null;
	}

	/**
	 * Gets the content of the file.
	 *
	 * @param file
	 *            the file to read from.
	 * @return the file content
	 * @throws CoreException
	 *             if this method fails.
	 */
	protected String getFileContent(final IFile file) throws CoreException {

		try {
			final byte[] buffer = getFileContentAsByte(file);
			return new String(buffer, file.getCharset());
		} catch (final UnsupportedEncodingException e) {
			final String msg = NLS.bind(
					"Unable to read the content of the file '{0}'.",
					file.getName());
			throw createCoreException(msg, e);
		}
	}

	protected byte[] getFileContentAsByte(final IFile file)
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
			final String msg = NLS.bind(
					"Unable to get content of the file '{0}'.", file.getName());
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
	protected boolean isChar(final byte[] content, final int index,
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
		return isChar(content, index, '\n') || isChar(content, index, '\r');
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
		return isChar(content, index, ' ');
	}

	protected abstract void resolve(IFile file, IMarker marker)
			throws CoreException;

	/**
	 * Sets content of the given file.
	 *
	 * @param file
	 *            the file to update.
	 * @param content
	 *            the content to set.
	 * @throws CoreException
	 *             if this method fails.
	 */
	protected void setFileContent(final IFile file, final byte[] content)
			throws CoreException {
		final ByteArrayInputStream source = new ByteArrayInputStream(content);
		file.setContents(source, true, true, null);
		// file.refreshLocal(IResource.DEPTH_ZERO, null);
	}

	/**
	 * Sets content of the given file.
	 *
	 * @param file
	 *            the file to update.
	 * @param content
	 *            the content to set.
	 * @throws CoreException
	 *             if this method fails.
	 */
	protected void setFileContent(final IFile file, final String content)
			throws CoreException {
		try {
			setFileContent(file, content.getBytes(file.getCharset()));
		} catch (final UnsupportedEncodingException e) {
			final String msg = NLS.bind(
					"Unable to set content of the file '{0}'.", file.getName());
			throw createCoreException(msg, e);
		}
	}
}
