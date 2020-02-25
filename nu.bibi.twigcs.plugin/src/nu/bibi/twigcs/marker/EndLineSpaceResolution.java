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
 * Marker resolution for end line space error.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class EndLineSpaceResolution extends AbstractResolution {

	/*
	 * the shared instance
	 */
	private static volatile EndLineSpaceResolution instance;

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static synchronized EndLineSpaceResolution instance() {
		// double check locking
		if (instance == null) {
			synchronized (EndLineSpaceResolution.class) {
				if (instance == null) {
					instance = new EndLineSpaceResolution();
				}
			}
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getErrorId() {
		return ERROR_LINE_END_SPACE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return Messages.Resolution_End_Line_Space;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resolve(final IFile file, final IMarker marker, int start,
			int end) throws CoreException {
		// get contents
		final byte[] content = getFileContentsAsByte(file);
		final int len = content.length;

		// new line?
		if (isNewLine(content, start)) {
			end--;
		}

		// find space before
		while (start > 0 && isWhitespace(content, start - 1)) {
			start--;
		}

		// remove spaces
		final int newLength = len - (end - start);
		final byte[] newContent = Arrays.copyOf(content, newLength);
		System.arraycopy(content, end, newContent, start, len - end);

		// save
		setFileContents(file, newContent);
	}
}
