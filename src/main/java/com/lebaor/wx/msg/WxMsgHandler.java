package com.lebaor.wx.msg;

import com.lebaor.wx.data.WxReqMsg;
import com.lebaor.wx.data.WxRespMsg;

public interface WxMsgHandler {
	/**
	 * 根据用户输入的消息，返回消息
	 * @param reqMsg
	 * @return
	 */
	public WxRespMsg handleReqMsg(WxReqMsg reqMsg);
}
