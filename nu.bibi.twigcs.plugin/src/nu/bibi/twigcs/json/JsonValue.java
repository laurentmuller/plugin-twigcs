/*******************************************************************************
 * Copyright (c) 2013, 2017 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package nu.bibi.twigcs.json;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Represents a JSON value. This can be a JSON <strong>object</strong>, an
 * <strong> array</strong>, a <strong>number</strong>, a
 * <strong>string</strong>, or one of the literals <strong>true</strong>,
 * <strong>false</strong>, and <strong>null</strong>.
 * <p>
 * The literals <strong>true</strong>, <strong>false</strong>, and
 * <strong>null</strong> are represented by the constants {@link Json#TRUE},
 * {@link Json#FALSE}, and {@link Json#NULL}.
 * </p>
 * <p>
 * JSON <strong>objects</strong> and <strong>arrays</strong> are represented by
 * the subtypes {@link JsonObject} and {@link JsonArray}. Instances of these
 * types can be created using the public constructors of these classes.
 * </p>
 * <p>
 * Instances that represent JSON <strong>numbers</strong>,
 * <strong>strings</strong> and <strong>boolean</strong> values can be created
 * using the static factory methods {@link Json#value(String)},
 * {@link Json#value(long)}, {@link Json#value(double)}, etc.
 * </p>
 * <p>
 * In order to find out whether an instance of this class is of a certain type,
 * the methods {@link #isObject()}, {@link #isArray()}, {@link #isString()},
 * {@link #isNumber()} etc. can be used.
 * </p>
 * <p>
 * If the type of a JSON value is known, the methods {@link #asObject()},
 * {@link #asArray()}, {@link #asString()}, {@link #asInt()}, etc. can be used
 * to get this value directly in the appropriate target type.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings("serial") // use default serial UID
public abstract class JsonValue implements Serializable {

	JsonValue() {
		// prevent subclasses outside of this package
	}

	/**
	 * Returns this JSON value as {@link JsonArray}, assuming that this value
	 * represents a JSON array. If this is not the case, an exception is thrown.
	 *
	 * @return a JSONArray for this value
	 * @throws JsonException
	 *             if this value is not a JSON array
	 */
	public JsonArray asArray() {
		throw new JsonException("This value is not an array: " + toString()); //$NON-NLS-1$
	}

	/**
	 * Returns this JSON value as a <code>boolean</code> value, assuming that
	 * this value is either <code>true</code> or <code>false</code>. If this is
	 * not the case, an exception is thrown.
	 *
	 * @return this value as <code>boolean</code>
	 * @throws JsonException
	 *             if this value is neither <code>true</code> or
	 *             <code>false</code>
	 */
	public boolean asBoolean() {
		throw new JsonException("This value is not a boolean: " + toString()); //$NON-NLS-1$
	}

	/**
	 * Returns this JSON value as a <code>double</code> value, assuming that
	 * this value represents a JSON number. If this is not the case, an
	 * exception is thrown.
	 * <p>
	 * If the JSON number is out of the <code>Double</code> range,
	 * {@link Double#POSITIVE_INFINITY} or {@link Double#NEGATIVE_INFINITY} is
	 * returned.
	 * </p>
	 *
	 * @return this value as <code>double</code>
	 * @throws JsonException
	 *             if this value is not a JSON number
	 */
	public double asDouble() {
		throw new JsonException("This value is not a double: " + toString()); //$NON-NLS-1$
	}

	/**
	 * Returns this JSON value as a <code>float</code> value, assuming that this
	 * value represents a JSON number. If this is not the case, an exception is
	 * thrown.
	 * <p>
	 * If the JSON number is out of the <code>Float</code> range,
	 * {@link Float#POSITIVE_INFINITY} or {@link Float#NEGATIVE_INFINITY} is
	 * returned.
	 * </p>
	 *
	 * @return this value as <code>float</code>
	 * @throws JsonException
	 *             if this value is not a JSON number
	 */
	public float asFloat() {
		throw new JsonException("This value is not a float: " + toString()); //$NON-NLS-1$
	}

	/**
	 * Returns this JSON value as an <code>int</code> value, assuming that this
	 * value represents a JSON number that can be interpreted as Java
	 * <code>int</code>. If this is not the case, an exception is thrown.
	 * <p>
	 * To be interpreted as Java <code>int</code>, the JSON number must neither
	 * contain an exponent nor a fraction part. Moreover, the number must be in
	 * the <code>Integer</code> range.
	 * </p>
	 *
	 * @return this value as <code>int</code>
	 * @throws JsonException
	 *             if this value is not a JSON number
	 * @throws NumberFormatException
	 *             if this JSON number can not be interpreted as
	 *             <code>int</code> value
	 */
	public int asInt() {
		throw new JsonException("This value is not an integer: " + toString()); //$NON-NLS-1$
	}

	/**
	 * Returns this JSON value as a <code>long</code> value, assuming that this
	 * value represents a JSON number that can be interpreted as Java
	 * <code>long</code>. If this is not the case, an exception is thrown.
	 * <p>
	 * To be interpreted as Java <code>long</code>, the JSON number must neither
	 * contain an exponent nor a fraction part. Moreover, the number must be in
	 * the <code>Long</code> range.
	 * </p>
	 *
	 * @return this value as <code>long</code>
	 * @throws JsonException
	 *             if this value is not a JSON number
	 * @throws NumberFormatException
	 *             if this JSON number can not be interpreted as
	 *             <code>long</code> value
	 */
	public long asLong() {
		throw new JsonException("This value is not a long: " + toString()); //$NON-NLS-1$
	}

	/**
	 * Returns this JSON value as {@link JsonObject}, assuming that this value
	 * represents a JSON object. If this is not the case, an exception is
	 * thrown.
	 *
	 * @return this value as <code>JSONObject</code>
	 * @throws JsonException
	 *             if this value is not a JSON object
	 */
	public JsonObject asObject() {
		throw new JsonException("This value is not an object: " + toString()); //$NON-NLS-1$
	}

	/**
	 * Returns this JSON value as <code>String</code>, assuming that this value
	 * represents a JSON string. If this is not the case, an exception is
	 * thrown.
	 *
	 * @return this value as <code>String</code>
	 * @throws JsonException
	 *             if this value is not a JSON string
	 */
	public String asString() {
		throw new JsonException("This value is not a string: " + toString()); //$NON-NLS-1$
	}

	@Override
	public abstract boolean equals(final Object object);

	@Override
	public abstract int hashCode();

	/**
	 * Detects whether this value represents a JSON array. If this is the case,
	 * this value is an instance of {@link JsonArray}.
	 *
	 * @return <code>true</code> if this value is an instance of JsonArray
	 */
	public boolean isArray() {
		return false;
	}

	/**
	 * Detects whether this value represents a boolean value.
	 *
	 * @return <code>true</code> if this value represents either the JSON
	 *         literal <code>true</code> or <code>false</code>
	 */
	public boolean isBoolean() {
		return false;
	}

	/**
	 * Detects whether this value represents the JSON literal
	 * <code>false</code>.
	 *
	 * @return <code>true</code> if this value represents the JSON literal
	 *         <code>false</code>
	 */
	public boolean isFalse() {
		return false;
	}

	/**
	 * Detects whether this value represents the JSON literal <code>null</code>.
	 *
	 * @return <code>true</code> if this value represents the JSON literal
	 *         <code>null</code>
	 */
	public boolean isNull() {
		return false;
	}

	/**
	 * Detects whether this value represents a JSON number. If this is the case,
	 * this value is an instance of {@link JsonNumber}.
	 *
	 * @return <code>true</code> if this value represents a JSON number
	 */
	public boolean isNumber() {
		return false;
	}

	/**
	 * Detects whether this value represents a JSON object. If this is the case,
	 * this value is an instance of {@link JsonObject}.
	 *
	 * @return <code>true</code> if this value is an instance of JsonObject
	 */
	public boolean isObject() {
		return false;
	}

	/**
	 * Detects whether this value represents a JSON string. If this is the case,
	 * this value is an instance of {@link JsonString}.
	 *
	 * @return <code>true</code> if this value represents a JSON string
	 */
	public boolean isString() {
		return false;
	}

	/**
	 * Detects whether this value represents the JSON literal <code>true</code>.
	 *
	 * @return <code>true</code> if this value represents the JSON literal
	 *         <code>true</code>
	 */
	public boolean isTrue() {
		return false;
	}

	/**
	 * Returns the JSON string for this value in its minimal form, without any
	 * additional whitespace. The result is guaranteed to be a valid input for
	 * the method {@link Json#parse(String)} and to create a value that is
	 * <em>equal</em> to this object.
	 *
	 * @return a JSON string that represents this value
	 */
	@Override
	public String toString() {
		return toString(WriterConfiguration.MINIMAL);
	}

	/**
	 * Returns the JSON string for this value using the given formatting.
	 *
	 * @param configuration
	 *            a configuration that controls the formatting or
	 *            <code>null</code> for the minimal form
	 * @return a JSON string that represents this value
	 * @throws JsonException
	 *             if the <code>configuration</code> argument is
	 *             <code>null</code>
	 */
	public String toString(final WriterConfiguration configuration) {
		final StringWriter writer = new StringWriter();
		try {
			writeTo(writer, configuration);
		} catch (final IOException e) {
			// StringWriter does not throw IOExceptions
		}
		return writer.toString();
	}

	/**
	 * Writes the JSON representation of this value to the given output stream
	 * in its minimal form, without any additional whitespace.
	 * <p>
	 * Writing performance can be improved by using a
	 * {@link java.io.BufferedOutputStream BufferedOutputStream}.
	 * </p>
	 *
	 * @param stream
	 *            the output stream to write this value to
	 * @throws JsonException
	 *             if the <code>stream</code> argument is <code>null</code>
	 * @throws IOException
	 *             if an I/O error occurs in the output stream
	 */
	public void writeTo(final OutputStream stream) throws IOException {
		writeTo(stream, WriterConfiguration.MINIMAL);
	}

	/**
	 * Writes the JSON representation of this value to the given output stream
	 * using the given formatting.
	 * <p>
	 * Writing performance can be improved by using a
	 * {@link java.io.BufferedOutputStream BufferedOutputStream}.
	 * </p>
	 *
	 * @param stream
	 *            the output stream to write this value to
	 * @param configuration
	 *            a configuration that controls the formatting
	 * @throws JsonException
	 *             if the <code>stream</code> argument or the
	 *             <code>configuration</code> argument is <code>null</code>
	 * @throws IOException
	 *             if an I/O error occurs in the output stream
	 */
	public void writeTo(final OutputStream stream,
			final WriterConfiguration configuration) throws IOException {
		if (stream == null) {
			throw new JsonException("The output stream argument is null."); //$NON-NLS-1$
		}
		if (configuration == null) {
			throw new JsonException("The configuration argument is null."); //$NON-NLS-1$
		}
		writeTo(new OutputStreamWriter(stream), configuration);
	}

	/**
	 * Writes the JSON representation of this value to the given writer in its
	 * minimal form, without any additional whitespace.
	 * <p>
	 * Writing performance can be improved by using a
	 * {@link java.io.BufferedWriter BufferedWriter}.
	 * </p>
	 *
	 * @param writer
	 *            the writer to write this value to
	 * @throws JsonException
	 *             if the <code>writer</code> argument is <code>null</code>
	 * @throws IOException
	 *             if an I/O error occurs in the writer
	 */
	public void writeTo(final Writer writer) throws IOException {
		writeTo(writer, WriterConfiguration.MINIMAL);
	}

	/**
	 * Writes the JSON representation of this value to the given writer using
	 * the given formatting.
	 * <p>
	 * Writing performance can be improved by using a
	 * {@link java.io.BufferedWriter BufferedWriter}.
	 * </p>
	 *
	 * @param writer
	 *            the writer to write this value to
	 * @param configuration
	 *            a configuration that controls the formatting
	 * @throws JsonException
	 *             if the <code>writer</code> argument or the
	 *             <code>configuration</code> argument is <code>null</code>
	 * @throws IOException
	 *             if an I/O error occurs in the writer
	 */
	public void writeTo(final Writer writer,
			final WriterConfiguration configuration) throws IOException {
		if (writer == null) {
			throw new JsonException("The writer argument is null."); //$NON-NLS-1$
		}
		if (configuration == null) {
			throw new JsonException("The configuration argument is null."); //$NON-NLS-1$
		}
		final WritingBuffer buffer = new WritingBuffer(writer, 128);
		write(configuration.createWriter(buffer));
		buffer.flush();
	}

	/**
	 * Writes the JSON representation of this value to the given writer.
	 *
	 * @param writer
	 *            the writer to write this value to
	 * @throws IOException
	 *             if an I/O error occurs in the writer
	 */
	protected abstract void write(JsonWriter writer) throws IOException;

}
