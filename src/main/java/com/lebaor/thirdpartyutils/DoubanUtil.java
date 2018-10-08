package com.lebaor.thirdpartyutils;

import javax.servlet.http.HttpUtils;

import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;

public class DoubanUtil {
	public static String getBookInfoByIsbn(String isbn) {
		String json = HttpClientUtil.doGet("https://api.douban.com/v2/book/isbn/:" + isbn);
		LogUtil.WEB_LOG.debug("DoubanUtil.getBookInfoByIsbn(" +isbn+"), return " 
				+ (json.length() > 0 ? json.substring(0, Math.min(json.length(), 100)): ""));
		return json;
	}
}
