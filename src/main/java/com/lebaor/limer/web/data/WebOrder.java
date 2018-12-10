package com.lebaor.limer.web.data;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.limer.data.Order;

public class WebOrder {
	String orderTime;//下单时间
	int status;//状态
	String statusDesc;//状态描述
	String mchTradeNo;//订单号
	int totalFee;//原价
	int realFee;//实际价格
	int deposit;//押金价格，单位分
	JSONArray items; //itemsjson
	
	String remainTimeDesc;//剩余借阅时间描述
	
	public String toJSON() {
		try {
			JSONObject o = new JSONObject();
			o.put("orderTime", orderTime);
			o.put("status", status);
			o.put("statusDesc", statusDesc);
			o.put("mchTradeNo", mchTradeNo);
			o.put("totalFee", totalFee);
			o.put("realFee", realFee);
			o.put("deposit", deposit);
			o.put("items", items);
			o.put("remainTimeDesc", remainTimeDesc);
			return o.toString();
		} catch (Exception e) {
			return "{error: 'format error.'}";
		}
		
	}

	public static WebOrder parseJSON(String s) {
		try {
			return parseJSON(new JSONObject(s));
		} catch (Exception e) {
			return null;
		}
	}


	public static WebOrder parseJSON(JSONObject o) {
		try {
			WebOrder n = new WebOrder();
			n.orderTime = o.getString("orderTime");
			n.status = o.getInt("status");
			n.statusDesc = o.getString("statusDesc");
			n.mchTradeNo = o.getString("mchTradeNo");
			n.totalFee = o.getInt("totalFee");
			n.realFee = o.getInt("realFee");
			n.deposit = o.getInt("deposit");
			n.items = o.getJSONArray("items");
			n.remainTimeDesc = o.getString("remainTimeDesc");
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
	
	public static class WebOrderItem {
		String isbn;
		String title;
		String author;
		String coverUrl;
		long limerBookId;
		
		public JSONObject toJSONObject() {
			try {
				return new JSONObject(toJSON());
			} catch (Exception e) {
				return new JSONObject();
			}
		}
		
		public String toJSON() {
			try {
				JSONObject o = new JSONObject();
				o.put("isbn", isbn);
				o.put("title", title);
				o.put("author", author);
				o.put("coverUrl", coverUrl);
				o.put("limerBookId", limerBookId);
				return o.toString();
			} catch (Exception e) {
				return "{error: 'format error.'}";
			}
			
		}

		public static WebOrderItem parseJSON(String s) {
			try {
				return parseJSON(new JSONObject(s));
			} catch (Exception e) {
				return null;
			}
		}


		public static WebOrderItem parseJSON(JSONObject o) {
			try {
				WebOrderItem n = new WebOrderItem();
				n.isbn = o.getString("isbn");
				n.title = o.getString("title");
				n.author = o.getString("author");
				n.coverUrl = o.getString("coverUrl");
				n.limerBookId = o.getLong("limerBookId");
				return n;
			} catch (Exception e) {
				return null;
			}
		}


		public String toString() {
			return toJSON();
		}
		
		public String getIsbn() {
			return isbn;
		}
		public void setIsbn(String isbn) {
			this.isbn = isbn;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
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
		public long getLimerBookId() {
			return limerBookId;
		}
		public void setLimerBookId(long limerBookId) {
			this.limerBookId = limerBookId;
		}
		
		
	}

	public String getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(String orderTime) {
		this.orderTime = orderTime;
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

	public String getMchTradeNo() {
		return mchTradeNo;
	}

	public void setMchTradeNo(String mchTradeNo) {
		this.mchTradeNo = mchTradeNo;
	}

	public int getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

	public int getRealFee() {
		return realFee;
	}

	public void setRealFee(int realFee) {
		this.realFee = realFee;
	}

	public JSONArray getItems() {
		return items;
	}

	public void setItems(JSONArray items) {
		this.items = items;
	}

	public String getRemainTimeDesc() {
		return remainTimeDesc;
	}

	public void setRemainTimeDesc(String remainTimeDesc) {
		this.remainTimeDesc = remainTimeDesc;
	}

	public int getDeposit() {
		return deposit;
	}

	public void setDeposit(int deposit) {
		this.deposit = deposit;
	}
	
	
}
