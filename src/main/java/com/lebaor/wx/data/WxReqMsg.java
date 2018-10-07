package com.lebaor.wx.data;

import java.io.StringReader;

import org.apache.commons.configuration.XMLConfiguration;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.wx.data.WxEventMsg.WxLocationEventMsg;
import com.lebaor.wx.data.WxEventMsg.WxMassEndEventMsg;
import com.lebaor.wx.data.WxEventMsg.WxMenuEventMsg;
import com.lebaor.wx.data.WxEventMsg.WxScanEventMsg;
import com.lebaor.wx.data.WxEventMsg.WxSubEventMsg;
import com.lebaor.wx.data.WxEventMsg.WxUnSubEventMsg;

public abstract class WxReqMsg {
	public static final String TYPE_TEXT = "text";//请求 & 回复
	public static final String TYPE_IMAGE = "image";//请求 & 回复
	public static final String TYPE_VOICE = "voice";//请求 & 回复
	public static final String TYPE_VIDEO = "video";//请求 & 回复
	public static final String TYPE_LOCATION = "location";//请求
	public static final String TYPE_LINK = "link";//请求
	public static final String TYPE_EVENT = "event";//请求
	
	String toUserName;//开发者微信号
	String fromUserName;//发送方帐号（一个OpenID）
	long createTime;//消息创建时间 （整型）
	String msgType;//text/
	long msgId;//消息id，64位整型；如果是event msg，则没有msgId
		
	public WxReqMsg(XMLConfiguration xmlConfig) {
		this.toUserName = xmlConfig.getString("ToUserName", null);
        this.fromUserName = xmlConfig.getString("FromUserName", null);
        this.createTime = xmlConfig.getInt("CreateTime", 0) * 1000L;
        this.msgType = xmlConfig.getString("MsgType", null);
        this.msgId = xmlConfig.getLong("MsgId", 0);
	}
	
	public String toString() {
		String s = "["+ TextUtil.formatTime(this.createTime) +"] ["+ this.getContent() +"]";
				
				//+ " ["+ this.msgType +"-"+ this.msgId + "]"
				//+" [" + this.toUserName + "," + this.fromUserName + "]";
			
		return s;
	}
	
	public boolean isEventMsg() {
		return this.msgType != null && this.msgType.equalsIgnoreCase(TYPE_EVENT);
	}
	
	public boolean isSubscribeMsg() {
		if (isEventMsg()) {
			WxEventMsg msg = (WxEventMsg)this;
			if (msg.getEvent().equals(WxEventMsg.EVENT_TYPE_SUBSCRIBE)) {
				return true;
			}
		}
		return false;
	}
	
	public abstract String getContent();
	
	public static WxReqMsg createMsg(String xml) {
		XMLConfiguration xmlConfig;
		try {
            xmlConfig = new XMLConfiguration();
            xmlConfig.load(new StringReader(xml));
            
            String type = xmlConfig.getString("MsgType", "");
            if (type.equalsIgnoreCase(TYPE_TEXT)) {
            	return new WxReqTextMsg(xmlConfig);
            } else if (type.equalsIgnoreCase(TYPE_IMAGE)) {
            	return new WxReqImageMsg(xmlConfig);
            } else if (type.equalsIgnoreCase(TYPE_VIDEO)) {
            	return new WxReqVideoMsg(xmlConfig);
            } else if (type.equalsIgnoreCase(TYPE_VOICE)) {
            	return new WxReqVoiceMsg(xmlConfig);
            } else if (type.equalsIgnoreCase(TYPE_LOCATION)) {
            	return new WxReqLocationMsg(xmlConfig);
            } else if (type.equalsIgnoreCase(TYPE_LINK)) {
            	return new WxReqLinkMsg(xmlConfig);
            } else if (type.equalsIgnoreCase(TYPE_EVENT)) {
            	String event = xmlConfig.getString("Event", "");
            	if (event.equalsIgnoreCase(WxEventMsg.EVENT_TYPE_UNSUBSCRIBE)) {
            		return new WxUnSubEventMsg(xmlConfig);
            	} else if (event.equalsIgnoreCase(WxEventMsg.EVENT_TYPE_SUBSCRIBE)) {
            		String eventKey = xmlConfig.getString("EventKey", "");
            		if (eventKey.trim().length() == 0) {
            			return new WxSubEventMsg(xmlConfig);
            		} else {
            			return new WxScanEventMsg(xmlConfig);
            		}
            	} else if (event.equalsIgnoreCase(WxEventMsg.EVENT_TYPE_LOCATION)) {
            		return new WxLocationEventMsg(xmlConfig);
            	} else if (event.equalsIgnoreCase(WxEventMsg.EVENT_TYPE_CLICK)
            			|| event.equalsIgnoreCase(WxEventMsg.EVENT_TYPE_VIEW)) {
            		return new WxMenuEventMsg(xmlConfig);
            	} else if (event.equalsIgnoreCase(WxEventMsg.EVENT_TYPE_MASSENDJOB)) {
            		return new WxMassEndEventMsg(xmlConfig);
            	} 
            }
            
		} catch(Exception e) {
			LogUtil.WEB_LOG.warn("createMsg error: " + xml, e);
		}
		
		return null;
	}
		

	public String getToUserName() {
		return toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public long getMsgId() {
		return msgId;
	}



	public static class WxReqTextMsg extends WxReqMsg{
		String content;//文本消息内容
		
		public WxReqTextMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.content = xmlConfig.getString("Content", null);
		}
		public String getContent() {
			return content;
		}
	}
	
	public static class WxReqImageMsg extends WxReqMsg{
		String picUrl;//图片链接
		String mediaId;//图片消息媒体id，可以调用多媒体文件下载接口拉取数据
		
		public WxReqImageMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.mediaId = xmlConfig.getString("MediaId", null);
			this.picUrl = xmlConfig.getString("PicUrl", null);
		}
		public String getPicUrl() {
			return picUrl;
		}

		public String getMediaId() {
			return mediaId;
		}
		public String getContent(){
			return this.picUrl + "," + mediaId;
		}
	}
	
	public static class WxReqVoiceMsg extends WxReqMsg{
		String format;//语音格式，如amr，speex等
		String mediaId;//语音消息媒体id，可以调用多媒体文件下载接口拉取数据
		
		public WxReqVoiceMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.mediaId = xmlConfig.getString("MediaId", null);
			this.format = xmlConfig.getString("Format", null);
		}
		
		public String getFormat() {
			return format;
		}

		public String getMediaId() {
			return mediaId;
		}
		public String getContent(){
			return this.format + "," + mediaId;
		}
	}
	
	public static class WxReqVideoMsg extends WxReqMsg{
		String thumbMediaId;//视频消息缩略图的媒体id，可以调用多媒体文件下载接口拉取数据
		String mediaId;//语音消息媒体id，可以调用多媒体文件下载接口拉取数据
		
		public WxReqVideoMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.mediaId = xmlConfig.getString("MediaId", null);
			this.thumbMediaId = xmlConfig.getString("ThumbMediaId", null);
		}
		public String getContent() {
			return thumbMediaId + "," + mediaId;
		}
		public String getThumbMediaId() {
			return thumbMediaId;
		}

		public String getMediaId() {
			return mediaId;
		}
	}
	public static class WxReqLocationMsg extends WxReqMsg{
		float locationX;//地理位置维度
		float locationY;//地理位置经度
		int scale;//地图缩放大小
		String label; //地理位置信息
		
		public WxReqLocationMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.locationX = xmlConfig.getFloat("Location_X", 0f);
			this.locationY = xmlConfig.getFloat("Location_Y", 0f);
			this.scale = xmlConfig.getInt("Scale", 0);
			this.label = xmlConfig.getString("Label", null);
		}
		
		public String getContent() {
			return  locationX + "," + locationY+"," + scale + "," + label;
		}
		public float getLocationX() {
			return locationX;
		}

		public float getLocationY() {
			return locationY;
		}

		public int getScale() {
			return scale;
		}

		public String getLabel() {
			return label;
		}
	}
	
	public static class WxReqLinkMsg extends WxReqMsg{
		String title;//消息标题
		String description;//消息描述
		String url; //消息链接
		
		public WxReqLinkMsg(XMLConfiguration xmlConfig) {
			super(xmlConfig);
			this.title = xmlConfig.getString("Title", null);
			this.description = xmlConfig.getString("Description", null);
			this.url = xmlConfig.getString("Url", null);
		}
		
		public String getContent() {
			return  title + "," + description+"," + url ;
		}
		public String getTitle() {
			return title;
		}

		public String getDescription() {
			return description;
		}

		public String getUrl() {
			return url;
		}
	}
}

