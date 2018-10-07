package com.lebaor.limer.data;

import org.json.JSONObject;

/**
 * 第三方相关信息
 * @author lixjl
 *
 */
public class UserAuth {
	long id;
	long userId;
	String appId;
	String openId;
	String unionId;
	String extraInfo;
	long createTime;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", id);
			o.put("userId", userId);
			o.put("appId", appId);
			o.put("openId", openId);
			o.put("unionId", unionId);
			o.put("extraInfo", extraInfo);
			o.put("createTime", createTime);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static UserAuth parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static UserAuth parseJSON(JSONObject o) {
		try {
			UserAuth n = new UserAuth();
			n.id = o.getLong("id");
			n.userId = o.getLong("userId");
			n.appId = o.getString("appId");
			n.openId = o.getString("openId");
			n.unionId = o.getString("unionId");
			n.extraInfo = o.getString("extraInfo");
			n.createTime = o.getLong("createTime");
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
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getAppId() {
		return appId;
	}
	public void setAppId(String appId) {
		this.appId = appId;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	
	
	
}
