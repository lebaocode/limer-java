package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.User;
import com.lebaor.utils.TextUtil;

public class UserDB {

	public static final String TABLENAME = "users";
	DBUtils dbUtils;
		
	public User[] getAllUsers() {
		int start = 0;
		int length = 1000;
		LinkedList<User> users = new LinkedList<User>();
		
		while (true) {
			User[] partUsers = getUsers(start, length);
			for (User u : partUsers) {
				users.add(u);
			}
			start += length;
			if (partUsers.length < length) {
				break;
			}
		}
		
		return users.toArray(new User[0]);
	}
	
	/**
	 * 按给定位置，给出Users
	 * @param start
	 * @param length
	 * @return
	 */
	public User[] getUsers(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		User[] UserArr = (User[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<User> list = new LinkedList<User>(); 
				while (rs.next()) {
					User u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new User[0]);
			}
		});
		return UserArr;
	}
	
	private User readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		User o = new User();
		o.setId(rs.getLong(1));
		o.setUserName(rs.getString(2));
		o.setUserLogo(rs.getString(3));
		o.setMobile(rs.getString(4));
		o.setAddress(rs.getString(5));
		o.setEmail(rs.getString(6));
		o.setSex(rs.getInt(7));
		o.setBirthday(rs.getString(8));
		o.setProvince(rs.getString(9));
		o.setCity(rs.getString(10));
		o.setDistrict(rs.getString(11));
		d = rs.getTimestamp(12);
		o.setCreateTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(13);
		o.setLastUpdateTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(14);
		o.setLastLoginTime(d != null ? d.getTime() : 0);
		return o;
	}
	
	
	public User getUserById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (User)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					User u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public User getUserByName(String userName) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE user_name=? ";
		User[] UserArr = (User[])dbUtils.executeQuery(sql, new Object[]{userName}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<User> list = new LinkedList<User>(); 
				while (rs.next()) {
					User u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new User[0]);
			}
		});
		User res = null;
		long max = 0;
		for (User u : UserArr) {
			if (u.getCreateTime() > max) {
				max = u.getCreateTime();
				res = u;
			}
		}
		return res;
	}
	
	/**
	 * 添加一个User进数据库
	 * @param User
	 * @return 添加成功，则返回true
	 */
	public boolean addUser(User o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getUserName(),
				o.getUserLogo(),
				o.getMobile(),
				o.getAddress(),
				o.getEmail(),
				o.getSex(),
				o.getBirthday(),
				o.getProvince(),
				o.getCity(),
				o.getDistrict(),
				TextUtil.formatTime(o.getCreateTime()),
				TextUtil.formatTime(o.getLastUpdateTime()),
				TextUtil.formatTime(o.getLastLoginTime()),
		});
	}
	
	/**
	 * 更新一个User进数据库
	 * @param User
	 * @return 添加成功，则返回true
	 */
	public boolean updateUser(User o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getUserName(),
				o.getUserLogo(),
				o.getMobile(),
				o.getAddress(),
				o.getEmail(),
				o.getSex(),
				o.getBirthday(),
				o.getProvince(),
				o.getCity(),
				o.getDistrict(),
				TextUtil.formatTime(o.getCreateTime()),
				TextUtil.formatTime(o.getLastUpdateTime()),
				TextUtil.formatTime(o.getLastLoginTime()),
				o.getId()
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "user_name", "user_logo", "mobile", 
		"address", "email", "sex", "birthday", 
		"province", "city", "district", "create_time", 
		"last_update_time", "last_login_time"};
	
	/**
	 * 创建UserTable
	 * long id;
	long userName;
	String userLogo;
	String mobile;
	String address;
	String email;
	
	int sex;
	String birthday;
	String province;
	String city;
	String district;
	
	long createTime;
	long lastUpdateTime;
	long lastLoginTime;
	 */
	private void createUserDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `user_name` varchar(64) NOT NULL,\r\n" +
				"  `user_logo` varchar(255) NOT NULL,\r\n" +
				"  `mobile` varchar(64) default NULL,\r\n" +
				"  `address` varchar(255) default NULL,\r\n" +
				"  `email` varchar(255) default NULL,\r\n" +
				
				"  `sex` smallint(2) default 0,\r\n" +
				
				"  `birthday` varchar(16) default NULL,\r\n" +
				"  `province` varchar(64) default NULL,\r\n" +
				"  `city` varchar(64) default NULL,\r\n" +
				"  `district` varchar(64) default NULL,\r\n" +
				
				"  `status` smallint(2) default 0,\r\n" +
				
				"  `create_time` datetime default NULL,\r\n" +
				"  `last_update_time` datetime default NULL,\r\n" +
				"  `last_login_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_username" + 
					" ON "+ TABLENAME +" (user_name)";
			dbUtils.executeSql(sql, null);
			
		}
		
	}
	
	public void init() {
		this.createUserDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
