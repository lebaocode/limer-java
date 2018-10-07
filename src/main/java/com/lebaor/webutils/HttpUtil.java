package com.lebaor.webutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;

/**
 * @deprecated
 */
public class HttpUtil {
	
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";  
    private static final String DEFAULT_CHARSET = "utf-8";  
    
    public static final int CONNECT_TIMEOUT = 5000; //5s
	public static final int READ_TIMEOUT = 30000; //30s
      
	public static String doPost(String url, String params)  {
		return doPost(url, params, DEFAULT_CHARSET, CONNECT_TIMEOUT, READ_TIMEOUT);
	}
	
    public static String doPost(String url, String params, String charset, int connectTimeout, int readTimeout)  {  
        String ctype = "application/json;charset=" + charset;  
        byte[] content = {};  
        if(params != null){  
        	try {
        		content = params.getBytes(charset);
        	} catch(Exception e) {}
        }  
          
        return doPost(url, ctype, content, connectTimeout, readTimeout);  
    }  
    
    public static String doGet(String url)  {
		return doGet(url, DEFAULT_CHARSET, CONNECT_TIMEOUT, READ_TIMEOUT);
	}
    
    public static String doGet(String url, String charset, int connectTimeout, int readTimeout)  {  
        String ctype = "application/json;charset=" + charset;  
        byte[] content = {};
        return doGet(url, ctype, content, connectTimeout, readTimeout);  
    } 
    public static String doPost(String url, String ctype, byte[] content,int connectTimeout,int readTimeout) {
    	return doPostGet(url, ctype, content, connectTimeout, readTimeout, true);  
    }
    public static String doGet(String url, String ctype, byte[] content,int connectTimeout,int readTimeout)  {
    	return doPostGet(url, ctype, content, connectTimeout, readTimeout, false);  
    }
        
    private static String doPostGet(String url, String ctype, byte[] content,int connectTimeout,int readTimeout, boolean isPost) {
        HttpURLConnection conn = null;  
        OutputStream out = null;  
        String rsp = null;  
        try {  
            try{
                conn = getConnection(new URL(url), isPost ? METHOD_POST : METHOD_GET, ctype);
                conn.setConnectTimeout(connectTimeout);  
                conn.setReadTimeout(readTimeout);
            }catch(Exception e){  
            	e.printStackTrace();
                LogUtil.WEB_LOG.warn("GET_CONNECTOIN_ERROR, URL = " + url, e);  
            }  
            try{  
                out = conn.getOutputStream();  
                out.write(content);  
                rsp = getResponseAsString(conn);  
            }catch(IOException e){  
            	e.printStackTrace();
            	LogUtil.WEB_LOG.warn("REQUEST_RESPONSE_ERROR, URL = " + url, e);  
            }  
              
        }finally {  
            if (out != null) {  
                try { 
                	out.close();  
                } catch (Exception e) {}
            }  
            if (conn != null) {  
                conn.disconnect();  
            }  
        }  
          
        LogUtil.WEB_LOG.info((isPost ? "POST " : "GET ") + " " + url + " SUCCESS");
        return rsp;  
    }  
        
    private static HttpURLConnection getConnection(URL url, String method, String ctype)  
            throws IOException {  
    	
    	HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
        conn.setRequestMethod(method);  
        conn.setDoInput(true);  
        conn.setDoOutput(true);  
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        //conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        //conn.setRequestProperty("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X; en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
        //conn.setRequestProperty("Cookie", "bid=\"PuRhuJFTaiA\"; ll=\"108288\"; viewed=\"6965746_4143105_2181438_3040870_2052176_3158926_25876241_11530748\"; __utma=30149280.443411579.1397269712.1403626532.1403707194.19; __utmb=30149280.4.10.1403707194; __utmc=30149280; __utmz=30149280.1403707194.19.14.utmcsr=developers.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/wiki/");
        conn.setRequestProperty("Content-Type", ctype);  
        
        return conn;  
    }  
  
//    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {  
//        String charset = getResponseCharset(conn.getContentType());  
//        InputStream es = conn.getErrorStream();  
//        if (es == null) {  
//            return getStreamAsString(conn.getInputStream(), charset);  
//        } else {  
//            String msg = getStreamAsString(es, charset);  
//            if (TextUtil.isEmpty(msg)) {  
//                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());  
//            } else {  
//                throw new IOException(msg);  
//            }  
//        }  
//    }  
    
    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {  
        String charset = getResponseCharset(conn.getContentType());  
        try {
        	String msg = getStreamAsString(conn.getInputStream(), charset);
        	return msg;
        } catch (Exception e) {
        	InputStream es = conn.getErrorStream();
        	String msg = es != null ? getStreamAsString(es, charset) : "";
        	throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage() + msg); 
        }
    }  
  
    private static String getStreamAsString(InputStream stream, String charset) throws IOException {  
        try {  
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));  
            StringWriter writer = new StringWriter();  
  
            char[] chars = new char[256];  
            int count = 0;  
            while ((count = reader.read(chars)) > 0) {  
                writer.write(chars, 0, count);  
            }  
  
            return writer.toString();  
        } finally {  
            if (stream != null) {  
                stream.close();  
            }  
        }  
    }  
  
    private static String getResponseCharset(String ctype) {  
        String charset = DEFAULT_CHARSET;  
  
        if (!TextUtil.isEmpty(ctype)) {  
            String[] params = ctype.split(";");  
            for (String param : params) {  
                param = param.trim();  
                if (param.startsWith("charset")) {  
                    String[] pair = param.split("=", 2);  
                    if (pair.length == 2) {  
                        if (!TextUtil.isEmpty(pair[1])) {  
                            charset = pair[1].trim();  
                        }  
                    }  
                    break;  
                }  
            }  
        }  
  
        return charset;  
    }  
}
