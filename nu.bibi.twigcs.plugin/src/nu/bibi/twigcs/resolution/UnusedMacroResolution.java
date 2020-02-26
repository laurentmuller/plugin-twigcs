/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.resolution;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.graphics.Image;

import nu.bibi.twigcs.TwigcsPlugin;
import nu.bibi.twigcs.internal.Messages;

/**
 * Marker resolution for unused macro error.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class UnusedMacroResolution extends AbstractResolution {

	/*
	 * the shared instance
	 */
	private static volatile UnusedMacroResolution instance;

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static synchronized UnusedMacroResolution instance() {
		// double check locking
		if (instance == null) {
			synchronized (UnusedMacroResolution.class) {
				if (instance == null) {
					instance = new UnusedMacroResolution();
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
		return ERROR_UNUSED_MACRO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage() {
		return TwigcsPlugin.getDefault().getQuickFixWarning();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return Messages.Resolution_Unused_Macro;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected byte[] resolveContents(final IFile file, final byte[] contents,
			int start, int end) throws CoreException {
		final int len = contents.length;

		// find open bracket
		while (start != -1 && !isEqualsChar(contents, start, '{')) {
			start--;
		}

		// find spaces before
		while (start > 1 && isWhitespace(contents, start - 1)) {
			start--;
		}

		// find end line before
		if (start > 1 && isNewLine(contents, start - 1)) {
			start--;
		}

		// find close bracket
		while (end < len && !isEqualsChar(contents, end, '}')) {
			end++;
		}
		end++;

		// find spaces after
		while (end < len - 1 && isWhitespace(contents, end + 1)) {
			end++;
		}

		// find end line after
		if (end < len - 1 && isNewLine(contents, end + 1)) {
			end++;
		}

		// validate range
		if (start < 0 || end >= len) {
			return contents;
		}

		// remove line
		final byte[] newContents = new byte[len - (end - start) + 1];
		System.arraycopy(contents, 0, newContents, 0, start);
		System.arraycopy(contents, end, newContents, start, len - end);

		return newContents;
	}
}
