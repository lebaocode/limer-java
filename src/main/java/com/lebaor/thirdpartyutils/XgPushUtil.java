package com.lebaor.thirdpartyutils;

import java.util.HashMap;

import org.json.JSONObject;

import com.lebaor.utils.LogUtil;
import com.tencent.xinge.MessageIOS;
import com.tencent.xinge.XingeApp;

public class XgPushUtil {
	private static final long APP_ID = 2200248739L;
	private static final String APP_SECRET = "b569241518b34e15523199facffdb006";
	
	public static boolean pushMsgToOne(String token, String msg) {
		if (token ==null || token.trim().length() == 0 || msg == null || msg.trim().length() == 0) return false;
		
		try {
			JSONObject o = XingeApp.pushTokenIos(APP_ID, APP_SECRET, msg, token, XingeApp.IOSENV_PROD);
			int code = o.getInt("ret_code");
			if (code == 0) {
				LogUtil.info("push_to_one success: " + token + " " + msg);
				return true;
			} else {
				String err = o.getString("err_msg");
				LogUtil.info("push_to_one failed: " + token + " " + msg + " "+ err);
				return false;
			}
		} catch (Exception e) {
			LogUtil.info("push_to_one failed: " + token + " " + msg + " "+ e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean pushMsgToAll(String msg) {
		try {
			JSONObject o = XingeApp.pushAllIos(APP_ID, APP_SECRET, msg, XingeApp.IOSENV_PROD);
			int code = o.getInt("ret_code");
			if (code == 0) {
				JSONObject ret = o.getJSONObject("result");
				String pushId = ret != null ? ret.getString("push_id") : null;
				LogUtil.info("push_to_all success: " + msg + " push_id: " + pushId);
				return true;
			} else {
				String err = o.getString("err_msg");
				LogUtil.info("push_to_all failed: " + msg + " "+ err);
				return false;
			}
		} catch (Exception e) {
			LogUtil.info("push_to_all failed: " + msg + " "+ e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	//点击打开url
	public static boolean pushMsgToOne(String token, String msg, String url) {
		
		try {
			XingeApp push = new XingeApp(APP_ID, APP_SECRET);
			MessageIOS msgObj = new MessageIOS();
			msgObj.setAlert(msg);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("url", url);
			msgObj.setCustom(map);
			
			JSONObject o = push.pushSingleDevice(token, msgObj, XingeApp.IOSENV_PROD);
			int code = o.getInt("ret_code");
			if (code == 0) {
				LogUtil.info("push_to_one success: " + token + " " + url  + " " + msg);
				return true;
			} else {
				String err = o.getString("err_msg");
				LogUtil.info("push_to_one failed: " + token + " " + url + " " + msg + " "+ err);
				return false;
			}
		} catch (Exception e) {
			LogUtil.info("push_to_one failed: " + token + " " + url + " " + msg + " "+ e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean pushMsgToAll(String msg, String url) {
		
		try {
			XingeApp push = new XingeApp(APP_ID, APP_SECRET);
			MessageIOS msgObj = new MessageIOS();
			msgObj.setAlert(msg);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("url", url);
			msgObj.setCustom(map);
			
			JSONObject o = push.pushAllDevice(0, msgObj, XingeApp.IOSENV_PROD);
			int code = o.getInt("ret_code");
			if (code == 0) {
				JSONObject ret = o.getJSONObject("result");
				String pushId = ret != null ? ret.getString("push_id") : null;
				LogUtil.info("push_to_all success: " + url + " " + msg + " push_id: " + pushId);
				return true;
			} else {
				String err = o.getString("err_msg");
				LogUtil.info("push_to_all failed: " + url+" " + msg + " "+ err);
				return false;
			}
		} catch (Exception e) {
			LogUtil.info("push_to_all failed: " + url + " " + msg + " "+ e.getMessage());
			e.printStackTrace();
			return false;
		}
	}
}
