/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Input/Output stream.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class IOStream implements Runnable {

	/**
	 * The default buffer size.
	 */
	public static final int BUFFER_SIZE = 8192;

	/**
	 * Gets all the contents of the given input stream.
	 *
	 * @param input
	 *            the input stream to read from.
	 * @return the input stream contents.
	 * @throws IOException
	 *             if an I/O exception occurs.
	 */
	public static byte[] readAll(final InputStream input) throws IOException {
		int len;
		final byte[] buffer = new byte[BUFFER_SIZE];
		final ByteArrayOutputStream output = new ByteArrayOutputStream(
				BUFFER_SIZE);
		while ((len = input.read(buffer)) != -1) {
			output.write(buffer, 0, len);
		}

		return output.toByteArray();
	}

	/*
	 * the input stream
	 */
	private final InputStream input;

	/*
	 * the output contents
	 */
	private byte[] output;

	/*
	 * the I/O exception
	 */
	private IOException exception;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param input
	 *            the input stream to read from.
	 */
	public IOStream(final InputStream input) {
		this.input = input;
	}

	/**
	 * Gets the read/write exception.
	 *
	 * @return the exception, if any; <code>null</code> otherwise.
	 */
	public IOException getException() {
		return exception;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		try {
			output = readAll(input);
		} catch (final IOException e) {
			exception = e;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation of <code>IOStream</code> returns the contents (if any)
	 * read from the input stream. Returns an empty string ("") if not yet
	 * started.
	 * </p>
	 */
	@Override
	public String toString() {
		return output != null ? new String(output) : "";
	}
}