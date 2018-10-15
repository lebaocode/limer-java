package com.lebaor.limer.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class User {
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
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
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
			o.put("createTime", Long.toString(createTime));
			o.put("lastUpdateTime", Long.toString(lastUpdateTime));
			o.put("lastLoginTime", Long.toString(lastLoginTime));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static User parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static User parseJSON(JSONObject o) {
		try {
			User n = new User();
			n.id = JSONUtil.getLong(o, "id");
			n.userName = JSONUtil.getString(o, "userName");
			n.userLogo = JSONUtil.getString(o, "userLogo");
			n.mobile = JSONUtil.getString(o, "mobile");
			n.address = JSONUtil.getString(o, "address");
			n.email = JSONUtil.getString(o, "email");
			n.sex = JSONUtil.getInt(o, "sex");
			n.birthday = JSONUtil.getString(o, "birthday");
			n.province = JSONUtil.getString(o, "province");
			n.city = JSONUtil.getString(o, "city");
			n.district = JSONUtil.getString(o, "district");
			n.status = JSONUtil.getInt(o, "status");
			n.createTime = JSONUtil.getLong(o, "createTime");
			n.lastUpdateTime = JSONUtil.getLong(o, "lastUpdateTime");
			n.lastLoginTime = JSONUtil.getLong(o, "lastLoginTime");
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
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
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getLastLoginTime() {
		return lastLoginTime;
	}
	public void setLastLoginTime(long lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
