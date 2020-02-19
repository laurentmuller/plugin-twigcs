/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.osgi.framework.BundleContext;

import nu.bibi.twigcs.core.IConstants;
import nu.bibi.twigcs.core.ResourceListener;

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
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static TwigcsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Logs the given exception.
	 *
	 * @param exception
	 *            the exception to log.
	 */
	public static void log(final CoreException exception) {
		if (exception != null) {
			log(exception.getStatus());
		}
	}

	/**
	 * Logs the given status.
	 *
	 * @param status
	 *            the status to log.
	 */
	public static void log(final IStatus status) {
		if (plugin != null && status != null) {
			plugin.getLog().log(status);
		}
	}

	/**
	 * Logs the given information message.
	 *
	 * @param message
	 *            the message to log.
	 */
	public static void logInfo(final String message) {
		if (message != null) {
			log(new Status(IStatus.INFO, PLUGIN_ID, message));
		}
	}

	private ResourceListener listener;

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsPlugin() {
	}

	@Override
	public ScopedPreferenceStore getPreferenceStore() {
		return (ScopedPreferenceStore) super.getPreferenceStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		if (listener == null) {
			listener = new ResourceListener();
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.addResourceChangeListener(listener,
					IResourceChangeEvent.POST_CHANGE);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		if (listener != null) {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			workspace.removeResourceChangeListener(listener);
			listener = null;
		}
	}
}
