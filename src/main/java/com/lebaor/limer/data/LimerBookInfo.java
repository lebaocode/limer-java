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
	long donateUserId;//捐赠人id 如果为0，表示是公司的书
	int status;
	int degree = 80;//书籍新旧程度
	String extraInfo;//如果是一套书里的其中一本，则把每一本信息放在这里no, title, price, pageNum
	long donateTime;//捐赠时间
	long lastUpdateTime;
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("id", Long.toString(id));
			o.put("isbn", isbn);
			o.put("donateUserId", donateUserId);
			o.put("status", status);
			o.put("degree", degree);
			o.put("donateTime", Long.toString(donateTime));
			o.put("lastUpdateTime", Long.toString(lastUpdateTime));
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
			n.donateUserId = JSONUtil.getLong(o, "donateUserId");
			n.status = JSONUtil.getInt(o, "status");
			n.degree = JSONUtil.getInt(o, "degee");
			n.donateTime = JSONUtil.getLong(o, "donateTime");
			n.lastUpdateTime = JSONUtil.getLong(o, "lastUpdateTime");
			return n;
		} catch (Exception e) {
			return null;
		}
	}


	public String toString() {
		return toJSON();
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
	public long getDonateUserId() {
		return donateUserId;
	}
	public void setDonateUserId(long donateUserId) {
		this.donateUserId = donateUserId;
	}
	public long getDonateTime() {
		return donateTime;
	}
	public void setDonateTime(long donateTime) {
		this.donateTime = donateTime;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}
	
	public void setSingleBookInfo(int no, String title, int price, int pageNum) {
		try {
			JSONObject o = new JSONObject();
			o.put("no", no);
			o.put("title", title);
			o.put("price", price);
			o.put("pageNum", pageNum);
			this.extraInfo = o.toString();
		}catch (Exception e) {
			
		}
	}
	
	//是一个isbn里的单册书吗？即每册书没有自己的isbn，一整套书才有一个isbn
	public boolean isSingleBook() {
		if (this.extraInfo == null || this.extraInfo.trim().length() == 0 || this.extraInfo.equals("{}")) return false;
		
		return true;
	}
	
	public int getSingleBookNo() {
		try {
			JSONObject o = new JSONObject(this.extraInfo);
			return o.getInt("no");
		} catch (Exception e) {
			return 0;
		}
	}
	public String getSingleBookTitle() {
		try {
			JSONObject o = new JSONObject(this.extraInfo);
			return o.getString("title");
		} catch (Exception e) {
			return "";
		}
	}
	public int getSingleBookPrice() {
		try {
			JSONObject o = new JSONObject(this.extraInfo);
			return o.getInt("price");
		} catch (Exception e) {
			return 0;
		}
	}
	public int getSingleBookPageNum() {
		try {
			JSONObject o = new JSONObject(this.extraInfo);
			return o.getInt("pageNum");
		} catch (Exception e) {
			return 0;
		}
	}
	
}
