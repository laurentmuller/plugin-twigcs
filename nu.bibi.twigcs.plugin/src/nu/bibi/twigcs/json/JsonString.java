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
class JsonString extends JsonValue {

	private final String string;

	JsonString(final String string) {
		if (string == null) {
			throw new JsonException("The string argument is null."); //$NON-NLS-1$
		}
		this.string = string;
	}

	@Override
	public String asString() {
		return string;
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
		final JsonString other = (JsonString) object;
		return string.equals(other.string);
	}

	@Override
	public int hashCode() {
		return string.hashCode();
	}

	@Override
	public boolean isString() {
		return true;
	}

	@Override
	protected void write(final JsonWriter writer) throws IOException {
		writer.writeString(string);
	}

}
