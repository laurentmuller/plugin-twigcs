package twigcs.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import twigcs.TwigcsPlugin;
import twigcs.model.TwigSeverity;

/**
 * Twigcs preferences initializer.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsPreferencesInitializer extends AbstractPreferenceInitializer
		implements TwigcsPreferencesConstants {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = TwigcsPlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(P_TWIG_VERSION, DEFAULT_TWIG_VERSION);
		store.setDefault(P_SEVERITY, TwigSeverity.warning.name());
		store.setDefault(P_REPORTER, DEFAULT_REPORTER);
	}
}
