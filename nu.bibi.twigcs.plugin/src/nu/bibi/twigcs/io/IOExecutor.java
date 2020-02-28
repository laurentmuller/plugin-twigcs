/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.io;

import java.io.IOException;
import java.util.List;

import nu.bibi.twigcs.internal.Messages;

/**
 * Class to execute a {@link Process}.
 * <p>
 * When the {@link #run(List)} method is invoked, an output string and error
 * string is available.
 * </p>
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class IOExecutor {

	/*
	 * the output message
	 */
	private String output;

	/*
	 * the output exception
	 */
	private IOException outputException;

	/*
	 * the error message
	 */
	private String error;

	/*
	 * the error exception
	 */
	private IOException errorException;

	/*
	 * the exit code
	 */
	private int exitCode;

	/**
	 * Gets the error contents.
	 *
	 * @return the error contents, if any; an empty string ("") otherwise.
	 */
	public String getError() {
		return error != null ? error : ""; //$NON-NLS-1$
	}

	/**
	 * Gets the error exception.
	 *
	 * @return the exception, if any; <code>null</code> otherwise.
	 */
	public IOException getErrorException() {
		return errorException;
	}

	/**
	 * Gets the exit code. By convention, the value 0 indicates normal
	 * termination.
	 *
	 * @return the exit code.
	 */
	public int getExitCode() {
		return exitCode;
	}

	/**
	 * Gets the output contents.
	 *
	 * @return the output contents, if any; an empty string ("") otherwise.
	 */
	public String getOutput() {
		return output != null ? output : ""; //$NON-NLS-1$
	}

	/**
	 * Gets the output exception.
	 *
	 * @return the exception, if any; <code>null</code> otherwise.
	 */
	public IOException getOutputException() {
		return outputException;
	}

	/**
	 * Runs the given command.
	 *
	 * @param command
	 *            the list containing the program and its arguments.
	 * @return the exit code. By convention, the value 0 indicates normal
	 *         termination.
	 * @throws NullPointerException
	 *             if the argument is <code>null</code>.
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	public int run(final List<String> command) throws IOException {
		// clear
		exitCode = 0;
		output = error = ""; //$NON-NLS-1$ ;
		outputException = errorException = null;

		// start
		final ProcessBuilder builder = new ProcessBuilder(command);
		final Process process = builder.start();

		// handle output message
		final IOStream outputStream = new IOStream(process.getInputStream());
		final Thread outputThread = new Thread(outputStream);

		// handle error message
		final IOStream errorStream = new IOStream(process.getErrorStream());
		final Thread errorThread = new Thread(errorStream);

		// start
		outputThread.start();
		errorThread.start();

		try {
			// wait
			exitCode = process.waitFor();

			// handle condition where the process ends before the threads finish
			outputThread.join();
			errorThread.join();

			// save
			output = outputStream.toString();
			outputException = outputStream.getException();
			error = errorStream.toString();
			errorException = errorStream.getException();

			return exitCode;

		} catch (final InterruptedException e) {
			throw new IOException(Messages.IOExecutor_Error_Interrupted, e);
		}
	}
}
