/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.PropertyPage;

import nu.bibi.twigcs.core.ICoreException;
import nu.bibi.twigcs.core.TwigcsBuilder;
import nu.bibi.twigcs.internal.Messages;
import nu.bibi.twigcs.model.TwigSeverity;
import nu.bibi.twigcs.model.TwigVersion;
import nu.bibi.twigcs.preferences.PreferencesInitializer;
import nu.bibi.twigcs.preferences.ProjectPreferences;

/**
 * Properties page for Twigcs project.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ProjectPropertyPage extends PropertyPage
		implements ICoreException {

	// viewers
	private FolderTableViewer includeViewer;
	private FolderTableViewer excludeViewer;

	// lists
	private List<IResource> includeList;
	private List<IResource> excludeList;

	// overrides
	private Button chkOverride;
	private EnumComboViewer<TwigVersion> twigVersionViewer;
	private EnumComboViewer<TwigSeverity> twigSeverityViewer;

	/**
	 * Creates a new instance of this class.
	 */
	public ProjectPropertyPage() {
		super();
		setDescription(Messages.ProjectPropertyPage_Description);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The implementation of <code>ProjectPropertyPage</code> cast the element
	 * to an {@link IProject}.
	 * </p>
	 */
	@Override
	public IProject getElement() {
		return (IProject) super.getElement();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performOk() {
		try {
			// preferences
			final ProjectPreferences preferences = getPreferences();

			// save paths
			preferences.setIncludeResources(includeList);
			preferences.setExcludeResources(excludeList);

			// save override
			TwigVersion version = null;
			TwigSeverity severity = null;
			if (chkOverride.getSelection()) {
				version = twigVersionViewer.getSelectedValue();
				severity = twigSeverityViewer.getSelectedValue();
			}
			preferences.setTwigVersion(version);
			preferences.setTwigSeverity(severity);

			// save and build if dirty
			if (preferences.isDirty()) {
				preferences.flush();
				TwigcsBuilder.triggerCleanBuild(preferences.getProject());
			}

		} catch (final CoreException e) {
			handleStatusShow(e.getStatus());
			return false;
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Composite createContents(final Composite parent) {
		// get lists
		final ProjectPreferences preferences = getPreferences();
		includeList = preferences.getIncludeResources();
		excludeList = preferences.getExcludeResources();

		// container
		final Composite container = createComposite(parent, 1);

		// include viewer
		includeViewer = createViewer(container, includeList,
				Messages.ProjectPropertyPage_Include);

		// exclude viewer
		excludeViewer = createViewer(container, excludeList,
				Messages.ProjectPropertyPage_Exclude);

		// add drag and drop support
		DragDropViewer.instance(includeViewer, includeList, //
				excludeViewer, excludeList);

		// override settings
		createOverride(parent, preferences);

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void performDefaults() {
		// clear
		includeList.clear();
		excludeList.clear();

		// find templates folder
		final IProject project = getElement();
		final IResource templates = project.findMember("templates"); //$NON-NLS-1$
		if (templates != null) {
			includeList.add(templates);
			includeViewer.refresh();
			includeViewer.setSelectedValue(templates);
		} else {
			includeViewer.refresh();
		}

		// find vendor folder
		final IResource vendor = project.findMember("vendor"); //$NON-NLS-1$
		if (vendor != null) {
			excludeList.add(vendor);
			excludeViewer.refresh();
			excludeViewer.setSelectedValue(vendor);
		} else {
			excludeViewer.refresh();
		}

		// override
		chkOverride.setSelection(false);
		twigVersionViewer
				.setSelectedValue(PreferencesInitializer.getTwigVersion());
		twigSeverityViewer
				.setSelectedValue(PreferencesInitializer.getTwigSeverity());
		chkOverride.notifyListeners(SWT.Selection, null);

		super.performDefaults();
	}

	/**
	 * Adds a resource.
	 *
	 * @param viewer
	 *            the viewer to update.
	 * @param list
	 *            the resource list to update.
	 */
	private void addResources(final FolderTableViewer viewer,
			final List<IResource> list) {
		final IResource resource = selectFolder(null);
		if (resource != null) {
			if (!list.contains(resource)) {
				list.add(resource);
				viewer.refresh();
			}
			viewer.setSelectedValue(resource);
		}
	}

	/**
	 * Creates a composite control.
	 *
	 * @param parent
	 *            the parent composite.
	 * @param columns
	 *            the number of columns.
	 * @return the newly created composite.
	 */
	private Composite createComposite(final Composite parent,
			final int columns) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		GridLayoutFactory.swtDefaults().numColumns(columns).margins(0, 5)
				.applyTo(composite);

		return composite;
	}

	/**
	 * Creates a read-only combo viewer for the given enumeration.
	 *
	 * @param <E>
	 *            the enumeration type.
	 * @param parent
	 *            the parent composite.
	 * @param clazz
	 *            the enumeration class.
	 * @param text
	 *            the label's message.
	 * @param selection
	 *            the default selection.
	 * @return the combo viewer.
	 */
	private <E extends Enum<E>> EnumComboViewer<E> createEnumViewer(
			final Composite parent, final Class<E> clazz, final String text,
			final E selection) {
		// label
		createLabel(parent, text, 1);

		// viewer
		final EnumComboViewer<E> viewer = new EnumComboViewer<>(parent, clazz);
		final GridData gd = new GridData();
		gd.widthHint = 120;
		viewer.setLayoutData(gd);
		viewer.setSelectedValue(selection);

		return viewer;
	}

	/**
	 * Creates a label control.
	 *
	 * @param parent
	 *            the parent composite.
	 * @param text
	 *            the label's text.
	 * @return the newly created label.
	 */
	private Label createLabel(final Composite parent, final String text,
			final int columns) {
		final Label label = new Label(parent, SWT.WRAP);
		if (text != null) {
			label.setText(text);
		}

		final GridData gd = new GridData();// GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = Math.max(columns, 1);
		label.setLayoutData(gd);

		return label;
	}

	/**
	 * Creates the override settings.
	 *
	 * @param parent
	 *            the parent composite.
	 * @param preferences
	 *            the project preferences.
	 */
	private void createOverride(final Composite parent,
			final ProjectPreferences preferences) {
		// override checkbox
		chkOverride = new Button(parent, SWT.CHECK);
		chkOverride.setText(Messages.ProjectPropertyPage_Override);

		// override container
		final Composite overrideContainer = createComposite(parent, 2);
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalIndent = 16;
		overrideContainer.setLayoutData(gd);

		// twig version
		final TwigVersion version = preferences.getTwigVersion();
		twigVersionViewer = createEnumViewer(overrideContainer,
				TwigVersion.class, Messages.PreferencesPage_Version, version);

		// twig severity
		final TwigSeverity severity = preferences.getTwigSeverity();
		twigSeverityViewer = createEnumViewer(overrideContainer,
				TwigSeverity.class, Messages.PreferencesPage_Severity,
				severity);

		// add listener
		chkOverride.addListener(SWT.Selection, e -> {
			final boolean enabled = chkOverride.getSelection();
			final Control[] children = overrideContainer.getChildren();
			for (final Control control : children) {
				control.setEnabled(enabled);
			}
		});

		// check if override
		if (!version.equals(PreferencesInitializer.getTwigVersion())
				|| !severity.equals(PreferencesInitializer.getTwigSeverity())) {
			chkOverride.setSelection(true);
		}
		chkOverride.notifyListeners(SWT.Selection, null);
	}

	/**
	 * Creates a resource viewer and buttons bar.
	 *
	 * @param parent
	 *            the parent composite.
	 * @param list
	 *            the resource list.
	 * @param text
	 *            the label's message.
	 * @return the newly created viewer.
	 */
	private FolderTableViewer createViewer(final Composite parent,
			final List<IResource> list, final String text) {
		// container for viewer and buttons
		final Composite container = createComposite(parent, 2);

		// label
		createLabel(container, text, 2);

		// table viewer
		final FolderTableViewer viewer = new FolderTableViewer(container);
		final Table table = viewer.getTable();
		GridDataFactory.fillDefaults().grab(true, true)
				.hint(convertHorizontalDLUsToPixels(120),
						table.getItemHeight() * 5)
				.applyTo(table);

		// buttons bar
		final Composite buttonsBar = new Composite(container, SWT.NONE);
		buttonsBar.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		GridLayoutFactory.fillDefaults().applyTo(buttonsBar);

		// button listeners
		final Listener addListener = e -> {
			addResources(viewer, list);
		};
		final Listener deleteListener = e -> {
			deleteResource(viewer, list);
		};
		final Listener editListener = e -> {
			editResources(viewer, list);
		};

		// buttons
		createViewerButton(buttonsBar, Messages.ProjectPropertyPage_Add,
				addListener);
		final Button deleteButton = createViewerButton(buttonsBar,
				Messages.ProjectPropertyPage_Remove, deleteListener);
		final Button editButton = createViewerButton(buttonsBar,
				Messages.ProjectPropertyPage_Edit, editListener);

		// add viewer listeners
		viewer.getTable().addListener(SWT.KeyDown, e -> {
			if (e.keyCode == SWT.DEL && deleteButton.isEnabled()) {
				deleteButton.notifyListeners(SWT.Selection, null);
			}
		});
		viewer.addSelectionChangedListener(e -> {
			final boolean enabled = !e.getSelection().isEmpty();
			deleteButton.setEnabled(enabled);
			editButton.setEnabled(enabled);
		});
		viewer.addDoubleClickListener(e -> {
			if (editButton.isEnabled()) {
				editButton.notifyListeners(SWT.Selection, null);
			}
		});

		// update
		viewer.setInput(list);
		viewer.setSelectedValue(list.isEmpty() ? null : list.get(0));

		return viewer;
	}

	/**
	 * Creates a viewer button.
	 *
	 * @param parent
	 *            the parent composite.
	 * @param text
	 *            the button's text.
	 * @param listener
	 *            the button's listener.
	 * @return the newly created button.
	 */
	private Button createViewerButton(final Composite parent, final String text,
			final Listener listener) {
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

	/**
	 * Delete a resource.
	 *
	 * @param viewer
	 *            the viewer to update.
	 * @param list
	 *            the resource list to update.
	 */
	private void deleteResource(final FolderTableViewer viewer,
			final List<IResource> list) {
		final IStructuredSelection selection = viewer.getStructuredSelection();
		if (selection.isEmpty()) {
			return;
		}

		list.removeAll(selection.toList());
		viewer.refresh();
	}

	/**
	 * Edit a resource.
	 *
	 * @param viewer
	 *            the viewer to update.
	 * @param list
	 *            the resource list to update.
	 */
	private void editResources(final FolderTableViewer viewer,
			final List<IResource> list) {
		// get current selection
		final IResource oldResource = viewer.getSelectedValue();
		if (oldResource == null) {
			return;
		}

		// get new selection
		final IResource newResource = selectFolder(oldResource);
		if (newResource == null || newResource.equals(oldResource)) {
			return;
		}

		// remove if present in the other list
		if (list.equals(includeList) //
				&& excludeList.remove(oldResource)) {
			excludeViewer.refresh();
		} else if (list.equals(excludeList)
				&& includeList.remove(oldResource)) {
			includeViewer.refresh();
		}

		list.remove(oldResource);
		if (!list.contains(newResource)) {
			list.add(newResource);
			viewer.refresh();
			viewer.setSelectedValue(newResource);
		}
	}

	/**
	 * Gets the project preferences.
	 *
	 * @return the project preferences.
	 */
	private ProjectPreferences getPreferences() {
		final IProject project = getElement();
		return new ProjectPreferences(project);
	}

	/**
	 * Display the folder selection dialog.
	 *
	 * @param selection
	 *            the current selection or <code>null</code> if none.
	 * @return the selected folder, if any; <code>null</code> otherwise.
	 */
	private IResource selectFolder(final IResource selection) {
		final IProject project = getElement();
		final List<IResource> list = new ArrayList<>();
		list.addAll(includeList);
		list.addAll(excludeList);
		list.remove(selection);

		final FolderSelectionDialog dlg = new FolderSelectionDialog(getShell(),
				project, list, selection);

		if (Window.OK == dlg.open()) {
			return dlg.getFirstResult();
		}

		return null;
	}
}