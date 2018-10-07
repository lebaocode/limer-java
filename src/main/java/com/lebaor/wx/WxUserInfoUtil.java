package com.lebaor.wx;

import java.net.URLEncoder;

import com.lebaor.webutils.HttpClientUtil;
import com.lebaor.wx.data.WxUserInfo;

/**
 * 获取用户信息
 * @author Administrator
 *
 */
public class WxUserInfoUtil {
	
	public static WxUserInfo getUserInfoByOpenId(String openId) {
		String accessToken = WxAccessTokenUtil.getAccessTokenWithRetry();
		return getUserInfoByOpenId(openId, accessToken);
	}
	
	private static WxUserInfo getUserInfoByOpenId(String openId, String accessToken) {
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token="
					+ URLEncoder.encode(accessToken, "utf-8")
					+"&openid="+ URLEncoder.encode(openId, "utf-8") +"&lang=zh_CN";
			String s = HttpClientUtil.doGet(url);
			WxUserInfo u = new WxUserInfo(s);
			return u;
		} catch(Exception e) {
			
		}
		return null;
	}
}
