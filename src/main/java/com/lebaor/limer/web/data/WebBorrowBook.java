package com.lebaor.limer.web.data;

import org.json.JSONObject;

public class WebBorrowBook {
	long bookId;
	long limerBookId;
	String title;
	String isbn;
	String author;
	String coverUrl;
	int status;
	String statusDesc;
	long borrowTime;
	
	public void setInfo(WebBookDetail b, long limerBookId, int status, String statusDesc, long borrowTime) {
		this.bookId = b.getBook().getId();
		this.limerBookId = limerBookId;
		this.title = b.getBook().getTitle();
		this.isbn = b.getBook().getIsbn13();
		this.author = b.getBook().getAuthors().toString();
		this.coverUrl = b.getBook().getCoverUrl();
		this.status = status;
		this.statusDesc = statusDesc;
		this.borrowTime = borrowTime;
	}
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("bookId", bookId);
			o.put("limerBookId", limerBookId);
			o.put("title", title);
			o.put("isbn", isbn);
			o.put("author", author);
			o.put("coverUrl", coverUrl);
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
			n.bookId = o.getLong("bookId");
			n.limerBookId = o.getLong("limerBookId");
			n.title = o.getString("title");
			n.isbn = o.getString("isbn");
			n.author = o.getString("author");
			n.coverUrl = o.getString("coverUrl");
			n.status = o.getInt("status");
			n.statusDesc = o.getString("statusDesc");
			n.borrowTime = Long.parseLong(o.getString("borrowTime"));
			return n;
		} catch (Exception e) {
			return null;
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
