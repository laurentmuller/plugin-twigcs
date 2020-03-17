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
import java.io.Reader;
import java.io.StringReader;

/**
 * A streaming parser for JSON text. The parser reports all events to a given
 * handler.
 *
 * @param <A>
 *            The type of handlers used for JSON arrays
 * @param <O>
 *            The type of handlers used for JSON objects
 */
public class JsonParser<A, O> {

	private static final int MAX_NESTING_LEVEL = 1000;
	private static final int MIN_BUFFER_SIZE = 10;
	private static final int DEFAULT_BUFFER_SIZE = 1024;

	private final JsonHandler<A, O> handler;
	private Reader reader;
	private char[] buffer;
	private int bufferOffset;
	private int index;
	private int fill;
	private int line;
	private int lineOffset;
	private int current;
	private StringBuilder captureBuffer;
	private int captureStart;
	private int nestingLevel;

	/*
	 * | bufferOffset v [a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t] < input
	 * [l|m|n|o|p|q|r|s|t|?|?] < buffer ^ ^ | index fill
	 */

	/**
	 * Creates a new JsonParser with the given handler. The parser will report
	 * all parser events to this handler.
	 *
	 * @param handler
	 *            the handler to process parser events
	 * @throws JsonException
	 *             if the <code>handler</code> argument is <code>null</code>
	 */
	public JsonParser(final JsonHandler<A, O> handler) {
		if (handler == null) {
			throw new JsonException("The handler argument is null."); //$NON-NLS-1$
		}
		this.handler = handler;
		this.handler.setParser(this);
	}

	public Location getLocation() {
		final int offset = bufferOffset + index - 1;
		final int column = offset - lineOffset + 1;
		return new Location(offset, line, column);
	}

	/**
	 * Reads the entire input from the given reader and parses it as JSON. The
	 * input must contain a valid JSON value, optionally padded with whitespace.
	 * <p>
	 * Characters are read in chunks into a default-sized input buffer. Hence,
	 * wrapping a reader in an additional <code>BufferedReader</code> likely
	 * won't improve reading performance.
	 * </p>
	 *
	 * @param reader
	 *            the reader to read the input from
	 * @throws IOException
	 *             if an I/O error occurs in the reader
	 * @throws JsonException
	 *             if the <code>reader</code> argument is <code>null</code>
	 * @throws JsonParseException
	 *             if the input is not a valid JSON or if an unexpected
	 *             character is found
	 */
	public void parse(final Reader reader) throws IOException {
		parse(reader, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * Reads the entire input from the given reader and parses it as JSON. The
	 * input must contain a valid JSON value, optionally padded with whitespace.
	 * <p>
	 * Characters are read in chunks into an input buffer of the given size.
	 * Hence, wrapping a reader in an additional <code>BufferedReader</code>
	 * likely won't improve reading performance.
	 * </p>
	 *
	 * @param reader
	 *            the reader to read the input from
	 * @param bufferSize
	 *            the size of the input buffer in chars
	 * @throws JsonException
	 *             if the <code>reader</code> argument is <code>null</code> or
	 *             if the buffer size is smaller than or equal to 0
	 * @throws JsonParseException
	 *             if the input is not a valid JSON or if an unexpected
	 *             character is found
	 * @throws IOException
	 *             if an I/O error occurs in the reader
	 */
	public void parse(final Reader reader, final int bufferSize)
			throws IOException {
		if (reader == null) {
			throw new JsonException("The reader argument is null."); //$NON-NLS-1$
		}
		if (bufferSize <= 0) {
			throw new JsonException(
					"The bufferSize argument is zero or negative."); //$NON-NLS-1$
		}
		this.reader = reader;
		buffer = new char[bufferSize];
		bufferOffset = 0;
		index = 0;
		fill = 0;
		line = 1;
		lineOffset = 0;
		current = 0;
		captureStart = -1;
		read();
		skipWhiteSpace();
		readValue();
		skipWhiteSpace();
		if (!isEndOfText()) {
			throw new JsonException("An unexpected character was found."); //$NON-NLS-1$
		}
	}

	/**
	 * Parses the given input string. The input must contain a valid JSON value,
	 * optionally padded with whitespace.
	 *
	 * @param string
	 *            the input string, must be valid JSON
	 * @throws JsonException
	 *             if the <code>string</code> argument is <code>null</code>
	 * @throws JsonParseException
	 *             if the input is not a valid JSON or if an unexpected
	 *             character is found
	 */
	public void parse(final String string) {
		if (string == null) {
			throw new JsonException("The string argument is null."); //$NON-NLS-1$
		}
		final int bufferSize = Math.max(MIN_BUFFER_SIZE,
				Math.min(DEFAULT_BUFFER_SIZE, string.length()));
		try {
			parse(new StringReader(string), bufferSize);
		} catch (final IOException exception) {
			// StringReader does not throw IOException
		}
	}

	private String endCapture() {
		final int start = captureStart;
		final int end = index - 1;
		captureStart = -1;
		if (captureBuffer.length() > 0) {
			captureBuffer.append(buffer, start, end - start);
			final String captured = captureBuffer.toString();
			captureBuffer.setLength(0);
			return captured;
		}
		return new String(buffer, start, end - start);
	}

	private JsonParseException error(final String message) {
		return new JsonParseException(message, getLocation());
	}

	private JsonParseException expected(final String expected) {
		if (isEndOfText()) {
			return error("Unexpected end of input."); //$NON-NLS-1$
		}
		return error("Expected " + expected + "."); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private boolean isDigit() {
		return current >= '0' && current <= '9';
	}

	private boolean isEndOfText() {
		return current == -1;
	}

	private boolean isHexDigit() {
		return current >= '0' && current <= '9'
				|| current >= 'a' && current <= 'f'
				|| current >= 'A' && current <= 'F';
	}

	private boolean isWhiteSpace() {
		return current == ' ' || current == '\t' || current == '\n'
				|| current == '\r';
	}

	private void pauseCapture() {
		final int end = current == -1 ? index : index - 1;
		captureBuffer.append(buffer, captureStart, end - captureStart);
		captureStart = -1;
	}

	private void read() throws IOException {
		if (index == fill) {
			if (captureStart != -1) {
				captureBuffer.append(buffer, captureStart, fill - captureStart);
				captureStart = 0;
			}
			bufferOffset += fill;
			fill = reader.read(buffer, 0, buffer.length);
			index = 0;
			if (fill == -1) {
				current = -1;
				index++;
				return;
			}
		}
		if (current == '\n') {
			line++;
			lineOffset = bufferOffset + index;
		}
		current = buffer[index++];
	}

	private void readArray() throws IOException {
		final A array = handler.startArray();
		read();
		if (++nestingLevel > MAX_NESTING_LEVEL) {
			throw error("Nesting too deep"); //$NON-NLS-1$
		}
		skipWhiteSpace();
		if (readChar(']')) {
			nestingLevel--;
			handler.endArray(array);
			return;
		}
		do {
			skipWhiteSpace();
			handler.startArrayValue(array);
			readValue();
			handler.endArrayValue(array);
			skipWhiteSpace();
		} while (readChar(','));
		if (!readChar(']')) {
			throw expected("',' or ']'"); //$NON-NLS-1$
		}
		nestingLevel--;
		handler.endArray(array);
	}

	private boolean readChar(final char ch) throws IOException {
		if (current != ch) {
			return false;
		}
		read();
		return true;
	}

	private boolean readDigit() throws IOException {
		if (!isDigit()) {
			return false;
		}
		read();
		return true;
	}

	private void readEscape() throws IOException {
		read();
		switch (current) {
		case '"':
		case '/':
		case '\\':
			captureBuffer.append((char) current);
			break;
		case 'b':
			captureBuffer.append('\b');
			break;
		case 'f':
			captureBuffer.append('\f');
			break;
		case 'n':
			captureBuffer.append('\n');
			break;
		case 'r':
			captureBuffer.append('\r');
			break;
		case 't':
			captureBuffer.append('\t');
			break;
		case 'u':
			final char[] hexChars = new char[4];
			for (int i = 0; i < 4; i++) {
				read();
				if (!isHexDigit()) {
					throw expected("hexadecimal digit"); //$NON-NLS-1$
				}
				hexChars[i] = (char) current;
			}
			captureBuffer
					.append((char) Integer.parseInt(new String(hexChars), 16));
			break;
		default:
			throw expected("valid escape sequence"); //$NON-NLS-1$
		}
		read();
	}

	private boolean readExponent() throws IOException {
		if (!readChar('e') && !readChar('E')) {
			return false;
		}
		if (!readChar('+')) {
			readChar('-');
		}
		if (!readDigit()) {
			throw expected("digit"); //$NON-NLS-1$
		}
		while (readDigit()) {
		}
		return true;
	}

	private void readFalse() throws IOException {
		handler.startBoolean();
		read();
		readRequiredChar('a');
		readRequiredChar('l');
		readRequiredChar('s');
		readRequiredChar('e');
		handler.endBoolean(false);
	}

	private boolean readFraction() throws IOException {
		if (!readChar('.')) {
			return false;
		}
		if (!readDigit()) {
			throw expected("digit"); //$NON-NLS-1$
		}
		while (readDigit()) {
		}
		return true;
	}

	private String readName() throws IOException {
		if (current != '"') {
			throw expected("name"); //$NON-NLS-1$
		}
		return readStringInternal();
	}

	private void readNull() throws IOException {
		handler.startNull();
		read();
		readRequiredChar('u');
		readRequiredChar('l');
		readRequiredChar('l');
		handler.endNull();
	}

	private void readNumber() throws IOException {
		handler.startNumber();
		startCapture();
		readChar('-');
		final int firstDigit = current;
		if (!readDigit()) {
			throw expected("digit"); //$NON-NLS-1$
		}
		if (firstDigit != '0') {
			while (readDigit()) {
			}
		}
		readFraction();
		readExponent();
		handler.endNumber(endCapture());
	}

	private void readObject() throws IOException {
		final O object = handler.startObject();
		read();
		if (++nestingLevel > MAX_NESTING_LEVEL) {
			throw error("Nesting too deep"); //$NON-NLS-1$
		}
		skipWhiteSpace();
		if (readChar('}')) {
			nestingLevel--;
			handler.endObject(object);
			return;
		}
		do {
			skipWhiteSpace();
			handler.startObjectName(object);
			final String name = readName();
			handler.endObjectName(object, name);
			skipWhiteSpace();
			if (!readChar(':')) {
				throw expected("':'"); //$NON-NLS-1$
			}
			skipWhiteSpace();
			handler.startObjectValue(object, name);
			readValue();
			handler.endObjectValue(object, name);
			skipWhiteSpace();
		} while (readChar(','));
		if (!readChar('}')) {
			throw expected("',' or '}'"); //$NON-NLS-1$
		}
		nestingLevel--;
		handler.endObject(object);
	}

	private void readRequiredChar(final char ch) throws IOException {
		if (!readChar(ch)) {
			throw expected("'" + ch + "'"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	private void readString() throws IOException {
		handler.startString();
		handler.endString(readStringInternal());
	}

	private String readStringInternal() throws IOException {
		read();
		startCapture();
		while (current != '"') {
			if (current == '\\') {
				pauseCapture();
				readEscape();
				startCapture();
			} else if (current < 0x20) {
				throw expected("valid string character"); //$NON-NLS-1$
			} else {
				read();
			}
		}
		final String string = endCapture();
		read();
		return string;
	}

	private void readTrue() throws IOException {
		handler.startBoolean();
		read();
		readRequiredChar('r');
		readRequiredChar('u');
		readRequiredChar('e');
		handler.endBoolean(true);
	}

	private void readValue() throws IOException {
		switch (current) {
		case 'n':
			readNull();
			break;
		case 't':
			readTrue();
			break;
		case 'f':
			readFalse();
			break;
		case '"':
			readString();
			break;
		case '[':
			readArray();
			break;
		case '{':
			readObject();
			break;
		case '-':
		case '0':
		case '1':
		case '2':
		case '3':
		case '4':
		case '5':
		case '6':
		case '7':
		case '8':
		case '9':
			readNumber();
			break;
		default:
			throw expected("value"); //$NON-NLS-1$
		}
	}

	private void skipWhiteSpace() throws IOException {
		while (isWhiteSpace()) {
			read();
		}
	}

	private void startCapture() {
		if (captureBuffer == null) {
			captureBuffer = new StringBuilder();
		}
		captureStart = index - 1;
	}

}
