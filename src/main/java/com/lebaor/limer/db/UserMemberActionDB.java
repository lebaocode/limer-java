package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.UserMemberAction;
import com.lebaor.utils.TextUtil;

public class UserMemberActionDB {

	public static final String TABLENAME = "useractions";
	DBUtils dbUtils;
	
	public UserMemberAction[] getAllUserMemberActions() {
		int start = 0;
		int length = 1000;
		LinkedList<UserMemberAction> users = new LinkedList<UserMemberAction>();
		
		while (true) {
			UserMemberAction[] partUserMemberActions = getUserMemberActions(start, length);
			for (UserMemberAction u : partUserMemberActions) {
				users.add(u);
			}
			start += length;
			if (partUserMemberActions.length < length) {
				break;
			}
		}
		
		return users.toArray(new UserMemberAction[0]);
	}
	
	/**
	 * 按给定位置，给出UserMemberActions
	 * @param start
	 * @param length
	 * @return
	 */
	public UserMemberAction[] getUserMemberActions(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		UserMemberAction[] UserMemberActionArr = (UserMemberAction[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<UserMemberAction> list = new LinkedList<UserMemberAction>(); 
				while (rs.next()) {
					UserMemberAction u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new UserMemberAction[0]);
			}
		});
		return UserMemberActionArr;
	}
	
	private UserMemberAction readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		UserMemberAction o = new UserMemberAction();
		o.setId(rs.getLong(1));
		o.setUserId(rs.getLong(2));
		o.setActionType(rs.getInt(3));
		o.setPriceFen(rs.getInt(4));
		d = rs.getTimestamp(5);
		o.setActionTime(d != null ? d.getTime() : 0);
		return o;

	}
	
	
	public UserMemberAction getUserMemberActionById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (UserMemberAction)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					UserMemberAction u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public UserMemberAction[] getActionsByUser(long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE user_id=? ";
		UserMemberAction[] UserMemberActionArr = (UserMemberAction[])dbUtils.executeQuery(sql, new Object[]{userId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<UserMemberAction> list = new LinkedList<UserMemberAction>(); 
				while (rs.next()) {
					UserMemberAction u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new UserMemberAction[0]);
			}
		});
		return UserMemberActionArr;
		
	}
	
	
	/**
	 * 添加一个UserMemberAction进数据库
	 * @param UserMemberAction
	 * @return 添加成功，则返回true
	 */
	public boolean addUserMemberAction(UserMemberAction o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getUserId(),
				o.getActionType(),
				o.getPriceFen(),
				TextUtil.formatTime(o.getActionTime()),
		});
	}
	
	/**
	 * 更新一个UserMemberAction进数据库
	 * @param UserMemberAction
	 * @return 添加成功，则返回true
	 */
	public boolean updateUserMemberAction(UserMemberAction o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getUserId(),
				o.getActionType(),
				o.getPriceFen(),
				TextUtil.formatTime(o.getActionTime()),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {
			"id", "user_id", "action_type", "price_fen", 
			"action_time"
	};
	
	
	private void createUserMemberActionDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `user_id` bigint(20) default 0,\r\n" +
				"  `action_type` smallint(2) default 0,\r\n" +
				"  `price_fen` smallint(2) default 0,\r\n" +
				"  `action_time` datetime default NULL,\r\n" +		
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (user_id)";
			dbUtils.executeSql(sql, null);
			
		}
		
	}
	
	public void init() {
		this.createUserMemberActionDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
