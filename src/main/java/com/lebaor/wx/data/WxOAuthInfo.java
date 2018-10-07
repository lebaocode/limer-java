package com.lebaor.wx.data;

import net.sf.json.JSONObject;


public class WxOAuthInfo {
	public static final String SESSION_NAME = "auth_info";
	
	String openId;
	String accessToken;
	String refreshToken;
	long tokenExpiredTime;
	WxUserInfo userInfo;
	
	int expiresIn;//过期时长 7200s
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public long getTokenExpiredTime() {
		return tokenExpiredTime;
	}
	public void setTokenExpiredTime(long tokenExpiredTime) {
		this.tokenExpiredTime = tokenExpiredTime;
	}
	public WxUserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(WxUserInfo userInfo) {
		this.userInfo = userInfo;
	}
	
	public int getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}
	public WxOAuthInfo(String json) {
		JSONObject o = JSONObject.fromObject(json);
		this.accessToken = o.getString("access_token");
		this.expiresIn = o.getInt("expires_in");
		this.refreshToken = o.getString("refresh_token");
		this.openId = o.getString("openid");
		//String scope = o.getString("scope");
		if (this.expiresIn > 0) {
			this.tokenExpiredTime = System.currentTimeMillis() + expiresIn*1000L - 10000L;
		} else {
			this.tokenExpiredTime = o.getLong("expired_time");
		}
		
		String userInfoStr = o.getString("user_info");
		this.userInfo = new WxUserInfo(userInfoStr);
	}
	public String toJson() {
		JSONObject o = new JSONObject();
		putIntoJson(o, "access_token", this.accessToken);
		putIntoJson(o, "refresh_token", this.refreshToken);
		putIntoJson(o, "expires_in", this.expiresIn);
		putIntoJson(o, "expired_time", this.tokenExpiredTime);
		putIntoJson(o, "openid", this.openId);
		putIntoJson(o, "user_info", this.userInfo == null ? null : this.userInfo.toJson());
		return o.toString();
	}
	
	public String toString() {
		return toJson();
	}
	
	private void putIntoJson(JSONObject o, String key, Object value) {
		if (value != null) {
			o.put(key, value);
		}
	}
	
	public WxOAuthInfo() {
		
	}
}
