package com.lebaor.limer.web.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;


public class WebBookListDetail {
	long id;
	String type;
	String title;
	String subTitle;
	String desc;
	
	JSONArray books;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("type", type);
			o.put("title", title);
			o.put("subTitle", subTitle);
			o.put("desc", desc);
			o.put("books", books);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebBookListDetail parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebBookListDetail parseJSON(JSONObject o) {
		try {
			WebBookListDetail n = new WebBookListDetail();
			n.id = JSONUtil.getLong(o, "id");
			n.type = JSONUtil.getString(o, "type");
			n.title = JSONUtil.getString(o, "title");
			n.subTitle = JSONUtil.getString(o, "subTitle");
			n.desc = JSONUtil.getString(o, "desc");
			n.books = JSONUtil.getJSONArray(o, "books");
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public JSONArray getBooks() {
		return books;
	}

	public void setBooks(JSONArray books) {
		this.books = books;
	}

	

}
