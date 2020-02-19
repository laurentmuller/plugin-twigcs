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
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.model.TwigReporter;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;
import nu.bibi.twigcs.preferences.PreferencesInitializer;

/**
 * This class contains properties used to build command arguments for Twigcs.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsProcessor implements ICoreException {

	/**
	 * Creates an instance of processor from the preference store.
	 *
	 * @return the processor,.
	 */
	public static TwigcsProcessor instance() {
		final TwigcsProcessor processor = new TwigcsProcessor();
		processor.setProgramPath(PreferencesInitializer.getExecutable());
		processor.setSeverity(PreferencesInitializer.getSeverity());
		processor.setReporter(PreferencesInitializer.getReporter());
		processor.setTwigVersion(PreferencesInitializer.getTwigVersion());

		return processor;
	}

	/*
	 * the Twigcs executable path
	 */
	private String programPath;

	/*
	 * the twig version
	 */
	private TwigVersion twigVersion = TwigVersion.VERSION_3;

	/*
	 * the minimum severity level
	 */
	private TwigSeverity severity = TwigSeverity.warning;

	/*
	 * the output reporter
	 */
	private TwigReporter reporter = TwigReporter.json;

	/*
	 * the search paths
	 */
	private final List<String> searchPaths = new ArrayList<>();

	/*
	 * the exclude paths
	 */
	private final List<String> excludePaths = new ArrayList<>();

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsProcessor() {
	}

	/**
	 * Adds an exclude path.
	 *
	 * @param path
	 *            the path to add.
	 */
	public void addExcludePath(final String path) {
		if (path != null && !path.isEmpty() && !excludePaths.contains(path)) {
			excludePaths.add(path);
		}
	}

	/**
	 * Adds a path to search in.
	 *
	 * @param path
	 *            the path to add.
	 */
	public void addSearchPath(final String path) {
		if (path != null && !path.isEmpty() && !searchPaths.contains(path)) {
			searchPaths.add(path);
		}
	}

	/**
	 * Adds paths to search in.
	 *
	 * @param paths
	 *            the paths to add.
	 */
	public void addSearchPaths(final String... paths) {
		for (final String path : paths) {
			addSearchPath(path);
		}
	}

	/**
	 * Builds the execution command.
	 *
	 * @return a string array containing the Twigcs program and its arguments.
	 * @throws CoreException
	 *             if some parameters are missing or invalid.
	 */
	public List<String> buildCommand() throws CoreException {
		// check status
		final IStatus status = validate();
		if (!status.isOK()) {
			throw new CoreException(status);
		}

		// build
		final List<String> command = new ArrayList<>();

		// executable
		command.add(toRealPath());

		// search paths
		command.addAll(searchPaths);

		// exclude paths
		if (!excludePaths.isEmpty()) {
			command.add("--exclude"); //$NON-NLS-1$
			command.addAll(excludePaths);
		}

		// reporter
		command.add("-r"); //$NON-NLS-1$
		command.add(reporter.name());

		// twig version
		command.add("-t"); //$NON-NLS-1$
		command.add(twigVersion.version());

		// severity
		command.add("-s"); //$NON-NLS-1$
		command.add(severity.name());

		return command;
	}

	/**
	 * Clear search paths.
	 */
	public void clearPaths() {
		searchPaths.clear();
	}

	/**
	 * Gets the exclude paths.
	 *
	 * @return the exclude paths.
	 */
	public List<String> getExcludePaths() {
		return excludePaths;
	}

	/**
	 * Gets the Twigcs executable path.
	 *
	 * @return the Twigcs executable path.
	 */
	public String getProgramPath() {
		return programPath;
	}

	/**
	 * Gets the reporter to use for output results.
	 *
	 * @return the reporter.
	 */
	public TwigReporter getReporter() {
		return reporter;
	}

	/**
	 * Gets the search paths.
	 *
	 * @return the search paths.
	 */
	public List<String> getSearchPaths() {
		return searchPaths;
	}

	/**
	 * Gets the minimum severity level.
	 *
	 * @return the minimum severity.
	 */
	public TwigSeverity getSeverity() {
		return severity;
	}

	/**
	 * Gets the twig version to use.
	 *
	 * @return the twig version.
	 */
	public TwigVersion getTwigVersion() {
		return twigVersion;
	}

	/**
	 * Sets the Twigcs executable path.
	 *
	 * @param programPath
	 *            the Twigcs executable path to set.
	 */
	public void setProgramPath(final String programPath) {
		this.programPath = programPath;
	}

	/**
	 * Sets the reporter to use for output results.
	 *
	 * @param reporter
	 *            the reporter to use
	 */
	public void setReporter(final TwigReporter reporter) {
		this.reporter = reporter;
	}

	/**
	 * Sets the search paths to a unique given path.
	 *
	 * @param path
	 *            the search path to set.
	 */
	public void setSearchPath(final String path) {
		searchPaths.clear();
		addSearchPath(path);
	}

	/**
	 * Sets the minimum severity level.
	 *
	 * @param severity
	 *            the minimum severity.
	 */
	public void setSeverity(final TwigSeverity severity) {
		this.severity = severity;
	}

	/**
	 * Set the twig version to use.
	 *
	 * @param twigVersion
	 *            the twig version.
	 */
	public void setTwigVersion(final TwigVersion twigVersion) {
		this.twigVersion = twigVersion;
	}

	/**
	 * Check validity of this properties.
	 *
	 * @return An <code>ERROR</code> status if not valid; an <code>OK</code>
	 *         status otherwise.
	 */
	public IStatus validate() {
		// executable?
		if (programPath == null || programPath.isEmpty()) {
			return createErrorStatus(Messages.TwigcsProcessor_Error_No_Path,
					null);
		}

		// valid?
		final Path path = Paths.get(programPath);
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS)
				|| !Files.isRegularFile(path, LinkOption.NOFOLLOW_LINKS)) {
			return createErrorStatus(Messages.TwigcsProcessor_Error_Not_Exist,
					null);
		}

		// search paths?
		if (searchPaths.isEmpty()) {
			return createErrorStatus(Messages.TwigcsProcessor_Error_Paths_Empty,
					null);
		}
		return Status.OK_STATUS;
	}

	/**
	 * Gets the real path of Twigcs executable.
	 *
	 * @return the real path.
	 * @throws IOException
	 *             if the file does not exist or an I/O error occurs.
	 */
	private String toRealPath() throws CoreException {
		try {
			return Paths.get(programPath).toRealPath().toString();
		} catch (final IOException e) {
			throw createCoreException(Messages.TwigcsProcessor_Error_Real_Path,
					e);
		}
	}
}
