/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Extends the workbench label provider by displaying the project relative path
 * as text.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class FolderLabelProvider extends WorkbenchLabelProvider {

	/**
	 * Returns an instance folder label provider.
	 *
	 * @return a new <code>FolderLabelProvider</code>.
	 */
	public static FolderLabelProvider getInstance() {
		return new FolderLabelProvider();

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String decorateText(final String input, final Object element) {
		if (element instanceof IResource) {
			final IResource resource = (IResource) element;
			final IPath path = resource.getProjectRelativePath();
			return path.toOSString();
		}
		return super.decorateText(input, element);
	}
}
