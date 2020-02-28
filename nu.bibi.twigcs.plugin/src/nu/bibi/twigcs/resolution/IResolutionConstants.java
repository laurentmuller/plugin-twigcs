/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.resolution;

/**
 * Constants for marker resolutions.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public interface IResolutionConstants {

	/**
	 * The invalid error identifier. Used for markers when no quick fix is
	 * available.
	 */
	int ERROR_INVALID = -1;

	/**
	 * The lower case error identifier. The error message is like:
	 *
	 * <pre>
	 * The "titleIcon" variable should be in lower case (use _ as a separator).
	 * </pre>
	 */
	int ERROR_LOWER_CASE = 1;

	/**
	 * The unused macro error identifier. The error message is like:
	 *
	 * <pre>
	 * Unused macro import "icons".
	 * </pre>
	 */
	int ERROR_UNUSED_MACRO = 2;

	/**
	 * The unused variable error identifier. The error message is like:
	 *
	 * <pre>
	 * Unused variable "title_path".
	 * </pre>
	 */
	int ERROR_UNUSED_VARIABLE = 3;

	/**
	 * The error identifier when no space is required. The error message is
	 * like:
	 *
	 * <pre>
	 * ... 0 space ...
	 * </pre>
	 */
	int ERROR_NO_SPACE = 4;

	/**
	 * The error identifier when one space is required. The error message is
	 * like:
	 *
	 * <pre>
	 *  ... 1 space ...
	 * </pre>
	 */
	int ERROR_ONE_SPACE = 5;

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
	default boolean isEqualsChar(final byte[] content, final int index,
			final char ch) {
		return index >= 0 && index < content.length && content[index] == ch;
	}

	/**
	 * Returns if the character at the given index is the given character.
	 *
	 * @param content
	 *            the content to get character for.
	 * @param index
	 *            the index to validate.
	 * @param ch
	 *            the character to compare to.
	 * @return <code>true</code> if same character.
	 */
	default boolean isEqualsChar(final String content, final int index,
			final char ch) {
		return index >= 0 && index < content.length()
				&& content.charAt(index) == ch;
	}

	/**
	 * Returns if the byte at the given index is a new line character or a
	 * carriage return character.
	 *
	 * @param content
	 *            the content to get character for.
	 * @param index
	 *            the index to validate.
	 * @return <code>true</code> if new line or carriage return character.
	 */
	default boolean isNewLine(final byte[] content, final int index) {
		return isEqualsChar(content, index, '\n')
				|| isEqualsChar(content, index, '\r');
	}

	/**
	 * Returns if the character at the given index is a new line character or a
	 * carriage return character.
	 *
	 * @param content
	 *            the content to get character for.
	 * @param index
	 *            the index to validate.
	 * @return <code>true</code> if new line or carriage return character.
	 */
	default boolean isNewLine(final String content, final int index) {
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
	default boolean isWhitespace(final byte[] content, final int index) {
		return isEqualsChar(content, index, ' ');
	}

	/**
	 * Returns if the character at the given index is a space character.
	 *
	 * @param content
	 *            the content to get character for.
	 * @param index
	 *            the index to validate.
	 * @return <code>true</code> if space character.
	 */
	default boolean isWhitespace(final String content, final int index) {
		return isEqualsChar(content, index, ' ');
	}
}
