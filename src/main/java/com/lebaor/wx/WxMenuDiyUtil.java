package com.lebaor.wx;

import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;
import com.lebaor.wx.data.WxError;
import com.lebaor.wx.data.WxMenu;

/**
 * 设置自定义菜单
 * 
 * @author Administrator
 *
 */
public class WxMenuDiyUtil {
	private static String POST_URL = "https://api.weixin.qq.com/cgi-bin/menu/create";
	private static String POST_PARAM = "access_token";
		
	public static void setMenu(WxMenu[] menus) {
		String accessToken = WxAccessTokenUtil.getAccessTokenWithRetry();
		setMenu(menus, accessToken);
		
	}
	
	public static void setMenu(WxMenu[] menus, String accessToken) {
		
		String url = POST_URL + "?" +POST_PARAM + "=" + accessToken;
		String params = WxMenu.toString(menus);
		
		String s = HttpClientUtil.doPost(url, params);
		boolean result = WxError.isSuccess(s);
		System.out.println(result);
		LogUtil.WEB_LOG.info("setmenu " + result);
	}
	
	public static void main(String[] args) throws Exception {
		WxAccessTokenUtil a = new WxAccessTokenUtil();
		a.start();
		
		Thread.sleep(6000);
		
		//WxMenu m1 = new WxMenu(true, "点我互动", "CLICK_MENU_1");
//		WxMenu m1 = new WxMenu(false, "一年级语文辅导", "http://www.limer.com.cn/fudao/listuserbooks");
//		WxMenu m2 = new WxMenu(false, "填字游戏卡", "http://mp.weixin.qq.com/bizmall/mallshelf?id=&t=mall/list&biz=MzI0ODczNzM5MA==&shelf_id=1&showwxpaytitle=1#wechat_redirect");
		
//		WxMenu m2 = new WxMenu(false, "错题本", "http://www.limer.com.cn/fudao/viewreview");
//		WxMenu m3 = new WxMenu(false, "下载APP", "http://www.limer.com.cn/download");
		//WxMenu m4 = new WxMenu("父菜单", new WxMenu[] {m1, m3});
		
//		WxMenu m1 = new WxMenu(false, "土著说", "http://www.limer.com.cn/mq/index");
//		WxMenu m3 = new WxMenu(false, "最近访问", "http://www.limer.com.cn/mq/recentplaces");
//		WxMenu m21 = new WxMenu(false, "我的提问", "http://www.limer.com.cn/mq/listmyquestions");
//		WxMenu m22 = new WxMenu(false, "我回答的问题", "http://www.limer.com.cn/mq/listmyreplys");
//		WxMenu m23 = new WxMenu(false, "待回答的问题", "http://www.limer.com.cn/mq/listmyneedreplys");
//		WxMenu m24 = new WxMenu(false, "我的消费和收益", "http://www.limer.com.cn/mq/listmyfees");
//		WxMenu m2 = new WxMenu("我的", new WxMenu[] {m24, m23, m22, m21});
//		WxMenuDiyUtil.setMenu(new WxMenu[]{m1, m3, m2});
		WxMenuDiyUtil.setMenu(new WxMenu[]{});
		
		Thread.sleep(3000);
		a.stop();
		System.out.println("end");
	}
	
}
