/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.internal.test;

import java.io.IOException;
import java.util.List;

import nu.bibi.twigcs.core.TwigcsProcessor;
import nu.bibi.twigcs.core.TwigcsResultParser;
import nu.bibi.twigcs.io.IOExecutor;
import nu.bibi.twigcs.model.TwigDisplay;
import nu.bibi.twigcs.model.TwigFile;
import nu.bibi.twigcs.model.TwigResult;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;
import nu.bibi.twigcs.model.TwigViolation;

public class MainTest {

	private static final String EXEC_PATH = "D:/GitHub/twigcs/bin/twigcs.bat"; //$NON-NLS-1$
	private static final String TWIG_FILE = "D:/GitHub/calculation/templates/about/about.html.twig"; //$NON-NLS-1$

	public static void main(final String[] args) {
		int exitCode = 0;
		try {
			final TwigcsProcessor processor = getProcessor();
			processor.addSearchPath(TWIG_FILE);

			final List<String> command = processor.buildCommand();
			final IOExecutor exec = new IOExecutor();
			exitCode = exec.run(command);

			// check exceptions
			final IOException errorException = exec.getErrorException();
			if (errorException != null) {
				errorException.printStackTrace();
			}
			final IOException outputException = exec.getOutputException();
			if (outputException != null) {
				outputException.printStackTrace();
			}

			// check error
			final String error = exec.getError();
			if (!error.isEmpty()) {
				System.err.println(error);
			}

			// check output
			final String output = exec.getOutput();
			if (!output.isEmpty()) {
				final TwigcsResultParser parser = new TwigcsResultParser();
				final TwigResult result = parser.parse(output);

				System.out.println(result);
				for (final TwigFile file : result) {
					System.out.println("  " + file); //$NON-NLS-1$
					for (final TwigViolation violation : file) {
						System.out.println("    " + violation); //$NON-NLS-1$
					}
				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Exit Code: " + exitCode); //$NON-NLS-1$
		}
	}

	private static TwigcsProcessor getProcessor() {
		final TwigcsProcessor processor = new TwigcsProcessor();
		processor.setProgramPath(EXEC_PATH);
		processor.setTwigSeverity(TwigSeverity.WARNING);
		processor.setTwigVersion(TwigVersion.VERSION_2);
		processor.setTwigDisplay(TwigDisplay.ALL);
		return processor;
	}
}
