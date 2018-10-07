package com.lebaor.thirdpartyutils;

import com.lebaor.utils.FileUtil;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;

//上传图片至七牛
public class QiniuUtil {
	private static final String ACCESS_KEY = "rT-Fy63iACuYYSriqf8NOB__x__RSS_ovuFv63dG";
	private static final String SECRET_KEY = "VW5yfujtswSCyFwHm6oK3kOlLLtP-NfEhIMzxqW8";
	private static final String BUCKET_NAME = "lebao";
	
	public static final String URL_PREFIX = "http://7u2pjw.com1.z0.glb.clouddn.com";
	
	public static void deleteImg(String fileName) {
		Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);
        RSClient client = new RSClient(mac);
        client.delete(BUCKET_NAME, fileName);
	}
	
	public static void uploadPic(String prefix, String fileName, byte[] data) throws Exception {
		Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);
        
        PutPolicy putPolicy = new PutPolicy(BUCKET_NAME);
        String upToken = putPolicy.token(mac);
        String mimeType = "image/jpeg";//
//	        String mimeType = "application/octet-stream";
        
        String key = prefix + "/" +fileName;
        PutExtra extra = new PutExtra();
        extra.mimeType = mimeType;
        
        IoApi.Put(upToken, key, new java.io.ByteArrayInputStream(data), extra);
//	        IoApi.putFile(upToken, key, "D:\\data\\test2.jpg", extra);
	        
	}
	
	public static void uploadVoice(String prefix, String fileName, byte[] data) throws Exception {
		Mac mac = new Mac(ACCESS_KEY, SECRET_KEY);
        
        PutPolicy putPolicy = new PutPolicy(BUCKET_NAME);
        String upToken = putPolicy.token(mac);
        String mimeType = "audio/amr";//
//	        String mimeType = "application/octet-stream";
        
        String key = prefix + "/" +fileName;
        PutExtra extra = new PutExtra();
        extra.mimeType = mimeType;
        
        IoApi.Put(upToken, key, new java.io.ByteArrayInputStream(data), extra);
//	        IoApi.putFile(upToken, key, "D:\\data\\test2.jpg", extra);
	}
	
	public static void main(String[] args) throws Exception {
		String data = FileUtil.readFromFile("D:\\data\\test2.jpg", "Unicode");
		uploadPic("images", "mouse2.jpg", data.getBytes());
		System.out.println("end");
	}
}
