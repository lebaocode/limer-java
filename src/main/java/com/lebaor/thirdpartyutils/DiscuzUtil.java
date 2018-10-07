package com.lebaor.thirdpartyutils;

import java.net.URLEncoder;
import java.util.HashMap;

import net.sf.json.JSONObject;

import com.lebaor.utils.TextUtil;
import com.lebaor.webutils.HttpClientUtil;

/**
 * cookiepre":"yBYe_2132_",
 * "auth":"5c37SHsM6WhMnaPURqt4X9vj340FjqxaGWUIGCN0bXSUu8dB665m3QNeLG6AR0DncbcNlY0GvHTZrrGwMria",
 * "saltkey":"p5iz055E","member_uid":"2","member_username":"lebao","member_avatar":"http:\/\/lxj.s19.myverydz.com\/uc_server\/avatar.php?uid=2&size=small","groupid":"3","formhash":"3e6f2446","
 * @author lixjl
 *
 */
public class DiscuzUtil {
//	public static final String API_URL = "http://lxj.s19.myverydz.com/source/plugin/mobile/mobile.php?version=1";
	public static final String API_URL = "http://lxj.s19.myverydz.com/api/mobile/index.php?version=4";
	public static final String USERNAME = "lebao";
	public static final String PASSWORD = "Hjkd6$3@9#o1";
	public static final String ENCODING = "UTF-8";
	
	public static DiscuzCommonVar login() {
		String url = API_URL + "&module=login&loginsubmit=yes"
				+ "&username="+ USERNAME +"&password=" + TextUtil.MD5(PASSWORD);
				
		String ret = HttpClientUtil.doGet(url);
		System.out.println(ret);
		DiscuzCommonVar var = DiscuzCommonVar.parseJson(ret);
		System.out.println(var);
		return var;
	}
	
	public static void postThread(String authorId, String title, String content, int boardId, int typeId, DiscuzCommonVar var) {
		try {
			String url = API_URL + "&module=newthread&fid="+ boardId +"&topicsubmit=yes"
					+ "&formhash=" + var.getFormHash()  
					+ "&typeid=" + typeId;
//			System.out.println(url);
			String params = "subject="+ URLEncoder.encode(title, ENCODING) +"&message=" + URLEncoder.encode(content, ENCODING);
//			url += "&"+params;
//			String ret = HttpClientUtil.doGet(url);
			HashMap<String, String> headers = new HashMap<String, String>();
			String cookie = var.genCookieString();
//			System.out.println(cookie);
			headers.put("Cookie", var.genCookieString());
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
			String ret = HttpClientUtil.doPost(url, params, headers);
			System.out.println("ret="+ret);
			DiscuzCommonVar retVar = DiscuzCommonVar.parseJson(ret);
			System.out.println(retVar);
		} catch (Exception e) {
			
		}
	}
	
	public static void main(String[] args) {
		DiscuzCommonVar var = login();
		String content = "content";
		postThread("", "title", "", 2,  3, var);
	}
	
	public static class DiscuzCommonVar {
		String cookiePre;
		String auth;
		String saltKey;
		String memUid;
		String formHash;
		boolean success;
		String errorMsg;
		
		public static DiscuzCommonVar parseJson(String json) {
			DiscuzCommonVar var = new DiscuzCommonVar();
			try{
				
				JSONObject o = JSONObject.fromObject(json).getJSONObject("Message");
				String msgVal = o.getString("messageval").toLowerCase();
				if (msgVal.indexOf("succeed") != -1 || msgVal.indexOf("success") != -1) {
					o = JSONObject.fromObject(json).getJSONObject("Variables");
					var.setAuth(o.getString("auth"));
					var.setCookiePre(o.getString("cookiepre"));
					var.setSaltKey(o.getString("saltkey"));
					var.setMemUid(o.getString("member_uid"));
					var.setFormHash(o.getString("formhash"));
					var.setSuccess(true);
					return var;
				} else {
					var.setSuccess(false);
					var.setErrorMsg(o.getString("messagestr"));
					return var;
				}
			} catch (Exception e) {
				var.setSuccess(false);
				var.setErrorMsg(e.getMessage());
			}
			return var;
		}
		
		public String toString() {
			if (success) {
				return "success=true&cookiepre="+ cookiePre + "&saltkey=" + saltKey +"&formhash=" + formHash + "&mem_uid=" + memUid + "&auth=" + auth;
			} else {
				return "success=false&errormsg=" + errorMsg;
			}
		}
		
		public String genCookieString() {
			try {
				return cookiePre + "auth=" +URLEncoder.encode(auth, ENCODING) + "; " 
					+ cookiePre + "saltkey=" + URLEncoder.encode(saltKey, ENCODING);
			} catch (Exception e) {
				return "";
			}
		}
		
		public boolean isSuccess() {
			return success;
		}

		public void setSuccess(boolean success) {
			this.success = success;
		}

		public String getErrorMsg() {
			return errorMsg;
		}

		public void setErrorMsg(String errorMsg) {
			this.errorMsg = errorMsg;
		}

		public String getCookiePre() {
			return cookiePre;
		}
		public void setCookiePre(String cookiePre) {
			this.cookiePre = cookiePre;
		}
		public String getAuth() {
			return auth;
		}
		public void setAuth(String auth) {
			this.auth = auth;
		}
		public String getSaltKey() {
			return saltKey;
		}
		public void setSaltKey(String saltKey) {
			this.saltKey = saltKey;
		}
		public String getMemUid() {
			return memUid;
		}
		public void setMemUid(String memUid) {
			this.memUid = memUid;
		}
		public String getFormHash() {
			return formHash;
		}
		public void setFormHash(String formHash) {
			this.formHash = formHash;
		}
		
		
	}
}
