package com.lebaor.wx;

import java.net.URLEncoder;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;
import com.lebaor.wx.data.WxUserInfo;

/**
 * 得到所有关注者信息
 * https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN&next_openid=NEXT_OPENID
 * @author Administrator
 *
 */
public class WxGetFollowersUtil {
	
	public static void getAllFollowers(String accessToken) {
		
	}
	
	public static String getFollowers(String nextOpenId, WxUserDBIf db) {
		try {
			String accessToken = WxAccessTokenUtil.getAccessToken();
			String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token="
				+ URLEncoder.encode(accessToken, "utf-8") +"&next_openid="
				+ URLEncoder.encode(nextOpenId, "utf-8");
			String json = HttpClientUtil.doGet(url);
			JSONObject o = JSONObject.fromObject(json);
			//int total = o.getInt("total");
			//int count = o.getInt("count");
			String newNextOpenId = o.getString("next_openid");
			JSONObject ids = o.getJSONObject("data");
			JSONArray arr = ids.getJSONArray("openid");
			for (int i = 0; i < arr.size(); i++) {
				String openId = arr.getString(i);
				WxUserInfo user = db.getUserByOpenId(openId);
				if (user == null) {
					boolean result = db.addUser(user);
					if (result) {
						LogUtil.STAT_LOG.info("[NEW_USER_FOLLOWED] " + openId);
					} else {
						LogUtil.WEB_LOG.warn("getFollowers addUser failed: " + openId);
					}
				}
			}
			return newNextOpenId;
			
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("getFollowers failed: ", e);
		}
		return null;
	}
}
