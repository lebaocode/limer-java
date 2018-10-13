package com.lebaor.wx.msg;

import java.util.HashMap;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lebaor.webutils.HttpClientUtil;
import com.lebaor.wx.WxAccessTokenUtil;
import com.lebaor.wx.data.WxError;

/**
 * 给指定用户发消息（走客服接口）
 * 如果超过48小时，则返回：
 * {
    "errcode": 45015, 
    "errmsg": "response out of time limit"
}
 * 如果发送成功，会返回ok
 * {
    "errcode": 0, 
    "errmsg": "ok"
} 
 * @author Administrator
 *
 */
public class WxMsgUtil {
	private final static String SINGLE_POST_URL = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
	private final static String MASS_POST_URL = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=";
	
	
	public static boolean sendTextMsgToUser(String openId, String msg) {
		String accessToken = WxAccessTokenUtil.getAccessTokenWithRetry();
		JSONObject o = new JSONObject();
		o.put("touser", openId);
		o.put("msgtype", "text");
		JSONObject content = new JSONObject();
		content.put("content", msg);
		o.put("text", content);
		
		String params = o.toString();
		
		String resp = HttpClientUtil.doPost(SINGLE_POST_URL + accessToken, params, new HashMap<String, String>());
		return WxError.isSuccess(resp);
	}
	
	public static boolean sendMassTextMsg(String[] openIds, String msg) {
		String accessToken = WxAccessTokenUtil.getAccessTokenWithRetry();
		JSONObject o = new JSONObject();
		JSONArray a = new JSONArray();
		for (String openId : openIds) {
			a.add(openId);
		}
		o.put("touser", a);
		o.put("msgtype", "text");
		JSONObject content = new JSONObject();
		content.put("content", msg);
		o.put("text", content);
		
		String params = o.toString();
		
		String resp = HttpClientUtil.doPost(MASS_POST_URL + accessToken, params);
		return WxError.isSuccess(resp);
	}
}
