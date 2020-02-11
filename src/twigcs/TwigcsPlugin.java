/******************************************************************************
 * Copyright (c) 2020 HaslerRail AG. All rights reserved.
 *
 * This computer code is protected by copyright law and international
 * treaties. Unauthorised reproduction or distribution of this code, or
 * any portion of it, may result in severe civil and criminal penalties,
 * and will be prosecuted to the maximum extent possible under the law.
 * All additional information on our web site: http://www.haslerrail.com
 *
 * Contributors:
 *     Laurent Muller - initial API and implementation
 *******************************************************************************/
package twigcs;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The Twigcs Plugin.
 *
 * @author Laurent Muller
 * @version 1.0
 */
public class TwigcsPlugin extends AbstractUIPlugin {

	/*
	 * The shared instance
	 */
	private static TwigcsPlugin plugin;

	/**
	 * Gets the shared instance.
	 *
	 * @return the shared instance.
	 */
	public static TwigcsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Creates a new instance of this class.
	 */
	public TwigcsPlugin() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}
}
