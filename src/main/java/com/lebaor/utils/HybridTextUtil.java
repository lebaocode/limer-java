package com.lebaor.utils;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lebaor.webutils.ViewUtils;

//文本中包含部分自定义符号，替换为html
public class HybridTextUtil {
	
	//html格式的处理
	public static String getHybridContentAbbr(String content) {
		if (content == null) return "";
		
		LinkedList<String> varList = new LinkedList<String>();
		String s = content;
		s = filterHtml(s, "<a href=", "</a>", varList);
		s = filterHtml(s, "<img src=", "</img>", varList);
		s = filterHtml(s, "<b>", "</b>", varList);
		s = ViewUtils.getHTMLValidText(s);
		s = s.replace("&nbsp;", " ");
		s = s.replace("<br/>", " ");
		int i = 0;
		for (String var : varList) {
			String replaced = "";
			if (var.startsWith("<a")) {
				int index = var.indexOf(">");
				replaced = var.substring(index + 1, var.length() - 4);
			} else if (var.startsWith("<img")) {
				replaced = " ";
			} else if (var.startsWith("<b>")) {
				replaced = var.substring(3, var.length() - 4);
			}
			s = s.replace("${lebao_param_"+ i +"}", replaced);
			i++;
		}
		s = s.replaceAll("\\s+", " ");
		s = s.trim();
		if (s.length() > 50) {
			return s.substring(0, 50) + "...";
		} else {
			return s;
		}
	}
	
	public static String handleHybridContent(String content){
		LinkedList<String> varList = new LinkedList<String>();
		String s = content;
		s = filterHtml(s, "<a href=", "</a>", varList);
		s = filterHtml(s, "<img src=", "</img>", varList);
		s = filterHtml(s, "<b>", "</b>", varList);
		
		s = ViewUtils.getHTMLValidText(s);
		int i = 0;
		for (String var : varList) {
			s = s.replace("${lebao_param_"+ i +"}", var);
			i++;
		}
		return s;
	}
	
	private static String filterHtml(String content, String prefix, String suffix, LinkedList<String> varList) {
		StringBuffer sb = new StringBuffer();
		
		String s = content;
		int lastPos = 0;
		int start = s.toLowerCase().indexOf(prefix);
		int end;
		
		while (start != -1) {
			end = s.toLowerCase().indexOf(suffix, start);
			if (end == -1) break;
			
			sb.append(s.substring(lastPos, start));
			sb.append("${lebao_param_"+ varList.size() +"}");
			
			String var = s.substring(start, end + suffix.length());
			varList.add(var.toLowerCase());
			
			lastPos = end + suffix.length();
			start = s.toLowerCase().indexOf(prefix, end);
		}
		
		sb.append(s.substring(lastPos));
		
		return sb.toString();
	}
	
	static Pattern imgP = Pattern.compile("<img src=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
	public static String[] parseImgUrls(String content) {
		LinkedList<String> list = new LinkedList<String>();
		
		Matcher m = imgP.matcher(content.toLowerCase());
		while (m.find()) {
			String url = m.group(1);
			if (url.trim().length() > 0) {
				list.add(url);
			}
		}
		return list.toArray(new String[0]);
	}


	public static String getFirstImgUrl(String content) {
		Matcher m = imgP.matcher(content);
		if (m.find()) {
			String url = m.group(1);
			return url;
		}
		return "";
	}
	
	private static void testUnit(String s, String expect, String real) {
		if (expect.equals(real)) return;
		System.out.println("ERROR\ncontent=" + s + "\n"
				+"expect:" + expect + "\n"
				+"real:" + real + "\n");
	}
	
	public static void main(String[] args) {
		String content;
		String real;
		String expect;
		String realAbbr;
		String expectAbbr;
		
		String[] imgUrls;
		
		content = "<html></html>";
		real = handleHybridContent(content);
		expect = "&lt;html&gt;&lt;/html&gt;";
		testUnit(content, expect, real);
		realAbbr = getHybridContentAbbr(content);
		expectAbbr = "&lt;html&gt;&lt;/html&gt;";
		testUnit(content, expectAbbr, realAbbr);
		
		content = "决定开房间打开链接发来<a href=\"http://www.a.cn\" target=\"_blank\">你好</a>大家开房间打开链接<a href=\"http://www.b.cn\" target=\"_blank\">你好</a>反馈了";
		real = handleHybridContent(content);
		expect = content;
		testUnit(content, expect, real);
		realAbbr = getHybridContentAbbr(content);
		expectAbbr = "决定开房间打开链接发来你好大家开房间打开链接你好反馈了";
		testUnit(content, expectAbbr, realAbbr);
		
		content = "决定开房间打开链接发来<img src=\"http://www.a.cn\" width=\"200px\"></img>大家开房间打开<img src=\"http://www.b.cn\" width=\"200px\"></img>链接反馈了";
		real = handleHybridContent(content);
		expect = content;
		testUnit(content, expect, real);
		realAbbr = getHybridContentAbbr(content);
		expectAbbr = "决定开房间打开链接发来 大家开房间打开 链接反馈了";
		testUnit(content, expectAbbr, realAbbr);
		
		imgUrls = parseImgUrls(content);
		testUnit(content, "[http://www.a.cn, http://www.b.cn]", java.util.Arrays.toString(imgUrls));
		//System.out.println(java.util.Arrays.toString(imgUrls));
		
		content = "决定开房间打开链接发来<b>哈哈</b>大家开房间打开<b>hoho</b>链接反馈了";
		real = handleHybridContent(content);
		expect = content;
		testUnit(content, expect, real);
		realAbbr = getHybridContentAbbr(content);
		expectAbbr = "决定开房间打开链接发来哈哈大家开房间打开hoho链接反馈了";
		testUnit(content, expectAbbr, realAbbr);
		
		
		System.out.println("end");
		
	}
}
