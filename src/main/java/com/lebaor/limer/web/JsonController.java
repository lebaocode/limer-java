package com.lebaor.limer.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import com.lebaor.limer.data.LimerBookStatus;
import com.lebaor.limer.db.LimerBookStatusDB;
import com.lebaor.limer.web.data.WebBook;
import com.lebaor.limer.web.data.WebBookDetail;
import com.lebaor.limer.web.data.WebUser;
import com.lebaor.thirdpartyutils.SmsCodeUtil;
import com.lebaor.thirdpartyutils.SmsCodeUtil.SmsCode;
import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.GenericTemplateController;
import com.lebaor.wx.WxAccessTokenUtil;
import com.lebaor.wx.WxConstants;


public class JsonController extends EntryController implements Runnable {
	ConcurrentHashMap<String, SmsCode> smsCodeMap = new ConcurrentHashMap<String, SmsCode>();//key:mobile, value:smsCode
	ConcurrentHashMap<String, SmsCode> smsIdCodeMap = new ConcurrentHashMap<String, SmsCode>();//key:id, value:smsCode
	Thread t;
	
	LimerBookStatusDB lbsDB;
	
	public void init() {
		t= new Thread("clear-smscode-thread");
		t.setDaemon(true);
		t.start();
	}
	
	public void handleRequestInternal(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		
		String uri = req.getRequestURI();
		if (uri.startsWith("/json/smsreceiver")) {
			smsReceiver(req, res, model);
			return;
		} else if (uri.startsWith("/json/sendsms")) {
			sendSms(req, res, model);
			return;
		} else if (uri.startsWith("/json/verifysmscode")) {
			verifySmsCode(req, res, model);
			return;
		} else if (uri.startsWith("/json/getRecentBooks")) {//所有可借阅书籍列表
			getRecentBooks(req, res, model);
			return;
		} else if (uri.startsWith("/json/getBookDetailByIsbn")) {//所有可借阅书籍列表
			getBookDetailByIsbn(req, res, model);
			return;
		} else if (uri.startsWith("/json/wxsign")) {
			wxSign(req, res, model);
			return;
		} else if (uri.startsWith("/json/submitDonate")) {
			submitDonate(req, res, model);
			return;
		} 
	}
	
	public void submitDonate(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		
		
		//获取用户id
		String userName = this.getParameterValue(req, "userName", "");
		String userLogo = this.getParameterValue(req, "userLogo", "");
		String code = this.getParameterValue(req, "code", "");
		WebUser wu = cache.code2Session(userName, userLogo, code);
		if (wu == null) {
			LogUtil.WEB_LOG.warn("submitDonate error, no user: [code=]" + code + " [userName="+ userName +"]");
			this.setRetJson(model, "");
			return;
		}
		
		long curUserId = wu.getUserId();
		
		JSONArray resArr = new JSONArray();
		
		String isbnArrJson = this.getParameterValue(req, "isbn_arr", "");
		JSONArray arr = new JSONArray(isbnArrJson);
		for (int i = 0; i <arr.length(); i++) {
			String isbn = arr.getString(i);
			WebBookDetail wbd = cache.getDoubanBookInfo(isbn);
			if (wbd == null) continue;
			boolean success = cache.addDonateBook(wbd.getBookId(), curUserId);
			
			JSONObject ro = new JSONObject();
			ro.put("isbn", isbn);
			ro.put("result", success);
			resArr.put(ro);
		}
		
		this.setRetJson(model, resArr.toString());
	}
	
	public void getBookDetailByIsbn(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		JSONObject o = new JSONObject();
		
		String isbn = this.getParameterValue(req, "isbn", "");
		WebBookDetail wbd = cache.getDoubanBookInfo(isbn);
		if (wbd == null) {
			o.put("result", "error");
			o.put("msg", "找不到isbn="+ isbn+"的书");
			this.setRetJson(model, o.toString());
			return;
		}
		
		this.setRetJson(model, wbd.toJSON());
	}
	
	public void getRecentBooks(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		int start = this.getIntParameterValue(req, "start", 0);
		int len = this.getIntParameterValue(req, "len", 10);
		String tag = this.getParameterValue(req, "cat", "");
		
		JSONArray arr = new JSONArray();
		
		List<WebBook> list = cache.getRecentReadyBooks(tag, start, len);
		for (WebBook wb : list) {
			try {
				arr.put(new JSONObject(wb.toJSON()));
			} catch (Exception e) {
				LogUtil.WEB_LOG.warn("In getRecentBooks, WebBook toJson error: isbn=" + wb.getIsbn() + " title="+ wb.getTitle(), e);
			}
		}
		
		this.setRetJson(model, arr.toString());
	}
	
	public void wxSign(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		JSONObject o = new JSONObject();
		String url = this.getParameterValue(req, "url", "");
		String digest = WxAccessTokenUtil.genSign(url);
		
		o.put("status", "ok");
		o.put("timestamp", System.currentTimeMillis()/1000);
		o.put("appId", WxConstants.WX_APPID);
		o.put("nonceStr", WxConstants.WX_NONCESTR);
		o.put("sign", digest);
		this.setRetJson(model, o.toString());
	}
	
	public void run() {
		while (true) {
			try {
				Thread.sleep(10 * 60 * 1000L);//每隔10分钟清理一次
			} catch (Exception e) {
				break;
			}
			Iterator<SmsCode> it = smsCodeMap.values().iterator();
			while (it.hasNext()) {
				SmsCode c = it.next();
				if ((System.currentTimeMillis() - c.getCreateTime()) > 30*60*1000L) {
					//超过30分钟，则清掉
					it.remove();
				}
			}
			
			it = smsIdCodeMap.values().iterator();
			while (it.hasNext()) {
				SmsCode c = it.next();
				if ((System.currentTimeMillis() - c.getCreateTime()) > 30*60*1000L) {
					//超过30分钟，则清掉
					it.remove();
				}
			}
		}
	}
	
	
	public void verifySmsCode(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		JSONObject o = new JSONObject();
		String code = this.getParameterValue(req, "code", "");
		String mobile = this.getParameterValue(req, "mobile", "");
		String flag = this.getParameterValue(req, "flag", "");
		
		boolean found = false;
		SmsCode c = smsCodeMap.get(mobile);
		if (c != null) {
			if (c.getCode() != null && c.getCode().equals(code)) {
				found = true;
			} else {
				String id = c.getId();
				if (id != null) {
					SmsCode correctCodeObj = smsIdCodeMap.get(id);
					if (correctCodeObj != null && correctCodeObj.getCode() != null &&  correctCodeObj.getCode().equals(code)) {
						found = true;
						c.setCode(correctCodeObj.getCode());
						smsIdCodeMap.remove(id);
					}
				}
			}
		}
		
		//测试账号		
		if (mobile.equals("18601026360")) {
			found = true;
		}
		
		//测试flag
		if (flag.equalsIgnoreCase("rpwtrpjj")) {
			found = true;
		}
		
		if (found) {
			o.put("status", "ok");
		} else {
			o.put("status", "fail");
		}
		this.setRetJson(model, o.toString());
	}
	
	public void smsReceiver(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		JSONObject o = new JSONObject();
		String randCode = this.getParameterValue(req, "rand_code", "");
		String id = this.getParameterValue(req, "identifier", "");
		
		if (id.trim().length() > 0 && randCode.trim().length() > 0) {
			SmsCode codeObj = new SmsCode();
			codeObj.setCode(randCode);
			codeObj.setId(id);
			codeObj.setCreateTime(System.currentTimeMillis());
			smsIdCodeMap.put(id, codeObj);
			LogUtil.info("receive_sms_code SUCCESS: rand_code=" + randCode + " identifier=" + id);
		}
		
		o.put("res_code", "0");
		this.setRetJson(model, o.toString());
	}
	private static String[] TEST_MOBILE = {"18601026360"};
	public void sendSms(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		JSONObject o = new JSONObject();
		
		String mobile = this.getParameterValue(req, "mobile", "").trim();
		if (mobile.trim().length() != 11) {
			o.put("status", "fail");
			this.setRetJson(model, o.toString());
			return;
		}
		
		for (String m : TEST_MOBILE) {
			if (mobile.equals(m)) {
				//测试账号不用发验证码
				o.put("status", "ok");
				o.put("msg", "测试账号的验证码为123456");
				this.setRetJson(model, o.toString());
				return;
			}
		}
		
		//看一下mobile是否已经发送过code
		SmsCode sc = smsCodeMap.get(mobile);
		if (sc != null && ((System.currentTimeMillis() - sc.getCreateTime()) < 2*60*1000L)) {
			//2分钟内已经发送过验证码了
			o.put("status", "sent_once");
			this.setRetJson(model, o.toString());
			return;
		}
		
		SmsCode codeObj = new SmsCode();
		codeObj.setCreateTime(System.currentTimeMillis());
		codeObj.setMobile(mobile);
		smsCodeMap.put(mobile, codeObj);
		LogUtil.info("send_sms_code: " + mobile);
		
		String sendId = SmsCodeUtil.sendSmsCode(mobile, "www"+domain);
		if (sendId == null || sendId.trim().length() == 0) {
			//下发短信失败
			o.put("status", "fail");
			this.setRetJson(model, o.toString());
			LogUtil.info("send_sms_code FAILED: mobile=" + mobile);
			return;
		}
		
		codeObj.setId(sendId);
		SmsCode co = smsIdCodeMap.get(sendId);
		if (co != null && co.getCode() !=null && co.getCode().trim().length() > 0) {
			codeObj.setCode(co.getCode());
			smsIdCodeMap.remove(sendId);
		}
		
		LogUtil.info("send_sms_code SUCCESS: mobile=" + mobile + " identifier=" + sendId + " code=" + codeObj.getCode());
		o.put("status", "ok");
		this.setRetJson(model, o.toString());
	}
}
