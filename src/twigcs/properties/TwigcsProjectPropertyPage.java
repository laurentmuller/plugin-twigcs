package twigcs.properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.dialogs.ResourceListSelectionDialog;
import org.osgi.service.prefs.BackingStoreException;

import twigcs.core.IConstants;
import twigcs.core.ProjectPreferences;

/**
 * Twigcs project properties page.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsProjectPropertyPage extends PropertyPage
		implements IConstants {

	private Table includeTable;
	private Table excludeTable;

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsProjectPropertyPage() {
		super();
		setDescription("Project settings for twigcs");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		final IProject project = getProject();
		final ProjectPreferences preferences = new ProjectPreferences(project);

		// get value
		final String[] includePaths = getPaths(includeTable);
		final String[] excludePaths = getPaths(excludeTable);

		try {
			// set preferences and save
			preferences.setIncludePaths(includePaths);
			preferences.setExcludePaths(excludePaths);
			preferences.flush();
		} catch (final BackingStoreException e) {
			return false;
		}

		return true;
	}

	private Button createBarButton(Composite parent, String text,
			Listener listener) {
		final Button button = new Button(parent, SWT.PUSH);
		setButtonLayoutData(button);

		if (text != null) {
			button.setText(text);
		}

		if (listener != null) {
			button.addListener(SWT.Selection, listener);
		}

		return button;
	}

	private Composite createComposite(final Composite parent,
			final int columns) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout(columns, false));
		return composite;
	}

	private Table createTable(Composite parent, String text) {
		final Composite container = createComposite(parent, 2);

		// label
		final Label label = new Label(container, SWT.LEFT);
		if (text != null) {
			label.setText(text);
		}
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		// tables
		final Table table = new Table(container, SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		// buttons
		final Composite bars = new Composite(container, SWT.NONE);
		bars.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		final GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		bars.setLayout(layout);

		createBarButton(bars, "Add...", e -> {
			final IResource[] resources = selectResources(null);
			for (final IResource resource : resources) {
				final String path = resource.getProjectRelativePath()
						.toPortableString();
				final TableItem item = new TableItem(table, SWT.LEFT);
				item.setText(path);
			}
		});

		final Button removeButton = createBarButton(bars, "Remove", e -> {
			final TableItem[] items = table.getSelection();
			for (final TableItem item : items) {
				item.dispose();
			}
		});

		final Button editButton = createBarButton(bars, "Change...", e -> {

		});

		table.addListener(SWT.Selection, e -> {
			final boolean enabled = table.getSelectionCount() > 0;
			removeButton.setEnabled(enabled);
			editButton.setEnabled(enabled);
		});

		return table;

	}

	/**
	 * Gets paths for the given table.
	 *
	 * @param table
	 *            the table to paths for.
	 * @return the paths.
	 */
	private String[] getPaths(Table table) {
		final TableItem[] items = table.getItems();
		final String[] result = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			result[i] = items[i].getText();
		}
		return result;
	}

	/**
	 * Gets the project.
	 *
	 * @return the project.
	 */
	private IProject getProject() {
		final IAdaptable element = getElement();
		if (element instanceof IProject) {
			return (IProject) element;
		} else {
			return element.getAdapter(IProject.class);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Composite createContents(final Composite parent) {
		final Composite composite = createComposite(parent, 1);

		// include paths
		includeTable = createTable(composite,
				"Enable Twigcs validation for this folders and files:");

		// exclude paths
		excludeTable = createTable(composite,
				"Exclude this files and folders from validation:");

		setPaths(includeTable, "test");

		includeTable.notifyListeners(SWT.Selection, null);
		excludeTable.notifyListeners(SWT.Selection, null);

		return composite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void performDefaults() {
		if (includeTable != null) {
			includeTable.clearAll();
		}
		if (excludeTable != null) {
			excludeTable.clearAll();
		}
		super.performDefaults();
	}

	IResource[] selectResources(IResource selection) {
		final IProject project = getProject();
		final ResourceListSelectionDialog dlg = new ResourceListSelectionDialog(
				getShell(), project, IResource.FOLDER | IResource.FILE);
		dlg.setAllowUserToToggleDerived(false);

		if (Window.OK == dlg.open()) {
			final Object[] result = dlg.getResult();
			final IResource[] resources = new IResource[result.length];
			for (int i = 0; i < result.length; i++) {
				resources[i] = (IResource) result[i];
			}
			return resources;
		}

		return new IResource[] {};
	}

	void setPaths(Table table, String... paths) {
		table.clearAll();
		for (final String path : paths) {
			if (path != null && !path.isEmpty()) {
				final TableItem item = new TableItem(table, SWT.DEFAULT);
				item.setText(path);
			}
		}
	}
}