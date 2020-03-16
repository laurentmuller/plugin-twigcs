/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

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
	String PLUGIN_ID = "nu.bibi.twigcs"; //$NON-NLS-1$

	/**
	 * The project build identifier.
	 */
	String BUILDER_ID = PLUGIN_ID + ".builder"; //$NON-NLS-1$

	/**
	 * The project nature identifier.
	 */
	String NATURE_ID = PLUGIN_ID + ".nature"; //$NON-NLS-1$ twigcsNature

	/**
	 * The marker identifier.
	 */
	String MARKER_TYPE = PLUGIN_ID + ".marker"; //$NON-NLS-1$

	/**
	 * The Twig file extension.
	 */
	String TWIG_EXTENSION = "twig"; //$NON-NLS-1$

	/**
	 * The PHP project nature identifier.
	 */
	String PHP_NATURE = "org.eclipse.php.core.PHPNature"; //$NON-NLS-1$

}
