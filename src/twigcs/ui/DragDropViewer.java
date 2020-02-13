/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package twigcs.ui;

import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Adds drag and drop support between 2 table viewers.
 *
 * @author Laurent Muller
 * @version 1.0
 * @param <E>
 *            the generic model objects displayed in the viewers.
 */
public class DragDropViewer<E> {

	// the viewers
	private final TableViewer sourceViewer;
	private final TableViewer targetViewer;

	// the model lists
	private final List<E> sourceList;
	private final List<E> targetList;

	// the current dragging viewer
	private TableViewer dragViewer;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param sourceViewer
	 *            the source viewer.
	 * @param sourceList
	 *            the source model list.
	 * @param targetViewer
	 *            the target viewer.
	 * @param targetList
	 *            the target model list.
	 */
	public DragDropViewer(TableViewer sourceViewer, List<E> sourceList,
			TableViewer targetViewer, List<E> targetList) {
		this.sourceViewer = sourceViewer;
		this.sourceList = sourceList;

		this.targetViewer = targetViewer;
		this.targetList = targetList;

		// initialize
		addDragDropSupport(sourceViewer);
		addDragDropSupport(targetViewer);
	}

	/**
	 * Adds drag and drop support.
	 *
	 * @param viewer
	 *            the viewer to handle.
	 */
	private void addDragDropSupport(final TableViewer viewer) {
		addDragSupport(viewer);
		addDropSupport(viewer);
	}

	/**
	 * Adds drag support.
	 *
	 * @param viewer
	 *            the viewer to handle.
	 */
	private void addDragSupport(final TableViewer viewer) {
		final int operations = DND.DROP_MOVE;
		final LocalSelectionTransfer transfer = getTransfer();
		final Transfer[] types = new Transfer[] { transfer };

		final DragSourceAdapter adapter = new DragSourceAdapter() {
			@Override
			public void dragFinished(final DragSourceEvent event) {
				clearSelection();
			}

			@Override
			public void dragSetData(final DragSourceEvent event) {
				if (transfer.isSupportedType(event.dataType)) {
					event.data = transfer.getSelection();
				}
			}

			@Override
			public void dragStart(final DragSourceEvent event) {
				// only start dragging if there is a selection
				final ISelection selection = viewer.getSelection();
				if (selection.isEmpty()) {
					clearSelection();
					event.doit = false;
				} else {
					dragViewer = viewer;
					transfer.setSelection(selection);
					transfer.setSelectionSetTime(event.time & 0xFFFF);
				}
			}

			private void clearSelection() {
				dragViewer = null;
				transfer.setSelection(null);
				transfer.setSelectionSetTime(0);
			}
		};
		viewer.addDragSupport(operations, types, adapter);
	}

	/**
	 * Adds drop support.
	 *
	 * @param viewer
	 *            the viewer to handle.
	 */
	private void addDropSupport(final TableViewer viewer) {
		final int operations = DND.DROP_MOVE;
		final LocalSelectionTransfer transfer = getTransfer();
		final Transfer[] types = new Transfer[] { transfer };

		final ViewerDropAdapter adapter = new ViewerDropAdapter(viewer) {
			@Override
			public boolean performDrop(final Object data) {
				// check selection
				if (!(data instanceof IStructuredSelection)) {
					return false;
				}

				// get selection
				final IStructuredSelection selection = (IStructuredSelection) data;
				if (selection.isEmpty()) {
					return false;
				}

				// move
				if (dragViewer == sourceViewer) {
					return moveToTargetViewer(selection);
				} else if (dragViewer == targetViewer) {
					return moveToSourceViewer(selection);
				} else {
					return false;
				}
			}

			@Override
			public boolean validateDrop(final Object target,
					final int operation, final TransferData transferType) {
				if (!transfer.isSupportedType(transferType)) {
					return false;
				} else {
					// prevent self-drop
					return dragViewer != viewer;
				}
			}
		};
		viewer.addDropSupport(operations, types, adapter);
	}

	/**
	 * Gets the singleton local selection transfer instance.
	 *
	 * @return the transfer instance.
	 */
	private LocalSelectionTransfer getTransfer() {
		return LocalSelectionTransfer.getTransfer();
	}

	/**
	 * Move the given selection from the source viewer to the destination viewer
	 * and update model lists accordingly.
	 *
	 * @param selection
	 *            the selection to move.
	 * @param fromViewer
	 *            the source viewer.
	 * @param fromList
	 *            the source model list.
	 * @param toViewer
	 *            the destination viewer
	 * @param toList
	 *            the destination model list.
	 * @return <code>true</code> if one or more elements has been moved.
	 */
	private boolean move(IStructuredSelection selection, TableViewer fromViewer,
			List<E> fromList, TableViewer toViewer, List<E> toList) {
		// selection?
		if (selection.isEmpty()) {
			return false;
		}

		// move
		int fromIdx, toIdx;
		boolean moved = false;
		final List<?> elements = selection.toList();
		for (final Object element : elements) {
			fromIdx = fromList.indexOf(element);
			toIdx = toList.indexOf(element);
			if (fromIdx != -1 && toIdx == -1) {
				toList.add(fromList.remove(fromIdx));
				moved = true;
			}
		}

		// update
		if (moved) {
			fromViewer.refresh();
			toViewer.refresh();
			toViewer.setSelection(selection, true);
		}
		return moved;
	}

	/**
	 * Moves the given selection from the target viewer to the source viewer.
	 *
	 * @param selection
	 *            the selection to move.
	 * @return <code>true</code> if one or more elements has been moved.
	 */
	private boolean moveToSourceViewer(IStructuredSelection selection) {
		return move(selection, targetViewer, targetList, //
				sourceViewer, sourceList);
	}

	/**
	 * Moves the given selection from the source viewer to the target viewer.
	 *
	 * @param selection
	 *            the selection to move.
	 * @return <code>true</code> if one or more elements has been moved.
	 */
	private boolean moveToTargetViewer(IStructuredSelection selection) {
		return move(selection, sourceViewer, sourceList, //
				targetViewer, targetList);
	}
}
