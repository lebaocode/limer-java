package com.lebaor.limer.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

/**
 * 书单里的一本书
 * @author lixjl
 *
 */
public class BookListItem {
	long id;
	long bookListId;//书单id
	long userId;//哪位用户贡献的 0表示系统管理员加的
	String isbn;//哪本书

	long createTime;//添加时间
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("bookListId", Long.toString(bookListId));
			o.put("userId", Long.toString(userId));
			o.put("isbn", isbn);
			o.put("createTime", Long.toString(createTime));
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static BookListItem parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static BookListItem parseJSON(JSONObject o) {
		try {
			BookListItem n = new BookListItem();
			n.id = JSONUtil.getLong(o, "id");
			n.bookListId = JSONUtil.getLong(o, "bookListId");
			n.userId = JSONUtil.getLong(o, "userId");
			n.isbn = JSONUtil.getString(o, "isbn");
			n.createTime = JSONUtil.getLong(o, "createTime");
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

	public long getBookListId() {
		return bookListId;
	}

	public void setBookListId(long bookListId) {
		this.bookListId = bookListId;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
}
