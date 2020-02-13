/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import twigcs.TwigcsPlugin;
import twigcs.model.TwigReporter;
import twigcs.model.TwigSeverity;
import twigcs.model.TwigVersion;

/**
 * Twigcs preferences initializer.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsPreferencesInitializer extends AbstractPreferenceInitializer
		implements TwigcsPreferencesConstants {

	/**
	 * Gets the preference store.
	 *
	 * @return the preference store.
	 */
	public static IPreferenceStore getPreferenceStore() {
		return TwigcsPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Gets the twig executable path from the preference store.
	 *
	 * @return the twig executable path.
	 */
	public static String getTwigExecutable() {
		final IPreferenceStore store = getPreferenceStore();
		return store.getString(P_EXECUTABLE_PATH);
	}

	/**
	 * Gets the twig reporter from the preference store.
	 *
	 * @return the twig version.
	 */
	public static TwigReporter getTwigReporter() {
		return valueOf(P_REPORTER, TwigReporter.class);
	}

	/**
	 * Gets the twig severity from the preference store.
	 *
	 * @return the twig severity.
	 */
	public static TwigSeverity getTwigSeverity() {
		return valueOf(P_SEVERITY, TwigSeverity.class);
	}

	/**
	 * Gets the twig version from the preference store.
	 *
	 * @return the twig version
	 */
	public static TwigVersion getTwigVersion() {
		return valueOf(P_TWIG_VERSION, TwigVersion.class);
	}

	/**
	 * Returns the enum constant of the specified enum type
	 *
	 * @param <T>
	 *            the enum type whose constant is to be returned.
	 * @param key
	 *            the key to get the name of the constant from preference store.
	 * @param clazz
	 *            the {@code Class} object of the enum type from which to return
	 *            a constant.
	 * @return the enum constant of the specified enum type.
	 * @throws IllegalArgumentException
	 *             if the specified enum type has no constant with the specified
	 *             name.
	 */
	private static <T extends Enum<T>> T valueOf(String key, Class<T> clazz) {
		final IPreferenceStore store = getPreferenceStore();
		final String name = store.getString(key);
		return Enum.valueOf(clazz, name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = getPreferenceStore();
		store.setDefault(P_TWIG_VERSION, DEFAULT_TWIG_VERSION);
		store.setDefault(P_SEVERITY, TwigSeverity.warning.name());
		store.setDefault(P_REPORTER, DEFAULT_REPORTER);

		final String home = System.getProperty("user.home"); //$NON-NLS-1$
		if (home != null) {
			final File file = new File(home,
					"AppData/Roaming/Composer/vendor/bin/twigcs.bat"); //$NON-NLS-1$
			if (file.exists()) {
				store.setDefault(P_EXECUTABLE_PATH, file.getAbsolutePath());
			}
		}
	}
}
