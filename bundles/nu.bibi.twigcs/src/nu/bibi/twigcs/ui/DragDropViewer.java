/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.ui;

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

	/*
	 * the DND operation
	 */
	private static final int DND_OPERATION = DND.DROP_MOVE;

	/*
	 * the DND transfer
	 */
	static final LocalSelectionTransfer DND_TRANSFER = LocalSelectionTransfer
			.getTransfer();

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
	 * @return the newly created instance.
	 */
	public static <E> DragDropViewer<E> instance(final TableViewer sourceViewer,
			final List<E> sourceList, final TableViewer targetViewer,
			final List<E> targetList) {
		return new DragDropViewer<>(sourceViewer, sourceList, targetViewer,
				targetList);
	}

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
	public DragDropViewer(final TableViewer sourceViewer,
			final List<E> sourceList, final TableViewer targetViewer,
			final List<E> targetList) {
		this.sourceViewer = sourceViewer;
		this.sourceList = sourceList;

		this.targetViewer = targetViewer;
		this.targetList = targetList;

		// initialize
		addDragSupport(sourceViewer);
		addDropSupport(sourceViewer);
		addDragSupport(targetViewer);
		addDropSupport(targetViewer);
	}

	/**
	 * Adds drag support.
	 *
	 * @param viewer
	 *            the viewer to handle.
	 */
	private void addDragSupport(final TableViewer viewer) {
		final DragSourceAdapter adapter = new DragSourceAdapter() {
			@Override
			public void dragFinished(final DragSourceEvent event) {
				clearSelection();
			}

			@Override
			public void dragSetData(final DragSourceEvent event) {
				if (DND_TRANSFER.isSupportedType(event.dataType)) {
					event.data = DND_TRANSFER.getSelection();
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
					DND_TRANSFER.setSelection(selection);
					DND_TRANSFER.setSelectionSetTime(event.time & 0xFFFF);
				}
			}

			private void clearSelection() {
				dragViewer = null;
				DND_TRANSFER.setSelection(null);
				DND_TRANSFER.setSelectionSetTime(0);
			}
		};
		viewer.addDragSupport(DND_OPERATION, new Transfer[] { DND_TRANSFER },
				adapter);
	}

	/**
	 * Adds drop support.
	 *
	 * @param viewer
	 *            the viewer to handle.
	 */
	private void addDropSupport(final TableViewer viewer) {
		final ViewerDropAdapter adapter = new ViewerDropAdapter(viewer) {

			@Override
			public boolean performDrop(final Object data) {
				// check selection
				if (!(data instanceof IStructuredSelection)) {
					return false;
				}
				if (((IStructuredSelection) data).isEmpty()) {
					return false;
				}

				// move
				if (dragViewer == sourceViewer) {
					return moveToTargetViewer();
				} else if (dragViewer == targetViewer) {
					return moveToSourceViewer();
				} else {
					return false;
				}
			}

			@Override
			public boolean validateDrop(final Object target,
					final int operation, final TransferData transferType) {
				if (!DND_TRANSFER.isSupportedType(transferType)) {
					return false;
				} else {
					// prevent self-drop
					return dragViewer != viewer;
				}
			}
		};
		viewer.addDropSupport(DND_OPERATION, new Transfer[] { DND_TRANSFER },
				adapter);
	}

	/**
	 * Move the given selection from the source viewer to the destination viewer
	 * and update model lists accordingly.
	 *
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
	private boolean move(final TableViewer fromViewer, final List<E> fromList,
			final TableViewer toViewer, final List<E> toList) {
		// selection
		final IStructuredSelection selection = fromViewer
				.getStructuredSelection();
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
	 * Moves the target viewer selection to the source viewer and update model
	 * lists accordingly.
	 *
	 * @return <code>true</code> if one or more elements has been moved.
	 */
	private boolean moveToSourceViewer() {
		return move(targetViewer, targetList, sourceViewer, sourceList);
	}

	/**
	 * Moves the source viewer selection to the target viewer and update model
	 * lists accordingly.
	 *
	 * @return <code>true</code> if one or more elements has been moved.
	 */
	private boolean moveToTargetViewer() {
		return move(sourceViewer, sourceList, targetViewer, targetList);
	}
}
