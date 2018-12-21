package com.lebaor.limer.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.LinkedList;

import org.json.JSONObject;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.LimerBookInfo;
import com.lebaor.utils.TextUtil;

public class LimerBookInfoDB {
	public static final String TABLENAME = "limerbooks";
	DBUtils dbUtils;
	
	public LimerBookInfo[] getAllLimerBookInfos() {
		int start = 0;
		int length = 1000;
		LinkedList<LimerBookInfo> users = new LinkedList<LimerBookInfo>();
		
		while (true) {
			LimerBookInfo[] partLimerBookInfos = getLimerBookInfos(start, length);
			for (LimerBookInfo u : partLimerBookInfos) {
				users.add(u);
			}
			start += length;
			if (partLimerBookInfos.length < length) {
				break;
			}
		}
		
		return users.toArray(new LimerBookInfo[0]);
	}
	
	/**
	 * 按给定位置，给出LimerBookInfos
	 * @param start
	 * @param length
	 * @return
	 */
	public LimerBookInfo[] getLimerBookInfos(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		LimerBookInfo[] LimerBookInfoArr = (LimerBookInfo[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<LimerBookInfo> list = new LinkedList<LimerBookInfo>(); 
				while (rs.next()) {
					LimerBookInfo u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new LimerBookInfo[0]);
			}
		});
		return LimerBookInfoArr;
	}
	
	private LimerBookInfo readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		LimerBookInfo o = new LimerBookInfo();
		o.setId(rs.getLong(1));
		o.setIsbn(rs.getString(2));
		o.setStoreNum(rs.getInt(3));
		o.setExtraInfo(rs.getString(4));
		d = rs.getTimestamp(5);
		o.setLastUpdateTime(d != null ? d.getTime() : 0);
		o.setBookTitle(rs.getString(6));
		o.setBookPrice(rs.getInt(7));
		o.setBookPageNum(rs.getInt(8));
		o.setIsbnSingle(rs.getInt(9) == 1);
		o.setBookNo(rs.getString(10));
		o.setBookSeriesTitle(rs.getString(11));
		return o;
	}
	
	
	public LimerBookInfo getLimerBookInfoById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (LimerBookInfo)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					LimerBookInfo u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public LimerBookInfo[] getLimerBooksByDonateUser(long donateUserId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE donate_user_id=? ";
		LimerBookInfo[] LimerBookInfoArr = (LimerBookInfo[])dbUtils.executeQuery(sql, new Object[]{donateUserId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<LimerBookInfo> list = new LinkedList<LimerBookInfo>(); 
				while (rs.next()) {
					LimerBookInfo u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new LimerBookInfo[0]);
			}
		});
		return LimerBookInfoArr;
		
	}
	
	public LimerBookInfo getRecentDonateBook(String isbn, long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE isbn=? and donate_user_id=?";
		LimerBookInfo[] LimerBookInfoArr = (LimerBookInfo[])dbUtils.executeQuery(sql, new Object[]{isbn, userId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<LimerBookInfo> list = new LinkedList<LimerBookInfo>(); 
				while (rs.next()) {
					LimerBookInfo u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new LimerBookInfo[0]);
			}
		});
		
		long max = 0;
		LimerBookInfo b = null;
		for (LimerBookInfo lbi: LimerBookInfoArr) {
			if (lbi.getId() > max) {
				max = lbi.getId();
				b = lbi;
			}
		}
		
		return b;
		
	}
	
	public LimerBookInfo[] getLimerBooksByIsbn(String isbn) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE isbn=? ";
		LimerBookInfo[] LimerBookInfoArr = (LimerBookInfo[])dbUtils.executeQuery(sql, new Object[]{isbn}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<LimerBookInfo> list = new LinkedList<LimerBookInfo>(); 
				while (rs.next()) {
					LimerBookInfo u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new LimerBookInfo[0]);
			}
		});
		return LimerBookInfoArr;
		
	}
	
	
	/**
	 * 添加一个LimerBookInfo进数据库
	 * @param LimerBookInfo
	 * @return 添加成功，则返回true
	 */
	public boolean addLimerBookInfo(LimerBookInfo o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getIsbn(),
				o.getStoreNum(),
				o.getExtraInfo(),
				TextUtil.formatTime(o.getLastUpdateTime()),
				o.getBookTitle(),
				o.getBookPrice(),
				o.getBookPageNum(),
				o.isIsbnSingle(),
				o.getBookNo(),
				o.getBookSeriesTitle(),
		});
	}
	
	/**
	 * 更新一个LimerBookInfo进数据库
	 * @param LimerBookInfo
	 * @return 添加成功，则返回true
	 */
	public boolean updateLimerBookInfo(LimerBookInfo o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getIsbn(),
				o.getStoreNum(),
				o.getExtraInfo(),
				TextUtil.formatTime(o.getLastUpdateTime()),
				o.getBookTitle(),
				o.getBookPrice(),
				o.getBookPageNum(),
				o.isIsbnSingle(),
				o.getBookNo(),
				o.getBookSeriesTitle(),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {
			"id", "isbn", "store_num", "extra_info", 
			"last_update_time", "book_title", "book_price", "book_page_num", 
			"is_isbn_single", "book_no", "book_series_title"		
	};
	
	
	private void createLimerBookInfoDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `isbn` varchar(255) default NULL,\r\n" +
				"  `store_num` smallint(2) default 0,\r\n" +
				"  `extra_info` varchar(1023) default NULL,\r\n" +
				"  `last_update_time` datetime default NULL,\r\n" +
				"  `book_title` varchar(255) default NULL,\r\n" +
				"  `book_price` MEDIUMINT(9) default 0,\r\n" +
				"  `book_page_num` smallint(4) default 0,\r\n" +
				"  `is_isbn_single` smallint(2)  default 0,\r\n" +
				"  `book_no` varchar(255) default NULL,\r\n" +
				"  `book_series_title` varchar(255) default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_isbn" + 
					" ON "+ TABLENAME +" (isbn)";
			dbUtils.executeSql(sql, null);
			
		}
		
	}
	
	public void init() {
		this.createLimerBookInfoDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
	
	public static void main(String[] args) throws Exception {
		Connection conn = null;
		try {
//			DBUtils dbUtils = new DBUtils();
//			dbUtils.setDbUrl("jdbc:mysql://cd-cdb-bh931mgw.sql.tencentcdb.com:63600/lebao?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&characterSetResults=utf8");
//			dbUtils.setCharset("utf-8");
//			dbUtils.setDbName("lebao");
//			dbUtils.setDbPassword("d3tIrGk32");
//			dbUtils.setDbUser("root");
//			
//			dbUtils.connect();
//			conn = dbUtils.getConnection();
//			if (conn == null) {
//				System.out.println("conn is null");
//				return;
//			}
//			
//			LimerBookInfoDB db = new LimerBookInfoDB();
//			db.setDbUtils(dbUtils);
			
			LimerBookInfo lbi = new LimerBookInfo();
			
			System.out.println(lbi.getExtraInfo());
			lbi.setId(6);
//			db.updateLimerBookInfo(lbi);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) conn.close();
		}
	}
}
