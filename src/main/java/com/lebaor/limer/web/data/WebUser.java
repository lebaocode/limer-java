package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.limer.data.User;

public class WebUser {
	User user;
	
	String unionId;
	String openId;
	
	public static WebUser create(User u, String unionId, String openId) {
		String ujson = u.toJSON();
				
		return parseJSON(ujson.substring(0, ujson.length() - 1) +", 'openId': '" + openId + "', 'unionId': '" + unionId + "'}");
	}
	
	public long getUserId() {
		return user != null ? user.getId() : 0;
	}
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject(user.toJSON());
			o.put("unionId", unionId);
			o.put("openId", openId);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebUser parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}

	public static WebUser parseJSON(JSONObject o) {
		try {
			WebUser n = new WebUser();
			n.user = User.parseJSON(o);
			n.unionId = o.getString("unionId");
			n.openId = o.getString("openId");
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
	}
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	
	
}
