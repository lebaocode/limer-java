package com.lebaor.wx.data;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;

import net.sf.json.JSONObject;

/**
 * 微信用户信息
 * @author Administrator
 *
 */
public class WxUserInfo {
	public static final int SEX_FEMALE = 2;
	public static final int SEX_MALE = 1;
	public static final int SEX_UNKNOWN = 0;
	
	public static final int SUB_YES = 1;//订阅公众号
	public static final int SUB_NO = 2;//未订阅公众号
	public static final int SUB_UNKNOWN = 0;//未知
	
	public static final String LANG_SIMPLE = "zh_CN";
	public static final String LANG_TRADITIONAL = "zh_TW";
	public static final String LANG_EN = "en";
	
	/**
	 * 用户是否订阅该公众号标识，值为0时，代表此用户没有关注该公众号，拉取不到其余信息。
	 */
	int subscribe = SUB_UNKNOWN;
	String openId;//用户的标识，对当前公众号唯一
	String nickName;//用户的昵称
	int sex = SEX_UNKNOWN;
	String city; //广州
	String country; //中国
	String province; //广东
	String lang = LANG_SIMPLE;
	
	/**
	 * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，
	 * 0代表640*640正方形头像），用户没有头像时该项为空
	 * 如：http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0
	 */
	String headImgUrl;
	
	/**
	 * 用户关注时间，为时间戳。如果用户曾多次关注，则取最后关注时间
	 * 如：1382694957
	 */
	long subscribeTime;
	
	public WxUserInfo(){}
	
	public WxUserInfo(String json) {
		try {
			JSONObject obj = JSONObject.fromObject(json);
			this.subscribe = obj.getInt("subscribe");
			this.openId = obj.getString("openid");
			this.nickName = obj.getString("nickname");
			this.sex = obj.getInt("sex");
			this.city = obj.getString("city");
			this.country = obj.getString("country");
			this.province = obj.getString("province");
			this.lang = obj.getString("language");
			this.headImgUrl = obj.getString("headimgurl");
			this.subscribeTime = obj.getInt("subscribe_time") * 1000L;
			
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("UserInfo json parse eror: " + json, e);
		}
	}
	
	private void putIntoJson(JSONObject o, String key, Object value) {
		if (value != null) {
			o.put(key, value);
		}
	}
	
	public String toJson() {
		JSONObject o = new JSONObject();
		putIntoJson(o, "openid", this.openId);
		putIntoJson(o, "nickname", this.nickName);
		putIntoJson(o, "sex", this.sex);
		putIntoJson(o, "city", this.city);
		putIntoJson(o, "country", this.country);
		putIntoJson(o, "province", this.province);
		putIntoJson(o, "language", this.lang);
		putIntoJson(o, "headimgurl", this.headImgUrl);
		putIntoJson(o, "subscribe", this.subscribe);
		putIntoJson(o, "subscribe_time", (int)(this.subscribeTime/1000));
		return o.toString();
	}
	
	public String explainSex(int sex) {
		if (sex == SEX_FEMALE) return "妹纸";
		else if (sex == SEX_MALE) return "帅哥";
		else return "火星人";
	}
	
	public String toString() {
		return this.subscribe + "," + this.nickName + "," + explainSex(this.sex)
				+ "," + this.city + "," + this.country + "," + this.province 
				+ "," + TextUtil.formatTime(this.subscribeTime);
				
	}

	public int getSubscribe() {
		return subscribe;
	}

	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public long getSubscribeTime() {
		return subscribeTime;
	}

	public void setSubscribeTime(long subscribeTime) {
		this.subscribeTime = subscribeTime;
	}
	
	
}
