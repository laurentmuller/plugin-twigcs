/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.gson;

import java.lang.reflect.Type;

import org.eclipse.osgi.util.NLS;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.model.TwigSeverity;

/**
 * Class to deserialize a {@link TwigSeverity}.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public final class SeverityDeserializer
		implements JsonDeserializer<TwigSeverity> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TwigSeverity deserialize(final JsonElement json, final Type typeOfT,
			final JsonDeserializationContext context)
			throws JsonParseException {

		// parse
		final int value = json.getAsInt();
		final TwigSeverity severity = TwigSeverity.valueOf(value);
		if (severity != null) {
			return severity;
		}

		throw new JsonParseException(
				NLS.bind(Messages.SeverityDeserializer_Error, value));
	}
}
