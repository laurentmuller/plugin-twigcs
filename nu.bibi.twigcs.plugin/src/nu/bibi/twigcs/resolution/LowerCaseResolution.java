/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.resolution;

import java.io.UnsupportedEncodingException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;

import nu.bibi.twigcs.TwigcsPlugin;
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
	public int getErrorId() {
		return ERROR_LOWER_CASE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Image getImage() {
		return TwigcsPlugin.getDefault().getQuickFixError();
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
	protected boolean canGrouping() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected byte[] resolveContents(final IFile file, final byte[] contents,
			final int start, final int end) throws CoreException {
		try {
			// get contents as string
			final String text = new String(contents, file.getCharset());

			// update variable
			final String oldVariable = text.substring(start, end);
			final String newVariable = convertVariable(oldVariable);

			// new variable present?
			if (text.indexOf(newVariable) != -1) {
				return contents;
			}

			// replace all
			final String newContent = text.replace(oldVariable, newVariable);
			return newContent.getBytes(file.getCharset());

		} catch (final UnsupportedEncodingException e) {
			final String msg = NLS.bind(Messages.Resolution_Error_Charset,
					file.getName());
			throw createCoreException(msg, e);
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
		final StringBuilder buffer = new StringBuilder();
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
