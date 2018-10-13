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
	
	public JSONObject toJSONObject() {
		try {
			return new JSONObject(toJSON());
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	
	public String toJSON() {
		if (book == null) return "{}";
		return book.toDoubanJSON();
		
	}

	public String toString() {
		return toJSON();
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}
	
	
}
