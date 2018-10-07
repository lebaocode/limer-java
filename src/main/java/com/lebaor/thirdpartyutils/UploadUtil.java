package com.lebaor.thirdpartyutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UploadUtil {
	private static String UPLOAD_PIC_DIR = "";
	private static String parseQiniuImgFileName(String url) {
		return null;//TODO
	}
	private static String parseLocalImgFileName(String url) {
		return null;//TODO
	}
	
	public static void deleteImg(String imgUrl) {
		//删除七牛存储的图片
		String qiniuFileName = parseQiniuImgFileName(imgUrl);
		QiniuUtil.deleteImg(qiniuFileName);
		
		//删除本地图片
		String localFileName = parseLocalImgFileName(imgUrl);
		new File(localFileName).delete();
	}
	
	public static void uploadPics(String base64, String fileName, String key) throws Exception {
		File filePath = new File(UPLOAD_PIC_DIR, fileName);
		byte[] buf = convertBase64ToBuf(base64);
		QiniuUtil.uploadPic(key, fileName, buf);
		writeToFile(buf, filePath);
	}

	private static byte[] convertBase64ToBuf(String data) throws Exception {
		int index = data.indexOf("base64,");
		String trueData = data;
		if (index != -1 && index < 30) {
			trueData = data.substring(index + "base64,".length());
		} 
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		byte[] buf = decoder.decodeBuffer(trueData);
//		for (int i = 0; i < buf.length; i++) {
//			if (buf[i] < 0) buf[i] += 256;
//		}
		return buf;
	}
	
	private static void writeToFile(byte[] buf, File f) throws Exception {
		FileOutputStream writer = null;
		try {
			writer = new FileOutputStream(f, false);
			writer.write(buf);
			writer.flush();

		} finally {
			if (writer != null) writer.close();
		}
		
	}
	
	// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
	private static String convertImgToBase64(String imgFilePath) {  
		byte[] data = null;  
		  
		// 读取图片字节数组  
		try {  
			InputStream in = new FileInputStream(imgFilePath);  
			data = new byte[in.available()];  
			in.read(data);  
			in.close();  
		} catch (IOException e) {  
			e.printStackTrace();  
		}  
		  
		// 对字节数组Base64编码  
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();  
		return encoder.encode(data);// 返回Base64编码过的字节数组字符串  
	}  
	
	public static void main(String[] args) throws Exception{
//		convertBase64ToImgFile(data, new File("d://test.jpg"));
//		fixImg("d://cce1e4cace4e7ee6b16d639e687502f5.jpg");
		
//		String s = encodeImg("d://good.jpg");
//		System.out.println(s.substring(0, 500));
//		System.out.println(s.substring(s.length() - 500, s.length()));
	}
	
	
	//未通过
//	public static void fixImg(String imgFilePath) throws Exception {
//		String data = convertImgToBase64(imgFilePath);
//		System.out.println(data.substring(0,200));
//		String s = "data:image/jpeg;base64,";
//		
//		String ds = data.substring(s.length());
//		
//		convertBase64ToImgFile( s + ds, new File(imgFilePath + ".fix.jpg"));
//	}
	
	private static final String data = "data:image/jpeg;base64,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";
}
