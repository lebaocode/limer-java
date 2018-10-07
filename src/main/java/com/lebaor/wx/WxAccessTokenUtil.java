package com.lebaor.wx;

import net.sf.json.JSONObject;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.webutils.HttpClientUtil;

/**
 * 微信规定一天最多取2000次，有效期2小时。
 * 在线程启动时，就去取ACCESS_TOKEN，每隔半小时，重新取一次ACCESS_TOKEN。
 * 如果当前取失败了，那么每隔5秒重试一次。
 * 
 * 在线程里取ACCESS_TOKEN，主要目的是为不重复取，或被多线程重复取，也可避免想得到
 * ACCESS_TOKEN时被阻塞。
 * 
 * 需要用ACCESS_TOKEN时，只需要调用 AccessTokenUtil.getAccessToken() 即可。
 * 
 * 注：需要保证环境初始化时，就启动线程。
 * 
 * @author Administrator
 *
 */
public class WxAccessTokenUtil implements Runnable {
	private static final long INTERVAL = 1800000; //7200s有效期
	
	private static final String RES_JSON_KEY = "access_token";
	private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
	private static final String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";
	private static final String JSAPI_RES_JSON_KEY = "ticket";
	
	private static String ACCESS_TOKEN = null;
	private static String JSAPI_TICKET = null;
	private long lastGetTime = 0;
	private Thread t;
	private boolean running = false;
	
	public static String getAccessToken() {
		return ACCESS_TOKEN;
	}
	
	public static String genSign(String url) {
		if (url.indexOf("#") != -1) {
			//去掉#后边的部分
			url = url.substring(0, url.indexOf("#"));
		}
		String concat = "jsapi_ticket=" + getJsApiTicketWithRetry() + "&noncestr=" + WxConstants.WX_NONCESTR
				+ "&timestamp=" + System.currentTimeMillis()/1000 + "&url=" + url;
		return TextUtil.SHA1(concat);
	}
	
	public static String genPaySign(String url, String prepayId) {
		if (url.indexOf("#") != -1) {
			//去掉#后边的部分
			url = url.substring(0, url.indexOf("#"));
		}
		String concat = "jsapi_ticket=" + getJsApiTicketWithRetry() 
				+"&appId=" + WxConstants.WX_APPID 
				+ "&noncestr=" + WxConstants.WX_NONCESTR
				+ "&package=prepay_id=" + prepayId 
				+ "&signType=MD5"
				+ "&timeStamp=" + System.currentTimeMillis()/1000 + "&url=" + url;
		return TextUtil.SHA1(concat);
	}
	
	public static String getAccessTokenWithRetry() {
		String accessToken = getAccessToken();
		int num = 0;
		while (accessToken == null && num < 5) {
			try {
				Thread.sleep(3000);
				accessToken = getAccessToken();
			} catch (Exception e) {
				break;
			}
			num++;
		}
		return accessToken;
	}
	
	public static String getJsApiTicket() {
		return JSAPI_TICKET;
	}
	
	public static String getJsApiTicketWithRetry() {
		String ticket = getJsApiTicket();
		int num = 0;
		while (ticket == null && num < 5) {
			try {
				Thread.sleep(3000);
				ticket = getJsApiTicket();
			} catch (Exception e) {
				break;
			}
			num++;
		}
		return ticket;
	}
	
	private void setJsApiTicket() {
		String accessToken = getAccessTokenWithRetry();
		if (accessToken == null) return;
		
		String params = "?access_token="+ accessToken +"&type=jsapi";
		String resp = null;
		try {
			resp = HttpClientUtil.doGet(JSAPI_TICKET_URL + params);
			if (resp == null)  {
				LogUtil.WEB_LOG.warn("setJsApiTicket error, " + resp);
				return;
			}
			
			JSONObject o = JSONObject.fromObject(resp);
			String ticket = o.getString(JSAPI_RES_JSON_KEY);
			if (TextUtil.isEmpty(ticket)) {
				LogUtil.WEB_LOG.warn("setJsApiTicket error, " + resp);
			} else {
				JSAPI_TICKET = ticket;
//				lastGetTime = System.currentTimeMillis();
				LogUtil.WEB_LOG.info("setJsApiTicket success! " + JSAPI_TICKET);
			}
		} catch (Throwable e) {
			LogUtil.WEB_LOG.warn("setJsApiTicket error: " + resp, e);
		}
	}
	
	private void setAccessToken(String appId, String appSecret) {
		String params = "?grant_type=client_credential&appid="+ appId +"&secret=" + appSecret;
		String resp = null;
		try {
			resp = HttpClientUtil.doGet(ACCESS_TOKEN_URL + params);
			if (resp == null)  {
				LogUtil.WEB_LOG.warn("setAccessToken error, " + resp);
				return;
			}
			
			JSONObject o = JSONObject.fromObject(resp);
			String token = o.getString(RES_JSON_KEY);
			if (TextUtil.isEmpty(token)) {
				LogUtil.WEB_LOG.warn("setAccessToken error, " + resp);
			} else {
				ACCESS_TOKEN = token;
				lastGetTime = System.currentTimeMillis();
				LogUtil.WEB_LOG.info("setAccessToken success! " + ACCESS_TOKEN);
			}
		} catch (Throwable e) {
			LogUtil.WEB_LOG.warn("setAccessToken error: " + resp, e);
		}
		
	}
	
	
	
	public void start() {
		t = new Thread(this, "setAccessTokenThread");
		t.setDaemon(true);
		running = true;
		t.start();
	}
	
	public void stop() {
		running = false;
	}
	
	public void run () {
		while (running) {
			try {
				//如果已经有token了，且还未失效，则直接用上一次的
				if (ACCESS_TOKEN == null || System.currentTimeMillis() - lastGetTime > INTERVAL) {
					setAccessToken(WxConstants.WX_APPID, WxConstants.WX_APPSECRET);
					setJsApiTicket();
				}
				
				Thread.sleep(5000);
				
			} catch (Throwable e) {
				
			}
		}
	}
}
