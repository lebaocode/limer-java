package com.lebaor.wx;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.webutils.HttpClientUtil;
import com.lebaor.wx.data.WxError;
import com.lebaor.wx.data.WxOAuthInfo;
import com.lebaor.wx.data.WxUserInfo;

/**
 * 网页授权流程分为四步：
 * 1. 引导用户进入授权页面同意授权，获取code
 * 2. 通过code换取网页授权access_token（与基础支持中的access_token不同）
 * 3. 如果需要，开发者可以刷新网页授权access_token，避免过期
 * 4. 通过网页授权access_token和openid获取用户基本信息
 * @author Administrator
 *
 */
public class WxOAuthUtil {
	public static String appId = WxConstants.WX_APPID;
	public static String appSecret = WxConstants.WX_APPSECRET;
	public static final String STATE_SNSAPI_BASE = "100";
	public static final String STATE_SNSAPI_USERINFO = "101";
	
	public static final String OAUTH_STATE_PARAM = "oauth_state";
	public static final int OAUTH_USER_ALLOW = 1;
	public static final int OAUTH_USER_DENY = 2;
	
	/**
	 * 当前的code必须是从snsapi_userinfo返回的
	 * 处理code, token, userinfo
	 * @redirectUrl 响应oauth response的url
	 * @param req
	 * @param res
	 * @return
	 * @throws Exception
	 */
	public static WxOAuthInfo handleOAuth2(HttpServletRequest req,
            HttpServletResponse res) throws Exception {
		
        try {
        	//判断是否从snsapi_userinfo返回
        	String state = TextUtil.filterNull(req.getParameter("state"));
        	if (state.equalsIgnoreCase(STATE_SNSAPI_BASE)) {
        		LogUtil.WEB_LOG.debug("[OAUTH_ERROR] [IS_SNSAPI_BASE]");
        		return null;
        	} else if (!state.equalsIgnoreCase(STATE_SNSAPI_USERINFO)) {
        		//不是userinfo授权，说明是出错了，或者用户取消授权
        		//&state=&code=authdeny&reason=101
        		LogUtil.WEB_LOG.debug("[OAUTH_ERROR] [USER_DENY]");
        		return null;
        	}
        	

        	//从参数里得到code
        	String code = TextUtil.filterNull(req.getParameter("code"));
        	if (code.trim().length() == 0) {
    			//没拿到code？这应该是微信出错了
    			LogUtil.WEB_LOG.debug("[OAUTH_ERROR_USERINFO] [NO_RET_CODE]");
    			return null;
    		} 
        	
        	//用code获取access_token和openid
			WxOAuthInfo info = oauth2(code, req, res);
			String openId = info.getOpenId();
			if (openId == null) {
				//拿不到openid? 微信出错了
				LogUtil.WEB_LOG.debug("[OAUTH_ERROR_USERINFO] [AFTER_CODE_NO_OPENID]");
				return null;
			}
			
			//如果当前是snsapi_userinfo，则直接获取userinfo
			String accessToken = info.getAccessToken();
			WxUserInfo u = oauthGetUserInfo(accessToken, openId);
			if (u == null) {
				//没拿到用户信息？微信出错
				LogUtil.WEB_LOG.debug("[OAUTH_ERROR_USERINFO] [AFTER_TOKEN_NO_USER_INFO]");
				return null;
			}
			
			//拿到用户信息
			info.setUserInfo(u);
			saveAuthInfoIntoCookie(info, req, res);
			LogUtil.STAT_LOG.info("[OAUTH] [NEW_USER_VISIT] ["+ openId +"]");
			return info;
			
        } catch (Exception e) {
        	LogUtil.WEB_LOG.error("oauth error, ", e);
        }
        
        return null;
	}
	
	public static WxOAuthInfo getAuthInfoFromCookie(HttpServletRequest req) {
		Cookie[] cookies =  req.getCookies();
		if (cookies == null) return null;
		
		for (Cookie c : cookies) {
			if (c.getName().equalsIgnoreCase(WxOAuthInfo.SESSION_NAME)){
				String s = TextUtil.filterNull(c.getValue());
				try {
					s = URLDecoder.decode(s, "utf-8");
					LogUtil.WEB_LOG.debug("getAuthInfoFromCookie : " + s);
					WxOAuthInfo info = new WxOAuthInfo(s);
					return info;
				} catch (Exception e) {
					LogUtil.WEB_LOG.warn("getAuthInfoFromCookie error: " + s, e);
					return null;
				}
			}
		}
    	return null;
	}
	
	public static void saveAuthInfoIntoCookie(WxOAuthInfo info, HttpServletRequest req, HttpServletResponse res) {
		//删除旧的cookie
		Cookie old = new Cookie(WxOAuthInfo.SESSION_NAME, "");
		old.setDomain(req.getHeader("host"));
		old.setMaxAge(0);
		res.addCookie(old);
		
		//写一个新的
		try { 
			String encodeValue = URLEncoder.encode(info.toJson(), "utf-8");
			Cookie c = new Cookie(WxOAuthInfo.SESSION_NAME, encodeValue);
			c.setDomain(req.getHeader("host"));
			c.setMaxAge(365 * 24 * 3600 * 1000);//1年
			res.addCookie(c);
		} catch (Exception e) {}
		
		LogUtil.WEB_LOG.debug("save autoinfo to cookie: " + info.toJson());
	}
		
	/**
	 * 第一步：用户同意授权，获取code
	 */
	public static void oauth1UserInfo(String redirectUri,  
			HttpServletRequest req, HttpServletResponse res) throws IOException{
		
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="
				+ appId +"&redirect_uri=" + URLEncoder.encode(redirectUri, "utf-8")
				+ "&response_type=code&scope=snsapi_userinfo&state="+ STATE_SNSAPI_USERINFO +"#wechat_redirect";
		LogUtil.WEB_LOG.debug("oauth1_userinfo: " + url);
		res.sendRedirect(url);
		
	}
	
	/**
	 * 第一步：用户同意授权，获取code
	 * 用不上
	 */
	public static void oauth1Base(String redirectUri, 
			HttpServletRequest req, HttpServletResponse res) throws IOException{
		String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+ appId 
				+"&redirect_uri=" + URLEncoder.encode(redirectUri, "utf-8")
				+ "&response_type=code&scope=snsapi_base&state="+ STATE_SNSAPI_BASE +"#wechat_redirect";
		LogUtil.WEB_LOG.debug("oauth1_base: " + url);
		res.sendRedirect(url);
		
	}
	
	/**
	 * 第二步：通过code换取网页授权access_token
	 * 首先请注意，这里通过code换取的网页授权access_token,与基础支持中的access_token不同。
	 * 公众号可通过下述接口来获取网页授权access_token。如果网页授权的作用域为snsapi_base，
	 * 则本步骤中获取到网页授权access_token的同时，也获取到了openid，snsapi_base式的
	 * 网页授权流程即到此为止。
	 * 
	 * 返回json：
	 * access_token	 网页授权接口调用凭证,注意：此access_token与基础支持的access_token不同
	 * expires_in	 access_token接口调用凭证超时时间，单位（秒）
	 * refresh_token	 用户刷新access_token
	 * openid	 用户唯一标识，请注意，在未关注公众号时，用户访问公众号的网页，也会产生一个用户和公众号唯一的OpenID
	 * scope	 用户授权的作用域，使用逗号（,）分隔
	 * 
	 * @param code
	 * @param req
	 * @param res
	 * @throws IOException
	 */
	public static WxOAuthInfo oauth2(String code, 
			HttpServletRequest req, HttpServletResponse res) throws IOException{
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+ appId 
				+"&secret="+ appSecret +"&code="+ code +"&grant_type=authorization_code";
		String json = HttpClientUtil.doGet(url);
		if (WxError.isSuccess(json)) {
			JSONObject o = JSONObject.fromObject(json);
			String accessToken = o.getString("access_token");
			int expires = o.getInt("expires_in");
			String refreshToken = o.getString("refresh_token");
			String openId = o.getString("openid");
			//String scope = o.getString("scope");
			long tokenExpiredTime = System.currentTimeMillis() + expires*1000L - 10000L;
			
			WxOAuthInfo info = new WxOAuthInfo();
			info.setAccessToken(accessToken);
			info.setOpenId(openId);
			info.setRefreshToken(refreshToken);
			info.setTokenExpiredTime(tokenExpiredTime);
			info.setExpiresIn(expires);
			//info.setUserInfo(this.getUserAuthedInfo(openId));
			
			LogUtil.WEB_LOG.debug("oauth2 success! " + json);
			return info;
		} else {
			LogUtil.WEB_LOG.warn("oauth2 error: " + json);
		}
		
		return null;
	}
	

	
	/**
	 * openid	 用户的唯一标识
	 * nickname	 用户昵称
	 * sex	 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
	 * province	 用户个人资料填写的省份
	 * city	 普通用户个人资料填写的城市
	 * country	 国家，如中国为CN
	 * headimgurl	 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空
	 * privilege	 用户特权信息，json 数组，如微信沃卡用户为（chinaunicom）
	 * @param accessToken
	 * @param openId
	 */
	private static WxUserInfo oauthGetUserInfo(String accessToken, String openId) {
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+ accessToken 
				+"&openid="+ openId +"&lang=zh_CN";
		String json = HttpClientUtil.doGet(url);
		if (WxError.isSuccess(json)) {
			JSONObject o = JSONObject.fromObject(json);
			String nickName = o.getString("nickname");
			int sex = o.getInt("sex");
			String province = o.getString("province");
			String city = o.getString("city");
			String country = o.getString("country");
			String headImgUrl = o.getString("headimgurl");
			
			WxUserInfo u = new WxUserInfo();
			u.setOpenId(openId);
			u.setNickName(nickName);
			u.setSex(sex);
			u.setProvince(province);
			u.setCity(city);
			u.setCountry(country);
			u.setHeadImgUrl(headImgUrl);
			
			LogUtil.WEB_LOG.debug("oauth getuserinfo success! " + json);
			return u;
		} else {
			LogUtil.WEB_LOG.warn("oauth getuserinfo error: " + json);
		}
		return null;
	}
		
}
