package com.lebaor.webutils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

/**
 * @deprecated use HttpClientUtil instead.
 *
 */
public class HttpsUtil {
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_DELETE = "DELETE";  
		
    private static final String DEFAULT_CHARSET = "utf-8";  
    
    public static final int CONNECT_TIMEOUT = 5000; //5s
	public static final int READ_TIMEOUT = 30000; //30s
      
	public static String doGet(String url)  {
		return doGet(url, null);
	}
	
	public static String doPost(String url, String params)  {
		return doPost(url, params, null);
	}
	
	public static String doGet(String url, HashMap<String, String> headers)  {
		String ctype = "application/json;charset=" + DEFAULT_CHARSET;  
        byte[] content = {};
        return doHttpAction(url, ctype, content, headers, CONNECT_TIMEOUT, READ_TIMEOUT, METHOD_GET);
	}
	
	public static String doGet(String url, String params, HashMap<String, String> headers)  {  
        String ctype = "application/json;charset=" + DEFAULT_CHARSET;  
        byte[] content = {};  
        if(params != null){  
        	try {
        		content = params.getBytes(DEFAULT_CHARSET);
        	} catch(Exception e) {}
        }  
          
        return doHttpAction(url, ctype, content, headers, CONNECT_TIMEOUT, READ_TIMEOUT, METHOD_GET);  
    }
	
    public static String doPost(String url, String params, HashMap<String, String> headers)  {  
        String ctype = "application/json;charset=" + DEFAULT_CHARSET;  
        byte[] content = {};  
        if(params != null){  
        	try {
        		content = params.getBytes(DEFAULT_CHARSET);
        	} catch(Exception e) {}
        }  
          
        return doHttpAction(url, ctype, content, headers, CONNECT_TIMEOUT, READ_TIMEOUT, METHOD_POST);  
    }  
    
    public static String doDelete(String url, String params, HashMap<String, String> headers) {
    	String ctype = "application/json;charset=" + DEFAULT_CHARSET;
    	byte[] content = {};  
        if(params != null){  
        	try {
        		content = params.getBytes(DEFAULT_CHARSET);
        	} catch(Exception e) {}
        } 
    	return doHttpAction(url, ctype, content, headers, CONNECT_TIMEOUT, READ_TIMEOUT, METHOD_DELETE);
    }
    
    private static String doHttpAction(String url, String ctype, byte[] content, HashMap<String, String> headers,
    		int connectTimeout,int readTimeout, String httpMethod) {
        HttpsURLConnection conn = null;  
        OutputStream out = null;  
        String rsp = null;  
        try {  
            try{  
                SSLContext ctx = SSLContext.getInstance("TLS");  
                ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());  
                SSLContext.setDefault(ctx);  
  
                conn = getConnection(new URL(url), httpMethod, ctype, headers);   
                conn.setHostnameVerifier(new HostnameVerifier() {  
                    
                    public boolean verify(String hostname, SSLSession session) {  
                        return true;  
                    }  
                });  
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
          
        LogUtil.WEB_LOG.info(httpMethod + " " + url + " SUCCESS");
        return rsp;  
    }  
  
    private static class DefaultTrustManager implements X509TrustManager {  
  
		public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}  
  
          
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}  
  
          
        public X509Certificate[] getAcceptedIssuers() {  
            return null;  
        }  
  
    }  
    
    private static HttpsURLConnection getConnection(URL url, String method, String ctype, HashMap<String, String> headers)  
            throws IOException {
    	HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();  
        conn.setRequestMethod(method);  
        conn.setDoInput(true);  
        conn.setDoOutput(true);  
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html,application/json");  
        conn.setRequestProperty("User-Agent", "stargate");  
        conn.setRequestProperty("Content-Type", ctype);
        
        if (headers != null) {
        	for (String key : headers.keySet()) {
        		conn.setRequestProperty(key, headers.get(key));
        	}
        }
        return conn;  
    }
  
    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {  
        String charset = getResponseCharset(conn.getContentType());  
        InputStream es = conn.getErrorStream();  
        if (es == null) {  
            return getStreamAsString(conn.getInputStream(), charset);  
        } else {  
            String msg = getStreamAsString(es, charset);  
            if (TextUtil.isEmpty(msg)) {  
                throw new IOException(conn.getResponseCode() + ":" + conn.getResponseMessage());  
            } else {  
                throw new IOException(conn.getResponseCode() + ":" + msg);  
            }  
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
