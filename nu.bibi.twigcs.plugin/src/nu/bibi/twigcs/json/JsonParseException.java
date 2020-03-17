/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.json;

/**
 * An unchecked exception to indicate that an input does not qualify as valid
 * JSON.
 */
@SuppressWarnings("serial") // use default serial UID
public class JsonParseException extends JsonException {

	private final Location location;

	JsonParseException(final String message, final Location location) {
		super(message + " at " + location); //$NON-NLS-1$
		this.location = location;
	}

	/**
	 * Returns the column number at which the error occurred, i.e. the number of
	 * the character in its line. The number of the first character of a line is
	 * 1.
	 *
	 * @return the column in which the error occurred, will be &gt;= 1
	 */
	public int getColumn() {
		return location.column;
	}

	/**
	 * Returns the line number in which the error occurred. The number of the
	 * first line is 1.
	 *
	 * @return the line in which the error occurred, will be &gt;= 1
	 */
	public int getLine() {
		return location.line;
	}

	/**
	 * Returns the location at which the error occurred.
	 *
	 * @return the error location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Returns the absolute character index at which the error occurred. The
	 * offset of the first character of a document is 0.
	 *
	 * @return the character offset at which the error occurred, will be &gt;= 0
	 */
	public int getOffset() {
		return location.offset;
	}

}
