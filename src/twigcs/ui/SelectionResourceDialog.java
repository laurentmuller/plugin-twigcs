package twigcs.ui;

import java.text.Collator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Dialog to select a single folder or file.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class SelectionResourceDialog extends ElementTreeSelectionDialog {

	/**
	 * Creates a new instance of this class.
	 *
	 * @param parent
	 *            the parent shell.
	 * @param project
	 *            the root project.
	 */
	public SelectionResourceDialog(final Shell parent, final IProject project) {
		super(parent, new WorkbenchLabelProvider(),
				new ResourceContentProvider(project));
		setComparator(new ViewerComparator(Collator.getInstance()));
		setValidator(new ResourceValidator());
		setDoubleClickSelects(false);
		setHelpAvailable(false);
		setAllowMultiple(false);

		// input
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IWorkspaceRoot root = workspace.getRoot();
		setInput(root);

		setMessage("&Choose a folder or a file:");
		setTitle("Resource selection");
	}

	@Override
	public IResource getFirstResult() {
		final Object result = super.getFirstResult();
		return result instanceof IResource ? (IResource) result : null;
	}
}
