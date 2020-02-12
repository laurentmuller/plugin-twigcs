package twigcs;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import twigcs.core.IConstants;

/**
 * The Twigcs Plugin.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsPlugin extends AbstractUIPlugin implements IConstants {

	/*
	 * The shared instance
	 */
	private static TwigcsPlugin plugin;

	/**
	 * Creates a core exception.
	 *
	 * @param message
	 *            the status message.
	 * @return the core exception.
	 */
	public static CoreException createCoreException(String message) {
		return createCoreException(message, null);
	}

	/**
	 * Creates a core exception.
	 *
	 * @param message
	 *            the status message.
	 * @param exception
	 *            a low-level exception, or <code>null</code> if not applicable.
	 * @return the core exception.
	 */
	public static CoreException createCoreException(String message,
			Throwable exception) {
		final IStatus status = createErrorStatus(message, exception);
		return new CoreException(status);
	}

	/**
	 * Creates an error status.
	 *
	 * @param message
	 *            the status message.
	 * @return the error status.
	 */
	public static IStatus createErrorStatus(String message) {
		return createErrorStatus(message, null);
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
	public static IStatus createErrorStatus(String message,
			Throwable exception) {
		return new Status(IStatus.ERROR, PLUGIN_ID, message, exception);
	}

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static TwigcsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsPlugin() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}
