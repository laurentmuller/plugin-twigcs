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

@SuppressWarnings("serial") // use default serial UID
class JsonLiteral extends JsonValue {

	private final String value;
	private final boolean isNull;
	private final boolean isTrue;
	private final boolean isFalse;

	JsonLiteral(final String value) {
		if (value == null) {
			throw new JsonException("The value argument is null."); //$NON-NLS-1$
		}
		this.value = value;
		isNull = "null".equals(value); //$NON-NLS-1$
		isTrue = "true".equals(value); //$NON-NLS-1$
		isFalse = "false".equals(value); //$NON-NLS-1$
	}

	@Override
	public boolean asBoolean() {
		return isNull ? super.asBoolean() : isTrue;
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
		final JsonLiteral other = (JsonLiteral) object;
		return value.equals(other.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public boolean isBoolean() {
		return isTrue || isFalse;
	}

	@Override
	public boolean isFalse() {
		return isFalse;
	}

	@Override
	public boolean isNull() {
		return isNull;
	}

	@Override
	public boolean isTrue() {
		return isTrue;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	protected void write(final JsonWriter writer) throws IOException {
		writer.writeLiteral(value);
	}

}
