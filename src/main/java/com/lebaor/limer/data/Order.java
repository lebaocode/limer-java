package com.lebaor.limer.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;
import com.lebaor.utils.TextUtil;

/**
 * 订单
 * @author lixjl
 *
 */
public class Order {
	long id;
	String mchTradeNo;//内部订单id
	String wxTradeNo;//微信订单id，即prepayid
	String payMethod = "weixin";//支付方式，默认都是微信支付
	
	long userId;//用户id
	String openid;
	String unionid;
	String ip;
	
	String productId;
	String extraJson;//包含depositFee
	String title;//订单名称
	int mchNum;//商品数量
	
	int totalFee;//原价 分
	int realFee;//实际支付价格 分
	String coupounId;//优惠券id
	int coupounFee;//优惠金额
	String feeType;//币种
	
	int status;

	long orderStartTime;//开始时间
	long orderFinishTime;//订单完成时间
	
	public int getDepositFee() {
		if (this.extraJson != null && this.extraJson.trim().length() > 0) {
			try {
				JSONObject o = new JSONObject(this.extraJson);
				return JSONUtil.getInt(o, "depositFee");
			}catch (Exception e) {}
		}
		
		return 0;
	}
		
	public static String genMchTradeNo(long userId) {
		String s = Long.toString(userId);
		if (s.length() >= 5) {
			s = s.substring(0, 5);
		} else {
			s = String.format("%05d", userId);
		}
		return TextUtil.formatTime2(System.currentTimeMillis()) +  s;
	}
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", id);
			o.put("mchTradeNo", mchTradeNo);
			o.put("wxTradeNo", wxTradeNo);
			o.put("payMethod", payMethod);
			o.put("productId", productId);
			o.put("userId", userId);
			o.put("openid", openid);
			o.put("unionid", unionid);
			o.put("ip", ip);
			o.put("extraJson", extraJson);
			o.put("title", title);
			o.put("bookNum", mchNum);
			o.put("totalFee", totalFee);
			o.put("realFee", realFee);
			o.put("coupounId", coupounId);
			o.put("coupounFee", coupounFee);
			o.put("feeType", feeType);
			o.put("status", status);
			o.put("orderStartTime", orderStartTime);
			o.put("orderFinishTime", orderFinishTime);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static Order parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static Order parseJSON(JSONObject o) {
		try {
			Order n = new Order();
			n.id = JSONUtil.getLong(o, "id");
			n.mchTradeNo = JSONUtil.getString(o, "mchTradeNo");
			n.wxTradeNo = JSONUtil.getString(o, "wxTradeNo");
			n.payMethod = JSONUtil.getString(o, "payMethod");
			n.productId = JSONUtil.getString(o, "productId");
			n.userId = JSONUtil.getLong(o, "userId");
			n.openid = JSONUtil.getString(o, "openid");
			n.unionid = JSONUtil.getString(o, "unionid");
			n.ip = JSONUtil.getString(o, "ip");
			n.extraJson = JSONUtil.getString(o, "extraJson");
			n.title = JSONUtil.getString(o, "title");
			n.mchNum = JSONUtil.getInt(o, "bookNum");
			n.totalFee = JSONUtil.getInt(o, "totalFee");
			n.realFee = JSONUtil.getInt(o, "realFee");
			n.coupounId = JSONUtil.getString(o, "coupounId");
			n.coupounFee = JSONUtil.getInt(o, "coupounFee");
			n.feeType = JSONUtil.getString(o, "feeType");
			n.status = JSONUtil.getInt(o, "status");
			n.orderStartTime = JSONUtil.getLong(o, "orderStartTime");
			n.orderFinishTime = JSONUtil.getLong(o, "orderFinishTime");
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
	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getExtraJson() {
		return extraJson;
	}
	public void setExtraJson(String extraJson) {
		this.extraJson = extraJson;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getMchNum() {
		return mchNum;
	}
	public void setMchNum(int mchNum) {
		this.mchNum = mchNum;
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
	public String getCoupounId() {
		return coupounId;
	}
	public void setCoupounId(String coupounId) {
		this.coupounId = coupounId;
	}
	public int getCoupounFee() {
		return coupounFee;
	}
	public void setCoupounFee(int coupounFee) {
		this.coupounFee = coupounFee;
	}
	public String getFeeType() {
		return feeType;
	}
	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getOrderStartTime() {
		return orderStartTime;
	}
	public void setOrderStartTime(long orderStartTime) {
		this.orderStartTime = orderStartTime;
	}
	public long getOrderFinishTime() {
		return orderFinishTime;
	}
	public void setOrderFinishTime(long orderFinishTime) {
		this.orderFinishTime = orderFinishTime;
	}

}
