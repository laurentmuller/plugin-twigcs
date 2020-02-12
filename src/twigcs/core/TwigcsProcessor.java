package twigcs.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import twigcs.TwigcsPlugin;
import twigcs.model.TwigReporter;
import twigcs.model.TwigSeverity;
import twigcs.model.TwigVersion;
import twigcs.preferences.TwigcsPreferencesInitializer;

/**
 * This class contains properties used to build command arguments for Twigcs.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsProcessor implements IConstants {

	/**
	 * Creates an instance of processor from the preference store.
	 *
	 * @return the processor,.
	 */
	public static TwigcsProcessor instance() {
		final TwigcsProcessor processor = new TwigcsProcessor();
		processor.setExec(TwigcsPreferencesInitializer.getTwigExecutable());
		processor.setSeverity(TwigcsPreferencesInitializer.getTwigSeverity());
		processor.setReporter(TwigcsPreferencesInitializer.getTwigReporter());
		processor.setTwigVersion(TwigcsPreferencesInitializer.getTwigVersion());

		return processor;
	}

	/*
	 * the Twigcs executable
	 */
	private String exec;

	/*
	 * the twig version
	 */
	private TwigVersion twigVersion = TwigVersion.V3;

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
	public String[] buildCommand() throws CoreException {
		// check values
		if (exec == null || exec.isEmpty()) {
			throw TwigcsPlugin.createCoreException( //
					"The Twigcs executable is not defined.");
		}
		if (searchPaths.isEmpty()) {
			throw TwigcsPlugin.createCoreException( //
					"The search paths are empty.");
		}

		// build
		final List<String> command = new ArrayList<>();

		// executable
		command.add(exec);

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

		// convert
		return command.toArray(new String[command.size()]);
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
	 * Gets the Twigcs executable.
	 *
	 * @return the Twigcs executable.
	 */
	public String getExec() {
		return exec;
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
	 * Sets the Twigcs executable.
	 *
	 * @param exec
	 *            the Twigcs executable to set.
	 */
	public void setExec(final String exec) {
		this.exec = exec;
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
}
