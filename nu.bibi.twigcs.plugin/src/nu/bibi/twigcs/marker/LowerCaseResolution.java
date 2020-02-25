/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.marker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

import nu.bibi.twigcs.internal.Messages;

/**
 * Marker resolution for lower case error.
 *
 * @author Laurent Muller
 */
public class LowerCaseResolution extends AbstractResolution {

	/*
	 * the shared instance
	 */
	private static volatile LowerCaseResolution instance;

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static synchronized LowerCaseResolution instance() {
		// double check locking
		if (instance == null) {
			synchronized (LowerCaseResolution.class) {
				if (instance == null) {
					instance = new LowerCaseResolution();
				}
			}
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return Messages.Resolution_Lower_Case;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resolve(final IFile file, final IMarker marker,
			final int start, final int end) throws CoreException {
		// get contents
		final String content = getFileContentsAsString(file);

		// get variable
		final String oldVariable = content.substring(start, end);
		final String newVariable = convertVariable(oldVariable);

		// new variable not present?
		if (content.indexOf(newVariable) == -1) {
			final String newContent = content.replace(oldVariable, newVariable);
			setFileContents(file, newContent);
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
