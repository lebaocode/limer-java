package com.lebaor.wx.data;

import java.io.StringWriter;
import java.util.LinkedList;

import org.apache.commons.configuration.XMLConfiguration;

import com.lebaor.utils.LogUtil;

public abstract class WxRespMsg {
	public static final String TYPE_TEXT = "text";//请求 & 回复
	public static final String TYPE_IMAGE = "image";//请求 & 回复
	public static final String TYPE_VOICE = "voice";//请求 & 回复
	public static final String TYPE_VIDEO = "video";//请求 & 回复
	public static final String TYPE_MUSIC = "music";//回复
	public static final String TYPE_NEWS = "news";//回复
	

	String toUserName;//开发者微信号
	String fromUserName;//发送方帐号（一个OpenID）
	long createTime;//消息创建时间 （整型）
	String msgType;//text/
	
	public WxRespMsg(String toUserName, String fromUserName) {
		this.toUserName = toUserName;
		this.fromUserName = fromUserName;
		this.createTime = System.currentTimeMillis();
				
	}
	
	protected String cdata(String s) {
		return "<![CDATA[" + s +  "]]>";
		//return s;
	}
	
	public String toXml() {
		try {
			XMLConfiguration xmlConfig = new XMLConfiguration();
			xmlConfig.setRootElementName("xml");
			xmlConfig.setDelimiterParsingDisabled(true);
			xmlConfig.setProperty("ToUserName",  cdata(this.toUserName));
			xmlConfig.setProperty("FromUserName", cdata(this.fromUserName));
			xmlConfig.setProperty("CreateTime", (int)(this.createTime/1000));
			xmlConfig.setProperty("MsgType", cdata(this.msgType));
			saveProperties(xmlConfig);
			
			StringWriter sw = new StringWriter();
			xmlConfig.save(sw);
			return sw.toString();
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("toXml error: ", e);
			return null;
		}
	}
	
	public String toString() {
		return toXml();
	}
	
	public void setCreateTime(long time) {
		this.createTime = time;
	}
	
	public abstract void saveProperties(XMLConfiguration xmlConfig);
	
	public static class WxRespTextMsg extends WxRespMsg {
		String content;
		public WxRespTextMsg(String toUserName, String fromUserName, String content) {
			super(toUserName, fromUserName);
			this.content = content;
			this.msgType = TYPE_TEXT;
					
		}
		public void saveProperties(XMLConfiguration xmlConfig) {
			xmlConfig.setProperty("Content", cdata(this.content));
		}
	}
	public static class WxRespImageMsg extends WxRespMsg {
		String mediaId;
		public WxRespImageMsg(String toUserName, String fromUserName, String mediaId) {
			super(toUserName, fromUserName);
			this.mediaId = mediaId;
			this.msgType = TYPE_IMAGE;
					
		}
		public void saveProperties(XMLConfiguration xmlConfig) {
			xmlConfig.setProperty("Image.MediaId", cdata(this.mediaId));
		}
	}
	public static class WxRespVoiceMsg extends WxRespMsg {
		String mediaId;
		public WxRespVoiceMsg(String toUserName, String fromUserName, String mediaId) {
			super(toUserName, fromUserName);
			this.mediaId = mediaId;
			this.msgType = TYPE_VOICE;
					
		}
		public void saveProperties(XMLConfiguration xmlConfig) {
			xmlConfig.setProperty("Voice.MediaId", cdata(this.mediaId));
		}
	}
	public static class WxRespVideoMsg extends WxRespMsg {
		String mediaId;
		String title;
		String description;
		public WxRespVideoMsg(String toUserName, String fromUserName, String mediaId, String title, String description) {
			super(toUserName, fromUserName);
			this.mediaId = mediaId;
			this.title = title;
			this.description = description;
			this.msgType = TYPE_VIDEO;
					
		}
		public void saveProperties(XMLConfiguration xmlConfig) {
			xmlConfig.setProperty("Video.MediaId", cdata(this.mediaId));
			xmlConfig.setProperty("Video.Title", cdata(this.title));
			xmlConfig.setProperty("Video.Description", cdata(this.description));
		}
	}
	public static class WxRespMusicMsg extends WxRespMsg {
		String musicUrl;
		String hqMusicUrl;
		String thumbMediaId;
		String title;
		String description;
		public WxRespMusicMsg(String toUserName, String fromUserName, String thumbMediaId, 
				String title, String description,
				String musicUrl, String hqMusicUrl) {
			super(toUserName, fromUserName);
			this.thumbMediaId = thumbMediaId;
			this.musicUrl = musicUrl;
			this.hqMusicUrl = hqMusicUrl;
			this.title = title;
			this.description = description;
			this.msgType = TYPE_MUSIC;
					
		}
		public void saveProperties(XMLConfiguration xmlConfig) {
			xmlConfig.setProperty("Music.MusicURL", cdata(this.musicUrl));
			xmlConfig.setProperty("Music.HQMusicUrl", cdata(this.hqMusicUrl));
			xmlConfig.setProperty("Music.ThumbMediaId", cdata(this.thumbMediaId));
			xmlConfig.setProperty("Music.Title", cdata(this.title));
			xmlConfig.setProperty("Music.Description", cdata(this.description));
			
		}
	}
	public static class WxRespNewsMsg extends WxRespMsg {
		LinkedList<WxRespNewsArticle> articles = new LinkedList<WxRespNewsArticle>(); 
		
		public WxRespNewsMsg(String toUserName, String fromUserName, WxRespNewsArticle[] articles) {
			super(toUserName, fromUserName);
			this.articles.clear();
			if (articles != null) {
				for (WxRespNewsArticle a : articles) {
					this.articles.add(a);
				}
			}
			this.msgType = TYPE_NEWS;
					
		}
		public void saveProperties(XMLConfiguration xmlConfig) {
			xmlConfig.setProperty("ArticleCount", this.articles.size());
			
			int i = 0;
			for (WxRespNewsArticle a : this.articles) {
				xmlConfig.setProperty("Articles.item("+ i +").Title", cdata(a.getTitle()));
				xmlConfig.setProperty("Articles.item("+ i +").Description", cdata(a.getDescription()));
				xmlConfig.setProperty("Articles.item("+ i +").PicUrl", cdata(a.getPicUrl()));
				xmlConfig.setProperty("Articles.item("+ i +").Url", cdata(a.getUrl()));
				i++;
			}
		}
		public WxRespNewsArticle[] getArticles() {
			return articles.toArray(null);
		}
		
	}
	
	public static class WxRespNewsArticle {
		String title;
		String description;
		String picUrl;
		String url;
		public WxRespNewsArticle(String title, String description,
				String picUrl, String url) {
			super();
			this.title = title;
			this.description = description;
			this.picUrl = picUrl;
			this.url = url;
		}
		public String getTitle() {
			return title;
		}
		public String getDescription() {
			return description;
		}
		public String getPicUrl() {
			return picUrl;
		}
		public String getUrl() {
			return url;
		}
		
	}
}
