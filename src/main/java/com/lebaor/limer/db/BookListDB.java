package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.BookList;
import com.lebaor.utils.TextUtil;

public class BookListDB {

	public static final String TABLENAME = "booklists";
	DBUtils dbUtils;
	
	public BookList[] getAllBookLists() {
		int start = 0;
		int length = 1000;
		LinkedList<BookList> users = new LinkedList<BookList>();
		
		while (true) {
			BookList[] partBookLists = getBookLists(start, length);
			for (BookList u : partBookLists) {
				users.add(u);
			}
			start += length;
			if (partBookLists.length < length) {
				break;
			}
		}
		
		return users.toArray(new BookList[0]);
	}
	
	/**
	 * 按给定位置，给出BookLists
	 * @param start
	 * @param length
	 * @return
	 */
	public BookList[] getBookLists(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		BookList[] BookListArr = (BookList[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookList> list = new LinkedList<BookList>(); 
				while (rs.next()) {
					BookList u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookList[0]);
			}
		});
		return BookListArr;
	}
	
	private BookList readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		BookList o = new BookList();
		o.setId(rs.getLong(1));
		o.setType(rs.getString(2));
		o.setTitle(rs.getString(3));
		o.setSubTitle(rs.getString(4));
		o.setDesc(rs.getString(5));
		o.setBookIsbnsJson(rs.getString(6));
		d = rs.getTimestamp(7);
		o.setCreateTime(d != null ? d.getTime() : 0);
		return o;
	}
	
	
	public BookList getBookListById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (BookList)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					BookList u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	/**
	 * 添加一个BookList进数据库
	 * @param BookList
	 * @return 添加成功，则返回true
	 */
	public boolean addBookList(BookList o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getType(),
				o.getTitle(),
				o.getSubTitle(),
				o.getDesc(),
				o.getBookIsbnsJson(),
				TextUtil.formatTime(o.getCreateTime()),
		});
	}
	
	/**
	 * 更新一个BookList进数据库
	 * @param BookList
	 * @return 添加成功，则返回true
	 */
	public boolean updateBookList(BookList o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getType(),
				o.getTitle(),
				o.getSubTitle(),
				o.getDesc(),
				o.getBookIsbnsJson(),
				TextUtil.formatTime(o.getCreateTime()),
				o.getId()
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "type", "title", "sub_title", 
			"desc", "book_isbns_json", "create_time"};
	
	
	private void createBookListDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `type` varchar(255) default NULL,\r\n" +
				"  `title` varchar(255) NOT NULL,\r\n" +
				"  `sub_title` varchar(255) default NULL,\r\n" +
				"  `desc` TEXT default NULL,\r\n" +
				"  `book_isbns_json` varchar(255) NOT NULL,\r\n" +
				"  `create_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
		}
		
	}
	
	public void init() {
		this.createBookListDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
	
}
