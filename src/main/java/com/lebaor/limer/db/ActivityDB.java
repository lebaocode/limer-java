package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.Activity;
import com.lebaor.utils.TextUtil;



public class ActivityDB {

	public static final String TABLENAME = "activity";
	DBUtils dbUtils;
	
	public Activity[] getAllActivitys() {
		int start = 0;
		int length = 1000;
		LinkedList<Activity> users = new LinkedList<Activity>();
		
		while (true) {
			Activity[] partActivitys = getActivitys(start, length);
			for (Activity u : partActivitys) {
				users.add(u);
			}
			start += length;
			if (partActivitys.length < length) {
				break;
			}
		}
		
		return users.toArray(new Activity[0]);
	}
	
	/**
	 * 按给定位置，给出Activitys
	 * @param start
	 * @param length
	 * @return
	 */
	public Activity[] getActivitys(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		Activity[] ActivityArr = (Activity[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Activity> list = new LinkedList<Activity>(); 
				while (rs.next()) {
					Activity u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Activity[0]);
			}
		});
		return ActivityArr;
	}
	
	private Activity readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		Activity o = new Activity();
		o.setId(rs.getLong(1));
		o.setTitle(rs.getString(2));
		o.setSubTitle(rs.getString(3));
		o.setDesc(rs.getString(4));
		o.setStatus(rs.getInt(5));
		d = rs.getTimestamp(6);
		o.setStartTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(7);
		o.setEndTime(d != null ? d.getTime() : 0);
		return o;
	}
	
	
	public Activity getActivityById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (Activity)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					Activity u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	/**
	 * 添加一个Activity进数据库
	 * @param Activity
	 * @return 添加成功，则返回true
	 */
	public boolean addActivity(Activity o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getTitle(),
				o.getSubTitle(),
				o.getDesc(),
				o.getStatus(),
				TextUtil.formatTime(o.getStartTime()),
				TextUtil.formatTime(o.getEndTime())
		});
	}
	
	/**
	 * 更新一个Activity进数据库
	 * @param Activity
	 * @return 添加成功，则返回true
	 */
	public boolean updateActivity(Activity o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getTitle(),
				o.getSubTitle(),
				o.getDesc(),
				o.getStatus(),
				TextUtil.formatTime(o.getStartTime()),
				TextUtil.formatTime(o.getEndTime()),
				o.getId()
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "title", "sub_title", "desc", 
			"status", "start_time", "end_time"};
	
	
	private void createActivityDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `title` varchar(255) default NULL,\r\n" +
				"  `sub_title` varchar(255) default NULL,\r\n" +
				"  `desc` TEXT default NULL,\r\n" +
				"  `status` smallint(2) default 0,\r\n" +
				"  `start_time` datetime default NULL,\r\n" +
				"  `end_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			
		}
		
	}
	
	public void init() {
		this.createActivityDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
	
}
