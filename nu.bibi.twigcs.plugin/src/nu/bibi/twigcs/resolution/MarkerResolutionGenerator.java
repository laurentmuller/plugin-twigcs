/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.resolution;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

/**
 * Marker resolution generator for Twigcs.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class MarkerResolutionGenerator
		implements IMarkerResolutionGenerator2, IResolutionConstants {

	/*
	 * the empty resolutions
	 */
	private static final IMarkerResolution[] EMPTY_RESOLUTIONS = {};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IMarkerResolution[] getResolutions(final IMarker marker) {
		switch (getErrorId(marker)) {
		case ERROR_LOWER_CASE:
			return toArray(LowerCaseResolution.instance());
		case ERROR_UNUSED_MACRO:
			return toArray(UnusedMacroResolution.instance());
		case ERROR_UNUSED_VARIABLE:
			return toArray(UnusedVariableResolution.instance());
		case ERROR_NO_SPACE:
			return toArray(NoSpaceResolution.instance());
		case ERROR_ONE_SPACE:
			return toArray(OneSpaceResolution.instance());
		default:
			return EMPTY_RESOLUTIONS;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasResolutions(final IMarker marker) {
		return getErrorId(marker) != ERROR_INVALID;
	}

	/**
	 * Get the source error identifier.
	 *
	 * @param marker
	 *            the marker to get identifier for.
	 * @return the error identifier or -1 if none.
	 */
	private int getErrorId(final IMarker marker) {
		return marker.getAttribute(IMarker.SOURCE_ID, ERROR_INVALID);
	}

	/**
	 * Convert the given resolution to an array.
	 *
	 * @param resolution
	 *            the resolution to convert or <code>null</code> if none.
	 * @return the array of resolutions.
	 */
	private IMarkerResolution[] toArray(final IMarkerResolution resolution) {
		if (resolution != null) {
			return new IMarkerResolution[] { resolution };
		} else {
			return EMPTY_RESOLUTIONS;
		}
	}
}
