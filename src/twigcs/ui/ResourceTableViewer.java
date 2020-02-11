package twigcs.ui;

import java.text.Collator;
import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;

public class ResourceTableViewer extends TableViewer {

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
	 *
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

}
