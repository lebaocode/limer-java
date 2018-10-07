package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.Logistics;
import com.lebaor.utils.TextUtil;

public class LogisticsDB {
	public static final String TABLENAME = "logistics";
	DBUtils dbUtils;
	
	public Logistics[] getAllLogisticss() {
		int start = 0;
		int length = 1000;
		LinkedList<Logistics> users = new LinkedList<Logistics>();
		
		while (true) {
			Logistics[] partLogisticss = getLogisticss(start, length);
			for (Logistics u : partLogisticss) {
				users.add(u);
			}
			start += length;
			if (partLogisticss.length < length) {
				break;
			}
		}
		
		return users.toArray(new Logistics[0]);
	}
	
	/**
	 * 按给定位置，给出Logisticss
	 * @param start
	 * @param length
	 * @return
	 */
	public Logistics[] getLogisticss(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		Logistics[] LogisticsArr = (Logistics[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Logistics> list = new LinkedList<Logistics>(); 
				while (rs.next()) {
					Logistics u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Logistics[0]);
			}
		});
		return LogisticsArr;
	}
	
	private Logistics readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		Logistics o = new Logistics();
		o.setId(rs.getLong(1));
		o.setLimerBookId(rs.getLong(2));
		o.setBorrowRecordId(rs.getLong(3));
		o.setGoodsName(rs.getString(4));
		o.setFromUserId(rs.getLong(5));
		o.setFromUserName(rs.getString(6));
		o.setFromUserAddress(rs.getString(7));
		o.setFromMobile(rs.getString(8));
		o.setToUserId(rs.getLong(9));
		o.setToUserName(rs.getString(10));
		o.setToUserAddress(rs.getString(11));
		o.setToMobile(rs.getString(12));
		o.setStatus(rs.getInt(13));
		o.setStatusDesc(rs.getString(14));
		o.setPrice(rs.getInt(15));
		o.setLogisCompany(rs.getInt(16));
		o.setLogisOrderId(rs.getString(17));
		d = rs.getTimestamp(18);
		o.setCreateTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(19);
		o.setLastUpdateTime(d != null ? d.getTime() : 0);
		o.setOrderDetail(rs.getString(20));
		return o;
	}
	
	
	public Logistics getLogisticsById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (Logistics)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					Logistics u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public Logistics getLogisticsByBorrowRecordId(long borrowRecordId) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE borrow_record_id=? ";
		return (Logistics)dbUtils.executeQuery(sql, new Object[]{borrowRecordId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					Logistics u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public Logistics[] getLogisticsByFromUserId(long fromUserId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE from_user_id=? ";
		Logistics[] LogisticsArr = (Logistics[])dbUtils.executeQuery(sql, new Object[]{fromUserId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Logistics> list = new LinkedList<Logistics>(); 
				while (rs.next()) {
					Logistics u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Logistics[0]);
			}
		});
		return LogisticsArr;
		
	}
	
	public Logistics[] getLogisticsByToUserId(long toUserId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE to_user_id=? ";
		Logistics[] LogisticsArr = (Logistics[])dbUtils.executeQuery(sql, new Object[]{toUserId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Logistics> list = new LinkedList<Logistics>(); 
				while (rs.next()) {
					Logistics u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Logistics[0]);
			}
		});
		return LogisticsArr;
		
	}
	
	
	/**
	 * 添加一个Logistics进数据库
	 * @param Logistics
	 * @return 添加成功，则返回true
	 */
	public boolean addLogistics(Logistics o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getLimerBookId(),
				o.getBorrowRecordId(),
				o.getGoodsName(),
				o.getFromUserId(),
				o.getFromUserName(),
				o.getFromUserAddress(),
				o.getFromMobile(),
				o.getToUserId(),
				o.getToUserName(),
				o.getToUserAddress(),
				o.getToMobile(),
				o.getStatus(),
				o.getStatusDesc(),
				o.getPrice(),
				o.getLogisCompany(),
				o.getLogisOrderId(),
				TextUtil.formatTime(o.getCreateTime()),
				TextUtil.formatTime(o.getLastUpdateTime()),
				o.getOrderDetail()
		});
	}
	
	/**
	 * 更新一个Logistics进数据库
	 * @param Logistics
	 * @return 添加成功，则返回true
	 */
	public boolean updateLogistics(Logistics o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getLimerBookId(),
				o.getBorrowRecordId(),
				o.getGoodsName(),
				o.getFromUserId(),
				o.getFromUserName(),
				o.getFromUserAddress(),
				o.getFromMobile(),
				o.getToUserId(),
				o.getToUserName(),
				o.getToUserAddress(),
				o.getToMobile(),
				o.getStatus(),
				o.getStatusDesc(),
				o.getPrice(),
				o.getLogisCompany(),
				o.getLogisOrderId(),
				TextUtil.formatTime(o.getCreateTime()),
				TextUtil.formatTime(o.getLastUpdateTime()),
				o.getOrderDetail(),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "limer_book_id", "borrow_record_id", "goods_name", 
		"from_user_id", "from_user_name", "from_user_address", "from_mobile", 
		"to_user_id", "to_user_name", "to_user_address", "to_mobile", 
		"status", "status_desc", "price", "logis_company", 
		"logis_order_id", "create_time", "last_update_time", "order_detail"};
	
	
	private void createLogisticsDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `limer_book_id` bigint(20) NOT NULL,\r\n" +
				"  `borrow_record_id` bigint(20) NOT NULL,\r\n" +
				"  `goods_name` varchar(255) NOT NULL,\r\n" +
				
				"  `from_user_id` bigint(20) NOT NULL,\r\n" +
				"  `from_user_name` varchar(255) default NULL,\r\n" +
				"  `from_user_address` varchar(255) NOT NULL,\r\n" +
				"  `from_mobile` varchar(255) NOT NULL,\r\n" +
				
				"  `to_user_id` bigint(20) NOT NULL,\r\n" +
				"  `to_user_name` varchar(255) default NULL,\r\n" +
				"  `to_user_address` varchar(255) NOT NULL,\r\n" +
				"  `to_mobile` varchar(255) NOT NULL,\r\n" +
				
				"  `status` smallint(2) default 0,\r\n" +
				"  `status_desc` varchar(255) default NULL,\r\n" +
				
				"  `price` smallint(2) default 0,\r\n" +
				"  `logis_company` smallint(2) default 0,\r\n" +
				"  `logis_order_id` varchar(255) default NULL,\r\n" +
				
				"  `create_time` datetime default NULL,\r\n" +
				"  `last_update_time` datetime default NULL,\r\n" +
				"  `order_detail` TEXT default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_fromuserid" + 
					" ON "+ TABLENAME +" (from_user_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_touserid" + 
					" ON "+ TABLENAME +" (to_user_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_borrowrecordid" + 
					" ON "+ TABLENAME +" (borrow_record_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_bookid" + 
					" ON "+ TABLENAME +" (limer_book_id)";
			dbUtils.executeSql(sql, null);
			
		}
		
	}
	
	public void init() {
		this.createLogisticsDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
