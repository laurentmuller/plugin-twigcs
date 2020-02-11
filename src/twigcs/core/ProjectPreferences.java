package twigcs.core;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.service.prefs.BackingStoreException;

/**
 * Project preferences helper.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ProjectPreferences implements IConstants {

	/*
	 * the key to get/set include paths
	 */
	private static final String KEY_INCLUDE = "includePaths"; //$NON-NLS-1$

	/*
	 * the key to get/set exclude paths
	 */
	private static final String KEY_EXCLUDE = "excludePaths"; //$NON-NLS-1$

	/*
	 * the paths separators
	 */
	private static final String SEPARATOR = "|"; //$NON-NLS-1$

	/*
	 * the project's preferences
	 */
	private final IEclipsePreferences preferences;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param project
	 *            the project to get or set preferences.
	 */
	public ProjectPreferences(IProject project) {
		preferences = new ProjectScope(project).getNode(PLUGIN_ID);
	}

	/**
	 * Forces any changes in the contents of this preferences to the persistent
	 * store.
	 *
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 */
	public void flush() throws BackingStoreException {
		preferences.flush();
	}

	/**
	 * Gets the exclude directories and files.
	 *
	 * @return the exclude paths.
	 */
	public String[] getExcludePaths() {
		return getList(KEY_EXCLUDE);
	}

	/**
	 * Gets the include directories and files.
	 *
	 * @return the include paths.
	 */
	public String[] getIncludePaths() {
		return getList(KEY_INCLUDE);
	}

	/**
	 * Get the project preferences.
	 *
	 * @return the preferences.
	 */
	public IEclipsePreferences getPreferences() {
		return preferences;
	}

	/**
	 * Sets the exclude directories and files.
	 *
	 * @param paths
	 *            the paths to exclude.
	 */
	public void setExcludePaths(String[] paths) {
		putList(KEY_EXCLUDE, paths);
	}

	/**
	 * Sets the include directories and files.
	 *
	 * @param paths
	 *            the paths to include.
	 */
	public void setIncludePaths(String[] paths) {
		putList(KEY_INCLUDE, paths);
	}

	/**
	 * Gets the paths.
	 *
	 * @param key
	 *            the key whose associated paths is to be returned.
	 * @return an array, maybe empty, of paths.
	 */
	private String[] getList(String key) {
		final String value = preferences.get(key, ""); //$NON-NLS-1$
		final String[] values = value.split(SEPARATOR);
		Arrays.sort(values, String.CASE_INSENSITIVE_ORDER);

		return values;
	}

	/**
	 * Saves the paths. The <code>null</code> or empty paths are not saved.
	 *
	 * @param key
	 *            the key with which the specified paths is to be associated.
	 * @param paths
	 *            the paths to be associated with the specified key.
	 */
	private void putList(String key, String[] paths) {
		final StringBuffer buffer = new StringBuffer();
		for (final String path : paths) {
			if (path != null && !path.isEmpty()) {
				if (buffer.length() > 0) {
					buffer.append(SEPARATOR);
				}
				buffer.append(path);
			}
		}

		preferences.put(key, buffer.toString());
	}
}
