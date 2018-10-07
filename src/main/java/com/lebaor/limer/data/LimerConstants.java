package com.lebaor.limer.data;

public class LimerConstants {
	//对于一本书，所有人看到的状态有如下几种：
	public final static int LIMER_BOOK_STATUS_READY = 1;//可借阅
	public final static int LIMER_BOOK_STATUS_BUSY = 2;//已被借出
	public final static int LIMER_BOOK_STATUS_INVALID = 3;//捐赠人取回 等同于该书下架
	public final static int LIMER_BOOK_STATUS_LOST = 4;//丢失或下落不明
		
	public final static int BORROW_STATUS_ING = 1;//借阅中
	public final static int BORROW_STATUS_RETURNED = 2;//已归还
	public final static int BORROW_STATUS_PUNISHED = 3;//已罚款，不需要再归还
	
	public final static int LOGISTICS_STATUS_NOTCREATE = 0;//还未下单成功
	public final static int LOGISTICS_STATUS_ONROAD = 1;//在路上
	public final static int LOGISTICS_STATUS_DELIVERED = 2;//已送达
	public final static int LOGISTICS_STATUS_RETURNED = 3;//已退回
	public final static int LOGISTICS_STATUS_CANCELD = 4;//已取消订单
	
	public final static int SCORE_BORROW_ONE_BOOK = 10;//借一本书，减多少积分
	public final static int SCORE_DONATE_ONE_BOOK = 100;//捐赠一本书，加多少积分
	
	public final static String MINIPROGRAM_APPID = "wx2d040ec926fe84b2";
	public final static String MINIPROGRAM_APPSECRET = "9cfb7831eeebad5e1532014b4401e6b7";
	public final static String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
}
