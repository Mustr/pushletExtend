// Copyright (c) 2000 Just Objects B.V. <just@justobjects.nl>
// Distributable under LGPL license. See terms of license at gnu.org.

package com.mustr.pushlet.test;

import com.mustr.pushlet.core.Event;
import com.mustr.pushlet.core.EventPullSource;
import com.mustr.pushlet.core.SessionManager;
import com.mustr.pushlet.util.Rand;


/**
 * Event sources for testing.
 *
 * @author Just van den Broecke - Just Objects &copy;
 * @version $Id: TestEventPullSources.java,v 1.10 2007/11/09 13:16:57 justb Exp $
 */
public class TestEventPullSources {

	/**
	 * Produces a fake temparature event.
	 */
	static public class TemperatureEventPullSource extends EventPullSource {
		String[] cities = {"amsterdam", null, "rotterdam", null,
				"leeuwarden", null, "twente", null, "limburg", null};

		public long getSleepTime() {
			return Rand.randomLong(3000, 5000);
		}

		public Event pullEvent() {
			int cityNumber = Rand.randomInt(0, (cities.length) / 2 - 1);
			int nextCityIndex = 2 * cityNumber;

			Event event = Event.createDataEvent("/temperature");

			event.setField("number", "" + cityNumber);
			event.setField("city", cities[nextCityIndex]);
			if (cities[nextCityIndex + 1] == null) {
				cities[nextCityIndex + 1] = "" + Rand.randomInt(5, 10);
			}
			int currentCityValue = new Integer(cities[nextCityIndex + 1]).intValue();
			int newCityValue = currentCityValue + Rand.randomInt(-2, 2);

			event.setField("value", "" + newCityValue);
			return event;
		}
	}

	/**
	 * Produces a ping event.
	 */
	static public class PingEventPullSource extends EventPullSource {
		public long getSleepTime() {
			return 3000;
		}

		public Event pullEvent() {

			return Event.createDataEvent("/pushlet/ping");
		}
	}

	/**
	 * Produces an event related to the JVM status.
	 */
	static public class SystemStatusEventPullSource extends EventPullSource {
		Runtime runtime = Runtime.getRuntime();

		public long getSleepTime() {
			return 4000;
		}

		public Event pullEvent() {
			Event event = Event.createDataEvent("/system/jvm");
			event.setField("totalMemory", "" + runtime.totalMemory());
			event.setField("freeMemory", "" + runtime.freeMemory());
			event.setField("maxMemory", "" + runtime.maxMemory());
			int activeCount = Thread.activeCount();
			event.setField("threads", "" + activeCount);

			return event;
		}
	}

	/**
	 * Produces an event related to the Dispatcher.getInstance(). status.
	 */
	static public class PushletStatusEventPullSource extends EventPullSource {

		public long getSleepTime() {
			return 5000;
		}

		public Event pullEvent() {
			Event event = Event.createDataEvent("/system/pushlet");
			// p(Dispatcher.getStatus());
			event.setField("publisher", "" + SessionManager.getInstance().getStatus());
			return event;
		}
	}


	/**
	 * Produces events simulating stocks from the AEX.
	 */
	static public class AEXStocksEventPullSource extends EventPullSource {

		String[] stocks = {"abn amro", "26",
				"aegon", "38",
				"ahold", "34",
				"akzo nobel", "51",
				"asm lith h", "26",
				"corus plc", "2",
				"dsm", "40",
				"elsevier", "14",
				"fortis (nl)", "32",
				"getronics", "6",
				"gucci", "94",
				"hagemeyer", "25",
				"heineken", "61",
				"ing c", "78",
				"klm", "66",
				"kon olie", "66",
				"kpn", "13",
				"numico c", "44",
				"philips, kon", "38",
				"tnt", "26",
				"unilever c", "62",
				"vendex kbb", "16",
				"vnu", "49",
				"wolt-kluw c", "25"};

		public long getSleepTime() {
			return Rand.randomLong(2000, 4000);
		}

		public Event pullEvent() {
			Event event = Event.createDataEvent("/stocks/aex");
			int stockNumber = Rand.randomInt(0, (stocks.length) / 2 - 1);
			int nextStockIndex = 2 * stockNumber;

			event.setField("number", "" + stockNumber);
			event.setField("name", stocks[nextStockIndex]);
			if (stocks[nextStockIndex + 1] == null) {
				stocks[nextStockIndex + 1] = "" + Rand.randomInt(50, 150);
			}
			int currentStockValue = new Integer(stocks[nextStockIndex + 1]).intValue();
			int newStockValue = currentStockValue + Rand.randomInt(-2, 2);

			event.setField("rate", "" + newStockValue + "." + Rand.randomInt(0, 99));
			return event;
		}

	}

	/**
	 * Produces an URL event for automatic webpresentation.
	 */
	static public class WebPresentationEventPullSource extends EventPullSource {
		String slideRootDir = "http://www.justobjects.org/cowcatcher/browse/j2ee/slides/";
		String[] slideURLs = {
				"ejb/j2ee/ejbover/slide.0.0.html",
				"ejb/j2ee/ejbover/slide.0.1.html",
				"ejb/j2ee/ejbover/slide.0.2.html",
				"ejb/j2ee/ejbover/slide.0.3.html",
				"ejb/j2ee/ejbover/slide.0.4.html"
		};

		int nextSlideNumber = 0;

		public long getSleepTime() {
			return 5000;
		}

		public Event pullEvent() {
			Event event = Event.createDataEvent("/webpres/auto");
			event.setField("url", slideRootDir + slideURLs[nextSlideNumber++]);
			if (nextSlideNumber == slideURLs.length) {
				nextSlideNumber = 0;
			}
			// Log.debug("Sending next slide url=" + event.getField("url"));
			return event;
		}
	}

	/**
	 * Produces an event related to the Dispatcher.getInstance(). status.
	 */
	static public class TestEventPullSource extends EventPullSource {
		private int number = 0;

		public long getSleepTime() {
			return 2000;
		}

		public Event pullEvent() {
			Event event = Event.createDataEvent("/system/test");
			// p(Dispatcher.getInstance()..getStatus());
			event.setField("nr", "" + (number++));
			event.setField("time", "" + System.currentTimeMillis());
			return event;
		}

	}

	/**
	 * Util: stderr print method.
	 */
	public static void e(String s) {
		System.out.println(s);
	}

	/**
	 * Util: stdout print method.
	 */
	public static void p(String s) {
		// System.out.println(s);
	}
}

