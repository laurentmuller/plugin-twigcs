/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import twigcs.TwigcsPlugin;
import twigcs.core.IConstants;
import twigcs.core.TwigcsBuilder;
import twigcs.model.TwigReporter;
import twigcs.model.TwigSeverity;
import twigcs.model.TwigVersion;

/**
 * Twigcs preferences page.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsPreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage, TwigcsPreferencesConstants, IConstants {

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsPreferencesPage() {
		super(GRID);
		setDescription("Sets the default values to use for running Twigcs.");
		setPreferenceStore(TwigcsPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createFieldEditors() {
		addFileEditor();
		addEnumEditor(P_TWIG_VERSION, "&Twig-version", TwigVersion.class);
		addEnumEditor(P_SEVERITY, "&Severity", TwigSeverity.class);
		addEnumEditor(P_REPORTER, "&Reporter", TwigReporter.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ScopedPreferenceStore getPreferenceStore() {
		return (ScopedPreferenceStore) super.getPreferenceStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		// default
		final boolean result = super.performOk();

		// changes?
		final ScopedPreferenceStore store = getPreferenceStore();
		if (store.needsSaving()) {
			try {
				// save
				store.save();

				// rebuild
				TwigcsBuilder.triggerFullBuild();

			} catch (final IOException e) {
				TwigcsPlugin.handleError(
						createErrorStatus("Failed to save preferences", e));
				return false;
			} catch (final CoreException e) {
				TwigcsPlugin.handleError(e.getStatus());
				return false;
			}
		}
		return result;
	}

	/**
	 * Adds a combo field editor for the given enumeration class
	 *
	 * @param key
	 *            the name of the preference this field editor works on.
	 * @param label
	 *            the label text of the field editor.
	 * @param clazz
	 *            the enumeration class.
	 */
	private void addEnumEditor(final String key, final String labelText,
			final Class<? extends Enum<?>> clazz) {
		addField(new ComboFieldEditor(key, labelText, getEnumNames(clazz),
				getFieldEditorParent()));
	}

	/**
	 * Adds the file field editor for the executable path.
	 */
	private void addFileEditor() {
		final FileFieldEditor editor = new FileFieldEditor(P_EXECUTABLE_PATH,
				"Twigcs &Path", true, getFieldEditorParent()) {
			@Override
			protected Text createTextWidget(final Composite parent) {
				final Text text = super.createTextWidget(parent);
				text.addListener(SWT.FocusIn, e -> {
					((Text) e.widget).selectAll();
				});
				return text;
			}
		};
		editor.setErrorMessage("The executable path must be defined.");
		editor.setEmptyStringAllowed(false);
		addField(editor);
	}

	/**
	 * Gets the enumeration names to populate a combo field editor.
	 *
	 * @param clazz
	 *            the enumeration class.
	 * @return an array with the enumeration names.
	 */
	private String[][] getEnumNames(final Class<? extends Enum<?>> clazz) {
		final Enum<?>[] values = clazz.getEnumConstants();
		final String[][] result = new String[values.length][2];
		for (int i = 0; i < values.length; i++) {
			result[i][0] = toProperCase(values[i].name());
			result[i][1] = values[i].name();
		}
		return result;
	}

	/**
	 * Convert the given string to proper case.
	 *
	 * @param text
	 *            the string to convert.
	 * @return the converted string.
	 */
	private String toProperCase(final String text) {
		return Character.toUpperCase(text.charAt(0))
				+ text.substring(1).replace('_', ' ').toLowerCase();
	}

	/**
	 * Trigger a clean build for all accessible projects.
	 *
	 * @throws CoreException
	 *             if the build fails.
	 */
	void triggerCleanBuild() throws CoreException {
		TwigcsBuilder.triggerCleanBuild();
	}
}