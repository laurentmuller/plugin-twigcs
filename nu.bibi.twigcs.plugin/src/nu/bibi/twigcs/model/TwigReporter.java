/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.model;

/**
 * Twigcs output reporters.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public enum TwigReporter implements ICommand {

	/**
	 * Output the Twig result to Symfony console format (1 line per files and
	 * violations).
	 */
	CONSOLE,

	/**
	 * Output the Twig result to as check style format (XML).
	 */
	CHECKSTYLE,

	/**
	 * Output the Twig result to as JUnit format (XML).
	 */
	JUNIT,

	/**
	 * Output the Twig result to as Emacs format (1 line per files and
	 * violations).
	 */
	EMACS,

	/**
	 * Output the Twig result to the Json format.
	 */
	JSON;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParameter() {
		return name().toLowerCase();
	}
}
