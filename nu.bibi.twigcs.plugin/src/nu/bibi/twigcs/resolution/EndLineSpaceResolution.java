/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.resolution;

import java.util.Arrays;

import org.eclipse.core.resources.IFile;
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
	protected byte[] resolveContents(final IFile file, final byte[] contents,
			int start, int end) throws CoreException {
		// new line?
		if (isNewLine(contents, start)) {
			end--;
		}

		// find space before
		while (start > 0 && isWhitespace(contents, start - 1)) {
			start--;
		}

		// remove spaces
		final int len = contents.length;
		final int newLength = len - (end - start);
		final byte[] newContents = Arrays.copyOf(contents, newLength);
		System.arraycopy(contents, end, newContents, start, len - end);

		return newContents;
	}
}
