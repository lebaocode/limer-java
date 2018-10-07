package com.lebaor.limer.data;

/**
 * 
 * 本表记录：
 * 1. 书籍当前是否可借阅
 * 2. 书籍当前持有人是谁
 * @author lixjl
 *
 */
public class LimerBookStatus {
	long id;
	long bookId;
	long limerBookId;
	int status;//当前书籍状态，详见LIMER_BOOK_STATUS
	long holderUserId;//当前持有人id（一旦借阅就更新持有人id）最开始为捐赠者
	long lastUpdateTime;
	
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
	public long getHolderUserId() {
		return holderUserId;
	}
	public void setHolderUserId(long holderUserId) {
		this.holderUserId = holderUserId;
	}
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	
}
