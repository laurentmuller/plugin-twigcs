package twigcs.core;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import twigcs.TwigcsPlugin;
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

	/**
	 * Creates a new instance of this class.
	 */
	public ResourceVisitor(final ProjectPreferences preferences) {
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
		if (!isFiltered(resource)) {
			check(resource);
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean visit(final IResourceDelta delta) throws CoreException {
		final IResource resource = delta.getResource();
		switch (delta.getKind()) {
		case IResourceDelta.ADDED:
		case IResourceDelta.CHANGED:
			if (!isFiltered(resource)) {
				check(resource);
				return true;
			}
			return false;
		default:
			return !isFiltered(resource);
		}
	}

	/**
	 * Creates and returns the marker for the given violation.
	 *
	 * @param file
	 *            the file to update.
	 * @param violation
	 *            the violation to get values from.
	 * @return the new marker.
	 * @throws CoreException
	 *             if an exception occurs while creating the marker.
	 */
	private IMarker addMarker(final IFile file, ResourceText text,
			TwigViolation violation) throws CoreException {
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
	 * @return a string array containing the Twigcs program and its arguments.
	 * @throws CoreException
	 *             if some parameters are missing or invalid.
	 */
	private String[] buildCommand(IFile file) throws CoreException {
		if (processor == null) {
			processor = TwigcsProcessor.instance();
		}

		// update
		processor.clearPaths();
		processor.addSearchPath(file.getLocation().toPortableString());

		return processor.buildCommand();
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
			file.getProjectRelativePath();
			// remove markers
			deleteMarkers(file);

			// process
			process(file);

			// for test
			// System.out.println(path.toString());
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

	/**
	 * gets the GSON parser.
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
	 * Returns if the given resource is filtered.
	 *
	 * @param resource
	 *            the resource to be tested.
	 * @return <code>true</code> if filtered; <code>false</code> to visit.
	 */
	private boolean isFiltered(final IResource resource) {
		// filter?
		if (noFilter) {
			return false;
		}

		// relative path
		final IPath path = resource.getProjectRelativePath();

		// predicate
		final Predicate<IPath> isPrefixOf = current -> current.isPrefixOf(path);

		// include
		if (includePaths.contains(path)) {
			return false;
		} else if (includePaths.stream().anyMatch(isPrefixOf)) {
			return false;
		}

		// exclude
		if (excludePaths.contains(path)) {
			return true;
		} else if (excludePaths.stream().anyMatch(isPrefixOf)) {
			return true;
		}

		// ??
		// !includePaths.isEmpty();
		return false;
	}

	/**
	 * Returns if the given resource is a Twig file.
	 *
	 * @param resource
	 *            the resource to validate.
	 * @return <code>true</code> if a Twig file.
	 */
	private boolean isTwigFile(final IResource resource) {
		return resource instanceof IFile
				&& resource.getFileExtension().equalsIgnoreCase(TWIG_EXTENSION);
	}

	/**
	 * Parses the execution result.
	 *
	 * @param data
	 *            the output data of the execution.
	 * @return the file result, if any; <code>null</code> otherwise.
	 */
	private TwigFile parseResult(String data) {
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
	private void process(IFile file) throws CoreException {
		try {
			// run
			final String[] command = buildCommand(file);
			final IOExecutor executor = new IOExecutor();
			final int exitCode = executor.run(command);

			// output?
			final String output = executor.getOutput();
			if (output != null && !output.isEmpty()) {
				// convert
				final TwigFile twigFile = parseResult(output);
				if (twigFile != null) {
					// add violations
					final ResourceText text = new ResourceText(file);
					for (final TwigViolation violation : twigFile) {
						addMarker(file, text, violation);
					}
				}

			} else if (exitCode != 0) { // error?
				IOException e = null;
				final String error = executor.getError();
				if (error != null && !error.isEmpty()) {
					e = new IOException(error);
				}
				final String msg = String.format(
						"Unable to process the resource '%s' (code: %d).",
						file.getName(), exitCode);
				throw TwigcsPlugin.createCoreException(msg, e);
			}

		} catch (IOException | InterruptedException | JsonSyntaxException e) {
			final String msg = String.format(
					"Unable to process the resource '%s'.", file.getName());
			throw TwigcsPlugin.createCoreException(msg, e);
		}
	}
}
