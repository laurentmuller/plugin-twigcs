package twigcs.model;

/**
 * Twig versions.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public enum TwigVersion {

	V1("1"), //
	V2("2"), //
	V3("3");

	private final String version;

	private TwigVersion(final String version) {
		this.version = version;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version.
	 */
	public String version() {
		return version;
	}

}
