/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.marker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

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
	public String getLabel() {
		return Messages.Resolution_Unused_Macro;
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

		// find open bracket
		while (start != -1 && !isEqualsChar(content, start, '{')) {
			start--;
		}

		// find spaces before
		while (start > 1 && isWhitespace(content, start - 1)) {
			start--;
		}

		// find end line before
		if (start > 1 && isNewLine(content, start - 1)) {
			start--;
		}

		// find close bracket
		while (end < len && !isEqualsChar(content, end, '}')) {
			end++;
		}
		end++;

		// find spaces after
		while (end < len - 1 && isWhitespace(content, end + 1)) {
			end++;
		}

		// find end line after
		if (end < len - 1 && isNewLine(content, end + 1)) {
			end++;
		}

		// validate range
		if (start < 0 || end >= len) {
			return;
		}

		// remove line
		final byte[] newContent = new byte[len - (end - start) + 1];
		System.arraycopy(content, 0, newContent, 0, start);
		System.arraycopy(content, end, newContent, start, len - end);

		// save
		setFileContents(file, newContent);
	}
}
