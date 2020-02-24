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
public class SpaceAfterResolution extends AbstractResolution {

	@Override
	public String getLabel() {
		return "Remove space(s) after.";
	}

	@Override
	protected void resolve(final IFile file, final IMarker marker)
			throws CoreException {
		// get positions
		final int start = getCharStart(marker);
		int end = getCharEnd(marker);
		if (start == INVALID_POS || end == INVALID_POS) {
			return;
		}

		// get content
		final byte[] content = getFileContentAsByte(file);
		final int len = content.length;

		// find space after
		while (end < len && isWhitespace(content, end)) {
			end++;
		}

		// remove line
		final byte[] newContent = new byte[len - (end - start) + 1];
		System.arraycopy(content, 0, newContent, 0, start);
		System.arraycopy(content, end, newContent, start + 1, len - end);

		// save
		setFileContent(file, newContent);
	}
}
