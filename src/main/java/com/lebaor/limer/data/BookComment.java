package com.lebaor.limer.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class BookComment {
	long id;
	String isbn;
	long userId;
	String content;
	String imgUrlsJson = "[]";
	int likeNum;//点赞数
	
	long createTime;
	long lastModifyTime;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("isbn", isbn);
			o.put("userId", Long.toString(userId));
			o.put("content", content);
			o.put("imgUrlsJson", new JSONArray(imgUrlsJson));
			o.put("likeNum", likeNum);
			o.put("createTime", Long.toString(createTime));
			o.put("lastModifyTime", Long.toString(lastModifyTime));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static BookComment parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static BookComment parseJSON(JSONObject o) {
		try {
			BookComment n = new BookComment();
			n.id = JSONUtil.getLong(o, "id");
			n.isbn = JSONUtil.getString(o, "isbn");
			n.userId = JSONUtil.getLong(o, "userId");
			n.content = JSONUtil.getString(o, "content");
			n.imgUrlsJson = JSONUtil.getJSONArray(o, "imgUrlsJson").toString();
			n.likeNum = JSONUtil.getInt(o, "likeNum");
			n.createTime = JSONUtil.getLong(o, "createTime");
			n.lastModifyTime = JSONUtil.getLong(o, "lastModifyTime");
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
	
	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getImgUrlsJson() {
		return imgUrlsJson;
	}
	public void setImgUrlsJson(String imgUrlsJson) {
		this.imgUrlsJson = imgUrlsJson;
	}
	public int getLikeNum() {
		return likeNum;
	}
	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getLastModifyTime() {
		return lastModifyTime;
	}
	public void setLastModifyTime(long lastModifyTime) {
		this.lastModifyTime = lastModifyTime;
	}
	
	
}
