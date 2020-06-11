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
public enum TwigSeverity implements ICommand {

	/**
	 * Severity indicating information only. The corresponding value for marker
	 * is {@link IMarker#SEVERITY_INFO}.
	 */
	INFO(),

	/**
	 * Severity indicating a warning. The corresponding value for marker is
	 * {@link IMarker#SEVERITY_WARNING}.
	 */
	WARNING(),

	/**
	 * Severity indicating an error state. The corresponding value for marker is
	 * {@link IMarker#SEVERITY_ERROR}.
	 */
	ERROR(),

	/**
	 * Severity to ignore. The corresponding value for marker is not defined.
	 */
	IGNORE();

	/**
	 * Finds a severity for the given value.
	 *
	 * @param value
	 *            the value to search for.
	 * @param defaultValue
	 *            the default value to return if the severity is not found.
	 * @return the severity, if found; the default value otherwise.
	 */
	public static TwigSeverity valueOf(final int value,
			final TwigSeverity defaultValue) {
		final TwigSeverity[] severities = TwigSeverity.values();
		for (final TwigSeverity severity : severities) {
			if (severity.equalsValue(value)) {
				return severity;
			}
		}

		return defaultValue;
	}

	/**
	 * Returns true if the specified value is equal to this enum constant value.
	 *
	 * @param value
	 *            the value to be compared for equality with this object.
	 * @return true if the specified value is equal to this enum constant.
	 * @see #value()
	 */
	public boolean equalsValue(final int value) {
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
	 * {@inheritDoc}
	 */
	@Override
	public String getParameter() {
		return name().toLowerCase();
	}

	/**
	 * Returns if this value is above the given severity value.
	 *
	 * @param o
	 *            the other severity to be compared.
	 * @return <code>true</code> if above.
	 * @see #isBelow(TwigSeverity)
	 */
	public boolean isAbove(final TwigSeverity o) {
		return compareTo(o) > 0;
	}

	/**
	 * Returns if this value is below the given severity value.
	 *
	 * @param o
	 *            the other severity to be compared.
	 * @return <code>true</code> if below.
	 * @see #isAbove(TwigSeverity)
	 */
	public boolean isBelow(final TwigSeverity o) {
		return compareTo(o) < 0;
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
