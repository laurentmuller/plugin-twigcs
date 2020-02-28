/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs;

import java.util.Optional;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.resource.ResourceLocator;
import org.eclipse.swt.graphics.Image;
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

	/**
	 * The default quick fix image identifier.
	 */
	public static final String QUICK_FIX_DEFAULT = "icons/quickfix.png"; //$NON-NLS-1$

	/**
	 * The error quick fix image identifier.
	 */
	public static final String QUICK_FIX_ERROR = "icons/quickfix_error.png"; //$NON-NLS-1$

	/**
	 * The warning quick fix image identifier.
	 */
	public static final String QUICK_FIX_WARNING = "icons/quickfix_warning.png"; //$NON-NLS-1$

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

	/*
	 * the resource listener
	 */
	private ResourceListener listener;

	/*
	 * the preference store
	 */
	private ScopedPreferenceStore preferenceStore;

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsPlugin() {
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation of <code>TwigcsPlugin</code> overrides the default
	 * behavior because saving strings always change the dirty state to
	 * <code>true</code>.
	 * </p>
	 */
	@Override
	public ScopedPreferenceStore getPreferenceStore() {
		// return (ScopedPreferenceStore) super.getPreferenceStore();
		if (preferenceStore == null) {
			final IScopeContext context = InstanceScope.INSTANCE;
			final String qualifier = getBundle().getSymbolicName();
			preferenceStore = new ScopedPreferenceStore(context, qualifier) {
				@Override
				public void setValue(final String name, final String value) {
					final String oldValue = getString(name);
					if (!oldValue.equals(value)) {
						super.setValue(name, value);
					}
				}
			};
		}
		return preferenceStore;
	}

	/**
	 * Gets the quick fix image.
	 *
	 * @return the image, if found; <code>null</code> otherwise.
	 */
	public Image getQuickFix() {
		return getImageRegistry().get(QUICK_FIX_DEFAULT);
	}

	/**
	 * Gets the error quick fix image.
	 *
	 * @return the image, if found; <code>null</code> otherwise.
	 */
	public Image getQuickFixError() {
		return getImageRegistry().get(QUICK_FIX_ERROR);
	}

	/**
	 * Gets the warning quick fix image.
	 *
	 * @return the image, if found; <code>null</code> otherwise.
	 */
	public Image getQuickFixWarning() {
		return getImageRegistry().get(QUICK_FIX_WARNING);
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ImageRegistry createImageRegistry() {
		final ImageRegistry registry = super.createImageRegistry();
		putImageDescriptor(registry, QUICK_FIX_DEFAULT);
		putImageDescriptor(registry, QUICK_FIX_ERROR);
		putImageDescriptor(registry, QUICK_FIX_WARNING);
		return registry;
	}

	/**
	 * Put an image descriptor to the given registry.
	 *
	 * @param registry
	 *            the registry to update.
	 * @param path
	 *            the path of the image file, relative to the root of this
	 *            bundle. The path is also used as key.
	 */
	private void putImageDescriptor(final ImageRegistry registry,
			final String path) {
		final Optional<ImageDescriptor> descriptor = ResourceLocator
				.imageDescriptorFromBundle(PLUGIN_ID, path);
		if (descriptor.isPresent()) {
			registry.put(path, descriptor.get());
		}
	}
}
