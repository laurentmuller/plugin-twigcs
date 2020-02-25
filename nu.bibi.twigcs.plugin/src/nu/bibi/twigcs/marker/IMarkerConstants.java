/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.marker;

/**
 * Constants for marker resolutions.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public interface IMarkerConstants {

	/**
	 * The error identifier attribute.
	 */
	String ERROR_ID = "errorId"; //$NON-NLS-1$

	/**
	 * The error identifier when invalid.
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
	 * The end line space error identifier. The error message is like:
	 *
	 * <pre>
	 * A line should not end with blank space
	 * </pre>
	 */
	int ERROR_LINE_END_SPACE = 4;

	/**
	 * The error identifier when no space is required. The error message is
	 * like:
	 *
	 * <pre>
	 * ... 0 space ...
	 * </pre>
	 */
	int ERROR_NO_SPACE = 5;

	/**
	 * The error identifier when one space is required. The error message is
	 * like:
	 *
	 * <pre>
	 *  ... 1 space ...
	 * </pre>
	 */
	int ERROR_ONE_SPACE = 6;
}
