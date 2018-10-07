package com.lebaor.wx.data;

import java.util.LinkedList;

import com.lebaor.utils.TextUtil;

/**
 * 微信菜单
 * @author Administrator
 *
 */
public class WxMenu {
	public static final String TYPE_CLICK = "click";
	public static final String TYPE_VIEW = "view";
	public static final String PARAM_BUTTON = "button";
	public static final String PARAM_SUB_BUTTON = "sub_button";
	public static final String PARAM_TYPE = "type";
	public static final String PARAM_NAME = "name";
	public static final String PARAM_KEY = "key";
	public static final String PARAM_URL = "url";
	
	boolean isClickType = true;//菜单的响应动作类型，目前有click、view两种类型
	String name = null;//菜单标题，不超过16个字节，子菜单不超过40个字节
	String key = null;//菜单KEY值，用于消息接口推送，不超过128字节; click类型必须
	String url = null; //网页链接，用户点击菜单可打开链接，不超过256字节; view类型必须
	LinkedList<WxMenu> subMenus = new LinkedList<WxMenu>();//每个一级菜单最多包含5个二级菜单
	
	public WxMenu(boolean isClickType, String name, String value) {
		this.isClickType = isClickType;
		this.name = name;
		if (this.isClickType) {
			this.key = value;
		} else {
			this.url = value;
		}
		this.subMenus.clear();
	}
	
	public WxMenu(String name, WxMenu[] subMenus) {
		this.name = name;
		this.key = null;
		this.url = null;
		isClickType = true;
		this.subMenus.clear();
		
		if (subMenus != null) {
			for (WxMenu m : subMenus) {
				this.subMenus.add(m);		
			}
		}
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder(); 
		if (subMenus.size() == 0) {
			sb.append("{\n");
			sb.append("\"" + PARAM_TYPE + "\":\"" + (isClickType ? TYPE_CLICK : TYPE_VIEW)+ "\", \n");
			sb.append("\"" + PARAM_NAME + "\":\"" + TextUtil.filterNull(name)+ "\", \n");
			if (isClickType) {
				sb.append("\"" + PARAM_KEY + "\":\"" + TextUtil.filterNull(key)+ "\"\n");
			} else {
				sb.append("\"" + PARAM_URL + "\":\"" + TextUtil.filterNull(url)+ "\"\n");
			}
			
			sb.append("}");
		} else {
			sb.append("{\n\"" + PARAM_NAME + "\":\"" + TextUtil.filterNull(name)+ "\", \n");
			sb.append("\"" + PARAM_SUB_BUTTON + "\":[\n");
			int i = 0;
			for (WxMenu m : subMenus) {
				if (i > 0) sb.append(", \n");
				sb.append(m.toString());
				i++;
			}
			sb.append("]}\n");
		}
		return sb.toString();
	}
	
	public static String toString(WxMenu[] menus) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{\n");
		sb.append("\"" + PARAM_BUTTON + "\":[\n");
		int i = 0;
		for (WxMenu m : menus) {
			if (i > 0) sb.append(", \n");
			sb.append(m.toString());
			i++;
		}
		
		sb.append("]\n");
		sb.append("}");
		
		return sb.toString();
	}
	
	public boolean isSubMenu() {
		return false;
	}
	public boolean isParentMenu() {
		return true;
	}
	
	public void addSubMenu(WxMenu subMenu) {
		subMenus.add(subMenu);
	}
	
	public void deleteSubMenu(String name) {
		int index = 0;
		for (WxMenu m : subMenus) {
			if (TextUtil.filterNull(m.getName()).equals(name)) {
				subMenus.remove(index);
				break;
			}
			index ++;
		}
	}
	
	public void deleteAllSubMenus() {
		subMenus.clear();
	}
	
	public WxMenu[] getSubMenus() {
		return subMenus.toArray(null);
	}
	
	public boolean isClickType() {
		return isClickType;
	}

	public void setClickType(boolean isClickType) {
		this.isClickType = isClickType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

