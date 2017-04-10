package com.ford.caseiterator;

import java.util.Hashtable;
import java.util.Map;

/**
 * Used to pass custom objects (not implementing Serializable/Parcelable
 * interfaces) between activities within one application.
 * 
 * http://stackoverflow.com/questions/2736389/how-to-pass-object-from-one-activity-to-another-in-android/11120538#11120538
 * 
 * @author enikolsky
 * 
 */
public class IntentHelper {

	private static IntentHelper sInstance;
	private Map<String, Object> mMap;

	private IntentHelper() {
		mMap = new Hashtable<String, Object>();
	}

	private static IntentHelper getInstance() {
		if (sInstance == null) {
			sInstance = new IntentHelper();
		}
		return sInstance;
	}

	public static void addObjectForKey(Object obj, String key) {
		getInstance().mMap.put(key, obj);
	}

	public static Object getObjectForKey(String key) {
		return getInstance().mMap.get(key);
	}

    public static boolean containsKey(String key) {
        return getInstance().mMap.containsKey(key);
    }

	public static void removeObjectForKey(String key) {
		getInstance().mMap.remove(key);
	}
}