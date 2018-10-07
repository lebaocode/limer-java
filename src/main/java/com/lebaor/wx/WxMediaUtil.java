package com.lebaor.wx;

import net.sf.json.JSONObject;

import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;
import com.lebaor.wx.data.WxError;
import com.lebaor.wx.data.WxUserInfo;

/**
 * 素材管理
 * @author lixjl
 *
 */
public class WxMediaUtil {
	public static byte[] downloadImage(String mediaId) {
		String accessToken = WxAccessTokenUtil.getAccessTokenWithRetry();
		
		String url = "https://api.weixin.qq.com/cgi-bin/media/get?access_token="+ accessToken 
				+"&media_id="+ mediaId;
		byte[] arr = HttpClientUtil.download(url);
		return arr;
		
	}
}
