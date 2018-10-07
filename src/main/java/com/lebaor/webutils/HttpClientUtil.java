package com.lebaor.webutils;

import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.lebaor.utils.LogUtil;


public class HttpClientUtil {
	private static final String METHOD_POST = "POST";
	private static final String METHOD_GET = "GET";
	private static final String METHOD_DELETE = "DELETE";  
	private static final String METHOD_PUT = "PUT";  
		
	private static void addHeader(HttpRequestBase httpMethod, HashMap<String, String> headers) {
		
		if (headers != null) {
        	for (String key : headers.keySet()) {
        		httpMethod.addHeader(key, headers.get(key));
        	}
        }
		
//		if (headers == null || httpMethod.getFirstHeader("Content-Type") == null) {
//		LogUtil.WEB_LOG.debug("Add Header Content-Type:application/json;charset=utf-8");
//			httpMethod.addHeader("Content-Type", "application/json;charset=utf-8");
//		}
//		
//		if (headers == null || httpMethod.getFirstHeader("Content-Type") == null) {
//			LogUtil.WEB_LOG.debug("Add Header Content-Type:application/x-www-form-urlencoded;charset=utf-8");
//			httpMethod.addHeader("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
//		}
		
		if (headers == null || httpMethod.getFirstHeader("User-Agent") == null) {
			httpMethod.addHeader("User-Agent", "stargate");
		}
	}
	
	public static String doGet(String url)  {
		return doGet(url, null);
	}
	
	public static byte[] download(String url)  {
		try {
			return downloadHTTPRequest(new URL(url), null, METHOD_GET, null);
		} catch(Exception e) {
			LogUtil.WEB_LOG.debug(e);
		}
		return null;
	}
	
	public static String doGet(String url, HashMap<String, String> headers)  {
		try {
			return sendHTTPRequest(new URL(url), null, METHOD_GET, headers);
		} catch(Exception e) {
			LogUtil.WEB_LOG.debug(e);
		}
		return null;
	}
	
	public static String doDelete(String url, HashMap<String, String> headers)  {
		try {
			return sendHTTPRequest(new URL(url), null, METHOD_DELETE, headers);
		} catch(Exception e) {
			LogUtil.WEB_LOG.debug(e);
		}
		return null;
	}
	
	public static String doPost(String url, String params)  {
		return doPost(url, params, null);
	}
	
	public static String doPost(String url, String params, HashMap<String, String> headers)  {
        try {
        	return sendHTTPRequest(new URL(url), params, METHOD_POST, headers);
        } catch(Exception e) {
        	LogUtil.WEB_LOG.debug(e);
		}
		return null;
    } 
	
	/**
	 * Send Request
	 * 没有加header contentType?还有charset
	 * @return
	 */
	public static String sendHTTPRequest(URL url, Object dataBody, String method, HashMap<String, String> headers) {
		boolean isSSL = url.getProtocol().equalsIgnoreCase("https") ? true : false;
		LogUtil.WEB_LOG.debug("BEGIN "+ url.getProtocol() +" "+ method +" "+ url + " BODY "+ dataBody);
		
		HttpClient httpClient = getClient(isSSL);

		try {

			HttpResponse response = null;

			if (method.equals(METHOD_POST)) {
				HttpPost httpPost = new HttpPost(url.toURI());
				addHeader(httpPost, headers);
				httpPost.setEntity(new StringEntity(dataBody.toString(), "UTF-8"));

				response = httpClient.execute(httpPost);
			} else if (method.equals(METHOD_PUT)) {
				HttpPut httpPut = new HttpPut(url.toURI());
				addHeader(httpPut, headers);
				httpPut.setEntity(new StringEntity(dataBody.toString(), "UTF-8"));

				response = httpClient.execute(httpPut);
			} else if (method.equals(METHOD_GET)) {

				HttpGet httpGet = new HttpGet(url.toURI());
				addHeader(httpGet, headers);
				response = httpClient.execute(httpGet);

			} else if (method.equals(METHOD_DELETE)) {
				HttpDelete httpDelete = new HttpDelete(url.toURI());
				addHeader(httpDelete, headers);
				response = httpClient.execute(httpDelete);
			}
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				
				String responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
				int status = response.getStatusLine().getStatusCode();
				LogUtil.WEB_LOG.debug("END "+ url.getProtocol() +" "+ method +" "+ url + " RESP " + status 
						+ (status == 200 ? "" : (" " + responseContent)));
				return responseContent;
			}
		} catch (Exception e) {
			LogUtil.WEB_LOG.debug(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return null;
	}
	

	/**
	 * Send Request
	 * 没有加header contentType?还有charset
	 * @return
	 */
	public static byte[] downloadHTTPRequest(URL url, Object dataBody, String method, HashMap<String, String> headers) {
		boolean isSSL = url.getProtocol().equalsIgnoreCase("https") ? true : false;
		LogUtil.WEB_LOG.debug("BEGIN "+ url.getProtocol() +" "+ method +" "+ url + " BODY "+ dataBody);
		
		HttpClient httpClient = getClient(isSSL);

		try {

			HttpResponse response = null;

			if (method.equals(METHOD_POST)) {
				HttpPost httpPost = new HttpPost(url.toURI());
				addHeader(httpPost, headers);
				httpPost.setEntity(new StringEntity(dataBody.toString(), "UTF-8"));

				response = httpClient.execute(httpPost);
			} else if (method.equals(METHOD_PUT)) {
				HttpPut httpPut = new HttpPut(url.toURI());
				addHeader(httpPut, headers);
				httpPut.setEntity(new StringEntity(dataBody.toString(), "UTF-8"));

				response = httpClient.execute(httpPut);
			} else if (method.equals(METHOD_GET)) {

				HttpGet httpGet = new HttpGet(url.toURI());
				addHeader(httpGet, headers);
				response = httpClient.execute(httpGet);

			} else if (method.equals(METHOD_DELETE)) {
				HttpDelete httpDelete = new HttpDelete(url.toURI());
				addHeader(httpDelete, headers);
				response = httpClient.execute(httpDelete);
			}
			
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				
				byte[] responseContent = EntityUtils.toByteArray(entity);
				EntityUtils.consume(entity);
				int status = response.getStatusLine().getStatusCode();
				LogUtil.WEB_LOG.debug("END "+ url.getProtocol() +" "+ method +" "+ url + " RESP " + status 
						+ (status == 200 ? "" : (" " + responseContent)));
				return responseContent;
			}
		} catch (Exception e) {
			LogUtil.WEB_LOG.debug(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return null;
	}
	
	public static HttpClient getClient(boolean isSSL) {
		
		HttpClient httpClient = new DefaultHttpClient();
		
		if (isSSL) {
			X509TrustManager xtm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			try {
				SSLContext ctx = SSLContext.getInstance("TLS");

				ctx.init(null, new TrustManager[] { xtm }, null);

				SSLSocketFactory socketFactory = new SSLSocketFactory(ctx);

				httpClient.getConnectionManager().getSchemeRegistry().register(new Scheme("https", 443, socketFactory));

			} catch (Exception e) {
				throw new RuntimeException();
			}
		}

		return httpClient;
	}
	
}
