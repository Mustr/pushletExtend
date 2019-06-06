// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.core;

import com.mustr.pushlet.util.Log;
import com.mustr.pushlet.util.PushletException;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Routes Events to Subscribers.
 *
 * @author Just van den Broecke - Just Objects &copy;
 * @version $Id: Dispatcher.java,v 1.9 2007/12/04 13:55:53 justb Exp $
 */
public class Dispatcher implements Protocol, ConfigDefs {
	/**
	 * Singleton pattern:  single instance.
	 */
	private static Dispatcher instance;
	protected SessionManagerVisitor sessionManagerVisitor;

	static {
		try {
			instance = (Dispatcher) Config.getClass(DISPATCHER_CLASS, "com.mustr.pushlet.core.Dispatcher").newInstance();
			Log.info("Dispatcher created className=" + instance.getClass());
		} catch (Throwable t) {
			Log.fatal("Cannot instantiate Dispatcher from config", t);
		}
	}

	/**
	 * Singleton pattern with factory method: protected constructor.
	 */
	protected Dispatcher() {

	}

	/**
	 * Singleton pattern: get single instance.
	 */
	public static Dispatcher getInstance() {
		return instance;
	}

	/**
	 * Send event to all subscribers.
	 */
	public synchronized void broadcast(Event anEvent) {
		try {
			// Let the SessionManager loop through Sessions, calling
			// our Visitor Method for each Session. This is done to guard
			// synchronization with SessionManager and to optimize by
			// not getting an array of all sessions.
			Object[] args = new Object[2];
			args[1] = anEvent;
			Method method = sessionManagerVisitor.getMethod("visitBroadcast");
			SessionManager.getInstance().apply(sessionManagerVisitor, method, args);
		} catch (Throwable t) {
			Log.error("Error calling SessionManager.apply: ", t);
		}
	}

	/**
	 * Send event to subscribers matching Event subject.
	 */
	public synchronized void multicast(Event anEvent) {
		try {
			// Let the SessionManager loop through Sessions, calling
			// our Visitor Method for each Session. This is done to guard
			// synchronization with SessionManager and to optimize by
			// not getting an array of all sessions.
			Method method = sessionManagerVisitor.getMethod("visitMulticast");
			Object[] args = new Object[2];
			args[1] = anEvent;
			SessionManager.getInstance().apply(sessionManagerVisitor, method, args);
		} catch (Throwable t) {
			Log.error("Error calling SessionManager.apply: ", t);
		}
	}


	/**
	 * Send event to specific subscriber.
	 * ��ָ����sessionId�������ݡ�
	 */
	public synchronized void unicast(Event event, String aSessionId) {
		// Get subscriber to send event to
		Session session = SessionManager.getInstance().getSession(aSessionId);
		if (session == null) {
			Log.warn("unicast: session with id=" + aSessionId + " does not exist");
			return;
		}

		// Send Event to subscriber.
		session.getSubscriber().onEvent((Event) event.clone());
	}

	/**
	 * Start Dispatcher.
	 */
	public void start() throws PushletException {
		Log.info("Dispatcher started");

		// Create callback for SessionManager visits.
		sessionManagerVisitor = new SessionManagerVisitor();
	}

	/**
	 * Stop Dispatcher.
	 */
	public void stop() {
		// Send abort control event to all subscribers.
		Log.info("Dispatcher stopped: broadcast abort to all subscribers");
		broadcast(new Event(E_ABORT));
	}

	/**
	 * Supplies Visitor methods for callbacks from SessionManager.
	 */
	private class SessionManagerVisitor {
		private final Map<String, Method> visitorMethods = new HashMap<String, Method>(2);

		SessionManagerVisitor() throws PushletException {

			try {
				// Setup Visitor Methods for callback from SessionManager
				// This is a slight opitmization over creating Method objects
				// on each invokation.
				Class<?>[] argsClasses = {Session.class, Event.class};
				visitorMethods.put("visitMulticast", this.getClass().getMethod("visitMulticast", argsClasses));
				visitorMethods.put("visitBroadcast", this.getClass().getMethod("visitBroadcast", argsClasses));
			} catch (NoSuchMethodException e) {
				throw new PushletException("Failed to setup SessionManagerVisitor", e);
			}
		}

		/**
		 * Return Visitor Method by name.
		 */
		public Method getMethod(String aName) {
			return (Method) visitorMethods.get(aName);

		}

		/**
		 * Visitor method called by SessionManager.
		 */
		public void visitBroadcast(Session aSession, Event event) {
			aSession.getSubscriber().onEvent((Event) event.clone());
		}

		/**
		 * Visitor method called by SessionManager.
		 */
		public void visitMulticast(Session aSession, Event event) {
			Subscriber subscriber = aSession.getSubscriber();
			Event clonedEvent;
			Subscription subscription;

			// Send only if the subscriber's criteria
			// match the event.
			if ((subscription = subscriber.match(event)) != null) {
				// Personalize event
				clonedEvent = (Event) event.clone();

				// Set subscription id and optional label
				clonedEvent.setField(P_SUBSCRIPTION_ID, subscription.getId());
				if (subscription.getLabel() != null) {
					event.setField(P_SUBSCRIPTION_LABEL, subscription.getLabel());
				}

				subscriber.onEvent(clonedEvent);
			}
		}
	}
}

