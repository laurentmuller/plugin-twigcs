package twigcs.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;

import twigcs.core.IConstants;

/**
 * Extends the workbench content provider by filtering folders and files.
 *
 * @author Laurent Muller
 */
public class ResourceContentProvider extends BaseWorkbenchContentProvider
		implements IConstants {

	private final IProject project;

	public ResourceContentProvider(final IProject project) {
		this.project = project;
	}

	@Override
	public Object[] getChildren(final Object element) {
		final Object[] children = super.getChildren(element);
		if (children.length > 0) {
			final List<Object> result = new ArrayList<>(children.length);
			for (final Object child : children) {
				if (select(child)) {
					result.add(child);
				}
			}
			return result.toArray();
		}

		return children;
	}

	/**
	 * Checks that the given element can be selected.
	 *
	 * @param element
	 *            the element to verify.
	 * @return <code>true</code> if selected.
	 */
	private boolean select(final Object element) {
		if (element instanceof IProject) {
			return project == element;
		} else if (element instanceof IFolder) {
			final IFolder container = (IFolder) element;
			return container.getName().charAt(0) != '.';
		} else if (element instanceof IFile) {
			final IFile file = (IFile) element;
			return TWIG_EXTENSION.equals(file.getFileExtension());
		} else {
			return false;
		}
	}
}
