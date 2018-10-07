package com.lebaor.wx;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.TextUtil;

public class WxPayHttpClientUtil {
	
	/**
	 * Send Request
	 * 没有加header contentType?还有charset
	 * @return
	 */
	public static String post(String href, String body, String wxPassword) {
		CloseableHttpClient httpClient = null;

		try {
			URL url = new URL(href);
			LogUtil.WEB_LOG.debug("BEGIN "+ url.getProtocol() +" POST "+ url + " BODY "+ body);
			
			httpClient = getClientWxPay(wxPassword);
			HttpPost httpost = new HttpPost(url.toURI()); //
	        httpost.addHeader("Connection", "keep-alive");
	        httpost.addHeader("Accept", "*/*");
	        httpost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
	        httpost.addHeader("Host", "api.mch.weixin.qq.com");
	        httpost.addHeader("X-Requested-With", "XMLHttpRequest");
	        httpost.addHeader("Cache-Control", "max-age=0");
	        httpost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
	        httpost.setEntity(new StringEntity(body, "UTF-8"));
	        CloseableHttpResponse  response = httpClient.execute(httpost);
	        HttpEntity entity = response.getEntity();
	        
			if (entity != null) {
				String responseContent = EntityUtils.toString(entity, "UTF-8");
				EntityUtils.consume(entity);
				int status = response.getStatusLine().getStatusCode();
				LogUtil.WEB_LOG.debug("END "+ url.getProtocol() +" POST "+ url + " RESP " + status 
						+ (status == 200 ? "" : (" " + responseContent)));
				response.close();
				return responseContent;
			}
		} catch (Exception e) {
			LogUtil.WEB_LOG.debug("pay", e);
		} finally {
			try {
				if (httpClient != null) httpClient.close();
			} catch (Exception e) {}
		}

		return null;
	}
	
		
	public static CloseableHttpClient getClientWxPay(String wxCertPassword) throws Exception{
		KeyStore keyStore  = KeyStore.getInstance("PKCS12");
		FileInputStream instream = new FileInputStream(new File("apiclient_cert.p12"));
		keyStore.load(instream, wxCertPassword.toCharArray());
		instream.close();
		SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, wxCertPassword.toCharArray()).build();
		SSLSocketFactory socketFactory = new SSLSocketFactory(sslcontext);
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext,new String[] { "TLSv1" },null,
		SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf) .build();
		
		return httpClient;
	}
	
}
