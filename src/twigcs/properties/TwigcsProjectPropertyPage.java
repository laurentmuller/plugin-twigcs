package twigcs.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import twigcs.core.IConstants;
import twigcs.core.ProjectPreferences;
import twigcs.ui.ResourceTableViewer;
import twigcs.ui.SelectionResourceDialog;

/**
 * Properties page for Twigcs project.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsProjectPropertyPage extends PropertyPage
		implements IConstants {

	// viewers
	private ResourceTableViewer includeViewer;
	private ResourceTableViewer excludeViewer;

	// lists
	private List<IResource> includeList;
	private List<IResource> excludeList;

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsProjectPropertyPage() {
		super();
		setDescription(
				"By default, all twig files are validate. Select the resources to include or to exclude.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IProject getElement() {
		final IAdaptable element = super.getElement();
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
	public boolean performOk() {
		try {
			// save
			final ProjectPreferences preferences = getPreferences();
			preferences.setIncludeResources(includeList);
			preferences.setExcludeResources(excludeList);
			preferences.flush();
		} catch (final BackingStoreException e) {
			return false;
		}

		return true;
	}

	/**
	 * Adds a resource.
	 *
	 * @param viewer
	 *            the viewer to update.
	 * @param list
	 *            the resource list to update.
	 */
	private void addResources(final ResourceTableViewer viewer,
			final List<IResource> list) {
		final IResource resource = selectResource(null);
		if (resource == null || list.contains(resource)) {
			return;
		}

		list.add(resource);
		viewer.refresh();
		viewer.setSelection(new StructuredSelection(resource));
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

		final GridLayout layout = new GridLayout(columns, false);
		layout.marginWidth = 0;
		composite.setLayout(layout);

		return composite;
	}

	/**
	 * Creates a resource viewer.
	 *
	 * @param parent
	 *            the parent composite.
	 * @param list
	 *            the resource list.
	 * @param text
	 *            the label's message.
	 * @return the newly created viewer.
	 */
	private ResourceTableViewer createViewer(final Composite parent,
			final List<IResource> list, final String text) {

		final Composite container = createComposite(parent, 2);

		// label
		final Label label = new Label(container, SWT.LEFT);
		if (text != null) {
			label.setText(text);
		}
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		label.setLayoutData(gd);

		// table viewer
		final ResourceTableViewer viewer = new ResourceTableViewer(container);
		viewer.setLayoutData(new GridData(GridData.FILL_BOTH));

		// buttons bar
		final Composite bars = new Composite(container, SWT.NONE);
		bars.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		final GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		bars.setLayout(layout);

		// buttons
		createViewerButton(bars, "Add...", e -> {
			addResources(viewer, list);
		});
		final Button deleteButton = createViewerButton(bars, "Remove", e -> {
			deleteResource(viewer, list);
		});
		final Button editButton = createViewerButton(bars, "Edit...", e -> {
			editResources(viewer, list);
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
		viewer.setSelection(StructuredSelection.EMPTY);

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
	private void deleteResource(final ResourceTableViewer viewer,
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
	private void editResources(final ResourceTableViewer viewer,
			final List<IResource> list) {
		final IStructuredSelection selection = viewer.getStructuredSelection();
		if (selection.isEmpty()) {
			return;
		}

		final IResource element = (IResource) selection.getFirstElement();
		final IResource resource = selectResource(element);
		if (resource == null || resource == element) {
			return;
		}

		list.remove(element);
		if (!list.contains(resource)) {
			list.add(resource);
			viewer.refresh();
			viewer.setSelection(new StructuredSelection(resource));
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
	 * Display the resource selection dialog.
	 *
	 * @param selection
	 *            the current selection or <code>null</code> if none.
	 * @return the selected resource, if any; <code>null</code> otherwise.
	 */
	private IResource selectResource(final IResource selection) {
		final IProject project = getElement();
		final SelectionResourceDialog dlg = new SelectionResourceDialog(
				getShell(), project);

		if (selection != null) {
			dlg.setInitialSelection(selection);
		}

		if (Window.OK == dlg.open()) {
			return dlg.getFirstResult();
		}

		return null;
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
				"Folders and files to &validate:");

		// exclude viewer
		excludeViewer = createViewer(container, excludeList,
				"Folders and files to e&xclude:");

		return container;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void performDefaults() {
		// clear
		includeList = new ArrayList<>();
		excludeList = new ArrayList<>();
		includeViewer.setInput(includeList);
		excludeViewer.setInput(excludeList);

		super.performDefaults();
	}

}