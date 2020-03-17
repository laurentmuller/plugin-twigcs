/**
 * This file is part of the twigcs-plugin package.
 *
 * (c) Laurent Muller <bibi@bibi.nu>
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package nu.bibi.twigcs.model;

/**
 * Class implementing this interface deals with command parameter.
 *
 * @author Laurent Muller
 */
public interface ICommand {

	/**
	 * Gets the command parameter value.
	 * 
	 * @return the parameter value.
	 */
	String getParameter();
}
