package com.lebaor.wx;

import com.lebaor.utils.TextUtil;

public class WxConstants {
	public static String WX_ID = "lebao_qinzi";
	public static String WX_APPID = "wxd31b99e4d2f44791";
	public static String WX_APPSECRET = "98617129eaeb1d8fc14365872e3e6c1b";
	public static String WX_MCHID = "1430694502";
	public static String WX_MCHSECRET = "1709774a1d3c5172e3e6c1b0374cb954";
	public static String WX_TOKEN = "tongnian";
//	public static long WX_TIMESTAMP = 1484909088L;
	public static String WX_NONCESTR = "1939430029";
	public static String WX_MCH_ID = "1430694502";
	public static String SERVER_IP = "222.128.156.59";//local home 
	//encodingAESKey:1GjIjHeANRV5FSScj4lRs9HTDdeDVGOswvdBVepuh54
//	public static String SERVER_IP = "120.77.250.200";//www.limer.com.cn
	
	public static String getWxSign(String token, String timestamp, String nonce) {
		String[] str = { token, timestamp, nonce };
        java.util.Arrays.sort(str); // 字典序排序
        String bigStr = str[0] + str[1] + str[2];
        // SHA1加密
		String digest = TextUtil.SHA1(bigStr);
		return digest;
	}
}
