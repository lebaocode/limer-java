package com.lebaor.wx.msg;

import com.lebaor.wx.data.WxReqMsg;
import com.lebaor.wx.data.WxRespMsg;
import com.lebaor.wx.data.WxRespMsg.WxRespTextMsg;

/**
 * 返回输入的消息
 * @author Administrator
 *
 */
public class WxMsgEchoHandler implements WxMsgHandler{
	public WxRespMsg handleReqMsg(WxReqMsg reqMsg){
		if (reqMsg == null) return null;
		
		String content = "您的消息已收到：\n"
				+  reqMsg.getContent() + "\n\n"
				+ "服务正在开发中~";
		WxRespTextMsg resp = new WxRespTextMsg(reqMsg.getFromUserName(), reqMsg.getToUserName(), content);
		return resp;
	}
}
