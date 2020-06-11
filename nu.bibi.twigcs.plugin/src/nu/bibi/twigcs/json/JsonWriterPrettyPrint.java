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

/* package */
class JsonWriterPrettyPrint extends JsonWriter {

	private final char[] indentChars;
	private int indent;

	JsonWriterPrettyPrint(final Writer writer, final char[] indentChars) {
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