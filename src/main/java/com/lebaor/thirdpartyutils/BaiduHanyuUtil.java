package com.lebaor.thirdpartyutils;

import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lebaor.utils.FileUtil;
import com.lebaor.webutils.HttpClientUtil;

public class BaiduHanyuUtil {
	private static Pattern p = Pattern.compile("<a class=\"check\\-red\"[^>]+>([^<]+)</a>"+
			"\\s*</div>\\s*<div[^>]+>([^<]+)<", 
			Pattern.CASE_INSENSITIVE);
	
	public static BaiduCiyu[] zuci(String hanzi) throws Exception {
		String url = "http://hanyu.baidu.com/s?wd="+ URLEncoder.encode(hanzi, "utf-8") +"+%E7%BB%84%E8%AF%8D&from=poem";
		
		String html = HttpClientUtil.doGet(url);
		return parseHtml(html);
	}
	
	public static BaiduCiyu[] zuci(String[] hanzis) throws Exception {
		Random random = new Random();
		LinkedList<BaiduCiyu> list = new LinkedList<BaiduCiyu>();
		
		for (String hanzi : hanzis) {
			if (hanzi == null ||  hanzi.trim().length() == 0) continue;
			
			int n = random.nextInt(10000);
			Thread.sleep(n);
			
			try{
				BaiduCiyu[] ciyus = zuci(hanzi);
				for (BaiduCiyu cy : ciyus) {
					list.add(cy);
				}
			} catch(Exception e) {
				continue;
			}
		}
		return list.toArray(new BaiduCiyu[0]);
	}
	
	public static BaiduCiyu[] parseHtml(String html) {
		LinkedList<BaiduCiyu> list = new LinkedList<BaiduCiyu>();
		Matcher m = p.matcher(html);
		while (m.find()) {
			String ciyu = m.group(1).trim();
			String pinyin = m.groupCount() > 1 ? m.group(2).trim() : "[]";
			pinyin = pinyin.substring(1,pinyin.length() - 1);
			
			BaiduCiyu bc = new BaiduCiyu();
			bc.setCiyu(ciyu);
			bc.setPinyin(pinyin);
			list.add(bc);
		}
		return list.toArray(new BaiduCiyu[0]);
	}
	
	public static class BaiduCiyu {
		String ciyu;
		String pinyin;//用空格分隔
		
		public String toString() {
			return ciyu + " " + pinyin;
		}
		
		public String getCiyu() {
			return ciyu;
		}
		public void setCiyu(String ciyu) {
			this.ciyu = ciyu;
		}
		public String getPinyin() {
			return pinyin;
		}
		public void setPinyin(String pinyin) {
			this.pinyin = pinyin;
		}
	}
	
	public static void main(String[] args) throws Exception {
		BaiduCiyu[] ciyus = BaiduHanyuUtil.zuci("春");
//		BaiduCiyu[] ciyus = BaiduHanyuUtil.parseHtml(html);
		for (BaiduCiyu cy : ciyus) {
			System.out.println(cy.toString());
		}
		System.out.println(ciyus.length);
		System.out.println("end");
	}
}
