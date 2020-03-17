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
import java.io.Writer;

class JsonWriter {

	private static final int CONTROL_CHARACTERS_END = 0x001f;

	private static final char[] QUOT_CHARS = { '\\', '"' };
	private static final char[] BS_CHARS = { '\\', '\\' };
	private static final char[] LF_CHARS = { '\\', 'n' };
	private static final char[] CR_CHARS = { '\\', 'r' };
	private static final char[] TAB_CHARS = { '\\', 't' };

	// In JavaScript, U+2028 and U+2029 characters count as line endings and
	// must be encoded.
	// http://stackoverflow.com/questions/2965293/javascript-parse-error-on-u2028-unicode-character
	private static final char[] UNICODE_2028_CHARS = { '\\', 'u', '2', '0', '2',
			'8' };
	private static final char[] UNICODE_2029_CHARS = { '\\', 'u', '2', '0', '2',
			'9' };
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	private static char[] getReplacementChars(final char ch) {
		if (ch > '\\') {
			if (ch < '\u2028' || ch > '\u2029') {
				// The lower range contains 'a' .. 'z'. Only 2 checks required.
				return null;
			}
			return ch == '\u2028' ? UNICODE_2028_CHARS : UNICODE_2029_CHARS;
		} else if (ch == '\\') {
			return BS_CHARS;
		} else if (ch > '"') {
			// This range contains '0' .. '9' and 'A' .. 'Z'. Need 3 checks to
			// get here.
			return null;
		} else if (ch == '"') {
			return QUOT_CHARS;
		} else if (ch > CONTROL_CHARACTERS_END) {
			return null;
		}

		switch (ch) {
		case '\n':
			return LF_CHARS;
		case '\r':
			return CR_CHARS;
		case '\t':
			return TAB_CHARS;
		default:
			return new char[] { '\\', 'u', '0', '0',
					HEX_DIGITS[ch >> 4 & 0x000f], HEX_DIGITS[ch & 0x000f] };
		}
	}

	protected final Writer writer;

	JsonWriter(final Writer writer) {
		this.writer = writer;
	}

	protected void writeArrayClose() throws IOException {
		writer.write(']');
	}

	protected void writeArrayOpen() throws IOException {
		writer.write('[');
	}

	protected void writeArraySeparator() throws IOException {
		writer.write(',');
	}

	protected void writeJsonString(final String string) throws IOException {
		final int length = string.length();
		int start = 0;
		for (int index = 0; index < length; index++) {
			final char[] replacement = getReplacementChars(
					string.charAt(index));
			if (replacement != null) {
				writer.write(string, start, index - start);
				writer.write(replacement);
				start = index + 1;
			}
		}
		writer.write(string, start, length - start);
	}

	protected void writeLiteral(final String value) throws IOException {
		writer.write(value);
	}

	protected void writeMemberName(final String name) throws IOException {
		writer.write('"');
		writeJsonString(name);
		writer.write('"');
	}

	protected void writeMemberSeparator() throws IOException {
		writer.write(':');
	}

	protected void writeNumber(final String string) throws IOException {
		writer.write(string);
	}

	protected void writeObjectClose() throws IOException {
		writer.write('}');
	}

	protected void writeObjectOpen() throws IOException {
		writer.write('{');
	}

	protected void writeObjectSeparator() throws IOException {
		writer.write(',');
	}

	protected void writeString(final String string) throws IOException {
		writer.write('"');
		writeJsonString(string);
		writer.write('"');
	}

}
