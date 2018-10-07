package com.lebaor.wx.data;

/**
 * 请求消息的处理状态
 * @author Administrator
 *
 */
public class WxReqMsgStatus {
	public static final int STATUS_HANDLING = 1;
	public static final int STATUS_DONE = 2;
	
	int status;
	long receiveTime;
	
	public WxReqMsgStatus(int status, long receiveTime) {
		super();
		this.status = status;
		this.receiveTime = receiveTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getReceiveTime() {
		return receiveTime;
	}
	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}
	
	
}
