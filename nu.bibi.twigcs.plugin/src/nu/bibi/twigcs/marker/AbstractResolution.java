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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.markers.WorkbenchMarkerResolution;

import nu.bibi.twigcs.core.IConstants;
import nu.bibi.twigcs.core.ICoreException;
import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.io.IOStream;

/**
 * Abstract marker resolution.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public abstract class AbstractResolution extends WorkbenchMarkerResolution
		implements IMarkerConstants, IConstants, ICoreException {

	/*
	 * the empty marker array
	 */
	private static final IMarker[] EMPTY_MARKERS = {};

	/*
	 * the invalid position for start and end character attributes.
	 */
	private static final int INVALID_POS = -1;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMarker[] findOtherMarkers(final IMarker[] markers) {
		if (markers.length > 1) {
			final int id = getErrorId();
			final Predicate<IMarker> predicate = marker -> marker
					.getAttribute(ERROR_ID, ERROR_INVALID) == id;
			return Arrays.stream(markers).filter(predicate)
					.toArray(IMarker[]::new);
		} else {
			return EMPTY_MARKERS;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return null;
	}

	/**
	 * Gets the error identifier.
	 *
	 * @return the error identifier.
	 */
	public abstract int getErrorId();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage() {
		return null;
	}

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
	 * {@inheritDoc}
	 */
	@Override
	public void run(final IMarker[] markers, final IProgressMonitor monitor) {
		final Map<IFile, List<IMarker>> map = createMarkerMap(markers);

		monitor.beginTask(getLabel(), map.size());
		for (final Entry<IFile, List<IMarker>> entry : map.entrySet()) {
			fixMarkersInFile(entry.getKey(), entry.getValue(), monitor);
		}
		monitor.done();
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

		final ByteArrayOutputStream output = new ByteArrayOutputStream(
				IOStream.BUFFER_SIZE);

		try (InputStream input = file.getContents()) {
			IOStream.readAll(input, output);
			return output.toByteArray();

		} catch (final IOException e) {
			final String msg = NLS.bind(Messages.Resolution_Error_Read,
					file.getName());
			throw createCoreException(msg, e);
		}
	}

	// public boolean isValidOther(final IMarker marker) {
	// // is it the originalMarker, we don't want duplicates!
	// // if(markerToCheck.equals(originalMarker)) {
	// // return false;
	// // }
	// // is it in the same file as original marker?
	// // if (!marker.getResource().equals(originalMarker.getResource())) {
	// // return false;
	// // }
	// // is it the same validator?
	// // final String checkerName = LightOutputRatioChecker.class.getName();
	// // if (!checkerName.equals(getCheckerName(marker))) {
	// // return false;
	// // }
	// // is it the same error found?
	// // final String checkerMessage = getCheckerMessage(marker);
	// // if (!checkerMessage
	// // .startsWith(LightOutputRatioChecker.DIRECT_RATIO_1)) {
	// // return false;
	// // }
	// // return true;
	// return false;
	// }

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
			final String charset = file.getCharset();
			return new String(buffer, charset);

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
			final String charset = file.getCharset();
			final byte[] buffer = contents.getBytes(charset);
			setFileContents(file, buffer);

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
		if (resource instanceof IFile && resource.isAccessible()) {
			return (IFile) resource;
		}
		return null;
	}

	int compareAttributes(final IMarker o1, final IMarker o2,
			final String name) {
		final int x1 = o1.getAttribute(name, -1);
		final int x2 = o2.getAttribute(name, -1);
		return Integer.compare(x2, x1);
	}

	/**
	 * Gets a map grouping the markers by their file.
	 *
	 * @return the map.
	 */
	Map<IFile, List<IMarker>> createMarkerMap(final IMarker[] markers) {
		// sort markers in reverse
		Arrays.sort(markers, (o1, o2) -> {
			int result = compareAttributes(o1, o2, IMarker.LINE_NUMBER);
			if (result == 0) {
				result = compareAttributes(o1, o2, IMarker.CHAR_END);
			}
			if (result == 0) {
				result = compareAttributes(o1, o2, IMarker.CHAR_START);
			}
			return result;
		});

		final Map<IFile, List<IMarker>> map = new HashMap<>();
		for (final IMarker marker : markers) {
			final IResource resource = marker.getResource();
			if (resource instanceof IFile && resource.isAccessible()) {
				final IFile key = (IFile) resource;
				List<IMarker> value = map.get(key);
				if (value == null) {
					value = new ArrayList<>();
					map.put(key, value);
				}
				value.add(marker);
			}
		}
		return map;
	}

	void fixMarkersInFile(final IFile file, final List<IMarker> markers,
			final IProgressMonitor monitor) {
		monitor.subTask(file.getFullPath().toOSString());
		for (final IMarker marker : markers) {
			run(marker);
		}
		monitor.worked(1);
	}
}
