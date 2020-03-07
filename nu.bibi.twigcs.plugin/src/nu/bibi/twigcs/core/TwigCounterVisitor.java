/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * Resource visitor to count Twig (*.twig) files.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigCounterVisitor extends AbstractResouceVisitor {

	/*
	 * the visited files
	 */
	private int files = 0;

	/**
	 * Gets the number of visited files.
	 *
	 * @return the number of files.
	 */
	public int getFiles() {
		return files;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean doVisit(final IResource resource) throws CoreException {
		if (TwigcsValidationVisitor.isTwigFile(resource)) {
			files++;
		}
		return true;
	}
}
