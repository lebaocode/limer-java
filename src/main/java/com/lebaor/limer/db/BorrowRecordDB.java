package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.BorrowRecord;
import com.lebaor.utils.TextUtil;

public class BorrowRecordDB {
	public static final String TABLENAME = "borrowrecords";
	DBUtils dbUtils;
	
	public BorrowRecord[] getAllBorrowRecords() {
		int start = 0;
		int length = 1000;
		LinkedList<BorrowRecord> users = new LinkedList<BorrowRecord>();
		
		while (true) {
			BorrowRecord[] partBorrowRecords = getBorrowRecords(start, length);
			for (BorrowRecord u : partBorrowRecords) {
				users.add(u);
			}
			start += length;
			if (partBorrowRecords.length < length) {
				break;
			}
		}
		
		return users.toArray(new BorrowRecord[0]);
	}
	
	/**
	 * 按给定位置，给出BorrowRecords
	 * @param start
	 * @param length
	 * @return
	 */
	public BorrowRecord[] getBorrowRecords(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		BorrowRecord[] BorrowRecordArr = (BorrowRecord[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BorrowRecord> list = new LinkedList<BorrowRecord>(); 
				while (rs.next()) {
					BorrowRecord u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BorrowRecord[0]);
			}
		});
		return BorrowRecordArr;
	}
	
	private BorrowRecord readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		BorrowRecord o = new BorrowRecord();
		o.setId(rs.getLong(1));
		o.setUserId(rs.getLong(2));
		o.setIsbn(rs.getString(3));
		o.setLimerBookId(rs.getLong(4));
		o.setStatus(rs.getInt(5));
		d = rs.getTimestamp(6);
		o.setBorrowTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(7);
		o.setReturnTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(8);
		o.setPunishTime(d != null ? d.getTime() : 0);
		return o;


	}
	
	
	public BorrowRecord getBorrowRecordById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (BorrowRecord)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					BorrowRecord u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public BorrowRecord getBorrowRecordByUserBook(long limerBookId, long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE user_id=?  and limer_book_id=?";
		BorrowRecord[] BorrowRecordArr = (BorrowRecord[])dbUtils.executeQuery(sql, new Object[]{userId, limerBookId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BorrowRecord> list = new LinkedList<BorrowRecord>(); 
				while (rs.next()) {
					BorrowRecord u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BorrowRecord[0]);
			}
		});
		BorrowRecord br = null;
		long max = 0;
		for (BorrowRecord b : BorrowRecordArr) {
			if (b.getId() > max) {
				max = b.getId();
				br = b;
			}
		}
		
		return br;
		
	}
	
	public BorrowRecord[] getBorrowRecordByUserId(long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE user_id=? ";
		BorrowRecord[] BorrowRecordArr = (BorrowRecord[])dbUtils.executeQuery(sql, new Object[]{userId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BorrowRecord> list = new LinkedList<BorrowRecord>(); 
				while (rs.next()) {
					BorrowRecord u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BorrowRecord[0]);
			}
		});
		return BorrowRecordArr;
		
	}
	
	public BorrowRecord[] getBorrowRecordByIsbn(String isbn) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE isbn=? ";
		BorrowRecord[] BorrowRecordArr = (BorrowRecord[])dbUtils.executeQuery(sql, new Object[]{isbn}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BorrowRecord> list = new LinkedList<BorrowRecord>(); 
				while (rs.next()) {
					BorrowRecord u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BorrowRecord[0]);
			}
		});
		return BorrowRecordArr;
		
	}
	
	public BorrowRecord[] getBorrowRecordByLimerBookId(long limerBookId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE limer_book_id=? ";
		BorrowRecord[] BorrowRecordArr = (BorrowRecord[])dbUtils.executeQuery(sql, new Object[]{limerBookId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BorrowRecord> list = new LinkedList<BorrowRecord>(); 
				while (rs.next()) {
					BorrowRecord u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BorrowRecord[0]);
			}
		});
		return BorrowRecordArr;
		
	}
	
	
	/**
	 * 添加一个BorrowRecord进数据库
	 * @param BorrowRecord
	 * @return 添加成功，则返回true
	 */
	public boolean addBorrowRecord(BorrowRecord o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getUserId(),
				o.getIsbn(),
				o.getLimerBookId(),
				o.getStatus(),
				TextUtil.formatTime(o.getBorrowTime()),
				TextUtil.formatTime(o.getReturnTime()),
				TextUtil.formatTime(o.getPunishTime())
		});
	}
	
	/**
	 * 更新一个BorrowRecord进数据库
	 * @param BorrowRecord
	 * @return 添加成功，则返回true
	 */
	public boolean updateBorrowRecord(BorrowRecord o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getUserId(),
				o.getIsbn(),
				o.getLimerBookId(),
				o.getStatus(),
				TextUtil.formatTime(o.getBorrowTime()),
				TextUtil.formatTime(o.getReturnTime()),
				TextUtil.formatTime(o.getPunishTime()),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "user_id", "isbn", "limer_book_id", "status", 
		"borrow_time", "return_time", "punish_time"};
	
	
	private void createBorrowRecordDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `user_id` bigint(20) NOT NULL,\r\n" +
				"  `isbn` varchar(255) NOT NULL,\r\n" +
				"  `limer_book_id` bigint(20) NOT NULL,\r\n" +
				"  `status` smallint(2) default 0,\r\n" +
				"  `borrow_time` datetime default NULL,\r\n" +
				"  `return_time` datetime default NULL,\r\n" +
				"  `punish_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (user_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_isbn" + 
					" ON "+ TABLENAME +" (isbn)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_limerbookid" + 
					" ON "+ TABLENAME +" (limer_book_id)";
			dbUtils.executeSql(sql, null);
			
		}
		
	}
	
	public void init() {
		this.createBorrowRecordDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
