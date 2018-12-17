package com.lebaor.limer.data;

public class LimerConstants {
	//对于一本书，所有人看到的状态有如下几种：
	public final static int LIMER_BOOK_STATUS_READY = 1;//可借阅
	public final static int LIMER_BOOK_STATUS_BUSY = 2;//已被借出
	public final static int LIMER_BOOK_STATUS_INVALID = 3;//捐赠人取回 等同于该书下架
	public final static int LIMER_BOOK_STATUS_LOST = 4;//丢失或下落不明
	public static boolean isBookReady(int status) {
		return status == LIMER_BOOK_STATUS_READY;
	}
	
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
	public final static int ORDER_STATUS_MCH_PRE_XIADAN = 1;//商户预下单
	public final static int ORDER_STATUS_WX_PRE_XIADAN = 2;//预下单
	public final static int ORDER_STATUS_PAY_SUCCESS = 3;//支付成功
	public final static int ORDER_STATUS_PAY_FAILED = 4;//支付失败
	public final static int ORDER_STATUS_PAY_EXPIRED = 5;//时间过期
	
	public static boolean isOrderEnd(int status) {
		return status == ORDER_STATUS_PAY_SUCCESS 
				|| status == ORDER_STATUS_PAY_FAILED
				|| status == ORDER_STATUS_PAY_EXPIRED;
	}
	
	public static boolean isOrderSuccess(int status) {
		return status == ORDER_STATUS_PAY_SUCCESS;
	}
	
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
	
	public final static String PAY_METHOD_WXPAY = "wxpay";
	public final static String PAY_METHOD_ALIPAY = "alipay";
	public final static String FEE_TYPE_RMB = "CNY";
	
	public final static int ACTIVITY_STATUS_NOTPUBLISHED = 0;//未发布
	public final static int ACTIVITY_STATUS_DOING = 0;//进行中
	public final static int ACTIVITY_STATUS_END = 0;//已结束
	
	public final static String[] BOOKLIST_TYPE_ARRAY = new String[] {
		"人文积淀 1.1.1",
		"人文情怀	1.1.2",
		"审美情趣	1.1.3",
		"理性思维	1.2.1",
		"批判质疑	1.2.2",
		"勇于探究	1.2.3",
		"世界认知	1.3.1",
		"英文学习	1.3.2",
		"社会认知	1.3.3",
		"学习习惯	2.1.1",
		"勤于反思	2.1.2",
		"信息意识	2.1.3",
		"生活习惯	2.2.1",
		"性格培养	2.2.2",
		"自我管理	2.2.3",
		"社会责任	3.1.1",
		"国家认同	3.1.2",
		"国际理解	3.1.3",
		"劳动意识	3.2.1",
		"问题解决	3.2.2",
		"技术运用	3.2.3",
		"其它	0.0.0"
	};
	
	public static String getBookListTypeDesc(String code) {
		if (code != null) {
			for (int i = 0; i < BOOKLIST_TYPE_ARRAY.length; i++) {
				String[] arr = BOOKLIST_TYPE_ARRAY[i].split("\\s+");
				if (arr[1].equals(code)) {
					return arr[0];
				}
			}
		}
		return "其它";
	}
	
	public static String parseBookListTypeFromTag(String tag) {
		if (tag != null) {
			for (int i = 0; i < BOOKLIST_TYPE_ARRAY.length; i++) {
				String[] arr = BOOKLIST_TYPE_ARRAY[i].split("\\s+");
				if (arr[0].equals(tag)) {
					return arr[1];
				}
			}
		}
		return "其它";
	}
	
	public static final String PRODUCT_ID_MEM_MONTH = "member_month";
	
	//按地区收会员费
	public static int getMemberPrice(String address) {
		if (address.startsWith("北京")) {
			return 3000;//30元
		} else if (address.startsWith("青海")
				|| address.startsWith("海南")
				|| address.startsWith("云南")
				|| address.startsWith("贵州")
				|| address.startsWith("广西")
				|| address.startsWith("西藏")
				|| address.startsWith("新疆")
				) {
			return 5000;
		} else {
			return 4000;
		}
	}
	
	public static final int DEPOSIT_FEE = 10000;//100元
	
	public static void main(String[] args) {
		for (int i =11 ;i <100; i++  ) {
			for (int j = 11;j<100;j++) {
				int n = i*j;
				
				if (n % 2 == 1
						&& (i % 2 == 1)
						&& (j % 2 == 1)
						&& (i/10 % 2 == 1)
						&& (j/10 % 2 == 1)
						&& (n/10 % 2 == 1)
						&& (n/100 % 2 == 1)
						&& (n/1000 % 2 == 1)
						) {
					System.out.println(i+"x" + j +"=" + n);
				}
			}
		}
	}
}
