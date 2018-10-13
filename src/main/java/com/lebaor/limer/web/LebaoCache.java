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
import com.lebaor.limer.task.OrderTask;
import com.lebaor.limer.web.data.*;
import com.lebaor.limer.web.data.WebOrder.WebOrderItem;
import com.lebaor.limer.data.*;
import com.lebaor.thirdpartyutils.DoubanUtil;
import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.webutils.HttpClientUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LebaoCache {
	BookDB bookDB;
	LimerBookInfoDB lbiDB;
	UserScoreDB userScoreDB;
	BorrowRecordDB brDB;
	UserDB userDB;
	UserAuthDB userAuthDB;
	LogisticsDB ltDB;
	OrderDB orderDB;
	
	private static final String KEY_RECENT_BOOKS = "recent_books"; //最近所有的书籍。WebBookDetail json list, 评分高的在前，缓存前1000个。
	private static final int KEY_RECENT_BOOKS_NUM = 1000;
	
	private static final String KEY_BOOK_DETAIL = "book_detail";//key,value key=isbn, value=book 缓存里不保证是全部的数据
	private static final String KEY_BOOK_STATUS = "book_status";//key,value key=limerBookId, value=limerbookInfo 缓存里不保证是全部的数据
	private static final String KEY_USER_INFO = "user_info";//key,value key=userId value=userprofile json 缓存里不保证是全部的数据
	private static final String KEY_USER_AUTH = "user_auth";//key,value key=unionId value=userId 缓存里不保证是全部的数据
	
	private static final int BOOK_PAGE_NUM = 10;//每页展示10本书
	
	int jedisMaxTotal;
	int jedisMaxIdle;
	int jedisMinIdle;
	String jedisHost;
	int jedisPort;
	String jedisPassword;
	
	static JedisPool jedisPool;
	
	static LebaoCache _self;
	
	public LebaoCache() {
		_self = this;
	}
	
	public static LebaoCache getInstance() {
		return _self;
	}
	
	public void init() {
		JedisPoolConfig c = new JedisPoolConfig();
		c.setMaxIdle(jedisMaxIdle);
		c.setMinIdle(jedisMinIdle);
		c.setMaxTotal(jedisMaxTotal);
		
		jedisPool = new JedisPool(c, jedisHost, jedisPort);
		LogUtil.WEB_LOG.info("JedisPool inited, host=" + jedisHost + ":" + jedisPort);
		
		//从jedis里取
		try {
			if (!getJedis().exists(KEY_RECENT_BOOKS)) {
				//最近1000本书籍状态
				Map<String, Double> scoreMembers = new HashMap<String, Double>();
				Book[] bookArr = bookDB.getBooks(0, KEY_RECENT_BOOKS_NUM);
				LogUtil.WEB_LOG.debug("getRecentReadyBooks() read from db:"+bookArr.length);
				
				for (Book b : bookArr) {
					WebBookDetail wbd = this.getBookInfo(b.getIsbn13());
					
					Map<String, Double> sm = new HashMap<String, Double>();
					sm.put(wbd.toJSON(), (double)wbd.computeScore());
					scoreMembers.put(wbd.toJSON(), (double)wbd.computeScore());
					
					for (String t : wbd.getBook().getTagsString()) {
						getJedis().zadd(KEY_RECENT_BOOKS+"_" + TextUtil.MD5(t), sm);
						LogUtil.WEB_LOG.debug("getRecentReadyBooks() write to redis: "+ KEY_RECENT_BOOKS+"_" +t+": "+ sm.size());
					}
				}
				getJedis().zadd(KEY_RECENT_BOOKS, scoreMembers);
				LogUtil.WEB_LOG.debug("getRecentReadyBooks() write to redis: "+KEY_RECENT_BOOKS+ " "+scoreMembers.size());
			}
		} catch(Throwable t) {
			LogUtil.WEB_LOG.warn("getRecentReadyBooks(0,"+KEY_RECENT_BOOKS_NUM+") error", t);
		}
		
		LogUtil.WEB_LOG.info("init Jedis recent_books success.");
	}
	
	public static Jedis getJedis() {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(getInstance().jedisPassword);
		return jedis;
	}
	
	public LimerBookInfo borrowOneBook(String isbn, long userId) {
		//判断该书是否可借
		LimerBookInfo one =null;
		int inlibNum = 0;
		LimerBookInfo[] lbsArr = lbiDB.getLimerBooksByIsbn(isbn);
		for (LimerBookInfo lbs : lbsArr) {
			if (lbs.getStatus() == LimerConstants.LIMER_BOOK_STATUS_READY) {
				//可以借
				if (one == null) one = lbs;
				inlibNum++;
			}
		}
		
		//无书可借
		if (one == null) return null;
		
		WebBookDetail wb = this.getBookInfo(isbn);
		if (wb == null) return null;
		
		//更新书籍状态
		one.setStatus(LimerConstants.LIMER_BOOK_STATUS_BUSY);
		one.setLastUpdateTime(System.currentTimeMillis());
		lbiDB.updateLimerBookInfo(one);
		
		//产生一条借书记录
		BorrowRecord br = new BorrowRecord();
		br.setBorrowTime(System.currentTimeMillis());
		br.setLimerBookId(one.getId());
		br.setStatus(LimerConstants.BORROW_STATUS_ING);
		br.setUserId(userId);
		brDB.addBorrowRecord(br);
		br = brDB.getBorrowRecordByUserBook(br.getLimerBookId(), userId);
		
		return one;
	}
	
	public WebOrder[] getMyOrders(long userId) {
		Order[] orders = orderDB.getOrderByUserId(userId);
		LinkedList<WebOrder> myOrderList = new LinkedList<WebOrder>();
		
		try {
			for (Order o : orders) {
				WebOrder wo = new WebOrder();
				wo.setOrderTime(TextUtil.formatTime3(o.getOrderStartTime()));
				wo.setStatus(o.getStatus());
				wo.setMchTradeNo(o.getMchTradeNo());
				wo.setTotalFee(o.getTotalFee());
				wo.setRealFee(o.getRealFee());
				wo.setStatusDesc(LimerConstants.explainOrderStatus(o.getStatus()));
				JSONArray items = new JSONArray();
				
				JSONArray ids = new JSONArray(o.getLimerBookIdsJson());
				for (int i = 0;i < ids.length(); i++) {
					long limerBookId = Long.parseLong(ids.getString(i));
					LimerBookInfo lbi = this.getLimerBookStatus(limerBookId);
					WebBookDetail wbd = this.getBookInfo(lbi.getIsbn());
					
					WebOrderItem woi = new WebOrderItem();
					woi.setAuthor(wbd.getBook().getAuthorAsString());
					woi.setCoverUrl(wbd.getBook().getCoverUrl());
					woi.setIsbn(lbi.getIsbn());
					woi.setLimerBookId(limerBookId);
					woi.setTitle(wbd.getBook().getTitle());
					
					items.put(woi.toJSONObject());
				}
				
				wo.setItems(items);
				myOrderList.add(wo);
			}
		} catch(Exception e) {
			LogUtil.WEB_LOG.warn("getMyOrders(userId="+ userId+") exception: ", e);
		}
		return myOrderList.toArray(new WebOrder[0]);
	}
	
	public WebJSONArray borrowBooks(String[] isbnArr, WebUser u, String ip) {
		if (isbnArr == null ||  isbnArr.length == 0 || u == null) return null;
		
		JSONArray resArr = new JSONArray();
		synchronized (this) {
			try {
				int successNum = 0;
				JSONArray limerBookIds = new JSONArray();
				for (String isbn : isbnArr) {
					LimerBookInfo lbi = borrowOneBook(isbn, u.getUserId());
					if (lbi == null) {
						resArr.put(new WebJSONObject(false, "借书失败"));
					} else {
						successNum++;
						limerBookIds.put(Long.toString(lbi.getId()));
						resArr.put(new WebJSONObject(true, "借书成功", lbi.toJSON()));
					}
				}
				
				//创建一个订单
				Order order = new Order();
				order.setBookNum(successNum);
				order.setIp(ip);
				order.setLimerBookIdsJson(limerBookIds.toString());
				order.setUserId(u.getUserId());
				order.setMchTradeNo(Order.genMchTradeNo(u.getUserId()));
				//order.setOpenid(u.getOpenid());//TODO
				order.setUnionid(u.getUnionId());
				order.setOrderStartTime(System.currentTimeMillis());
				int fee = 0;
				order.setRealFee(fee);
				order.setStatus(LimerConstants.ORDER_STATUS_NOT_XIADAN);
				order.setTitle("");
				order.setTotalFee(fee);
				
				//加入队列
				OrderTask.addOrder(order);
			} catch(Throwable t) {
				LogUtil.WEB_LOG.warn("Exception when borrowBook(books="+ Arrays.toString(isbnArr) +", userId=" + u.getUserId()+")", t);
				return null;
			}
			
			return new WebJSONArray(resArr.toString());
		}
	}
	
	
	
	//这个用户是否已捐赠过这本书
	public boolean isDonatedBook(long userId, String isbn) {
		LimerBookInfo lbi = lbiDB.getRecentDonateBook(isbn, userId);
		
		if (lbi == null) return false;
		return true;//如果用户已捐赠，且已取回，则也算捐赠过。
	}
	
	public User getUserInfo(long userId) {
		//先从jedis取
		String bookJson = getJedis().hget(KEY_USER_INFO, Long.toString(userId));
		if (bookJson != null) {
			try {
				JSONObject o = new JSONObject(bookJson);
				if (o != null && !o.has("error")) {
					return User.parseJSON(o);
				}
			} catch (Exception e) {
				return null;
			}
		}
		
		//去数据库里获取
		User b = userDB.getUserById(userId);
		if (b == null) return null;
		
		//存入jedis
		getJedis().hset(KEY_USER_INFO, Long.toString(userId), b.toJSON());
		
		return b;
		
	}
	
	
	public boolean returnBook(long userId, long limerBookId) {
		boolean res = false;
		synchronized (this) {
			try {
				BorrowRecord br = brDB.getBorrowRecordByUserBook(limerBookId, userId);
				if (br == null) {
					LogUtil.WEB_LOG.error("[return book but no record] [userId="+ userId +"] [limerBookId="+ limerBookId +"]");
					return false;
				}
				
				br.setStatus(LimerConstants.BORROW_STATUS_RETURNED);
				br.setReturnTime(System.currentTimeMillis());
				res = brDB.updateBorrowRecord(br);
				if (res) {
					LogUtil.WEB_LOG.info("[RETURN_BOOK_SUCCESS] [userId=]"+ userId +"] [limerBookId="+ limerBookId +"]");
					
					//更新书籍状态
					LimerBookInfo lbi = lbiDB.getLimerBookInfoById(limerBookId);
					if (lbi == null) {
						LogUtil.WEB_LOG.error("[return book but no book status] [userId="+ userId +"] [limerBookId="+ limerBookId +"]");
						return false;
					}
					lbi.setStatus(LimerConstants.LIMER_BOOK_STATUS_READY);
					lbiDB.updateLimerBookInfo(lbi);

					//更新缓存
					getJedis().hset(KEY_BOOK_STATUS, Long.toString(limerBookId), lbi.toJSON());
				}
			} catch(Throwable e) {
				LogUtil.WEB_LOG.warn("[returnBook error: "+ "[userId=]"+ userId +"] [limerBookId="+ limerBookId +"]", e);
			}
		}
		return res;
	}
	
	public LimerBookInfo getLimerBookStatus(long limerBookId) {
		//加缓存 TODO
		return lbiDB.getLimerBookInfoById(limerBookId);
	}
	
	public WebBorrowBook[] getMyBorrowBooks(long userId) {
		BorrowRecord[] brs = brDB.getBorrowRecordByUserId(userId);
		LinkedList<WebBorrowBook> wbbList = new LinkedList<WebBorrowBook>();
		for (BorrowRecord br: brs) {
			WebBorrowBook wbb = new WebBorrowBook();
			wbb.setLimerBookId(br.getLimerBookId());
			
			WebBookDetail wb = this.getBookInfo(wbb.getIsbn());
			LimerBookInfo lbs = this.getLimerBookStatus(br.getLimerBookId());
			if (lbs == null) {
				LogUtil.WEB_LOG.error("no status of limer book: [id="+ br.getLimerBookId() +"]");
				continue;//有问题
			}
			
			wbb.setInfo(wb, br.getLimerBookId(), lbs.getStatus(), 
					LimerConstants.explainBorrowBookStatus(lbs.getStatus()), br.getBorrowTime());
			wbbList.add(wbb);
		}
		return wbbList.toArray(new WebBorrowBook[0]);
	}
	
	public WebDonateBook[] getMyDonateBooks(long userId) {
		LimerBookInfo[] lbis = lbiDB.getLimerBooksByDonateUser(userId);
		LinkedList<WebDonateBook> wbbList = new LinkedList<WebDonateBook>();
		for (LimerBookInfo b: lbis) {
			WebDonateBook wbb = new WebDonateBook();
			wbb.setLimerBookId(b.getId());
			
			WebBookDetail wb = this.getBookInfo(wbb.getIsbn());
			LimerBookInfo lbs = this.getLimerBookStatus(b.getId());
			if (lbs == null) {
				LogUtil.WEB_LOG.error("no status of limer book: [id="+ b.getId() +"]");
				continue;//有问题
			}
			
			wbb.setInfo(wb, b.getId(), lbs.getStatus(), LimerConstants.explainDonateBookStatus(lbs.getStatus()));
			wbbList.add(wbb);
		}
		return wbbList.toArray(new WebDonateBook[0]);
	}
	
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
	public WebBookStatus getWebBookStatus(String isbn) {
		//不从jedis里取了，因为状态一直在发生改变，而且读取不频繁，可以直接读写数据库
		//去数据库里获取
		LimerBookInfo[] lbsArr = lbiDB.getLimerBooksByIsbn(isbn);
		LinkedList<Long> ids = new LinkedList<Long>();
		int inlibNum = 0;
		for (LimerBookInfo lbs: lbsArr) {
			if (lbs.getStatus() != LimerConstants.LIMER_BOOK_STATUS_READY) continue;
			inlibNum ++;
			ids.add(lbs.getId());
		}
		
		WebBookStatus wbs = new WebBookStatus();
		wbs.setIsbn(isbn);
		wbs.setInlibNum(inlibNum);
		wbs.setLimerBookIds(ids.toArray(new Long[0]));
		
		if (inlibNum == 0) {
			wbs.setStatus(LimerConstants.LIMER_BOOK_STATUS_BUSY);
		} else {
			wbs.setStatus(LimerConstants.LIMER_BOOK_STATUS_READY);
		}
		return wbs;
	}
	
	
	
	public List<WebBookDetail> getRecentBooks(String tag, int start, int len) {
		List<WebBookDetail> resultList = new LinkedList<WebBookDetail>();
		
		Set<String> list = getJedis().zrevrange(KEY_RECENT_BOOKS+"_"+TextUtil.MD5(tag), start, len);
		LogUtil.WEB_LOG.debug("getRecentReadyBooks() read from redis: "+KEY_RECENT_BOOKS+ "_"+ tag +": "+list.size());
		if (list == null || list.size() == 0) return resultList;
		
		LogUtil.WEB_LOG.debug("getRecentBooks("+tag+","+start+","+len+"), return "+ list.size());
		for (String s: list) {
			WebBookDetail wb = new WebBookDetail();
			wb.setBook(Book.parseFromDoubanJSON(s));
			resultList.add(wb);
		}
		return resultList;
	}
	
	public WebBookDetail getBookInfo(String isbn) {
		if (isbn == null || isbn.trim().length() == 0) return null;
		if (isbn.length() != 10 && isbn.length() != 13) return null;
		
		//先从jedis取
		String bookJson = getJedis().hget(KEY_BOOK_DETAIL, isbn);
		if (bookJson != null) {
			WebBookDetail wbd = new WebBookDetail();
			Book book = Book.parseFromDoubanJSON(bookJson);
			wbd.setBook(book);
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
			bookDB.addBook(b);
		}
		
		WebBookDetail wbd = new WebBookDetail();
		wbd.setBook(b);
		
		//更新缓存
		getJedis().hset(KEY_BOOK_DETAIL, isbn, wbd.toJSON());
		
		//更新最近书籍缓存
		Map<String, Double> scoreMembers = new HashMap<String, Double>();
		scoreMembers.put(wbd.toJSON(), (double)wbd.computeScore());
		getJedis().zadd(KEY_RECENT_BOOKS, scoreMembers);
		for (String tag: wbd.getBook().getTagsString()) {
			getJedis().zadd(KEY_RECENT_BOOKS+"_"+TextUtil.MD5(tag), scoreMembers);
		}
		
		return wbd;
	}
	
	public boolean addDonateBook(String isbn, long userId) {
		if (isbn == null || isbn.trim().length() == 0 || userId <= 0) return false;
		
		synchronized (this) {
			try {
				LimerBookInfo[] lbiArr = lbiDB.getLimerBooksByDonateUser(userId);
				for (LimerBookInfo lbi: lbiArr) {
					if (isbn.equals(lbi.getIsbn())) {
						//已捐赠过此书
						return false;
					}
				}
				
				//判断isbn是否入库过，如果没有，则入库
				WebBookDetail wbd = this.getBookInfo(isbn);
				
				//添加书的状态
				LimerBookInfo lbi = new LimerBookInfo();
				lbi.setIsbn(isbn);
				lbi.setDonateUserId(userId);
				lbi.setStatus(LimerConstants.LIMER_BOOK_STATUS_READY);
				lbi.setDonateTime(System.currentTimeMillis());
				lbi.setLastUpdateTime(System.currentTimeMillis());
				boolean res = lbiDB.addLimerBookInfo(lbi);
				if (!res) return false;
				
				LogUtil.WEB_LOG.info("[DONATE_BOOK_SUCCESS] [isbn="+ isbn +"] [userId="+ userId +"]");

				//更新缓存
				lbi = lbiDB.getRecentDonateBook(isbn, userId);
				getJedis().hset(KEY_BOOK_STATUS, Long.toString(lbi.getId()), lbi.toJSON());
				
				return res;
			} catch(Throwable t) {
				LogUtil.WEB_LOG.warn("Exception when addDonateBook(isbn="+ isbn +", userId=" + userId+")", t);
				return false;
			}
		}
	}
	
	public long getUserIdByUnionId(String unionId) {
		//先从jedis取
		String strUserId = getJedis().hget(KEY_USER_AUTH, unionId);
		
		if (strUserId != null && strUserId.length() > 0) {
			try {
				return Long.parseLong(strUserId);
			} catch (Exception e) {
				
			}
		}
		
		//去数据库里获取
		UserAuth u = userAuthDB.getUserAuthByUnionId(unionId);
		if (u == null) return 0;
		
		//存入jedis
		getJedis().hset(KEY_USER_AUTH, unionId, Long.toString(u.getUserId()));
		
		return u.getUserId();
	}
	
	public boolean updateUserLoginTime(long userId, User u) {
		if (userId == 0 || u == null) return false;
		
		u.setId(userId);
		u.setLastLoginTime(System.currentTimeMillis());
		
		boolean res = userDB.updateUser(u);
		if (res) {
			getJedis().hset(KEY_USER_INFO, Long.toString(userId), u.toJSON());
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
			getJedis().hset(KEY_USER_INFO, Long.toString(userId), u.toJSON());
		}
		
		return res;
	}
	
	public WebUser code2Session(String userName, String userLogo, String code) {
		String json = HttpClientUtil.doGet(LimerConstants.CODE2SESSION_URL + "?appid=" + LimerConstants.MINIPROGRAM_APPID
				+ "&secret=" + LimerConstants.MINIPROGRAM_APPSECRET 
				+ "&js_code=" + code
				+ "&grant_type=authorization_code");
		
		String openId = null;
		String sessionKey = null;
		String unionId = null;
		try {
			JSONObject o = new JSONObject(json);
			String errCode = o.getString("errcode");
			if (!errCode.equals("0")) {
				LogUtil.WEB_LOG.warn("code2session error: " + " code="+ code +", return json=" + json);
				return null;
			}
			
			openId = o.getString("openid");
			sessionKey = o.getString("session_key");
			unionId = o.getString("unionid");
		} catch (Exception e ) {
			LogUtil.WEB_LOG.warn("code2session error: " + " code="+ code +", return json=" + json, e);
			return null;
		}
		
		long userId = 0;
		User u = null;
		synchronized (this) {
			try {
				userId = this.getUserIdByUnionId(unionId);
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
					getJedis().hset(KEY_USER_INFO, Long.toString(userId), u.toJSON());
					LogUtil.info("[CREATE_USER] [userId="+ userId +"] [userName="+ userName +"]");
					
					UserAuth ua = new UserAuth();
					ua.setAppId(LimerConstants.MINIPROGRAM_APPID);
					ua.setCreateTime(System.currentTimeMillis());
					ua.setOpenId(openId);
					ua.setUnionId(unionId);
					ua.setUserId(userId);
					userAuthDB.addUserAuth(ua);
					getJedis().hset(KEY_USER_AUTH, unionId, Long.toString(userId));
					LogUtil.info("[CREATE_USER_AUTH] [userId="+ userId +"] [unionId="+ unionId +"] [openId="+ openId+"] [appId="+ ua.getAppId() +"]");
				} else {
					u = this.getUserInfo(userId);
					LogUtil.info("[OLD_USER_VISIT] [userId=" + userId + "] [unionId="+ unionId +"]");
				}
			} catch (Throwable t) {
				LogUtil.WEB_LOG.warn("code2session check&createuser error: " + " code="+ code +", return json=" + json);
				return null;
			}
		}
		
		WebUser wu = WebUser.create(u, unionId, sessionKey);
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
	
}
