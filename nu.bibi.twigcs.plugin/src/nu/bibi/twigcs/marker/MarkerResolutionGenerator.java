/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * Marker resolution generator for Twigcs.
 *
 * @author Laurent Muller
 */
public class MarkerResolutionGenerator
		implements IMarkerResolutionGenerator2, IMarkerConstants {

	/*
	 * the empty resolutions
	 */
	private static final IMarkerResolution[] EMPTY_RESOLUTIONS = {};

	@Override
	public IMarkerResolution[] getResolutions(final IMarker marker) {
		switch (getErrorId(marker)) {
		case ERROR_LOWER_CASE:
			return toArray(new LowerCaseResolution());
		case ERROR_UNUSED_MACRO:
			return toArray(new UnusedMacroResolution());
		case ERROR_UNUSED_VARIABLE:
			return toArray(new UnusedVariableResolution());
		case ERROR_SPACE_BEFORE:
			return toArray(new SpaceBeforeResolution());
		case ERROR_SPACE_AFTER:
			return toArray(new SpaceAfterResolution());
		case ERROR_LINE_END_SPACE:
			return toArray(new EndLineSpaceResolution());
		default:
			return EMPTY_RESOLUTIONS;
		}
	}

	@Override
	public boolean hasResolutions(final IMarker marker) {
		return getErrorId(marker) != -1;
	}

	/**
	 * Get the error identifier.
	 *
	 * @param marker
	 *            the marker to get identifier for.
	 * @return the error identifier or -1 if none.
	 */
	private int getErrorId(final IMarker marker) {
		return marker.getAttribute(ERROR_ID, -1);
	}

	/**
	 * Convert the given resolution to an array.
	 *
	 * @param resolution
	 *            the resolution to convert.
	 * @return the array of resolutions.
	 */
	private IMarkerResolution[] toArray(final IMarkerResolution resolution) {
		return new IMarkerResolution[] { resolution };
	}
}
