package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.Child;
import com.lebaor.utils.TextUtil;

public class ChildDB {

	public static final String TABLENAME = "childs";
	DBUtils dbUtils;
		
	public Child[] getAllChilds() {
		int start = 0;
		int length = 1000;
		LinkedList<Child> childs = new LinkedList<Child>();
		
		while (true) {
			Child[] partChilds = getChilds(start, length);
			for (Child u : partChilds) {
				childs.add(u);
			}
			start += length;
			if (partChilds.length < length) {
				break;
			}
		}
		
		return childs.toArray(new Child[0]);
	}
	
	/**
	 * 按给定位置，给出Childs
	 * @param start
	 * @param length
	 * @return
	 */
	public Child[] getChilds(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		Child[] ChildArr = (Child[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Child> list = new LinkedList<Child>(); 
				while (rs.next()) {
					Child u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Child[0]);
			}
		});
		return ChildArr;
	}
	
	private Child readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		Child o = new Child();
		o.setId(rs.getLong(1));
		o.setChildName(rs.getString(2));
		o.setBirthday(rs.getString(3));
		o.setSex(rs.getInt(4));
		o.setRelation(rs.getInt(5));
		o.setParentUserId(rs.getLong(6));
		o.setExtraInfo(rs.getString(7));
		d = rs.getTimestamp(8);
		o.setCreateTime(d != null ? d.getTime() : 0);
		return o;
	}
	
	
	public Child getChildById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (Child)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					Child u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public Child[] getChildrenByParent(long parentId) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE parent_user_id=? ";
		Child[] ChildArr = (Child[])dbUtils.executeQuery(sql, new Object[]{parentId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Child> list = new LinkedList<Child>(); 
				while (rs.next()) {
					Child u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Child[0]);
			}
		});
		return ChildArr;
	}
	
	/**
	 * 添加一个Child进数据库
	 * @param Child
	 * @return 添加成功，则返回true
	 */
	public boolean addChild(Child o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getChildName(),
				o.getBirthday(),
				o.getSex(),
				o.getRelation(),
				o.getParentUserId(),
				o.getExtraInfo(),
				TextUtil.formatTime(o.getCreateTime())
		});
	}
	
	/**
	 * 更新一个Child进数据库
	 * @param Child
	 * @return 添加成功，则返回true
	 */
	public boolean updateChild(Child o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getChildName(),
				o.getBirthday(),
				o.getSex(),
				o.getRelation(),
				o.getParentUserId(),
				o.getExtraInfo(),
				TextUtil.formatTime(o.getCreateTime()),
				o.getId()
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "child_name", "birthday", "sex", 
			"relation", "parent_user_id", "extra_info", "create_time"};
	
	/**
	 * 创建ChildTable
	 * long id;
	long childName;
	String childLogo;
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
	private void createChildDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `child_name` varchar(255) default NULL,\r\n" +
				"  `birthday` varchar(255) default NULL,\r\n" +
				"  `sex` smallint(2) default 0,\r\n" +
				"  `relation` smallint(2) default 0,\r\n" +
				"  `parent_user_id` bigint(20) default 0,\r\n" +
				"  `extra_info` TEXT default NULL,\r\n" +
				"  `create_time` datetime default NULL,\r\n" +

				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_parent" + 
					" ON "+ TABLENAME +" (parent_user_id)";
			dbUtils.executeSql(sql, null);
			
		}
		
	}
	
	public void init() {
		this.createChildDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
