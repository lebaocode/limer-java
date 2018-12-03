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
		o.setIsbn(rs.getString(2));
		o.setDonateUserId(rs.getLong(3));
		o.setStatus(rs.getInt(4));
		o.setDegree(rs.getInt(5));
		o.setExtraInfo(rs.getString(6));
		d = rs.getTimestamp(7);
		o.setDonateTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(8);
		o.setLastUpdateTime(d != null ? d.getTime() : 0);
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
				o.getDonateUserId(),
				o.getStatus(),
				o.getDegree(),
				o.getExtraInfo(),
				TextUtil.formatTime(o.getDonateTime()),
				TextUtil.formatTime(o.getLastUpdateTime()),
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
				o.getDonateUserId(),
				o.getStatus(),
				o.getDegree(),
				o.getExtraInfo(),
				TextUtil.formatTime(o.getDonateTime()),
				TextUtil.formatTime(o.getLastUpdateTime()),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "isbn", "donate_user_id", "status", "degree", "extra_info", "donate_time", "last_update_time"};
	
	
	private void createLimerBookInfoDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `isbn` varchar(255) NOT NULL,\r\n" +
				"  `donate_user_id` bigint(20) NOT NULL,\r\n" +
				"  `status` smallint(2) default 0,\r\n" +
				"  `degree` smallint(3) default 0,\r\n" +
				"  `extra_info` TEXT default NULL,\r\n" +
				
				"  `donate_time` datetime default NULL,\r\n" +
				"  `last_update_time` datetime default NULL,\r\n" +
				
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (donate_user_id)";
			dbUtils.executeSql(sql, null);
			
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
}
