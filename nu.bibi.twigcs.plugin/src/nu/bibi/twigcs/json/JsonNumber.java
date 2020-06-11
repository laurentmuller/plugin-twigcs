/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.json;

import java.io.IOException;

/* package */
@SuppressWarnings("serial") // use default serial UID
class JsonNumber extends JsonValue {

	private final String string;

	JsonNumber(final String string) {
		if (string == null) {
			throw new JsonException("The string argument is null."); //$NON-NLS-1$
		}
		this.string = string;
	}

	@Override
	public double asDouble() {
		return Double.parseDouble(string);
	}

	@Override
	public float asFloat() {
		return Float.parseFloat(string);
	}

	@Override
	public int asInt() {
		return Integer.parseInt(string, 10);
	}

	@Override
	public long asLong() {
		return Long.parseLong(string, 10);
	}

	@Override
	public boolean equals(final Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		final JsonNumber other = (JsonNumber) object;
		return string.equals(other.string);
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	protected void write(final JsonWriter writer) throws IOException {
		writer.writeNumber(string);
	}

}
