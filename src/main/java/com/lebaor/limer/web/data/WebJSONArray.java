package com.lebaor.limer.web.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class WebJSONArray {
	boolean success;
	String msg;
	String result; //json
	
	public WebJSONArray() {}
	
	public WebJSONArray(boolean success, String msg, String result) {
		super();
		this.success = success;
		this.msg = msg;
		this.result = result;
	}
	
	public WebJSONArray(String result) {
		this.success = true;
		this.msg = "";
		this.result = result;
	}

	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("success", success);
			o.put("msg", msg);
			o.put("data", new JSONArray(result));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebJSONArray parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebJSONArray parseJSON(JSONObject o) {
		try {
			WebJSONArray n = new WebJSONArray();
			n.success = o.getBoolean("success");
			n.msg = o.getString("msg");
			n.result = o.getJSONArray("data").toString();
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
