package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.Order;
import com.lebaor.utils.TextUtil;

public class OrderDB {

	public static final String TABLENAME = "orders";
	DBUtils dbUtils;
	
	public Order[] getOrders(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" ORDER BY last_update_time DESC LIMIT ?,? ";
		Order[] OrderArr = (Order[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Order> list = new LinkedList<Order>(); 
				while (rs.next()) {
					Order u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Order[0]);
			}
		});
		return OrderArr;
	}
	
	private Order readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		Order o = new Order();
		o.setId(rs.getLong(1));
		o.setMchTradeNo(rs.getString(2));
		o.setWxTradeNo(rs.getString(3));
		o.setPayMethod(rs.getString(4));
		o.setUserId(rs.getLong(5));
		o.setOpenid(rs.getString(6));
		o.setUnionid(rs.getString(7));
		o.setIp(rs.getString(8));
		o.setLimerBookIdsJson(rs.getString(9));
		o.setTitle(rs.getString(10));
		o.setBookNum(rs.getInt(11));
		o.setTotalFee(rs.getInt(12));
		o.setRealFee(rs.getInt(13));
		o.setCoupounId(rs.getString(14));
		o.setCoupounFee(rs.getInt(15));
		o.setFeeType(rs.getString(16));
		o.setStatus(rs.getInt(17));
		d = rs.getTimestamp(18);
		o.setOrderStartTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(19);
		o.setOrderFinishTime(d != null ? d.getTime() : 0);
		return o;
	}
	
	
	public Order getOrderById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (Order)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					Order u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public Order[] getOrderByUserId(long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE user_id=? ";
		Order[] OrderArr = (Order[])dbUtils.executeQuery(sql, new Object[]{userId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Order> list = new LinkedList<Order>(); 
				while (rs.next()) {
					Order u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Order[0]);
			}
		});
		return OrderArr;
	}
		
	
	/**
	 * 添加一个Order进数据库
	 * @param Order
	 * @return 添加成功，则返回true
	 */
	public boolean addOrder(Order o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getMchTradeNo(),
				o.getWxTradeNo(),
				o.getPayMethod(),
				o.getUserId(),
				o.getOpenid(),
				o.getUnionid(),
				o.getIp(),
				o.getLimerBookIdsJson(),
				o.getTitle(),
				o.getBookNum(),
				o.getTotalFee(),
				o.getRealFee(),
				o.getCoupounId(),
				o.getCoupounFee(),
				o.getFeeType(),
				o.getStatus(),
				TextUtil.formatTime(o.getOrderStartTime()),
				TextUtil.formatTime(o.getOrderFinishTime())
		});
	}
	
	/**
	 * 更新一个Order进数据库
	 * @param Order
	 * @return 添加成功，则返回true
	 */
	public boolean updateOrder(Order o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getMchTradeNo(),
				o.getWxTradeNo(),
				o.getPayMethod(),
				o.getUserId(),
				o.getOpenid(),
				o.getUnionid(),
				o.getIp(),
				o.getLimerBookIdsJson(),
				o.getTitle(),
				o.getBookNum(),
				o.getTotalFee(),
				o.getRealFee(),
				o.getCoupounId(),
				o.getCoupounFee(),
				o.getFeeType(),
				o.getStatus(),
				TextUtil.formatTime(o.getOrderStartTime()),
				TextUtil.formatTime(o.getOrderFinishTime()),
				o.getId()
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "mch_trade_no", "wx_trade_no", "pay_method", 
			"user_id", "openid", "unionid", "ip", 
			"limer_book_idsjson", "title", "book_num", "total_fee", 
			"real_fee", "coupoun_id", "coupoun_fee", "fee_type", 
			"status", "order_start_time", "order_finish_time"};
	
	
	private void createOrderDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `mch_trade_no` varchar(255) NOT NULL,\r\n" +
				"  `wx_trade_no` varchar(255) NOT NULL,\r\n" +
				"  `pay_method` varchar(64) NOT NULL,\r\n" +
				
				"  `user_id` bigint(20) NOT NULL,\r\n" +
				"  `openid` varchar(255) default NULL,\r\n" +
				"  `unionid` varchar(255) NOT NULL,\r\n" +
				
				"  `ip` varchar(32) default NULL,\r\n" +
				"  `limer_book_idsjson` TEXT NOT NULL,\r\n" +
				"  `title` varchar(255) default NULL,\r\n" +
				"  `book_num` smallint(2) default 0,\r\n" +
				"  `total_fee` int(6) default 0,\r\n" +
				"  `real_fee` int(6) default 0,\r\n" +
				"  `coupoun_id` varchar(255) default NULL,\r\n" +
				"  `coupoun_fee` int(6) default 0,\r\n" +
				"  `fee_type` varchar(255) default NULL,\r\n" +
				"  `status` smallint(2) default 0,\r\n" +
				"  `order_start_time` datetime default NULL,\r\n" +
				"  `order_finish_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_wxtradeno" + 
					" ON "+ TABLENAME +" (wx_trade_no)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (user_id)";
			dbUtils.executeSql(sql, null);
		}
		
	}
	
	public void init() {
		this.createOrderDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
