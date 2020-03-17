/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

/**
 * @author Laurent Muller
 *
 */
public interface IString {

	/**
	 * Replaces all underscore (<code>'_'</code>) characters by a space
	 * (<code>' '</code>) character and converts to proper case. For example:
	 *
	 * <pre>
	 * "TWIG_VERSION_1" -> "Twig version 1"
	 * </pre>
	 *
	 * @param text
	 *            the string to convert.
	 * @return the converted string.
	 */
	default String toProperCase(final String text) {
		return Character.toUpperCase(text.charAt(0))
				+ text.substring(1).replace('_', ' ').toLowerCase();
	}
}
