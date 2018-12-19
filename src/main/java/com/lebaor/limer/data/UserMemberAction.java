package com.lebaor.limer.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

//会员信息
public class UserMemberAction {
	long id;
	long userId;//哪个用户
	int actionType;// 动作类型
	int priceFen;//金额
	long actionTime;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("userId", Long.toString(userId));
			o.put("actionType", actionType);
			o.put("priceFen", priceFen);
			o.put("actionTime", Long.toString(actionTime));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static UserMemberAction parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static UserMemberAction parseJSON(JSONObject o) {
		try {
			UserMemberAction n = new UserMemberAction();
			n.id = JSONUtil.getLong(o, "id");
			n.userId = JSONUtil.getLong(o, "userId");
			n.actionType = JSONUtil.getInt(o, "actionType");
			n.priceFen = JSONUtil.getInt(o, "priceFen");
			n.actionTime = JSONUtil.getLong(o, "actionTime");
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
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public int getActionType() {
		return actionType;
	}
	public void setActionType(int actionType) {
		this.actionType = actionType;
	}
	public int getPriceFen() {
		return priceFen;
	}
	public void setPriceFen(int priceFen) {
		this.priceFen = priceFen;
	}
	public long getActionTime() {
		return actionTime;
	}
	public void setActionTime(long actionTime) {
		this.actionTime = actionTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
}
