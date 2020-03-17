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
 * An immutable object that represents a location in the parsed text.
 */
public class Location {

	/**
	 * The absolute character index, starting at 0.
	 */
	public final int offset;

	/**
	 * The line number, starting at 1.
	 */
	public final int line;

	/**
	 * The column number, starting at 1.
	 */
	public final int column;

	Location(final int offset, final int line, final int column) {
		this.offset = offset;
		this.column = column;
		this.line = line;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Location other = (Location) obj;
		return offset == other.offset && column == other.column
				&& line == other.line;
	}

	@Override
	public int hashCode() {
		return offset;
	}

	@Override
	public String toString() {
		return line + ":" + column; //$NON-NLS-1$
	}

}