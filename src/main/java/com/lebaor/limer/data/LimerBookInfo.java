package com.lebaor.limer.data;

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
	long bookId;
	long donateUserId;//捐赠人id
	long donateTime;//捐赠时间
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getBookId() {
		return bookId;
	}
	public void setBookId(long bookId) {
		this.bookId = bookId;
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
	
	
	
}
