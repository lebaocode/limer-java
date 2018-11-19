package com.lebaor.limer.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class Child {
	long id;
	String childName;
	String birthday;
	int sex;
	int relation;//孩子和家长的关系：妈妈、爸爸这些
	long parentUserId;
	String extraInfo;
	long createTime;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("childName", childName);
			o.put("birthday", birthday);
			o.put("sex", sex);
			o.put("relation", relation);
			o.put("parentUserId", Long.toString(parentUserId));
			o.put("extraInfo", extraInfo);
			o.put("createTime", Long.toString(createTime));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static Child parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static Child parseJSON(JSONObject o) {
		try {
			Child n = new Child();
			n.id = JSONUtil.getLong(o, "id");
			n.childName = JSONUtil.getString(o, "childName");
			n.birthday = JSONUtil.getString(o, "birthday");
			n.sex = JSONUtil.getInt(o, "sex");
			n.relation = JSONUtil.getInt(o, "relation");
			n.parentUserId = JSONUtil.getLong(o, "parentUserId");
			n.extraInfo = JSONUtil.getString(o, "extraInfo");
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
	public String getChildName() {
		return childName;
	}
	public void setChildName(String childName) {
		this.childName = childName;
	}
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public int getRelation() {
		return relation;
	}
	public void setRelation(int relation) {
		this.relation = relation;
	}
	public long getParentUserId() {
		return parentUserId;
	}
	public void setParentUserId(long parentUserId) {
		this.parentUserId = parentUserId;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	
	
}
