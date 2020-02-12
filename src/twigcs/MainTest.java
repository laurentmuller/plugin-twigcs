package twigcs;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import twigcs.core.TwigcsProcessor;
import twigcs.gson.SeverityDeserializer;
import twigcs.io.IOExecutor;
import twigcs.model.TwigFile;
import twigcs.model.TwigResult;
import twigcs.model.TwigSeverity;
import twigcs.model.TwigVersion;
import twigcs.model.TwigViolation;

public class MainTest {

	public static void main(final String[] args) {
		int exitCode = 1;
		try {
			final TwigcsProcessor processor = new TwigcsProcessor();
			processor.setExec(
					"C:\\Users\\muller\\AppData\\Roaming\\Composer\\vendor\\bin\\twigcs.bat");
			processor.setTwigVersion(TwigVersion.V2);
			processor.setSeverity(TwigSeverity.ignore);
			// processor.addSearchPath("D:/GitHub/calculation/templates/user");
			processor.addSearchPath(
					"D:/GitHub/calculation/templates/user/user_rights.html.twig");
			// processor.addSearchPath(
			// "D:/GitHub/calculation/templates/user/user_theme.html.twig");
			// processor.addSearchPath(
			// "D:/GitHub/calculation/templates/user/user_card.html.twig");

			// processor.addExcludePath("D:/GitHub/calculation/templates/user");

			final String[] command = processor.buildCommand();

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
					System.out.println(" " + file);
					for (final TwigViolation violation : file) {
						System.out.println(" " + violation);
					}
				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Exit Code: " + exitCode);
			System.exit(exitCode);
		}
	}

	static Gson getGson() {
		final GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(TwigSeverity.class,
				new SeverityDeserializer());
		return gsonBuilder.create();
	}

	static String readStream(final InputStream stream) throws IOException {
		int len;
		final byte[] bytes = new byte[8192];
		final ByteArrayOutputStream output = new ByteArrayOutputStream(8192);
		final BufferedInputStream buffer = new BufferedInputStream(stream);
		while ((len = buffer.read(bytes)) != -1) {
			output.write(bytes, 0, len);
		}
		return output.toString();
	}

}
