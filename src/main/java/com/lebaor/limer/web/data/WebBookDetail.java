package com.lebaor.limer.web.data;

import org.json.JSONObject;

import com.lebaor.limer.data.Book;

public class WebBookDetail {
	Book book;
	int commentNum;
	int likeNum;
	
	public float computeScore() {
		if (book == null) return 0;
		if (book.getRaterNum() > 50) return Float.parseFloat(book.getRating());
		else return (float)(Float.parseFloat(book.getRating())*4/5);
	}
	
	public JSONObject toWebJSONObject() {
		try {
			JSONObject o;
			if (book != null) {
				o = new JSONObject(book.toWebJSON());
			} else {
				o = new JSONObject();
			}
			o.put("commentNum", commentNum);
			o.put("likeNum", likeNum);
			return o;
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	
	public String toWebJSON() {
		return this.toWebJSONObject().toString();
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

	public int getCommentNum() {
		return commentNum;
	}

	public void setCommentNum(int commentNum) {
		this.commentNum = commentNum;
	}

	public int getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
	}
}
