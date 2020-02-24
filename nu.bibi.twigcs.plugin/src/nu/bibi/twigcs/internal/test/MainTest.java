package nu.bibi.twigcs.internal.test;

import java.io.IOException;
import java.util.List;

import org.eclipse.core.runtime.CoreException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import nu.bibi.twigcs.core.TwigcsProcessor;
import nu.bibi.twigcs.gson.SeverityDeserializer;
import nu.bibi.twigcs.io.IOExecutor;
import nu.bibi.twigcs.model.TwigDisplay;
import nu.bibi.twigcs.model.TwigFile;
import nu.bibi.twigcs.model.TwigResult;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;
import nu.bibi.twigcs.model.TwigViolation;

public class MainTest {

	static final String EXEC_PATH = "C:/Users/bibi/AppData/Roaming/Composer/vendor/bin/twigcs.bat"; //$NON-NLS-1$

	public static void main(final String[] args) {
		int exitCode = 1;
		try {
			final TwigcsProcessor processor = getProcessor();
			// processor.addSearchPath("D:/GitHub/calculation/templates/user");
			processor.addSearchPath(
					"D:/GitHub/calculation/templates/user/user_rights.html.twig"); //$NON-NLS-1$
			// processor.addSearchPath(
			// "D:/GitHub/calculation/templates/user/user_theme.html.twig");
			// processor.addSearchPath(
			// "D:/GitHub/calculation/templates/user/user_card.html.twig");
			// processor.addExcludePath("D:/GitHub/calculation/templates/user");
			processor.setTwigDisplay(TwigDisplay.blocking);
			processor.setTwigSeverity(TwigSeverity.error);

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
			if (error != null && !error.isEmpty()) {
				System.err.println(error);
			}

			// check output
			final String output = exec.getOutput();
			if (output != null && !output.isEmpty()) {
				final Gson gson = getGson();
				final TwigResult result = gson.fromJson(output,
						TwigResult.class);
				result.sort();

				System.out.println(result);
				for (final TwigFile file : result) {
					System.out.println(" " + file); //$NON-NLS-1$
					for (final TwigViolation violation : file) {
						System.out.println(" " + violation); //$NON-NLS-1$
					}
				}
			}

		} catch (final CoreException | IOException | JsonSyntaxException e) {
			e.printStackTrace();
		} finally {
			System.out.println("Exit Code: " + exitCode); //$NON-NLS-1$
			System.exit(exitCode);
		}
	}

	static Gson getGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(TwigSeverity.class,
				new SeverityDeserializer());
		return gsonBuilder.create();
	}

	static TwigcsProcessor getProcessor() {
		final TwigcsProcessor processor = new TwigcsProcessor();
		processor.setProgramPath(EXEC_PATH);
		processor.setTwigSeverity(TwigSeverity.error);
		processor.setTwigVersion(TwigVersion.VERSION_2);
		return processor;
	}
}
