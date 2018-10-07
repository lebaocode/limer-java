package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.limer.data.User;

public class WebUser {
	long id;
	String userName;
	String userLogo;
	String mobile;
	String address;
	String email;
	
	int sex;
	String birthday;
	String province;
	String city;
	String district;
	
	int status;
	
	long createTime;
	long lastUpdateTime;
	long lastLoginTime;
	
	String unionId;
	
	String sessionKey;
	
	public static WebUser create(User u, String unionId, String sessionKey) {
		String ujson = u.toJSON();
				
		return parseJSON(ujson.substring(0, ujson.length() - 1) +", 'sessionKey': '" + sessionKey + "', 'unionId': '" + unionId + "'}");
	}
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", id);
			o.put("userName", userName);
			o.put("userLogo", userLogo);
			o.put("mobile", mobile);
			o.put("address", address);
			o.put("email", email);
			o.put("sex", sex);
			o.put("birthday", birthday);
			o.put("province", province);
			o.put("city", city);
			o.put("district", district);
			o.put("status", status);
			o.put("createTime", createTime);
			o.put("lastUpdateTime", lastUpdateTime);
			o.put("lastLoginTime", lastLoginTime);
			o.put("unionId", unionId);
			o.put("sessionKey", sessionKey);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebUser parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebUser parseJSON(JSONObject o) {
		try {
			WebUser n = new WebUser();
			n.id = o.getLong("id");
			n.userName = o.getString("userName");
			n.userLogo = o.getString("userLogo");
			n.mobile = o.getString("mobile");
			n.address = o.getString("address");
			n.email = o.getString("email");
			n.sex = o.getInt("sex");
			n.birthday = o.getString("birthday");
			n.province = o.getString("province");
			n.city = o.getString("city");
			n.district = o.getString("district");
			n.status = o.getInt("status");
			n.createTime = o.getLong("createTime");
			n.lastUpdateTime = o.getLong("lastUpdateTime");
			n.lastLoginTime = o.getLong("lastLoginTime");
			n.unionId = o.getString("unionId");
			n.sessionKey = o.getString("sessionKey");
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
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserLogo() {
		return userLogo;
	}
	public void setUserLogo(String userLogo) {
		this.userLogo = userLogo;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public long getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getSessionKey() {
		return sessionKey;
	}

	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	
}
