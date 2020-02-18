/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.model;

import org.eclipse.core.resources.IMarker;

/**
 * Twigcs severities
 *
 * @author Laurent Muller
 * @version 1.0
 */
public enum TwigSeverity {

	/**
	 * Severity indicating information only. The corresponding value for marker
	 * is {@link IMarker#SEVERITY_INFO}.
	 */
	info(),

	/**
	 * Severity indicating a warning. The corresponding value for marker is
	 * {@link IMarker#SEVERITY_WARNING}.
	 */
	warning(),

	/**
	 * Severity indicating an error state. The corresponding value for marker is
	 * {@link IMarker#SEVERITY_ERROR}.
	 */
	error(),

	/**
	 * Severity to ignore. The corresponding value for marker is not defined.
	 */
	ignore();

	/**
	 * Finds a severity for the given value.
	 *
	 * @param value
	 *            the value to search for.
	 * @return the severity, if found; <code>null</code> otherwise.
	 */
	public static TwigSeverity valueOf(final int value) {
		final TwigSeverity[] severities = TwigSeverity.values();
		for (final TwigSeverity severity : severities) {
			if (severity.equals(value)) {
				return severity;
			}
		}

		return null;
	}

	/**
	 * Returns true if the specified value is equal to this enum constant value.
	 *
	 * @param value
	 *            the value to be compared for equality with this object.
	 * @return true if the specified value is equal to this enum constant.
	 */
	public boolean equals(final int value) {
		return value() == value;
	}

	/**
	 * Gets the marker severity. This value is used to create markers.
	 *
	 * @return the marker severity.
	 * @see IMarker#SEVERITY
	 */
	public int getMarkerSeverity() {
		return ordinal();
	}

	/**
	 * Returns if this value is above the given severity value.
	 *
	 * @param severity
	 *            the other severity to test.
	 * @return <code>true</code> if above.
	 * @see #value()
	 */
	public boolean isAbove(final TwigSeverity severity) {
		return value() > severity.value();
	}

	/**
	 * Returns if this value is below the given severity value.
	 *
	 * @param severity
	 *            the other severity to test.
	 * @return <code>true</code> if below.
	 * @see #value()
	 */
	public boolean isBelow(final TwigSeverity severity) {
		return value() < severity.value();
	}

	/**
	 * Gets the severity value.
	 *
	 * @return the severity value.
	 */
	public int value() {
		return ordinal() + 1;
	}
}
