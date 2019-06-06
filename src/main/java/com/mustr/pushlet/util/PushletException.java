// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.util;

/**
 * Generic exception wrapper.
 *
 * @author Just van den Broecke
 * @version $Id: PushletException.java,v 1.1 2005/02/15 15:14:34 justb Exp $
 */
public class PushletException extends Exception {
    private static final long serialVersionUID = -8948026380367934196L;

	public PushletException(String aMessage, Throwable t) {
		super(aMessage + "\n embedded exception=" + t.toString());
	}

	public PushletException(String aMessage) {
		super(aMessage);
	}

	public PushletException(Throwable t) {
		this("PushletException: ", t);
	}

	public String toString() {
		return "PushletException: " + getMessage();
	}
}
