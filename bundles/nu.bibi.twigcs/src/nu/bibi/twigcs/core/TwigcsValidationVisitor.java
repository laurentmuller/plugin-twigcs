/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import java.io.IOException;
import java.util.List;
import java.util.function.Predicate;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import nu.bibi.twigcs.gson.SeverityDeserializer;
import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.io.IOExecutor;
import nu.bibi.twigcs.model.TwigFile;
import nu.bibi.twigcs.model.TwigResult;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigViolation;
import nu.bibi.twigcs.preferences.PreferencesInitializer;
import nu.bibi.twigcs.preferences.ProjectPreferences;

/**
 * Resource visitor to validate Twig files.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsValidationVisitor extends AbstractResouceVisitor
		implements IConstants {

	/**
	 * Returns if the given resource is a Twig file.
	 *
	 * @param file
	 *            the resource to validate.
	 * @return <code>true</code> if a Twig file.
	 */
	public static boolean isTwigFile(final IResource resource) {
		return resource instanceof IFile
				&& TWIG_EXTENSION.equals(resource.getFileExtension());
	}

	/*
	 * the include paths
	 */
	private final List<IPath> includePaths;

	/*
	 * the exclude paths
	 */
	private final List<IPath> excludePaths;

	/*
	 * the Twigcs processor
	 */
	private TwigcsProcessor processor;

	/*
	 * the Gson parser
	 */
	private Gson gson;

	/*
	 * the severity level
	 */
	private final TwigSeverity severity;

	/*
	 * the progress monitor
	 */
	private final IProgressMonitor monitor;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param project
	 *            the project to get preferences.
	 * @param monitor
	 *            the progress monitor to display activity.
	 */
	public TwigcsValidationVisitor(final IProject project,
			final IProgressMonitor monitor) throws CoreException {
		this.monitor = monitor;

		// get preferences
		final ProjectPreferences preferences = new ProjectPreferences(project);
		severity = PreferencesInitializer.getSeverity();
		includePaths = preferences.getIncludePaths();
		excludePaths = preferences.getExcludePaths();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doVisit(final IResource resource) throws CoreException {
		if (isTwigFile(resource)) {
			final IFile file = (IFile) resource;
			monitor.subTask(file.getName());
			deleteMarkers(file);
			if (mustProcess(file)) {
				process(file);
			}
			monitor.worked(1);
		}

		return !monitor.isCanceled();
	}

	/**
	 * Creates and returns the marker for the given violation. Do nothing if the
	 * violation severity is below the preferences severity.
	 *
	 * @param file
	 *            the file to update.
	 * @param text
	 *            the parsed file content.
	 * @param violation
	 *            the violation to get values from.
	 * @return the new marker or <code>null</code> if the violation severity is
	 *         below the preferences severity.
	 * @throws CoreException
	 *             if an exception occurs while creating the marker.
	 */
	private IMarker addMarker(final IFile file, final ResourceText text,
			final TwigViolation violation) throws CoreException {
		// below?
		if (violation.getSeverity().isBelow(severity)) {
			return null;
		}

		// get values
		final String message = violation.getMessage();
		final int severity = violation.getMarkerSeverity();
		final int line = violation.getLine();
		final int offset = violation.getOffset(text);

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
	 *            the file to process.
	 * @return a string list containing the Twigcs program and its arguments.
	 * @throws CoreException
	 *             if some parameters are missing or invalid.
	 */
	private List<String> buildCommand(final IFile file) throws CoreException {
		if (processor == null) {
			processor = TwigcsProcessor.instance();
		}

		// update
		final String path = file.getLocation().toPortableString();
		processor.setSearchPath(path);

		// build
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
	 * Returns if the given file must be processed.
	 *
	 * @param file
	 *            the file to be tested.
	 * @return <code>true</code> if processed; <code>false</code> to skip.
	 */
	private boolean mustProcess(final IFile file) {
		// include paths?
		if (includePaths.isEmpty()) {
			return false;
		}

		// predicate
		final IPath path = file.getProjectRelativePath();
		final Predicate<IPath> isPrefixOf = p -> p.isPrefixOf(path);

		// include paths
		if (includePaths.contains(path)) {
			return true;
		} else if (includePaths.stream().anyMatch(isPrefixOf)) {
			return true;
		}

		// exclude paths
		if (excludePaths.contains(path)) {
			return false;
		} else if (excludePaths.stream().anyMatch(isPrefixOf)) {
			return false;
		}

		// default
		return false;
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
	 * Validate the given file with the Twigcs component.
	 *
	 * @param file
	 *            the file to validate.
	 * @throws CoreException
	 *             if an error occurs while processing the file.
	 */
	private void process(final IFile file) throws CoreException {
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
						Messages.ValidationVisitor_Error_Validate_Code,
						file.getName(), exitCode);
				throw createCoreException(msg, e);
			}

		} catch (IOException | JsonSyntaxException e) {
			final String msg = String.format(
					Messages.ValidationVisitor_Error_Validate_Name,
					file.getName());
			throw createCoreException(msg, e);
		}
	}
}