package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class WebOrder {
	String orderTime;//下单时间
	int status;//状态
	String statusDesc;//状态描述
	String mchTradeNo;//商户订单号
	String wxTradeNo;//微信订单号
	int totalFee;//原价
	int realFee;//实际价格
	int deposit;//押金价格，单位分
	String productId;//产品id
	String productDesc;//产品描述
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("orderTime", orderTime);
			o.put("status", status);
			o.put("statusDesc", statusDesc);
			o.put("mchTradeNo", mchTradeNo);
			o.put("wxTradeNo", wxTradeNo);
			o.put("totalFee", totalFee);
			o.put("realFee", realFee);
			o.put("deposit", deposit);
			o.put("productId", productId);
			o.put("productDesc", productDesc);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebOrder parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebOrder parseJSON(JSONObject o) {
		try {
			WebOrder n = new WebOrder();
			n.orderTime = JSONUtil.getString(o, "orderTime");
			n.status = JSONUtil.getInt(o, "status");
			n.statusDesc = JSONUtil.getString(o, "statusDesc");
			n.mchTradeNo = JSONUtil.getString(o, "mchTradeNo");
			n.wxTradeNo = JSONUtil.getString(o, "wxTradeNo");
			n.totalFee = JSONUtil.getInt(o, "totalFee");
			n.realFee = JSONUtil.getInt(o, "realFee");
			n.deposit = JSONUtil.getInt(o, "deposit");
			n.productId = JSONUtil.getString(o, "productId");
			n.productDesc = JSONUtil.getString(o, "productDesc");
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

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public String getMchTradeNo() {
		return mchTradeNo;
	}

	public void setMchTradeNo(String mchTradeNo) {
		this.mchTradeNo = mchTradeNo;
	}

	public String getWxTradeNo() {
		return wxTradeNo;
	}

	public void setWxTradeNo(String wxTradeNo) {
		this.wxTradeNo = wxTradeNo;
	}

	public int getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

	public int getRealFee() {
		return realFee;
	}

	public void setRealFee(int realFee) {
		this.realFee = realFee;
	}

	public int getDeposit() {
		return deposit;
	}

	public void setDeposit(int deposit) {
		this.deposit = deposit;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getProductDesc() {
		return productDesc;
	}

	public void setProductDesc(String productDesc) {
		this.productDesc = productDesc;
	}
	
	
	
}
