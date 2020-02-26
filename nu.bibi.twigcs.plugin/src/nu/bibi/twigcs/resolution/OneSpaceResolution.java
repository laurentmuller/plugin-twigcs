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
import org.eclipse.swt.graphics.Image;

import nu.bibi.twigcs.TwigcsPlugin;
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
	public int getErrorId() {
		return ERROR_ONE_SPACE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage() {
		return TwigcsPlugin.getDefault().getQuickFixError();
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
	protected byte[] resolveContents(final IFile file, final byte[] contents,
			final int start, int end) throws CoreException {
		byte[] newContents = null;
		final int len = contents.length;

		if (isWhitespace(contents, end)) {
			// more than one space -> trim
			while (end < len - 1 && isWhitespace(contents, end + 1)) {
				end++;
			}

			// remove spaces
			final int newLength = len - (end - start);
			newContents = Arrays.copyOf(contents, newLength);
			System.arraycopy(contents, end, newContents, start, len - end);

		} else {
			// no space -> insert one
			final int newLength = len + 1;
			newContents = Arrays.copyOf(contents, newLength);
			System.arraycopy(contents, start, newContents, start + 1,
					len - end);
			newContents[start] = ' ';
		}

		return newContents;
	}
}
