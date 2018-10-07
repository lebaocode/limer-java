package com.lebaor.webutils;

import java.util.HashMap;
import java.util.TreeSet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.lebaor.utils.LogUtil;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class CookieUtil {

    /* cookie configuration*/
    public static final String COOKIE_NAME = "LEBAO_COOKIE";//
    public static final String COOKIE_PATH = "/";//
    public static final int COOKIE_EXPIRATION = 259200000;//8year

    //public static final String COOKIE_NAME_PERIOD = "LEBAO_PERIOD_COOKIE";//
    //public static final int COOKIE_EXPIRATION_PERIOD = 2592000;//a month in second
    
    private static class CookieItem {
        public String key;
        public TreeSet<String> values;
        public String defaultValue;
        public CookieItem(String key, String defaultValue) {
             this.key = key;
             this.defaultValue = defaultValue;
             values = new TreeSet<String>();
             values.add(defaultValue);
        }
    }
    private static HashMap<String, CookieItem> items = new HashMap<String, CookieItem>();
    static {
//        CookieItem it = new CookieItem("resultCountDisplay", "10");
//        it.values.add("20");
//        it.values.add("30");
//        items.put(it.key, it);
    }
    private static final Logger LOG = LogUtil.WEB_LOG;

    /**
     * 根据几个模块传入的req的cookie,设置model的值,如果没有cookie则设置成固定值
     * @param request
     * @param model
     */
    public static void setViewModel(HttpServletRequest request, HashMap<String, Object> model){
            LOG.info("setViewModel()");
            Cookie destCookie;
            String cookieName = System.getProperty("cookie.name");
            if(cookieName == null || cookieName.equals(""))
                cookieName = COOKIE_NAME;
            destCookie = getCookie(request, cookieName);
            if (destCookie == null) {
                    LOG.info("No cookie exists from client!");
                    setDefaultViewModel(model);
            } else {
                    LOG.info("Cookie found");
                    setViewModel(destCookie,model);
            }
    }

    /**
     * 根据cookie name解析req的cookie
     * @param request
     * @param name
     * @return
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null)
                    return null;
            for (Cookie cookie : cookies) {
                    if (cookie.getName().equals(name))
                            return cookie;
            }
            return null;
    }

    /**
     * 如果没有cookie,则设置偏好的缺省值,方便页面设置
     * @param model
     */
    private static void setDefaultViewModel(HashMap<String, Object> model){
            for(String key : items.keySet()) {
                 model.put(key, items.get(key).defaultValue);
            }
    }

    /**
     * 根据客户端传送过来的cookie,设置页面对应项值
     * @param destCookie
     * @param model
     */
    private static void setViewModel(Cookie destCookie, HashMap<String, Object> model){
            LOG.info("setting model values according to cookie");
            HashMap<String, String> map = parseCookie(destCookie);
            for(String key : items.keySet()) {
                 String value = map.get(key);
                 CookieItem item = items.get(key);
                 if(value != null && item.values.contains(value)) {
                     model.put(key, value);
                 } else {
                     model.put(key, item.defaultValue);
                 }
            }
    }

    /**
     * 解析cookie成名值对的形式
     * @param cookie
     * @return
     */
    private static HashMap<String,String> parseCookie(Cookie cookie){
            HashMap<String,String>map = new HashMap<String,String>();
            if(cookie == null)
                    return map;
            String value = decode(cookie.getValue());
            String []pairs = value.split(";");//之前为什么是分号？
            if(pairs!=null){
                    for(int i = 0; i<pairs.length;i++){
                            int index = pairs[i].indexOf("=");
                            if(index !=-1){
                                    if(index == (pairs[i].length()-1))
                                            map.put(pairs[i].substring(0,index),"");
                                    else
                                            map.put(pairs[i].substring(0,index),pairs[i].substring(index+1));
                            }
                    }
            }
            return map;
    }

    /**
     * 将cookie写入客户端
     * @param response
     * @param name
     * @param value
     * @param domain
     * @param path
     * @param age
     * @throws ServletException
     */
    @SuppressWarnings("unused")
	private static void addCookie(HttpServletResponse response, String name,
                    String value, String domain, String path, int age)
                    throws ServletException {
            if (name.indexOf(' ') >= 0 || value.indexOf(' ') >= 0) {
                    throw new ServletException("cookie value should not contains space");
            }
            Cookie cookie = new Cookie(name, value);
            if (domain != null && !domain.equals("")) {
                    cookie.setDomain(domain);
            }
            if(path != null && !path.equals(""))
                    cookie.setPath(path);
            cookie.setMaxAge(age);
            response.addCookie(cookie);
    }
    
    public static Cookie addCookie(HttpServletRequest req, HttpServletResponse res, String key, String cookieValue, String cookieDomain) throws ServletException {
    	Cookie cookie = getCookie(req, COOKIE_NAME);
    	return addCookie(res, cookie, key ,cookieValue, cookieDomain, COOKIE_EXPIRATION);
    }
    
    
    public static Cookie removeCookieProperty(HttpServletRequest req, HttpServletResponse res, String key, String cookieDomain) throws ServletException {
    	Cookie cookie = getCookie(req, COOKIE_NAME);
    	return addCookie(res, cookie, key ,"", cookieDomain, 1);
    }
    
    public static Cookie addCookie(HttpServletResponse res, Cookie cookie, String key, String cookieValue, String cookieDomain, int cookieExpiration) throws ServletException {
    	HashMap<String, String> pairs;
    	if (cookie == null) {
    		pairs = new HashMap<String, String>();
    		pairs.put(key,  cookieValue);
    		return addCookie(res, pairs, cookieDomain, cookieExpiration);
    	} else {
	    	pairs = parseCookie(cookie);
	    	if (pairs == null) {
	    		pairs = new HashMap<String, String>();
	    	}
	    	
	    	pairs.remove(key);
	    	pairs.put(key,  cookieValue);
	    	
	    	return editCookie(res, cookie, pairs, cookieDomain, cookieExpiration);
    	}
    	
    	
    }
    
    private static Cookie editCookie(HttpServletResponse res, Cookie cookie, HashMap<String, String> values, String cookieDomain, int cookieExpiration) throws ServletException {
        StringBuilder buffer = new StringBuilder();
        for (String key : values.keySet()) {
        	if(buffer.length() != 0) {
                buffer.append(";");
           }
           buffer.append(key);
           buffer.append("=");
           buffer.append(values.get(key));
        }
//        
//        for(String key : items.keySet()) {
//             String value = values.get(key);
//             CookieItem item = items.get(key);
//             if(value == null || !item.values.contains(value)) {
//                 value = item.defaultValue;
//             }
//             if(buffer.length() != 0) {
//                  buffer.append(";");
//             }
//             buffer.append(key);
//             buffer.append("=");
//             buffer.append(value);
//        }
        String value = buffer.toString();
        LOG.info("write cookie=" + value);
        
        String cookieName = COOKIE_NAME;


        String cookiePath = COOKIE_PATH;

        //int cookieExpiration = COOKIE_EXPIRATION;

        if (cookieName.indexOf(' ') >= 0 || value.indexOf(' ') >= 0) {
                throw new ServletException("cookie value should not contains space");
        }
        value = encode(value);
        
        cookie.setValue(value);
        
        if(cookieDomain != null && !cookieDomain.equals("")) {
                LOG.info("set cookie domain to " + cookieDomain);
                cookie.setDomain(cookieDomain);
        }
        if(cookiePath != null && !cookiePath.equals("")) {
                LOG.info("set cookie path to " + cookiePath);
                cookie.setPath(cookiePath);
        }
        LOG.info("set cookie expiration to " + cookieExpiration);
        cookie.setMaxAge(cookieExpiration);

        res.addCookie(cookie);
        return cookie;
}
    
    /**
     * 将cookie写入客户端
     * @param res
     * @param conf
     * @param value
     * @return
     * @throws ServletException
     */
    private static Cookie addCookie(HttpServletResponse res, HashMap<String, String> values, String cookieDomain, int cookieExpiration) throws ServletException {
            StringBuilder buffer = new StringBuilder();
            for (String key : values.keySet()) {
            	if(buffer.length() != 0) {
                    buffer.append(";");
               }
               buffer.append(key);
               buffer.append("=");
               buffer.append(values.get(key));
            }
//            
//            for(String key : items.keySet()) {
//                 String value = values.get(key);
//                 CookieItem item = items.get(key);
//                 if(value == null || !item.values.contains(value)) {
//                     value = item.defaultValue;
//                 }
//                 if(buffer.length() != 0) {
//                      buffer.append(";");
//                 }
//                 buffer.append(key);
//                 buffer.append("=");
//                 buffer.append(value);
//            }
            String value = buffer.toString();
            LOG.info("write cookie=" + value);
            String cookieName = COOKIE_NAME;


            String cookiePath = COOKIE_PATH;

            //int cookieExpiration = COOKIE_EXPIRATION;

            if (cookieName.indexOf(' ') >= 0 || value.indexOf(' ') >= 0) {
                    throw new ServletException("cookie value should not contains space");
            }
            value = encode(value);
            Cookie cookie = new Cookie(cookieName,value);
            if(cookieDomain != null && !cookieDomain.equals("")) {
                    LOG.info("set cookie domain to " + cookieDomain);
                    cookie.setDomain(cookieDomain);
            }
            if(cookiePath != null && !cookiePath.equals("")) {
                    LOG.info("set cookie path to " + cookiePath);
                    cookie.setPath(cookiePath);
            }
            LOG.info("set cookie expiration to " + cookieExpiration);
            cookie.setMaxAge(cookieExpiration);

            res.addCookie(cookie);
            return cookie;
    }

    /**
     * 将加密之后的cookie value中的'\n'和'\r'替换
     * @param input
     * @return
     */
    private static String encode(String input) {
            String ret = "";
            try {
                    BASE64Encoder encoder = new BASE64Encoder();
                    ret = encoder.encode(input.getBytes("UTF-8"));
            } catch (Exception e) {
                    ret = "";
            }
            ret=ret.replace('\n', '_').replace('\r', '@');
            return ret;
    }

    /**
     * 还原cookie value中的 '\n'和'\r'
     * @param input
     * @return
     */
    public static String decode(String input){
            String ret="";
            input = input.replace('_', '\n').replace('@', '\r');
            try{
                    BASE64Decoder decoder = new BASE64Decoder();
                    byte []b = decoder.decodeBuffer(input);
                    ret = new String(b,"UTF-8");
            }catch(Exception e){
                    ret = "";
            }
            return ret;
    }

    /**
     * 获取cookie指定的字段的值
     * @param cookie
     * @param property
     * @return
     */
    private static String getCookiePropertyValue(Cookie cookie, String property){
    		LOG.info("get cookie: " + property);
    		LOG.info("cookie value before decoded: " + cookie.getValue());
            String cookieVal = decode(cookie.getValue());
            LOG.info("cookie value decoded: " + cookieVal);
            int start = cookieVal.indexOf(property + "=");
            if( start == -1)
                    return null;
            start +=  property.length() + 1;
            int end = cookieVal.indexOf(";",start);
            if(end == -1)
                    end = cookieVal.length();
            String propertyValue = cookieVal.substring(start, end);
            LOG.info("get cookie: " + property + "=" + propertyValue);
            return propertyValue;
    }
    
    public static String getCookiePropertyValue(HttpServletRequest req, String property){
    	Cookie c = getCookie(req, COOKIE_NAME);
    	if (c == null) return null;
    	return getCookiePropertyValue(c, property);
    }
    
    public static void main(String[] args){
    	String value = "YXR0ZW5kX3VzZXJwbGFuX2lkPTQ=";
    	System.out.println(decode(value));
    	
    }
}
