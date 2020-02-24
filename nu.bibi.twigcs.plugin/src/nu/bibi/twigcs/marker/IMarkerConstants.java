package nu.bibi.twigcs.marker;

public interface IMarkerConstants {

	/**
	 * The error identifier attribute.
	 */
	String ERROR_ID = "errorId";

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
	 * The space before error identifier. The error message is like:
	 *
	 * <pre>
	 * There should be 1 space before the "="
	 * </pre>
	 */
	int ERROR_SPACE_BEFORE = 4;

	/**
	 * The space before error identifier. The error message is like:
	 *
	 * <pre>
	 * "There should be 1 space after the "="
	 * </pre>
	 */
	int ERROR_SPACE_AFTER = 5;

	/**
	 * The space before error identifier. The error message is like:
	 *
	 * <pre>
	 * A line should not end with blank space(s).
	 * </pre>
	 */
	int ERROR_LINE_END_SPACE = 6;
}
