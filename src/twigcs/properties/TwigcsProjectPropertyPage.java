package twigcs.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.PropertyPage;
import org.eclipse.ui.part.ResourceTransfer;
import org.osgi.service.prefs.BackingStoreException;

import twigcs.core.IConstants;
import twigcs.core.ProjectPreferences;
import twigcs.ui.ResourceSelectionDialog;
import twigcs.ui.ResourceTableViewer;

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
				"By default, all Twig files (*.twig) are validate. Select the resources to include or to exclude.");
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

		// note label
		final Label label = createLabel(container,
				"Note: Included folders and files have priority over excluded folders and files.",
				1);
		((GridData) label.getLayoutData()).widthHint = 250;

		// drag and drop
		addDragDropSupport(includeViewer, includeList, excludeViewer,
				excludeList);

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

	/**
	 * Adds drag and drop support between the 2 viewers.
	 *
	 * @param sourceViewer
	 *            the source viewer.
	 * @param targetViewer
	 *            the target viewer.
	 */
	private void addDragDropSupport(ResourceTableViewer sourceViewer,
			List<IResource> sourceList, ResourceTableViewer targetViewer,
			List<IResource> targetList) {

		final int operations = DND.DROP_MOVE;
		final ResourceTransfer transfer = ResourceTransfer.getInstance();
		final Transfer[] types = new Transfer[] { transfer };

		final DragSourceAdapter dragAdapter = new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event) {
				final IResource[] resources = getResources();
				if (resources != null) {
					event.data = resources;
				} else {
					event.doit = false;
				}
			}

			private IResource[] getResources() {
				final Object[] data = sourceViewer.getStructuredSelection()
						.toArray();
				if (data.length != 0) {
					final IResource[] resources = new IResource[data.length];
					System.arraycopy(data, 0, resources, 0, data.length);
					return resources;
				}
				return null;
			}
		};
		sourceViewer.addDragSupport(operations, types, dragAdapter);

		// add drop
		final ViewerDropAdapter dropAdapter = new ViewerDropAdapter(
				targetViewer) {
			@Override
			public boolean performDrop(Object data) {
				final List<IResource> list = getResources(data);
				if (list != null) {
					// move
					sourceList.removeAll(list);
					targetList.addAll(list);

					// update viewers
					sourceViewer.refresh();
					targetViewer.refresh();

					// select
					targetViewer.setSelection(new StructuredSelection(list));

					return true;
				}
				return false;
			}

			@Override
			public boolean validateDrop(Object target, int operation,
					TransferData transferType) {
				if (!transfer.isSupportedType(transferType)) {
					return false;
				}
				return true;
			}

			private List<IResource> getResources(Object data) {
				if (data instanceof IResource[]) {
					final IResource[] resources = (IResource[]) data;
					return Arrays.asList(resources);
				}
				return null;
			}
		};
		dropAdapter.setFeedbackEnabled(false);
		targetViewer.addDropSupport(operations, types, dropAdapter);
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
		if (resource != null && !list.contains(resource)) {
			list.add(resource);
			viewer.refresh();
		}
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
	 * Creates a label control.
	 *
	 * @param parent
	 *            the parent composite.
	 * @param text
	 *            the label's text.
	 * @return the newly created label.
	 */
	private Label createLabel(final Composite parent, String text,
			int columns) {
		final Label label = new Label(parent, SWT.WRAP);
		if (text != null) {
			label.setText(text);
		}
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		if (columns > 1) {
			gd.horizontalSpan = columns;
		}
		label.setLayoutData(gd);

		return label;
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
		createLabel(container, text, 2);

		// table viewer
		final ResourceTableViewer viewer = new ResourceTableViewer(container);
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = convertHorizontalDLUsToPixels(120);
		gd.heightHint = viewer.getTable().getItemHeight() * 5;
		viewer.setLayoutData(gd);

		// button bar
		final Composite buttonBar = new Composite(container, SWT.NONE);
		buttonBar.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		final GridLayout layout = new GridLayout();
		layout.marginWidth = layout.marginHeight = 0;
		buttonBar.setLayout(layout);

		// buttons
		createViewerButton(buttonBar, "Add...", //
				e -> {
					addResources(viewer, list);
				});
		final Button deleteButton = createViewerButton(buttonBar, "Remove",
				e -> {
					deleteResource(viewer, list);
				});
		final Button editButton = createViewerButton(buttonBar, "Edit...",
				e -> {
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

		final Object element = selection.getFirstElement();
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
	private IResource selectResource(final Object selection) {
		final IProject project = getElement();
		final ResourceSelectionDialog dlg = new ResourceSelectionDialog(
				getShell(), project);

		if (selection != null) {
			dlg.setInitialSelection(selection);
		}

		if (Window.OK == dlg.open()) {
			return dlg.getFirstResult();
		}

		return null;
	}
}