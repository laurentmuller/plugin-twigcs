/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.ui;

import java.text.Collator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ViewerComparator;

/**
 * Comparator for {@link IResource}. The {@link IContainer} elements have
 * precedence over {@link IFile} elements.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class ResourceViewerComparator extends ViewerComparator {

	/**
	 * Creates a new instance of this class.
	 */
	public ResourceViewerComparator() {
		super(Collator.getInstance());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int category(Object element) {
		if (element instanceof IContainer) {
			return 0;
		} else {
			return 1;
		}
	}
}
