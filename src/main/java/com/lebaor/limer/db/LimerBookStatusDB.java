package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.LimerBookStatus;
import com.lebaor.utils.TextUtil;

public class LimerBookStatusDB {

	public static final String TABLENAME = "bookstatus";
	DBUtils dbUtils;
	
	public LimerBookStatus[] getLimerBooks(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" ORDER BY last_update_time DESC LIMIT ?,? ";
		LimerBookStatus[] LimerBookStatusArr = (LimerBookStatus[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<LimerBookStatus> list = new LinkedList<LimerBookStatus>(); 
				while (rs.next()) {
					LimerBookStatus u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new LimerBookStatus[0]);
			}
		});
		return LimerBookStatusArr;
	}
	
	/**
	 * 按给定位置，给出LimerBookStatuss
	 * @param start
	 * @param length
	 * @return
	 */
	public LimerBookStatus[] getRecentLimerBooks(int status, int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" WHERE status = ?  ORDER BY last_update_time DESC LIMIT ?,? ";
		LimerBookStatus[] LimerBookStatusArr = (LimerBookStatus[])dbUtils.executeQuery(sql, new Object[]{status, start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<LimerBookStatus> list = new LinkedList<LimerBookStatus>(); 
				while (rs.next()) {
					LimerBookStatus u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new LimerBookStatus[0]);
			}
		});
		return LimerBookStatusArr;
	}
	
	private LimerBookStatus readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		LimerBookStatus o = new LimerBookStatus();
		o.setId(rs.getLong(1));
		o.setBookId(rs.getLong(2));
		o.setLimerBookId(rs.getLong(3));
		o.setStatus(rs.getInt(4));
		o.setHolderUserId(rs.getLong(5));
		d = rs.getTimestamp(6);
		o.setLastUpdateTime(d != null ? d.getTime() : 0);
		return o;
	}
	
	
	public LimerBookStatus getLimerBookStatusById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (LimerBookStatus)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					LimerBookStatus u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public LimerBookStatus[] getLimerBookStatus(long bookId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE book_id=? ";
		LimerBookStatus[] LimerBookStatusArr = (LimerBookStatus[])dbUtils.executeQuery(sql, new Object[]{bookId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<LimerBookStatus> list = new LinkedList<LimerBookStatus>(); 
				while (rs.next()) {
					LimerBookStatus u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new LimerBookStatus[0]);
			}
		});
		return LimerBookStatusArr;
	}
		
	
	/**
	 * 添加一个LimerBookStatus进数据库
	 * @param LimerBookStatus
	 * @return 添加成功，则返回true
	 */
	public boolean addLimerBookStatus(LimerBookStatus o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getBookId(),
				o.getLimerBookId(),
				o.getStatus(),
				o.getHolderUserId(),
				TextUtil.formatTime(o.getLastUpdateTime())
		});
	}
	
	/**
	 * 更新一个LimerBookStatus进数据库
	 * @param LimerBookStatus
	 * @return 添加成功，则返回true
	 */
	public boolean updateLimerBookStatus(LimerBookStatus o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getBookId(),
				o.getLimerBookId(),
				o.getStatus(),
				o.getHolderUserId(),
				TextUtil.formatTime(o.getLastUpdateTime()),
				o.getId()
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "book_id", "limer_book_id", "status", "holder_user_id", 
		"last_update_time"};
	
	
	private void createLimerBookStatusDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `book_id` bigint(20) NOT NULL,\r\n" +
				"  `limer_book_id` bigint(20) NOT NULL,\r\n" +
				"  `status` smallint(2) default 0,\r\n" +
				"  `holder_user_id` bigint(20) NOT NULL,\r\n" +
				"  `last_update_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_bookid" + 
					" ON "+ TABLENAME +" (book_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_limerbookid" + 
					" ON "+ TABLENAME +" (limer_book_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (holder_user_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_status" + 
					" ON "+ TABLENAME +" (status)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_updatetime" + 
					" ON "+ TABLENAME +" (last_update_time)";
			dbUtils.executeSql(sql, null);
		}
		
	}
	
	public void init() {
		this.createLimerBookStatusDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
