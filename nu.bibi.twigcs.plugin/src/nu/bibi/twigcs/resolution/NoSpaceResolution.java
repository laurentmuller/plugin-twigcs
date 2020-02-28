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
	public int getErrorId() {
		return ERROR_NO_SPACE;
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
		return Messages.Resolution_No_Space;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected byte[] resolveContents(final IFile file, final byte[] contents,
			final int start, int end) throws CoreException {

		// find spaces after
		final int len = contents.length;
		while (end < len && isWhitespace(contents, end)) {
			end++;
		}

		// remove spaces
		final int newLength = len - (end - start);
		final byte[] newContent = Arrays.copyOf(contents, newLength);
		System.arraycopy(contents, end, newContent, start, len - end);

		return newContent;
	}
}
