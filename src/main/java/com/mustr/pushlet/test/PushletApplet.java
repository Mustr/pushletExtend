// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.test;

import com.mustr.pushlet.client.PushletClient;
import com.mustr.pushlet.client.PushletClientListener;
import com.mustr.pushlet.core.Event;
import com.mustr.pushlet.core.Protocol;
import com.mustr.pushlet.util.PushletException;

import java.applet.Applet;
import java.awt.*;

/**
 * Tester for applet clients; displays incoming events in text area.
 *
 * @version $Id: PushletApplet.java,v 1.16 2005/02/18 09:54:15 justb Exp $
 * @author Just van den Broecke - Just Objects &copy;
 **/
public class PushletApplet extends Applet implements PushletClientListener, Protocol {
	private TextArea textArea;
	private String host = "localhost";
	private int port = 8080;
	private String subject;
	private PushletClient pushletClient;
	private String VERSION = "15.feb.05 #5";
	private String PUSH_MODE = Protocol.MODE_PULL;

	/** One-time setup. */
	public void init() {
		// Subject to subscribe to
		subject = getParameter(P_SUBJECT);

		host = getDocumentBase().getHost();
		port = getDocumentBase().getPort();

		// Hmm sometimes this value is -1...(Mozilla with Java 1.3.0 on Win)
		if (port == -1) {
			port = 80;
		}

		setLayout(new GridLayout(1, 1));
		textArea = new TextArea(15, 40);
		textArea.setForeground(Color.yellow);
		textArea.setBackground(Color.gray);
		textArea.setEditable(false);
		add(textArea);
		p("PushletApplet - " + VERSION);
	}

	public void start() {
		dbg("start()");
		bailout();

		try {
			pushletClient = new PushletClient(host, port);
			p("Created PushletClient");

			pushletClient.join();
			p("Joined server");

			pushletClient.listen(this, PUSH_MODE);
			p("Listening in mode=" + PUSH_MODE);

			pushletClient.subscribe(subject);
			p("Subscribed to=" + subject);
		} catch (PushletException pe) {
			p("Error exception=" + pe);
			bailout();
		}
	}

	public void stop() {
		dbg("stop()");
		bailout();
	}

	/** Abort event from server. */
	public void onAbort(Event theEvent) {
		p(theEvent.toXML());
		bailout();
	}

	/** Data event from server. */
	public void onData(Event theEvent) {
		p(theEvent.toXML());
	}

	/** Heartbeat event from server. */
	public void onHeartbeat(Event theEvent) {
		p(theEvent.toXML());
	}

	/** Error occurred. */
	public void onError(String message) {
		p(message);
		bailout();
	}

	private void bailout() {
		if (pushletClient != null) {
			p("Stopping PushletClient");
			try {
				pushletClient.leave();
			} catch (PushletException ignore) {
				p("Error during leave pe=" + ignore);

			}
			pushletClient = null;
		}
	}

	/** Generic print. */
	private void p(String s) {
		dbg("event: " + s);
		synchronized (textArea) {
			textArea.append(s + "\n");
		}
	}

	/** Generic print. */
	private void dbg(String s) {
		System.out.println("[PushletApplet] " + s);
	}
}

