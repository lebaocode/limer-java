package com.lebaor.limer.web;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.limer.data.LimerConstants;
import com.lebaor.limer.data.User;
import com.lebaor.limer.data.UserAuth;
import com.lebaor.limer.web.data.WebBookDetail;
import com.lebaor.limer.web.data.WebBookList;
import com.lebaor.limer.web.data.WebBookStatus;
import com.lebaor.limer.web.data.WebBorrowBook;
import com.lebaor.limer.web.data.WebDonateBook;
import com.lebaor.limer.web.data.WebJSONArray;
import com.lebaor.limer.web.data.WebJSONObject;
import com.lebaor.limer.web.data.WebOrder;
import com.lebaor.limer.web.data.WebUser;
import com.lebaor.limer.web.data.WebUserCenterInfo;
import com.lebaor.thirdpartyutils.SmsCodeUtil;
import com.lebaor.thirdpartyutils.SmsCodeUtil.SmsCode;
import com.lebaor.utils.JSONUtil;
import com.lebaor.utils.LogUtil;
import com.lebaor.wx.WxAccessTokenUtil;
import com.lebaor.wx.WxConstants;
import com.lebaor.wx.WxMiniProgramUtil;


public class JsonController extends EntryController implements Runnable {
	ConcurrentHashMap<String, SmsCode> smsCodeMap = new ConcurrentHashMap<String, SmsCode>();//key:mobile, value:smsCode
	ConcurrentHashMap<String, SmsCode> smsIdCodeMap = new ConcurrentHashMap<String, SmsCode>();//key:id, value:smsCode
	Thread t;
		
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
		} else if (uri.startsWith("/json/getRecentBookLists")) {//所有书单
			getRecentBookLists(req, res, model);
			return;
		} else if (uri.startsWith("/json/wxsign")) {
			wxSign(req, res, model);
			return;
		} else if (uri.startsWith("/json/code2Session")) {
			code2Session(req, res, model);
			return;
		} else if (uri.startsWith("/json/submitDonate")) {
			submitDonate(req, res, model);
			return;
		} else if (uri.startsWith("/json/getUserCenterInfo")) {
			getUserCenterInfo(req, res, model);
			return;
		} else if (uri.startsWith("/json/getBookDetail")) {
			getBookDetailByIsbn(req, res, model);
			return;
		} else if (uri.startsWith("/json/decryptUserInfo")) {
			decryptUserInfo(req, res, model);
			return;
		} else if (uri.startsWith("/json/getMyBorrowBooks")) {
			getMyBorrowBooks(req, res, model);
			return;
		} else if (uri.startsWith("/json/getMyDonateBooks")) {
			getMyDonateBooks(req, res, model);
			return;
		} else if (uri.startsWith("/json/borrowBooks")) {
			borrowBooks(req, res, model);
			return;
		} else if (uri.startsWith("/json/returnBook")) {
			returnBook(req, res, model);
			return;
		} else if (uri.startsWith("/json/isBookDonated")) {
			isBookDonated(req, res, model);
			return;
		} else if (uri.startsWith("/json/getMyOrders")) {
			getMyOrders(req, res, model);
			return;
		} else if (uri.startsWith("/json/getLogisticsFee")) {
			getLogisticsFee(req, res, model);
			return;
		} 
		
	}
	public void code2Session(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		String code = this.getParameterValue(req, "code", "");
		
		JSONObject jo = WxMiniProgramUtil.code2Session(code);
		this.setRetJson(model, jo.toString());
	}
	
	public void decryptUserInfo(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		String encryptedData = this.getParameterValue(req, "encryptedData", "");
		String sessionKey = this.getParameterValue(req, "sessionKey", "");
		String iv = this.getParameterValue(req, "iv", "");
				
		LogUtil.WEB_LOG.debug("begin decryptUserInfo(encryptedData=["+ encryptedData +"], sessionKey=["+ sessionKey +"], iv=["+  iv +"])");
		JSONObject o = WxMiniProgramUtil.getUserInfo(encryptedData, sessionKey, iv);
		cache.createUserIfNotExist(o);
		
		this.setRetJson(model, o.toString());
	}
	
	public WebUser getUser(HttpServletRequest req) {
		String openId = this.getParameterValue(req, "openId", "");
		String unionId = this.getParameterValue(req, "unionId", "");
		if (unionId.trim().length() == 0) {
			//没有unionId
			LogUtil.WEB_LOG.warn("getUser error, no user: [unionId=]");
			return null;
		}
		
		long userId = cache.getUserIdByUnionId(unionId);
		if (userId <= 0) {
			//没有unionId
			LogUtil.WEB_LOG.warn("getUser error, no user: [unionId="+ unionId +"] userId=" + userId);
			return null;
		}
		
		User user = cache.getUserInfo(userId);
		LogUtil.STAT_LOG.info("[USER_VISIT] ["+ userId +"] ["+ unionId +"] ["+ req.getRequestURI() +"] ["+ req.getQueryString() +"]");
		return WebUser.create(user, unionId, openId);
		
	}
	
	public void getMyOrders(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		WebOrder[] wos = cache.getMyOrders(wu.getUserId());
		JSONArray ja = new JSONArray();
		for (int i = 0; i < wos.length; i++) {
			ja.put(wos[i].toJSONObject());
		}
		
		WebJSONArray wja = new WebJSONArray(ja.toString());
		
		this.setRetJson(model, wja.toJSON());
	}
	
	public void borrowBooks(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		
		//用户一次借n本书
		String isbnArrJson = this.getParameterValue(req, "isbn_arr", "");
		
		JSONArray jArr = new JSONArray(isbnArrJson);
		String[] isbnArr = new String[jArr.length()];
		for (int i = 0; i <  jArr.length(); i++) {
			String isbn = jArr.getString(i);
			isbnArr[i] = isbn;
		}
		
		String ip = this.getUserIp(req);
		WebJSONArray wja = cache.borrowBooks(isbnArr, wu, ip);
		
		this.setRetJson(model, wja.toJSON());
	}
	
	public void isBookDonated(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		long curUserId = wu.getUserId();
		JSONObject o = new JSONObject();
		
		String isbn = this.getParameterValue(req, "isbn", "");
		WebBookDetail wbd = cache.getBookInfo(isbn);
		if (wbd == null) {
			o.put("donated", false);
			this.setRetJson(model, o.toString());
			return;
		} 
		
		boolean isDonated = cache.isDonatedBook(curUserId, isbn);
		o.put("donated", isDonated);
		
		WebJSONObject wjo = new WebJSONObject(o.toString());
		this.setRetJson(model, wjo.toString());
	}
	
	public void returnBook(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		long curUserId = wu.getUserId();
		
		long limerBookId = this.getLongParameterValue(req, "limerBookId", 0L);
		boolean result = cache.returnBook(curUserId, limerBookId);
		WebJSONObject o = new WebJSONObject(result, result? "成功":"失败");
		
		this.setRetJson(model, o.toString());
	}
	
	
	public void getMyDonateBooks(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		long curUserId = wu.getUserId();
		
		WebDonateBook[] bArr = cache.getMyDonateBooks(curUserId);
		JSONArray arr = new JSONArray();
		for (int i = 0; i < bArr.length; i++) {
			arr.put(new JSONObject(bArr[i].toJSON()));
		}
		
		this.setRetJson(model, new WebJSONArray(arr.toString()).toJSON());
	}
	
	public void getMyBorrowBooks(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		long curUserId = wu.getUserId();
		
		WebBorrowBook[] bArr = cache.getMyBorrowBooks(curUserId);
		JSONArray arr = new JSONArray();
		for (int i = 0; i < bArr.length; i++) {
			arr.put(new JSONObject(bArr[i].toJSON()));
		}
		
		this.setRetJson(model, new WebJSONArray(arr.toString()).toJSON());
	}
	
	public void getUserCenterInfo(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		long curUserId = wu.getUserId();
		WebUserCenterInfo wuci = cache.getWebUserCenterInfo(curUserId, wu.getUnionId());
		
		if (wuci == null) {
			WebJSONObject wjo = new WebJSONObject(false, "没有该用户信息", WebJSONObject.DEFAULT_DATA);
			this.setRetJson(model, wjo.toJSON());
			return;
		}
		
		this.setRetJson(model, wuci.toString());
	}
	
	
	public void submitDonate(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		long curUserId = wu.getUserId();
		
		JSONArray resArr = new JSONArray();
		
		String isbnArrJson = this.getParameterValue(req, "isbn_arr", "");
		JSONArray arr = new JSONArray(isbnArrJson);
		for (int i = 0; i <arr.length(); i++) {
			String isbn = arr.getString(i);
			WebBookDetail wbd = cache.getBookInfo(isbn);
			
			
			JSONObject data = new JSONObject();
			data.put("isbn", isbn);
			if (wbd == null) {
				WebJSONObject ro = new WebJSONObject(false, "没有isbn="+isbn+"对应的书籍", data.toString());
				resArr.put(ro.toJSONObject());
				continue;
			}
			LogUtil.WEB_LOG.debug("begin addDonateBook("+ isbn +", "+ curUserId +")");
			boolean success = cache.addDonateBook(isbn, curUserId);
			WebJSONObject ro = new WebJSONObject(success, success ? "成功" : "失败", data.toString());
			resArr.put(ro.toJSONObject());
		}
		
		WebJSONArray resJson = new WebJSONArray(resArr.toString());
		this.setRetJson(model, resJson.toJSON());
	}
	
	public void getLogisticsFee(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		
		int pn = this.getIntParameterValue(req, "pageNum", 0);
		
		int fee = LimerConstants.computeLogisticsFee(pn);
		JSONObject jo = new JSONObject();
		jo.put("fee", fee);
		this.setRetJson(model, new WebJSONObject(jo.toString()).toJSON()) ;
	}
	
	public void getBookDetailByIsbn(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		
		String isbn = this.getParameterValue(req, "isbn", "");
		WebBookDetail wbd = cache.getBookInfo(isbn);
		if (wbd == null) {
			WebJSONObject data = new WebJSONObject(false, "找不到isbn="+ isbn+"的书", "{}");
			this.setRetJson(model, data.toJSON());
			return;
		}
		
		//加入该书状态，是否可借阅
		WebBookStatus wbs = cache.getWebBookStatus(isbn);
		
		JSONObject jo = wbd.toJSONObject();
		jo.put("status", wbs.getStatus());
		jo.put("statusDesc", LimerConstants.explainBookStatus(wbs.getStatus()));
		
		this.setRetJson(model, new WebJSONObject(jo.toString()).toJSON());
	}
	
	public void getRecentBookLists(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		int start = this.getIntParameterValue(req, "start", 0);
		int len = this.getIntParameterValue(req, "len", 20);
		String tag = this.getParameterValue(req, "tag", "");
		
		JSONArray arr = new JSONArray();
		
		List<WebBookList> list = cache.getRecentBookLists(tag, start, len);
		for (WebBookList wb : list) {
			try {
				arr.put(new JSONObject(wb.toJSON()));
			} catch (Exception e) {
				LogUtil.WEB_LOG.warn("In getRecentBookLists, WebBookDetail toJson error: WebBookList=" + wb.getTitle(), e);
			}
		}
		
		WebJSONArray wja = new WebJSONArray(arr.toString());
		this.setRetJson(model, wja.toJSON());
	}
	
	public void getRecentBooks(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		int start = this.getIntParameterValue(req, "start", 0);
		int len = this.getIntParameterValue(req, "len", 20);
		String tag = this.getParameterValue(req, "tag", "");
		
		JSONArray arr = new JSONArray();
		
		List<WebBookDetail> list = cache.getRecentBooks(tag, start, len);
		for (WebBookDetail wb : list) {
			try {
				arr.put(new JSONObject(wb.toJSON()));
			} catch (Exception e) {
				LogUtil.WEB_LOG.warn("In getRecentBooks, WebBookDetail toJson error: isbn=" + wb.getBook().getIsbn13() 
						+ " title="+ wb.getBook().getTitle(), e);
			}
		}
		
		WebJSONArray wja = new WebJSONArray(arr.toString());
		this.setRetJson(model, wja.toJSON());
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
