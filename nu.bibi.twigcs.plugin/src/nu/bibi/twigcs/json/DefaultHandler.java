package nu.bibi.twigcs.json;

/**
 * The default handler implementation.
 */
public class DefaultHandler extends JsonHandler<JsonArray, JsonObject> {

	/*
	 * the current value
	 */
	protected JsonValue value;

	@Override
	public void endArray(final JsonArray array) {
		value = array;
	}

	@Override
	public void endArrayValue(final JsonArray array) {
		array.add(value);
	}

	@Override
	public void endBoolean(final boolean bool) {
		value = bool ? Json.TRUE : Json.FALSE;
	}

	@Override
	public void endNull() {
		value = Json.NULL;
	}

	@Override
	public void endNumber(final String string) {
		value = new JsonNumber(string);
	}

	@Override
	public void endObject(final JsonObject object) {
		value = object;
	}

	@Override
	public void endObjectValue(final JsonObject object, final String name) {
		object.add(name, value);
	}

	@Override
	public void endString(final String string) {
		value = new JsonString(string);
	}

	/**
	 * Gets the parsed JSON value.
	 *
	 * @return the value, if applicable; <code>null</code> otherwise.
	 */
	public JsonValue getValue() {
		return value;
	}

	@Override
	public JsonArray startArray() {
		return new JsonArray();
	}

	@Override
	public JsonObject startObject() {
		return new JsonObject();
	}

}