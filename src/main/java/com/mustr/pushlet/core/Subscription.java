// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.core;

import com.mustr.pushlet.util.Rand;
import com.mustr.pushlet.util.PushletException;


/**
 * Represents single subject subscription
 *
 * @author Just van den Broecke - Just Objects &copy;
 * @version $Id: Subscription.java,v 1.5 2007/11/23 14:33:07 justb Exp $
 */
public class Subscription implements ConfigDefs {
	public static final int ID_SIZE = 5;
	public static final String SUBJECT_SEPARATOR = ",";
	private String id = Rand.randomName(ID_SIZE);
	private String subject;
	private String[] subjects;

	/**
	 * Optional label, a user supplied token.
	 */
	private String label;


	/**
	 * Protected constructor as we create through factory method.
	 */
	protected Subscription() {
	}

	/**
	 * Create instance through factory method.
	 *
	 * @param aSubject the subject (topic).
	 * @return a Subscription object (or derived)
	 * @throws com.mustr.pushlet.util.PushletException
	 *          exception, usually misconfiguration
	 */
	public static Subscription create(String aSubject) throws PushletException {
		return create(aSubject, null);
	}

	/**
	 * Create instance through factory method.
	 *
	 * @param aSubject the subject (topic).
	 * @param aLabel   the subject label (optional).
	 * @return a Subscription object (or derived)
	 * @throws com.mustr.pushlet.util.PushletException
	 *          exception, usually misconfiguration
	 */
	public static Subscription create(String aSubject, String aLabel) throws PushletException {
		if (aSubject == null || aSubject.length() == 0) {
			throw new IllegalArgumentException("Null or emtpy subject");
		}

		Subscription subscription;
		try {
			subscription = (Subscription) Config.getClass(SUBSCRIPTION_CLASS, "com.mustr.pushlet.core.Subscription").newInstance();
		} catch (Throwable t) {
			throw new PushletException("Cannot instantiate Subscriber from config", t);
		}

		// Init
		subscription.subject = aSubject;

		// We may subscribe to multiple subjects by separating
		// them with SUBJECT_SEPARATOR, e.g. "/stocks/aex,/system/memory,..").
		subscription.subjects = aSubject.split(SUBJECT_SEPARATOR);

		subscription.label = aLabel;
		return subscription;
	}


	public String getId() {
		return id;
	}

	public String getLabel() {
		return label;
	}

	public String getSubject() {
		return subject;
	}

	/**
	 * Determine if Event matches subscription.
	 */
	public boolean match(Event event) {
		String eventSubject = event.getSubject();

		// Silly case but check anyway
		if (eventSubject == null || eventSubject.length() == 0) {
			return false;
		}

		// Test if one of the subjects matches
		for (int i = 0; i < subjects.length; i++) {
			if (eventSubject.startsWith(subjects[i])) {
				return true;
			}
		}

		// No match
		return false;
	}
}

