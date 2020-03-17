/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.preferences;

import nu.bibi.twigcs.model.TwigDisplay;
import nu.bibi.twigcs.model.TwigReporter;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;

/**
 * Twig preferences constants.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public interface IPreferencesConstants {

	/**
	 * The default display.
	 */
	TwigDisplay DEFAULT_DISPLAY = TwigDisplay.BLOCKING;

	/**
	 * The default output reporter.
	 */
	TwigReporter DEFAULT_REPORTER = TwigReporter.JSON;

	/**
	 * The default severity.
	 */
	TwigSeverity DEFAULT_SEVERITY = TwigSeverity.WARNING;

	/**
	 * The default twig version.
	 */
	TwigVersion DEFAULT_VERSION = TwigVersion.VERSION_2;

	/**
	 * The Twigcs executable path property.
	 */
	String P_EXECUTABLE_PATH = "twigcs.executable_path"; //$NON-NLS-1$

	/**
	 * The twig display property.
	 */
	String P_DISPLAY = "twigcs.display"; //$NON-NLS-1$

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
	String P_VERSION = "twigcs.version"; //$NON-NLS-1$
}
