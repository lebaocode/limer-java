package com.lebaor.limer.db;

import java.sql.ResultSet;
import java.util.LinkedList;

import com.lebaor.dbutils.DBUtils;
import com.lebaor.dbutils.ResultSetHandler;
import com.lebaor.limer.data.BookComment;
import com.lebaor.utils.TextUtil;

public class BookCommentDB {
	public static final String TABLENAME = "bookcomments";
	DBUtils dbUtils;
	
	public BookComment[] getAllBookComments() {
		int start = 0;
		int length = 1000;
		LinkedList<BookComment> users = new LinkedList<BookComment>();
		
		while (true) {
			BookComment[] partBookComments = getBookComments(start, length);
			for (BookComment u : partBookComments) {
				users.add(u);
			}
			start += length;
			if (partBookComments.length < length) {
				break;
			}
		}
		
		return users.toArray(new BookComment[0]);
	}
	
	/**
	 * 按给定位置，给出BookComments
	 * @param start
	 * @param length
	 * @return
	 */
	public BookComment[] getBookComments(int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" LIMIT ?,?";
		BookComment[] BookCommentArr = (BookComment[])dbUtils.executeQuery(sql, new Object[]{start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookComment> list = new LinkedList<BookComment>(); 
				while (rs.next()) {
					BookComment u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookComment[0]);
			}
		});
		return BookCommentArr;
	}
	
	public BookComment[] getBookCommentsByBook(long bookId, int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" WHERE book_id=? LIMIT ?,?";
		BookComment[] BookCommentArr = (BookComment[])dbUtils.executeQuery(sql, new Object[]{bookId, start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookComment> list = new LinkedList<BookComment>(); 
				while (rs.next()) {
					BookComment u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookComment[0]);
			}
		});
		return BookCommentArr;
	}
	
	public BookComment[] getBookCommentsByUser(long userId, int start, int length) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+" WHERE user_id=? LIMIT ?,?";
		BookComment[] BookCommentArr = (BookComment[])dbUtils.executeQuery(sql, new Object[]{userId, start, length}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookComment> list = new LinkedList<BookComment>(); 
				while (rs.next()) {
					BookComment u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookComment[0]);
			}
		});
		return BookCommentArr;
	}
	
	private BookComment readOneRow(ResultSet rs) throws Exception {
		java.sql.Timestamp d;
		BookComment o = new BookComment();
		o.setId(rs.getLong(1));
		o.setBookId(rs.getLong(2));
		o.setUserId(rs.getLong(3));
		o.setContent(rs.getString(4));
		o.setImgUrlsJson(rs.getString(5));
		o.setLikeNum(rs.getInt(6));
		d = rs.getTimestamp(7);
		o.setCreateTime(d != null ? d.getTime() : 0);
		d = rs.getTimestamp(8);
		o.setLastModifyTime(d != null ? d.getTime() : 0);
		return o;
	}
	
	
	public BookComment getBookCommentById(long id) {
		String sql = "SELECT * FROM " + TABLENAME + " " 
				+ " WHERE id=? ";
		return (BookComment)dbUtils.executeQuery(sql, new Object[]{id}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				while (rs.next()) {
					BookComment u = readOneRow(rs);
					return u;
				}
				return null;
			}
		});
	}
	
	public BookComment getBookCommentByUserBook(long limerBookId, long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE user_id=?  and limer_book_id=?";
		BookComment[] BookCommentArr = (BookComment[])dbUtils.executeQuery(sql, new Object[]{userId, limerBookId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookComment> list = new LinkedList<BookComment>(); 
				while (rs.next()) {
					BookComment u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookComment[0]);
			}
		});
		BookComment br = null;
		long max = 0;
		for (BookComment b : BookCommentArr) {
			if (b.getId() > max) {
				max = b.getId();
				br = b;
			}
		}
		
		return br;
		
	}
	
	public BookComment[] getBookCommentByUserId(long userId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE user_id=? ";
		BookComment[] BookCommentArr = (BookComment[])dbUtils.executeQuery(sql, new Object[]{userId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookComment> list = new LinkedList<BookComment>(); 
				while (rs.next()) {
					BookComment u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookComment[0]);
			}
		});
		return BookCommentArr;
		
	}
	
	public BookComment[] getBookCommentByIsbn(String isbn) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE isbn=? ";
		BookComment[] BookCommentArr = (BookComment[])dbUtils.executeQuery(sql, new Object[]{isbn}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookComment> list = new LinkedList<BookComment>(); 
				while (rs.next()) {
					BookComment u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookComment[0]);
			}
		});
		return BookCommentArr;
		
	}
	
	public BookComment[] getBookCommentByLimerBookId(long limerBookId) {
		String sql = "SELECT * FROM " + TABLENAME + " "
				+ " WHERE limer_book_id=? ";
		BookComment[] BookCommentArr = (BookComment[])dbUtils.executeQuery(sql, new Object[]{limerBookId}, new ResultSetHandler(){
			public Object handle(ResultSet rs, Object[] params) throws Exception {
				LinkedList<BookComment> list = new LinkedList<BookComment>(); 
				while (rs.next()) {
					BookComment u = readOneRow(rs);
					list.add(u);
				}
				return list.toArray(new BookComment[0]);
			}
		});
		return BookCommentArr;
		
	}
	
	
	/**
	 * 添加一个BookComment进数据库
	 * @param BookComment
	 * @return 添加成功，则返回true
	 */
	public boolean addBookComment(BookComment o) {
		
		String sql = DBUtils.genAddRowSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				o.getBookId(),
				o.getUserId(),
				o.getContent(),
				o.getImgUrlsJson(),
				o.getLikeNum(),
				TextUtil.formatTime(o.getCreateTime()),
				TextUtil.formatTime(o.getLastModifyTime())
		});
	}
	
	/**
	 * 更新一个BookComment进数据库
	 * @param BookComment
	 * @return 添加成功，则返回true
	 */
	public boolean updateBookComment(BookComment o) {
		String sql = DBUtils.genUpdateTableSql(TABLENAME, COL_NAMES);
		return dbUtils.executeUpdate(sql, new Object[]{
				
				o.getBookId(),
				o.getUserId(),
				o.getContent(),
				o.getImgUrlsJson(),
				o.getLikeNum(),
				TextUtil.formatTime(o.getCreateTime()),
				TextUtil.formatTime(o.getLastModifyTime()),
				o.getId(),
				});
	}
	
	
	private static final String[] COL_NAMES = {"id", "book_id", "user_id", "content", 
			"img_urls_json", "like_num", "create_time", "last_modify_time"};
	
	
	private void createBookCommentDBTable() {

		String sql = "  CREATE TABLE `"+ TABLENAME.toLowerCase() +"` (\r\n" + 
				"  `id` bigint(20) NOT NULL auto_increment,\r\n" +
				"  `book_id` bigint(20) default 0,\r\n" +
				"  `user_id` bigint(20) default 0,\r\n" +
				"  `content` varchar(255) default NULL,\r\n" +
				"  `img_urls_json` varchar(255) default NULL,\r\n" +
				"  `like_num` smallint(2) default 0,\r\n" +
				"  `create_time` datetime default NULL,\r\n" +
				"  `last_modify_time` datetime default NULL,\r\n" +
				
				"  PRIMARY KEY  (`ID`)\r\n" + 
				") ENGINE=InnoDB DEFAULT CHARSET=utf8;";
		
		if (dbUtils.createTable(TABLENAME, sql)) {
			sql = " CREATE INDEX index_"+ TABLENAME +"_userid" + 
					" ON "+ TABLENAME +" (user_id)";
			dbUtils.executeSql(sql, null);
			
			sql = " CREATE INDEX index_"+ TABLENAME +"_bookid" + 
					" ON "+ TABLENAME +" (bookid)";
			dbUtils.executeSql(sql, null);
			
		}
		
	}
	
	public void init() {
		this.createBookCommentDBTable();
	}
	
	public DBUtils getDbUtils() {
		return dbUtils;
	}

	public void setDbUtils(DBUtils dbUtils) {
		this.dbUtils = dbUtils;
	}
}
