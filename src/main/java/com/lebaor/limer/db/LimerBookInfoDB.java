package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

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
		o.setBookId(rs.getLong(2));
		o.setDonateUserId(rs.getLong(3));
		d = rs.getTimestamp(4);
		o.setDonateTime(d != null ? d.getTime() : 0);
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
	
	public LimerBookInfo getRecentDonateBook(long bookId, long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE book_id=? and user_id=?";
		LimerBookInfo[] LimerBookInfoArr = (LimerBookInfo[])dbUtils.executeQuery(sql, new Object[]{bookId, userId}, new ResultSetHandler(){
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
	
	public LimerBookInfo[] getLimerBooksByBookId(long bookId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE book_id=? ";
		LimerBookInfo[] LimerBookInfoArr = (LimerBookInfo[])dbUtils.executeQuery(sql, new Object[]{bookId}, new ResultSetHandler(){
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
				
				o.getBookId(),
				o.getDonateUserId(),
				TextUtil.formatTime(o.getDonateTime())
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
				
				o.getBookId(),
				o.getDonateUserId(),
				TextUtil.formatTime(o.getDonateTime()),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "book_id", "donate_user_id", "donate_time"};
	
	
	private void createLimerBookInfoDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `book_id` bigint(20) NOT NULL,\r\n" +
				"  `donate_user_id` bigint(20) NOT NULL,\r\n" +
				"  `donate_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (donate_user_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_bookid" + 
					" ON "+ TABLENAME +" (book_id)";
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
}
