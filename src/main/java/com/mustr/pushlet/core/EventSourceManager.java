// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.core;

import com.mustr.pushlet.util.Log;
import com.mustr.pushlet.util.Sys;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.io.File;

/**
 * Maintains lifecycle of event sources.
 *
 * @author Just van den Broecke - Just Objects &copy;
 * @version $Id: EventSourceManager.java,v 1.14 2007/11/10 13:44:02 justb Exp $
 */
public class EventSourceManager {
	private static Vector<EventSource> eventSources = new Vector<EventSource>(0);
	private static final String PROPERTIES_FILE = "sources.properties";

	/**
	 * Initialize event sources from properties file.
	 */
	public static void start(String aDirPath) {
		// Load Event sources using properties file.
		Log.info("EventSourceManager: start");

		Properties properties = null;

		try {
			properties = Sys.loadPropertiesResource(PROPERTIES_FILE);
		} catch (Throwable t) {
			// Try from provided dir (e.g. WEB_INF/pushlet.properties)
			String filePath = aDirPath + File.separator + PROPERTIES_FILE;
			Log.info("EventSourceManager: cannot load " + PROPERTIES_FILE + " from classpath, will try from " + filePath);

			try {
				properties = Sys.loadPropertiesFile(filePath);
			} catch (Throwable t2) {
				Log.fatal("EventSourceManager: cannot load properties file from " + filePath, t);

				// Give up
				Log.warn("EventSourceManager: not starting local event sources (maybe that is what you want)");
				return;
			}
		}

		// Create event source collection
		eventSources = new Vector<EventSource>(properties.size());

		// Add the configured sources
		for (Enumeration<?> e = properties.keys(); e.hasMoreElements();) {
			String nextKey = (String) e.nextElement();
			String nextClass = properties.getProperty(nextKey);
			EventSource nextEventSource = null;
			try {
				nextEventSource = (EventSource) Class.forName(nextClass).newInstance();
				Log.info("created EventSource: key=" + nextKey + " class=" + nextClass);
				eventSources.addElement(nextEventSource);
			} catch (Exception ex) {
				Log.warn("Cannot create EventSource: class=" + nextClass, ex);
			}
		}

		activate();
	}

	/**
	 * Activate all event sources.
	 */
	public static void activate() {
		Log.info("Activating " + eventSources.size() + " EventSources");
		for (int i = 0; i < eventSources.size(); i++) {
			((EventSource) eventSources.elementAt(i)).activate();
		}
		Log.info("EventSources activated");
	}

	/**
	 * Deactivate all event sources.
	 */
	public static void passivate() {
		Log.info("Passivating " + eventSources.size() + " EventSources");
		for (int i = 0; i < eventSources.size(); i++) {
			((EventSource) eventSources.elementAt(i)).passivate();
		}
		Log.info("EventSources passivated");
	}

	/**
	 * Halt event sources.
	 */
	public static void stop() {
		Log.info("Stopping " + eventSources.size() + " EventSources...");
		for (int i = 0; i < eventSources.size(); i++) {
			((EventSource) eventSources.elementAt(i)).stop();
		}
		Log.info("EventSources stopped");
	}

}
