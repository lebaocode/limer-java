package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.Book;
import com.lebaor.utils.TextUtil;

public class BookDB {

	public static final String TABLENAME = "books";
	DBUtils dbUtils;
	
	public Book[] getAllBooks() {
		int start = 0;
		int length = 1000;
		LinkedList<Book> users = new LinkedList<Book>();
		
		while (true) {
			Book[] partBooks = getBooks(start, length);
			for (Book u : partBooks) {
				users.add(u);
			}
			start += length;
			if (partBooks.length < length) {
				break;
			}
		}
		
		return users.toArray(new Book[0]);
	}
	
	/**
	 * 按给定位置，给出Books
	 * @param start
	 * @param length
	 * @return
	 */
	public Book[] getBooks(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		Book[] BookArr = (Book[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Book> list = new LinkedList<Book>(); 
				while (rs.next()) {
					Book u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Book[0]);
			}
		});
		return BookArr;
	}
	
	private Book readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		Book o = new Book();
		o.setId(rs.getLong(1));
		o.setIsbn10(rs.getString(2));
		o.setIsbn13(rs.getString(3));
		o.setJson(rs.getString(4));
		d = rs.getTimestamp(5);
		o.setCreateTime(d != null ? d.getTime() : 0);
		o.setBookFrom(rs.getString(6));
		return o;
	}
	
	
	public Book getBookById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (Book)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					Book u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	//isbn不唯一的时候，取最新一条
	public Book getBookByIsbn(String isbn) {
		String columnName = isbn.length() == 10 ? "isbn10" : "isbn13";
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE "+ columnName +"=? ";
		Book[] BookArr = (Book[])dbUtils.executeQuery(sql, new Object[]{isbn}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<Book> list = new LinkedList<Book>(); 
				while (rs.next()) {
					Book u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new Book[0]);
			}
		});
		
		long max = 0;
		Book maxB = null;
		for (Book b : BookArr) {
			if (b.getCreateTime() > max) {
				maxB = b;
				max = b.getCreateTime();
			}
		}
		return maxB;
	}
	
	/**
	 * 添加一个Book进数据库
	 * @param Book
	 * @return 添加成功，则返回true
	 */
	public boolean addBook(Book o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getIsbn10(),
				o.getIsbn13(),
				o.getJson(),
				TextUtil.formatTime(o.getCreateTime()),
				o.getBookFrom()
		});
	}
	
	/**
	 * 更新一个Book进数据库
	 * @param Book
	 * @return 添加成功，则返回true
	 */
	public boolean updateBook(Book o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getIsbn10(),
				o.getIsbn13(),
				o.getJson(),
				TextUtil.formatTime(o.getCreateTime()),
				o.getBookFrom(),
				o.getId()
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "isbn10", "isbn13", "json", 
		"create_time", "book_from"};
	
	
	private void createBookDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `isbn10` varchar(10) NOT NULL,\r\n" +
				"  `isbn13` varchar(13) NOT NULL,\r\n" +
				"  `json` TEXT default NULL,\r\n" +
				"  `create_time` datetime default NULL,\r\n" +
				"  `book_from` varchar(255) default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_isbn10" + 
					" ON "+ TABLENAME +" (isbn10)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_isbn13" + 
					" ON "+ TABLENAME +" (isbn13)";
			dbUtils.executeSql(sql, null);
		}
		
	}
	
	public void init() {
		this.createBookDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
