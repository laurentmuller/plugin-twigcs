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
public enum TwigVersion implements ICommand {

	/**
	 * The version 1.0.
	 */
	VERSION_1("1"), //$NON-NLS-1$

	/**
	 * The version 2.0.
	 */
	VERSION_2("2"), //$NON-NLS-1$

	/**
	 * The version 3.0.
	 */
	VERSION_3("3"); //$NON-NLS-1$

	/*
	 * the parameter value
	 */
	private final String parameter;

	private TwigVersion(final String parameter) {
		this.parameter = parameter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getParameter() {
		return parameter;
	}
}
