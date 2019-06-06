// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.core;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Implementation of ClientAdapter that sends Events as serialized objects.
 * <p/>
 * NOTE: You are discouraged to use this adapter, since it is Java-only
 * and may have JVM-specific problems. Far better choice is to use XML
 * and the XMLAdapter.
 *
 * @author Just van den Broecke - Just Objects &copy;
 * @version $Id: SerializedAdapter.java,v 1.4 2007/11/23 14:33:07 justb Exp $
 */
class SerializedAdapter implements ClientAdapter {
	private ObjectOutputStream out = null;
	public static final String CONTENT_TYPE = "application/x-java-serialized-object";
	private HttpServletResponse servletRsp;

	/**
	 * Initialize.
	 */
	public SerializedAdapter(HttpServletResponse aServletResponse) {
		servletRsp = aServletResponse;
	}

	public void start() throws IOException {

		servletRsp.setContentType(CONTENT_TYPE);

		// Use a serialized object output stream
		out = new ObjectOutputStream(servletRsp.getOutputStream());

		// Don't need this further
		servletRsp = null;
	}

	/**
	 * Push Event to client.
	 */
	public void push(Event anEvent) throws IOException {
		out.writeObject(anEvent);

		out.flush();
	}


	public void stop() throws IOException {
	}
}

