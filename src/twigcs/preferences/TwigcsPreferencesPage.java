package twigcs.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import twigcs.TwigcsPlugin;
import twigcs.model.TwigReporter;
import twigcs.model.TwigSeverity;
import twigcs.model.TwigVersion;

/**
 * Twigcs preferences page.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsPreferencesPage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage, TwigcsPreferencesConstants {

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsPreferencesPage() {
		super(GRID);
		setPreferenceStore(TwigcsPlugin.getDefault().getPreferenceStore());
		setDescription("Sets the default values to use for running Twigcs.");
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
	public void init(final IWorkbench workbench) {
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
	private void addEnumEditor(String key, String labelText,
			Class<? extends Enum<?>> clazz) {
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
			protected Text createTextWidget(Composite parent) {
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
	private String[][] getEnumNames(Class<? extends Enum<?>> clazz) {
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
	private String toProperCase(String text) {
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}
}