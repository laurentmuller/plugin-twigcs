/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.model;

/**
 * Twigcs output reporters.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public enum TwigReporter {

	/**
	 * Output the Twig result to Symfony console format (1 line per files and
	 * violations).
	 */
	console,

	/**
	 * Output the Twig result to as check style format (XML).
	 */
	checkstyle,

	/**
	 * Output the Twig result to as JUnit format (XML).
	 */
	junit,

	/**
	 * Output the Twig result to as Emacs format (1 line per files and
	 * violations).
	 */
	emacs,

	/**
	 * Output the Twig result to the Json format.
	 */
	json
}
