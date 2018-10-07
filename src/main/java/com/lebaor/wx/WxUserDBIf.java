package com.lebaor.wx;

import com.lebaor.wx.data.WxUserInfo;


public interface WxUserDBIf {
	public WxUserInfo getUserByOpenId(String openId);
	public boolean addUser(WxUserInfo user);
	public boolean updateUser(WxUserInfo user);
	
	public boolean updateUserUnSub(WxUserInfo user);
}
