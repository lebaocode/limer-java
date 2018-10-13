package com.lebaor.limer.web.data;

import org.json.JSONObject;

public class WebJSONObject {
	boolean success;
	String msg;
	String result; //json
	public static String DEFAULT_DATA = "{}";
	public static JSONObject DEFAULT_DATA_OBJECT = new JSONObject();
	
	public WebJSONObject() {}
	
	public WebJSONObject(boolean success, String msg, String result) {
		super();
		this.success = success;
		this.msg = msg;
		this.result = result;
	}
	
	public WebJSONObject(boolean success, String msg) {
		super();
		this.success = success;
		this.msg = msg;
		this.result = DEFAULT_DATA;
	}
	
	public WebJSONObject(String result) {
		super();
		this.success = true;
		this.msg = "";
		this.result = result;
	}

	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("success", success);
			o.put("msg", msg);
			o.put("data", new JSONObject(result));
			return o.toString();
		} catch (Exception e) {
			try {
				JSONObject o = new JSONObject();
				o.put("success", success);
				o.put("msg", msg);
				o.put("data", DEFAULT_DATA_OBJECT);
				return o.toString();
			} catch (Exception ex) {
				return "{}";
			}
		}
		
	}
	
	public JSONObject toJSONObject() {
		try {
			return new JSONObject(toJSON());
		} catch (Exception e) {
			return new JSONObject();
		}
	}

	public static WebJSONObject parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebJSONObject parseJSON(JSONObject o) {
		try {
			WebJSONObject n = new WebJSONObject();
			n.success = o.getBoolean("success");
			n.msg = o.getString("msg");
			n.result = o.getJSONObject("data").toString();
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	
}
