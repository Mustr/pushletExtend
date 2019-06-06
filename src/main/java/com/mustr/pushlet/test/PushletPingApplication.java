// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.test;

import com.mustr.pushlet.client.PushletClient;
import com.mustr.pushlet.client.PushletClientListener;
import com.mustr.pushlet.core.Event;
import com.mustr.pushlet.core.Protocol;
import com.mustr.pushlet.util.PushletException;

import java.util.HashMap;
import java.util.Map;

/**
 * Tester to demonstrate Pushlet use in Java applications.
 *
 * This program does two things:
 * (1) it subscribes to the subject "test/ping"
 * (2) it publishes an Event with subject "/test/ping" every few seconds.
 *
 * @version $Id: PushletPingApplication.java,v 1.15 2005/02/21 16:59:17 justb Exp $
 * @author Just van den Broecke - Just Objects &copy;
 **/
public class PushletPingApplication extends Thread implements PushletClientListener, Protocol {
	private PushletClient pushletClient;
	private String host;
	private int port;
	private static final String SUBJECT = "/test/ping";
	private static final long PUBLISH_INTERVAL_MILLIS = 3000;

	public PushletPingApplication(String aHost, int aPort) {
		host = aHost;
		port = aPort;
	}

	public void run() {
		// Create and start a Pushlet client; we receive callbacks
		// through onHeartbeat() and onData().
		try {
			pushletClient = new PushletClient(host, port);
			pushletClient.setDebug(true);
			pushletClient.join();
			pushletClient.listen(this, Protocol.MODE_STREAM);

			// Test subscribe/unsubscribe
			String subscriptionId = pushletClient.subscribe(SUBJECT);
			pushletClient.unsubscribe(subscriptionId);

			// The real subscribe
			pushletClient.subscribe(SUBJECT);
			p("pushletClient started");
		} catch (PushletException pe) {
			p("Error in setting up pushlet session pe=" + pe);
			return;
		}

		// Publish an event to the server every N seconds.
		Map eventData = new HashMap(2);
		int seqNr = 1;
		while (true) {
			try {
				// Create event data
				eventData.put("seqNr", "" + seqNr++);
				eventData.put("time", "" + System.currentTimeMillis());

				// POST event to pushlet server
				pushletClient.publish(SUBJECT, eventData);

				p("published ping # " + (seqNr - 1) + " - sleeping...");
				Thread.sleep(PUBLISH_INTERVAL_MILLIS);
			} catch (Exception e) {
				p("Postlet exception: " + e);
				System.exit(-1);
			}
		}
	}

	/** Error occurred. */
	public void onError(String message) {
		p(message);
	}

	/** Abort event from server. */
	public void onAbort(Event theEvent) {
		p("onAbort received: " + theEvent);
	}

	/** Data event from server. */
	public void onData(Event theEvent) {
		// Calculate round trip delay
		long then = Long.parseLong(theEvent.getField("time"));
		long delay = System.currentTimeMillis() - then;
		p("onData: ping #" + theEvent.getField("seqNr") + " in " + delay + " ms");
	}

	/** Heartbeat event from server. */
	public void onHeartbeat(Event theEvent) {
		p("onHeartbeat received: " + theEvent);
	}

	/** Generic print. */
	public void p(String s) {
		System.out.println("[PushletPing] " + s);
	}

	/** Main program. */
	public static void main(String args[]) {
		for (int i = 0; i < 1; i++) {
			if (args.length == 0) {
				new PushletPingApplication("localhost", 8080).start();
			} else {
				// Supply a host and port
				new PushletPingApplication(args[0], Integer.parseInt(args[1])).start();
			}
		}

	}
}

