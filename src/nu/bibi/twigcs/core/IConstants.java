/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * Global constants.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public interface IConstants {

	/**
	 * The plugin identifier.
	 */
	String PLUGIN_ID = "nu.bibi.twigcs"; //$NON-NLS-1$

	/**
	 * The project build identifier.
	 */
	String BUILDER_ID = PLUGIN_ID + ".builder"; //$NON-NLS-1$

	/**
	 * The project nature identifier.
	 */
	String NATURE_ID = PLUGIN_ID + ".nature"; //$NON-NLS-1$ twigcsNature

	/**
	 * The marker identifier.
	 */
	String MARKER_TYPE = PLUGIN_ID + ".marker"; //$NON-NLS-1$

	/**
	 * The Twig file extension.
	 */
	String TWIG_EXTENSION = "twig"; //$NON-NLS-1$

	/**
	 * Creates a core exception.
	 *
	 * @param message
	 *            the status message.
	 * @param exception
	 *            a low-level exception, or <code>null</code> if not applicable.
	 * @return the core exception.
	 */
	default CoreException createCoreException(final String message,
			final Throwable exception) {
		final IStatus status = createErrorStatus(message, exception);
		return new CoreException(status);
	}

	/**
	 * Creates an error status.
	 *
	 * @param message
	 *            the status message.
	 * @param exception
	 *            a low-level exception, or <code>null</code> if not applicable.
	 * @return the error status.
	 */
	default IStatus createErrorStatus(final String message,
			final Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, exception);
	}

	/**
	 * Handles the given error status.
	 *
	 * @param status
	 *            the status to handle.
	 */
	default void handleError(final IStatus status) {
		if (status != null) {
			StatusManager.getManager().handle(status);
		}
	}
}
