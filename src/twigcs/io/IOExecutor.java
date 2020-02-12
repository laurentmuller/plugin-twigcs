package twigcs.io;

import java.io.IOException;

/**
 * Class to execute a process.
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
	 * the output exception
	 */
	private IOException errorException;

	/*
	 * the exit code
	 */
	private int exitCode;

	/**
	 * Gets the error message.
	 *
	 * @return the error message.
	 */
	public String getError() {
		return error;
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
	 * Gets the output message.
	 *
	 * @return the output message.
	 */
	public String getOutput() {
		return output;
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
	 *            a string array containing the program and its arguments.
	 * @return the exit code. By convention, the value 0 indicates normal
	 *         termination.
	 * @throws IOException
	 *             if I/O exception occurs.
	 * @throws InterruptedException
	 *             if any thread has interrupted the current thread.
	 */
	public int run(final String... command)
			throws IOException, InterruptedException {

		// clear
		output = error = null;
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

		// wait
		exitCode = process.waitFor();

		// Handle condition where the process ends before the threads finish
		outputThread.join();
		errorThread.join();

		// save
		output = outputStream.toString();
		outputException = outputStream.getException();
		error = errorStream.toString();
		errorException = errorStream.getException();

		return exitCode;
	}
}
