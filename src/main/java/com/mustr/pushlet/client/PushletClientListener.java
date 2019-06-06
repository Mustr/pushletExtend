// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.client;

import com.mustr.pushlet.core.Event;
import com.mustr.pushlet.core.Protocol;

/**
 * Interface for listener of the PushletClient object.
 *
 * @version $Id: PushletClientListener.java,v 1.5 2005/02/21 11:50:37 justb Exp $
 * @author Just van den Broecke - Just Objects &copy;
 **/
public interface PushletClientListener extends Protocol {
	/** Abort event from server. */
	public void onAbort(Event theEvent);

	/** Data event from server. */
	public void onData(Event theEvent);

	/** Heartbeat event from server. */
	public void onHeartbeat(Event theEvent);

	/** Error occurred. */
	public void onError(String message);
}
