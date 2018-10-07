package com.lebaor.utils;

import org.apache.log4j.Logger;

public class LogUtil {

	public static final Logger WEB_LOG = Logger.getLogger("lebaoweb");
	public static final Logger STAT_LOG = Logger.getLogger("lebaostat");
	public static final Logger VAQ_LOG = Logger.getLogger("lebaovaquero");
	public static final Logger ACTION_LOG = Logger.getLogger("lebaoaction");
	public static final String logPrefix = "@@ANALYSIS@@ ";
	
	public static void logAction(String pageDef, String deviceToken, String curUrl, String extInfo) {
		LogUtil.ACTION_LOG.info("["+ pageDef +"] ["+ deviceToken +"] ["+ curUrl +"] ["+ extInfo +"]");
	}
	
	public static void logPointOp(long userId, int point) {
		LogUtil.ACTION_LOG.info("[POINT_OP] ["+ userId +"] ["+ point +"]");
	}
	
	public static void info(String s) {
		LogUtil.WEB_LOG.info(s);
	}
}
