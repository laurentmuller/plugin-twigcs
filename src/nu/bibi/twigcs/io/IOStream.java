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

	/*
	 * the input stream
	 */
	private final InputStream input;

	/*
	 * the output stream
	 */
	private final ByteArrayOutputStream output;

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
		output = new ByteArrayOutputStream(8192);
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
			int c;
			while ((c = input.read()) != -1) {
				output.write(c);
			}
		} catch (final IOException e) {
			exception = e;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation returns the content read from the input stream.
	 * </p>
	 */
	@Override
	public String toString() {
		return output.toString();
	}
}