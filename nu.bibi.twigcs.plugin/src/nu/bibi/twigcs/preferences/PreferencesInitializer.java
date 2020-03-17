/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.preferences;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import nu.bibi.twigcs.TwigcsPlugin;
import nu.bibi.twigcs.model.TwigDisplay;
import nu.bibi.twigcs.model.TwigReporter;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;

/**
 * Twigcs preferences initializer.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class PreferencesInitializer extends AbstractPreferenceInitializer
		implements IPreferencesConstants {

	/**
	 * Gets the Twigcs executable path from the preference store.
	 *
	 * @return the Twigcs executable path.
	 */
	public static String getExecutable() {
		final IPreferenceStore store = getPreferenceStore();
		return store.getString(P_EXECUTABLE_PATH);
	}

	/**
	 * Gets the preference store.
	 *
	 * @return the preference store.
	 */
	public static IPreferenceStore getPreferenceStore() {
		return TwigcsPlugin.getDefault().getPreferenceStore();
	}

	/**
	 * Gets the Twig display from the preference store.
	 *
	 * @return the Twig display.
	 */
	public static TwigDisplay getTwigDisplay() {
		return valueOf(P_DISPLAY, TwigDisplay.class, DEFAULT_DISPLAY);
	}

	/**
	 * Gets the Twig reporter from the preference store.
	 *
	 * @return the Twig reporter.
	 */
	public static TwigReporter getTwigReporter() {
		return valueOf(P_REPORTER, TwigReporter.class, DEFAULT_REPORTER);
	}

	/**
	 * Gets the Twig severity from the preference store.
	 *
	 * @return the Twig severity.
	 */
	public static TwigSeverity getTwigSeverity() {
		return valueOf(P_SEVERITY, TwigSeverity.class, DEFAULT_SEVERITY);
	}

	/**
	 * Gets the Twig version from the preference store.
	 *
	 * @return the Twig version.
	 */
	public static TwigVersion getTwigVersion() {
		return valueOf(P_VERSION, TwigVersion.class, DEFAULT_VERSION);
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
	 * @param defaultValue
	 *            the default value to return if enum constant is not found.
	 * @return the enum constant of the specified enum type.
	 */
	private static <T extends Enum<T>> T valueOf(final String key,
			final Class<T> clazz, final T defaultValue) {
		try {
			final IPreferenceStore store = getPreferenceStore();
			String name = store.getString(key);
			if (name == null || name.isEmpty()) {
				name = store.getDefaultString(key);
			}
			return Enum.valueOf(clazz, name);
		} catch (NullPointerException | IllegalArgumentException e) {
			return defaultValue;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeDefaultPreferences() {
		final IPreferenceStore store = getPreferenceStore();
		store.setDefault(P_DISPLAY, DEFAULT_DISPLAY.name());
		store.setDefault(P_VERSION, DEFAULT_VERSION.name());
		store.setDefault(P_SEVERITY, DEFAULT_SEVERITY.name());
		store.setDefault(P_REPORTER, DEFAULT_REPORTER.name());

		// find path for windows
		final String home = System.getProperty("user.home"); //$NON-NLS-1$
		if (home != null) {
			final File file = new File(home,
					"AppData/Roaming/Composer/vendor/bin/twigcs.bat"); //$NON-NLS-1$
			if (file.exists()) {
				store.setDefault(P_EXECUTABLE_PATH, file.getAbsolutePath());
			}
		}

		// TODO find path for Linux or Mac
	}
}
