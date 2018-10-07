package com.lebaor.wx.data;

import org.apache.commons.configuration.XMLConfiguration;

public abstract class WxEventMsg extends WxReqMsg {
	public static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
	public static final String EVENT_TYPE_UNSUBSCRIBE = "unsubscribe";
	public static final String EVENT_TYPE_LOCATION = "LOCATION";
	public static final String EVENT_TYPE_CLICK = "CLICK";
	public static final String EVENT_TYPE_VIEW = "VIEW";
	public static final String EVENT_TYPE_MASSENDJOB = "MASSSENDJOBFINISH";
	
	protected String event;//事件类型
	
	public WxEventMsg(XMLConfiguration xmlConfig) {
		super(xmlConfig);
		this.event = xmlConfig.getString("Event", null);
	}
	
	public String getEvent() {
		return event;
	}
	
	public static class WxSubEventMsg extends WxEventMsg {
		public WxSubEventMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
		}
		public String getContent() {
			return  event;
		}
	}
	public static class WxUnSubEventMsg extends WxEventMsg {
		public WxUnSubEventMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
		}
		public String getContent() {
			return  event;
		}
	}
	
	public static class WxScanEventMsg extends WxEventMsg {
		String eventKey;//事件KEY值，qrscene_为前缀，后面为二维码的参数值
		String ticket;//二维码的ticket，可用来换取二维码图片
		
		public WxScanEventMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.eventKey = xmlConfig.getString("eventKey", null);
			this.ticket = xmlConfig.getString("ticket", null);
			
		}
		public String getContent() {
			return event + "," + eventKey +","+ ticket;
		}
		public String getScanValue() {
			if (eventKey != null && eventKey.startsWith("qrscene_")) {
				return eventKey.substring("qrscene_".length(), eventKey.length());
			}
			return null;
		}
		public String getEventKey() {
			return eventKey;
		}
		public String getTicket() {
			return ticket;
		}
		
	}
	public static class WxLocationEventMsg extends WxEventMsg {
		String latitude;//地理位置纬度
		String longitude;//地理位置经度
		String precision;//地理位置精度
		
		public WxLocationEventMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.latitude = xmlConfig.getString("Latitude", null);
			this.longitude = xmlConfig.getString("Longitude", null);
			this.precision = xmlConfig.getString("Precision", null);
			
		}
		public String getContent() {
			return event + "," +  latitude +","+ longitude +","+ precision;
		}
		public String getLatitude() {
			return latitude;
		}
		public String getLongitude() {
			return longitude;
		}
		public String getPrecision() {
			return precision;
		}
		
	}
	public static class WxMenuEventMsg extends WxEventMsg {
		String eventKey;//CLICK事件KEY值，与自定义菜单接口中KEY值对应; VIEW事件KEY值，设置的跳转URL
				
		public WxMenuEventMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.eventKey = xmlConfig.getString("eventKey", null);
			
		}
		
		public String getContent() {
			return  event + "," + eventKey;
		}
		public String getEventKey() {
			return eventKey;
		}
		
		
	}
	
	//高级群发之后的反馈事件
	public static class WxMassEndEventMsg extends WxEventMsg {
		/**
		 * 群发的结构，为“send success”或“send fail”或“err(num)”。但send success时，也有可能因用户拒收公众号的消息、系统错误等原因造成少量用户接收失败。err(num)是审核失败的具体原因，可能的情况如下：
err(10001), //涉嫌广告 err(20001), //涉嫌政治 err(20004), //涉嫌社会 err(20002), //涉嫌色情 err(20006), //涉嫌违法犯罪 err(20008), //涉嫌欺诈 err(20013), //涉嫌版权 err(22000), //涉嫌互推(互相宣传) err(21000), //涉嫌其他
		 */
		String status;
		int totalCount;
		int filterCount;
		int sentCount;
		int errorCount;
		
		public WxMassEndEventMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.status = xmlConfig.getString("Status", null);
			this.totalCount = xmlConfig.getInt("TotalCount");
			this.filterCount = xmlConfig.getInt("FilterCount");
			this.sentCount = xmlConfig.getInt("SentCount");
			this.errorCount = xmlConfig.getInt("ErrorCount");
			
		}
		public String getContent() {
			return  event + "," + status + "," + totalCount+"," + filterCount+"," + sentCount+","+errorCount;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public int getTotalCount() {
			return totalCount;
		}
		public void setTotalCount(int totalCount) {
			this.totalCount = totalCount;
		}
		public int getFilterCount() {
			return filterCount;
		}
		public void setFilterCount(int filterCount) {
			this.filterCount = filterCount;
		}
		public int getSentCount() {
			return sentCount;
		}
		public void setSentCount(int sentCount) {
			this.sentCount = sentCount;
		}
		public int getErrorCount() {
			return errorCount;
		}
		public void setErrorCount(int errorCount) {
			this.errorCount = errorCount;
		}
		
	}
	
}
