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
import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;

public class ResourceTableViewer extends TableViewer {

	/**
	 * Creates a table viewer on a newly-created table control under the given
	 * parent. The table control is created using the SWT style bits
	 * <code>SINGLE, H_SCROLL, V_SCROLL,</code> and <code>BORDER</code>.
	 *
	 * @param parent
	 *            the parent control.
	 */
	public ResourceTableViewer(final Composite parent) {
		super(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		setComparator(new ViewerComparator(Collator.getInstance()));
		setContentProvider(ArrayContentProvider.getInstance());
		setLabelProvider(new ResourceLabelProvider());
		setInput(new ArrayList<IResource>());
	}

	/**
	 * Sets the layout data associated with the receiver to the argument.
	 *
	 * @param layoutData
	 *            the new layout data for the receiver.
	 * @exception SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the receiver has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the receiver</li>
	 *                </ul>
	 */
	public void setLayoutData(final Object layoutData) {
		getTable().setLayoutData(layoutData);
	}

	/**
	 * Sets a new selection for this viewer and makes it visible.
	 *
	 * @param resource
	 *            the new selection or <code>null</code> if none.
	 */
	public void setSelection(final IResource resource) {
		if (resource == null) {
			setSelection(StructuredSelection.EMPTY);
		} else {
			setSelection(new StructuredSelection(resource), true);
		}
	}
}
