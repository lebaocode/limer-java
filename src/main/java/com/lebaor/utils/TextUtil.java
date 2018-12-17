package com.lebaor.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.lebaor.webutils.ViewUtils;


public class TextUtil {
	protected static TextUtil singleton;
	static {
		 singleton = new TextUtil();
	}
	protected TextUtil() {
    	
    }
    public static TextUtil getInstance() {
        return singleton;
    }
	
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
		 '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	
	public static String getAbbr(String content) {
		return getAbbr(content, 50);
	}
	
	public static String getAbbr(String content, int num) {
		if (content == null || content.trim().length() == 0) return "";
		String s = content.trim();
		if (s.length() > num) {
			return s.substring(0, num) + "...";
		} else {
			return s;
		}
	}
	
	public static String filterNull(String s) {
        if (s == null) return "";
        return s;
    }
	
	public static boolean isEmpty(String s) {
		return s == null || s.trim().equals("");
	}
	
	public static String SHA1(String inStr) {
        MessageDigest md = null;
        String outStr = null;
        try {
        	
            md = MessageDigest.getInstance("SHA-1");
            md.update(inStr.getBytes());
            byte[] digest = md.digest();
            outStr = getFormattedText(digest);
        }
        catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return outStr;
    }
	
	public static String MD5(String inStr) {
        MessageDigest md = null;
        String outStr = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(inStr.getBytes());
            byte[] digest = md.digest();
            outStr = getFormattedText(digest);
        }
        catch (NoSuchAlgorithmException nsae) {
            nsae.printStackTrace();
        }
        return outStr;
    }
	
	public static String base64Encode(byte[] b) {
		try {
			return new String(Base64.encodeBase64(b));
		} catch (Exception e) {
			return null;
		}
	}
	public static String base64Encode(String s) {
		try {
			return new String(Base64.encodeBase64(s.getBytes()));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String trimChnSpace(String s) {
		if (s == null) return null;
		
		int i = 0;
		while (i < s.length()) {
			int ch = s.charAt(i);
			if (ch == '　' || ch == '\t' || ch == ' ' || ch == '\r' || ch =='\n') {
				i++;
				continue;
			} else {
				break;
			}
		}
		int start = i;
		if (start == s.length()) return "";
		
		i = s.length() - 1;
		while (i > start) {
			int ch = s.charAt(i);
			if (ch == '　' || ch == '\t' || ch == ' ' || ch == '\r' || ch =='\n') {
				i--;
				continue;
			} else {
				break;
			}
		}
		int end = i + 1;
		return s.substring(start, end);
	}
	
	private static String getFormattedText(byte[] bytes) {
		int len = bytes.length;
		StringBuilder buf = new StringBuilder(len * 2);
		// 把密文转换成十六进制的字符串形式
		for (int j = 0; j < len; j++) {          
			buf.append(HEX_DIGITS[(bytes[j] >> 4) & 0x0f]);
			buf.append(HEX_DIGITS[bytes[j] & 0x0f]);
		}
		return buf.toString();
	}

	public static String formatTime(long time) {
		return String.format("%1$tF %1$tT", time);
	}
	public static String formatTime2(long time) {
		return String.format("%1$tY%1$tm%1$td%1$tH%1$tM%1$tS", time);
	}
	public static String formatTime3(long time) {
		return String.format("%1$tY-%1$tm-%1$td %1$tH:%1$tM", time);
	}
	
	//20130904
	public static String formatDay(long time) {
		return String.format("%1$tY%1$tm%1$td", time);
	}
	
	public static String formatDay2(long time) {
		return String.format("%1$tY-%1$tm-%1$td", time);
	}
	
    public static String getHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    public static byte[] hmacSHA1Bytes(String msg, String secretKey) throws Exception {
    	SecretKey secretKeyObj = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(secretKeyObj);
        return hmac.doFinal(msg.getBytes());
    }
    
    public static String hmacSHA1(String msg, String secretKey) throws Exception {
        return hmacSHA1(msg, secretKey, false);
    }
    
    public static String hmacSHA1(String msg, String secretKey, boolean rawOutput) throws Exception {
        SecretKey secretKeyObj = new SecretKeySpec(secretKey.getBytes(), "HmacSHA1");
        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(secretKeyObj);
        if (rawOutput) {
        	return new String(hmac.doFinal(msg.getBytes()));
        } else {
        	return getHexString(hmac.doFinal(msg.getBytes()));
        }
    }
    
    public static String hmacSHA256(String message, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(message.getBytes());
            hash = getHexString(bytes);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========" + e.getMessage());
        }
        return hash;
    }
    
    public static int getMinusDayOfTwoDate(String d1, String d2) {
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, Integer.parseInt(d1.substring(0,4)));	
		cal.set(Calendar.MONTH, Integer.parseInt(d1.substring(4,6)) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d1.substring(6,8)));
		cal.set(Calendar.HOUR_OF_DAY, 0); //一定要大于4点
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long t1 = cal.getTimeInMillis();
		
		cal.set(Calendar.YEAR, Integer.parseInt(d2.substring(0,4)));	
		cal.set(Calendar.MONTH, Integer.parseInt(d2.substring(4,6)) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(d2.substring(6,8)));
		cal.set(Calendar.HOUR_OF_DAY, 0); //一定要大于4点
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		long t2 = cal.getTimeInMillis();
		
		return Math.abs((int)((t1 - t2)/(24 * 60 * 60 * 1000)));
	}
    
    public static long getTime(String day) {
    	return parseTime(day, "yyyymmdd");
    }
    
    public static long parseTime(String s, String format) {
    	if (format.equalsIgnoreCase("yyyymmddHHMMSS")) {
    		Calendar cal = Calendar.getInstance();
    		cal.set(Calendar.YEAR, Integer.parseInt(s.substring(0,4)));	
    		cal.set(Calendar.MONTH, Integer.parseInt(s.substring(4,6)) - 1);
    		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(6,8)));
    		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(8,10)));
    		cal.set(Calendar.MINUTE, Integer.parseInt(s.substring(10, 12)));
    		cal.set(Calendar.SECOND, Integer.parseInt(s.substring(12,14)));
    		return cal.getTimeInMillis();
    	} else if (format.equalsIgnoreCase("yyyymmdd")) {
    		Calendar cal = Calendar.getInstance();
    		
    		cal.set(Calendar.YEAR, Integer.parseInt(s.substring(0,4)));	
    		cal.set(Calendar.MONTH, Integer.parseInt(s.substring(4,6)) - 1);
    		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(6,8)));
    		cal.set(Calendar.HOUR_OF_DAY, 0);
    		cal.set(Calendar.MINUTE, 0);
    		cal.set(Calendar.SECOND, 1);
    		return cal.getTimeInMillis();
    	} else if (format.equalsIgnoreCase("yyyy-mm-dd HH：MM：SS") || format.equalsIgnoreCase("yyyy-mm-dd HH:MM:SS")) {
    		Calendar cal = Calendar.getInstance();
    		cal.set(Calendar.YEAR, Integer.parseInt(s.substring(0,4)));	
    		cal.set(Calendar.MONTH, Integer.parseInt(s.substring(5,7)) - 1);
    		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(8,10)));
    		cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s.substring(11,13)));
    		cal.set(Calendar.MINUTE, Integer.parseInt(s.substring(14, 16)));
    		cal.set(Calendar.SECOND, Integer.parseInt(s.substring(17,19)));
    		return cal.getTimeInMillis();
    	} else if (format.equalsIgnoreCase("yyyy-mm-dd")) {
    		Calendar cal = Calendar.getInstance();
    		
    		cal.set(Calendar.YEAR, Integer.parseInt(s.substring(0,4)));	
    		cal.set(Calendar.MONTH, Integer.parseInt(s.substring(5,7)) - 1);
    		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s.substring(8,10)));
    		cal.set(Calendar.HOUR_OF_DAY, 0);
    		cal.set(Calendar.MINUTE, 0);
    		cal.set(Calendar.SECOND, 1);
    		return cal.getTimeInMillis();
    	} 
    	
    	return 0;
    }
	
	public static String addDay(String date, int minus) {
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0,4)));	
		cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4,6)) - 1);
		cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6,8)));
		cal.set(Calendar.HOUR_OF_DAY, 0); //一定要大于4点
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		cal.add(Calendar.DAY_OF_MONTH, minus);
		
		int month = cal.get(Calendar.MONTH) + 1;
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		return cal.get(Calendar.YEAR) +(month < 10 ? "0" : "")+ month  + (dayOfMonth < 10 ? "0" : "") + dayOfMonth ;
	}
	
	public static String parseAge(String date) {
		if (date != null && date.trim().length() > 0) {
			long b = TextUtil.parseTime(date, "yyyy-mm-dd");
			long minus = System.currentTimeMillis() - b;
			float age = (float)minus/(365*24*3600*1000L);
			
			int ageInt = (int)age;
			if (ageInt > 0) {
				String month = age - ageInt > 0.5 ? "半" : "";
				return ageInt + "岁" + month;
			} else {
				String month = (int)((age - ageInt)*12) + "个月";
				return month;
			}
		} else {
			return "";
		}
	}
    
    public static void main(String[] args) {
//    	String s = SHA1("jsapi_ticket=sM4AOVdWfPE4DxkXGEs8VMCPGGVi4C3VM0P37wVUCFvkVAy_90u5h9nbSlYy3-Sl-HhTdfl2fzFy1AOcHKP7qg&noncestr=Wm3WZYTPz0wzccnW&timestamp=1414587457&url=http://mp.weixin.qq.com?params=value");
//    	System.out.println(s.equals("0f9de62fce790f9a083d5c99e95740ceb90c27ed"));
//    	System.out.println(String.format("%1$tY%1$tm%1$td", System.currentTimeMillis()));
//    	
//    	System.out.println(addDay("20150311", -1));
//    	System.out.println(addDay("20150301", -1));
//    	System.out.println(addDay("20150301", -2));
//    	
//    	System.out.println(getMinusDayOfTwoDate("20150301", "20150305"));
//    	System.out.println(getMinusDayOfTwoDate("20150301", "20150302"));
//    	System.out.println(getMinusDayOfTwoDate("20150301", "20150301"));
//    	System.out.println(getMinusDayOfTwoDate("20150301", "20150228"));
//    	System.out.println(getMinusDayOfTwoDate("20150301", "20150227"));
    	
    	System.out.println(parseAge("2017-12-10"));
    	
    }
}
