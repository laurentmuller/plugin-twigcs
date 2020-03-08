/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.resolution;

import nu.bibi.twigcs.internal.Messages;

/**
 * Marker resolution for unused variable warning.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class UnusedVariableResolution extends UnusedValueResolution {

	/*
	 * the shared instance
	 */
	private static volatile UnusedVariableResolution instance;

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static synchronized UnusedVariableResolution instance() {
		// double check locking
		if (instance == null) {
			synchronized (UnusedVariableResolution.class) {
				if (instance == null) {
					instance = new UnusedVariableResolution();
				}
			}
		}
		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getErrorId() {
		return ERROR_UNUSED_VARIABLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return Messages.Resolution_Unused_Variable;
	}
}
