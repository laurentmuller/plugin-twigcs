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

/**
 * Marker resolution for unused value warning.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public abstract class UnusedValueResolution extends AbstractResolution {

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
	protected byte[] resolveContents(final IFile file, final byte[] contents,
			final int start, final int end) throws CoreException {

		// start offset
		int startOffset = start;
		startOffset = findOpenBracket(contents, startOffset);
		startOffset = findSpacesBefore(contents, startOffset);
		startOffset = findEndLineBefore(contents, startOffset);

		// end offset
		int endOffset = end;
		endOffset = findCloseBracket(contents, endOffset);
		endOffset = findSpacesAfter(contents, endOffset);
		endOffset = findEndLineAfter(contents, endOffset);

		// validate range
		final int len = contents.length;
		if (startOffset < 0 || endOffset >= len) {
			return contents;
		}

		// remove line
		final byte[] newContents = new byte[len - (endOffset - startOffset)
				+ 1];
		System.arraycopy(contents, 0, newContents, 0, startOffset);
		System.arraycopy(contents, endOffset, newContents, startOffset,
				len - endOffset);

		return newContents;

		// final int len = contents.length;
		//
		// // find open bracket
		// while (start != -1 && !isEqualsChar(contents, start, '{')) {
		// start--;
		// }
		//
		// // find spaces before
		// while (start > 1 && isWhitespace(contents, start - 1)) {
		// start--;
		// }
		//
		// // find end line before
		// if (start > 1 && isNewLine(contents, start - 1)) {
		// start--;
		// }
		//
		// // find close bracket
		// while (end < len && !isEqualsChar(contents, end, '}')) {
		// end++;
		// }
		// end++;
		//
		// // find spaces after
		// while (end < len - 1 && isWhitespace(contents, end + 1)) {
		// end++;
		// }
		//
		// // find end line after
		// if (end < len - 1 && isNewLine(contents, end + 1)) {
		// end++;
		// }
		//
		// // validate range
		// if (start < 0 || end >= len) {
		// return contents;
		// }
		//
		// // remove line
		// final byte[] newContents = new byte[len - (end - start) + 1];
		// System.arraycopy(contents, 0, newContents, 0, start);
		// System.arraycopy(contents, end, newContents, start, len - end);
		//
		// return newContents;
	}

	/**
	 * Finds close bracket.
	 *
	 * @param contents
	 *            the file content.
	 * @param offset
	 *            the current offset.
	 * @return the close bracket offset.
	 */
	private int findCloseBracket(final byte[] contents, final int offset) {
		int result = offset;
		final int len = contents.length;
		while (result < len && !isEqualsChar(contents, result, '}')) {
			result++;
		}
		result++;
		return result;
	}

	/**
	 * Finds end line after.
	 *
	 * @param contents
	 *            the file content.
	 * @param offset
	 *            the current offset.
	 * @return the end line after offset.
	 */
	private int findEndLineAfter(final byte[] contents, final int offset) {
		final int len = contents.length;
		if (offset < len - 1 && isNewLine(contents, offset + 1)) {
			return offset + 1;
		}
		return offset;
	}

	/**
	 * Finds end line before.
	 *
	 * @param contents
	 *            the file content.
	 * @param offset
	 *            the current offset.
	 * @return the end line before offset.
	 */
	private int findEndLineBefore(final byte[] contents, final int offset) {
		if (offset > 1 && isNewLine(contents, offset - 1)) {
			return offset - 1;
		}
		return offset;
	}

	/**
	 * Finds open bracket.
	 *
	 * @param contents
	 *            the file content.
	 * @param offset
	 *            the current offset.
	 * @return the open bracket offset.
	 */
	private int findOpenBracket(final byte[] contents, final int offset) {
		int result = offset;
		while (result != -1 && !isEqualsChar(contents, result, '{')) {
			result--;
		}
		return result;
	}

	/**
	 * Finds space after.
	 *
	 * @param contents
	 *            the file content.
	 * @param offset
	 *            the current offset.
	 * @return the space after offset.
	 */
	private int findSpacesAfter(final byte[] contents, final int offset) {
		int result = offset;
		final int len = contents.length;
		while (result < len - 1 && isWhitespace(contents, result + 1)) {
			result++;
		}
		return result;
	}

	/**
	 * Finds space before.
	 *
	 * @param contents
	 *            the file content.
	 * @param offset
	 *            the current offset.
	 * @return the space before offset.
	 */
	private int findSpacesBefore(final byte[] contents, final int offset) {
		int result = offset;
		while (result > 1 && isWhitespace(contents, result - 1)) {
			result--;
		}
		return result;
	}
}
