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
import java.util.Arrays;

/**
 * Enables human readable JSON output by inserting whitespace between values,
 * after commas and colons. Example:
 *
 * <pre>
 * jsonValue.writeTo(writer, PrettyPrint.singleLine());
 * </pre>
 */
public class PrettyPrint extends WriterConfiguration {

	private static class PrettyPrintWriter extends JsonWriter {

		private final char[] indentChars;
		private int indent;

		private PrettyPrintWriter(final Writer writer,
				final char[] indentChars) {
			super(writer);
			this.indentChars = indentChars;
		}

		@Override
		protected void writeArrayClose() throws IOException {
			indent--;
			writeNewLine();
			super.writeArrayClose();
		}

		@Override
		protected void writeArrayOpen() throws IOException {
			indent++;
			super.writeArrayOpen();
			writeNewLine();
		}

		@Override
		protected void writeArraySeparator() throws IOException {
			super.writeArraySeparator();
			if (!writeNewLine()) {
				writer.write(' ');
			}
		}

		@Override
		protected void writeMemberSeparator() throws IOException {
			super.writeMemberSeparator();
			writer.write(' ');
		}

		@Override
		protected void writeObjectClose() throws IOException {
			indent--;
			writeNewLine();
			super.writeObjectClose();
		}

		@Override
		protected void writeObjectOpen() throws IOException {
			indent++;
			super.writeObjectOpen();
			writeNewLine();
		}

		@Override
		protected void writeObjectSeparator() throws IOException {
			super.writeObjectSeparator();
			if (!writeNewLine()) {
				writer.write(' ');
			}
		}

		private boolean writeNewLine() throws IOException {
			if (indentChars == null) {
				return false;
			}
			writer.write('\n');
			for (int i = 0; i < indent; i++) {
				writer.write(indentChars);
			}
			return true;
		}

	}

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
	public static PrettyPrint indentWithSpaces(final int number) {
		if (number < 0) {
			throw new JsonException("The number of space is negative."); //$NON-NLS-1$
		}
		final char[] chars = new char[number];
		Arrays.fill(chars, ' ');
		return new PrettyPrint(chars);
	}

	/**
	 * Print every value on a separate line. Use tabs (<code>\t</code>) for
	 * indentation.
	 *
	 * @return A PrettyPrint instance for wrapped mode with tab indentation
	 */
	public static PrettyPrint indentWithTabs() {
		return new PrettyPrint(new char[] { '\t' });
	}

	/**
	 * Do not break lines, but still insert whitespace between values.
	 *
	 * @return A PrettyPrint instance for single-line mode
	 */
	public static PrettyPrint singleLine() {
		return new PrettyPrint(null);
	}

	private final char[] indentChars;

	protected PrettyPrint(final char[] indentChars) {
		this.indentChars = indentChars;
	}

	@Override
	protected JsonWriter createWriter(final Writer writer) {
		return new PrettyPrintWriter(writer, indentChars);
	}

}
