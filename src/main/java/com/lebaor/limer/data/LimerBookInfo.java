package com.lebaor.limer.data;

import org.json.JSONObject;

import com.lebaor.utils.JSONUtil;

/**
 * 书籍本身信息在Book里，这里只包含本产品相关的书籍信息，比如书籍内部编号，由谁捐赠等。
 * 同一本书（isbn相同）有可能被多人捐赠。
 * 本表包含：
 * 1. 每本书由谁捐赠，每人捐赠哪些书
 * 
 * 一本被捐赠的书，在此表里应只有一条记录。
 * @author lixjl
 *
 */
public class LimerBookInfo {
	
	long id;
	String isbn;
	int storeNum;//购买数量
	String extraInfo;//如果是一套书里的其中一本，则把每一本信息放在这里isSingle(此isbn是否单本),no, title, price, pageNum
	long lastUpdateTime;
	
	String bookTitle;//单本title 如果不是单本，则不需要填写。因为在book表里都有
	int bookPrice; //单本price
	int bookPageNum;//单本的书页数量
	
	boolean isIsbnSingle;//isbn是否本书的（true），还是该书系列的（false）
	String bookNo;//单本书在系里里的序号 如果isIsbnSingle=false，再填。也可以不填。
	String bookSeriesTitle;//系列书的名称   如果isIsbnSingle=false，必填。否则不填。
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("isbn", isbn);
			o.put("storeNum", storeNum);
			o.put("extraInfo", extraInfo);
			o.put("lastUpdateTime", Long.toString(lastUpdateTime));
			o.put("bookTitle", bookTitle);
			o.put("bookPrice", bookPrice);
			o.put("bookPageNum", bookPageNum);
			o.put("isIsbnSingle", isIsbnSingle);
			o.put("bookNo", bookNo);
			o.put("bookSeriesTitle", bookSeriesTitle);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static LimerBookInfo parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static LimerBookInfo parseJSON(JSONObject o) {
		try {
			LimerBookInfo n = new LimerBookInfo();
			n.id = JSONUtil.getLong(o, "id");
			n.isbn = JSONUtil.getString(o, "isbn");
			n.storeNum = JSONUtil.getInt(o, "storeNum");
			n.extraInfo = JSONUtil.getString(o, "extraInfo");
			n.lastUpdateTime = JSONUtil.getLong(o, "lastUpdateTime");
			n.bookTitle = JSONUtil.getString(o, "bookTitle");
			n.bookPrice = JSONUtil.getInt(o, "bookPrice");
			n.bookPageNum = JSONUtil.getInt(o, "bookPageNum");
			n.isIsbnSingle = JSONUtil.getBoolean(o, "isIsbnSingle");
			n.bookNo = JSONUtil.getString(o, "bookNo");
			n.bookSeriesTitle = JSONUtil.getString(o, "bookSeriesTitle");
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
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public int getStoreNum() {
		return storeNum;
	}
	public void setStoreNum(int storeNum) {
		this.storeNum = storeNum;
	}
	public String getExtraInfo() {
		return extraInfo;
	}
	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public String getBookTitle() {
		return bookTitle;
	}
	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}
	public int getBookPrice() {
		return bookPrice;
	}
	public void setBookPrice(int bookPrice) {
		this.bookPrice = bookPrice;
	}
	public int getBookPageNum() {
		return bookPageNum;
	}
	public void setBookPageNum(int bookPageNum) {
		this.bookPageNum = bookPageNum;
	}
	public boolean isIsbnSingle() {
		return isIsbnSingle;
	}
	public void setIsbnSingle(boolean isIsbnSingle) {
		this.isIsbnSingle = isIsbnSingle;
	}
	public String getBookNo() {
		return bookNo;
	}
	public void setBookNo(String bookNo) {
		this.bookNo = bookNo;
	}
	public String getBookSeriesTitle() {
		return bookSeriesTitle;
	}
	public void setBookSeriesTitle(String bookSeriesTitle) {
		this.bookSeriesTitle = bookSeriesTitle;
	}
	
	
	
}
