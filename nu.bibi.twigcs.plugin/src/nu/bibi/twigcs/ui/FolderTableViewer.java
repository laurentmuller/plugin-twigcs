/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

import java.text.Collator;
import java.util.ArrayList;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Table viewer to display {@link IFolder} elements.
 *
 * @author Laurent Muller
 */
public class FolderTableViewer extends TableViewer {

	/**
	 * Creates a table viewer on a newly-created table control under the given
	 * parent. The table control is created using the SWT style bits
	 * <code>SINGLE, H_SCROLL, V_SCROLL, FULL_SELECTION</code> and
	 * <code>BORDER</code>.
	 *
	 * @param parent
	 *            the parent control.
	 */
	public FolderTableViewer(final Composite parent) {
		super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.BORDER);
		setComparator(new ViewerComparator(Collator.getInstance()));
		setContentProvider(ArrayContentProvider.getInstance());
		setLabelProvider(FolderLabelProvider.getInstance());
		setInput(new ArrayList<IResource>());
	}

	/**
	 * Gets the selected value
	 *
	 * @return the selected value, if any; <code>null</code> otherwise.
	 */
	public IResource getSelectedValue() {
		final Object element = getStructuredSelection().getFirstElement();
		if (element instanceof IResource) {
			return (IResource) element;
		}
		return null;
	}

	/**
	 * Sets the layout data associated with the table to the argument.
	 *
	 * @param layoutData
	 *            the new layout data for the receiver.
	 * @exception org.eclipse.swt.SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the table has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the table</li>
	 *                </ul>
	 */
	public void setLayoutData(final Object layoutData) {
		getTable().setLayoutData(layoutData);
	}

	/**
	 * Sets the selected value.
	 *
	 * @param resource
	 *            the value to select or <code>null</code> if none.
	 */
	public void setSelectedValue(final IResource resource) {
		if (resource == null) {
			setSelection(StructuredSelection.EMPTY, true);
		} else {
			setSelection(new StructuredSelection(resource), true);
		}
	}
}
