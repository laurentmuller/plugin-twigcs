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
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import nu.bibi.twigcs.json.JsonObject.Member;

/**
 * Represents a JSON object, a set of name/value pairs, where the names are
 * strings and the values are JSON values.
 * <p>
 * Members can be added using the <code>add(String, ...)</code> methods which
 * accept instances of {@link JsonValue}, strings, primitive numbers, and
 * boolean values. To modify certain values of an object, use the
 * <code>set(String, ...)</code> methods. Please note that the <code>add</code>
 * methods are faster than <code>set</code> as they do not search for existing
 * members. On the other hand, the <code>add</code> methods do not prevent
 * adding multiple members with the same name. Duplicate names are discouraged
 * but not prohibited by JSON.
 * </p>
 * <p>
 * Members can be accessed by their name using {@link #get(String)}. A list of
 * all names can be obtained from the method {@link #names()}. This class also
 * supports iterating over the members in document order using an
 * {@link #iterator()} or an enhanced for loop:
 * </p>
 *
 * <pre>
 * for (Member member : jsonObject) {
 *   String name = member.getName();
 *   JsonValue value = member.getValue();
 *   ...
 * }
 * </pre>
 * <p>
 * Even though JSON objects are unordered by definition, instances of this class
 * preserve the order of members to allow processing in document order and to
 * guarantee a predictable output.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads
 * access a <code>JsonObject</code> instance concurrently, while at least one of
 * these threads modifies the contents of this object, access to the instance
 * must be synchronized externally. Failure to do so may lead to an inconsistent
 * state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings("serial") // use default serial UID
public class JsonObject extends JsonValue implements Iterable<Member> {

	/**
	 * Represents a member of a JSON object. A pair of a name and a value.
	 */
	public static class Member {

		private final String name;
		private final JsonValue value;

		Member(final String name, final JsonValue value) {
			this.name = name;
			this.value = value;
		}

		/**
		 * Indicates whether a given object is "equal to" this JsonObject. An
		 * object is considered equal if it is also a <code>JsonObject</code>
		 * and both objects contain the same members <em>in the same order</em>.
		 * <p>
		 * If two JsonObjects are equal, they will also produce the same JSON
		 * output.
		 * </p>
		 *
		 * @param object
		 *            the object to be compared with this JsonObject
		 * @return <tt>true</tt> if the specified object is equal to this
		 *         JsonObject, <code>false</code> otherwise
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
			final Member other = (Member) object;
			return name.equals(other.name) && value.equals(other.value);
		}

		/**
		 * Returns the name of this member.
		 *
		 * @return the name of this member, never <code>null</code>
		 */
		public String getName() {
			return name;
		}

		/**
		 * Returns the value of this member.
		 *
		 * @return the value of this member, never <code>null</code>
		 */
		public JsonValue getValue() {
			return value;
		}

		@Override
		public int hashCode() {
			int result = 1;
			result = 31 * result + name.hashCode();
			result = 31 * result + value.hashCode();
			return result;
		}

	}

	static class HashIndexTable {

		private final byte[] hashTable = new byte[32]; // must be a power of two

		HashIndexTable() {
		}

		// HashIndexTable(final HashIndexTable original) {
		// System.arraycopy(original.hashTable, 0, hashTable, 0,
		// hashTable.length);
		// }

		private void add(final String name, final int index) {
			final int slot = hashSlotFor(name);
			if (index < 0xff) {
				// increment by 1, 0 stands for empty
				hashTable[slot] = (byte) (index + 1);
			} else {
				hashTable[slot] = 0;
			}
		}

		private int get(final String name) {
			final int slot = hashSlotFor(name);
			// subtract 1, 0 stands for empty
			return (hashTable[slot] & 0xff) - 1;
		}

		private int hashSlotFor(final String element) {
			return element.hashCode() & hashTable.length - 1;
		}

		private void remove(final int index) {
			for (int i = 0; i < hashTable.length; i++) {
				if ((hashTable[i] & 0xff) == index + 1) {
					hashTable[i] = 0;
				} else if ((hashTable[i] & 0xff) > index + 1) {
					hashTable[i]--;
				}
			}
		}

	}

	/**
	 * Returns an unmodifiable JsonObject for the specified one. This method
	 * allows to provide read-only access to a JsonObject.
	 * <p>
	 * The returned JsonObject is backed by the given object and reflect changes
	 * that happen to it. Attempts to modify the returned JsonObject result in
	 * an <code>UnsupportedOperationException</code>.
	 * </p>
	 *
	 * @param object
	 *            the JsonObject for which an unmodifiable JsonObject is to be
	 *            returned
	 * @return an unmodifiable view of the specified JsonObject
	 * @throws JsonException
	 *             if the <code>object</code> argument is <code>null</code>.
	 */
	public static JsonObject unmodifiableObject(final JsonObject object) {
		return new JsonObject(object, true);
	}

	/*
	 * the names
	 */
	private final List<String> names;

	/*
	 * the values
	 */
	private final List<JsonValue> values;

	/*
	 * the hash table
	 */
	private transient HashIndexTable table;

	/**
	 * Creates a new empty JsonObject.
	 */
	public JsonObject() {
		names = new ArrayList<>();
		values = new ArrayList<>();
		table = new HashIndexTable();
	}

	/**
	 * Creates a new JsonObject, initialized with the contents of the specified
	 * JSON object.
	 *
	 * @param object
	 *            the JSON object to get the initial contents from, must not be
	 *            <code>null</code>
	 * @throws JsonException
	 *             if the <code>object</code> argument is <code>null</code>.
	 */
	public JsonObject(final JsonObject object) {
		this(object, false);
	}

	/**
	 * Creates a new JsonObject, initialized with the contents of the specified
	 * JSON object.
	 *
	 * @param object
	 *            the JSON object to get the initial contents from, must not be
	 *            <code>null</code>
	 * @param unmodifiable
	 *            <code>true</code> to create an unmodifiable object.
	 * @throws JsonException
	 *             if the <code>object</code> argument is <code>null</code>.
	 */
	private JsonObject(final JsonObject object, final boolean unmodifiable) {
		if (object == null) {
			throw new JsonException("The object argument is null."); //$NON-NLS-1$
		}
		if (unmodifiable) {
			names = Collections.unmodifiableList(object.names);
			values = Collections.unmodifiableList(object.values);
		} else {
			names = new ArrayList<>(object.names);
			values = new ArrayList<>(object.values);
		}
		table = new HashIndexTable();
		updateHashIndex();
	}

	/**
	 * Appends a new member to the end of this object, with the specified name
	 * and the JSON representation of the specified <code>boolean</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling
	 * this method with a name that already exists in the object will append
	 * another member with the same name. In order to replace existing members,
	 * use the method <code>set(name, value)</code> instead. However, <strong>
	 * <em>add</em> is much faster than <em>set</em></strong> (because it does
	 * not need to search for existing members). Therefore <em>add</em> should
	 * be preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 * @see Json#value(boolean)
	 */
	public JsonObject add(final String name, final boolean value) {
		return add(name, Json.value(value));
	}

	/**
	 * Appends a new member to the end of this object, with the specified name
	 * and the JSON representation of the specified <code>double</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling
	 * this method with a name that already exists in the object will append
	 * another member with the same name. In order to replace existing members,
	 * use the method <code>set(name, value)</code> instead. However, <strong>
	 * <em>add</em> is much faster than <em>set</em></strong> (because it does
	 * not need to search for existing members). Therefore <em>add</em> should
	 * be preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code> or if value is infinite or
	 *             not a number (NaN).
	 * @see Json#value(double)
	 */
	public JsonObject add(final String name, final double value) {
		return add(name, Json.value(value));
	}

	/**
	 * Appends a new member to the end of this object, with the specified name
	 * and the JSON representation of the specified <code>float</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling
	 * this method with a name that already exists in the object will append
	 * another member with the same name. In order to replace existing members,
	 * use the method <code>set(name, value)</code> instead. However, <strong>
	 * <em>add</em> is much faster than <em>set</em></strong> (because it does
	 * not need to search for existing members). Therefore <em>add</em> should
	 * be preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code> or if value is infinite or
	 *             not a number (NaN).
	 * @see Json#value(float)
	 */
	public JsonObject add(final String name, final float value) {
		return add(name, Json.value(value));
	}

	/**
	 * Appends a new member to the end of this object, with the specified name
	 * and the JSON representation of the specified <code>int</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling
	 * this method with a name that already exists in the object will append
	 * another member with the same name. In order to replace existing members,
	 * use the method <code>set(name, value)</code> instead. However, <strong>
	 * <em>add</em> is much faster than <em>set</em></strong> (because it does
	 * not need to search for existing members). Therefore <em>add</em> should
	 * be preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 * @see Json#value(int)
	 */
	public JsonObject add(final String name, final int value) {
		return add(name, Json.value(value));
	}

	/**
	 * Appends a new member to the end of this object, with the specified name
	 * and the specified JSON value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling
	 * this method with a name that already exists in the object will append
	 * another member with the same name. In order to replace existing members,
	 * use the method <code>set(name, value)</code> instead. However, <strong>
	 * <em>add</em> is much faster than <em>set</em></strong> (because it does
	 * not need to search for existing members). Therefore <em>add</em> should
	 * be preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add, must not be <code>null</code>
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the <code>name</code> argument or the <code>value</code>
	 *             argument is <code>null</code>.
	 */
	public JsonObject add(final String name, final JsonValue value) {
		if (name == null) {
			throw new JsonException("The name argument is null."); //$NON-NLS-1$
		}
		if (value == null) {
			throw new JsonException("The value argument is null."); //$NON-NLS-1$
		}
		table.add(name, names.size());
		names.add(name);
		values.add(value);
		return this;
	}

	/**
	 * Appends a new member to the end of this object, with the specified name
	 * and the JSON representation of the specified <code>long</code> value.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling
	 * this method with a name that already exists in the object will append
	 * another member with the same name. In order to replace existing members,
	 * use the method <code>set(name, value)</code> instead. However, <strong>
	 * <em>add</em> is much faster than <em>set</em></strong> (because it does
	 * not need to search for existing members). Therefore <em>add</em> should
	 * be preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 * @see Json#value(long)
	 */
	public JsonObject add(final String name, final long value) {
		return add(name, Json.value(value));
	}

	/**
	 * Appends a new member to the end of this object, with the specified name
	 * and the JSON representation of the specified string.
	 * <p>
	 * This method <strong>does not prevent duplicate names</strong>. Calling
	 * this method with a name that already exists in the object will append
	 * another member with the same name. In order to replace existing members,
	 * use the method <code>set(name, value)</code> instead. However, <strong>
	 * <em>add</em> is much faster than <em>set</em></strong> (because it does
	 * not need to search for existing members). Therefore <em>add</em> should
	 * be preferred when constructing new objects.
	 * </p>
	 *
	 * @param name
	 *            the name of the member to add
	 * @param value
	 *            the value of the member to add
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 * @see Json#value(string)
	 */
	public JsonObject add(final String name, final String value) {
		return add(name, Json.value(value));
	}

	@Override
	public JsonObject asObject() {
		return this;
	}

	/**
	 * Checks if a specified member is present as a child of this object. This
	 * will not test if this object contains the literal <code>null</code>,
	 * {@link JsonValue#isNull()} should be used for this purpose.
	 *
	 * @param name
	 *            the name of the member to check for
	 * @return whether or not the member is present
	 */
	public boolean contains(final String name) {
		return names.contains(name);
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
		final JsonObject other = (JsonObject) obj;
		return names.equals(other.names) && values.equals(other.values);
	}

	/**
	 * Returns the value of the member with the specified name in this object.
	 * If this object contains multiple members with the given name, this method
	 * will return the last one.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @return the value of the last member with the specified name, or
	 *         <code>null</code> if this object does not contain a member with
	 *         that name
	 * @throws JsonException
	 *             if the <code>name</code> argument is <code>null</code>.
	 */
	public JsonValue get(final String name) {
		if (name == null) {
			throw new JsonException("The name argument is null."); //$NON-NLS-1$
		}
		final int index = indexOf(name);
		return index != -1 ? values.get(index) : null;
	}

	/**
	 * Returns the <code>boolean</code> value of the member with the specified
	 * name in this object. If this object does not contain a member with this
	 * name, the given default value is returned. If this object contains
	 * multiple members with the given name, the last one will be picked. If
	 * this member's value does not represent a JSON <code>true</code> or
	 * <code>false</code> value, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the
	 *         given default value if this object does not contain a member with
	 *         that name
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public boolean getBoolean(final String name, final boolean defaultValue) {
		final JsonValue value = get(name);
		return value != null ? value.asBoolean() : defaultValue;
	}

	/**
	 * Returns the <code>double</code> value of the member with the specified
	 * name in this object. If this object does not contain a member with this
	 * name, the given default value is returned. If this object contains
	 * multiple members with the given name, the last one will be picked. If
	 * this member's value does not represent a JSON number or if it cannot be
	 * interpreted as Java <code>double</code>, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the
	 *         given default value if this object does not contain a member with
	 *         that name
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public double getDouble(final String name, final double defaultValue) {
		final JsonValue value = get(name);
		return value != null ? value.asDouble() : defaultValue;
	}

	/**
	 * Returns the <code>float</code> value of the member with the specified
	 * name in this object. If this object does not contain a member with this
	 * name, the given default value is returned. If this object contains
	 * multiple members with the given name, the last one will be picked. If
	 * this member's value does not represent a JSON number or if it cannot be
	 * interpreted as Java <code>float</code>, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the
	 *         given default value if this object does not contain a member with
	 *         that name
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public float getFloat(final String name, final float defaultValue) {
		final JsonValue value = get(name);
		return value != null ? value.asFloat() : defaultValue;
	}

	/**
	 * Returns the <code>int</code> value of the member with the specified name
	 * in this object. If this object does not contain a member with this name,
	 * the given default value is returned. If this object contains multiple
	 * members with the given name, the last one will be picked. If this
	 * member's value does not represent a JSON number or if it cannot be
	 * interpreted as Java <code>int</code>, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the
	 *         given default value if this object does not contain a member with
	 *         that name
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public int getInt(final String name, final int defaultValue) {
		final JsonValue value = get(name);
		return value != null ? value.asInt() : defaultValue;
	}

	/**
	 * Returns the <code>long</code> value of the member with the specified name
	 * in this object. If this object does not contain a member with this name,
	 * the given default value is returned. If this object contains multiple
	 * members with the given name, the last one will be picked. If this
	 * member's value does not represent a JSON number or if it cannot be
	 * interpreted as Java <code>long</code>, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the
	 *         given default value if this object does not contain a member with
	 *         that name
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public long getLong(final String name, final long defaultValue) {
		final JsonValue value = get(name);
		return value != null ? value.asLong() : defaultValue;
	}

	/**
	 * Returns the <code>String</code> value of the member with the specified
	 * name in this object. If this object does not contain a member with this
	 * name, the given default value is returned. If this object contains
	 * multiple members with the given name, the last one is picked. If this
	 * member's value does not represent a JSON string, an exception is thrown.
	 *
	 * @param name
	 *            the name of the member whose value is to be returned
	 * @param defaultValue
	 *            the value to be returned if the requested member is missing
	 * @return the value of the last member with the specified name, or the
	 *         given default value if this object does not contain a member with
	 *         that name
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public String getString(final String name, final String defaultValue) {
		final JsonValue value = get(name);
		return value != null ? value.asString() : defaultValue;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 31 * result + names.hashCode();
		result = 31 * result + values.hashCode();
		return result;
	}

	/**
	 * Returns <code>true</code> if this object contains no members.
	 *
	 * @return <code>true</code> if this object contains no members
	 */
	public boolean isEmpty() {
		return names.isEmpty();
	}

	@Override
	public boolean isObject() {
		return true;
	}

	/**
	 * Returns an iterator over the members of this object in document order.
	 * The returned iterator cannot be used to modify this object. Attempts to
	 * modify the returned iterator will result in an
	 * <code>UnsupportedOperationException</code>.
	 *
	 * @return an iterator over the members of this object
	 */
	@Override
	public Iterator<Member> iterator() {
		final Iterator<String> namesIterator = names.iterator();
		final Iterator<JsonValue> valuesIterator = values.iterator();
		return new Iterator<JsonObject.Member>() {

			@Override
			public boolean hasNext() {
				return namesIterator.hasNext();
			}

			@Override
			public Member next() {
				final String name = namesIterator.next();
				final JsonValue value = valuesIterator.next();
				return new Member(name, value);
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

	/**
	 * Copies all members of the specified object into this object. When the
	 * specified object contains members with names that also exist in this
	 * object, the existing values in this object will be replaced by the
	 * corresponding values in the specified object.
	 *
	 * @param object
	 *            the object to merge
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the <code>object</code> argument is <code>null</code>.
	 */
	public JsonObject merge(final JsonObject object) {
		if (object == null) {
			throw new JsonException("The object argument is null."); //$NON-NLS-1$
		}
		for (final Member member : object) {
			this.set(member.name, member.value);
		}
		return this;
	}

	/**
	 * Returns a list of the names in this object in document order. The
	 * returned list is backed by this object and will reflect subsequent
	 * changes. It cannot be used to modify this object. Attempts to modify the
	 * returned list will result in an exception.
	 *
	 * @return a list of the names in this object
	 */
	public List<String> names() {
		return Collections.unmodifiableList(names);
	}

	/**
	 * Removes a member with the specified name from this object. If this object
	 * contains multiple members with the given name, only the last one is
	 * removed. If this object does not contain a member with the specified
	 * name, the object is not modified.
	 *
	 * @param name
	 *            the name of the member to remove
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the <code>name</code> argument is <code>null</code>.
	 */
	public JsonObject remove(final String name) {
		if (name == null) {
			throw new JsonException("The name argument is null."); //$NON-NLS-1$
		}
		final int index = indexOf(name);
		if (index != -1) {
			table.remove(index);
			names.remove(index);
			values.remove(index);
		}
		return this;
	}

	/**
	 * Sets the value of the member with the specified name to the JSON
	 * representation of the specified <code>boolean</code> value. If this
	 * object does not contain a member with this name, a new member is added at
	 * the end of the object. If this object contains multiple members with this
	 * name, only the last one is changed.
	 * <p>
	 * This method should <strong>only be used to modify existing
	 * objects</strong>. To fill a new object with members, the method
	 * <code>add(name, value)</code> should be preferred which is much faster
	 * (as it does not need to search for existing members).
	 * </p>
	 *
	 * @param name
	 *            the name of the member to replace
	 * @param value
	 *            the value to set to the member
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public JsonObject set(final String name, final boolean value) {
		return set(name, Json.value(value));
	}

	/**
	 * Sets the value of the member with the specified name to the JSON
	 * representation of the specified <code>double</code> value. If this object
	 * does not contain a member with this name, a new member is added at the
	 * end of the object. If this object contains multiple members with this
	 * name, only the last one is changed.
	 * <p>
	 * This method should <strong>only be used to modify existing
	 * objects</strong>. To fill a new object with members, the method
	 * <code>add(name, value)</code> should be preferred which is much faster
	 * (as it does not need to search for existing members).
	 * </p>
	 *
	 * @param name
	 *            the name of the member to replace
	 * @param value
	 *            the value to set to the member
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code> or if value is infinite or
	 *             not a number (NaN).
	 */
	public JsonObject set(final String name, final double value) {
		return set(name, Json.value(value));
	}

	/**
	 * Sets the value of the member with the specified name to the JSON
	 * representation of the specified <code>float</code> value. If this object
	 * does not contain a member with this name, a new member is added at the
	 * end of the object. If this object contains multiple members with this
	 * name, only the last one is changed.
	 * <p>
	 * This method should <strong>only be used to modify existing
	 * objects</strong>. To fill a new object with members, the method
	 * <code>add(name, value)</code> should be preferred which is much faster
	 * (as it does not need to search for existing members).
	 * </p>
	 *
	 * @param name
	 *            the name of the member to replace
	 * @param value
	 *            the value to set to the member
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code> or if value is infinite or
	 *             not a number (NaN).
	 */
	public JsonObject set(final String name, final float value) {
		return set(name, Json.value(value));
	}

	/**
	 * Sets the value of the member with the specified name to the JSON
	 * representation of the specified <code>int</code> value. If this object
	 * does not contain a member with this name, a new member is added at the
	 * end of the object. If this object contains multiple members with this
	 * name, only the last one is changed.
	 * <p>
	 * This method should <strong>only be used to modify existing
	 * objects</strong>. To fill a new object with members, the method
	 * <code>add(name, value)</code> should be preferred which is much faster
	 * (as it does not need to search for existing members).
	 * </p>
	 *
	 * @param name
	 *            the name of the member to replace
	 * @param value
	 *            the value to set to the member
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public JsonObject set(final String name, final int value) {
		return set(name, Json.value(value));
	}

	/**
	 * Sets the value of the member with the specified name to the specified
	 * JSON value. If this object does not contain a member with this name, a
	 * new member is added at the end of the object. If this object contains
	 * multiple members with this name, only the last one is changed.
	 * <p>
	 * This method should <strong>only be used to modify existing
	 * objects</strong>. To fill a new object with members, the method
	 * <code>add(name, value)</code> should be preferred which is much faster
	 * (as it does not need to search for existing members).
	 * </p>
	 *
	 * @param name
	 *            the name of the member to replace
	 * @param value
	 *            the value to set to the member, must not be <code>null</code>
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the <code>name</code> argument or the <code>value</code>
	 *             argument is <code>null</code>.
	 */
	public JsonObject set(final String name, final JsonValue value) {
		if (name == null) {
			throw new JsonException("The name argument is null."); //$NON-NLS-1$
		}
		if (value == null) {
			throw new JsonException("The value argument is null."); //$NON-NLS-1$
		}
		final int index = indexOf(name);
		if (index != -1) {
			values.set(index, value);
			return this;
		} else {
			return add(name, value);
		}
	}

	/**
	 * Sets the value of the member with the specified name to the JSON
	 * representation of the specified <code>long</code> value. If this object
	 * does not contain a member with this name, a new member is added at the
	 * end of the object. If this object contains multiple members with this
	 * name, only the last one is changed.
	 * <p>
	 * This method should <strong>only be used to modify existing
	 * objects</strong>. To fill a new object with members, the method
	 * <code>add(name, value)</code> should be preferred which is much faster
	 * (as it does not need to search for existing members).
	 * </p>
	 *
	 * @param name
	 *            the name of the member to replace
	 * @param value
	 *            the value to set to the member
	 * @return the object itself, to enable method chaining
	 */
	public JsonObject set(final String name, final long value) {
		return set(name, Json.value(value));
	}

	/**
	 * Sets the value of the member with the specified name to the JSON
	 * representation of the specified string. If this object does not contain a
	 * member with this name, a new member is added at the end of the object. If
	 * this object contains multiple members with this name, only the last one
	 * is changed.
	 * <p>
	 * This method should <strong>only be used to modify existing
	 * objects</strong>. To fill a new object with members, the method
	 * <code>add(name, value)</code> should be preferred which is much faster
	 * (as it does not need to search for existing members).
	 * </p>
	 *
	 * @param name
	 *            the name of the member to replace
	 * @param value
	 *            the value to set to the member
	 * @return the object itself, to enable method chaining
	 * @throws JsonException
	 *             if the name is <code>null</code>.
	 */
	public JsonObject set(final String name, final String value) {
		return set(name, Json.value(value));
	}

	/**
	 * Returns the number of members (name/value pairs) in this object.
	 *
	 * @return the number of members in this object
	 */
	public int size() {
		return names.size();
	}

	@Override
	protected void write(final JsonWriter writer) throws IOException {
		writer.writeObjectOpen();
		if (!isEmpty()) {
			boolean separator = false;
			for (final Member member : this) {
				if (separator) {
					writer.writeObjectSeparator();
				}
				writer.writeMemberName(member.getName());
				writer.writeMemberSeparator();
				member.getValue().write(writer);
				separator = true;
			}
		}
		writer.writeObjectClose();
	}

	private int indexOf(final String name) {
		final int index = table.get(name);
		if (index != -1 && name.equals(names.get(index))) {
			return index;
		}
		return names.lastIndexOf(name);
	}

	private synchronized void readObject(final ObjectInputStream inputStream)
			throws IOException, ClassNotFoundException {
		inputStream.defaultReadObject();
		table = new HashIndexTable();
		updateHashIndex();
	}

	private void updateHashIndex() {
		final int size = names.size();
		for (int i = 0; i < size; i++) {
			table.add(names.get(i), i);
		}
	}

}
