package com.lebaor.limer.web;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.limer.db.*;
import com.lebaor.limer.web.data.*;
import com.lebaor.limer.data.*;
import com.lebaor.thirdpartyutils.DoubanUtil;
import com.lebaor.utils.JSONUtil;
import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.wx.WxConstants;
import com.lebaor.wx.WxPayUtil;
import com.lebaor.wx.data.WxPayNotifyData;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LebaoCache {
	BookDB bookDB;
	LimerBookInfoDB lbiDB;
	UserScoreDB userScoreDB;
	BorrowRecordDB brDB;
	UserDB userDB;
	ChildDB childDB;
	UserAuthDB userAuthDB;
	LogisticsDB ltDB;
	OrderDB orderDB;
	BookListDB bookListDB;
	ActivityDB activityDB;
	BookCommentDB commentDB;
	BookListItemDB booklistItemDB;
	UserMemberActionDB actionDB;
	
	String wxPayNotifyUrl;
	
	//TODO 未考虑缓存的大小问题？
	private static final String KEY_RECENT_BOOKS = "recent_books"; //最近所有的书籍。WebBookDetail json list, 评分高的在前，缓存前1000个。
	private static final int KEY_RECENT_BOOKS_NUM = 1000;
	private static final String KEY_RECENT_BOOKLISTS = "recent_booklists"; //最近所有的书单。WebBookList json list，创建时间新的在前。
	private static final int KEY_RECENT_BOOKLISTS_NUM = 20;
	
	private static final String KEY_CHILD_PREFIX = "chd:";//key,value key=chd:parentid, value=childObject
	private static final int KEY_CHILD_EXPIRE = 3600*72;//3天
	
	private static final String KEY_COMMENT_PREFIX = "cm:";//书评 key=cm:id,value=commentObject
	private static final int KEY_COMMENT_EXPIRE = 3600*72;//3天
	
	private static final String KEY_BOOK_PREFIX = "bk:";//key,value key=bk:isbn, value=bookObject
	private static final int KEY_BOOK_EXPIRE = 3600*24*21;//21天
	
	private static final String KEY_LIMERBOOK_PREFIX = "lbi:";//key,value key=lbi:id, value=bookObject
	private static final int KEY_LIMERBOOK_EXPIRE = 3600*24*21;//21天
	
	private static final String KEY_COMMENTNUM_PREFIX = "cmn:";//key,value key=cmn:isbn, value=书评数
	private static final int KEY_COMMENTNUM_EXPIRE = 3600*720;//30天
	
	private static final String KEY_COMMENTLIKENUM_PREFIX = "cmagree:";//key,value key=cmagree:isbn, value=点赞数
	private static final int KEY_COMMENTLIKENUM_EXPIRE = 3600*720;//30天
	
	private static final String KEY_BOOK_STATUS_PREFIX = "bs:";//key,value key=bs:limerBookId, value=limerbookInfo
	private static final int KEY_BOOK_STATUS_EXPIRE = 3600*720;//30天
	
	private static final String KEY_USER_PREFIX = "user:";//key,value key=user:userId value=userprofileObject json 缓存里不保证是全部的数据
	private static final int KEY_USER_EXPIRE = 3600*72;//3天
	
	private static final String KEY_USER_AUTH_PREFIX = "wx:";//key,value key=wx:unionId value=userId 缓存里不保证是全部的数据
	private static final int KEY_USER_AUTH_EXPIRE = 3600*72;//3天
	
	private static final String KEY_PRE_BORROW_PREFIX = "preb:";//key,value key=preb:limerBookId value=userId
	private static final int KEY_PRE_BORROW_EXPIRE = 3600*2;//2小时
	
	int jedisMaxTotal;
	int jedisMaxIdle;
	int jedisMinIdle;
	String jedisHost;
	int jedisPort;
	String jedisPassword;
	
	static JedisPool jedisPool;
	static Jedis globalJedis;
	
	static LebaoCache _self;
	
	public LebaoCache() {
		_self = this;
	}
	
	public static LebaoCache getInstance() {
		return _self;
	}
	
	public void release() {
		if (globalJedis != null) globalJedis.close();
		if (jedisPool != null) jedisPool.close();
	}
	
	public void init() {
		JedisPoolConfig c = new JedisPoolConfig();
		c.setMaxIdle(jedisMaxIdle);
		c.setMinIdle(jedisMinIdle);
		c.setMaxTotal(jedisMaxTotal);
		
		jedisPool = new JedisPool(c, jedisHost, jedisPort);
		LogUtil.WEB_LOG.info("JedisPool inited, host=" + jedisHost + ":" + jedisPort);
		
		globalJedis = jedisPool.getResource();
		globalJedis.auth(getInstance().jedisPassword);
		
		//从jedis里取
		//更新recentBooks缓存
		LimerBookInfo[] lbiBooks = lbiDB.getAllLimerBookInfos();
		for (LimerBookInfo lbi : lbiBooks) {
			WebBookDetail wbd = this.getBookInfo(lbi.getIsbn());
			if (wbd == null) {
				LogUtil.WEB_LOG.warn("no book info: isbn="+lbi.getIsbn());
				continue;
			}
			
			//把这本书加入tag对应的缓存
			String[] tags = wbd.getBook().getLimerTagsAsArray();
			for (String tag : tags) {
				Map<String, Double> sm = new HashMap<String, Double>();
				sm.put(wbd.getBook().getIsbn(), (double)wbd.computeScore());
				getJedis().zadd(KEY_RECENT_BOOKS+"_" + TextUtil.MD5(tag), sm);
			}
		}
		
		try {
			if (!getJedis().exists(KEY_RECENT_BOOKLISTS)) {
				//最近1000本书籍状态
				Map<String, Double> scoreMembers = new HashMap<String, Double>();
				BookList[] bookArr = bookListDB.getBookLists(0, KEY_RECENT_BOOKLISTS_NUM);
				LogUtil.WEB_LOG.debug("getRecentBookLists() read from db:"+bookArr.length);
				
				for (BookList b : bookArr) {
					WebBookListDetail wb = new WebBookListDetail();
					wb.setDesc(b.getDesc());
					wb.setId(b.getId());
					wb.setSubTitle(b.getSubTitle());
					wb.setTitle(b.getTitle());
					wb.setType(b.getType());
					
					JSONArray bookJsonArr = new JSONArray();
					String[] isbns = b.getBookIsbns();
					for (String isbn : isbns) {
						WebBookDetail bd = this.getBookInfo(isbn);
						bookJsonArr.put(new JSONObject(bd.toWebJSON()));
					}
					wb.setBooks(bookJsonArr);
					
					if (b.getType() != null && b.getType().trim().length() > 0) {
						Map<String, Double> sm = new HashMap<String, Double>();
						sm.put(wb.toJSON(), (double)b.getCreateTime());
						getJedis().zadd(KEY_RECENT_BOOKLISTS + "_"+ wb.getType(), sm);
					}
					
					scoreMembers.put(wb.toJSON(), (double)b.getCreateTime());
				}
				getJedis().zadd(KEY_RECENT_BOOKLISTS, scoreMembers);
				LogUtil.WEB_LOG.debug("getRecentBookLists() write to redis: "+KEY_RECENT_BOOKLISTS+ " "+scoreMembers.size());
			}
		} catch(Exception t) {
			LogUtil.WEB_LOG.debug("getRecentBookLists(0,"+KEY_RECENT_BOOKLISTS_NUM+") error", t);
		}
		
		LogUtil.WEB_LOG.info("init Jedis recent_booklists success.");
	}
	
	
	
	public static Jedis getJedis() {
		return globalJedis;
	}
	
	//下单
	public WebPayParam preOrderMember(String openId, String unionId, String productId, long userId, String ip, 
			int totalFee, int realFee, int depositFee) {
		Order order = new Order();
		order.setMchNum(1);
		order.setCoupounFee(0);
		order.setCoupounId(null);
		order.setFeeType(LimerConstants.FEE_TYPE_RMB);
		order.setIp(ip);
		
		String mchTradeNo = Order.genMchTradeNo(userId);
		order.setMchTradeNo(mchTradeNo);
		order.setOpenid(openId);
		order.setOrderStartTime(System.currentTimeMillis());
		order.setPayMethod(LimerConstants.PAY_METHOD_WXPAY);
		order.setRealFee(realFee);
		order.setStatus(LimerConstants.ORDER_STATUS_MCH_PRE_XIADAN);
		order.setTotalFee(totalFee);
		order.setUnionid(unionId);
		order.setUserId(userId);
		order.setProductId(productId);
		order.setWxTradeNo("");
		
		order.setExtraJson("{depositFee: "+ depositFee +"}");
		boolean result = orderDB.addOrder(order);
		if (!result) {
			return null;
		}
		
		//调起微信支付
		String prepayId = "";
		try {
			prepayId = WxPayUtil.unifiedOrderJsApi(WxConstants.MINIPROGRAM_APPID, LimerConstants.PRODUCT_ID_MEM_MONTH, 
					mchTradeNo, realFee, ip, wxPayNotifyUrl, openId, "青柠月度会员", "青柠月度会员");
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("WxPayUnifyOrder exception [userId="+ userId +"]", e);
		}
		
		if (prepayId == null) {
			LogUtil.WEB_LOG.warn("WxPayUnifyOrder prepayId=null [userId="+ userId +"]");
			return null;
		}
		
		order = orderDB.getOrderByMchOrderId(mchTradeNo);
		order.setWxTradeNo(prepayId);
		order.setStatus(LimerConstants.ORDER_STATUS_WX_PRE_XIADAN);
		result = orderDB.updateOrder(order);
		
		WebPayParam p = new WebPayParam();
		p.setNonstr(WxConstants.WX_NONCESTR);
		String timestamp = (long)(System.currentTimeMillis()/1000) + "";
		p.setPrepayId(prepayId);
		String sign = WxConstants.getMiniProgramPaySign(prepayId, timestamp, WxConstants.WX_NONCESTR);
		p.setTimestamp(timestamp);
		p.setPaySign(sign);
		
		return p;
	}
	
	//判断该用户是否有押金没退
	public int getDepositFee(long userId) {
		UserMemberAction[] as = actionDB.getActionsByUser(userId);
		if (as.length == 0) return 0;
		
		int paied = 0;
		int returned = 0;
		for (UserMemberAction a : as) {
			if (a.getActionType() == LimerConstants.ACTION_TYPE_PAYDEPOSIT) {
				paied += a.getPriceFen();
			} else if (a.getActionType() == LimerConstants.ACTION_TYPE_RETURNDEPOSIT) {
				returned += a.getPriceFen();
			}
		}
		
		int res = paied - returned;
		if (res > 0) return res;
		
		return 0;
	}
	
	public long getMemberEndTime(long userId) {
		UserMemberAction[] as = actionDB.getActionsByUser(userId);
		if (as.length == 0) return 0;
		
		long endTime = 0;
		for (UserMemberAction a : as) {
			if (a.getActionType() == LimerConstants.ACTION_TYPE_PAYMEMBERMONTH) {
				if (endTime == 0 || a.getActionTime() > endTime) {
					endTime = a.getActionTime() + LimerConstants.PERIOD_INTERVAL + LimerConstants.PERIOD_MONTH;
				} else {
					endTime += LimerConstants.PERIOD_MONTH;
				}
				
			} else if (a.getActionType() == LimerConstants.ACTION_TYPE_ADDVALIDTIME) {
				endTime += a.getPriceFen() * 24*3600*1000L;
			}
		}
		
		if (System.currentTimeMillis() > endTime) {
			//已过期
			return 0;
		}
		
		return endTime;
	}
	
	//申请退还押金
	public boolean askReturnDeposit(long userId, int priceFen) {
		UserMemberAction act = new UserMemberAction();
		act.setActionTime(System.currentTimeMillis());
		act.setActionType(LimerConstants.ACTION_TYPE_ASKRETURNDEPOSIT);
		act.setPriceFen(priceFen);
		act.setUserId(userId);
		return actionDB.addUserMemberAction(act);
	}
	
	public void updateOrder(WxPayNotifyData data) {
		Order o = orderDB.getOrderByMchOrderId(data.getOrderId());
		if (o == null || LimerConstants.isOrderEnd(o.getStatus())) return;
		
		o.setOrderFinishTime(TextUtil.parseTime(data.getPayEndTime(), "yyyyMMddHHmmss"));
		o.setWxTradeNo(data.getTransactionId());
		o.setStatus(LimerConstants.ORDER_STATUS_PAY_SUCCESS);
		
		try {
			String json = o.getExtraJson();
			if (json == null || json.trim().length() == 0) json = "{}";
			JSONObject jo = new JSONObject(json);
			jo.put("pay_notify", data.getRawXml());
			o.setExtraJson(jo.toString());
		} catch (Exception e) {}
		
		orderDB.updateOrder(o);
		LogUtil.STAT_LOG.info("[WXPAY] [SUCCESS] ["+ o.getUnionid() +"] ["+ o.getRealFee() +"] ["+ o.getWxTradeNo() +"] ["+ o.getTitle() +"]");
		
		//记录action
		int depositFee = o.getDepositFee();
		int memberFee = o.getRealFee() - depositFee;
		
		UserMemberAction act = new UserMemberAction();
		act.setActionTime(System.currentTimeMillis());
		act.setActionType(LimerConstants.ACTION_TYPE_PAYMEMBERMONTH);
		act.setPriceFen(memberFee);
		act.setUserId(o.getUserId());
		actionDB.addUserMemberAction(act);
		
		if (depositFee > 0) {
			act = new UserMemberAction();
			act.setActionTime(System.currentTimeMillis());
			act.setActionType(LimerConstants.ACTION_TYPE_PAYDEPOSIT);
			act.setPriceFen(depositFee);
			act.setUserId(o.getUserId());
			actionDB.addUserMemberAction(act);
		}
		
	}
	
	//只能添加一个孩子 TODO
	public boolean addChild(Child child) {
		boolean result = childDB.addChild(child);
		if (result) {
			//添加缓存
			getJedis().set(KEY_CHILD_PREFIX+ child.getParentUserId(), child.toJSON());
		}
		return result;
	}
	
	//只能添加一个孩子 TODO
	public boolean updateChild(Child child) {
		boolean result = childDB.updateChild(child);
		if (result) {
			//添加缓存
			getJedis().set(KEY_CHILD_PREFIX+ child.getParentUserId(), child.toJSON());
		}
		return result;
	}
	
	//需要用缓存 只取了第一个孩子 TODO
	public Child getChild(long parentId) {
		Child child = null;
		
		String json = getJedis().get(KEY_CHILD_PREFIX+ parentId);
		if (json != null && json.trim().length() > 0) {
			child = Child.parseJSON(json);
		}
		
		if (child == null) {
			Child[] children = childDB.getChildrenByParent(parentId);
			if (children.length == 0) return null;
			
			child = children[0];
		}
		
		if (child != null) {
			getJedis().set(KEY_CHILD_PREFIX+ child.getParentUserId(), child.toJSON());
		}

		return child; 
	}
	
	//为一个书单添加一本书
	public boolean addBookToBookList(long bookListId, String isbn,  long userId) {
		BookListItem item = booklistItemDB.getBookListItem(bookListId, isbn, userId);
		if (item != null) return true;
		
		item = new BookListItem();
		item.setIsbn(isbn);
		item.setBookListId(bookListId);
		item.setUserId(userId);
		item.setCreateTime(System.currentTimeMillis());
		return booklistItemDB.addBookListItem(item);
	}
	
	private WebBookComment toWebBookComment(BookComment bc) {
		if (bc == null) return null;
		LogUtil.WEB_LOG.debug(bc.toJSON());
		
		WebBookComment wbc = new WebBookComment();
		wbc.setIsbn(bc.getIsbn());
		
		WebBookDetail wbd = this.getBookInfo(bc.getIsbn());
		if (wbd == null) {
			LogUtil.WEB_LOG.warn("toWebBookComment [commentId="+ bc.getId() +"] no_book [isbn="+ bc.getIsbn() +"]");
			return null;
		}
		
		wbc.setBookId(wbd.getBook().getId());
		wbc.setBookImg(wbd.getBook().getCoverUrl());
		wbc.setBookTitle(wbd.getBook().getTitle());
		wbc.setContent(bc.getContent());
		wbc.setCommentId(bc.getId());
		wbc.setCreateTimeDisplay(TextUtil.formatDay2(bc.getCreateTime()));
		try {
			wbc.setImgUrls(new JSONArray(bc.getImgUrlsJson()));
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("toWebBookComment [commentId="+ bc.getId() +"] exception", e);
		}
		wbc.setIsbn(bc.getIsbn());
		
		//数据库里的likeNum不一定是最新值，应取缓存里的值
		String likeNumStr = getJedis().get(KEY_COMMENT_PREFIX + bc.getId());
		wbc.setLikeNum(Math.max(bc.getLikeNum(), likeNumStr == null? bc.getLikeNum() : 
			Math.max(bc.getLikeNum(), Integer.parseInt(likeNumStr))));
		
		wbc.setUserId(bc.getUserId());
		
		User u = this.getUserInfo(bc.getUserId());
		if (u == null) {
			LogUtil.WEB_LOG.warn("toWebBookComment [commentId="+ bc.getId() +"] no_user [userId="+ bc.getUserId() +"]");
			return null;
		}
		wbc.setUserLogo(u.getUserLogo());
		wbc.setUserName(u.getUserName());
		
		//设置孩子年龄
		Child child = getChild(wbc.getUserId());
		if (child != null) {
			wbc.setChildAge(TextUtil.parseAge(child.getBirthday()));
		}
		
		return wbc;
	}
	
	public boolean agreeBookComment(long commentId, long userId, String isbn) {
		getJedis().incr(KEY_COMMENT_PREFIX + commentId);
		getJedis().expire(KEY_COMMENT_PREFIX + commentId, KEY_COMMENT_EXPIRE);
		
		getJedis().incr(KEY_COMMENTLIKENUM_PREFIX+ isbn);
		getJedis().expire(KEY_COMMENTLIKENUM_PREFIX+ isbn, KEY_COMMENTLIKENUM_EXPIRE);
		return true;
	}
	
	public boolean addBookComment(String content, long userId, String isbn, String imgUrlsJson) {
		long curTime = System.currentTimeMillis();
		boolean result;
		BookComment bc = commentDB.getBookCommentByUserBook(isbn, userId);
		if (bc != null) {
			//update
			bc.setContent(content);
			bc.setImgUrlsJson(imgUrlsJson);
			bc.setLastModifyTime(curTime);
			result = commentDB.updateBookComment(bc);
		} else {
			bc= new BookComment();
			bc.setContent(content);
			bc.setCreateTime(curTime);
			bc.setIsbn(isbn);
			bc.setLastModifyTime(curTime);
			bc.setLikeNum(0);
			bc.setUserId(userId);
			bc.setImgUrlsJson(imgUrlsJson);
			
			result =  commentDB.addBookComment(bc);
			bc = commentDB.getBookCommentByUserBook(isbn, userId);
			
			getJedis().incr(KEY_COMMENTNUM_PREFIX+ isbn);
			getJedis().expire(KEY_COMMENTNUM_PREFIX+ isbn, KEY_COMMENTNUM_EXPIRE);
		}
		
		return result;
	}
	
	private int getBookCommentNum(String isbn) {
		String nStr = getJedis().get(KEY_COMMENTNUM_PREFIX+isbn);
		if (nStr != null) {
			return Integer.parseInt(nStr);
		}
		
		//从数据库里获取
		int n = commentDB.getBookCommentNumByBook(isbn);
		getJedis().set(KEY_COMMENTNUM_PREFIX+isbn, Integer.toString(n));
		getJedis().expire(KEY_COMMENTNUM_PREFIX+isbn, KEY_COMMENTNUM_EXPIRE);
		return n;
	}
	
	private int getBookLikeNum(String isbn) {
		String nStr = getJedis().get(KEY_COMMENTLIKENUM_PREFIX+isbn);
		if (nStr != null) {
			return Integer.parseInt(nStr);
		}
		
		//从数据库里获取
		int n = commentDB.getBookLikeNumByBook(isbn);
		getJedis().set(KEY_COMMENTLIKENUM_PREFIX+isbn, Integer.toString(n));
		getJedis().expire(KEY_COMMENTLIKENUM_PREFIX+isbn, KEY_COMMENTLIKENUM_EXPIRE);
		return n;
	}
	
	public WebBookComment getWebBookComment(long commentId) {
		if (commentId <= 0) return null;
		
		BookComment bc = commentDB.getBookCommentById(commentId);
		return this.toWebBookComment(bc);
	}
	
	//得到一本书的对应数量的书评
	public WebBookComment[] getBookComments(String isbn, int start, int length) {
		BookComment[] bcs = commentDB.getBookCommentsByBook(isbn, start, length);
		
		LinkedList<WebBookComment> list = new LinkedList<WebBookComment>();
		for (BookComment bc : bcs) {
			WebBookComment wbc = this.toWebBookComment(bc);
			if (wbc == null) continue;
			
			list.add(wbc);
		}
		return list.toArray(new WebBookComment[0]);
	}
	
	//得到一本书的对应数量的书评
	public WebBookComment[] getUserBookComments(long userId, int start, int length) {
		BookComment[] bcs = commentDB.getBookCommentsByUser(userId, start, length);
		
		LinkedList<WebBookComment> list = new LinkedList<WebBookComment>();
		for (BookComment bc : bcs) {
			WebBookComment wbc = this.toWebBookComment(bc);
			if (wbc == null) continue;
			list.add(wbc);
		}
		return list.toArray(new WebBookComment[0]);
	}
	
//	public LimerBookInfo preBorrowOneBook(String isbn, long userId) {
//		//判断该书是否可借
//		LimerBookInfo one =null;
//		LimerBookInfo[] lbsArr = lbiDB.getLimerBooksByIsbn(isbn);
//		for (LimerBookInfo lbs : lbsArr) {
//			//判断缓存里是否已经被借走
//			if (getJedis().exists(KEY_PRE_BORROW_PREFIX + lbs.getId())) {
//				//已被预借
//				continue;
//			}
//			
//			if (lbs.getStatus() == LimerConstants.LIMER_BOOK_STATUS_READY) {
//				//可以借
//				if (one == null) one = lbs;
//				break;
//			}
//		}
//		
//		//无书可借
//		if (one == null) return null;
//		
//		WebBookDetail wb = this.getBookInfo(isbn);
//		if (wb == null) return null;
//		
//		//放入缓存
//		getJedis().set(KEY_PRE_BORROW_PREFIX + one.getId(), Long.toString(userId));
//		getJedis().expire(KEY_PRE_BORROW_PREFIX + one.getId(), KEY_PRE_BORROW_EXPIRE);
//		
//		return one;
//	}
//	
//	public LimerBookInfo borrowBook(long userId, long limerBookId) {
//		//判断该书是否可借
//		LimerBookInfo one =null;
//		int inlibNum = 0;
//		LimerBookInfo[] lbsArr = lbiDB.getLimerBooksByIsbn(isbn);
//		for (LimerBookInfo lbs : lbsArr) {
//			if (lbs.getStatus() == LimerConstants.LIMER_BOOK_STATUS_READY) {
//				//可以借
//				if (one == null) one = lbs;
//				inlibNum++;
//			}
//		}
//		
//		//无书可借
//		if (one == null) return null;
//		
//		WebBookDetail wb = this.getBookInfo(isbn);
//		if (wb == null) return null;
//		
//		//更新书籍状态
//		one.setStatus(LimerConstants.LIMER_BOOK_STATUS_BUSY);
//		one.setLastUpdateTime(System.currentTimeMillis());
//		lbiDB.updateLimerBookInfo(one);
//		
//		//产生一条借书记录
//		BorrowRecord br = new BorrowRecord();
//		br.setBorrowTime(System.currentTimeMillis());
//		br.setLimerBookId(one.getId());
//		br.setStatus(LimerConstants.BORROW_STATUS_ING);
//		br.setUserId(userId);
//		brDB.addBorrowRecord(br);
//		br = brDB.getBorrowRecordByUserBook(br.getLimerBookId(), userId);
//		
//		return one;
//	}
	
	public WebOrder[] getMyOrders(long userId) {
		Order[] orders = orderDB.getOrderByUserId(userId);
		LinkedList<WebOrder> myOrderList = new LinkedList<WebOrder>();
		
		try {
			for (Order o : orders) {
				if (!LimerConstants.isOrderSuccess(o.getStatus())) continue;
				
				WebOrder wo = new WebOrder();
				
				wo.setOrderTime(TextUtil.formatTime3(o.getOrderStartTime()));
				wo.setStatus(o.getStatus());
				wo.setMchTradeNo(o.getMchTradeNo());
				wo.setTotalFee(o.getTotalFee());
				wo.setRealFee(o.getRealFee());
				wo.setStatusDesc(LimerConstants.explainOrderStatus(o.getStatus()));
				wo.setDeposit(LimerConstants.DEPOSIT_FEE);
				wo.setProductId(o.getProductId());
				wo.setProductDesc(LimerConstants.PRODUCT_DESC_MEM_MONTH);
				wo.setWxTradeNo(o.getWxTradeNo());
				
				try {
					int depositFee = JSONUtil.getInt(new JSONObject(o.getExtraJson()), "depositFee");
					wo.setDeposit(depositFee);
				} catch (Exception ex) {}
				
				myOrderList.add(wo);
			}
		} catch(Exception e) {
			LogUtil.WEB_LOG.warn("getMyOrders(userId="+ userId+") exception: ", e);
		}
		return myOrderList.toArray(new WebOrder[0]);
	}
	
	public void createUserIfNotExist(JSONObject o) {

		//判断用户是否存在，如果不存在，则创建用户
		String unionId = JSONUtil.getString(o, "unionId");
		String avatarUrl = JSONUtil.getString(o, "avatarUrl");
		String nickName = JSONUtil.getString(o, "nickName");
		String openId = JSONUtil.getString(o, "openId");
		String province = JSONUtil.getString(o, "province");
		String city = JSONUtil.getString(o, "city");
		int gender = JSONUtil.getInt(o, "gender");
		
		if (unionId.length() == 0) return;
		
		long userId = getUserIdByUnionId(WxConstants.MINIPROGRAM_APPID, unionId);
		if (userId > 0) return;
		
		//创建用户
		User user = new User();
		user.setCity(city);
		user.setCreateTime(System.currentTimeMillis());
		user.setLastLoginTime(System.currentTimeMillis());
		user.setLastUpdateTime(System.currentTimeMillis());
		user.setProvince(province);
		user.setSex(gender);
		user.setUserLogo(avatarUrl);
		user.setUserName(nickName);
		userDB.addUser(user);
		
		User lastU = userDB.getUserByName(nickName);
		user.setId(lastU.getId());
		//更新缓存
		getJedis().set(KEY_USER_PREFIX+lastU.getId(), lastU.toJSON());
		getJedis().expire(KEY_USER_PREFIX+lastU.getId(), KEY_USER_EXPIRE);
		
		UserAuth ua = new UserAuth();
		ua.setAppId(LimerConstants.MINIPROGRAM_APPID);
		ua.setCreateTime(System.currentTimeMillis());
		ua.setOpenId(openId);
		ua.setUnionId(unionId);
		ua.setUserId(user.getId());
		userAuthDB.addUserAuth(ua);
		getJedis().set(KEY_USER_AUTH_PREFIX+ unionId, Long.toString(lastU.getId()));
		getJedis().expire(KEY_USER_AUTH_PREFIX+ unionId, KEY_USER_AUTH_EXPIRE);
		
		LogUtil.STAT_LOG.info("[CREATE_URSER] ["+ lastU.getId() +"] ["+ nickName +"] ["+ openId +"] ["+ unionId +"]");
		
	}
	
//	public WebJSONArray borrowBooks(String[] isbnArr, WebUser u, String ip) {
//		if (isbnArr == null ||  isbnArr.length == 0 || u == null) return null;
//		
//		JSONArray resArr = new JSONArray();
//		synchronized (this) {
//			try {
//				int successNum = 0;
//				JSONArray limerBookIds = new JSONArray();
//				for (String isbn : isbnArr) {
//					LimerBookInfo lbi = borrowOneBook(isbn, u.getUserId());
//					if (lbi == null) {
//						resArr.put(new WebJSONObject(false, "借书失败"));
//					} else {
//						successNum++;
//						limerBookIds.put(Long.toString(lbi.getId()));
//						resArr.put(new WebJSONObject(true, "借书成功", lbi.toJSON()));
//					}
//				}
//				
//				//创建一个订单TODO
//			} catch(Throwable t) {
//				LogUtil.WEB_LOG.warn("Exception when borrowBook(books="+ Arrays.toString(isbnArr) +", userId=" + u.getUserId()+")", t);
//				return null;
//			}
//			
//			return new WebJSONArray(resArr.toString());
//		}
//	}
	
	
	
	//这个用户是否已捐赠过这本书
	public boolean isDonatedBook(long userId, String isbn) {
		LimerBookInfo lbi = lbiDB.getRecentDonateBook(isbn, userId);
		
		if (lbi == null) return false;
		return true;//如果用户已捐赠，且已取回，则也算捐赠过。
	}
	
	public User getUserInfo(long userId) {
		//先从jedis取
		String json = getJedis().get(KEY_USER_PREFIX + userId);
		if (json != null) {
			try {
				JSONObject o = new JSONObject(json);
				if (o != null && !o.has("error")) {
					return User.parseJSON(o);
				}
			} catch (Exception e) {
				LogUtil.WEB_LOG.warn("jedis hget User error, userId=" + userId + ":" + json);
				return null;
			}
		}
		
		//去数据库里获取
		User b = userDB.getUserById(userId);
		if (b == null) {
			LogUtil.WEB_LOG.warn("read from UserDB error,no userId=" + userId);
			return null;
		}
		
		//存入jedis
		getJedis().set(KEY_USER_PREFIX+userId, b.toJSON());
		getJedis().expire(KEY_USER_PREFIX+userId, KEY_USER_EXPIRE);
		return b;
		
	}
	
	
//	public boolean returnBook(long userId, long limerBookId) {
//		boolean res = false;
//		synchronized (this) {
//			try {
//				BorrowRecord br = brDB.getBorrowRecordByUserBook(limerBookId, userId);
//				if (br == null) {
//					LogUtil.WEB_LOG.error("[return book but no record] [userId="+ userId +"] [limerBookId="+ limerBookId +"]");
//					return false;
//				}
//				
//				br.setStatus(LimerConstants.BORROW_STATUS_RETURNED);
//				br.setReturnTime(System.currentTimeMillis());
//				res = brDB.updateBorrowRecord(br);
//				if (res) {
//					LogUtil.WEB_LOG.info("[RETURN_BOOK_SUCCESS] [userId=]"+ userId +"] [limerBookId="+ limerBookId +"]");
//					
//					//更新书籍状态
//					LimerBookInfo lbi = lbiDB.getLimerBookInfoById(limerBookId);
//					if (lbi == null) {
//						LogUtil.WEB_LOG.error("[return book but no book status] [userId="+ userId +"] [limerBookId="+ limerBookId +"]");
//						return false;
//					}
//					lbi.setStatus(LimerConstants.LIMER_BOOK_STATUS_READY);
//					lbiDB.updateLimerBookInfo(lbi);
//
//					//更新缓存
//					getJedis().set(KEY_BOOK_STATUS_PREFIX+limerBookId, lbi.toJSON());
//				}
//			} catch(Throwable e) {
//				LogUtil.WEB_LOG.warn("[returnBook error: "+ "[userId=]"+ userId +"] [limerBookId="+ limerBookId +"]", e);
//			}
//		}
//		return res;
//	}
	
//	public LimerBookInfo getLimerBookStatus(long limerBookId) {
//		//先从jedis取
//		String bookJson = getJedis().get(KEY_BOOK_STATUS_PREFIX + limerBookId);
//		if (bookJson != null) {
//			try {
//				JSONObject o = new JSONObject(bookJson);
//				if (o != null && !o.has("error")) {
//					return LimerBookInfo.parseJSON(o);
//				}
//			} catch (Exception e) {
//				return null;
//			}
//		}
//		
//		//去数据库里获取
//		LimerBookInfo b = lbiDB.getLimerBookInfoById(limerBookId);
//		if (b == null) return null;
//		
//		//存入jedis
//		getJedis().set(KEY_BOOK_STATUS_PREFIX + limerBookId, b.toJSON());
//		
//		return b;
//	}
	
	
	
	public WebBorrowBook[] getMyBorrowBooks(long userId) {
		BorrowRecord[] brs = brDB.getBorrowRecordByUserId(userId);
		LinkedList<WebBorrowBook> wbbList = new LinkedList<WebBorrowBook>();
		for (BorrowRecord br: brs) {
			WebBorrowBook wbb = new WebBorrowBook();
			wbb.setLimerBookId(br.getLimerBookId());
			
			WebBookDetail b = this.getBookInfo(br.getIsbn());
			wbb.setAuthor(b.getBook().getAuthorAsString());
			wbb.setBookId(b.getBook().getId());
			wbb.setBorrowTime(br.getBorrowTime());
			wbb.setCoverUrl(b.getBook().getCoverUrl());
			wbb.setIsbn(br.getIsbn());
			
			LimerBookInfo lbi = this.getLimerBookInfo(br.getLimerBookId());
			if (lbi.isIsbnSingle()) {
				wbb.setTitle(lbi.getBookTitle());
			} else {
				wbb.setTitle(b.getBook().getTitle() + "："+ lbi.getBookTitle());
			}
			wbb.setStatus(br.getStatus());
			wbb.setStatusDesc(LimerConstants.explainBorrowBookStatus(br.getStatus()));
			
			wbbList.add(wbb);
		}
		return wbbList.toArray(new WebBorrowBook[0]);
	}
	
//	public WebDonateBook[] getMyDonateBooks(long userId) {
//		LimerBookInfo[] lbis = lbiDB.getLimerBooksByDonateUser(userId);
//		LinkedList<WebDonateBook> wbbList = new LinkedList<WebDonateBook>();
//		for (LimerBookInfo b: lbis) {
//			WebDonateBook wbb = new WebDonateBook();
//			wbb.setLimerBookId(b.getId());
//			
//			WebBookDetail wb = this.getBookInfo(wbb.getIsbn());
//			LimerBookInfo lbs = this.getLimerBookStatus(b.getId());
//			if (lbs == null) {
//				LogUtil.WEB_LOG.error("no status of limer book: [id="+ b.getId() +"]");
//				continue;//有问题
//			}
//			
//			wbb.setInfo(wb, b.getId(), lbs.getStatus(), LimerConstants.explainDonateBookStatus(lbs.getStatus()));
//			wbbList.add(wbb);
//		}
//		return wbbList.toArray(new WebDonateBook[0]);
//	}
	
	public WebUserCenterInfo getWebUserCenterInfo(long userId, String unionId) {
		
		BorrowRecord[] brs = brDB.getBorrowRecordByUserId(userId);
		LimerBookInfo[] lbis = lbiDB.getLimerBooksByDonateUser(userId);
		UserScore us = userScoreDB.getUserScoreByUserId(userId);
		
		WebUserCenterInfo wuc = new WebUserCenterInfo();
		wuc.setBorrowNum(brs.length);
		wuc.setDonateNum(lbis.length);//捐赠又取回的要不要排除掉
		wuc.setUserId(userId);
		wuc.setUnionId(unionId);
		wuc.setUserScore(us == null ? 0 : us.getScore());
		return wuc;
	}
	
	//直接取数据库
//	public WebBookStatus getWebBookStatus(String isbn) {
//		//不从jedis里取了，因为状态一直在发生改变，而且读取不频繁，可以直接读写数据库
//		//去数据库里获取
//		LimerBookInfo[] lbsArr = lbiDB.getLimerBooksByIsbn(isbn);
//		LinkedList<Long> ids = new LinkedList<Long>();
//		int inlibNum = 0;
//		for (LimerBookInfo lbs: lbsArr) {
//			if (lbs.getStatus() != LimerConstants.LIMER_BOOK_STATUS_READY) continue;
//			inlibNum ++;
//			ids.add(lbs.getId());
//		}
//		
//		WebBookStatus wbs = new WebBookStatus();
//		wbs.setIsbn(isbn);
//		wbs.setInlibNum(inlibNum);
//		wbs.setLimerBookIds(ids.toArray(new Long[0]));
//		
//		if (inlibNum == 0) {
//			wbs.setStatus(LimerConstants.LIMER_BOOK_STATUS_BUSY);
//		} else {
//			wbs.setStatus(LimerConstants.LIMER_BOOK_STATUS_READY);
//		}
//		return wbs;
//	}
	
	
	
	public List<WebBookDetail> getRecentBooks(String tag, int start, int len) {
		List<WebBookDetail> resultList = new LinkedList<WebBookDetail>();
		
		Set<String> list = getJedis().zrevrange(KEY_RECENT_BOOKS+"_"+TextUtil.MD5(tag), start, start+len-1);
		LogUtil.WEB_LOG.debug("getRecentBooks() read from redis: "+KEY_RECENT_BOOKS+ "_"+ tag +": "+list.size());
		if (list == null || list.size() == 0) return resultList;
		
		LogUtil.WEB_LOG.debug("getRecentBooks("+tag+","+start+","+len+"), return "+ list.size());
		for (String isbn: list) {
			WebBookDetail wb = this.getBookInfo(isbn);
			if (wb == null) continue;
			resultList.add(wb);
		}
		return resultList;
	}
	
	public List<WebBookListDetail> getRecentBookLists(String tag, int start, int len) {
		List<WebBookListDetail> resultList = new LinkedList<WebBookListDetail>();
		
		Set<String> list = getJedis().zrevrange(KEY_RECENT_BOOKLISTS+ (tag.length() > 0 ? ("_" +tag) : ""), start, start+len-1);
		LogUtil.WEB_LOG.debug("getRecentBookLists() read from redis: "+KEY_RECENT_BOOKLISTS + (tag.length() > 0 ? "_" : "")+ tag +": "+list.size());
		if (list == null || list.size() == 0) return resultList;
		
		LogUtil.WEB_LOG.debug("getRecentBookLists("+tag+","+start+","+len+"), return "+ list.size());
		for (String s: list) {
			WebBookListDetail wb = WebBookListDetail.parseJSON(s);
			resultList.add(wb);
		}
		return resultList;
	}
	
	public WebBookListDetail getBookListDetail(long id) {
		if (id <= 0) return null;
		
		//直接从数据库取
		BookList b = bookListDB.getBookListById(id);
		if (b == null) return null;
		
		//获取booklist的全部书籍列表
		BookListItem[] items = booklistItemDB.getBookListItems(b.getId(), 0, 100);
		
		WebBookListDetail wb = new WebBookListDetail();
		wb.setDesc(b.getDesc());
		wb.setId(b.getId());
		wb.setSubTitle(b.getSubTitle());
		wb.setTitle(b.getTitle());
		wb.setType(b.getType());

		JSONArray bookJsonArr = new JSONArray();
		try {
			for (BookListItem item : items) {
				WebBookDetail bd = this.getBookInfo(item.getIsbn());
				bookJsonArr.put(new JSONObject(bd.toWebJSON()));
			}
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("getBookListDetail("+id+") exception", e);
		}
		wb.setBooks(bookJsonArr);
		return wb;
	}
	
	public WebBookDetail getBookInfo(String isbn) {
		if (isbn == null || isbn.trim().length() == 0) return null;
		if (isbn.length() != 10 && isbn.length() != 13) return null;
		
		//先从jedis取
		String bookJson = getJedis().get(KEY_BOOK_PREFIX + isbn);
		if (bookJson != null) {
			WebBookDetail wbd = new WebBookDetail();
			Book book = Book.parseFromDoubanJSON(bookJson);
			wbd.setBook(book);
			
			int commentNum = this.getBookCommentNum(isbn);
			wbd.setCommentNum(commentNum);
			int likeNum = this.getBookLikeNum(isbn);
			wbd.setLikeNum(likeNum);
			return wbd;
		}
		
		//再从数据库取
		Book b = bookDB.getBookByIsbn(isbn);
		
		if (b == null) {
			//没有，则从豆瓣api获取
			String json = DoubanUtil.getBookInfoByIsbn(isbn);
			if (json == null || json.trim().length() == 0) return null;
			
			//插入数据库。如果相同的isbn，目前可以插入多条记录。取出时取最新的一条
			b = new Book();
			b.setCreateTime(System.currentTimeMillis());
			b.setDoubanJson(json);
			if (b.getIsbn13() == null || b.getIsbn13().length() != 13 || b.getIsbn10() == null || b.getIsbn10().length() != 10) {
				LogUtil.WEB_LOG.info("[DOUBAN_BOOK_JSON_ERROR] ["+ isbn +"]");
				//json有问题
				return null;
			}
			bookDB.addBook(b);
		}
		
		WebBookDetail wbd = new WebBookDetail();
		wbd.setBook(b);
		
		int commentNum = this.getBookCommentNum(isbn);
		wbd.setCommentNum(commentNum);
		int likeNum = this.getBookLikeNum(isbn);
		wbd.setLikeNum(likeNum);
		
		//更新缓存
		getJedis().set(KEY_BOOK_PREFIX+ isbn, wbd.getBook().toDoubanJSON());
		getJedis().expire(KEY_BOOK_PREFIX+ isbn, KEY_BOOK_EXPIRE);
		
		//更新最近书籍缓存
		Map<String, Double> scoreMembers = new HashMap<String, Double>();
		scoreMembers.put(wbd.getBook().toDoubanJSON(), (double)wbd.computeScore());
		getJedis().zadd(KEY_RECENT_BOOKS, scoreMembers);
		for (String tag: wbd.getBook().getTagsString()) {
			getJedis().zadd(KEY_RECENT_BOOKS+"_"+TextUtil.MD5(tag), scoreMembers);
		}
		
		return wbd;
	}
	
	public LimerBookInfo getLimerBookInfo(long limerBookId) {
		String json = getJedis().get(KEY_LIMERBOOK_PREFIX + limerBookId);
		if (json != null) {
			return LimerBookInfo.parseJSON(json);
		}
		
		//从数据库里读
		LimerBookInfo lbi = lbiDB.getLimerBookInfoById(limerBookId);
		if (lbi == null) return null;
		
		//存入缓存
		getJedis().set(KEY_LIMERBOOK_PREFIX + limerBookId, lbi.toJSON());
		getJedis().expire(KEY_LIMERBOOK_PREFIX+ limerBookId, KEY_LIMERBOOK_EXPIRE);
		
		return lbi;
	}
	
//	public boolean addDonateBook(String isbn, long userId) {
//		if (isbn == null || isbn.trim().length() == 0 || userId <= 0) return false;
//		
//		synchronized (this) {
//			try {
//				
//				//判断isbn是否入库过，如果没有，则入库
//				WebBookDetail wbd = this.getBookInfo(isbn);
//				
//				//添加书的状态
//				LimerBookInfo lbi = new LimerBookInfo();
//				lbi.setIsbn(isbn);
//				lbi.setDonateUserId(userId);
//				lbi.setStatus(LimerConstants.LIMER_BOOK_STATUS_READY);
//				lbi.setDonateTime(System.currentTimeMillis());
//				lbi.setLastUpdateTime(System.currentTimeMillis());
//				boolean res = lbiDB.addLimerBookInfo(lbi);
//				if (!res) {
//					LogUtil.WEB_LOG.debug("add book info failed: " + lbi);
//					return false;
//				}
//				
//				LogUtil.STAT_LOG.info("[DONATE_BOOK] ["+ isbn +"] ["+ userId +"]");
//
//				//更新缓存
//				lbi = lbiDB.getRecentDonateBook(isbn, userId);
//				getJedis().set(KEY_BOOK_STATUS_PREFIX+ lbi.getId(), lbi.toJSON());
//				
//				//更新recentBooks缓存
//				//把这本书加入tag对应的缓存
//				String[] tags = wbd.getBook().getLimerTagsAsArray();
//				for (String tag : tags) {
//					Map<String, Double> sm = new HashMap<String, Double>();
//					sm.put(wbd.getBook().getIsbn(), (double)wbd.computeScore());
//					getJedis().zadd(KEY_RECENT_BOOKS+"_" + TextUtil.MD5(tag), sm);
//				}
//				
//				return res;
//			} catch(Throwable t) {
//				LogUtil.WEB_LOG.warn("Exception when addDonateBook(isbn="+ isbn +", userId=" + userId+")", t);
//				return false;
//			}
//		}
//	}
	
	public String getOpenId(String appId, String unionId) {
		UserAuth u = userAuthDB.getUserAuthByUnionId(appId, unionId);
		if (u == null) return null;
		
		return u.getOpenId();
	}
	
	public void addUserAuth(long userId, String appId, String openId, String unionId) {
		UserAuth o = new UserAuth();
		o.setAppId(appId);
		o.setCreateTime(System.currentTimeMillis());
		o.setOpenId(openId);
		o.setUnionId(unionId);
		o.setUserId(userId);
		userAuthDB.addUserAuth(o);
	}
	
	public long getUserIdByUnionId(String appId, String unionId) {
		//先从jedis取
		String strUserId = getJedis().get(KEY_USER_AUTH_PREFIX+ unionId);
		
		if (strUserId != null && strUserId.length() > 0) {
			try {
				return Long.parseLong(strUserId);
			} catch (Exception e) {
				
			}
		}
		
		//去数据库里获取
		UserAuth u = userAuthDB.getUserAuthByUnionId(appId, unionId);
		if (u == null) return 0;
		
		//存入jedis
		getJedis().set(KEY_USER_AUTH_PREFIX+ unionId, Long.toString(u.getUserId()));
		getJedis().expire(KEY_USER_AUTH_PREFIX+ unionId, KEY_USER_AUTH_EXPIRE);
		
		return u.getUserId();
	}
	
	public boolean updateUserLoginTime(long userId, User u) {
		if (userId == 0 || u == null) return false;
		
		u.setId(userId);
		u.setLastLoginTime(System.currentTimeMillis());
		
		boolean res = userDB.updateUser(u);
		if (res) {
			getJedis().set(KEY_USER_PREFIX+userId, u.toJSON());
			getJedis().expire(KEY_USER_PREFIX+userId, KEY_USER_EXPIRE);
		}
		return res;
	}
	
	public boolean updateUserInfo(long userId, User u) {
		if (userId == 0 || u == null) return false;
		
		u.setId(userId);
		u.setLastUpdateTime(System.currentTimeMillis());
		u.setLastLoginTime(System.currentTimeMillis());
		
		boolean res = userDB.updateUser(u);
		if (res) {
			getJedis().set(KEY_USER_PREFIX+userId, u.toJSON());
			getJedis().expire(KEY_USER_PREFIX+userId, KEY_USER_EXPIRE);
		}
		
		return res;
	}
	
	public WebUser getWebUser(String userName, String userLogo, String openId, String unionId) {
		
		long userId = 0;
		User u = null;
		synchronized (this) {
			try {
				userId = this.getUserIdByUnionId(WxConstants.MINIPROGRAM_APPID, unionId);
				if (userId == 0){
					//新用户
					long curTime = System.currentTimeMillis();
					u = new User();
					u.setCreateTime(curTime);
					u.setLastLoginTime(curTime);
					u.setLastUpdateTime(curTime);
					u.setUserLogo(userLogo);
					u.setUserName(userName);
					userDB.addUser(u);
					
					u = userDB.getUserByName(userName);
					userId = u.getId();
					getJedis().set(KEY_USER_PREFIX+userId, u.toJSON());
					getJedis().expire(KEY_USER_PREFIX+userId, KEY_USER_EXPIRE);
					LogUtil.info("[CREATE_USER] [userId="+ userId +"] [userName="+ userName +"]");
					
					UserAuth ua = new UserAuth();
					ua.setAppId(LimerConstants.MINIPROGRAM_APPID);
					ua.setCreateTime(System.currentTimeMillis());
					ua.setOpenId(openId);
					ua.setUnionId(unionId);
					ua.setUserId(userId);
					userAuthDB.addUserAuth(ua);
					getJedis().set(KEY_USER_AUTH_PREFIX+ unionId, Long.toString(userId));
					getJedis().expire(KEY_USER_AUTH_PREFIX+ unionId, KEY_USER_AUTH_EXPIRE);
					
					LogUtil.info("[CREATE_USER_AUTH] [userId="+ userId +"] [unionId="+ unionId +"] [appId="+ ua.getAppId() +"]");
				} else {
					u = this.getUserInfo(userId);
					LogUtil.info("[OLD_USER_VISIT] [userId=" + userId + "] [unionId="+ unionId +"]");
				}
			} catch (Throwable t) {
				LogUtil.WEB_LOG.warn("code2session check&createuser error: " + " unionId="+ unionId, t);
				return null;
			}
		}
		
		WebUser wu = WebUser.create(u, unionId, openId);
		return wu;
	}

	public void setBookDB(BookDB bookDB) {
		this.bookDB = bookDB;
	}

	public void setLbiDB(LimerBookInfoDB lbiDB) {
		this.lbiDB = lbiDB;
	}

	public void setUserScoreDB(UserScoreDB userScoreDB) {
		this.userScoreDB = userScoreDB;
	}

	public void setBrDB(BorrowRecordDB brDB) {
		this.brDB = brDB;
	}

	public void setUserDB(UserDB userDB) {
		this.userDB = userDB;
	}

	public void setUserAuthDB(UserAuthDB userAuthDB) {
		this.userAuthDB = userAuthDB;
	}

	public void setLtDB(LogisticsDB ltDB) {
		this.ltDB = ltDB;
	}

	public void setJedisMaxTotal(int jedisMaxTotal) {
		this.jedisMaxTotal = jedisMaxTotal;
	}

	public void setJedisMaxIdle(int jedisMaxIdle) {
		this.jedisMaxIdle = jedisMaxIdle;
	}

	public void setJedisMinIdle(int jedisMinIdle) {
		this.jedisMinIdle = jedisMinIdle;
	}

	public void setJedisHost(String jedisHost) {
		this.jedisHost = jedisHost;
	}

	public void setJedisPort(int jedisPort) {
		this.jedisPort = jedisPort;
	}

	public void setJedisPassword(String jedisPassword) {
		this.jedisPassword = jedisPassword;
	}

	public void setOrderDB(OrderDB orderDB) {
		this.orderDB = orderDB;
	}

	public void setBookListDB(BookListDB bookListDB) {
		this.bookListDB = bookListDB;
	}

	public void setActivityDB(ActivityDB activityDB) {
		this.activityDB = activityDB;
	}

	public void setCommentDB(BookCommentDB commentDB) {
		this.commentDB = commentDB;
	}

	public void setBooklistItemDB(BookListItemDB booklistItemDB) {
		this.booklistItemDB = booklistItemDB;
	}

	public void setChildDB(ChildDB childDB) {
		this.childDB = childDB;
	}

	public String getWxPayNotifyUrl() {
		return wxPayNotifyUrl;
	}

	public void setWxPayNotifyUrl(String wxPayNotifyUrl) {
		this.wxPayNotifyUrl = wxPayNotifyUrl;
	}

	public void setActionDB(UserMemberActionDB actionDB) {
		this.actionDB = actionDB;
	}
	
}
