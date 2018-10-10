package com.lebaor.limer.web;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.limer.db.*;
import com.lebaor.limer.task.LogisticsTask;
import com.lebaor.limer.web.data.*;
import com.lebaor.limer.data.*;
import com.lebaor.thirdpartyutils.DoubanUtil;
import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class LebaoCache {
	LimerBookStatusDB lbsDB;
	BookDB bookDB;
	LimerBookInfoDB lbiDB;
	UserScoreDB userScoreDB;
	BorrowRecordDB brDB;
	UserDB userDB;
	UserAuthDB userAuthDB;
	LogisticsDB ltDB;
	
	private static final String KEY_RECENT_BOOKS = "recent_books"; //最近可借阅的书籍。WebBook json list, 顺序放，新的在前，缓存前1000个。需要实时确保准确
	private static final int KEY_RECENT_BOOKS_NUM = 1000;
	
	private static final String KEY_BOOK_DETAIL = "book_detail";//key,value key=bookId(douban book) 缓存里不保证是全部的数据
	private static final String KEY_BOOK_ISBN = "book_isbn";//key,value key=isbn, value=bookId(douban book) 缓存里不保证是全部的数据
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
	}
	
	public static Jedis getJedis() {
		Jedis jedis = jedisPool.getResource();
		jedis.auth(getInstance().jedisPassword);
		return jedis;
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
	
	public WebBook getWebBook(long bookId) {
		WebBookDetail d = getWebBookDetail(bookId);
		if (d == null) return null;
		
		return d.toWebBook();
		
	}
	
	//直接取数据库
	public WebBookStatus getWebBookStatus(long bookId) {
		//不从jedis里取了，因为状态一直在发生改变，而且读取不频繁，可以直接读写数据库
		//去数据库里获取
		LimerBookStatus[] lbsArr = lbsDB.getLimerBookStatus(bookId);
		LinkedList<Long> ids = new LinkedList<Long>();
		int inlibNum = 0;
		for (LimerBookStatus lbs: lbsArr) {
			if (lbs.getStatus() != LimerConstants.LIMER_BOOK_STATUS_READY) continue;
			inlibNum ++;
			ids.add(lbs.getLimerBookId());
		}
		
		WebBookStatus wbs = new WebBookStatus();
		wbs.setBookId(bookId);
		wbs.setInlibNum(inlibNum);
		wbs.setLimerBookIds(ids.toArray(new Long[0]));
		
		if (inlibNum == 0) {
			wbs.setStatus(LimerConstants.LIMER_BOOK_STATUS_BUSY);
		} else {
			wbs.setStatus(LimerConstants.LIMER_BOOK_STATUS_READY);
		}
		return wbs;
	}
	
	public WebBookDetail getWebBookDetail(long bookId) {
		//先从jedis取
		String bookJson = getJedis().hget(KEY_BOOK_DETAIL, Long.toString(bookId));
		if (bookJson != null) {
			try {
				JSONObject o = new JSONObject(bookJson);
				if (o != null && !o.has("error")) {
					return WebBookDetail.parseJSON(o);
				}
			} catch (Exception e) {
				return null;
			}
		}
		
		//去数据库里获取
		Book b = bookDB.getBookById(bookId);
		if (b == null) return null;
		
		WebBookDetail wb = new WebBookDetail();
		wb.setBookInfo(b);
		
		//存入jedis
		getJedis().hset(KEY_BOOK_DETAIL, Long.toString(bookId), wb.toJSON());
		
		return wb;
		
	}
	
	//访问频繁，只取缓存
	public List<WebBook> getRecentReadyBooks(String tag, int start, int len) {
		List<WebBook> resultList = new LinkedList<WebBook>();
		
		//从jedis里取
		synchronized (this) {
			try {
				if (!getJedis().exists(KEY_RECENT_BOOKS)) {
					//最近1000本书籍状态
					HashMap<String, Double> scoreMembers = new HashMap<String, Double>();
					LimerBookStatus[] lbsArr = lbsDB.getRecentLimerBooks(LimerConstants.LIMER_BOOK_STATUS_READY, 0, KEY_RECENT_BOOKS_NUM);
					for (LimerBookStatus lbs : lbsArr) {
						WebBook wb = this.getWebBook(lbs.getBookId());
						scoreMembers.put(wb.toJSON(), (double)lbs.getLimerBookId());
						
						for (String ct : wb.getTags()) {
							getJedis().zadd(KEY_RECENT_BOOKS+"_" + ct, scoreMembers);
						}
					}
					getJedis().zadd(KEY_RECENT_BOOKS, scoreMembers);
				}
			} catch(Throwable t) {
				LogUtil.WEB_LOG.warn("getRecentReadyBooks(0,"+KEY_RECENT_BOOKS_NUM+") error", t);
				return resultList;
			}
		}
		
		Set<String> list = getJedis().zrevrange(KEY_RECENT_BOOKS+"_"+tag, start, len);
		if (list == null || list.size() == 0) return resultList;
		
		for (String s: list) {
			WebBook wb = WebBook.parseJSON(s);
			resultList.add(wb);
		}
		return resultList;
	}
	
	public WebBookDetail getDoubanBookInfo(String isbn) {
		if (isbn == null || isbn.trim().length() == 0) return null;
		if (isbn.length() != 10 && isbn.length() != 13) return null;
		
		//先从jedis取
		String bookId = getJedis().hget(KEY_BOOK_ISBN, isbn);
		if (bookId != null) {
			return this.getWebBookDetail(Long.parseLong(bookId));
		}
		
		//没有，则从豆瓣api获取
		String json = DoubanUtil.getBookInfoByIsbn(isbn);
		if (json == null || json.trim().length() == 0) return null;
		
		//插入数据库。如果相同的isbn，目前可以插入多条记录。取出时取最新的一条
		Book b = new Book();
		b.setCreateTime(System.currentTimeMillis());
		b.setJson(json);
		bookDB.addBook(b);
		
		//取出id
		b = bookDB.getBookByIsbn(isbn);
		WebBookDetail wbd = new WebBookDetail();
		wbd.setBookInfo(b);
		
		//更新缓存
		getJedis().hset(KEY_BOOK_ISBN, isbn, Long.toString(b.getId()));
		getJedis().hset(KEY_BOOK_DETAIL, Long.toString(b.getId()), wbd.toJSON());
		
		return wbd;
	}
	
	public boolean addDonateBook(long bookId, long userId) {
		synchronized (this) {
			try {
				if (bookId <= 0 || userId <= 0) return false;
				
				LimerBookInfo[] lbiArr = lbiDB.getLimerBooksByDonateUser(userId);
				for (LimerBookInfo lbi: lbiArr) {
					if (lbi.getBookId() == bookId) {
						//已捐赠过此书
						return false;
					}
				}
				
				LimerBookInfo lbi = new LimerBookInfo();
				lbi.setBookId(bookId);
				lbi.setDonateUserId(userId);
				lbi.setDonateTime(System.currentTimeMillis());
				boolean res = lbiDB.addLimerBookInfo(lbi);
				if (!res) return false;
				
				lbi = lbiDB.getRecentDonateBook(bookId, userId);
				
				//添加status
				LimerBookStatus lbs = new LimerBookStatus();
				lbs.setBookId(bookId);
				lbs.setHolderUserId(userId);
				lbs.setLastUpdateTime(System.currentTimeMillis());
				lbs.setLimerBookId(lbi.getId());
				lbs.setStatus(LimerConstants.LIMER_BOOK_STATUS_READY);
				lbsDB.addLimerBookStatus(lbs);
				 
				WebBook wb = this.getWebBook(bookId);
				
				//加入可借阅缓存
				HashMap<String, Double> scoreMembers = new HashMap<String, Double>();
				scoreMembers.put(wb.toJSON(), (double)lbi.getId());
				getJedis().zadd(KEY_RECENT_BOOKS, scoreMembers);
				for (String tag: wb.getTags()) {
					getJedis().zadd(KEY_RECENT_BOOKS+"_"+tag, scoreMembers);
				}
				
				LogUtil.WEB_LOG.info("[DONATE_BOOK_SUCCESS] [bookId="+ bookId +"] [userId="+ userId +"]");
				
				return res;
			} catch(Throwable t) {
				LogUtil.WEB_LOG.warn("Exception when addDonateBook(bookId="+ bookId +", userId=" + userId+")", t);
				return false;
			}
		}
	}
	
	public LimerBookStatus borrowBook(long bookId, long userId) {
		synchronized (this) {
			try {
				if (bookId <= 0 || userId <= 0) return null;
				
				//判断该书是否可借
				LimerBookStatus one =null;
				int inlibNum = 0;
				LimerBookStatus[] lbsArr = lbsDB.getLimerBookStatus(bookId);
				for (LimerBookStatus lbs : lbsArr) {
					if (lbs.getStatus() == LimerConstants.LIMER_BOOK_STATUS_READY) {
						//可以借
						if (one == null) one = lbs;
						inlibNum++;
					}
				}
				
				//无书可借
				if (one == null) return null;
				
				WebBook wb = this.getWebBook(bookId);
				if (wb == null) return null;
				
				long lastHolderUserId = one.getHolderUserId();
				
				one.setHolderUserId(userId);
				one.setLastUpdateTime(System.currentTimeMillis());
				one.setStatus(LimerConstants.LIMER_BOOK_STATUS_BUSY);
				lbsDB.updateLimerBookStatus(one);
				
				//产生一条借书记录
				BorrowRecord br = new BorrowRecord();
				br.setBorrowTime(System.currentTimeMillis());
				br.setLimerBookId(one.getLimerBookId());
				br.setStatus(LimerConstants.BORROW_STATUS_ING);
				br.setUserId(userId);
				brDB.addBorrowRecord(br);
				br = brDB.getBorrowRecordByUserBook(br.getLimerBookId(), userId);
				
				if (inlibNum == 1) {
					//说明此书已不能再借出了 TODO 但这时可能又有捐赠进来，会有问题
					getJedis().zremrangeByScore(KEY_RECENT_BOOKS, one.getLimerBookId(), one.getLimerBookId());
					for (String tag: wb.getTags()) {
						getJedis().zremrangeByScore(KEY_RECENT_BOOKS+"_"+tag, one.getLimerBookId(), one.getLimerBookId());
					}
				}
				
				//减积分
				UserScore us = userScoreDB.getUserScoreByUserId(userId);
				if (us == null) {
					us = new UserScore();
					us.setLastUpdateTime(System.currentTimeMillis());
					us.setScore(0 - LimerConstants.SCORE_BORROW_ONE_BOOK);
					us.setUserId(userId);
					userScoreDB.addUserScore(us);
				} else {
					us.setScore(us.getScore() - LimerConstants.SCORE_BORROW_ONE_BOOK);
				}
				
				//创建物流记录
				Logistics lt = new Logistics();
				lt.setBorrowRecordId(br.getId());
				lt.setCreateTime(System.currentTimeMillis());
				
				User lastHolder = this.getUserInfo(lastHolderUserId);
				User borrowUser = this.getUserInfo(userId);
				if (lastHolder == null || borrowUser == null) return null;
				
				lt.setFromMobile(lastHolder.getMobile());
				lt.setFromUserAddress(lastHolder.getAddress());
				lt.setFromUserId(lastHolderUserId);
				lt.setFromUserName(lastHolder.getUserName());
				lt.setGoodsName(wb.getTitle());
				lt.setLastUpdateTime(System.currentTimeMillis());
				lt.setLimerBookId(one.getLimerBookId());
				lt.setStatus(LimerConstants.LOGISTICS_STATUS_NOTCREATE);
				lt.setToMobile(borrowUser.getMobile());
				lt.setToUserAddress(borrowUser.getAddress());
				lt.setToUserId(userId);
				lt.setToUserName(borrowUser.getUserName());
				ltDB.addLogistics(lt);
				
				lt = ltDB.getLogisticsByBorrowRecordId(br.getId());
				
				//通知物流下订单
				LogisticsTask.addTask(lt);
				
				return one;
			} catch(Throwable t) {
				LogUtil.WEB_LOG.warn("Exception when borrowBook(bookId="+ bookId +", userId=" + userId+")", t);
				return null;
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

	public void setLbsDB(LimerBookStatusDB lbsDB) {
		this.lbsDB = lbsDB;
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
	
}
