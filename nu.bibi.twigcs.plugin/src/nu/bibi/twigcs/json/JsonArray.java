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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a JSON array, an ordered collection of JSON values.
 * <p>
 * Elements can be added using the <code>add(...)</code> methods which accept
 * instances of {@link JsonValue}, strings, primitive numbers, and boolean
 * values. To replace an element of an array, use the <code>set(int, ...)</code>
 * methods.
 * </p>
 * <p>
 * Elements can be accessed by their index using {@link #get(int)}. This class
 * also supports iterating over the elements in document order using an
 * {@link #iterator()} or an enhanced for loop:
 * </p>
 *
 * <pre>
 * for (JsonValue value : jsonArray) {
 *   ...
 * }
 * </pre>
 * <p>
 * An equivalent {@link List} can be obtained from the method {@link #values()}.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads
 * access a <code>JsonArray</code> instance concurrently, while at least one of
 * these threads modifies the contents of this array, access to the instance
 * must be synchronized externally. Failure to do so may lead to an inconsistent
 * state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings("serial") // use default serial UID
public class JsonArray extends JsonValue implements Iterable<JsonValue> {

	/*
	 * the values
	 */
	private final List<JsonValue> values;

	/**
	 * Creates a new empty JsonArray.
	 */
	public JsonArray() {
		values = new ArrayList<>();
	}

	/**
	 * Creates a new JsonArray with the contents of the specified JSON array.
	 *
	 * @param array
	 *            the JsonArray to get the initial contents from, must not be
	 *            <code>null</code>
	 */
	public JsonArray(final JsonArray array) {
		values = new ArrayList<>(array.values);
	}

	/**
	 * Appends the JSON representation of the specified <code>boolean</code>
	 * value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 * @see Json#value(boolean)
	 */
	public JsonArray add(final boolean value) {
		return add(Json.value(value));
	}

	/**
	 * Appends the JSON representation of the specified <code>double</code>
	 * value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 * @throws JsonException
	 *             if the <code>value</code> argument is infinite or not a
	 *             number (NaN).
	 * @see Json#value(double)
	 */
	public JsonArray add(final double value) {
		return add(Json.value(value));
	}

	/**
	 * Appends the JSON representation of the specified <code>float</code> value
	 * to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 * @throws JsonException
	 *             if the <code>value</code> argument is infinite or not a
	 *             number (NaN).
	 * @see Json#value(float)
	 */
	public JsonArray add(final float value) {
		return add(Json.value(value));
	}

	/**
	 * Appends the JSON representation of the specified <code>int</code> value
	 * to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 * @see Json#value(int)
	 */
	public JsonArray add(final int value) {
		return add(Json.value(value));
	}

	/**
	 * Appends the specified JSON value to the end of this array.
	 *
	 * @param value
	 *            the JsonValue to add to the array, must not be
	 *            <code>null</code>
	 * @return the array itself, to enable method chaining
	 * @throws JsonException
	 *             if the <code>value</code> argument is <code>null</code>.
	 */
	public JsonArray add(final JsonValue value) {
		if (value == null) {
			throw new JsonException("The value argument is null."); //$NON-NLS-1$
		}
		values.add(value);
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>long</code> value
	 * to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 * @see Json#value(long)
	 */
	public JsonArray add(final long value) {
		return add(Json.value(value));
	}

	/**
	 * Appends the JSON representation of the specified string to the end of
	 * this array.
	 *
	 * @param value
	 *            the string to add to the array
	 * @return the array itself, to enable method chaining
	 * @see Json#value(String)
	 */
	public JsonArray add(final String value) {
		return add(Json.value(value));
	}

	@Override
	public JsonArray asArray() {
		return this;
	}

	/**
	 * Indicates whether a given object is "equal to" this JsonArray. An object
	 * is considered equal if it is also a <code>JsonArray</code> and both
	 * arrays contain the same list of values.
	 * <p>
	 * If two JsonArrays are equal, they will also produce the same JSON output.
	 * </p>
	 *
	 * @param object
	 *            the object to be compared with this JsonArray
	 * @return <tt>true</tt> if the specified object is equal to this JsonArray,
	 *         <code>false</code> otherwise
	 */
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
		final JsonArray other = (JsonArray) object;
		return values.equals(other.values);
	}

	/**
	 * Returns the value of the element at the specified position in this array.
	 *
	 * @param index
	 *            the index of the array element to return
	 * @return the value of the element at the specified position
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JsonValue get(final int index) {
		return values.get(index);
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean isArray() {
		return true;
	}

	/**
	 * Returns <code>true</code> if this array contains no elements.
	 *
	 * @return <code>true</code> if this array contains no elements
	 */
	public boolean isEmpty() {
		return values.isEmpty();
	}

	/**
	 * Returns an iterator over the values of this array in document order. The
	 * returned iterator cannot be used to modify this array. Attempts to modify
	 * the returned iterator will result in an
	 * <code>UnsupportedOperationException</code>.
	 *
	 * @return an iterator over the values of this array
	 */
	@Override
	public Iterator<JsonValue> iterator() {
		final Iterator<JsonValue> iterator = values.iterator();
		return new Iterator<JsonValue>() {

			@Override
			public boolean hasNext() {
				return iterator.hasNext();
			}

			@Override
			public JsonValue next() {
				return iterator.next();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * Removes the element at the specified index from this array.
	 *
	 * @param index
	 *            the index of the element to remove
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JsonArray remove(final int index) {
		values.remove(index);
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>boolean</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 * @see Json#value(boolean)
	 */
	public JsonArray set(final int index, final boolean value) {
		return set(index, Json.value(value));
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>double</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 * @throws JsonException
	 *             if the <code>value</code> argument is infinite or not a
	 *             number (NaN).
	 * @see Json#value(double)
	 */
	public JsonArray set(final int index, final double value) {
		return set(index, Json.value(value));
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>float</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 * @throws JsonException
	 *             if the <code>value</code> argument is infinite or not a
	 *             number (NaN).
	 * @see Json#value(float)
	 */
	public JsonArray set(final int index, final float value) {
		return set(index, Json.value(value));
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>int</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 * @see Json#value(int)
	 */
	public JsonArray set(final int index, final int value) {
		return set(index, Json.value(value));
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * specified JSON value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position, must
	 *            not be <code>null</code>
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 * @throws JsonException
	 *             if the <code>value</code> argument is <code>null</code>.
	 */
	public JsonArray set(final int index, final JsonValue value) {
		if (value == null) {
			throw new JsonException("The value argument is null."); //$NON-NLS-1$
		}
		values.set(index, value);
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>long</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 * @see Json#value(long)
	 */
	public JsonArray set(final int index, final long value) {
		return set(index, Json.value(value));
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified string.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the string to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 * @see Json#value(String)
	 */
	public JsonArray set(final int index, final String value) {
		return set(index, Json.value(value));
	}

	/**
	 * Returns the number of elements in this array.
	 *
	 * @return the number of elements in this array
	 */
	public int size() {
		return values.size();
	}

	/**
	 * Returns a list of the values in this array in document order. The
	 * returned list is backed by this array and will reflect subsequent
	 * changes. It cannot be used to modify this array. Attempts to modify the
	 * returned list will result in an
	 * <code>UnsupportedOperationException</code>.
	 *
	 * @return a list of the values in this array
	 */
	public List<JsonValue> values() {
		return Collections.unmodifiableList(values);
	}

	@Override
	protected void write(final JsonWriter writer) throws IOException {
		writer.writeArrayOpen();
		if (!isEmpty()) {
			boolean separator = false;
			for (final JsonValue value : this) {
				if (separator) {
					writer.writeArraySeparator();
				}
				value.write(writer);
				separator = true;
			}
		}
		writer.writeArrayClose();
	}
}
