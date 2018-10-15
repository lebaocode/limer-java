package com.lebaor.limer.data;

public class LimerConstants {
	//对于一本书，所有人看到的状态有如下几种：
	public final static int LIMER_BOOK_STATUS_READY = 1;//可借阅
	public final static int LIMER_BOOK_STATUS_BUSY = 2;//已被借出
	public final static int LIMER_BOOK_STATUS_INVALID = 3;//捐赠人取回 等同于该书下架
	public final static int LIMER_BOOK_STATUS_LOST = 4;//丢失或下落不明
	public static String explainDonateBookStatus(int s) {
		if (s == 1) return "未借出";
		else if (s == 2) return "已借出";
		else if (s == 3) return "已取回";
		else if (s == 4) return "已丢失";
		return "异常";
	}
	public static String explainBookStatus(int s) {
		if (s == 1) return "可借阅";
		else if (s == 2) return "已借完";
		return "无库存";
	}
	public static String explainBorrowBookStatus(int s) {
		if (s == 1) return "借阅中";
		else if (s == 2) return "已归还";
		else if (s == 3) return "已罚款";
		return "未知";
	}
	
		
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
	
	public final static int ORDER_STATUS_NOT_XIADAN = 0;//未下单
	public final static int ORDER_STATUS_XIADAN = 1;//刚下单
	public final static int ORDER_STATUS_PAY_SUCCESS = 2;//支付成功
	public final static int ORDER_STATUS_PAY_FAILED = 3;//支付失败
	public final static int ORDER_STATUS_PAY_EXPIRED = 4;//时间过期
	
	public final static int USER_STATUS_NORMAL = 0;//正常
	
	public static String explainOrderStatus(int s) {
		if (s == 2) return "支付成功";
		else if (s == 3) return "支付失败";
		
		return "未支付";
	}
	
	public final static String MINIPROGRAM_APPID = "wx2d040ec926fe84b2";
	public final static String MINIPROGRAM_APPSECRET = "9cfb7831eeebad5e1532014b4401e6b7";
	public final static String CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";
	
	public static int computeLogisticsFee(int pageNum) {
		return pageNum <= 30 ? 210 : pageNum* 7;
	}
}
