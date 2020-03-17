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

/**
 * Controls the formatting of the JSON output. Use one of the available
 * constants.
 */
public abstract class WriterConfiguration {

	/**
	 * Write JSON in its minimal form, without any additional whitespace. This
	 * is the default.
	 */
	public static WriterConfiguration MINIMAL = new WriterConfiguration() {
		@Override
		protected JsonWriter createWriter(final Writer writer) {
			return new JsonWriter(writer);
		}
	};

	/**
	 * Write JSON in pretty-print, with each value on a separate line and an
	 * indentation of two spaces.
	 */
	public static WriterConfiguration PRETTY_PRINT = PrettyPrint.indentWithSpaces(2);

	/**
	 * Creates a JSON writer.
	 * 
	 * @param writer
	 *            the writer to write to.
	 * @return a JSON writer.
	 */
	protected abstract JsonWriter createWriter(Writer writer);

}
