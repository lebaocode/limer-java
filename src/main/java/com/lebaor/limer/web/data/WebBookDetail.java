package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.limer.data.Book;

public class WebBookDetail {
	Book book;
	
	public float computeScore() {
		if (book == null) return 0;
		if (book.getRaterNum() > 50) return Float.parseFloat(book.getRating());
		else return (float)(Float.parseFloat(book.getRating())*4/5);
	}
	
	public JSONObject toWebJSONObject() {
		try {
			return new JSONObject(toWebJSON());
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	
	public String toWebJSON() {
		if (book == null) return "{}";
		return book.toJSON();
		
	}
	
	public String toDoubanJSON() {
		if (book == null) return "{}";
		return book.getJson();
		
	}

	public String toString() {
		return toWebJSON();
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}
	
	
}
