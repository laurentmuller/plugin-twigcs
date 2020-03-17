/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;

/**
 * Combo viewer for an {@link Enum} class.
 *
 * @author Laurent Muller
 * @version 1.0
 * @param <E>
 *            the enumeration type.
 */
public class EnumComboViewer<E extends Enum<E>> extends ComboViewer
		implements IString {

	/*
	 * the enumeration class
	 */
	private final Class<E> clazz;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param parent
	 *            the parent control.
	 * @param clazz
	 *            the enumeration class.
	 */
	public EnumComboViewer(final Composite parent, final Class<E> clazz) {
		super(parent);
		this.clazz = clazz;

		setLabelProvider(new LabelProvider() {
			@Override
			public String getText(final Object element) {
				return toProperCase(((Enum<?>) element).name());
			}
		});
		setContentProvider(ArrayContentProvider.getInstance());
		setInput(clazz.getEnumConstants());
	}

	/**
	 * Gets the selected value.
	 *
	 * @return the selected value, if any; <code>null</code> otherwise.
	 */
	public E getSelectedValue() {
		final Object value = getStructuredSelection().getFirstElement();
		if (value != null) {
			return clazz.cast(value);
		}
		return null;
	}

	/**
	 * Sets the layout data associated with the combo to the argument.
	 *
	 * @param layoutData
	 *            the new layout data for the receiver.
	 * @exception org.eclipse.swt.SWTException
	 *                <ul>
	 *                <li>ERROR_WIDGET_DISPOSED - if the combo has been
	 *                disposed</li>
	 *                <li>ERROR_THREAD_INVALID_ACCESS - if not called from the
	 *                thread that created the combo</li>
	 *                </ul>
	 */
	public void setLayoutData(final Object layoutData) {
		getControl().setLayoutData(layoutData);
	}

	/**
	 * Sets the selected value.
	 *
	 * @param value
	 *            the value to select.
	 */
	public void setSelectedValue(final E value) {
		if (value == null) {
			setSelection(StructuredSelection.EMPTY, true);
		} else {
			setSelection(new StructuredSelection(value), true);
		}
	}

}
