/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.preferences;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

/**
 * This implementation overrides the default behavior because saving strings
 * always change the dirty state to <code>true</code>.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class PluginScopedPreferenceStore extends ScopedPreferenceStore {

	/**
	 * Creates a new instance of this class for the given plugin.
	 *
	 * @param plugin
	 *            the plugin to get the qualifier used to look up the preference
	 *            node.
	 */
	public PluginScopedPreferenceStore(final Plugin plugin) {
		super(InstanceScope.INSTANCE, plugin.getBundle().getSymbolicName());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation of <code>ProjectScopedPreferenceStore</code> set value
	 * only if different from the the current value.
	 * </p>
	 */
	@Override
	public void setValue(final String name, final String value) {
		final String oldValue = getString(name);
		if (!oldValue.equals(value)) {
			super.setValue(name, value);
		}
	}
}
