package com.lebaor.wx;

import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import com.lebaor.webutils.HttpClientUtil;

public class WxTagUtil {
	/**
	 * 为用户打上最多三个标签。每次传入的openid列表个数不能超过50个。
	 * @param openIds
	 * @param tagId
	 */
	public static void tagUser(String[] openIds, String tagId) {
		String accessToken = WxAccessTokenUtil.getAccessTokenWithRetry();
		
		String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token="+ accessToken;
		JSONObject o = new JSONObject();
		JSONArray arr = new JSONArray();
		for (String openId : openIds) {
			arr.add(openId);
		}
		o.put("openid_list", arr);
		o.put("tagid", tagId);
		String json = o.toString();
		String ret = HttpClientUtil.doPost(url, json);
		
	}
}
