package twigcs.preferences;

import twigcs.model.TwigReporter;
import twigcs.model.TwigSeverity;
import twigcs.model.TwigVersion;

/**
 * Twig preferences constants.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public interface TwigcsPreferencesConstants {

	/**
	 * The default output reporter.
	 */
	String DEFAULT_REPORTER = TwigReporter.json.name();

	/**
	 * The default severity.
	 */
	String DEFAULT_SEVERITY = TwigSeverity.warning.name();

	/**
	 * The default twig version.
	 */
	String DEFAULT_TWIG_VERSION = TwigVersion.V3.name();

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
