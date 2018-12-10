package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class WebPreOrder {
	String address;
	String receiverMobile;
	String receiverName;
	
	String mchDesc;
	int realFee;
	int totalFee;
	
	int desopitFee;//押金
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("address", address);
			o.put("receiverMobile", receiverMobile);
			o.put("receiverName", receiverName);
			o.put("mchDesc", mchDesc);
			o.put("realFee", realFee);
			o.put("totalFee", totalFee);
			o.put("desopitFee", desopitFee);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebPreOrder parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebPreOrder parseJSON(JSONObject o) {
		try {
			WebPreOrder n = new WebPreOrder();
			n.address = JSONUtil.getString(o, "address");
			n.receiverMobile = JSONUtil.getString(o, "receiverMobile");
			n.receiverName = JSONUtil.getString(o, "receiverName");
			n.mchDesc = JSONUtil.getString(o, "mchDesc");
			n.realFee = JSONUtil.getInt(o, "realFee");
			n.totalFee = JSONUtil.getInt(o, "totalFee");
			n.desopitFee = JSONUtil.getInt(o, "desopitFee");
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
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getReceiverMobile() {
		return receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getMchDesc() {
		return mchDesc;
	}

	public void setMchDesc(String mchDesc) {
		this.mchDesc = mchDesc;
	}

	public int getRealFee() {
		return realFee;
	}

	public void setRealFee(int realFee) {
		this.realFee = realFee;
	}

	public int getDesopitFee() {
		return desopitFee;
	}

	public void setDesopitFee(int desopitFee) {
		this.desopitFee = desopitFee;
	}

	public int getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}
	
	
}