package com.lebaor.limer.web.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class WebBookList {
	long id;
	String title;
	String subTitle;
	JSONArray topBookCoverUrls;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("title", title);
			o.put("subTitle", subTitle);
			o.put("topBookCoverUrls", topBookCoverUrls);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebBookList parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebBookList parseJSON(JSONObject o) {
		try {
			WebBookList n = new WebBookList();
			n.id = JSONUtil.getLong(o, "id");
			n.title = JSONUtil.getString(o, "title");
			n.subTitle = JSONUtil.getString(o, "subTitle");
			n.topBookCoverUrls = JSONUtil.getJSONArray(o, "topBookCoverUrls");
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
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public JSONArray getTopBookCoverUrls() {
		return topBookCoverUrls;
	}
	public void setTopBookCoverUrls(JSONArray topBookCoverUrls) {
		this.topBookCoverUrls = topBookCoverUrls;
	}
	
	
}
