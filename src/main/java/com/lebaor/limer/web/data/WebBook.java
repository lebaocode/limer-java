package com.lebaor.limer.web.data;

import org.json.JSONObject;


public class WebBook {
	long bookId;
	String isbn;
	String coverUrl;
	String title;
	String subTitle;
	String author;
	
	public static WebBook parseJSON(JSONObject o) {
		try {
			WebBook n = new WebBook();
			n.bookId = o.getLong("bookId");
			n.isbn = o.getString("isbn13");
			n.coverUrl = o.getString("coverUrl");
			n.title = o.getString("title");
			n.subTitle = o.getString("subTitle");
			n.author = o.getString("author");
			return n;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static WebBook parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("bookId", bookId);
			o.put("isbn", isbn);
			o.put("coverUrl", coverUrl);
			o.put("title", title);
			o.put("subTitle", subTitle);
			o.put("author", author);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}
	
	public String toString() {
		return toJSON();
	}
	
	
	public long getBookId() {
		return bookId;
	}

	public void setBookId(long bookId) {
		this.bookId = bookId;
	}

	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public String getCoverUrl() {
		return coverUrl;
	}
	public void setCoverUrl(String coverUrl) {
		this.coverUrl = coverUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubTitle() {
		return subTitle;
	}
	public void setSubTitle(String subTitle) {
		this.subTitle = subTitle;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	
	
}
