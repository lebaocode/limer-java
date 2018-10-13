package com.lebaor.limer.data;

import org.json.JSONObject;

/**
 * 书籍本身信息在Book里，这里只包含本产品相关的书籍信息，比如书籍内部编号，由谁捐赠等。
 * 同一本书（isbn相同）有可能被多人捐赠。
 * 本表包含：
 * 1. 每本书由谁捐赠，每人捐赠哪些书
 * 
 * 一本被捐赠的书，在此表里应只有一条记录。
 * @author lixjl
 *
 */
public class LimerBookInfo {
	
	long id;
	String isbn;
	long donateUserId;//捐赠人id
	int status;
	long donateTime;//捐赠时间
	long lastUpdateTime;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", id);
			o.put("isbn", isbn);
			o.put("donateUserId", donateUserId);
			o.put("status", status);
			o.put("donateTime", donateTime);
			o.put("lastUpdateTime", lastUpdateTime);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static LimerBookInfo parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static LimerBookInfo parseJSON(JSONObject o) {
		try {
			LimerBookInfo n = new LimerBookInfo();
			n.id = o.getLong("id");
			n.isbn = o.getString("isbn");
			n.donateUserId = o.getLong("donateUserId");
			n.status = o.getInt("status");
			n.donateTime = o.getLong("donateTime");
			n.lastUpdateTime = o.getLong("lastUpdateTime");
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
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
	public long getDonateUserId() {
		return donateUserId;
	}
	public void setDonateUserId(long donateUserId) {
		this.donateUserId = donateUserId;
	}
	public long getDonateTime() {
		return donateTime;
	}
	public void setDonateTime(long donateTime) {
		this.donateTime = donateTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	
	
}
