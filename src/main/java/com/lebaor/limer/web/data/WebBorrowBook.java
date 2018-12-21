package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

public class WebBorrowBook {
	long bookId;
	long limerBookId;
	String title;
	String isbn;
	String author;
	String coverUrl;
	int price;
	int pageNum;
	int status;
	String statusDesc;
	long borrowTime;

	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("bookId", Long.toString(bookId));
			o.put("limerBookId", Long.toString(limerBookId));
			o.put("title", title);
			o.put("isbn", isbn);
			o.put("author", author);
			o.put("coverUrl", coverUrl);
			o.put("price", price);
			o.put("pageNum", pageNum);
			o.put("status", status);
			o.put("statusDesc", statusDesc);
			o.put("borrowTime", Long.toString(borrowTime));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebBorrowBook parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebBorrowBook parseJSON(JSONObject o) {
		try {
			WebBorrowBook n = new WebBorrowBook();
			n.bookId = JSONUtil.getLong(o, "bookId");
			n.limerBookId = JSONUtil.getLong(o, "limerBookId");
			n.title = JSONUtil.getString(o, "title");
			n.isbn = JSONUtil.getString(o, "isbn");
			n.author = JSONUtil.getString(o, "author");
			n.coverUrl = JSONUtil.getString(o, "coverUrl");
			n.price = JSONUtil.getInt(o, "price");
			n.pageNum = JSONUtil.getInt(o, "pageNum");
			n.status = JSONUtil.getInt(o, "status");
			n.statusDesc = JSONUtil.getString(o, "statusDesc");
			n.borrowTime = JSONUtil.getLong(o, "borrowTime");
			return n;
		} catch (Exception e) {
			return null;
		}
	}

	public JSONObject toJSONObject() {
		try {
			return new JSONObject(toJSON());
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	
	public String toString() {
		return toJSON();
	}
	
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusDesc() {
		return statusDesc;
	}

	public void setStatusDesc(String statusDesc) {
		this.statusDesc = statusDesc;
	}

	public long getBorrowTime() {
		return borrowTime;
	}

	public void setBorrowTime(long borrowTime) {
		this.borrowTime = borrowTime;
	}

	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
		this.bookId = bookId;
	}
	public long getLimerBookId() {
		return limerBookId;
	}
	public void setLimerBookId(long limerBookId) {
		this.limerBookId = limerBookId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCoverUrl() {
		return coverUrl;
	}
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}
	
	
}
