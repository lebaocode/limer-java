package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class WebPayParam {
	String timestamp;
	String prepayId;
	String nonstr;
	String paySign;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("timestamp", timestamp);
			o.put("prepayId", prepayId);
			o.put("nonstr", nonstr);
			o.put("paySign", paySign);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebPayParam parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebPayParam parseJSON(JSONObject o) {
		try {
			WebPayParam n = new WebPayParam();
			n.timestamp = JSONUtil.getString(o, "timestamp");
			n.prepayId = JSONUtil.getString(o, "prepayId");
			n.nonstr = JSONUtil.getString(o, "nonstr");
			n.paySign = JSONUtil.getString(o, "paySign");
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
	}


	public JSONObject toJSONObject() {
		try {
			return new JSONObject(toJSON());
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getPrepayId() {
		return prepayId;
	}
	public void setPrepayId(String prepayId) {
		this.prepayId = prepayId;
	}
	public String getNonstr() {
		return nonstr;
	}
	public void setNonstr(String nonstr) {
		this.nonstr = nonstr;
	}
	public String getPaySign() {
		return paySign;
	}
	public void setPaySign(String paySign) {
		this.paySign = paySign;
	}
	
	
}
