package twigcs.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class ResourceLabelProvider extends WorkbenchLabelProvider {

	@Override
	protected String decorateText(final String input, final Object element) {
		if (element instanceof IResource) {
			final IResource resource = (IResource) element;
			final IPath path = resource.getProjectRelativePath();
			return path.toString();
		}
		return super.decorateText(input, element);
	}

}
