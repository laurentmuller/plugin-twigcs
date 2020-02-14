/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.framework.BundleContext;

import twigcs.core.ResourceListener;

/**
 * The Twigcs Plugin.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsPlugin extends AbstractUIPlugin {

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
	 * Handles the given error status.
	 *
	 * @param status
	 *            the status to handle.
	 */
	public static void handleError(final IStatus status) {
		if (status != null) {
			StatusManager.getManager().handle(status);
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
			workspace.addResourceChangeListener(listener);
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
