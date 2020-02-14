/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.PropertyPage;
import org.osgi.service.prefs.BackingStoreException;

import twigcs.TwigcsPlugin;
import twigcs.core.IConstants;
import twigcs.core.ProjectPreferences;
import twigcs.core.TwigcsBuilder;
import twigcs.ui.DragDropViewer;
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

	// original lists
	private List<IResource> oldIncludeList;
	private List<IResource> oldExcludeList;

	// modified lists
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
			// change?
			if (!oldIncludeList.equals(includeList)
					|| !oldExcludeList.equals(excludeList)) {
				// save
				final ProjectPreferences preferences = getPreferences();
				preferences.setIncludeResources(includeList);
				preferences.setExcludeResources(excludeList);
				preferences.flush();

				// build
				TwigcsBuilder.triggerCleanBuild(getElement());
			}
		} catch (final BackingStoreException e) {
			TwigcsPlugin.handleError(
					createErrorStatus("Failed to save preferences", e));
			return false;
		} catch (final CoreException e) {
			TwigcsPlugin.handleError(e.getStatus());
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
		oldIncludeList = preferences.getIncludeResources();
		oldExcludeList = preferences.getExcludeResources();

		includeList = new ArrayList<IResource>(oldIncludeList);
		excludeList = new ArrayList<IResource>(oldExcludeList);

		// container
		final Composite container = createComposite(parent, 1);

		// include viewer
		includeViewer = createViewer(container, includeList,
				"Folders to &include:");

		// exclude viewer
		excludeViewer = createViewer(container, excludeList,
				"Folders to e&xclude:");

		// note label
		final Label label = createLabel(container,
				"Note: Included folders and files have priority over excluded folders and files.",
				1);
		((GridData) label.getLayoutData()).widthHint = 250;

		// add drag and drop support
		new DragDropViewer<IResource>(includeViewer, includeList, excludeViewer,
				excludeList);

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
		final IResource templates = getElement().findMember("templates");
		if (templates != null) {
			includeList.add(templates);
			includeViewer.refresh();
		}

		// find vendor folder
		final IResource vendor = getElement().findMember("vendor");
		if (vendor != null) {
			excludeList.add(vendor);
			excludeViewer.refresh();
		}

		// update
		includeViewer.refresh();
		excludeViewer.refresh();

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
	private void addResources(final ResourceTableViewer viewer,
			final List<IResource> list) {
		final IResource resource = selectResource(null);
		if (resource != null) {
			if (!list.contains(resource)) {
				list.add(resource);
				viewer.refresh();
			}
			viewer.setSelection(resource);
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

		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = Math.max(columns, 1);
		label.setLayoutData(gd);

		return label;
	}

	/**
	 * Creates a resource viewer and edtion buttons.
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
		// viewer and buttons container
		final Composite container = createComposite(parent, 2);

		// label
		createLabel(container, text, 2);

		// table viewer
		final ResourceTableViewer viewer = new ResourceTableViewer(container);
		GridDataFactory.fillDefaults().grab(true, true)
				.hint(convertHorizontalDLUsToPixels(120),
						viewer.getTable().getItemHeight() * 5)
				.applyTo(viewer.getControl());

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
		createViewerButton(buttonsBar, "Add...", addListener);
		final Button deleteButton = createViewerButton(buttonsBar, "Remove",
				deleteListener);
		final Button editButton = createViewerButton(buttonsBar, "Edit...",
				editListener);

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
		// viewer.setSelection(list.isEmpty() ? null : list.get(0));

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
			viewer.setSelection(resource);
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
		final List<IResource> list = new ArrayList<>();
		list.addAll(includeList);
		list.addAll(excludeList);
		list.remove(selection);

		final ResourceSelectionDialog dlg = new ResourceSelectionDialog(
				getShell(), project, selection);
		dlg.setExcludeResources(list);

		if (Window.OK == dlg.open()) {
			return dlg.getFirstResult();
		}

		return null;
	}
}