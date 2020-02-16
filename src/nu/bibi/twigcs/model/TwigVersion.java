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
 * Twig versions.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public enum TwigVersion {

	/**
	 * The version 1.0.
	 */
	VERSION_1("1"),

	/**
	 * The version 2.0.
	 */
	VERSION_2("2"),

	/**
	 * The version 3.0.
	 */
	VERSION_3("3");

	private final String version;

	private TwigVersion(final String version) {
		this.version = version;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version.
	 */
	public String version() {
		return version;
	}
}
