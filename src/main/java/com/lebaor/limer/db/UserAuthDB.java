package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.UserAuth;
import com.lebaor.utils.TextUtil;

public class UserAuthDB {
	public static final String TABLENAME = "userauth";
	DBUtils dbUtils;
	
	public UserAuth[] getAllUserAuths() {
		int start = 0;
		int length = 1000;
		LinkedList<UserAuth> users = new LinkedList<UserAuth>();
		
		while (true) {
			UserAuth[] partUserAuths = getUserAuths(start, length);
			for (UserAuth u : partUserAuths) {
				users.add(u);
			}
			start += length;
			if (partUserAuths.length < length) {
				break;
			}
		}
		
		return users.toArray(new UserAuth[0]);
	}
	
	/**
	 * 按给定位置，给出UserAuths
	 * @param start
	 * @param length
	 * @return
	 */
	public UserAuth[] getUserAuths(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		UserAuth[] UserAuthArr = (UserAuth[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<UserAuth> list = new LinkedList<UserAuth>(); 
				while (rs.next()) {
					UserAuth u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new UserAuth[0]);
			}
		});
		return UserAuthArr;
	}
	
	private UserAuth readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		UserAuth o = new UserAuth();
		o.setId(rs.getLong(1));
		o.setUserId(rs.getLong(2));
		o.setAppId(rs.getString(3));
		o.setOpenId(rs.getString(4));
		o.setUnionId(rs.getString(5));
		o.setExtraInfo(rs.getString(6));
		d = rs.getTimestamp(7);
		o.setCreateTime(d != null ? d.getTime() : 0);
		return o;


	}
	
	
	public UserAuth getUserAuthById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (UserAuth)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					UserAuth u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public UserAuth getUserAuthByUnionId(String appId, String unionId) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE app_id=? AND union_id=? ";
		return (UserAuth)dbUtils.executeQuery(sql, new Object[]{appId, unionId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					UserAuth u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public UserAuth getUserAuthByOpenId(String appId, String openId) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE app_id=? AND open_id=? ";
		return (UserAuth)dbUtils.executeQuery(sql, new Object[]{appId, openId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					UserAuth u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	/**
	 * 添加一个UserAuth进数据库
	 * @param UserAuth
	 * @return 添加成功，则返回true
	 */
	public boolean addUserAuth(UserAuth o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getUserId(),
				o.getAppId(),
				o.getOpenId(),
				o.getUnionId(),
				o.getExtraInfo(),
				TextUtil.formatTime(o.getCreateTime())
		});
	}
	
	/**
	 * 更新一个UserAuth进数据库
	 * @param UserAuth
	 * @return 添加成功，则返回true
	 */
	public boolean updateUserAuth(UserAuth o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getUserId(),
				o.getAppId(),
				o.getOpenId(),
				o.getUnionId(),
				o.getExtraInfo(),
				TextUtil.formatTime(o.getCreateTime()),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "user_id", "app_id", "open_id", 
		"union_id", "extra_info", "create_time"};
	
	
	private void createUserAuthDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `user_id` bigint(20) default 0,\r\n" +
				"  `app_id` VARCHAR(255) NOT NULL,\r\n" +
				"  `open_id` VARCHAR(255) NOT NULL,\r\n" +
				"  `union_id` VARCHAR(255) NOT NULL,\r\n" +
				"  `extra_info` TEXT default NULL,\r\n" +
				"  `create_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (user_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_unionid" + 
					" ON "+ TABLENAME +" (union_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_openid" + 
					" ON "+ TABLENAME +" (app_id, open_id)";
			dbUtils.executeSql(sql, null);
		}
		
	}
	
	public void init() {
		this.createUserAuthDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
