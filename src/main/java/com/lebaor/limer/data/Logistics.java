package com.lebaor.limer.data;

import org.json.JSONObject;

/**
 * 物流信息，每一个物流订单是一条记录
 * @author lixjl
 *
 */
public class Logistics {
	long id;
	
	long limerBookId;//具体哪本书
	long borrowRecordId;//哪次借阅相关的物流
	String goodsName;//物流商品名称
	
	long fromUserId;
	String fromUserName;
	String fromUserAddress;
	String fromMobile;
	
	long toUserId;
	String toUserName;
	String toUserAddress;
	String toMobile;
	
	int status;//订单状态
	String statusDesc;
	
	int price;//运费（分）
	
	int logisCompany;//物流公司
	String logisOrderId;//物流公司订单编号
	
	long createTime;
	long lastUpdateTime;
	String orderDetail;//订单详细流程
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", id);
			o.put("limerBookId", limerBookId);
			o.put("borrowRecordId", borrowRecordId);
			o.put("goodsName", goodsName);
			o.put("fromUserId", fromUserId);
			o.put("fromUserName", fromUserName);
			o.put("fromUserAddress", fromUserAddress);
			o.put("fromMobile", fromMobile);
			o.put("toUserId", toUserId);
			o.put("toUserName", toUserName);
			o.put("toUserAddress", toUserAddress);
			o.put("toMobile", toMobile);
			o.put("status", status);
			o.put("statusDesc", statusDesc);
			o.put("price", price);
			o.put("logisCompany", logisCompany);
			o.put("logisOrderId", logisOrderId);
			o.put("createTime", createTime);
			o.put("lastUpdateTime", lastUpdateTime);
			o.put("orderDetail", orderDetail);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static Logistics parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static Logistics parseJSON(JSONObject o) {
		try {
			Logistics n = new Logistics();
			n.id = o.getLong("id");
			n.limerBookId = o.getLong("limerBookId");
			n.borrowRecordId = o.getLong("borrowRecordId");
			n.goodsName = o.getString("goodsName");
			n.fromUserId = o.getLong("fromUserId");
			n.fromUserName = o.getString("fromUserName");
			n.fromUserAddress = o.getString("fromUserAddress");
			n.fromMobile = o.getString("fromMobile");
			n.toUserId = o.getLong("toUserId");
			n.toUserName = o.getString("toUserName");
			n.toUserAddress = o.getString("toUserAddress");
			n.toMobile = o.getString("toMobile");
			n.status = o.getInt("status");
			n.statusDesc = o.getString("statusDesc");
			n.price = o.getInt("price");
			n.logisCompany = o.getInt("logisCompany");
			n.logisOrderId = o.getString("logisOrderId");
			n.createTime = o.getLong("createTime");
			n.lastUpdateTime = o.getLong("lastUpdateTime");
			n.orderDetail = o.getString("orderDetail");
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
	}
	
	public long getBorrowRecordId() {
		return borrowRecordId;
	}
	public void setBorrowRecordId(long borrowRecordId) {
		this.borrowRecordId = borrowRecordId;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getLimerBookId() {
		return limerBookId;
	}
	public void setLimerBookId(long limerBookId) {
		this.limerBookId = limerBookId;
	}
	public String getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}
	public long getFromUserId() {
		return fromUserId;
	}
	public void setFromUserId(long fromUserId) {
		this.fromUserId = fromUserId;
	}
	public String getFromUserName() {
		return fromUserName;
	}
	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}
	public String getFromUserAddress() {
		return fromUserAddress;
	}
	public void setFromUserAddress(String fromUserAddress) {
		this.fromUserAddress = fromUserAddress;
	}
	public String getFromMobile() {
		return fromMobile;
	}
	public void setFromMobile(String fromMobile) {
		this.fromMobile = fromMobile;
	}
	public long getToUserId() {
		return toUserId;
	}
	public void setToUserId(long toUserId) {
		this.toUserId = toUserId;
	}
	public String getToUserName() {
		return toUserName;
	}
	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}
	public String getToUserAddress() {
		return toUserAddress;
	}
	public void setToUserAddress(String toUserAddress) {
		this.toUserAddress = toUserAddress;
	}
	public String getToMobile() {
		return toMobile;
	}
	public void setToMobile(String toMobile) {
		this.toMobile = toMobile;
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
	public int getLogisCompany() {
		return logisCompany;
	}
	public void setLogisCompany(int logisCompany) {
		this.logisCompany = logisCompany;
	}
	public String getLogisOrderId() {
		return logisOrderId;
	}
	public void setLogisOrderId(String logisOrderId) {
		this.logisOrderId = logisOrderId;
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
	public String getOrderDetail() {
		return orderDetail;
	}
	public void setOrderDetail(String orderDetail) {
		this.orderDetail = orderDetail;
	}
	
	
}
