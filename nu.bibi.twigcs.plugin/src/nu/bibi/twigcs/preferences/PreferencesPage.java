/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.preferences;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import nu.bibi.twigcs.TwigcsPlugin;
import nu.bibi.twigcs.core.ICoreException;
import nu.bibi.twigcs.core.TwigcsBuilder;
import nu.bibi.twigcs.core.TwigcsProcessor;
import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.io.IOExecutor;
import nu.bibi.twigcs.model.TwigDisplay;
import nu.bibi.twigcs.model.TwigReporter;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;

/**
 * Twigcs preferences page.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class PreferencesPage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage, PreferencesConstants, ICoreException {

	/**
	 * Gets the enumeration names to populate a combo field editor.
	 *
	 * @param clazz
	 *            the enumeration class.
	 * @return an array with the enumeration names.
	 */
	public static String[][] getEnumNames(
			final Class<? extends Enum<?>> clazz) {
		final Enum<?>[] values = clazz.getEnumConstants();
		return Arrays.stream(values).map(e -> {
			return new String[] { toProperCase(e.name()), e.name() };
		}).toArray(String[][]::new);
	}

	/**
	 * Replaces all underscore (<code>'_'</code>) characters by a space
	 * (<code>' '</code>) character and converts to proper case. For example:
	 *
	 * <pre>
	 * "TWIG_VERSION_1" -> "Twig version 1"
	 * </pre>
	 *
	 * @param text
	 *            the string to convert.
	 * @return the converted string.
	 */
	public static String toProperCase(final String text) {
		return Character.toUpperCase(text.charAt(0))
				+ text.substring(1).replace('_', ' ').toLowerCase();
	}

	/*
	 * the test button
	 */
	private Button btnTest;

	/*
	 * the Twigcs file editor
	 */
	private FileFieldEditor fileEditor;

	/*
	 * the template file name
	 */
	private String templateFile;

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
		addEnumEditor(P_VERSION, Messages.PreferencesPage_Version,
				TwigVersion.class);
		addEnumEditor(P_SEVERITY, Messages.PreferencesPage_Severity,
				TwigSeverity.class);
		addEnumEditor(P_REPORTER, Messages.PreferencesPage_Reporter,
				TwigReporter.class).setEnabled(false, getFieldEditorParent());
		addEnumEditor(P_DISPLAY, Messages.PreferencesPage_Display,
				TwigDisplay.class).setEnabled(false, getFieldEditorParent());
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		// default
		boolean result = super.performOk();

		// changes?
		final ScopedPreferenceStore store = getPreferenceStore();
		if (result && store.needsSaving()) {
			try {
				// save
				store.save();

				// clean
				TwigcsBuilder.triggerCleanBuild();

			} catch (final IOException e) {
				handleStatusShow(
						createErrorStatus(Messages.Preferences_Error_Save, e));
				result = false;
			} catch (final CoreException e) {
				handleStatusShow(e.getStatus());
				result = false;
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setValid(final boolean b) {
		super.setValid(b);
		if (btnTest != null) {
			btnTest.setEnabled(b);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void contributeButtons(final Composite parent) {
		super.contributeButtons(parent);
		((GridLayout) parent.getLayout()).numColumns++;

		btnTest = new Button(parent, SWT.PUSH);
		btnTest.setText(Messages.PreferencesPage_Test);
		btnTest.setEnabled(isValid());
		btnTest.addListener(SWT.Selection, e -> {
			testCommand();
		});
		setButtonLayoutData(btnTest);
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
		fileEditor = new FileFieldEditor(P_EXECUTABLE_PATH,
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
		fileEditor.setErrorMessage(Messages.PreferencesPage_Error_Path);
		fileEditor.setEmptyStringAllowed(false);
		addField(fileEditor);
	}

	/**
	 * Creates an empty template file.
	 *
	 * @return the template file.
	 * @throws IOException
	 *             If a file could not be created.
	 */
	private String createTemplate() throws IOException {
		// already created?
		if (templateFile != null) {
			return templateFile;
		}

		// create empty file
		final File file = File.createTempFile("template", "twig"); //$NON-NLS-1$ //$NON-NLS-2$
		file.deleteOnExit();
		templateFile = file.getAbsolutePath();

		return templateFile;
	}

	/**
	 * Tests the Twigcs command.
	 */
	private void testCommand() {
		final String fileName = fileEditor.getStringValue();
		if (fileName == null || fileName.isEmpty()) {
			MessageDialog.openError(getShell(), getTitle(),
					Messages.PreferencesPage_Error_Path);
			return;
		}

		try {
			// processor
			final TwigcsProcessor processor = new TwigcsProcessor();
			processor.setSearchPath(createTemplate());
			processor.setProgramPath(fileName);

			// execute
			final List<String> command = processor.buildCommand();
			final IOExecutor executor = new IOExecutor();
			final int exitCode = executor.run(command);

			if (exitCode == 0) {
				MessageDialog.openInformation(getShell(), getTitle(),
						Messages.PreferencesPage_Test_Success);
			} else {
				MessageDialog.openError(getShell(), getTitle(),
						Messages.PreferencesPage_Test_Error);
			}

		} catch (final CoreException | IOException e) {
			MessageDialog.openError(getShell(), getTitle(),
					Messages.PreferencesPage_Test_Error);
		}
	}
}