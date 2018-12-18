package com.lebaor.limer.web;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.limer.data.Child;
import com.lebaor.limer.data.LimerBookInfo;
import com.lebaor.limer.data.LimerConstants;
import com.lebaor.limer.data.User;
import com.lebaor.limer.data.UserAuth;
import com.lebaor.limer.web.data.WebBookComment;
import com.lebaor.limer.web.data.WebBookDetail;
import com.lebaor.limer.web.data.WebBookList;
import com.lebaor.limer.web.data.WebBookListDetail;
import com.lebaor.limer.web.data.WebBookStatus;
import com.lebaor.limer.web.data.WebBorrowBook;
import com.lebaor.limer.web.data.WebDonateBook;
import com.lebaor.limer.web.data.WebJSONArray;
import com.lebaor.limer.web.data.WebJSONObject;
import com.lebaor.limer.web.data.WebOrder;
import com.lebaor.limer.web.data.WebPayParam;
import com.lebaor.limer.web.data.WebPreOrder;
import com.lebaor.limer.web.data.WebUser;
import com.lebaor.limer.web.data.WebUserCenterInfo;
import com.lebaor.thirdpartyutils.SmsCodeUtil;
import com.lebaor.thirdpartyutils.SmsCodeUtil.SmsCode;
import com.lebaor.utils.JSONUtil;
import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.wx.WxAccessTokenUtil;
import com.lebaor.wx.WxConstants;
import com.lebaor.wx.WxMiniProgramUtil;
import com.lebaor.wx.WxUserInfoUtil;
import com.lebaor.wx.data.WxPayNotifyData;
import com.lebaor.wx.data.WxUserInfo;


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
		} else if (uri.startsWith("/json/getBookListDetail")) {
			getBookListDetail(req, res, model);
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
		} else if (uri.startsWith("/json/preBorrowOneBook")) {
			preBorrowOneBook(req, res, model);
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
		} else if (uri.startsWith("/json/getBookComments")) {//这个需要在前边
			getBookComments(req, res, model);
			return;
		} else if (uri.startsWith("/json/getBookSingleComment")) {
			getBookComment(req, res, model);
			return;
		} else if (uri.startsWith("/json/getUserBookComments")) {
			getUserBookComments(req, res, model);
			return;
		} else if (uri.startsWith("/json/addBookComment")) {
			addBookComment(req, res, model);
			return;
		} else if (uri.startsWith("/json/addBookToBookList")) {
			addBookToBookList(req, res, model);
			return;
		} else if (uri.startsWith("/json/agreeBookComment")) {
			agreeBookComment(req, res, model);
			return;
		} else if (uri.startsWith("/json/fillAddress")) {
			fillAddress(req, res, model);
			return;
		} else if (uri.startsWith("/json/isAddressFilled")) {
			isAddressFilled(req, res, model);
			return;
		} else if (uri.startsWith("/json/getAddress")) {
			getAddress(req, res, model);
			return;
		} else if (uri.startsWith("/json/isMpFollowed")) {
			isMpFollowed(req, res, model);
			return;
		} else if (uri.startsWith("/json/getMemberPayInfo")) {
			getMemberPayInfo(req, res, model);
			return;
		} else if (uri.startsWith("/json/orderMember")) {
			orderMember(req, res, model);
			return;
		} else if (uri.startsWith("/wxpay/paynotify")) {
			payNotify(req, res, model);
			return;
		} else if (uri.startsWith("/wx/callback")) {
			wxCallback(req, res, model);
			return;
		} 
	}
	
	public void wxCallback(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		
		String timestamp = this.getParameterValue(req, "timestamp", "");
		String signature = this.getParameterValue(req, "signature", "");
		String nonce = this.getParameterValue(req, "nonce", "");
		String echostr = this.getParameterValue(req, "echostr", "");
		String sign = WxConstants.getWxSign(WxConstants.WX_TOKEN, timestamp, nonce);
		
		if (sign != null && sign.equals(signature)) {
			//符合
			this.setRetText(model, echostr);
		} else {
			this.setRetText(model, "ERROR");
		}
		
	}
	
	public void payNotify(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream(), "utf-8"));
			String xml = "";
			String s = "";
			while ((s = reader.readLine())!= null) {
				xml += s + "\n";
			}
			WxPayNotifyData data = WxPayNotifyData.loadXml(xml);
			
			if (data != null && data.isSuccess()) {
				//处理订单更新
				cache.updateOrder(data);
				
				String resultXml = WxPayNotifyData.genSuccessResultXml();
				this.setRetText(model, resultXml);
			}
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("payNotify exception", e);
		}
	}
	
	public void getMemberPayInfo(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		
		WebUser wu = this.getUser(req);
		
		if (wu == null || wu.getUser() == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		String region="";
		String address = "";
		String receiverMobile = "";
		String receiverName = "";
		try {
			String extraJson = wu.getUser().getExtraInfo();
			if (extraJson == null || extraJson.trim().length() == 0) extraJson = "{}";
			JSONObject extra = new JSONObject(extraJson);
			region = JSONUtil.getString(extra, "region").replaceAll("[\\[\\]\\,\"]", "");
			address = JSONUtil.getString(extra, "address");
			receiverMobile = JSONUtil.getString(extra, "receiverMobile");
			receiverName = JSONUtil.getString(extra, "receiverName");
		} catch (Exception e) {}
		
		int realFee = LimerConstants.getMemberPrice(region);
		WebPreOrder wo = new WebPreOrder();
		wo.setRegion(region);
		wo.setAddress(address);
		
		WxUserInfo ui = WxUserInfoUtil.getUserInfoByOpenId(wu.getOpenId());
		
		wo.setAllowed(ui != null && ui.getSubscribe() != 0);//是否关注；关注则允许
		wo.setReceiverMobile(receiverMobile);
		wo.setReceiverName(receiverName);
		wo.setDepositFee(LimerConstants.DEPOSIT_FEE);
		wo.setMchDesc("青柠月度会员");
		wo.setRealFee(realFee);
		wo.setTotalFee(0);
		
		this.setRetJson(model, new WebJSONObject(wo.toJSON()).toJSON());
	}
	
	public void isMpFollowed(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		
		WebUser wu = this.getUser(req);
		
		if (wu == null || wu.getUser() == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		WxUserInfo ui = WxUserInfoUtil.getUserInfoByOpenId(wu.getOpenId());
		
		JSONObject fill = new JSONObject();
		try {
			fill.put("isFollowed", ui != null && ui.getSubscribe() != 0);
		} catch (Exception e) {
			
		}
		WebJSONObject o = new WebJSONObject(fill.toString());
		
		this.setRetJson(model, o.toString());
	}
	
	public void isAddressFilled(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		
		WebUser wu = this.getUser(req);
		
		if (wu == null || wu.getUser() == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		boolean result = false;
		try {
			String extraJson = wu.getUser().getExtraInfo();
			if (extraJson == null || extraJson.trim().length() == 0) extraJson = "{}";
			JSONObject extra = new JSONObject(extraJson);
			String address = JSONUtil.getString(extra, "address");
			result = address.trim().length() > 0;
		} catch (Exception e) {}
		
		JSONObject fill = new JSONObject();
		try {
			fill.put("hasInfo", result);
		} catch (Exception e) {
			
		}
		WebJSONObject o = new WebJSONObject(fill.toString());
		
		this.setRetJson(model, o.toString());
	}
	
	public void getAddress(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		
		WebUser wu = this.getUser(req);
		
		if (wu == null || wu.getUser() == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		boolean result = true;
		JSONObject fill = new JSONObject();
		
		try {
			String extraJson = wu.getUser().getExtraInfo();
			if (extraJson == null || extraJson.trim().length() == 0) {
				extraJson = "{}";
				result = false;
			}
			fill.put("hasInfo", result);
			
			JSONObject extra = new JSONObject(extraJson);
			String address = JSONUtil.getString(extra, "address");
			String region = JSONUtil.getString(extra, "region");
			String receiverMobile = JSONUtil.getString(extra, "receiverMobile");
			String receiverName = JSONUtil.getString(extra, "receiverName");
			
			fill.put("address", address);
			fill.put("region", region);
			fill.put("receiverMobile", receiverMobile);
			fill.put("receiverName", receiverName);
			
			Child c = cache.getChild(wu.getUserId());
			if (c != null) {
				fill.put("birthday", c.getBirthday());
				fill.put("childName", c.getChildName());
				fill.put("relation", c.getRelation());
				fill.put("sex", c.getSex());
			}
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("getAddress: [userId="+ wu.getUserId() +"]", e);
		}
		
		WebJSONObject o = new WebJSONObject(fill.toString());
		
		this.setRetJson(model, o.toString());
	}
	
	public void agreeBookComment(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		long commentId = this.getLongParameterValue(req, "commentId", 0);
		String isbn = this.getParameterValue(req, "isbn", "");
		WebUser wu = this.getUser(req);
		
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		boolean result = cache.agreeBookComment(commentId, wu.getUserId(), isbn);
		WebJSONObject o = new WebJSONObject(result, result? "成功":"失败");
		
		this.setRetJson(model, o.toString());
	}
	
	public void addBookToBookList(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		String isbn = this.getParameterValue(req, "isbn", "");
		long booklistId = this.getLongParameterValue(req, "booklistId", 0);
		String content = this.getParameterValue(req, "content", "");
		String imgUrls = this.getParameterValue(req, "imgUrls", "[]");
		WebUser wu = this.getUser(req);
		
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		boolean result = true;
		if (booklistId > 0) {
			//如果没有提交booklistId，则说明只是书评
			boolean result1 = cache.addBookToBookList(booklistId, isbn, wu.getUserId());
			result = result1;
		}
		boolean result2 = cache.addBookComment(content, wu.getUserId(), isbn, imgUrls);
		
		result = result && result2;
		WebJSONObject o = new WebJSONObject(result, result? "成功":"失败");
		
		this.setRetJson(model, o.toString());
	}
	
	public void fillAddress(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		String birthday = this.getParameterValue(req, "birthday", "");
		int sex = this.getIntParameterValue(req, "sex", 0);
		String nickName = this.getParameterValue(req, "childName", "");
		int relation = this.getIntParameterValue(req, "relation", 0);
		
		String region = this.getParameterValue(req, "region", "");
		String address = this.getParameterValue(req, "address", "");
		String receiverMobile = this.getParameterValue(req, "receiverMobile", "");
		String receiverName = this.getParameterValue(req, "receiverName", "");
		
		WebUser wu = this.getUser(req);
		
		if (wu == null || wu.getUser() == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		try {
			String extraJson = wu.getUser().getExtraInfo();
			if (extraJson == null || extraJson.trim().length() == 0) extraJson = "{}";
			JSONObject extra = new JSONObject(extraJson);
			extra.put("region", region);
			extra.put("address", address);
			extra.put("receiverMobile", receiverMobile);
			extra.put("receiverName", receiverName);
			wu.getUser().setExtraInfo(extra.toString());
			cache.updateUserInfo(wu.getUserId(), wu.getUser());
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("updateUserInfo exception [userId="+ wu.getUserId() +"]", e);
		}
		
		Child child = cache.getChild(wu.getUserId());
		boolean result;
		if (child == null) {
			child = new Child();
			child.setBirthday(birthday);
			child.setSex(sex);
			child.setChildName(nickName);
			child.setCreateTime(System.currentTimeMillis());
			child.setExtraInfo("{}");
			child.setParentUserId(wu.getUserId());
			child.setRelation(relation);
			result = cache.addChild(child);
		} else {
			child.setBirthday(birthday);
			child.setSex(sex);
			child.setChildName(nickName);
			child.setCreateTime(System.currentTimeMillis());
			child.setExtraInfo("{}");
			child.setParentUserId(wu.getUserId());
			child.setRelation(relation);
			result = cache.updateChild(child);
		}
		
		if (!result) {
			WebJSONObject o = new WebJSONObject(result, "填写信息提交失败");
			this.setRetJson(model, o.toString());
			return;
		}
		
	}
	
	public void orderMember(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		String ip = this.getUserIp(req);
		
		WebUser wu = this.getUser(req);
		int totalFee = this.getIntParameterValue(req, "totalFee", 0);
		int realFee = this.getIntParameterValue(req, "realFee", 0);
		int depositFee = this.getIntParameterValue(req, "deposit", 0);
		
		WebPayParam result = cache.preOrderMember(wu.getOpenId(), wu.getUnionId(), LimerConstants.PRODUCT_ID_MEM_MONTH,
				wu.getUserId(),
				ip, totalFee, realFee, depositFee
				);
		if (result == null) {
			WebJSONObject o = new WebJSONObject(false, "支付失败");
			this.setRetJson(model, o.toString());
		} else {
			WebJSONObject o = new WebJSONObject(result.toJSON());
			this.setRetJson(model, o.toString());
		}
		
		
	}
	
	
	public void addBookComment(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		String isbn = this.getParameterValue(req, "isbn", "");
		String content = this.getParameterValue(req, "content", "");
		String imgUrls = this.getParameterValue(req, "imgUrls", "[]");
		WebUser wu = this.getUser(req);
		
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		boolean result = cache.addBookComment(content, wu.getUserId(), isbn, imgUrls);
		WebJSONObject o = new WebJSONObject(result, result? "成功":"失败");
		
		this.setRetJson(model, o.toString());
	}
	
	public void getBookComment(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		long commentId = this.getLongParameterValue(req, "id", 0);
		WebBookComment wbc = cache.getWebBookComment(commentId);
		
		if (wbc == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有找到对应书评").toJSON());
			return;
		}
		
		this.setRetJson(model, new WebJSONObject(wbc.toJSON()).toJSON());
	}
	
	public void getBookComments(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		String isbn = this.getParameterValue(req, "isbn", "");
		int start = this.getIntParameterValue(req, "start", 0);
		int length = this.getIntParameterValue(req, "len", 10);
		
		WebBookComment[] wbcs = cache.getBookComments(isbn, start, length);
		JSONArray arr = new JSONArray();
		for (WebBookComment wbc : wbcs) {
			try {
				LogUtil.WEB_LOG.debug(wbc.toJSON());
				arr.put(new JSONObject(wbc.toJSON()));
			} catch (Exception e) {
				LogUtil.WEB_LOG.warn("getBookComments exception", e);
			}
		}
		
		this.setRetJson(model, new WebJSONArray(arr.toString()).toJSON());
	}
	
	public void getUserBookComments(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model) {
		WebUser wu = this.getUser(req);
		
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "没有用户信息").toJSON());
			return;
		}
		
		int start = this.getIntParameterValue(req, "start", 0);
		int length = this.getIntParameterValue(req, "len", 10);
		
		WebBookComment[] wbcs = cache.getUserBookComments(wu.getUserId(), start, length);
		JSONArray arr = new JSONArray();
		for (WebBookComment wbc : wbcs) {
			try {
				arr.put(new JSONObject(wbc.toJSON()));
			} catch (Exception e) {
				LogUtil.WEB_LOG.warn("getBookComments exception", e);
			}
		}
		
		this.setRetJson(model, new WebJSONObject(arr.toString()).toJSON());
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
			//从session里取
			Object o = req.getSession().getAttribute("unionId");
			if (o == null) { 
				LogUtil.WEB_LOG.warn("getUser error, no user: [unionId=]");
				return null;
			} else {
				unionId = (String)o;
			}
		}
		
		long userId = cache.getUserIdByUnionId(unionId);
		if (userId <= 0) {
			//没有unionId
			LogUtil.WEB_LOG.warn("getUser error, no user: [unionId="+ unionId +"] userId=" + userId);
			return null;
		}
		
		//set session
		req.getSession().setAttribute("unionId", unionId);
		
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
	
	public void preBorrowOneBook(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		WebUser wu = this.getUser(req);
		if (wu == null) {
			this.setRetJson(model, new WebJSONObject(false, "用户不存在").toJSON());
			return;
		}
		
		
		//用户一次借n本书
		String isbn = this.getParameterValue(req, "isbn", "");
		LimerBookInfo lbi = cache.preBorrowOneBook(isbn, wu.getUserId());
		
		if (lbi != null) {
			this.setRetJson(model, new WebJSONObject(lbi.getId()+"").toJSON());
		} else {
			this.setRetJson(model, new WebJSONObject(false, "已无库存").toJSON());
		}
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
		
		JSONObject jo = wbd.toWebJSONObject();
		jo.put("status", wbs.getStatus());
		jo.put("statusDesc", LimerConstants.explainBookStatus(wbs.getStatus()));
		
		//该用户是否已填写孩子信息
		WebUser wu = this.getUser(req);
		if (wu != null) {
			Child child = cache.getChild(wu.getUserId());
			jo.put("hasChildInfo", child != null);
		}
		
		this.setRetJson(model, new WebJSONObject(jo.toString()).toJSON());
	}
	
	public void getBookListDetail(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		
		long id = this.getLongParameterValue(req, "id", 0);
		
		WebBookListDetail wbd = cache.getBookListDetail(id);
		if (wbd == null) {
			WebJSONObject data = new WebJSONObject(false, "找不到id="+ id+"的书单", "{}");
			this.setRetJson(model, data.toJSON());
			return;
		}
		
		this.setRetJson(model, new WebJSONObject(wbd.toJSON()).toJSON());
	}
	
	public void getRecentBookLists(HttpServletRequest req, 
			HttpServletResponse res, HashMap<String, Object> model)  
            throws Exception {
		int start = this.getIntParameterValue(req, "start", 0);
		int len = this.getIntParameterValue(req, "len", 20);
		String tag = this.getParameterValue(req, "tag", "");
		
		JSONArray arr = new JSONArray();
		
		String type = LimerConstants.parseBookListTypeFromTag(tag);
		List<WebBookListDetail> list = cache.getRecentBookLists(type, start, len);
		for (WebBookListDetail wb : list) {
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
				arr.put(new JSONObject(wb.toWebJSON()));
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
