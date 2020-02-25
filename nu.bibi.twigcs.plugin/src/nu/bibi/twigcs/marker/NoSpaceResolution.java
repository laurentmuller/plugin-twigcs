/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.marker;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import nu.bibi.twigcs.internal.Messages;

/**
 * Marker resolution for no space error.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class NoSpaceResolution extends AbstractResolution {

	/*
	 * the shared instance
	 */
	private static volatile NoSpaceResolution instance;

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static synchronized NoSpaceResolution instance() {
		// double check locking
		if (instance == null) {
			synchronized (NoSpaceResolution.class) {
				if (instance == null) {
					instance = new NoSpaceResolution();
				}
			}
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return Messages.Resolution_No_Space;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resolve(final IFile file, final IMarker marker,
			final int start, int end) throws CoreException {
		// get contents
		final byte[] content = getFileContentsAsByte(file);
		final int len = content.length;

		// find space after
		while (end < len && isWhitespace(content, end)) {
			end++;
		}

		// remove spaces
		final int newLength = len - (end - start);
		final byte[] newContent = Arrays.copyOf(content, newLength);
		System.arraycopy(content, end, newContent, start, len - end);

		// save
		setFileContents(file, newContent);
	}
}
