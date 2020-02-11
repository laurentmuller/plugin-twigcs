package twigcs.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import twigcs.model.TwigSeverity;

/**
 * Resource visitor.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceVisitor
		implements IResourceVisitor, IResourceDeltaVisitor, IConstants {

	/**
	 * Creates a new instance of this class.
	 */
	public ResourceVisitor() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(final IResource resource) throws CoreException {
		check(resource);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
			check(resource);
			break;
		case IResourceDelta.CHANGED:
			check(resource);
			break;
		}
		return true;
	}

	/**
	 * Validate the given resource.
	 *
	 * @param resource
	 *            the resource to verify.
	 * @throws CoreException
	 *             if an exception occurs while validating the resource.
	 */
	private void check(final IResource resource) throws CoreException {
		if (isTwigFile(resource)) {
			final IFile file = (IFile) resource;
			final IPath path = file.getFullPath();
			if (path.toFile().exists()) {
				// remove markers
				deleteMarkers(file);

				// parse

			}
			// final String realPath = path.toPortableString();
		}
	}

	/**
	 * Delete all marker of the given file.
	 *
	 * @param file
	 *            the file to update.
	 * @throws CoreException
	 *             if an exception occurs while removing markers.
	 */
	private void deleteMarkers(final IFile file) throws CoreException {
		file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
	}

	private boolean isTwigFile(final IResource resource) {
		return resource instanceof IFile
				&& resource.getFileExtension().equalsIgnoreCase(TWIG_EXTENSION);
	}

	/**
	 * Creates and returns the marker with the specified type on this resource.
	 *
	 * @param file
	 *            the file to update.
	 * @param severity
	 *            the severity marker attribute
	 * @param message
	 *            the message marker attribute.
	 * @param lineNumber
	 *            the line number marker attribute. An integer value indicating
	 *            the line number for a text marker. This attribute is
	 *            1-relative.
	 * @return the new marker.
	 * @throws CoreException
	 *             if an exception occurs while creating the marker.
	 */
	IMarker addMarker(final IFile file, final int severity,
			final String message, int lineNumber) throws CoreException {
		// check line number
		if (lineNumber == -1) {
			lineNumber = 1;
		}

		final IMarker marker = file.createMarker(MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);

		return marker;
	}

	/**
	 * Creates and returns the marker with the specified type on this resource.
	 *
	 * @param file
	 *            the file to update.
	 * @param severity
	 *            the severity marker attribute
	 * @param message
	 *            the message marker attribute.
	 * @param lineNumber
	 *            the line number marker attribute. An integer value indicating
	 *            the line number for a text marker. This attribute is
	 *            1-relative.
	 * @return the new marker.
	 * @throws CoreException
	 *             if an exception occurs while creating the marker.
	 */
	IMarker addMarker(final IFile file, final TwigSeverity severity,
			final String message, final int lineNumber) throws CoreException {
		final int markerSeverity = severity.getMarkerSeverity();
		return addMarker(file, markerSeverity, message, lineNumber);
	}
}
