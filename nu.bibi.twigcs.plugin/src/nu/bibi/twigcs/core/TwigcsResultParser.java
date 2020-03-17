/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import java.io.IOException;

import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.json.Json;
import nu.bibi.twigcs.json.JsonArray;
import nu.bibi.twigcs.json.JsonException;
import nu.bibi.twigcs.json.JsonObject;
import nu.bibi.twigcs.json.JsonValue;
import nu.bibi.twigcs.model.TwigFile;
import nu.bibi.twigcs.model.TwigResult;
import nu.bibi.twigcs.model.TwigViolation;

/**
 * Class to parse a JSON string to a {@link TwigResult}.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsResultParser {

	/*
	 * the failures member name
	 */
	private static final String KEY_FAILURES = "failures"; //$NON-NLS-1$

	/*
	 * the files member name
	 */
	private static final String KEY_FILES = "files"; //$NON-NLS-1$

	/*
	 * the file member name
	 */
	private static final String KEY_FILE = "file"; //$NON-NLS-1$

	/*
	 * the violations member name
	 */
	private static final String KEY_VIOLATIONS = "violations"; //$NON-NLS-1$

	/*
	 * the severity member name
	 */
	private static final String KEY_SEVERITY = "severity"; //$NON-NLS-1$

	/*
	 * the line member name
	 */
	private static final String KEY_LINE = "line"; //$NON-NLS-1$

	/*
	 * the column member name
	 */
	private static final String KEY_COLUMN = "column"; //$NON-NLS-1$

	/*
	 * the message member name
	 */
	private static final String KEY_MESSAGE = "message"; //$NON-NLS-1$

	/*
	 * the default value to use for string.
	 */
	private static final String UNKNOWN_VALUE = "Unknown"; //$NON-NLS-1$

	/**
	 * Parses the given JSON string and return a Twig result.
	 *
	 * @param input
	 *            the input string to parse.
	 * @return the Twig result.
	 * @throws IOException
	 *             if the input string is not a valid representation of a
	 *             {@link TwigResult} type.
	 */
	public TwigResult parse(final String input) throws IOException {
		try {
			// parse
			final JsonObject value = Json.parse(input).asObject();

			// parse failures
			final TwigResult result = new TwigResult();
			final int failures = value.getInt(KEY_FAILURES, 0);
			result.setFailures(failures);

			// failures and files?
			if (failures == 0 || !isArray(value, KEY_FILES)) {
				return result;
			}

			// parse files
			parseFiles(result, value.get(KEY_FILES).asArray());

			return result;

		} catch (final JsonException e) {
			throw new IOException(Messages.TwigcsResultParser_Error, e);
		}
	}

	/**
	 * Creates a Twig file from the given JSON object.
	 *
	 * @param obj
	 *            the JSON object to get values from.
	 * @return the Twig file.
	 */
	private TwigFile createFile(final JsonObject obj) {
		final TwigFile file = new TwigFile();
		file.setPath(obj.getString(KEY_FILE, UNKNOWN_VALUE));
		if (isArray(obj, KEY_VIOLATIONS)) {
			parseViolations(file, obj.get(KEY_VIOLATIONS).asArray());
		}
		return file;
	}

	/**
	 * Creates a Twig violation from the given JSON object.
	 *
	 * @param obj
	 *            the JSON object to get values from.
	 * @return the Twig violation.
	 */
	private TwigViolation createViolation(final JsonObject obj) {
		final TwigViolation violation = new TwigViolation();
		violation.setLine(obj.getInt(KEY_LINE, 0));
		violation.setColumn(obj.getInt(KEY_COLUMN, 0));
		violation.setSeverity(obj.getInt(KEY_SEVERITY, 0));
		violation.setMessage(obj.getString(KEY_MESSAGE, UNKNOWN_VALUE));
		return violation;
	}

	/**
	 * Checks if a specified member is present as a child of the given object
	 * and represents a JSON array.
	 *
	 * @param name
	 *            the name of the member to check for.
	 * @return <code>true</code> if present and the value is an instance of
	 *         {@link JsonArray}.
	 */
	private boolean isArray(final JsonObject obj, final String name) {
		return obj.contains(name) && obj.get(name).isArray();
	}

	/**
	 * Parses the Twig files from the given JSON array.
	 *
	 * @param result
	 *            the Twig result to append files to.
	 * @param array
	 *            the JSON array to parse.
	 */
	private void parseFiles(final TwigResult result, final JsonArray array) {
		for (final JsonValue value : array) {
			result.addFile(createFile(value.asObject()));
		}
	}

	/**
	 * Parses the Twig violations from the given JSON array.
	 *
	 * @param file
	 *            the Twig file to append violations to.
	 * @param array
	 *            the JSON array to parse.
	 */
	private void parseViolations(final TwigFile file, final JsonArray array) {
		for (final JsonValue value : array) {
			file.addViolation(createViolation(value.asObject()));
		}
	}
}
