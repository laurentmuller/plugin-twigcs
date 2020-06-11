/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.json;

import java.io.Writer;
import java.util.Arrays;

/**
 * Enables human readable JSON output by inserting whitespace between values,
 * after commas and colons. Example:
 *
 * <pre>
 * jsonValue.writeTo(writer, PrettyPrint.singleLine());
 * </pre>
 */
public class WriterConfigurationPrettyPrint extends WriterConfiguration {

	/**
	 * Print every value on a separate line. Use the given number of spaces for
	 * indentation.
	 *
	 * @param number
	 *            the number of spaces to use
	 * @return A PrettyPrint instance for wrapped mode with spaces indentation
	 * @throws JsonException
	 *             if the number of spaces is negative
	 */
	public static WriterConfigurationPrettyPrint indentWithSpaces(final int number) {
		if (number < 0) {
			throw new JsonException("The number of space is negative."); //$NON-NLS-1$
		}
		final char[] chars = new char[number];
		Arrays.fill(chars, ' ');
		return new WriterConfigurationPrettyPrint(chars);
	}

	/**
	 * Print every value on a separate line. Use tabs (<code>\t</code>) for
	 * indentation.
	 *
	 * @return A PrettyPrint instance for wrapped mode with tab indentation
	 */
	public static WriterConfigurationPrettyPrint indentWithTabs() {
		return new WriterConfigurationPrettyPrint(new char[] { '\t' });
	}

	/**
	 * Do not break lines, but still insert whitespace between values.
	 *
	 * @return A PrettyPrint instance for single-line mode
	 */
	public static WriterConfigurationPrettyPrint singleLine() {
		return new WriterConfigurationPrettyPrint(null);
	}

	private final char[] indentChars;

	protected WriterConfigurationPrettyPrint(final char[] indentChars) {
		this.indentChars = indentChars;
	}

	@Override
	protected JsonWriter createWriter(final Writer writer) {
		return new JsonWriterPrettyPrint(writer, indentChars);
	}

}
