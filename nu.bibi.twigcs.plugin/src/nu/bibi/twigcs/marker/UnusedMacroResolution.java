/**
 *
 */
package nu.bibi.twigcs.marker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Laurent Muller
 *
 */
public class UnusedMacroResolution extends AbstractResolution {

	@Override
	public String getLabel() {
		return "Remove unused macro";
	}

	@Override
	protected void resolve(final IFile file, final IMarker marker)
			throws CoreException {
		// get positions
		int start = getCharStart(marker);
		int end = getCharEnd(marker);
		if (start == INVALID_POS || end == INVALID_POS) {
			return;
		}

		// get content
		final byte[] content = getFileContentAsByte(file);
		final int len = content.length;

		// find open bracket
		while (start != -1 && !isChar(content, start, '{')) {
			start--;
		}

		// find spaces before
		while (start > 1 && isWhitespace(content, start - 1)) {
			start--;
		}

		// find end line before
		if (start > 1 && isNewLine(content, start - 1)) {
			start--;
		}

		// find close bracket
		while (end < len && !isChar(content, end, '}')) {
			end++;
		}
		end++;

		// find spaces after
		while (end < len - 1 && isWhitespace(content, end + 1)) {
			end++;
		}

		// find end line after
		if (end < len - 1 && isNewLine(content, end + 1)) {
			end++;
		}

		// validate range
		if (start < 0 || end >= len) {
			return;
		}

		// remove line
		final byte[] newContent = new byte[len - (end - start) + 1];
		System.arraycopy(content, 0, newContent, 0, start);
		System.arraycopy(content, end, newContent, start + 1, len - end);

		// save
		setFileContent(file, newContent);
	}
}
