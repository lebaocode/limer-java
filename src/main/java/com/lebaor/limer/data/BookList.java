package com.lebaor.limer.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class BookList {
	long id;
	String type;
	String title;
	String subTitle;
	String desc;
	String bookIsbnsJson;
	long createTime;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("type", type);
			o.put("title", title);
			o.put("subTitle", subTitle);
			o.put("desc", desc);
			o.put("bookIsbnsJson", new JSONArray(bookIsbnsJson));
			o.put("createTime", Long.toString(createTime));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}
	
	public String[] getBookIsbns() {
		try {
			JSONArray a = new JSONArray(this.bookIsbnsJson);
			String[] isbns = new String[a.length()];
			for (int i =0;i<a.length();i++) {
				isbns[i] = a.getString(i);
			}
			return isbns;
		} catch (Exception e) {
			e.printStackTrace();
			return new String[0];
		}
	}

	public static BookList parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static BookList parseJSON(JSONObject o) {
		try {
			BookList n = new BookList();
			n.id = JSONUtil.getLong(o, "id");
			n.type = JSONUtil.getString(o, "type");
			n.title = JSONUtil.getString(o, "title");
			n.subTitle = JSONUtil.getString(o, "subTitle");
			n.desc = JSONUtil.getString(o, "desc");
			n.bookIsbnsJson = JSONUtil.getJSONArray(o, "bookIsbnsJson").toString();
			n.createTime = JSONUtil.getLong(o, "createTime");
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

	public String getBookIsbnsJson() {
		return bookIsbnsJson;
	}

	public void setBookIsbnsJson(String bookIsbnsJson) {
		this.bookIsbnsJson = bookIsbnsJson;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	
}
