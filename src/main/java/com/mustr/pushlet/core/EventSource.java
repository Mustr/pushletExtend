// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.core;

/**
 * Abstract Event source from which Events are pulled.
 *
 * @version $Id: EventSource.java,v 1.7 2007/11/23 14:33:07 justb Exp $
 * @author Just van den Broecke - Just Objects &copy;
 **/

/**
 * Interface for specifc Event(Pull/Push)Sources.
 */
public interface EventSource {
	/**
	 * Activate the event source.
	 */
	public void activate();

	/**
	 * Deactivate the event source.
	 */
	public void passivate();

	/**
	 * Halt the event source.
	 */
	public void stop();
}

