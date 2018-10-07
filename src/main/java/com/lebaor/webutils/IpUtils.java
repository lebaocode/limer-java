package com.lebaor.webutils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;


public class IpUtils {

    /**
     * Convert ip string as a byte array
     * @param ip  the ip address (in string format)
     * @return  the byte array
     */
    public static byte[] textToNumericFormat(String ip)  throws ParseException {
    	byte[] bs = null;
    	try {
	        InetAddress ia = InetAddress.getByName(ip);
	        bs = ia.getAddress();
    	} catch (UnknownHostException e) {
    		throw new ParseException(e.getMessage(), 0);
    	}
    	return bs;
    }

    /**
     * Parse an ip address (in string format) as an integer
     * @param ip  the ip address (in integer format)
     * @return  the ip address (in integer format)
     * @throws ParseException
     */
    public static int parseIp(String ip) throws ParseException {
    	byte[] bs = null;
    	try {
	        InetAddress ia = InetAddress.getByName(ip);
	        bs = ia.getAddress();
    	} catch (UnknownHostException e) {
    		throw new ParseException(e.getMessage(), 0);
    	}
        
        if (bs == null || bs.length != 4) {
            throw new ParseException("ip format error : " + ip, 0);
        }
        return (((int) bs[0] & 0xFF) << 24) | (((int) bs[1] & 0xFF) << 16) 
            | (((int) bs[2] & 0xFF) << 8) | ((int) bs[3] & 0xFF);
    }

    /**
     * Format an ip address in integer format as a string
     * @param ip ip address in integer format
     * @return ip address as a string
     */
    public static String formatIp(int ip) {
        StringBuilder buffer = new StringBuilder();
        for (int i=0; i<4; i++) {
            buffer.append((ip >> (8 * (3-i))) & 0xff).append('.');
        }
        buffer.setLength(buffer.length()-1);
        return buffer.toString();
    }
	

    /**
     * Whether this is an reserved ip address
     * @param ip ip address
     * @return whether it is reserved
     */
    public static boolean isReserved(InetAddress ip) {
        return ip.isAnyLocalAddress()   // Windows sometimes use 0.0.0.0 as any address
            || ip.getHostAddress().equals("0.0.0.0")
            || ip.getHostAddress().equals("255.255.255.255");
    }    
    
    /**
     * Determines if the address is a wide-area network endpoint
     * address, e.g. non-private and non-multicast and non-broadcast etc
     * @param addr
     * @return true if it is a WAN endpoint address
     * @author zhangkun
     */
    public static boolean isWideAreaEndpoint(InetAddress addr) {
        return !(addr.isAnyLocalAddress() || addr.isLinkLocalAddress()
                || addr.isLoopbackAddress() || addr.isMCGlobal()
                || addr.isMCLinkLocal() || addr.isMCNodeLocal()
                || addr.isMCOrgLocal() || addr.isMCSiteLocal()
                || addr.isMulticastAddress() || addr.isSiteLocalAddress()
                || addr.getHostAddress().startsWith("255."));
    }
    

    /**
     * Determines if this host is an IP.
     * 
     * @param url
     * @return
     * @author fjiang
     */
    public static boolean isIPv4(String host) {
        char[] tmp = host.toCharArray();
        int dot = 0;
        int value = -1;
        for (int i = 0; i < tmp.length; i++) {
            char c = tmp[i];
            if (c >= '0' && c <= '9') {
                value = Math.max(0, value) * 10 + c - '0';
            } else if (c == '.') {
            	if(value<0)
            		return false;
                dot++;
                if (value < 0 || value > 255) {
                    return false;
                }
                value = -1;
            } else {
                return false;
            }
        }
        if (dot == 3 && value >= 0 && value <= 255) {
            return true;
        } else {
            return false;
        }
    }


    public static boolean isIPv6(String host) {
        char[] tmp = host.toCharArray();
        int colon = 0;
        int dot = 0;
        for (int i = 0; i < tmp.length; i++) {
            char c = tmp[i];
            if (c == ':') {
                colon++;
            } else if (c == '.') {
                dot++;
            }
            if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F'))) {
                return false;
            }
        }
        if (colon >= 2 && colon <= 8 && (dot == 0 || dot == 3)) {
            return true;
        }
        return false;
    }
}
