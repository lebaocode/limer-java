package com.lebaor.limer.web.data;

import org.json.JSONObject;

public class WebUserCenterInfo {
	int borrowNum;
	int donateNum;
	int userScore;
	long userId;
	String unionId;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("borrowNum", borrowNum);
			o.put("donateNum", donateNum);
			o.put("userScore", userScore);
			o.put("userId", userId);
			o.put("unionId", unionId);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebUserCenterInfo parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebUserCenterInfo parseJSON(JSONObject o) {
		try {
			WebUserCenterInfo n = new WebUserCenterInfo();
			n.borrowNum = o.getInt("borrowNum");
			n.donateNum = o.getInt("donateNum");
			n.userScore = o.getInt("userScore");
			n.userId = o.getLong("userId");
			n.unionId = o.getString("unionId");
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
	}
	
	public int getBorrowNum() {
		return borrowNum;
	}
	public void setBorrowNum(int borrowNum) {
		this.borrowNum = borrowNum;
	}
	public int getDonateNum() {
		return donateNum;
	}
	public void setDonateNum(int donateNum) {
		this.donateNum = donateNum;
	}
	public int getUserScore() {
		return userScore;
	}
	public void setUserScore(int userScore) {
		this.userScore = userScore;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	
	
}
