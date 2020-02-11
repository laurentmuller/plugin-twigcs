package twigcs.core;

/**
 * Global constants.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public interface IConstants {

	/**
	 * The plugin identifier.
	 */
	String PLUGIN_ID = "plugin-twigcs"; //$NON-NLS-1$

	/**
	 * The project build identifier.
	 */
	String BUILDER_ID = PLUGIN_ID + ".twigcs"; //$NON-NLS-1$

	/**
	 * The project nature identifier.
	 */
	String NATURE_ID = PLUGIN_ID + ".twigcs"; //$NON-NLS-1$

	/**
	 * The marker identifier.
	 */
	String MARKER_TYPE = PLUGIN_ID + ".twigcs"; //$NON-NLS-1$

	/**
	 * The Twig file extension.
	 */
	String TWIG_EXTENSION = "twig"; //$NON-NLS-1$
}
