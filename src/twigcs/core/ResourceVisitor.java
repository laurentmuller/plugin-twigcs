/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.core;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import twigcs.gson.SeverityDeserializer;
import twigcs.io.IOExecutor;
import twigcs.model.TwigFile;
import twigcs.model.TwigResult;
import twigcs.model.TwigSeverity;
import twigcs.model.TwigViolation;

/**
 * Resource visitor.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceVisitor
		implements IResourceVisitor, IResourceDeltaVisitor, IConstants {

	/*
	 * the include paths
	 */
	private final List<IPath> includePaths;

	/*
	 * the exclude paths
	 */
	private final List<IPath> excludePaths;

	/*
	 * no filter to apply
	 */
	private final boolean noFilter;

	/*
	 * the Twigcs processor
	 */
	private TwigcsProcessor processor;

	/*
	 * the Gson parser
	 */
	private Gson gson;

	/*
	 * the progress monitor
	 */
	private final IProgressMonitor monitor;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param project
	 *            the project to visit.
	 * @param monitor
	 *            the progress monitor.
	 */
	public ResourceVisitor(final IProject project,
			final IProgressMonitor monitor) throws CoreException {
		this.monitor = monitor;

		// get preferences
		final ProjectPreferences preferences = new ProjectPreferences(project);

		// get include paths
		includePaths = preferences.getIncludeResources().stream()
				.map(IResource::getProjectRelativePath)
				.collect(Collectors.toList());

		// get exclude paths
		excludePaths = preferences.getExcludeResources().stream()
				.map(IResource::getProjectRelativePath)
				.collect(Collectors.toList());

		// no filter state
		noFilter = includePaths.isEmpty() && excludePaths.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(final IResource resource) throws CoreException {
		if (resource.exists()) {
			final IPath path = resource.getFullPath();
			System.out.println("Visit   : " + path);
			if (resource instanceof IFile) {
				final IFile file = (IFile) resource;
				if (isTwigFile(file)) {
					deleteMarkers(file);
					if (!isFiltered(file)) {
						System.out.println("Validate: " + path);
						validate(file);
					}
				}
			}
		}

		return !monitor.isCanceled();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		return visit(delta.getResource());
	}

	// /**
	// * Validate the given resource.
	// *
	// * @param resource
	// * the resource to verify.
	// * @throws CoreException
	// * if an exception occurs while validating the resource.
	// */
	// private void check(final IResource resource) throws CoreException {
	// if (isTwigFile(resource)) {
	// // convert
	// final IFile file = (IFile) resource;
	//
	// // remove markers
	// // deleteMarkers(file);
	//
	// // process
	// validate(file);
	// }
	// }

	/**
	 * Creates and returns the marker for the given violation.
	 *
	 * @param file
	 *            the file to update.
	 * @param text
	 *            the parsed file content.
	 * @param violation
	 *            the violation to get values from.
	 * @return the new marker.
	 * @throws CoreException
	 *             if an exception occurs while creating the marker.
	 */
	private IMarker addMarker(final IFile file, final ResourceText text,
			final TwigViolation violation) throws CoreException {
		// get values
		final int severity = violation.getSeverity().getMarkerSeverity();
		final String message = violation.getMessage();
		final int line = violation.getLine();
		final int offset = text.getOffset(line - 1) + violation.getColumn();

		// create
		final IMarker marker = file.createMarker(MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		marker.setAttribute(IMarker.LINE_NUMBER, line);
		marker.setAttribute(IMarker.CHAR_START, offset);
		marker.setAttribute(IMarker.CHAR_END, offset + 1);

		return marker;
	}

	/**
	 * Builds the execution command.
	 *
	 * @param file
	 *            the file process.
	 * @return a string list containing the Twigcs program and its arguments.
	 * @throws CoreException
	 *             if some parameters are missing or invalid.
	 */
	private List<String> buildCommand(final IFile file) throws CoreException {
		if (processor == null) {
			processor = TwigcsProcessor.instance();
		}

		// update
		processor.clearPaths();
		processor.addSearchPath(file.getLocation().toPortableString());

		return processor.buildCommand();
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

	/**
	 * Gets the GSON parser.
	 *
	 * @return the GSON parser.
	 */
	private Gson getGson() {
		if (gson == null) {
			final GsonBuilder builder = new GsonBuilder();
			gson = builder.registerTypeAdapter(TwigSeverity.class,
					new SeverityDeserializer()).create();
		}
		return gson;
	}

	/**
	 * Returns if the given file is filtered.
	 *
	 * @param file
	 *            the file to be tested.
	 * @return <code>true</code> if filtered; <code>false</code> to visit.
	 */
	private boolean isFiltered(final IFile file) {
		// filter?
		if (noFilter) {
			return false;
		}

		// predicate
		final IPath path = file.getProjectRelativePath();
		final Predicate<IPath> isPrefixOf = p -> p.isPrefixOf(path);

		// include validation
		if (includePaths.contains(path)) {
			return false;
		} else if (includePaths.stream().anyMatch(isPrefixOf)) {
			return false;
		}

		// exclude validation
		if (excludePaths.contains(path)) {
			return true;
		} else if (excludePaths.stream().anyMatch(isPrefixOf)) {
			return true;
		}

		// return false;
		return !includePaths.isEmpty();
	}

	/**
	 * Returns if the given file is a Twig file.
	 *
	 * @param file
	 *            the file to validate.
	 * @return <code>true</code> if a Twig file.
	 */
	private boolean isTwigFile(final IFile file) {
		return file.getFileExtension().equalsIgnoreCase(TWIG_EXTENSION);
	}

	/**
	 * Parses the execution result.
	 *
	 * @param data
	 *            the output data of the execution.
	 * @return the file result, if any; <code>null</code> otherwise.
	 * @throws JsonSyntaxException
	 *             if the data output is not a valid representation of
	 *             {@link TwigResult} type.
	 */
	private TwigFile parseResult(final String data) throws JsonSyntaxException {
		final Gson gson = getGson();
		final TwigResult result = gson.fromJson(data, TwigResult.class);
		return result.first();
	}

	/**
	 * Validate the given file with the Twigcs.
	 *
	 * @param file
	 *            the file to validate.
	 * @throws CoreException
	 *             if an error occurs while processing the file.
	 */
	private void validate(final IFile file) throws CoreException {
		try {
			// run
			final List<String> command = buildCommand(file);
			final IOExecutor executor = new IOExecutor();
			final int exitCode = executor.run(command);

			// output?
			final String output = executor.getOutput();
			if (output != null && !output.isEmpty()) {
				// convert
				final TwigFile result = parseResult(output);
				if (result != null) {
					// add violations
					final ResourceText text = new ResourceText(file);
					for (final TwigViolation violation : result) {
						addMarker(file, text, violation);
					}
				}

			} else if (exitCode != 0) { // error or invalid files?
				IOException e = null;
				final String error = executor.getError();
				if (error != null && !error.isEmpty()) {
					e = new IOException(error);
				}
				final String msg = String.format(
						"Unable to validate the resource '%s' (code: %d).",
						file.getName(), exitCode);
				throw createCoreException(msg, e);
			}

		} catch (IOException | JsonSyntaxException e) {
			final String msg = String.format(
					"Unable to validate the resource '%s'.", file.getName());
			throw createCoreException(msg, e);
		}
	}
}
