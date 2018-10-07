package com.lebaor.wx.msg;

import com.lebaor.utils.LogUtil;
import com.lebaor.wx.WxUserDBIf;
import com.lebaor.wx.WxUserInfoUtil;
import com.lebaor.wx.data.WxEventMsg;
import com.lebaor.wx.data.WxEventMsg.WxScanEventMsg;
import com.lebaor.wx.data.WxEventMsg.WxSubEventMsg;
import com.lebaor.wx.data.WxEventMsg.WxUnSubEventMsg;
import com.lebaor.wx.data.WxReqMsg;
import com.lebaor.wx.data.WxRespMsg;
import com.lebaor.wx.data.WxRespMsg.WxRespTextMsg;
import com.lebaor.wx.data.WxUserInfo;

public class WxSubEventMsgHandler implements WxMsgHandler{
	WxUserDBIf db;
	
	public WxRespMsg handleReqMsg(WxReqMsg reqMsg){
		if (reqMsg == null) return null;
		if (!(reqMsg instanceof WxEventMsg)) return null;
		
		WxEventMsg eventMsg = (WxEventMsg)reqMsg;
		String openId = eventMsg.getFromUserName();
		
		if (eventMsg instanceof WxUnSubEventMsg) {
			LogUtil.STAT_LOG.info("[USER_UNSUB] [] [" + openId + "]");
			return handleUnsub(eventMsg);
		}
		
		String source = null;
		if (eventMsg instanceof WxSubEventMsg){
			source = "DEFAULT"; 
		}else if ( (eventMsg instanceof WxScanEventMsg)) {
			source = "SCAN";
		}
		
		if (source == null) return null;//不是订阅消息
		
		LogUtil.STAT_LOG.info("[NEW_USER_FOLLOWED] ["+ source +"] [" + openId + "]");
		
		WxUserInfo user = db.getUserByOpenId(openId);
		if (user == null) {
			user =  WxUserInfoUtil.getUserInfoByOpenId(openId);
			
			boolean result = db.addUser(user);
			if (result) {
				LogUtil.STAT_LOG.info("[NEW_USER_ADD_DB] [SUUCESS] ["+ source +"] [" + openId + "]");
			} else {
				LogUtil.STAT_LOG.info("[NEW_USER_ADD_DB] [FAILED] ["+ source +"] [" + openId + "]");
				LogUtil.WEB_LOG.warn("[NEW_USER_ADD_DB] [FAILED] ["+ source +"] [" + openId + "]");
			}
		} else {
			//不为空则更新一下信息
			user =  WxUserInfoUtil.getUserInfoByOpenId(openId);
//			user = new LebaoUser(user.getId(), user.getMobile(), wxUserInfo);
			
			boolean result = db.updateUser(user);
			if (result) {
				LogUtil.STAT_LOG.info("[OLD_USER_ADD_DB] [SUUCESS] ["+ source +"] [" + openId + "]");
			} else {
				LogUtil.STAT_LOG.info("[OLD_USER_ADD_DB] [FAILED] ["+ source +"] [" + openId + "]");
				LogUtil.WEB_LOG.warn("[OLD_USER_ADD_DB] [FAILED] ["+ source +"] [" + openId + "]");
			}
		}
		
		
		String content =  "欢迎" + user.explainSex(user.getSex()) + user.getNickName() + "加入~";
		WxRespTextMsg resp = new WxRespTextMsg(eventMsg.getFromUserName(), eventMsg.getToUserName(), content);
		return resp;
	}
	
	public WxRespMsg handleUnsub(WxEventMsg eventMsg){
		//将用户的订阅属性改为退订
		String openId = eventMsg.getFromUserName();
		WxUserInfo user = db.getUserByOpenId(openId);
		if (user == null) return null;
		
		user.setSubscribe(WxUserInfo.SUB_NO);
		boolean result = db.updateUserUnSub(user);
		if (result) {
			LogUtil.STAT_LOG.info("[USER_UNSUB_DB] [SUUCESS] [] [" + openId + "]");
		} else {
			LogUtil.STAT_LOG.info("[USER_UNSUB_DB] [FAILED] [] [" + openId + "]");
			LogUtil.WEB_LOG.warn("[USER_UNSUB_DB] [FAILED] [] [" + openId + "]");
		}
		return null;
	}

	public WxUserDBIf getDb() {
		return db;
	}

	public void setDb(WxUserDBIf db) {
		this.db = db;
	}

}
