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
 * Interface to deals with {@link CoreException}.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public interface ICoreException /*extends IConstants*/ {

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
		return new Status(IStatus.ERROR, IConstants.PLUGIN_ID, message,
				exception);
	}

	/**
	 * Handles the given status. The {@link StatusManager#LOG LOG} style is used
	 * when this method is called.
	 *
	 * @param status
	 *            the status to handle
	 */
	default void handleStatus(final IStatus status) {
		if (status != null) {
			StatusManager.getManager().handle(status);
		}
	}

	/**
	 * Handles the given status due to the style. Because the facility depends
	 * on Workbench, this method will log the status, if Workbench isn't
	 * initialized and the style isn't {@link #NONE}. If Workbench isn't
	 * initialized and the style is {@link #NONE}, the manager will do nothing.
	 *
	 * @param status
	 *            the status to handle
	 * @param style
	 *            the style. Value can be combined with logical OR. One of
	 *            {@link StatusManager#NONE NONE}, {@link StatusManager#LOG
	 *            LOG}, {@link StatusManager#SHOW SHOW} and
	 *            {@link StatusManager#BLOCK BLOCK}.
	 */
	default void handleStatus(final IStatus status, final int style) {
		if (status != null) {
			StatusManager.getManager().handle(status, style);
		}
	}

	/**
	 * Handles the given status. The {@link StatusManager#LOG LOG} and
	 * {@link StatusManager#SHOW SHOW} style are used when this method is
	 * called.
	 *
	 * @param status
	 *            the status to handle
	 */
	default void handleStatusShow(final IStatus status) {
		handleStatus(status, StatusManager.LOG | StatusManager.SHOW);
	}
}
