package com.lebaor.limer.data;

/**
 * 用户借阅还回信息
 * 每次借阅都产生一条记录，还回则更新此记录
 * @author lixjl
 *
 */
public class BorrowRecord {
	long id;
	long userId;
	String isbn;
	long limerBookId;
	long orderId;
	int status;
	long borrowTime;//借书时间
	long returnTime;//归还时间
	long punishTime;//惩罚时间
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public long getOrderId() {
		return orderId;
	}
	public void setOrderId(long orderId) {
		this.orderId = orderId;
	}
	public String getIsbn() {
		return isbn;
	}
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public long getLimerBookId() {
		return limerBookId;
	}
	public void setLimerBookId(long limerBookId) {
		this.limerBookId = limerBookId;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public long getBorrowTime() {
		return borrowTime;
	}
	public void setBorrowTime(long borrowTime) {
		this.borrowTime = borrowTime;
	}
	public long getReturnTime() {
		return returnTime;
	}
	public void setReturnTime(long returnTime) {
		this.returnTime = returnTime;
	}
	public long getPunishTime() {
		return punishTime;
	}
	public void setPunishTime(long punishTime) {
		this.punishTime = punishTime;
	}
	
	
}
