package com.lebaor.thirdpartyutils;

import java.io.File;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lebaor.utils.FileUtil;
import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;

public class DoubanUtil {
	public static String getBookInfoByIsbn(String isbn) {
		String json = HttpClientUtil.doGet("https://api.douban.com/v2/book/isbn/:" + isbn, new HashMap<String, String>());
		LogUtil.WEB_LOG.debug("DoubanUtil.getBookInfoByIsbn(" +isbn+"), return " 
				+ (json.length() > 0 ? json.substring(0, Math.min(json.length(), 100)): ""));
		return json;
	}
	
	
	public static String getBookInfoByDoubanId(String id) {
		String json = HttpClientUtil.doGet("https://api.douban.com/v2/book/" + id, new HashMap<String, String>());
		LogUtil.WEB_LOG.debug("DoubanUtil.getBookInfoByDoubanId(" +id+"), return " 
				+ (json.length() > 0 ? json.substring(0, Math.min(json.length(), 100)): ""));
		return json;
	}
	
	public static void crawlTag(String tag, int num) throws Exception {
		int pageNum = 20;
		String nobookDesc = "没有找到符合条件的图书";
		
		int start = 0;
		while (start < num) {
			Thread.sleep(1234);
			if (new File("douban_tag", tag+"_" + start).exists()) {
				System.out.println(start + ": SKIP");
				start+= pageNum;
				continue;
			}
			
			String url = "https://book.douban.com/tag/"+ URLEncoder.encode(tag, "utf-8") +"?start="+ start +"&type=T";
			String html = HttpClientUtil.doGet(url);
			if (html == null) {
				System.out.println("Empty Result: " + url);
				break;
			}
			
			if (html.indexOf(nobookDesc) != -1) {
				System.out.println(start + ": No Result.");
				break;
			}
			
			File destFile = new File("douban_tag", tag+"_" + start);
			FileUtil.writeToFile(html,  destFile.getAbsolutePath(), false);
			System.out.println(start + ": " + html.length());
			start += pageNum;
		}
	}
	
	static Pattern p = Pattern.compile("<a class=\"nbg\" href=\"https://book\\.douban\\.com/subject/(\\d+)/");
	/***
	 * 
	 * @param html
	 * @return 豆瓣bookId array,
	 */
	public static String[] parseBooksFromHtml(String html) {
		LinkedList<String> list = new LinkedList<String>();
		Matcher m = p.matcher(html);
		while (m.find()) {
			String id = m.group(1);
			list.add(id);
		}
		return list.toArray(new String[0]);
	}
	
	public static void main(String[] args) throws Exception {
//		crawlTag("", 1000);
//		String s = getBookInfoByIsbn("9787555214571");
//		System.out.println(s);
				
		File d = new File("douban_tag");
		int j = 0;
		for (File f : d.listFiles()) {
			
			if (f.getName().indexOf(".") != -1 ||  new File(f.getAbsolutePath() + ".json").exists()){
				System.out.println(f.getName() + " SKIP");
				continue;
			}
			
			String html = FileUtil.readFromFile(f.getAbsolutePath());
			
			String[] ids = parseBooksFromHtml(html);
			System.out.println(ids.length);
			StringBuffer sb = new StringBuffer();
			sb.append("[");
			int i = 0;
			for (String id : ids) {
				String json = getBookInfoByDoubanId(id);
				int tryNum = 0;
				while (json.indexOf("rate_limit_exceeded") != -1) {
					System.out.println("rate_limit_exceeded");
					Thread.sleep(60000*(tryNum+1));
					json = getBookInfoByDoubanId(id);
					tryNum++;
				}
				
				Thread.sleep(2139);
				sb.append("\n" +(i==0 ? "" : ", ")+ json);
				System.out.println(f.getName() + " " + id + " DONE");
				i++;
			}
			sb.append("\n]");
			FileUtil.writeToFile(sb.toString(), f.getAbsolutePath()+".json", false);
			
			j++;
		}
		
	}
}
