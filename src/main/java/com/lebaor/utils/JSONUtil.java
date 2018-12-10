package com.lebaor.utils;

import org.json.JSONArray;
import org.json.JSONObject;

public class JSONUtil {
	public static String getString(JSONObject o, String key) {
		try {
			return o.getString(key);
		} catch (Exception e) {
			return "";
		}
	}
	
	public static int getStringInt(JSONObject o, String key) {
		try {
			String s = o.getString(key);
			return Integer.parseInt(s);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static float getStringFloat(JSONObject o, String key) {
		try {
			String s = o.getString(key);
			return Float.parseFloat(s);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static JSONArray getJSONArray(JSONObject o, String key) {
		try {
			return o.getJSONArray(key);
		} catch (Exception e) {
			return new JSONArray();
		}
	}
	
	public static JSONObject getJSONObject(JSONObject o, String key) {
		try {
			return o.getJSONObject(key);
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	
	public static int getInt(JSONObject o, String key) {
		try {
			return o.getInt(key);
		} catch (Exception e) {
			return 0;
		}
	}
	
	public static boolean getBoolean(JSONObject o, String key) {
		try {
			return o.getBoolean(key);
		} catch (Exception e) {
			return false;
		}
	}
	
	public static long getLong(JSONObject o, String key) {
		try {
			return Long.parseLong(o.getString(key));
		} catch (Exception e) {
			return 0L;
		}
	}
	
	public static void putLong(JSONObject o, String key, long value) {
		try {
			o.put(key, Long.toString(value));
		} catch (Exception e) {
			
		}
	}
}
