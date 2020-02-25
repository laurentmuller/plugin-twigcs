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
 * Marker resolution for one space error.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class OneSpaceResolution extends AbstractResolution {

	/*
	 * the shared instance
	 */
	private static volatile OneSpaceResolution instance;

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static synchronized OneSpaceResolution instance() {
		// double check locking
		if (instance == null) {
			synchronized (OneSpaceResolution.class) {
				if (instance == null) {
					instance = new OneSpaceResolution();
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
		return Messages.Resolution_One_Space;
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
		byte[] newContent = null;

		if (isWhitespace(content, end)) {
			// more than one space -> trim
			while (end < len - 1 && isWhitespace(content, end + 1)) {
				end++;
			}

			// remove spaces
			final int newLength = len - (end - start);
			newContent = Arrays.copyOf(content, newLength);
			System.arraycopy(content, end, newContent, start, len - end);

		} else {
			// no space -> insert one
			final int newLength = len + 1;
			newContent = Arrays.copyOf(content, newLength);
			System.arraycopy(content, start, newContent, start + 1, len - end);
			newContent[start] = ' ';
		}

		// save
		setFileContents(file, newContent);
	}
}
