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
import org.eclipse.osgi.util.NLS;

import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.io.IOExecutor;
import nu.bibi.twigcs.model.TwigFile;
import nu.bibi.twigcs.model.TwigResult;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;
import nu.bibi.twigcs.model.TwigViolation;
import nu.bibi.twigcs.preferences.ProjectPreferences;
import nu.bibi.twigcs.resolution.IResolutionConstants;

/**
 * Resource visitor to validate Twig files.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsValidationVisitor extends AbstractResouceVisitor
		implements IConstants, IResolutionConstants, ICoreException {

	/*
	 * the double quote character
	 */
	private static final char QUOTE_CHAR = '"';

	/**
	 * Returns if the given resource is a Twig file.
	 *
	 * @param file
	 *            the resource to validate.
	 * @return <code>true</code> if a Twig file.
	 */
	public static boolean isTwigFile(final IResource resource) {
		return resource instanceof IFile && resource.isAccessible()
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
	 * the Twig result parser
	 */
	private TwigResultParser parser;

	/*
	 * the twig version
	 */
	private final TwigVersion version;

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
		version = preferences.getTwigVersion();
		severity = preferences.getTwigSeverity();
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
			monitor.subTask(file.getFullPath().toOSString());
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
		final int column = violation.getColumn();
		final int offset = getOffset(message, text, line, column);
		final int length = getOffsetLength(message, text, offset);
		final int errorId = getErrorId(message);

		// create
		final IMarker marker = file.createMarker(MARKER_TYPE);
		marker.setAttribute(IMarker.MESSAGE, message);
		marker.setAttribute(IMarker.SEVERITY, severity);
		marker.setAttribute(IMarker.LINE_NUMBER, line);
		marker.setAttribute(IMarker.CHAR_START, offset);
		marker.setAttribute(IMarker.CHAR_END, offset + length);
		marker.setAttribute(IMarker.SOURCE_ID, errorId);

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
			processor.setTwigVersion(version);
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
	 * Gets the error identifier.
	 *
	 * @param message
	 *            the error message.
	 * @return the error identifier, if any; -1 otherwise.
	 */
	private int getErrorId(final String message) {
		if (isLowerCase(message)) {
			return ERROR_LOWER_CASE;
		} else if (isUnunsedMacro(message)) {
			return ERROR_UNUSED_MACRO;
		} else if (isUnunsedVariable(message)) {
			return ERROR_UNUSED_VARIABLE;
		} else if (isEndLineSpace(message) || isNoSpace(message)) {
			return ERROR_NO_SPACE;
		} else if (isOneSpace(message)) {
			return ERROR_ONE_SPACE;
		} else {
			return ERROR_INVALID;
		}
	}

	/**
	 * Gets the offset for the given violation message.
	 *
	 * @param message
	 *            the violation message.
	 * @param text
	 *            the parsed file content.
	 * @param line
	 *            the violation line.
	 * @param column
	 *            the violation column.
	 * @return the offset.
	 */
	private int getOffset(final String message, final ResourceText text,
			final int line, final int column) {
		int offset = text.getOffset(line - 1) + column;
		if (isEndLineSpace(message)) {
			final byte[] content = text.getContent();
			while (offset > 0 && isWhitespace(content, offset - 1)) {
				offset--;
			}
		} else if (isOneSpace(message)) {
			final byte[] content = text.getContent();
			if (isWhitespace(content, offset)) {
				offset++;
			}
		}
		return offset;
	}

	/**
	 * Gets the offset length for the given violation message.
	 *
	 * @param message
	 *            the violation message.
	 * @param text
	 *            the parsed file content.
	 * @param offset
	 *            the start violation offset.
	 * @return the offset length.
	 */
	private int getOffsetLength(final String message, final ResourceText text,
			final int offset) {
		int length = 1;
		if (isUnunsedVariable(message) //
				|| isUnunsedMacro(message) //
				|| isLowerCase(message)) {
			final int start = message.indexOf(QUOTE_CHAR);
			final int end = message.indexOf(QUOTE_CHAR, start + 1);
			if (start != -1 && end != -1) {
				length = end - start - 1;
			}
		} else if (isNoSpace(message)) {
			int end = offset;
			final byte[] content = text.getContent();
			while (isWhitespace(content, end)) {
				end++;
			}
			length = end - offset;
		} else if (isOneSpace(message)) {
			int end = offset + 1;
			final byte[] content = text.getContent();
			while (isWhitespace(content, end)) {
				end++;
			}
			length = end - offset;
		} else if (isEndLineSpace(message)) {
			int end = offset + 1;
			final byte[] content = text.getContent();
			while (isWhitespace(content, end)) {
				end++;
			}
			length = end - offset;
		}

		return Math.max(length, 1);
	}

	/**
	 * Gets the Twig result parser.
	 *
	 * @return Twig result parser.
	 */
	private TwigResultParser getParser() {
		if (parser == null) {
			parser = new TwigResultParser();
		}
		return parser;
	}

	/**
	 * Returns if the given message concern the end line space error.
	 *
	 * @param message
	 *            the message to be tested.
	 * @return <code>true</code> if end line space error.
	 */
	private boolean isEndLineSpace(final String message) {
		return message.contains("A line should not end with blank space"); //$NON-NLS-1$
	}

	/**
	 * Returns if the given message concern the lower case variable error.
	 *
	 * @param message
	 *            the message to be tested.
	 * @return <code>true</code> if lower case variable error.
	 */
	private boolean isLowerCase(final String message) {
		return message.contains("variable should be in lower case"); //$NON-NLS-1$
	}

	/**
	 * Returns if the given message concern the no space error.
	 *
	 * @param message
	 *            the message to be tested.
	 * @return <code>true</code> if no space error.
	 */
	private boolean isNoSpace(final String message) {
		return message.contains("0 space"); //$NON-NLS-1$
	}

	/**
	 * Returns if the given message concern the one space error.
	 *
	 * @param message
	 *            the message to be tested.
	 * @return <code>true</code> if one space error.
	 */
	private boolean isOneSpace(final String message) {
		return message.contains("1 space"); //$NON-NLS-1$
	}

	/**
	 * Returns if the given message concern the unused macro.
	 *
	 * @param message
	 *            the message to be tested.
	 * @return <code>true</code> if unused macro error.
	 */
	private boolean isUnunsedMacro(final String message) {
		return message.contains("Unused macro"); //$NON-NLS-1$
	}

	/**
	 * Returns if the given message concern the unused variable.
	 *
	 * @param message
	 *            the message to be tested.
	 * @return <code>true</code> if unused variable error.
	 */
	private boolean isUnunsedVariable(final String message) {
		return message.contains("Unused variable"); //$NON-NLS-1$
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
	 * @throws IOException
	 *             if the data output is not a valid representation of a
	 *             {@link TwigResult} type.
	 */
	private TwigFile parseResult(final String data) throws IOException {
		final TwigResultParser parser = getParser();
		final TwigResult result = parser.parse(data);
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
			if (!output.isEmpty()) {
				// convert
				final TwigFile result = parseResult(output);
				if (result != null && !result.isEmpty()) {
					// add violations
					final ResourceText text = new ResourceText(file);
					for (final TwigViolation violation : result) {
						addMarker(file, text, violation);
					}
				}

			} else if (exitCode != 0) { // error?
				IOException e = executor.getErrorException();
				final String error = executor.getError();
				if (!error.isEmpty()) {
					e = new IOException(error, e);
				}
				final String msg = NLS.bind(
						Messages.ValidationVisitor_Error_Validate_Code,
						file.getName(), exitCode);
				handleStatus(createErrorStatus(msg, e));
			}
		} catch (final IOException e) {
			final String msg = NLS.bind(
					Messages.ValidationVisitor_Error_Validate_Name,
					file.getName());
			handleStatus(createErrorStatus(msg, e));
		}
	}
}
