package com.lebaor.limer.web.data;

import org.json.JSONArray;
import org.json.JSONObject;

public class WebBookStatus {
	long bookId;

	int status;//状态
	int inlibNum;//库存数量
	Long[] limerBookIds; //对应的具体哪几本书
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("bookId", bookId);
			o.put("status", status);
			o.put("inlibNum", inlibNum);
			
			JSONArray arr = new JSONArray();
			if (limerBookIds != null) {
				for (long bid: limerBookIds) {
					arr.put(bid);
				}
			}
			o.put("limerBookIds", arr.toString());
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebBookStatus parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebBookStatus parseJSON(JSONObject o) {
		try {
			WebBookStatus n = new WebBookStatus();
			n.bookId = o.getLong("bookId");
			n.status = o.getInt("status");
			n.inlibNum = o.getInt("inlibNum");
			JSONArray arr  = o.getJSONArray("limerBookIds");
			if (arr != null) {
				n.limerBookIds = new Long[arr.length()];
				for (int i = 0; i < arr.length(); i++) {
					n.limerBookIds[i] = arr.getLong(i);
				}
			}
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
	}
	
	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
		this.bookId = bookId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getInlibNum() {
		return inlibNum;
	}
	public void setInlibNum(int inlibNum) {
		this.inlibNum = inlibNum;
	}
	public Long[] getLimerBookIds() {
		return limerBookIds;
	}
	public void setLimerBookIds(Long[] limerBookIds) {
		this.limerBookIds = limerBookIds;
	}
	
	
	
}
