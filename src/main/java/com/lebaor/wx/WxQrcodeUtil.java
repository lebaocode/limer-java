package com.lebaor.wx;

import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lebaor.webutils.HttpClientUtil;

public class WxQrcodeUtil {
	/**
	 * 获得临时二维码图片地址
	 * @param param 32位非0整型，永久二维码时最大值为100000（目前参数只支持1--100000）
	 * @return
	 * @throws Exception
	 */
	public static String getTempQrcodePicUrl(int param) throws Exception {
		String accessToken = WxAccessTokenUtil.getAccessTokenWithRetry();
		
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+ URLEncoder.encode(accessToken, "utf-8");
		JSONObject o = new JSONObject();
		o.put("expire_seconds", 604800);
		o.put("action_name", "QR_SCENE");
		JSONObject p = new JSONObject();
		JSONObject s = new JSONObject();
		s.put("scene_id", param);
		p.put("scene", s);
		o.put("action_info", p);
		String json = o.toString();
		String retJson = HttpClientUtil.doPost(url, json);
		
		JSONObject ret = JSONObject.fromObject(retJson);
		String ticket = ret.getString("ticket");
		if (ticket == null) {
			throw new Exception(ret.getString("errcode") + ret.getString("errmsg"));
		}
		
		url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(ticket, "utf-8");
		return url;
	}
	
	/**
	 * 获得永久二维码图片地址
	 * @param param 字符串类型，长度限制为1到64，仅永久二维码支持此字段
	 * @return
	 * @throws Exception
	 */
	public static String getPermanentQrcodePicUrl(String param) throws Exception {
		String accessToken = WxAccessTokenUtil.getAccessTokenWithRetry();
		
		String url = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token="+ URLEncoder.encode(accessToken, "utf-8");
		JSONObject o = new JSONObject();
		o.put("action_name", "QR_LIMIT_SCENE");
		JSONObject p = new JSONObject();
		JSONObject s = new JSONObject();
		s.put("scene_str", param);
		p.put("scene", s);
		o.put("action_info", p);
		String json = o.toString();
		String retJson = HttpClientUtil.doPost(url, json);
		
		JSONObject ret = JSONObject.fromObject(retJson);
		String ticket = ret.getString("ticket");
		if (ticket == null) {
			throw new Exception(ret.getString("errcode") + ret.getString("errmsg"));
		}
		
		url = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + URLEncoder.encode(ticket, "utf-8");
		return url;
	}
}
