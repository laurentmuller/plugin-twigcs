/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IPath;
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
	private static final String SEPARATOR = ":"; //$NON-NLS-1$

	/*
	 * the project's preferences
	 */
	private final IEclipsePreferences preferences;

	/*
	 * the handled project
	 */
	private final IProject project;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param project
	 *            the project to get or set preferences.
	 */
	public ProjectPreferences(final IProject project) {
		preferences = new ProjectScope(project).getNode(PLUGIN_ID);
		this.project = project;
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
	 * Gets the exclude resources.
	 *
	 * @return the exclude resources.
	 */
	public List<IResource> getExcludeResources() {
		return getResources(KEY_EXCLUDE);
	}

	/**
	 * Gets the include resources.
	 *
	 * @return the include resources.
	 */
	public List<IResource> getIncludeResources() {
		return getResources(KEY_INCLUDE);
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
	 * Sets the exclude resources.
	 *
	 * @param resources
	 *            the resources to exclude.
	 */
	public void setExcludeResources(final List<IResource> resources) {
		putResources(KEY_EXCLUDE, resources);
	}

	/**
	 * Sets the include resources.
	 *
	 * @param resources
	 *            the resources to include.
	 */
	public void setIncludeResources(final List<IResource> resources) {
		putResources(KEY_INCLUDE, resources);
	}

	/**
	 * Gets the resources.
	 *
	 * @param key
	 *            the key whose associated resources is to be returned.
	 * @return a list, maybe empty, of resources.
	 */
	private List<IResource> getResources(final String key) {
		final String value = preferences.get(key, ""); //$NON-NLS-1$
		if (value.isEmpty()) {
			return new ArrayList<>();
		}

		// split
		final String[] paths = value.split(SEPARATOR);

		// build
		return Arrays.stream(paths).map(path -> project.findMember(path))
				.filter(Objects::nonNull).filter(IResource::exists)
				.collect(Collectors.toList());
	}

	/**
	 * Saves the resources. The <code>null</code> resources not saved.
	 *
	 * @param key
	 *            the key with which the specified resources is to be
	 *            associated.
	 * @param resources
	 *            the resources to be associated with the specified key.
	 */
	private void putResources(final String key,
			final List<IResource> resources) {
		// build
		final String value = resources.stream().filter(Objects::nonNull)
				.map(IResource::getProjectRelativePath)
				.map(IPath::toPortableString)
				.collect(Collectors.joining(SEPARATOR));

		if (value.isEmpty()) {
			preferences.remove(key);
		} else {
			preferences.put(key, value);
		}
	}
}
