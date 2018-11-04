package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.BookListItem;
import com.lebaor.utils.TextUtil;

public class BookListItemDB {
	public static final String TABLENAME = "booklistitems";
	DBUtils dbUtils;
	
	public BookListItem[] getAllBookListItems() {
		int start = 0;
		int length = 1000;
		LinkedList<BookListItem> users = new LinkedList<BookListItem>();
		
		while (true) {
			BookListItem[] partBookListItems = getBookListItems(start, length);
			for (BookListItem u : partBookListItems) {
				users.add(u);
			}
			start += length;
			if (partBookListItems.length < length) {
				break;
			}
		}
		
		return users.toArray(new BookListItem[0]);
	}
	
	/**
	 * 按给定位置，给出BookListItems
	 * @param start
	 * @param length
	 * @return
	 */
	public BookListItem[] getBookListItems(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		BookListItem[] BookListItemArr = (BookListItem[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookListItem> list = new LinkedList<BookListItem>(); 
				while (rs.next()) {
					BookListItem u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookListItem[0]);
			}
		});
		return BookListItemArr;
	}
	
	public BookListItem[] getBookListItems(long bookListId, int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" WHERE book_list_id=?  LIMIT ?,?";
		BookListItem[] BookListItemArr = (BookListItem[])dbUtils.executeQuery(sql, new Object[]{bookListId, start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookListItem> list = new LinkedList<BookListItem>(); 
				while (rs.next()) {
					BookListItem u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookListItem[0]);
			}
		});
		return BookListItemArr;
	}
	
	private BookListItem readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		BookListItem o = new BookListItem();
		o.setId(rs.getLong(1));
		o.setBookListId(rs.getLong(2));
		o.setUserId(rs.getLong(3));
		o.setIsbn(rs.getString(4));
		d = rs.getTimestamp(5);
		o.setCreateTime(d != null ? d.getTime() : 0);
		return o;
	}
	
	
	public BookListItem getBookListItemById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (BookListItem)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					BookListItem u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public BookListItem getBookListItem(long booklistId, String isbn, long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE user_id=? AND book_list_id=? AND isbn=?";
		return (BookListItem)dbUtils.executeQuery(sql, new Object[]{userId, booklistId, isbn}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					BookListItem u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	/**
	 * 添加一个BookListItem进数据库
	 * @param BookListItem
	 * @return 添加成功，则返回true
	 */
	public boolean addBookListItem(BookListItem o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getBookListId(),
				o.getUserId(),
				o.getIsbn(),
				TextUtil.formatTime(o.getCreateTime())
		});
	}
	
	/**
	 * 更新一个BookListItem进数据库
	 * @param BookListItem
	 * @return 添加成功，则返回true
	 */
	public boolean updateBookListItem(BookListItem o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getBookListId(),
				o.getUserId(),
				o.getIsbn(),
				TextUtil.formatTime(o.getCreateTime()),
				o.getId()
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "book_list_id", "user_id", "isbn", "create_time"};
	
	
	private void createBookListItemDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `book_list_id` bigint(20) default 0,\r\n" +
				"  `user_id` bigint(20) default 0,\r\n" +
				"  `isbn` varchar(64) default NULL,\r\n" +
				"  `create_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_isbn" + 
					" ON "+ TABLENAME +" (isbn)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_booklistid" + 
					" ON "+ TABLENAME +" (book_list_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (user_id)";
			dbUtils.executeSql(sql, null);
		}
		
	}
	
	public void init() {
		this.createBookListItemDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
	
}
