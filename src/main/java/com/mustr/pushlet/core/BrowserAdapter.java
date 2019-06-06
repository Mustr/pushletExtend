// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.core;

import com.mustr.pushlet.util.Log;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * Generic implementation of ClientAdapter for browser clients.
 *
 * @author Just van den Broecke - Just Objects &copy;
 * @version $Id: BrowserAdapter.java,v 1.6 2007/11/09 13:15:35 justb Exp $
 */
public class BrowserAdapter implements ClientAdapter, Protocol {

	public static final String START_DOCUMENT =
			"<html><head><meta http-equiv=\"Pragma\" content=\"no-cache\"><meta http-equiv=\"Expires\" content=\"Tue, 31 Dec 1997 23:59:59 GMT\"></head>"
					+ "<body>"
					+ "\n<script language=\"JavaScript\"> var url=\" \"; \nfunction refresh() { document.location.href=url; }</script>";
	public static final String END_DOCUMENT = "</body></html>";

	private PrintWriter servletOut;
	private HttpServletResponse servletRsp;
	private int bytesSent;

	/**
	 * Constructor.
	 */
	public BrowserAdapter(HttpServletResponse aServletResponse) {
		servletRsp = aServletResponse;
	}

	/**
	 * Generic init.
	 */
	public void start() throws IOException {
		// Keep servlet request/response objects until page ends in stop()
		// Content type as HTML
		servletRsp.setStatus(HttpServletResponse.SC_OK);
		servletRsp.setContentType("text/html;charset=UTF-8");

		// http://www.junlu.com/msg/45902.html
		// Log.debug("bufsize=" + aRsp.getBufferSize());
		servletOut = servletRsp.getWriter();
		send(START_DOCUMENT);
	}

	/**
	 * Push Event to client.
	 */
	public void push(Event anEvent) throws IOException {
		Log.debug("BCA event=" + anEvent.toXML());

		// Check if we should refresh
		if (anEvent.getEventType().equals(Protocol.E_REFRESH)) {
			// Append refresh and tail of HTML document
			// Construct the JS callback line to be sent as last line of doc.
			// This will refresh the request using the unique id to determine
			// the subscriber instance on the server. The client will wait for
			// a number of milliseconds.
			long refreshWaitMillis = Long.parseLong(anEvent.getField(P_WAIT));

			// Create servlet request for requesting next events (refresh)
			String url = anEvent.getField(P_URL);
			String jsRefreshTrigger = "\n<script language=\"JavaScript\">url=\'" + url + "\';\n setTimeout(\"refresh()\", " + refreshWaitMillis + ");\n</script>";


			send(jsRefreshTrigger + END_DOCUMENT);
		} else {
			send(event2JavaScript(anEvent));
		}
	}

	/**
	 * End HTML page in client browser.
	 */
	public void stop() {
		// To be garbage collected if adapter remains active
		servletOut = null;
	}

	/**
	 * Send any string to browser.
	 */
	protected void send(String s) throws IOException {
		// Send string to browser.
		// Log.debug("Adapter: sending: " + s);
		if (servletOut == null) {
			throw new IOException("Client adapter was stopped");
		}

		servletOut.print(s);

		servletOut.flush();

		// Note: this doesn't seem to have effect
		// in Tomcat 4/5 if the client already disconnected.
		servletRsp.flushBuffer();

		bytesSent += s.length();
		Log.debug("bytesSent= " + bytesSent);
		// Log.debug("BCA sent event: " + s);
	}

	/**
	 * Converts the Java Event to a JavaScript function call in browser page.
	 */
	protected String event2JavaScript(Event event) throws IOException {

		// Convert the event to a comma-separated string.
		String jsArgs = "";
		for (Iterator iter = event.getFieldNames(); iter.hasNext();) {
			String name = (String) iter.next();
			String value = event.getField(name);
			String nextArgument = (jsArgs.equals("") ? "" : ",") + "'" + name + "'" + ", \"" + value + "\"";
			jsArgs += nextArgument;
		}

		// Construct and return the function call */
		return "<script language=\"JavaScript\">parent.push(" + jsArgs + ");</script>";
	}

}

