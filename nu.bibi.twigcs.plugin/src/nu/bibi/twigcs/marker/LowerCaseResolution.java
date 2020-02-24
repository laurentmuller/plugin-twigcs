/**
 *
 */
package nu.bibi.twigcs.marker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

/**
 * Marker resolution for lower case error.
 *
 * @author Laurent Muller
 *
 */
public class LowerCaseResolution extends AbstractResolution {

	@Override
	public String getLabel() {
		return "Update variable name";
	}

	@Override
	protected void resolve(final IFile file, final IMarker marker)
			throws CoreException {
		final int start = getCharStart(marker);
		final int end = getCharEnd(marker);
		if (start == INVALID_POS || end == INVALID_POS) {
			return;
		}

		// get content
		final String content = getFileContent(file);

		// get variable
		final String oldVariable = content.substring(start, end);
		final String newVariable = convertVariable(oldVariable);

		// new variable not present?
		if (content.indexOf(newVariable) == -1) {
			final String newContent = content.replace(oldVariable, newVariable);
			setFileContent(file, newContent);
		} else {

		}
	}

	/**
	 * Converts the variable by replacing each upper case characters with a
	 * underscore character and the lower case character. Example:
	 *
	 * <pre>
	 * "titleIcon" -> "title_icon"
	 * </pre>
	 *
	 * @param variable
	 *            the variable to convert.
	 * @return the converted variable.
	 */
	private String convertVariable(final String variable) {
		char ch;
		final StringBuffer buffer = new StringBuffer();
		for (int i = 0, len = variable.length(); i < len; i++) {
			ch = variable.charAt(i);
			if (Character.isUpperCase(ch)) {
				buffer.append('_');
				buffer.append(Character.toLowerCase(ch));
			} else {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}
}
