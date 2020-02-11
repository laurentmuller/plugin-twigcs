package twigcs.model;

import org.eclipse.core.resources.IMarker;

/**
 * Twigcs severities
 *
 * @author Laurent Muller
 * @version 1.0
 */
public enum TwigSeverity {

	/**
	 * Severity indicating information only.
	 */
	info(1, IMarker.SEVERITY_INFO),

	/**
	 * Severity indicating a warning.
	 */
	warning(2, IMarker.SEVERITY_WARNING),

	/**
	 * Severity indicating an error state.
	 */
	error(3, IMarker.SEVERITY_ERROR),

	/**
	 * Severity indicating to ignore.
	 */
	ignore(4, IMarker.SEVERITY_INFO);

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

	/*
	 * the severity value
	 */
	private final int value;

	/*
	 * the marker value
	 */
	private final int markerSeverity;

	private TwigSeverity(final int value, final int markerSeverity) {
		this.value = value;
		this.markerSeverity = markerSeverity;
	}

	/**
	 * Returns true if the specified value is equal to this enum constant value.
	 *
	 * @param value
	 *            the value to be compared for equality with this object.
	 * @return true if the specified value is equal to this enum constant.
	 */
	public boolean equals(final int value) {
		return this.value == value;
	}

	/**
	 * Gets the marker severity. This is used to create markers.
	 *
	 * @return the marker severity.
	 * @see IMarker#SEVERITY
	 */
	public int getMarkerSeverity() {
		return markerSeverity;
	}

	/**
	 * Gets the severity value.
	 *
	 * @return the severity value.
	 */
	public int value() {
		return value;
	}
}
