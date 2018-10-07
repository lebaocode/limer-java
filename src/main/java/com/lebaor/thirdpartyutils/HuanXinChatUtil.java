package com.lebaor.thirdpartyutils;

import java.util.HashMap;
import java.util.LinkedList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.spy.memcached.internal.OperationFuture;

import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;

public class HuanXinChatUtil {
	private static final String CLIENT_ID = "YXA6PA2VQIdkEeSvgSOkwSrFhw";
	private static final String CLIENT_SECRET = "YXA6OYLFtg3ewXQUazOqJn0Ko1y07SE";
	private static final String REQ_PATH_COMMON = "https://a1.easemob.com/lebaoapp/lebao";
	private static final String EMPTY_REQ_BODY = "{}";
	
	public static final String KEY_TOKEN_HUANXIN = "HUANXIN_ACCESS_TOKEN";
	
//	private static String accessToken = "YWMto6_HBqlPEeSiaElR4MrTWQAAAUx1PDxn3j-t6okz0wQEStR60txV8rCduls";
//	private static long tokenExpireTime = 1427867045037L;
	private static String APP_UUID = "3c0d9540-8764-11e4-af81-23a4c12ac587";
		
	public static String acquireAccessToken() {
		
		Object cachedToken = AliyunOCS.get(KEY_TOKEN_HUANXIN);
		if (cachedToken != null) {
			return (String)cachedToken;
		}
		
//		if (accessToken != null && ((System.currentTimeMillis() + 3600*1000L) < tokenExpireTime)) {
//			return accessToken;
//		}
		
		String url = REQ_PATH_COMMON + "/token";
		JSONObject param = new JSONObject();
		param.put("grant_type", "client_credentials");
		param.put("client_id", CLIENT_ID);
		param.put("client_secret", CLIENT_SECRET);
		String json = HttpClientUtil.doPost(url, param.toString(), null);
		
		JSONObject o = JSONObject.fromObject(json);
		String accessToken = o.getString("access_token");
		int expiresIn = o.getInt("expires_in");//单位秒，默认为7天
		APP_UUID = o.getString("application");
		System.out.println(APP_UUID);
//		tokenExpireTime = System.currentTimeMillis() + expiresIn*1000L;
		
		try {
			expiresIn = Math.min(expiresIn, AliyunOCS.EXPIRE_MAX);
			if (expiresIn > 7200) expiresIn = expiresIn - 3600;
			OperationFuture<Boolean> future = AliyunOCS.set(KEY_TOKEN_HUANXIN, 
					expiresIn, accessToken);
			if (future != null) future.get();
		} catch (Exception e) {
			LogUtil.WEB_LOG.debug("aliyunocs set KEY_TOKEN_HUANXIN error", e);
		}
		return accessToken;
	}
	
	/**
	 * 授权注册
	 * @param userName
	 * @param password
	 * @param nickName
	 * @return uuid
	 */
	public static String registerSingleUser(String userName, String password, String nickName) {
		String url = REQ_PATH_COMMON + "/users";
		JSONObject param = new JSONObject();
		param.put("username", userName);
		param.put("password", password);
		param.put("nickname", nickName);
		
		String json = HttpClientUtil.doPost(url, param.toString(), constructAuthHeader());
		
		JSONObject o = JSONObject.fromObject(json);
		JSONArray entities = o.getJSONArray("entities");
		if (entities.size() > 0) {
			JSONObject userObj = entities.getJSONObject(0);
			String uuid = userObj.getString("uuid");
			//type:user
			return uuid;
		}
		
		return null;
	}
	
	public static HuanXinUserInfo acquireUserInfo(String userName) {
		String url = REQ_PATH_COMMON + "/users/" + userName;
		String json = HttpClientUtil.doGet(url, constructAuthHeader());
		
		JSONObject o = JSONObject.fromObject(json);
		JSONArray entities = o.getJSONArray("entities");
		if (entities.size() > 0) {
			JSONObject userObj = entities.getJSONObject(0);
			HuanXinUserInfo user = new HuanXinUserInfo();
			user.setUuid(userObj.getString("uuid"));
			user.setUserName(userObj.getString("username"));
			if (userObj.containsKey("nickname")) {
				user.setNickName(userObj.getString("nickname"));
			}
			user.setType(userObj.getString("type"));
			return user;	
		}
		return null;
	}
	
	public static boolean isUserOnline(String userName) {
		String url = REQ_PATH_COMMON + "/users/" + userName + "/status";
		String json = HttpClientUtil.doGet(url, constructAuthHeader());
		
		JSONObject o = JSONObject.fromObject(json);
		if (o == null) return false;
		JSONObject data = o.getJSONObject("data");
		if (data == null) return false;
		String status = data.getString(userName);
		return status.equalsIgnoreCase("online");
	}
	
	public static void sendTextMessageToUser(String fromUserName, String toUserName, String msg, JSONObject extra) {
		sendTextMessageToUser(fromUserName, TARGET_TYPE_USERS, new String[]{toUserName}, msg, extra, false);
	}
	
	public static void sendTextMessageToGroup(String fromUserName, String toGroupId, String msg, JSONObject extra) {
		sendTextMessageToUser(fromUserName, TARGET_TYPE_GROUPS, new String[]{toGroupId}, msg, extra, false);
	}
	
	//未测试
//	public static void sendTransparentTextMessageToGroup(String fromUserName, String toGroupId, String msg, JSONObject extra) {
//		sendTextMessageToUser(fromUserName, TARGET_TYPE_GROUPS, new String[]{toGroupId}, msg, extra, true);
//	}
	
	private static final String TARGET_TYPE_USERS = "users";//给用户发消息
	private static final String TARGET_TYPE_GROUPS = "chatgroups";//给群 发消息
	//同一个IP地址每秒钟最多可以调用30次
	//isTransparent是否透传消息。透传消息：不会在客户端提示（铃声，震动，通知栏等），但可以在客户端监听到的消息推送。
	public static void sendTextMessageToUser(String fromUserName, String targetType, String[] targetUserNames, String msg, 
			JSONObject extra, boolean isTransparent) {
		String url = REQ_PATH_COMMON + "/messages";
		JSONObject param = new JSONObject();
		param.put("target_type", targetType);
		JSONArray targetArr = new JSONArray();
		for (String toUserName : targetUserNames) {
			targetArr.add(toUserName);
		}
		param.put("target", targetArr);
		JSONObject msgObj = new JSONObject();
		if (isTransparent) {//未测试
			msgObj.put("type", "cmd");
			msgObj.put("action", "action1");
			msgObj.put("msg", msg);
		} else {
			msgObj.put("type", "txt");
			msgObj.put("msg", msg);
		}
		param.put("msg", msgObj);
		param.put("from", fromUserName);
//		JSONObject extObj = new JSONObject();
//		extObj.put("type", "sys");
		if (extra != null) {
			param.put("ext", extra);
		}
		String json = HttpClientUtil.doPost(url, param.toString(), constructAuthHeader());
		
		JSONObject o = JSONObject.fromObject(json);
		if (o == null) return;
		JSONObject data = o.getJSONObject("data");
		if (data == null) return;
		
		LogUtil.WEB_LOG.debug(data.toString());
	}
	
	private static HashMap<String, String> constructAuthHeader() {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Authorization", "Bearer " + acquireAccessToken());
//		headers.put("Content-Type", "application/json;charset=utf-8");
		return headers;
	}
	
	/**
	 * 给userName添加一个好友friendUserName
	 * @param userName
	 * @param friendUserName
	 * @return
	 */
	public static void addFriend(String userName, String friendUserName) {
		String url = REQ_PATH_COMMON + "/users/" + userName + "/contacts/users/" + friendUserName;
		HttpClientUtil.doPost(url, EMPTY_REQ_BODY, constructAuthHeader());
	}
	
	public static void removeFriend(String userName, String friendUserName) {
		String url = REQ_PATH_COMMON + "/users/" + userName + "/contacts/users/" + friendUserName;
		HttpClientUtil.doDelete(url, constructAuthHeader());
	}
	
	/**
	 * 获取一个用户的好友列表
	 * @param userName
	 * @return 好友userName的列表
	 */
	public static LinkedList<String> acquireFriendList(String userName) {
		String url = REQ_PATH_COMMON + "/users/" + userName + "/contacts/users";
		String json = HttpClientUtil.doGet(url, constructAuthHeader());
		
		LinkedList<String> friendList = new LinkedList<String>();
		JSONObject o = JSONObject.fromObject(json);
		JSONArray data = o.getJSONArray("data");
		for (int i = 0; i < data.size(); i++) {
			friendList.add(data.getString(i));
		}
		return friendList;
	}
	
	
	public static LinkedList<HuanXinGroupInfo> acquireAllGroups() {
		String url = REQ_PATH_COMMON + "/chatgroups";
		String json = HttpClientUtil.doGet(url, constructAuthHeader());
		
		LinkedList<HuanXinGroupInfo> groupList = new LinkedList<HuanXinGroupInfo>();
		JSONObject o = JSONObject.fromObject(json);
		JSONArray data = o.getJSONArray("data");
		for (int i = 0; i < data.size(); i++) {
			JSONObject groupObj = data.getJSONObject(i);
			HuanXinGroupInfo groupInfo = new HuanXinGroupInfo();
			groupInfo.setGroupId(groupObj.getString("groupid"));
			groupInfo.setGroupName(groupObj.getString("groupname"));
			String[] arr = groupObj.getString("owner").split("_", 2);
			groupInfo.setOwnerUserName(arr[arr.length - 1]);
			groupInfo.setAffiliations(groupObj.getInt("affiliations"));
			groupList.add(groupInfo);
		}
		return groupList;
	}
	
	public static HuanXinGroupDetailInfo acquireGroupDetailInfo(String groupId) {
		LinkedList<HuanXinGroupDetailInfo> list = acquireGroupDetailInfos(new String[]{groupId});
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	//包括owner
	public static LinkedList<String> acquireGroupMembers(String groupId) {
		LinkedList<String> list = new LinkedList<String>();
		if (groupId == null) return list;
		
		HuanXinGroupDetailInfo info = acquireGroupDetailInfo(groupId);
		if (info == null) return list;
		
		String owner = info.getOwnerUserName();
		list.add(owner);
		
		for (String name: info.getMembersList()) {
			list.add(name);
		}
		return list;
	}
	
	public static boolean isGroupMember(String userName, String groupId) {
		if (userName == null || groupId == null) return false;
		
		HuanXinGroupDetailInfo info = acquireGroupDetailInfo(groupId);
		if (info == null) return false;
		
		String owner = info.getOwnerUserName();
		if (userName.equals(owner)) return true;
		
		for (String name: info.getMembersList()) {
			if (userName.equals(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static LinkedList<HuanXinGroupDetailInfo> acquireGroupDetailInfos(String[] groupIds) {
		if (groupIds == null || groupIds.length == 0) return new LinkedList<HuanXinGroupDetailInfo>();
		
		String param = "";
		for (int i = 0; i < groupIds.length; i++) {
			if (i > 0) param += ",";
			param += groupIds[i];
		}
		String url = REQ_PATH_COMMON + "/chatgroups/" + param;
		
		String json = HttpClientUtil.doGet(url, constructAuthHeader());
		
		LinkedList<HuanXinGroupDetailInfo> groupList = new LinkedList<HuanXinGroupDetailInfo>();
		JSONObject o = JSONObject.fromObject(json);
		JSONArray data = o.getJSONArray("data");
		for (int i = 0; i < data.size(); i++) {
			JSONObject groupObj = data.getJSONObject(i);
			HuanXinGroupDetailInfo groupInfo = new HuanXinGroupDetailInfo();
			groupInfo.setGroupId(groupObj.getString("id"));
			groupInfo.setGroupName(groupObj.getString("name"));
			groupInfo.setGroupDesc(groupObj.getString("description"));
			groupInfo.setPublic(groupObj.getBoolean("public"));
			groupInfo.setMembersOnly(groupObj.getBoolean("membersonly"));
			groupInfo.setAllowInvites(groupObj.getBoolean("allowinvites"));
			groupInfo.setMaxUsers(groupObj.getInt("maxusers"));
			groupInfo.setAffiliations(groupObj.getInt("affiliations_count"));
			JSONArray mems = groupObj.getJSONArray("affiliations");
			for (int j = 0; j < groupInfo.getAffiliations(); j++) {
				JSONObject memObj = mems.getJSONObject(j);
				if (memObj.containsKey("owner")) {
					groupInfo.setOwnerUserName(memObj.getString("owner"));
				} else if(memObj.containsKey("member")) {
					groupInfo.addMember(memObj.getString("member"));
				}
			}
			groupList.add(groupInfo);
		}
		return groupList;
	}
	
	/**
	 * 创建一个群组
	 * @param groupInfo
	 * @return groupId
	 */
	public static String createGroup(HuanXinGroupDetailInfo groupInfo) {
		String url = REQ_PATH_COMMON + "/chatgroups";
		JSONObject param = new JSONObject();
		param.put("groupname", groupInfo.getGroupName());
		param.put("desc", groupInfo.getGroupDesc());
		param.put("public", groupInfo.isPublic());
		param.put("maxusers", groupInfo.getMaxUsers());
		param.put("approval", groupInfo.isNeedApproval() ? false : true);
		param.put("owner", groupInfo.getOwnerUserName());
		if (groupInfo.getMembersList().size() > 0) {
			JSONArray memArr = new JSONArray();
			for (String memUserName : groupInfo.getMembersList()) {
				memArr.add(memUserName);
			}
			param.put("members", memArr);
		}
		
		String json = HttpClientUtil.doPost(url, param.toString(), constructAuthHeader());
		JSONObject o = JSONObject.fromObject(json);
		return o.getJSONObject("data").getString("groupid");
	}
	
	public static boolean addUserToGroup(String userName, String groupId) {
		String url = REQ_PATH_COMMON + "/chatgroups/" + groupId + "/users/" + userName;
		String json = HttpClientUtil.doPost(url, EMPTY_REQ_BODY, constructAuthHeader());
		JSONObject o = JSONObject.fromObject(json);
		return o.getJSONObject("data").getBoolean("result");
	}
	
	public static boolean removeUserFromGroup(String userName, String groupId) {
		String url = REQ_PATH_COMMON + "/chatgroups/" + groupId + "/users/" + userName;
		String json = HttpClientUtil.doDelete(url, constructAuthHeader());
		JSONObject o = JSONObject.fromObject(json);
		return o.getJSONObject("data").getBoolean("result");
	}
	
	/**
	 * 得到一个用户参与的所有群组信息
	 * @param userName
	 * @return 返回信息里只有id和name
	 */
	public static LinkedList<HuanXinGroupInfo> acquireUserGroups(String userName) {
		String url = REQ_PATH_COMMON + "/users/" + userName + "/joined_chatgroups";
		String json = HttpClientUtil.doGet(url, constructAuthHeader());
		JSONObject o = JSONObject.fromObject(json);
		JSONArray data = o.getJSONArray("data");
		LinkedList<HuanXinGroupInfo> groupList = new LinkedList<HuanXinGroupInfo>();
		for (int i = 0; i < data.size(); i++) {
			JSONObject groupObj = data.getJSONObject(i);
			HuanXinGroupInfo groupInfo = new HuanXinGroupInfo();
			groupInfo.setGroupId(groupObj.getString("groupid"));
			groupInfo.setGroupName(groupObj.getString("groupname"));
			groupList.add(groupInfo);
		}
		return groupList;
	}
	
	public static class HuanXinUserInfo {
		String uuid;
		String userName;
		String nickName;
		String type;//user
		public String toString() {
			return "uuid=" + uuid + ", userName=" + userName + ", nickName=" + nickName + ", type=" + type;
		}
		
		public String getUuid() {
			return uuid;
		}
		public void setUuid(String uuid) {
			this.uuid = uuid;
		}
		public String getUserName() {
			return userName;
		}
		public void setUserName(String userName) {
			this.userName = userName;
		}
		public String getNickName() {
			return nickName;
		}
		public void setNickName(String nickName) {
			this.nickName = nickName;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
	}
	
	public static class HuanXinGroupInfo {
		String groupId;
		String groupName;
		String ownerUserName;
		int affiliations;//群内的所有人数，包括群主在内
		public String toString() {
			return "groupId=" + groupId + ", groupName=" + groupName + ", ownerUserName=" + ownerUserName + ", groupUserNum=" + affiliations;
		}
		
		public String getGroupId() {
			return groupId;
		}
		public void setGroupId(String groupId) {
			this.groupId = groupId;
		}
		public String getGroupName() {
			return groupName;
		}
		public void setGroupName(String groupName) {
			this.groupName = groupName;
		}
		public String getOwnerUserName() {
			return ownerUserName;
		}
		public void setOwnerUserName(String ownerUserName) {
			this.ownerUserName = ownerUserName;
		}
		public int getAffiliations() {
			return affiliations;
		}
		public void setAffiliations(int affiliations) {
			this.affiliations = affiliations;
		}
		
	}
	public static class HuanXinGroupDetailInfo extends HuanXinGroupInfo {
		String groupDesc;
		boolean isPublic = true;//是否公开群，默认公开。公开指可被搜索到，可申请加入
		boolean isMembersOnly = true;//是否需要申请和验证才能加入。我们默认需要验证
		boolean allowInvites = false;//是否允许邀请。我们默认不允许
		boolean needApproval = true;//加入公开群是否需要批准，环信默认不需要批准，但我们需要
		int maxUsers = 200;//默认200
		LinkedList<String> membersList = new LinkedList<String>();//不包括owner
		public String toString() {
			String s = super.toString();
			return s+", groupDesc=" + groupDesc + ", isPublic=" + isPublic + ", isMembersOnly=" + isMembersOnly
					+ ", allowInvites=" + allowInvites + ", needApproval=" + needApproval + ", maxUsers=" + maxUsers
					+ ", membersList=" + membersList;
		}
		
		public String getGroupDesc() {
			return groupDesc;
		}
		public void setGroupDesc(String groupDesc) {
			this.groupDesc = groupDesc;
		}
		public boolean isPublic() {
			return isPublic;
		}
		public void setPublic(boolean isPublic) {
			this.isPublic = isPublic;
		}
		public boolean isMembersOnly() {
			return isMembersOnly;
		}
		public void setMembersOnly(boolean isMembersOnly) {
			this.isMembersOnly = isMembersOnly;
		}
		public boolean isAllowInvites() {
			return allowInvites;
		}
		public void setAllowInvites(boolean allowInvites) {
			this.allowInvites = allowInvites;
		}
		
		public boolean isNeedApproval() {
			return needApproval;
		}
		public void setNeedApproval(boolean needApproval) {
			this.needApproval = needApproval;
		}
		public int getMaxUsers() {
			return maxUsers;
		}
		public void setMaxUsers(int maxUsers) {
			this.maxUsers = maxUsers;
		}
		public LinkedList<String> getMembersList() {
			return membersList;
		}
		public void addMember(String memberUserName) {
			this.membersList.add(memberUserName);
		}
		
	}
	
	public static void main(String[] args) {
		AliyunOCS ocs = new AliyunOCS();
		ocs.connect();
		try {
//		System.out.println(HuanXinChatUtil.acquireAccessToken());
		//测试注册用户
//		String userName = "test_user7";
//		String password = "test";
//		String nickName = "测试用户7";
//		String uuid = HuanXinChatUtil.registerSingleUser(userName, password, nickName);
//		System.out.println("registerSingleUser success, uuid="+ uuid);
		

//		String userName = "test_user2";
//		String password = "test";
//		String nickName = "测试用户2";
//		String uuid = HuanXinChatUtil.registerSingleUser(userName, password, nickName);
//		System.out.println("registerSingleUser success, uuid="+ uuid);
		//uuid=e041570a-a96f-11e4-92e3-0b576bf76bc5
		
//		String userName = "test_user4";
//		String password = "test";
//		String nickName = "测试用户4";
//		String uuid = HuanXinChatUtil.registerSingleUser(userName, password, nickName);
//		System.out.println("registerSingleUser success, uuid="+ uuid);
		//uuid=fbab298a-a96f-11e4-9275-2313854c6db5 (test_user3)
		//uuid=a3e5cd1a-a9c7-11e4-9503-fd25a0fc534b (test_user4)
		
		
		//测试创建群
//		HuanXinGroupDetailInfo groupInfo = new HuanXinGroupDetailInfo();
//		groupInfo.setAffiliations(1);
//		groupInfo.setAllowInvites(false);
//		groupInfo.setGroupDesc("小区1");
//		groupInfo.setGroupName("小区1");
//		groupInfo.setMembersOnly(false);
//		groupInfo.setNeedApproval(true);
//		groupInfo.setOwnerUserName("test_user1");
//		groupInfo.setPublic(true);
//		System.out.println("createGroup success, groupid="+ HuanXinChatUtil.createGroup(groupInfo));
		//groupid=1422725136889165;
		
//		LinkedList<HuanXinGroupInfo> allGroupList = HuanXinChatUtil.acquireAllGroups();
//		System.out.println("acquireAllGroups success, groupList="+ allGroupList);
		
//		HuanXinChatUtil.addFriend("test_user1", "test_user2");
//		HuanXinChatUtil.addFriend("test_user1", "test_user3");
//		LinkedList<String> friendList = HuanXinChatUtil.acquireFriendList("test_user1");
//		System.out.println("acquireFriendList success, friendList="+ friendList);
		
		
//		HuanXinGroupDetailInfo groupInfo = HuanXinChatUtil.acquireGroupDetailInfo("1422725136889165");
//		HuanXinGroupDetailInfo groupInfo = HuanXinChatUtil.acquireGroupDetailInfo("142270255186623");
//		System.out.println("acquireGroupDetailInfo success, friendList="+ groupInfo);
		
//		LinkedList<HuanXinGroupInfo> groupList = HuanXinChatUtil.acquireUserGroups("test_user1");
//		System.out.println("acquireUserGroups success, groupList="+ groupList);
	
//		HuanXinUserInfo user = HuanXinChatUtil.acquireUserInfo("test_user1");
//		System.out.println("acquireUserInfo success, user="+ user);
		//user=uuid=011e7efa-a96e-11e4-91bc-cf2a53d443ec, userName=test_user1, nickName=测试用户1, type=user
		
//		boolean result = HuanXinChatUtil.addUserToGroup("test_user2", "1422725136889165");
//		System.out.println("addUserToGroup " + result);
		//true
	
//		boolean result = HuanXinChatUtil.isUserOnline("test_user1");
//		System.out.println("isUserOnline " + result);
	
//		boolean result = HuanXinChatUtil.removeUserFromGroup("test_user2", "1422725136889165");
//		System.out.println("removeUserFromGroup " + result);
		
//		JSONObject extra = new JSONObject();
//		extra.put("from_nickname", "浩博家长");
//		extra.put("from_userid", "lebao_30");
//		extra.put("from_userlogo", "'http://7u2pjw.com1.z0.glb.clouddn.com/userlogo/2c2064a3bdeaa3c6a533efdc1cb52b17.jpg");
//		extra.put("to", "1422776177429459");
//		extra.put("type", CloudData.CLOUD_TYPE_GROUP);
//		extra.put("msg_id", "1423724415843_30");
//		extra.put("send_time", "1423724415843");
//		HuanXinChatUtil.sendTransparentTextMessageToGroup("lebao_30", "1422776177429459", "啊", extra);
//		System.out.println("sendTextMessageToUser");
		
//		HuanXinChatUtil.removeFriend("test_user1", "test_user2");
//		System.out.println("removeFriend");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ocs.release();
		}
	}
	
	
}
