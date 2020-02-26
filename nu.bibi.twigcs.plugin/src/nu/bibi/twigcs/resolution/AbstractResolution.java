/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.resolution;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import nu.bibi.twigcs.TwigcsPlugin;
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
		implements IResolutionConstants, IConstants, ICoreException {

	/*
	 * the empty marker array
	 */
	private static final IMarker[] EMPTY_MARKERS = {};

	/*
	 * the attribute names used to compare markers
	 */
	private static final String[] ATTRIBUTE_NAMES = { //
			IMarker.LINE_NUMBER, IMarker.CHAR_END, IMarker.CHAR_START //
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMarker[] findOtherMarkers(final IMarker[] markers) {
		if (markers.length > 1 && canGrouping()) {
			final int id = getErrorId();
			final Predicate<IMarker> predicate = m -> getFile(m) != null
					&& getAttribute(m, IMarker.SOURCE_ID) == id;
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
	 * Gets the error identifier. This property is used to group markers.
	 *
	 * @return the error identifier.
	 * @see #findOtherMarkers(IMarker[])
	 */
	public abstract int getErrorId();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage() {
		return TwigcsPlugin.getDefault().getQuickFix();
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

		try {
			// update contents
			final byte[] contents = getFileContents(file);
			final byte[] newContents = fixMarker(file, marker, contents);

			// save if change
			if (!Arrays.equals(contents, newContents)) {
				setFileContents(file, newContents);
			}

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
			try {
				fixMarkers(entry.getKey(), entry.getValue(), monitor);
			} catch (final CoreException e) {
				handleStatus(e.getStatus());
			}
		}
		monitor.done();
	}

	/**
	 * Returns a value indicating if this resolution can be grouping. The
	 * default value is <code>true</code>. Subclass can override if they don't
	 * want to allow grouping.
	 *
	 * @return <code>true</code> if allowed grouping; <code>false</code>
	 *         otherwise.
	 */
	protected boolean canGrouping() {
		return true;
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
	 * Fix the contents.
	 *
	 * @param file
	 *            the resource file.
	 * @param contents
	 *            the file content.
	 * @param start
	 *            the start character attribute.
	 * @param end
	 *            the end character attribute.
	 * @return the modified contents, if applicable; the original contents
	 *         otherwise.
	 * @throws CoreException
	 *             if the resolution fails.
	 */
	protected abstract byte[] resolveContents(final IFile file,
			final byte[] contents, final int start, final int end)
			throws CoreException;

	/**
	 * Compare attributes in reverse mode.
	 *
	 * @param o1
	 *            the first marker.
	 * @param o2
	 *            the second marker.
	 * @param name
	 *            the attribute name.
	 * @return the comparison result.
	 */
	private int compareAttributes(final IMarker o1, final IMarker o2,
			final String name) {
		final int x1 = getAttribute(o1, name);
		final int x2 = getAttribute(o2, name);
		return Integer.compare(x2, x1);
	}

	/**
	 * Gets a map grouping the markers by their file.
	 *
	 * @return the map.
	 */
	private Map<IFile, List<IMarker>> createMarkerMap(final IMarker[] markers) {
		// sort markers in reverse position
		Arrays.sort(markers, (o1, o2) -> {
			int result = 0;
			for (final String name : ATTRIBUTE_NAMES) {
				result = compareAttributes(o1, o2, name);
				if (result != 0) {
					return result;
				}
			}
			return 0;
		});

		// create map
		final Map<IFile, List<IMarker>> map = new HashMap<>(markers.length);
		for (final IMarker marker : markers) {
			final IFile key = getFile(marker);
			if (key != null) {
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

	/**
	 * Fix the given marker
	 *
	 * @param file
	 *            the marker's file.
	 * @param marker
	 *            the marker to fix.
	 * @param contents
	 *            the file contents.
	 * @return the modified contents, if applicable; the original contents
	 *         otherwise.
	 * @throws CoreException
	 *             if the fix fails.
	 */
	private byte[] fixMarker(final IFile file, final IMarker marker,
			final byte[] contents) throws CoreException {
		final int start = getAttribute(marker, IMarker.CHAR_START);
		final int end = getAttribute(marker, IMarker.CHAR_END);
		if (start != ERROR_INVALID && end != ERROR_INVALID) {
			return resolveContents(file, contents, start, end);
		} else {
			return contents;
		}
	}

	/**
	 * Fix the given markers.
	 *
	 * @param file
	 *            the marker's file.
	 * @param markers
	 *            the markers to fix.
	 * @param monitor
	 *            the monitor to show progress of activity.
	 * @throws CoreException
	 *             if the fix fails.
	 */
	private void fixMarkers(final IFile file, final List<IMarker> markers,
			final IProgressMonitor monitor) throws CoreException {

		monitor.subTask(file.getFullPath().toOSString());

		// get contents
		final byte[] contents = getFileContents(file);
		byte[] newContents = Arrays.copyOf(contents, contents.length);

		// resolve
		for (final IMarker marker : markers) {
			newContents = fixMarker(file, marker, newContents);
		}

		// save if change
		if (!Arrays.equals(contents, newContents)) {
			setFileContents(file, newContents);
		}
		monitor.worked(1);
	}

	/**
	 * Gets an integer attribute.
	 *
	 * @param marker
	 *            the marker.
	 * @param name
	 *            the attribute name.
	 * @return the attribute value, if present; -1 otherwise.
	 */
	private int getAttribute(final IMarker marker, final String name) {
		return marker.getAttribute(name, ERROR_INVALID);
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

	/**
	 * Gets the contents of the file.
	 *
	 * @param file
	 *            the file to read from.
	 * @return the file contents.
	 * @throws CoreException
	 *             if this method fails.
	 */
	private byte[] getFileContents(final IFile file) throws CoreException {
		try (InputStream input = file.getContents()) {

			return IOStream.readAll(input);

		} catch (final IOException e) {
			final String msg = NLS.bind(Messages.Resolution_Error_Read,
					file.getName());
			throw createCoreException(msg, e);
		}
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
	private void setFileContents(final IFile file, final byte[] contents)
			throws CoreException {
		final InputStream source = new ByteArrayInputStream(contents);
		file.setContents(source, true, true, null);
	}
}
