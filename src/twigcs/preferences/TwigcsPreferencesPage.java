package twigcs.preferences;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
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
		// executable
		final FileFieldEditor execEditor = new FileFieldEditor(
				P_EXECUTABLE_PATH, "Twigcs Path", //
				true, getFieldEditorParent());
		execEditor.setErrorMessage("The executable path must be defined.");
		execEditor.setEmptyStringAllowed(false);
		addField(execEditor);

		// twig version
		addField(new ComboFieldEditor(P_TWIG_VERSION, "Twig-version",
				getEnumNames(TwigVersion.class), getFieldEditorParent()));

		// severity
		addField(new ComboFieldEditor(P_SEVERITY, "Severity",
				getEnumNames(TwigSeverity.class), getFieldEditorParent()));

		// reporter
		addField(new ComboFieldEditor(P_REPORTER, "Reporter",
				getEnumNames(TwigReporter.class), getFieldEditorParent()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(final IWorkbench workbench) {
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
			result[i][0] = result[i][1] = values[i].name();
		}
		return result;
	}
}