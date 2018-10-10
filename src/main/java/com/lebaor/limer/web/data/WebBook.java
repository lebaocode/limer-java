package com.lebaor.limer.web.data;

import org.json.JSONArray;
import org.json.JSONObject;


public class WebBook {
	long bookId;
	String isbn;
	String coverUrl;
	String title;
	String subTitle;
	String author;
	String publisher;
	String[] tags;
	
	public static WebBook parseJSON(JSONObject o) {
		try {
			WebBook n = new WebBook();
			n.bookId = o.getLong("bookId");
			n.isbn = o.getString("isbn13");
			n.coverUrl = o.getString("coverUrl");
			n.title = o.getString("title");
			n.subTitle = o.getString("subTitle");
			n.author = o.getString("author");
			n.publisher = o.getString("publisher");
			JSONArray arr = o.getJSONArray("tags");
			if (arr == null) n.tags = new String[0];
			else {
				n.tags = new String[arr.length()];
				for (int i = 0; i < arr.length(); i++) {
					n.tags[i] = arr.getJSONObject(i).getString("name");
				}
			}
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
			o.put("publisher", publisher);
			
			JSONArray arr = new JSONArray();
			if (tags == null) {
				o.put("tags", arr.toString());
			} else {
				for (String tag: tags) {
					JSONObject jo = new JSONObject();
					jo.put("name", tag);
					arr.put(jo);
				}
			}
			
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}
	
	public String toString() {
		return toJSON();
	}
	
	
	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String[] getTags() {
		return tags;
	}

	public void setTags(String[] tags) {
		this.tags = tags;
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
