/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.preferences;

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

import nu.bibi.twigcs.TwigcsPlugin;
import nu.bibi.twigcs.core.IConstants;
import nu.bibi.twigcs.core.TwigcsBuilder;
import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.model.TwigReporter;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;

/**
 * Twigcs preferences page.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class PreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage, PreferencesConstants, IConstants {

	/**
	 * Creates a new instance of this class.
	 */
	public PreferencesPage() {
		super(GRID);
		setDescription(Messages.PreferencesPage_Description);
		setPreferenceStore(TwigcsPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createFieldEditors() {
		addFileEditor();
		addEnumEditor(P_TWIG_VERSION, Messages.PreferencesPage_Twig_Version,
				TwigVersion.class);
		addEnumEditor(P_SEVERITY, Messages.PreferencesPage_Severity,
				TwigSeverity.class);
		final ComboFieldEditor editor = addEnumEditor(P_REPORTER,
				Messages.PreferencesPage_Reporter, TwigReporter.class);
		editor.setEnabled(false, getFieldEditorParent());
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
		if (result && store.needsSaving()) {
			try {
				// save
				store.save();

				// clean
				TwigcsBuilder.triggerCleanBuild();

			} catch (final IOException e) {
				handleError(createErrorStatus(
						Messages.PreferencesPage_Error_Save, e));
				return false;
			} catch (final CoreException e) {
				handleError(e.getStatus());
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
	 * @return the field editor.
	 */
	private ComboFieldEditor addEnumEditor(final String key,
			final String labelText, final Class<? extends Enum<?>> clazz) {
		final ComboFieldEditor editor = new ComboFieldEditor(key, labelText,
				getEnumNames(clazz), getFieldEditorParent());
		addField(editor);
		return editor;
	}

	/**
	 * Adds the file field editor for the executable path.
	 */
	private void addFileEditor() {
		final FileFieldEditor editor = new FileFieldEditor(P_EXECUTABLE_PATH,
				Messages.PreferencesPage_Path, true, getFieldEditorParent()) {
			@Override
			protected Text createTextWidget(final Composite parent) {
				final Text text = super.createTextWidget(parent);
				text.addListener(SWT.FocusIn, e -> {
					((Text) e.widget).selectAll();
				});
				return text;
			}
		};
		editor.setErrorMessage(Messages.PreferencesPage_Error_Path);
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
}