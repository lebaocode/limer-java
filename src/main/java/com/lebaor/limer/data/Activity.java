package com.lebaor.limer.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class Activity {
	long id;
	String title;
	String subTitle;
	String desc;
	int status;
	
	long startTime;
	long endTime;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("title", title);
			o.put("subTitle", subTitle);
			o.put("desc", desc);
			o.put("status", status);
			o.put("startTime", Long.toString(startTime));
			o.put("endTime", Long.toString(endTime));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static Activity parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static Activity parseJSON(JSONObject o) {
		try {
			Activity n = new Activity();
			n.id = JSONUtil.getLong(o, "id");
			n.title = JSONUtil.getString(o, "title");
			n.subTitle = JSONUtil.getString(o, "subTitle");
			n.desc = JSONUtil.getString(o, "desc");
			n.status = JSONUtil.getInt(o, "status");
			n.startTime = JSONUtil.getLong(o, "startTime");
			n.endTime = JSONUtil.getLong(o, "endTime");
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
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	
}
