/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Dialog to select a single folder or eventually a file from a project.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceSelectionDialog extends ElementTreeSelectionDialog {

	/*
	 * the display files option
	 */
	private boolean displayFiles = false;

	/*
	 * the excluded resources
	 */
	private List<IResource> excludeResources = new ArrayList<>();

	/*
	 * the content provider
	 */
	private ResourceContentProvider provider;

	/**
	 * Creates a new instance of this class. The files are not displayed.
	 *
	 * @param parent
	 *            the parent shell.
	 * @param project
	 *            the root project.
	 * @param selection
	 *            the initial selection.
	 */
	public ResourceSelectionDialog(final Shell parent, final IProject project,
			final Object selection) {
		this(parent, project, selection, false);
	}

	/**
	 * Creates a new instance of this class.
	 *
	 * @param parent
	 *            the parent shell.
	 * @param project
	 *            the root project.
	 * @param selection
	 *            the initial selection.
	 * @param displayFiles
	 *            <code>true</code> to display files; <code>false</code> to
	 *            hide.
	 */
	public ResourceSelectionDialog(final Shell parent, final IProject project,
			final Object selection, final boolean displayFiles) {
		super(parent, new WorkbenchLabelProvider(),
				new ResourceContentProvider(project));

		setComparator(new ResourceViewerComparator());
		setValidator(new ResourceValidator());
		setDoubleClickSelects(false);
		setHelpAvailable(false);
		setAllowMultiple(false);

		// selection
		if (selection != null) {
			setInitialSelection(selection);
		} else {
			setInitialSelection(project);
		}

		// input
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot root = workspace.getRoot();
		setInput(root);

		setTitle("Resource selection");
		setDisplayFiles(displayFiles);
	}

	/**
	 * Gets the resources to excludes.
	 *
	 * @return the resources to excludes.
	 */
	public List<IResource> getExcludeResources() {
		return excludeResources;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IResource getFirstResult() {
		final Object result = super.getFirstResult();
		return result instanceof IResource ? (IResource) result : null;
	}

	/**
	 * Returns if the files are displayed. The default value <code>false</code>.
	 *
	 * @return <code>true</code> to display files; <code>false</code> to hide.
	 */
	public boolean isDisplayFiles() {
		return displayFiles;
	}

	/**
	 * Sets if the files are display. This property must be set before opening
	 * this dialog.
	 *
	 * @param displayFiles
	 *            <code>true</code> to display files; <code>false</code> to
	 *            hide.
	 */
	public void setDisplayFiles(final boolean displayFiles) {
		if (displayFiles) {
			setMessage("&Choose a folder or a file:");
		} else {
			setMessage("&Choose a folder:");
		}
		if (provider != null) {
			provider.setDisplayFiles(displayFiles);
			getTreeViewer().refresh();
		}
		this.displayFiles = displayFiles;
	}

	/**
	 * Sets the resources to exclude. This property must be set before opening
	 * this dialog.
	 *
	 * @param excludeResources
	 *            the resources to exclude.
	 */
	public void setExcludeResources(final List<IResource> excludeResources) {
		if (excludeResources == null) {
			this.excludeResources = new ArrayList<>();
		} else {
			this.excludeResources = excludeResources;
		}
		if (provider != null) {
			provider.setExcludeResources(this.excludeResources);
			getTreeViewer().refresh();
		}
	}

	@Override
	protected TreeViewer createTreeViewer(final Composite parent) {
		final TreeViewer viewer = super.createTreeViewer(parent);
		provider = (ResourceContentProvider) viewer.getContentProvider();
		provider.setExcludeResources(excludeResources);
		provider.setDisplayFiles(displayFiles);
		viewer.refresh();
		return viewer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected TreeViewer doCreateTreeViewer(final Composite parent,
			final int style) {
		final TreeViewer viewer = super.doCreateTreeViewer(parent, style);
		viewer.setAutoExpandLevel(2);
		return viewer;
	}
}
