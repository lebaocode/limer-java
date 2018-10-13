package com.lebaor.limer.data;

import org.json.JSONObject;

import com.lebaor.utils.TextUtil;

/**
 * 订单
 * @author lixjl
 *
 */
public class Order {
	long id;
	String mchTradeNo;//内部订单id
	String wxTradeNo;//微信订单id
	String payMethod = "weixin";//支付方式，默认都是微信支付
	
	long userId;//用户id
	String openid;
	String unionid;
	String ip;
	
	String limerBookIdsJson;//此次订单包含的书籍limerBookId ["3323", "23283"]
	String title;//订单名称
	int bookNum;//商品数量
	
	int totalFee;//原价 分
	int realFee;//实际支付价格 分
	String coupounId;//优惠券id
	int coupounFee;//优惠金额
	String feeType;//币种
	
	int status;

	long orderStartTime;//开始时间
	long orderFinishTime;//订单完成时间
	
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
			o.put("userId", userId);
			o.put("openid", openid);
			o.put("unionid", unionid);
			o.put("ip", ip);
			o.put("limerBookIdsJson", limerBookIdsJson);
			o.put("title", title);
			o.put("bookNum", bookNum);
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
			n.id = o.getLong("id");
			n.mchTradeNo = o.getString("mchTradeNo");
			n.wxTradeNo = o.getString("wxTradeNo");
			n.payMethod = o.getString("payMethod");
			n.userId = o.getLong("userId");
			n.openid = o.getString("openid");
			n.unionid = o.getString("unionid");
			n.ip = o.getString("ip");
			n.limerBookIdsJson = o.getString("limerBookIdsJson");
			n.title = o.getString("title");
			n.bookNum = o.getInt("bookNum");
			n.totalFee = o.getInt("totalFee");
			n.realFee = o.getInt("realFee");
			n.coupounId = o.getString("coupounId");
			n.coupounFee = o.getInt("coupounFee");
			n.feeType = o.getString("feeType");
			n.status = o.getInt("status");
			n.orderStartTime = o.getLong("orderStartTime");
			n.orderFinishTime = o.getLong("orderFinishTime");
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
	public String getLimerBookIdsJson() {
		return limerBookIdsJson;
	}
	public void setLimerBookIdsJson(String limerBookIdsJson) {
		this.limerBookIdsJson = limerBookIdsJson;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getBookNum() {
		return bookNum;
	}
	public void setBookNum(int bookNum) {
		this.bookNum = bookNum;
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