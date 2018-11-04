package com.lebaor.limer.web.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

/**
 * 一本书，一个用户，的评论
 * @author lixjl
 *
 */
public class WebBookComment {
	long userId;
	String userLogo;
	String userName;
	String createTimeDisplay;
	
	String content;
	JSONArray imgUrls;
	int likeNum;
	long commentId;
	
	long bookId;
	String isbn;
	String bookImg;
	String bookTitle;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("userId", Long.toString(userId));
			o.put("userLogo", userLogo);
			o.put("userName", userName);
			o.put("createTimeDisplay", createTimeDisplay);
			o.put("content", content);
			o.put("imgUrls", imgUrls);
			o.put("likeNum", likeNum);
			o.put("commentId", Long.toString(commentId));
			o.put("bookId", Long.toString(bookId));
			o.put("isbn", isbn);
			o.put("bookImg", bookImg);
			o.put("bookTitle", bookTitle);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebBookComment parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebBookComment parseJSON(JSONObject o) {
		try {
			WebBookComment n = new WebBookComment();
			n.userId = JSONUtil.getLong(o, "userId");
			n.userLogo = JSONUtil.getString(o, "userLogo");
			n.userName = JSONUtil.getString(o, "userName");
			n.createTimeDisplay = JSONUtil.getString(o, "createTimeDisplay");
			n.content = JSONUtil.getString(o, "content");
			n.imgUrls = JSONUtil.getJSONArray(o, "imgUrls");
			n.likeNum = JSONUtil.getInt(o, "likeNum");
			n.commentId = JSONUtil.getLong(o, "commentId");
			n.bookId = JSONUtil.getLong(o, "bookId");
			n.isbn = JSONUtil.getString(o, "isbn");
			n.bookImg = JSONUtil.getString(o, "bookImg");
			n.bookTitle = JSONUtil.getString(o, "bookTitle");
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
	}


	public JSONObject toJSONObject() {
		try {
			return new JSONObject(toJSON());
		} catch (Exception e) {
			return new JSONObject();
		}
	}
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getUserLogo() {
		return userLogo;
	}
	public void setUserLogo(String userLogo) {
		this.userLogo = userLogo;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCreateTimeDisplay() {
		return createTimeDisplay;
	}
	public void setCreateTimeDisplay(String createTimeDisplay) {
		this.createTimeDisplay = createTimeDisplay;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public JSONArray getImgUrls() {
		return imgUrls;
	}
	public void setImgUrls(JSONArray imgUrls) {
		this.imgUrls = imgUrls;
	}
	public int getLikeNum() {
		return likeNum;
	}
	public void setLikeNum(int likeNum) {
		this.likeNum = likeNum;
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
	public String getBookImg() {
		return bookImg;
	}
	public void setBookImg(String bookImg) {
		this.bookImg = bookImg;
	}
	public String getBookTitle() {
		return bookTitle;
	}
	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public long getCommentId() {
		return commentId;
	}

	public void setCommentId(long commentId) {
		this.commentId = commentId;
	}
	
	
}
