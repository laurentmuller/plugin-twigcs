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
 * Marker resolution for unused macro warning.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class UnusedMacroResolution extends UnusedValueResolution {

	/*
	 * the shared instance
	 */
	private static volatile UnusedMacroResolution instance;

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static synchronized UnusedMacroResolution instance() {
		// double check locking
		if (instance == null) {
			synchronized (UnusedMacroResolution.class) {
				if (instance == null) {
					instance = new UnusedMacroResolution();
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
		return ERROR_UNUSED_MACRO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getLabel() {
		return Messages.Resolution_Unused_Macro;
	}
}
