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
 * Define how violations are returned. By default TwigCS will output all lines
 * that have violations regardless of whether they match the severity level
 * specified or not. If you only want to see violations that are greater than or
 * equal to the severity level you've specified you can use the
 * {@link TwigDisplay#blocking blocking} value.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public enum TwigDisplay {

	/**
	 * Output all lines that have violations regardless of whether they match
	 * the severity level specified or not.
	 */
	all,

	/**
	 * Output all violations that are greater than or equal to the severity
	 * level.
	 */
	blocking

}
