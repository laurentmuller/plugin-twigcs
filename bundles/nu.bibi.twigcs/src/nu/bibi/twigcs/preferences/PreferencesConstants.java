/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.preferences;

import nu.bibi.twigcs.model.TwigReporter;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;

/**
 * Twig preferences constants.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public interface PreferencesConstants {

	/**
	 * The default output reporter.
	 */
	String DEFAULT_REPORTER = TwigReporter.json.name();

	/**
	 * The default severity (as name).
	 */
	String DEFAULT_SEVERITY = TwigSeverity.warning.name();

	/**
	 * The default twig version.
	 */
	String DEFAULT_TWIG_VERSION = TwigVersion.VERSION_3.name();

	/**
	 * The Twigcs executable path property.
	 */
	String P_EXECUTABLE_PATH = "twigcs.executable_path"; //$NON-NLS-1$

	/**
	 * The output reporter property.
	 */
	String P_REPORTER = "twigcs.reporter"; //$NON-NLS-1$

	/**
	 * The severity property.
	 */
	String P_SEVERITY = "twigcs.severity"; //$NON-NLS-1$

	/**
	 * The twig version property.
	 */
	String P_TWIG_VERSION = "twigcs.twig_version"; //$NON-NLS-1$
}
