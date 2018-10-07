package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.UserScore;
import com.lebaor.utils.TextUtil;

public class UserScoreDB {
	public static final String TABLENAME = "userscore";
	DBUtils dbUtils;
	
	public UserScore[] getAllUserScores() {
		int start = 0;
		int length = 1000;
		LinkedList<UserScore> users = new LinkedList<UserScore>();
		
		while (true) {
			UserScore[] partUserScores = getUserScores(start, length);
			for (UserScore u : partUserScores) {
				users.add(u);
			}
			start += length;
			if (partUserScores.length < length) {
				break;
			}
		}
		
		return users.toArray(new UserScore[0]);
	}
	
	/**
	 * 按给定位置，给出UserScores
	 * @param start
	 * @param length
	 * @return
	 */
	public UserScore[] getUserScores(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		UserScore[] UserScoreArr = (UserScore[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<UserScore> list = new LinkedList<UserScore>(); 
				while (rs.next()) {
					UserScore u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new UserScore[0]);
			}
		});
		return UserScoreArr;
	}
	
	private UserScore readOneRow(ResultSet rs) throws Exception {

		java.sql.Timestamp d;
		UserScore o = new UserScore();
		o.setId(rs.getLong(1));
		o.setUserId(rs.getLong(2));
		o.setScore(rs.getInt(3));
		d = rs.getTimestamp(4);
		o.setLastUpdateTime(d != null ? d.getTime() : 0);
		return o;
		
	}
	
	
	public UserScore getUserScoreById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (UserScore)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					UserScore u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public UserScore getUserScoreByUserId(long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE user_id=? ";
		return (UserScore)dbUtils.executeQuery(sql, new Object[]{userId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					UserScore u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	
	/**
	 * 添加一个UserScore进数据库
	 * @param UserScore
	 * @return 添加成功，则返回true
	 */
	public boolean addUserScore(UserScore o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getUserId(),
				o.getScore(),
				TextUtil.formatTime(o.getLastUpdateTime())
		});
	}
	
	/**
	 * 更新一个UserScore进数据库
	 * @param UserScore
	 * @return 添加成功，则返回true
	 */
	public boolean updateUserScore(UserScore o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getUserId(),
				o.getScore(),
				TextUtil.formatTime(o.getLastUpdateTime()),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "user_id", "score", "last_update_time"};
	
	
	private void createUserScoreDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `user_id` bigint(20) NOT NULL,\r\n" +
				"  `score` int(8) default 0,\r\n" +
				"  `last_update_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (user_id)";
			dbUtils.executeSql(sql, null);
			
		}
		
	}
	
	public void init() {
		this.createUserScoreDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
