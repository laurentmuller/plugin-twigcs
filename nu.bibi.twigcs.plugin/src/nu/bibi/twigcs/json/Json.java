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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * This class serves as the entry point to the minimal-json API.
 * <p>
 * To <strong>parse</strong> a given JSON input, use the <code>parse()</code>
 * methods like in this example:
 * </p>
 *
 * <pre>
 * JsonObject object = Json.parse(string).asObject();
 * </pre>
 * <p>
 * To <strong>create</strong> a JSON data structure to be serialized, use the
 * methods <code>value()</code>, <code>array()</code>, and
 * <code>object()</code>. For example, the following snippet will produce the
 * JSON string <em>{"foo": 23, "bar": true}</em>:
 * </p>
 *
 * <pre>
 * String string = Json.object().add("foo", 23).add("bar", true).toString();
 * </pre>
 * <p>
 * To <strong>create</strong> a JSON array from a given Java array, you can use
 * one of the <code>array()</code> methods with varargs parameters:
 * </p>
 *
 * <pre>
 * String[] names = ...
 * JsonArray array = Json.array(names);
 * </pre>
 */
public final class Json {

	/**
	 * Represents the JSON literal <code>null</code>.
	 */
	public static final JsonValue NULL = new JsonLiteral("null"); //$NON-NLS-1$

	/**
	 * Represents the JSON literal <code>true</code>.
	 */
	public static final JsonValue TRUE = new JsonLiteral("true"); //$NON-NLS-1$

	/**
	 * Represents the JSON literal <code>false</code>.
	 */
	public static final JsonValue FALSE = new JsonLiteral("false"); //$NON-NLS-1$

	/**
	 * Creates a new empty JsonArray. This is equivalent to creating a new
	 * JsonArray using the constructor.
	 *
	 * @return a new empty JSON array
	 */
	public static JsonArray array() {
		return new JsonArray();
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the
	 * given <code>boolean</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 * @throws JsonException
	 *             if the <code>values</code> argument are <code>null</code>.
	 * @see JsonArray#add(boolean)
	 */
	public static JsonArray array(final boolean... values) {
		if (values == null) {
			throw new JsonException("The values argument is null."); //$NON-NLS-1$
		}
		final JsonArray array = new JsonArray();
		for (final boolean value : values) {
			array.add(value);
		}
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the
	 * given <code>double</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 * @throws JsonException
	 *             if the <code>values</code> argument are <code>null</code> or
	 *             if one of the values is infinite or not a number (NaN).
	 * @see JsonArray#add(double)
	 */
	public static JsonArray array(final double... values) {
		if (values == null) {
			throw new JsonException("The values argument is null."); //$NON-NLS-1$
		}
		final JsonArray array = new JsonArray();
		for (final double value : values) {
			array.add(value);
		}
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the
	 * given <code>float</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 * @throws JsonException
	 *             if the <code>values</code> argument are <code>null</code> or
	 *             if one of the values is infinite or not a number (NaN).
	 * @see JsonArray#add(float)
	 */
	public static JsonArray array(final float... values) {
		if (values == null) {
			throw new JsonException("The values argument is null."); //$NON-NLS-1$
		}
		final JsonArray array = new JsonArray();
		for (final float value : values) {
			array.add(value);
		}
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the
	 * given <code>int</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 * @throws JsonException
	 *             if the <code>values</code> argument are <code>null</code>.
	 * @see JsonArray#add(int)
	 */
	public static JsonArray array(final int... values) {
		if (values == null) {
			throw new JsonException("The values argument is null."); //$NON-NLS-1$
		}
		final JsonArray array = new JsonArray();
		for (final int value : values) {
			array.add(value);
		}
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the
	 * given <code>long</code> values.
	 *
	 * @param values
	 *            the values to be included in the new JSON array
	 * @return a new JSON array that contains the given values
	 * @throws JsonException
	 *             if the <code>values</code> argument are <code>null</code>.
	 * @see JsonArray#add(long)
	 */
	public static JsonArray array(final long... values) {
		if (values == null) {
			throw new JsonException("The values argument is null."); //$NON-NLS-1$
		}
		final JsonArray array = new JsonArray();
		for (final long value : values) {
			array.add(value);
		}
		return array;
	}

	/**
	 * Creates a new JsonArray that contains the JSON representations of the
	 * given strings.
	 *
	 * @param values
	 *            the strings to be included in the new JSON array
	 * @return a new JSON array that contains the given strings
	 * @throws JsonException
	 *             if the <code>values</code> argument are <code>null</code>.
	 * @see JsonArray#add(String)
	 */
	public static JsonArray array(final String... values) {
		if (values == null) {
			throw new JsonException("The values argument is null."); //$NON-NLS-1$
		}
		final JsonArray array = new JsonArray();
		for (final String value : values) {
			array.add(value);
		}
		return array;
	}

	/**
	 * Creates a new empty JsonObject. This is equivalent to creating a new
	 * JsonObject using the constructor.
	 *
	 * @return a new empty JSON object
	 */
	public static JsonObject object() {
		return new JsonObject();
	}

	/**
	 * Reads the entire input from the given stream and parses it as JSON. The
	 * input must contain a valid JSON value, optionally padded with whitespace.
	 * <p>
	 * Characters are read in chunks into an input buffer. Hence, wrapping a
	 * reader in an additional <code>BufferedReader</code> likely won't improve
	 * reading performance.
	 * </p>
	 *
	 * @param stream
	 *            the input stream to read the JSON value from
	 * @return a value that represents the parsed JSON
	 * @throws IOException
	 *             if an I/O error occurs in the input stream
	 * @throws JsonParseException
	 *             if the <code>stream</code> argument is <code>null</code> or
	 *             if the input is not valid JSON
	 */
	public static JsonValue parse(final InputStream stream) throws IOException {
		if (stream == null) {
			throw new JsonException("The stream argument is null."); //$NON-NLS-1$
		}
		return parse(new InputStreamReader(stream));
	}

	/**
	 * Reads the entire input from the given reader and parses it as JSON. The
	 * input must contain a valid JSON value, optionally padded with whitespace.
	 * <p>
	 * Characters are read in chunks into an input buffer. Hence, wrapping a
	 * reader in an additional <code>BufferedReader</code> likely won't improve
	 * reading performance.
	 * </p>
	 *
	 * @param reader
	 *            the reader to read the JSON value from
	 * @return a value that represents the parsed JSON
	 * @throws IOException
	 *             if an I/O error occurs in the reader
	 * @throws JsonParseException
	 *             if the <code>reader</code> argument is <code>null</code> or
	 *             if the input is not valid JSON
	 */
	public static JsonValue parse(final Reader reader) throws IOException {
		if (reader == null) {
			throw new JsonException("The reader argument is null."); //$NON-NLS-1$
		}
		final DefaultHandler handler = new DefaultHandler();
		new JsonParser<>(handler).parse(reader);
		return handler.getValue();
	}

	/**
	 * Parses the given input string as JSON. The input must contain a valid
	 * JSON value, optionally padded with whitespace.
	 *
	 * @param string
	 *            the input string, must be valid JSON
	 * @return a value that represents the parsed JSON
	 * @throws JsonParseException
	 *             if the <code>string</code> argument is <code>null</code> or
	 *             if the input is not valid JSON
	 */
	public static JsonValue parse(final String string) {
		if (string == null) {
			throw new JsonException("The string argument is null."); //$NON-NLS-1$
		}
		final DefaultHandler handler = new DefaultHandler();
		new JsonParser<>(handler).parse(string);
		return handler.getValue();
	}

	/**
	 * Returns a JsonValue instance that represents the given
	 * <code>boolean</code> value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 */
	public static JsonValue value(final boolean value) {
		return value ? TRUE : FALSE;
	}

	/**
	 * Returns a JsonValue instance that represents the given
	 * <code>double</code> value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 * @throws JsonException
	 *             if the <code>value</code> argument is infinite or not a
	 *             number (NaN).
	 * @see Double#isInfinite()
	 * @see Double#isNaN()
	 */
	public static JsonValue value(final double value) {
		if (Double.isInfinite(value) || Double.isNaN(value)) {
			throw new JsonException(
					"Infinite and NaN values are not permitted in JSON."); //$NON-NLS-1$
		}
		return new JsonNumber(cutOffPointZero(Double.toString(value)));
	}

	/**
	 * Returns a JsonValue instance that represents the given <code>float</code>
	 * value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 * @throws JsonException
	 *             if the <code>value</code> argument is infinite or not a
	 *             number (NaN).
	 * @see Float#isInfinite()
	 * @see Float#isNaN()
	 */
	public static JsonValue value(final float value) {
		if (Float.isInfinite(value) || Float.isNaN(value)) {
			throw new JsonException(
					"Infinite and NaN values are not permitted in JSON."); //$NON-NLS-1$
		}
		return new JsonNumber(cutOffPointZero(Float.toString(value)));
	}

	/**
	 * Returns a JsonValue instance that represents the given <code>int</code>
	 * value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 */
	public static JsonValue value(final int value) {
		return new JsonNumber(Integer.toString(value, 10));
	}

	/**
	 * Returns a JsonValue instance that represents the given <code>long</code>
	 * value.
	 *
	 * @param value
	 *            the value to get a JSON representation for
	 * @return a JSON value that represents the given value
	 */
	public static JsonValue value(final long value) {
		return new JsonNumber(Long.toString(value, 10));
	}

	/**
	 * Returns a JsonValue instance that represents the given string.
	 *
	 * @param string
	 *            the string to get a JSON representation for
	 * @return a JSON value that represents the given string
	 */
	public static JsonValue value(final String string) {
		return string == null ? NULL : new JsonString(string);
	}

	private static String cutOffPointZero(final String string) {
		if (string.endsWith(".0")) { //$NON-NLS-1$
			return string.substring(0, string.length() - 2);
		}
		return string;
	}

	private Json() {
		throw new AssertionError("No Json instances is allowed."); //$NON-NLS-1$
	}

}
