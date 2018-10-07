package com.lebaor.thirdpartyutils;

import java.util.HashMap;
import java.util.TreeMap;

import open189.sign.ParamsSign;

import net.sf.json.JSONObject;
import net.spy.memcached.internal.OperationFuture;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.webutils.HttpClientUtil;

/**
 * 短信验证码工具
 * 使用天翼开放平台服务
 *
 */
public class SmsCodeUtil {
	
	private static final String TIANYI_APPID = "519362410000039730";
	private static final String TIANYI_SECRETKEY = "913b4105bdd98d097f1e3c5044ab646b";
//	private static String accessToken = "f2ccf105e4467557faa9ec70b10e95fe1423292446318";
//	private static long tokenExpireTime = 1423811008964L;
	
	public static final String KEY_TOKEN_TIANYI = "TIANYI_ACCESS_TOKEN";
	
	private static final HashMap<String, String> HEADERS = new HashMap<String, String>(); 
	static {
		HEADERS.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
	}
	
	private static String acquireAccessToken() {
		Object cachedToken = AliyunOCS.get(KEY_TOKEN_TIANYI);
		if (cachedToken != null) {
			return (String)cachedToken;
		}
		
//		if (accessToken != null && ((System.currentTimeMillis() + 3600*1000L) < tokenExpireTime)) {
//			return accessToken;
//		}
		
		String url = "https://oauth.api.189.cn/emp/oauth2/v3/access_token";
		String param = "grant_type=client_credentials&app_id=" + TIANYI_APPID
				+ "&app_secret=" + TIANYI_SECRETKEY;
		
		String json = HttpClientUtil.doPost(url, param, HEADERS);
		JSONObject o = JSONObject.fromObject(json);
		int resCode = o.getInt("res_code");
		if (resCode == 0) {//成功
			String access_token = o.getString("access_token");
			int expiresIn = o.getInt("expires_in");//单位秒，默认为7天
//			LogUtil.WEB_LOG.debug("return access_token:" + access_token);
			
			try {
				expiresIn = Math.min(expiresIn, AliyunOCS.EXPIRE_MAX);
				if (expiresIn > 7200) expiresIn = expiresIn - 3600;
				OperationFuture<Boolean> future = AliyunOCS.set(KEY_TOKEN_TIANYI, 
						expiresIn, access_token);
				if (future != null) future.get();
			} catch (Exception e) {
				LogUtil.WEB_LOG.debug("aliyunocs set KEY_TOKEN_TIANYI error", e);
			}
			return access_token;
		} else {
			String resMsg = o.getString("res_message");
//			LogUtil.WEB_LOG.debug("acquireAccessToken failed:" + resCode +" " + resMsg);
		}
		
		return null;
	}
	
	private static String acquireToken(String accessToken) throws Exception {
		String url = "http://api.189.cn/v2/dm/randcode/token";
		String param =  //按字典序排序 
				"access_token=" + accessToken
				+ "&app_id=" + TIANYI_APPID
				+ "&timestamp=" + TextUtil.formatTime(System.currentTimeMillis())
				;
		
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		paramsMap.put("app_id", TIANYI_APPID);
		paramsMap.put("access_token", accessToken);
		paramsMap.put("timestamp", TextUtil.formatTime(System.currentTimeMillis()));
		String sign = ParamsSign.value(paramsMap, TIANYI_SECRETKEY);
		
		String json = HttpClientUtil.doPost(url, param + "&sign=" +sign, HEADERS);
		JSONObject o = JSONObject.fromObject(json);
		String token = o.getString("token");
		return token;
	}
	
	/**
	 * 向一个手机号下发短信验证码
	 * @param mobile
	 * @return
	 */
	public static String sendSmsCode(String mobile, String host) {
		try {
			
			String accessToken = acquireAccessToken();
			if (accessToken == null) return null;
			
//			LogUtil.WEB_LOG.debug("begin accuire tianyi token:");
			String token = acquireToken(accessToken);
//			LogUtil.WEB_LOG.debug("return token:" + token);
			if (token == null) return null;
			
			LogUtil.WEB_LOG.debug("begin send sms:");
			String param = "access_token=" + accessToken
					+ "&app_id=" + TIANYI_APPID
					+ "&phone=" + mobile
					+ "&timestamp=" + TextUtil.formatTime(System.currentTimeMillis())
					+ "&token=" + token
					+ "&url=" + "http://"+ host +"/json/smsreceiver"
					; //需按照字典序升序
			
			TreeMap<String, String> paramsMap = new TreeMap<String, String>();
			paramsMap.put("app_id", TIANYI_APPID);
			paramsMap.put("access_token", accessToken);
			paramsMap.put("timestamp", TextUtil.formatTime(System.currentTimeMillis()));
			paramsMap.put("phone", mobile);
			paramsMap.put("token", token);
			paramsMap.put("url", "http://"+ host +"/json/smsreceiver");
			String sign = ParamsSign.value(paramsMap, TIANYI_SECRETKEY);
			
			String url = "http://api.189.cn/v2/dm/randcode/send";
			String json = HttpClientUtil.doPost(url, param + "&sign=" + sign, HEADERS);
//			LogUtil.WEB_LOG.debug("return json:" + json);
			JSONObject o = JSONObject.fromObject(json);
			String identifier = o.getString("identifier");
			//String create_at = o.getString("create_at");
			return identifier;
		} catch (Exception e) {
			LogUtil.WEB_LOG.debug("", e);
		}
		return null;
	}
	
	public static class SmsCode {
		String mobile;
		String id;
		String code;
		long createTime;
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		public long getCreateTime() {
			return createTime;
		}
		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}
		
		
		
	}
	
	public static void main(String[] args) {
		AliyunOCS ocs = new AliyunOCS();
		
		try {
			ocs.connect();
			String id = SmsCodeUtil.sendSmsCode("18601026360", "www.limer.cn");
			System.out.println(id);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ocs.release();
		}
		
	}
}
