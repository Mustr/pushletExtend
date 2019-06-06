// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.core;

import java.io.IOException;

/**
 * Adapter interface for encapsulation of specific HTTP clients.
 *
 * @author Just van den Broecke - Just Objects &copy;
 * @version $Id: ClientAdapter.java,v 1.8 2007/11/23 14:33:07 justb Exp $
 */
public interface ClientAdapter {

	/**
	 * Start event push.
	 */
	public void start() throws IOException;

	/**
	 * Push single Event to client.
	 */
	public void push(Event anEvent) throws IOException;

	/**
	 * Stop event push.
	 */
	public void stop() throws IOException;
}

