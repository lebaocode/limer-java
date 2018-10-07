package com.lebaor.thirdpartyutils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.lebaor.utils.FileUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.webutils.HttpClientUtil;

public class DictCnUtil {
	
	
	public static void crawl(String[] hanzis, String outputFileName) {
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		int i = 0;
		for (String w : hanzis) {
			if (w == null || w.trim().length() == 0) continue;
			String text = crawl(w);
			i++;
			if (text != null) {
				sb.append("\n");
				sb.append(text);
			} else {
				sb.append("\n");
				sb.append(w);
			}
			
			try {
				if ( i % 10 == 0) {
					FileUtil.writeToFile(sb.toString(), outputFileName, true);
					sb.delete(0, sb.length());
				}
			} catch (Exception e) {}
			
			try {
				Thread.sleep(r.nextInt(8) * 1000L + r.nextInt(1000));
			} catch (Exception e) {
				break;
			} 
		}
		
		try {
			if (sb.length() > 0) {
				FileUtil.writeToFile(sb.toString(), outputFileName, true);
				sb.delete(0, sb.length());
			}
		} catch (Exception e) {}
		
		
	}
	
	//只能是汉字
	public static String crawl(String hanzi) {
		System.out.println(TextUtil.formatTime(System.currentTimeMillis())  + " begin crawl " + hanzi);
		try {
			String url = "http://hanyu.dict.cn/" + URLEncoder.encode(hanzi, "utf-8");
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
			String html = HttpClientUtil.doPost(url, "", headers);
			String ret = analysis(html);
			if (ret != null) {
				return hanzi + "\t" + ret;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static Pattern PATTERN_HANZI = Pattern.compile(
			"<span[^>]*>拼&nbsp;&nbsp;音</span>((?:<span>[^<]*</span>)+)</p>\\s*"
			+ "<ul[^>]*>\\s*" 
			+ "<li[^>]*><span[^>]*>[^<]*</span>(?:<strong><a[^>]*>[^<]*</a></strong>)*</li>\\s*"
			+ "<li[^>]*><span[^>]*>[^<]*</span>(?:<strong><a[^>]*>[^<]*</a></strong>)*</li>\\s*"
			+ "</ul>\\s*"
			+ "<ul[^>]*>\\s*"
			+ "<li[^>]*><span[^>]*>部&nbsp;&nbsp;首</span><span>([^<]*)</span></li>\\s*" 
			+ "<li[^>]*><span[^>]*>笔&nbsp;&nbsp;画</span><span>(\\d*)[笔画部]?</span></li>\\s*"
			+ "<li[^>]*><span[^>]*>五&nbsp;&nbsp;笔</span><span>[^<]*</span></li>\\s*"
			+ "</ul>\\s*"
			+ "<ul[^>]*>\\s*"
			+ "<li[^>]*><span[^>]*>结&nbsp;&nbsp;构</span><span>([^<]*)</span></li>\\s*" 
			+ "<li[^>]*><span[^>]*>造字法</span><span>([^<]*)</span></li>"
			, 
			Pattern.CASE_INSENSITIVE);
	
	private static String analysis(String html) throws Exception {
		Matcher m = PATTERN_HANZI.matcher(html);
		if (m.find()) {
			String s = m.group(1);
			s = s.replaceAll("<span>", " ").replaceAll("</span>", " ").trim();
			String[] pinyins = s.split(" +");
			
//			System.out.println(pinyin);
			
			String bushou = m.group(2).trim();
			if (bushou.endsWith("部")) bushou = bushou.substring(0, bushou.length() - 1);
//			System.out.println(bushou);
			
			String bihua = m.group(3).trim();
//			System.out.println(bihua);
			
			String jiegou = m.group(4).trim();
//			System.out.println(jiegou);
			
			String zaozifa = m.group(5).trim();
//			System.out.println(zaozifa);
			
			return java.util.Arrays.toString(pinyins) + "\t" + bushou + "\t" + bihua + "\t"+ jiegou + "\t" + zaozifa;
		}
		
		return null;
	}
	
	private static void crawlDictCn() throws Exception {
		HashSet<String> existHanzis = new HashSet<String>();
		String[] lines = FileUtil.readFromFile("hanzi_info.txt", "utf-8").split("\n+");
		for (String line : lines) {
			if (line.trim().length() == 0) continue;
			String[] arr  = line.split("\t");
			if (arr.length == 1) continue;
			existHanzis.add(arr[0].trim());
		}
		
		//取10个没抓取的汉字
		int len = 100;
		String text = FileUtil.readFromFile("all_hanzi_7000.txt", "utf-8");
		char[] allHanzis = text.toCharArray();
		String[] hanziArr = new String[len];
		int i = 0; 
		for (char hanzi : allHanzis) {
			String word = hanzi + "";
			if (word.trim().length() == 0) continue;
			
			if (existHanzis.contains(word)) continue;
			
			hanziArr[i] = word;
			i++;
			if (i >= len) break;
		}
		
		crawl(hanziArr, "hanzi_info.txt");
	}
	
	public static void main(String[] args) throws Exception {
//		String s = analysis(html5);
//		System.out.println(s);
//		crawl("觎");
		
		String[] lines = FileUtil.readFromFile("hanzi_info.txt", "utf-8").split("\n+");
		int n = 0;
		for (String line : lines) {
			if (line.trim().length() == 0) continue;
			String[] arr = line.split("\t");
			if (arr.length > 1 && arr.length < 6) {
				System.out.println(line);
				n++;
			}
		}
		System.out.println(n);
		
		
//		crawlDictCn();
		System.out.println("end");
	}
	private static String html5 = "<div style=\"width: 678px;\">\n" + 
			"							<p class=\"hz_pinyin clearFix\"><span class=\"zh_title\">拼&nbsp;&nbsp;音</span><span>làng</span><span>liáng</span></p>\n" + 
			"						<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title hz_wait\">繁&nbsp;&nbsp;体</span></li>\n" + 
			"				<li style=\"width:60%;\"><span class=\"zh_title hz_wait\">异&nbsp;&nbsp;体</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">部&nbsp;&nbsp;首</span><span>艹</span></li>\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title hz_wait\">笔&nbsp;&nbsp;画</span><span></span></li>\n" + 
			"				<li><span class=\"zh_title \">五&nbsp;&nbsp;笔</span><span>AYVE</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title hz_wait\">结&nbsp;&nbsp;构</span><span></span></li>\n" + 
			"				<li><span class=\"zh_title hz_wait\">造字法</span><span></span></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"";
	
	private static String html4 = "\n" + 
			"<!DOCTYPE HTML>\n" + 
			"<html>\n" + 
			"	<head>\n" + 
			"		<meta name=\"renderer\" content=\"webkit\">\n" + 
			"				<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7\" />\n" + 
			"				<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" + 
			"		<title>觎是什么意思_觎汉语解释_觎的例句 - 海词汉语</title>\n" + 
			"	\n" + 
			"		<meta name=\"keywords\" content=\"觎,觎是什么意思,觎的解释,觎的例句\" />\n" + 
			"		<meta name=\"description\" content=\"海词汉语频道为广大中文用户提供觎是什么意思、觎的解释、觎的例句，更多觎汉语解释到海词汉语。\" />\n" + 
			"		<meta name=\"author\" content=\"海词词典\" />\n" + 
			"		<link rel=\"canonical\" href=\"http://hanyu.dict.cn/觎\" />\n" + 
			"		<link rel=\"icon\" href=\"http://dict.cn/favicon.ico\" type=\"/image/x-icon\" />\n" + 
			"		<link rel=\"shortcut icon\" href=\"http://dict.cn/favicon.ico\" type=\"/image/x-icon\" />\n" + 
			"		<link href=\"http://i1.haidii.com/v/1484140449/i1/css/base.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" + 
			"		<link href=\"http://i1.haidii.com/v/1484140452/i1/css/obase.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" + 
			"		<script>var cur_dict = 'hanyu';var i1_home='http://i1.haidii.com';var xuehai_home='http://xuehai.cn';var passport_home='http://passport.dict.cn';</script>\n" + 
			"		<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1408420485/i1/js/jquery-1.8.0.min.js\"></script>\n" + 
			"		\n" + 
			"		        <script>var crumb='', dict_homepath = 'http://dict.cn', hc_jspath = 'http://i1.haidii.com/v/1484140447/i1/js/hc3/hc.min.js',use_bingTrans='', multi_langs = '';</script>\n" + 
			"	</head>\n" + 
			"	<body>\n" + 
			"<object style=\"position:absolute;top:-1000%;width:1px;height:1px;opacity:0;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);\" width=\"1\" height=\"1\" id=\"daudio\" type=\"application/x-shockwave-flash\" data=\"http://dict.cn/player/player.swf\">\n" + 
			"	<param name=\"movie\" value=\"http://dict.cn/player/player.swf\">\n" + 
			"	<param name=\"quality\" value=\"high\" />\n" + 
			"	<param name=\"bgcolor\" value=\"#ffffff\" />\n" + 
			"	<param name=\"allowScriptAccess\" value=\"always\" />\n" + 
			"	<param name=\"allowFullScreen\" value=\"true\" />\n" + 
			"	<param name=\"hasPriority\" value=\"true\" />\n" + 
			"	<param name=\"FlashVars\" value=\"volume=100\" />\n" + 
			"	<embed style=\"position:absolute;top:-1000%;width:1px;height:1px;-khtml-opacity:0;-moz-opacity:0;opacity:0;\" src=\"http://dict.cn/player/player.swf\" allowscriptaccess=\"always\" allowfullscreen=\"true\" quality=\"high\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\"></embed>\n" + 
			"</object>\n" + 
			"<div id=\"header\">\n" + 
			"	<div class=\"nav\">\n" + 
			"	<div class=\"links\">\n" + 
			"		<a class=cur href=\"http://dict.cn\">海词</a>\n" + 
			"		<a  onclick=\"cnewClose(this);\"   href=\"http://cidian.dict.cn/center.html?iref=dict-header-center\">权威词典</a>\n" + 
			"				<div id=\"cnewDiv\" style=\"position: relative;float:left;line-height: 40px;height:40px;\"><div style=\"position: absolute;top:-10px;left:-23px;\"><img style=\"vertical-align: middle\" src=\"http://i1.haidii.com/v/1420610131/i1/cidian/images/new.png\" /></div></div>\n" + 
			"		<script>function cnewClose(obj){var Days = 3600;var exp  = new Date();exp.setTime(exp.getTime() + Days*24*60*60*1000);document.cookie = 'cnew' + \"=\"+ escape(1) +\";expires=\"+ exp.toGMTString()+\";path=/;domain=.dict.cn\";document.cookie = 'cnewt' + \"=\"+ escape(1420560000) +\";expires=\"+ exp.toGMTString()+\"path=/;domain=.dict.cn\"; $(\"#cnewDiv\").hide();}</script>\n" + 
			"				<a  href=\"http://fanyi.dict.cn\">翻译</a>\n" + 
			"				<style type=\"text/css\">#header .links .top-download a{width:140px;height:40px;line-height:normal;background:url(http://i1.haidii.com/v/1408420472/i1/images/top-download-icon2.png) 0 0 no-repeat}#header .links .top-download a:hover{background:url(http://i1.haidii.com/v/1408420472/i1/images/top-download-icon2.png) 0 -40px no-repeat}</style>\n" + 
			"		<div class=\"top-download\"><a href=\"http://cidian.dict.cn/home.html?iref=dict-header-button\"></a></div>\n" + 
			"	</div>\n" + 
			"	<div class=\"login\">\n" + 
			"		<em action=\"feedback\" onclick=\"feedBackForm(this);\" title=\"意见反馈\">意见反馈</em>\n" + 
			"		<i>|</i>\n" + 
			"					<a title=\"注册\" href=\"http://passport.dict.cn/register\">注册</a><i>|</i><a title=\"登录\" href=\"http://passport.dict.cn/login\">登录</a>\n" + 
			"									</div>\n" + 
			"</div>\n" + 
			"<script>var pagetype = '';</script>\n" + 
			"	<script type='text/javascript'>\n" + 
			"var googletag = googletag || {};\n" + 
			"googletag.cmd = googletag.cmd || [];\n" + 
			"(function() {\n" + 
			"var gads = document.createElement('script');\n" + 
			"gads.async = true;\n" + 
			"gads.type = 'text/javascript';\n" + 
			"var useSSL = 'https:' == document.location.protocol;\n" + 
			"gads.src = (useSSL ? 'https:' : 'http:') + \n" + 
			"'//www.googletagservices.com/tag/js/gpt.js';\n" + 
			"var node = document.getElementsByTagName('script')[0];\n" + 
			"node.parentNode.insertBefore(gads, node);\n" + 
			"})();\n" + 
			"</script>\n" + 
			"\n" + 
			"<script type='text/javascript'>\n" + 
			"googletag.cmd.push(function() {\n" + 
			"googletag.defineSlot('/146434140/search_tab', [200, 48], 'div-gpt-ad-1422600134018-10').addService(googletag.pubads());\n" + 
			"googletag.pubads().enableSingleRequest();\n" + 
			"googletag.enableServices();\n" + 
			"});\n" + 
			"</script><div class=\"top\">\n" + 
			"			<a href=\"/\" class=\"logo\"><img alt=\"海词词典\" titile=\"海词词典\" src=\"http://i1.haidii.com/v/1408420472/i1/images/hanyu_search_logo.png\" /></a>\n" + 
			"		<div class=\"search\">\n" + 
			"		<div class=\"search_nav\">\n" + 
			"		<a  href=\"http://dict.cn\" data-param=\"zh,en,other\"><b>英　汉</b></a>\n" + 
			"    <a  href=\"http://dict.cn/kr/\" data-param=\"zh,en,other\"><b>韩　汉</b></a>\n" + 
			"    \n" + 
			"							\n" + 
			"	<a href=\"http://dict.cn/jp/\" data-param=\"zh,en,other\"><b>日　汉</b></a>    <a class=\"sbox_morebtn searchnav-morea\" href=\"javascript:;\">更多</a>\n" + 
			"    \n" + 
			"	<em>|</em>\n" + 
			"    <a  href=\"http://juhai.dict.cn\" data-param=\"zh,en\">句海</a>\n" + 
			"    <a class=\"cur\" style=\"background-position-x:-77px\" href=\"http://hanyu.dict.cn\" data-param=\"zh\">汉语</a>\n" + 
			"    <em>|</em>\n" + 
			"	<a  href=\"http://shh.dict.cn\" data-param=\"zh\">上海话</a>\n" + 
			"	<a  href=\"http://gdh.dict.cn\" data-param=\"zh\">广东话</a>\n" + 
			"    <a href=\"http://abbr.dict.cn\" data-param=\"zh,en\">缩略语</a>\n" + 
			"    <a href=\"http://ename.dict.cn\" data-param=\"zh,en\">人名</a>\n" + 
			"</div>\n" + 
			"<div class=\"sbox_more_wrap\">\n" + 
			"    <div class=\"sbox_more\">\n" + 
			"        <div class=\"sbox_marrow\"></div>\n" + 
			"        <div class=\"sbox_mlangs\">\n" + 
			"            <a style=\"display:none\" href=\"http://dict.cn/jp/\" data-param=\"zh,en,other\"><b>日　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/fr/\" data-param=\"zh,en,other\"><b>法　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/de/\" data-param=\"zh,en,other\"><b>德　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/es/\" data-param=\"zh,en,other\"><b>西　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/it/\" data-param=\"zh,en,other\"><b>意　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/ru/\" data-param=\"zh,en,other\"><b>俄　汉</b></a>\n" + 
			"        </div>\n" + 
			"    </div>\n" + 
			"</div>\n" + 
			"    \n" + 
			"<div class=\"search_box\">\n" + 
			"	<form action=\"http://hanyu.dict.cn/search.php\" method=\"get\">\n" + 
			"	<input type=\"text\" id=\"q\" class=\"search_input\" name=\"q\" value=\"觎\" autocomplete=\"off\" placeholder=\"请输入汉语字词\" x-webkit-speech ><input type=\"submit\" class=\"search_submit\" id=\"search\" title=\"查词\" value=\"\">\n" + 
			"	</form>\n" + 
			"</div>	</div>\n" + 
			"	<div class=\"spread\" style=\"padding-left:10px;\">\n" + 
			"		<!-- search_tab -->\n" + 
			"		<div id='div-gpt-ad-1456898554209-4' style='width:200px; height:48px;'>		<script type='text/javascript'>\n" + 
			"		googletag.cmd.push(function() { googletag.display('div-gpt-ad-1456898554209-4'); });\n" + 
			"		</script>		</div>\n" + 
			"\n" + 
			"	</div>\n" + 
			"</div>\n" + 
			"</div>\n" + 
			"<script type='text/javascript'>\n" + 
			"var googletag = googletag || {};\n" + 
			"googletag.cmd = googletag.cmd || [];\n" + 
			"(function() {\n" + 
			"var gads = document.createElement('script');\n" + 
			"gads.async = true;\n" + 
			"gads.type = 'text/javascript';\n" + 
			"var useSSL = 'https:' == document.location.protocol;\n" + 
			"gads.src = (useSSL ? 'https:' : 'http:') + \n" + 
			"'//www.googletagservices.com/tag/js/gpt.js';\n" + 
			"var node = document.getElementsByTagName('script')[0];\n" + 
			"node.parentNode.insertBefore(gads, node);\n" + 
			"})();\n" + 
			"</script>\n" + 
			"\n" + 
			"<script type='text/javascript'>\n" + 
			"googletag.cmd.push(function() {\n" + 
			"googletag.defineSlot('/146434140/hanyu_topbanner', [728, 90], 'div-gpt-ad-1422601792026-0').addService(googletag.pubads());\n" + 
			"googletag.defineSlot('/146434140/hanyu_dbanner', [728, 90], 'div-gpt-ad-1422601792026-1').addService(googletag.pubads());\n" + 
			"googletag.pubads().enableSingleRequest();\n" + 
			"googletag.enableServices();\n" + 
			"});\n" + 
			"</script>\n" + 
			"<div id=\"main\">\n" + 
			"	<div class=\"mslide\">\n" + 
			"		<div class=\"m_nav\">\n" + 
			"			<ul>\n" + 
			"												<li><a href=\"http://scb.dict.cn/?ref=left&scb\" target=\"_blank\" class=\"scb-d\">我的生词本</a></li>\n" + 
			"				<li><a href=\"http://bdc.dict.cn/?ref=left&bdc\" target=\"_blank\" class=\"bdc-d\">在线背单词</a></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"				<dl class=\"mslide_udbox\">\n" + 
			"			<dt><a href=\"javascript:;\">查词历史»</a></dt>\n" + 
			"			<dd>\n" + 
			"				<ul>\n" + 
			"															<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E8%A7%8E\">1.&nbsp;\n" + 
			"														觎													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E7%AB%B9%E5%AD%90\">2.&nbsp;\n" + 
			"														竹子													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%95%8A\">3.&nbsp;\n" + 
			"														啊													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%90%95\">4.&nbsp;\n" + 
			"														吕													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%8F%82\">5.&nbsp;\n" + 
			"														参													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E7%89%9B\">6.&nbsp;\n" + 
			"														牛													</a>\n" + 
			"					</li>\n" + 
			"														</ul>\n" + 
			"			</dd>\n" + 
			"		</dl>\n" + 
			"			</div>\n" + 
			"	<div class=\"m\" style=\"width:819px;\">\n" + 
			"		<div style=\"margin-bottom:15px;\">\n" + 
			"			<!-- hanyu_topbanner -->\n" + 
			"			<div id='div-gpt-ad-1422601792026-0' style='width:728px; height:90px;'>\n" + 
			"				<script type='text/javascript'>\n" + 
			"				googletag.cmd.push(function() { googletag.display('div-gpt-ad-1422601792026-0'); });\n" + 
			"				</script>  \n" + 
			"			</div>\n" + 
			"		</div>\n" + 
			"		<!--功能设置-->\n" + 
			"<div class=\"tbcont_0 popup1\" style=\"display:none;\">\n" + 
			"	<div>\n" + 
			"		<h5 class=\"fy\">发音按钮选项</h5>\n" + 
			"		<ul class=\"popup01 clearFix\">\n" + 
			"			<li><input type=\"radio\" name=\"sound-opt\" onclick=\"_dict_config.ss = (_dict_config.ss >>1)<<1;saveConfig();\"/>鼠标悬停即发声音</li>\n" + 
			"			<li><input type=\"radio\" name=\"sound-opt\" onclick=\"_dict_config.ss |= 1;saveConfig();\"/>需要点击才发音</li>\n" + 
			"			<li><input type=\"checkbox\" onclick=\"if(this.checked)_dict_config.ss |= 2;else _dict_config.ss &= (~2);saveConfig();\"/>加载页面即发音</li>\n" + 
			"		</ul>\n" + 
			"	</div>\n" + 
			"	<div style=\"border-top:1px solid #d3e1e3;padding-top:10px;\">\n" + 
			"		<h5 class=\"hc\">鼠标划词</h5>\n" + 
			"		<ul class=\"popup01 clearFix\">\n" + 
			"			<li><input type=\"radio\" name=\"huaci-opt\" onclick=\"huaciSwitch(true)\"/>开启</li>\n" + 
			"			<li><input type=\"radio\" name=\"huaci-opt\" onclick=\"huaciSwitch(false)\"/>关闭</li>\n" + 
			"		</ul>\n" + 
			"		<a href=\"###\" class=\"close icon18\">X</a>					</div>\n" + 
			"</div>		\n" + 
			"				<!--bottom-right-->\n" + 
			"<div id=\"cy\" class=\"bottom-right\">\n" + 
			"		<!--字的释义-->\n" + 
			"	<div class=\"t-bgs clearFix\" style=\"padding-right:0;width:819px;\">\n" + 
			"		<h1 id=\"word-key\" class=\"cn-left cn-bg\" seg=\"\" >\n" + 
			"							觎					</h1>\n" + 
			"		<div style=\"width: 678px;\">\n" + 
			"							<p class=\"hz_pinyin clearFix\"><span class=\"zh_title\">拼&nbsp;&nbsp;音</span><span>yú</span></p>\n" + 
			"						<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">繁&nbsp;&nbsp;体</span><strong><a href=\"http://hanyu.dict.cn/覦\">覦</a></strong></li>\n" + 
			"				<li style=\"width:60%;\"><span class=\"zh_title hz_wait\">异&nbsp;&nbsp;体</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">部&nbsp;&nbsp;首</span><span>见</span></li>\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">笔&nbsp;&nbsp;画</span><span>13</span></li>\n" + 
			"				<li><span class=\"zh_title \">五&nbsp;&nbsp;笔</span><span>wgeq</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title hz_wait\">结&nbsp;&nbsp;构</span><span></span></li>\n" + 
			"				<li><span class=\"zh_title hz_wait\">造字法</span><span></span></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"		<span class=\"seach-right st\"></span>\n" + 
			"	</div>\n" + 
			"	<!--tab-切换-字的释义--->\n" + 
			"	<div class=\"tab\" style=\"padding-top:15px;\">\n" + 
			"				<h6 class=\"title\">\n" + 
			"			<span class=\" current\">现代汉语</span>\n" + 
			"		</h6>\n" + 
			"		<div class=\"tabcontent block\">\n" + 
			"							<div id=\"ly\" class=\"cont-1\">\n" + 
			"					<ul class=\"L-L L-L2\">\n" + 
			"													<li style=\"padding:0;\">\n" + 
			"								<h2>觎<span class=\"duyin\">yú																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_3593_0\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em><s></s>&nbsp;[动]</em>\n" + 
			"																																																																																	<p><span>企求；妄想得到：<em class=\"hz_sy\">觊～｜睥～｜窥～江左</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[动]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>企求；妄想得到：觊～｜睥～｜窥～江左:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"											</ul>\n" + 
			"				</div>\n" + 
			"					</div>\n" + 
			"		<div class=\"tabcontent \">\n" + 
			"					</div>\n" + 
			"	</div>\n" + 
			"	</div>\n" + 
			"\n" + 
			"	\n" + 
			"					\n" + 
			"			\n" + 
			"				<div style=\"margin-top:80px;\">\n" + 
			"			<!--分享微博-->\n" + 
			"			<div class=\"sc-fx\" id=\"page-share\">\n" + 
			"				<a onclick=\"addFavorite(location.href, document.title);\" href=\"###\">收藏</a>|<span>分享到：</span>\n" + 
			"				<a href=\"javascript:window.open('http://share.renren.com/share/buttonshare.do?link='+encodeURIComponent(document.location.href)+'&amp;title='+encodeURIComponent(document.title));void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"renren fx\" title=\"分享到人人网\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://www.kaixin001.com/repaste/share.php?rtitle='+encodeURIComponent(document.title)+'&amp;rurl='+encodeURIComponent(document.location.href)+'&amp;rcontent=');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"kaixin fx\" title=\"分享到开心网\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://v.t.sina.com.cn/share/share.php?title='+encodeURIComponent(document.title)+'&amp;url='+encodeURIComponent(location.href)+'&amp;source=dict.cn','_blank','width=615,height=505');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"sina fx\" title=\"分享到新浪微博\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://t.163.com/article/user/checkLogin.do?link=http://news.163.com/&amp;source=dict.cn&amp;info='+encodeURIComponent(document.title)+' '+encodeURIComponent(location.href),'_blank','width=510,height=300');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"wangyi fx\" title=\"分享到网易微博\"></a>\n" + 
			"				<span style=\"position:absolute; right:60px;\"><a style=\"color:#326598;\" href=\"http://hanyu.dict.cn/more.php\">《海词汉语词典》 编者的话</a></span>\n" + 
			"			</div>\n" + 
			"			<div>\n" + 
			"				<!-- hanyu_dbanner -->\n" + 
			"				<div id='div-gpt-ad-1422601792026-1' style='width:728px; height:90px;'>\n" + 
			"					<script type='text/javascript'>\n" + 
			"					googletag.cmd.push(function() { googletag.display('div-gpt-ad-1422601792026-1'); });\n" + 
			"					</script>\n" + 
			"				</div>\n" + 
			"			</div>\n" + 
			"		</div>\n" + 
			"	</div>\n" + 
			"	<div class=\"cl\"></div>\n" + 
			"</div>\n" + 
			"<script type=\"text/javascript\" id=\"data-js\">\n" + 
			"	var $dict_id    = \"3593\";\n" + 
			"	var $dict_query = \"\\u89ce\";\n" + 
			"	var $dict_dict  = \"han\";\n" + 
			"</script>\n" + 
			"<script type=\"text/javascript\" src=\"http://i1.haidii.com/i1/js/external.js\"></script>\n" + 
			"<div id=\"footer\">\n" + 
			"    <p><a href=\"http://about.dict.cn/introduce\" ref=\"nofollow\">关于海词</a> - <a href=\"http://about.dict.cn/copyrightstatement?cur=1\" ref=\"nofollow\">版权声明</a> - <a href=\"http://about.dict.cn/contact\" ref=\"nofollow\">联系海词</a> - <a target=\"_blank\" href=\"http://dict.cn/dir/\">星级词汇</a> - <a target=\"_blank\" href=\"http://dict.cn/dir/ceindex.html\">汉字列表</a> - <a target=\"_blank\" href=\"http://hr.dict.cn\" ref=\"nofollow\">招贤纳士</a></p>\n" + 
			"	<p>&copy;2003 - 2017 <a href=\"http://dict.cn/\">海词词典</a>(Dict.CN) - 自 2003 年开始服务 &nbsp;<a target=\"_blank\" href=\"http://www.miitbeian.gov.cn\" ref=\"nofollow\">沪ICP备08018881号</a>&nbsp;&nbsp;<a target=\"_blank\" href=\"http://www.sgs.gov.cn/lz/licenseLink.do?method=licenceView&amp;entyId=20120601170952752\" style=\"text-decoration:none;background-color:white;\" ref=\"nofollow\"><img border=\"0\" src=\"http://i1.haidii.com/i1/images/gs_icon.gif\"></a> 		 	<a target=\"_blank\" href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=31011502000490\" style=\"text-decoration:none;background-color:white;\"  ref=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/beian.png\" /><span style=\"display:none; color:#939393;\">沪公网安备 31011502000490号</span></a>\n" + 
			"		 </p>\n" + 
			"	<p style=\"text-align: center;margin-top:10px;\"><a href=\"http://m.dict.cn\" target=\"_blank\">海词词典手机移动站</a></p>\n" + 
			"	</div>\n" + 
			"<script>var langt='';</script>\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"	<script type=\"text/javascript\">\n" + 
			"	var cur_dict = 'hanyu', i1_home='http://i1.haidii.com', xuehai_home='http://xuehai.cn', passport_home='http://passport.dict.cn', $dict_id = null, $dict_query = \"\\u89ce\", $dict_dict  = \"han\", $user_id = 0, $dict_ver=1486623708;\n" + 
			"	</script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/i1/js/ddialog/ddialog.1.0.0.min.js\" crossorigin></script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1481554274/i1/js/inputPrompt.min.js\" crossorigin></script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1481554264/i1/js/base.min.js\" crossorigin></script>\n" + 
			"	<div style=\"display:none;\">\n" + 
			"		<script type=\"text/javascript\">\n" + 
			"		var _bdhmProtocol = ((\"https:\" == document.location.protocol) ? \" https://\" : \" http://\");\n" + 
			"		document.write(unescape(\"%3Cscript src='\" + _bdhmProtocol + \"hm.baidu.com/h.js%3F8fd7837425ffd5a7fb88d32ea7060960' type='text/javascript'%3E%3C/script%3E\"));\n" + 
			"		</script>\n" + 
			"	</div>\n" + 
			"	<script type=\"text/javascript\">\n" + 
			"	var _gaq = _gaq || [];\n" + 
			"	_gaq.push(['_setAccount', 'UA-138041-2']);\n" + 
			"	_gaq.push(['_setDomainName', 'dict.cn']);\n" + 
			"	_gaq.push(['_trackPageview']);\n" + 
			"	(function() {\n" + 
			"		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" + 
			"		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" + 
			"		var s  = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" + 
			"	})();\n" + 
			"	</script>\n" + 
			"\n" + 
			"<script type=\"text/javascript\">\n" + 
			"window.onerror = function(errorMsg, url, lineNumber, column, errorObj){\n" + 
			"	$.getScript('http://dict-log.cn-hangzhou.log.aliyuncs.com/logstores/jsreport/track?APIVersion=0.6.0&call=error&ver=' + ($dict_ver || '') + '&url=' + encodeURIComponent(url)+ '&line=' + lineNumber + '&emsg=' + encodeURIComponent(errorMsg) + '&page='+ _href \n" + 
			"			+ '&agent=' + encodeURIComponent(navigator.userAgent) + '&column=' + encodeURIComponent(column) + '&StackTrace=' + encodeURIComponent(errorObj));\n" + 
			"	return true;\n" + 
			"};\n" + 
			"</script>\n" + 
			"</body>\n" + 
			"</html>\n" + 
			"";
	
	private static String html3 = "\n" + 
			"<!DOCTYPE HTML>\n" + 
			"<html>\n" + 
			"	<head>\n" + 
			"		<meta name=\"renderer\" content=\"webkit\">\n" + 
			"				<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7\" />\n" + 
			"				<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" + 
			"		<title>啊是什么意思_啊汉语解释_啊的例句 - 海词汉语</title>\n" + 
			"	\n" + 
			"		<meta name=\"keywords\" content=\"啊,啊是什么意思,啊的解释,啊的例句\" />\n" + 
			"		<meta name=\"description\" content=\"海词汉语频道为广大中文用户提供啊是什么意思、啊的解释、啊的例句，更多啊汉语解释到海词汉语。\" />\n" + 
			"		<meta name=\"author\" content=\"海词词典\" />\n" + 
			"		<link rel=\"canonical\" href=\"http://hanyu.dict.cn/啊\" />\n" + 
			"		<link rel=\"icon\" href=\"http://dict.cn/favicon.ico\" type=\"/image/x-icon\" />\n" + 
			"		<link rel=\"shortcut icon\" href=\"http://dict.cn/favicon.ico\" type=\"/image/x-icon\" />\n" + 
			"		<link href=\"http://i1.haidii.com/v/1484140449/i1/css/base.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" + 
			"		<link href=\"http://i1.haidii.com/v/1484140452/i1/css/obase.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" + 
			"		<script>var cur_dict = 'hanyu';var i1_home='http://i1.haidii.com';var xuehai_home='http://xuehai.cn';var passport_home='http://passport.dict.cn';</script>\n" + 
			"		<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1408420485/i1/js/jquery-1.8.0.min.js\"></script>\n" + 
			"		\n" + 
			"		        <script>var crumb='', dict_homepath = 'http://dict.cn', hc_jspath = 'http://i1.haidii.com/v/1484140447/i1/js/hc3/hc.min.js',use_bingTrans='', multi_langs = '';</script>\n" + 
			"	</head>\n" + 
			"	<body>\n" + 
			"<object style=\"position:absolute;top:-1000%;width:1px;height:1px;opacity:0;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);\" width=\"1\" height=\"1\" id=\"daudio\" type=\"application/x-shockwave-flash\" data=\"http://dict.cn/player/player.swf\">\n" + 
			"	<param name=\"movie\" value=\"http://dict.cn/player/player.swf\">\n" + 
			"	<param name=\"quality\" value=\"high\" />\n" + 
			"	<param name=\"bgcolor\" value=\"#ffffff\" />\n" + 
			"	<param name=\"allowScriptAccess\" value=\"always\" />\n" + 
			"	<param name=\"allowFullScreen\" value=\"true\" />\n" + 
			"	<param name=\"hasPriority\" value=\"true\" />\n" + 
			"	<param name=\"FlashVars\" value=\"volume=100\" />\n" + 
			"	<embed style=\"position:absolute;top:-1000%;width:1px;height:1px;-khtml-opacity:0;-moz-opacity:0;opacity:0;\" src=\"http://dict.cn/player/player.swf\" allowscriptaccess=\"always\" allowfullscreen=\"true\" quality=\"high\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\"></embed>\n" + 
			"</object>\n" + 
			"<div id=\"header\">\n" + 
			"	<div class=\"nav\">\n" + 
			"	<div class=\"links\">\n" + 
			"		<a class=cur href=\"http://dict.cn\">海词</a>\n" + 
			"		<a  onclick=\"cnewClose(this);\"   href=\"http://cidian.dict.cn/center.html?iref=dict-header-center\">权威词典</a>\n" + 
			"				<div id=\"cnewDiv\" style=\"position: relative;float:left;line-height: 40px;height:40px;\"><div style=\"position: absolute;top:-10px;left:-23px;\"><img style=\"vertical-align: middle\" src=\"http://i1.haidii.com/v/1420610131/i1/cidian/images/new.png\" /></div></div>\n" + 
			"		<script>function cnewClose(obj){var Days = 3600;var exp  = new Date();exp.setTime(exp.getTime() + Days*24*60*60*1000);document.cookie = 'cnew' + \"=\"+ escape(1) +\";expires=\"+ exp.toGMTString()+\";path=/;domain=.dict.cn\";document.cookie = 'cnewt' + \"=\"+ escape(1420560000) +\";expires=\"+ exp.toGMTString()+\"path=/;domain=.dict.cn\"; $(\"#cnewDiv\").hide();}</script>\n" + 
			"				<a  href=\"http://fanyi.dict.cn\">翻译</a>\n" + 
			"				<style type=\"text/css\">#header .links .top-download a{width:140px;height:40px;line-height:normal;background:url(http://i1.haidii.com/v/1408420472/i1/images/top-download-icon2.png) 0 0 no-repeat}#header .links .top-download a:hover{background:url(http://i1.haidii.com/v/1408420472/i1/images/top-download-icon2.png) 0 -40px no-repeat}</style>\n" + 
			"		<div class=\"top-download\"><a href=\"http://cidian.dict.cn/home.html?iref=dict-header-button\"></a></div>\n" + 
			"	</div>\n" + 
			"	<div class=\"login\">\n" + 
			"		<em action=\"feedback\" onclick=\"feedBackForm(this);\" title=\"意见反馈\">意见反馈</em>\n" + 
			"		<i>|</i>\n" + 
			"					<a title=\"注册\" href=\"http://passport.dict.cn/register\">注册</a><i>|</i><a title=\"登录\" href=\"http://passport.dict.cn/login\">登录</a>\n" + 
			"									</div>\n" + 
			"</div>\n" + 
			"<script>var pagetype = '';</script>\n" + 
			"	<script type='text/javascript'>\n" + 
			"var googletag = googletag || {};\n" + 
			"googletag.cmd = googletag.cmd || [];\n" + 
			"(function() {\n" + 
			"var gads = document.createElement('script');\n" + 
			"gads.async = true;\n" + 
			"gads.type = 'text/javascript';\n" + 
			"var useSSL = 'https:' == document.location.protocol;\n" + 
			"gads.src = (useSSL ? 'https:' : 'http:') + \n" + 
			"'//www.googletagservices.com/tag/js/gpt.js';\n" + 
			"var node = document.getElementsByTagName('script')[0];\n" + 
			"node.parentNode.insertBefore(gads, node);\n" + 
			"})();\n" + 
			"</script>\n" + 
			"\n" + 
			"<script type='text/javascript'>\n" + 
			"googletag.cmd.push(function() {\n" + 
			"googletag.defineSlot('/146434140/search_tab', [200, 48], 'div-gpt-ad-1422600134018-10').addService(googletag.pubads());\n" + 
			"googletag.pubads().enableSingleRequest();\n" + 
			"googletag.enableServices();\n" + 
			"});\n" + 
			"</script><div class=\"top\">\n" + 
			"			<a href=\"/\" class=\"logo\"><img alt=\"海词词典\" titile=\"海词词典\" src=\"http://i1.haidii.com/v/1408420472/i1/images/hanyu_search_logo.png\" /></a>\n" + 
			"		<div class=\"search\">\n" + 
			"		<div class=\"search_nav\">\n" + 
			"		<a  href=\"http://dict.cn\" data-param=\"zh,en,other\"><b>英　汉</b></a>\n" + 
			"    <a  href=\"http://dict.cn/kr/\" data-param=\"zh,en,other\"><b>韩　汉</b></a>\n" + 
			"    \n" + 
			"							\n" + 
			"	<a href=\"http://dict.cn/jp/\" data-param=\"zh,en,other\"><b>日　汉</b></a>    <a class=\"sbox_morebtn searchnav-morea\" href=\"javascript:;\">更多</a>\n" + 
			"    \n" + 
			"	<em>|</em>\n" + 
			"    <a  href=\"http://juhai.dict.cn\" data-param=\"zh,en\">句海</a>\n" + 
			"    <a class=\"cur\" style=\"background-position-x:-77px\" href=\"http://hanyu.dict.cn\" data-param=\"zh\">汉语</a>\n" + 
			"    <em>|</em>\n" + 
			"	<a  href=\"http://shh.dict.cn\" data-param=\"zh\">上海话</a>\n" + 
			"	<a  href=\"http://gdh.dict.cn\" data-param=\"zh\">广东话</a>\n" + 
			"    <a href=\"http://abbr.dict.cn\" data-param=\"zh,en\">缩略语</a>\n" + 
			"    <a href=\"http://ename.dict.cn\" data-param=\"zh,en\">人名</a>\n" + 
			"</div>\n" + 
			"<div class=\"sbox_more_wrap\">\n" + 
			"    <div class=\"sbox_more\">\n" + 
			"        <div class=\"sbox_marrow\"></div>\n" + 
			"        <div class=\"sbox_mlangs\">\n" + 
			"            <a style=\"display:none\" href=\"http://dict.cn/jp/\" data-param=\"zh,en,other\"><b>日　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/fr/\" data-param=\"zh,en,other\"><b>法　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/de/\" data-param=\"zh,en,other\"><b>德　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/es/\" data-param=\"zh,en,other\"><b>西　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/it/\" data-param=\"zh,en,other\"><b>意　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/ru/\" data-param=\"zh,en,other\"><b>俄　汉</b></a>\n" + 
			"        </div>\n" + 
			"    </div>\n" + 
			"</div>\n" + 
			"    \n" + 
			"<div class=\"search_box\">\n" + 
			"	<form action=\"http://hanyu.dict.cn/search.php\" method=\"get\">\n" + 
			"	<input type=\"text\" id=\"q\" class=\"search_input\" name=\"q\" value=\"啊\" autocomplete=\"off\" placeholder=\"请输入汉语字词\" x-webkit-speech ><input type=\"submit\" class=\"search_submit\" id=\"search\" title=\"查词\" value=\"\">\n" + 
			"	</form>\n" + 
			"</div>	</div>\n" + 
			"	<div class=\"spread\" style=\"padding-left:10px;\">\n" + 
			"		<!-- search_tab -->\n" + 
			"		<div id='div-gpt-ad-1456898554209-4' style='width:200px; height:48px;'>		<script type='text/javascript'>\n" + 
			"		googletag.cmd.push(function() { googletag.display('div-gpt-ad-1456898554209-4'); });\n" + 
			"		</script>		</div>\n" + 
			"\n" + 
			"	</div>\n" + 
			"</div>\n" + 
			"</div>\n" + 
			"<script type='text/javascript'>\n" + 
			"var googletag = googletag || {};\n" + 
			"googletag.cmd = googletag.cmd || [];\n" + 
			"(function() {\n" + 
			"var gads = document.createElement('script');\n" + 
			"gads.async = true;\n" + 
			"gads.type = 'text/javascript';\n" + 
			"var useSSL = 'https:' == document.location.protocol;\n" + 
			"gads.src = (useSSL ? 'https:' : 'http:') + \n" + 
			"'//www.googletagservices.com/tag/js/gpt.js';\n" + 
			"var node = document.getElementsByTagName('script')[0];\n" + 
			"node.parentNode.insertBefore(gads, node);\n" + 
			"})();\n" + 
			"</script>\n" + 
			"\n" + 
			"<script type='text/javascript'>\n" + 
			"googletag.cmd.push(function() {\n" + 
			"googletag.defineSlot('/146434140/hanyu_topbanner', [728, 90], 'div-gpt-ad-1422601792026-0').addService(googletag.pubads());\n" + 
			"googletag.defineSlot('/146434140/hanyu_dbanner', [728, 90], 'div-gpt-ad-1422601792026-1').addService(googletag.pubads());\n" + 
			"googletag.pubads().enableSingleRequest();\n" + 
			"googletag.enableServices();\n" + 
			"});\n" + 
			"</script>\n" + 
			"<div id=\"main\">\n" + 
			"	<div class=\"mslide\">\n" + 
			"		<div class=\"m_nav\">\n" + 
			"			<ul>\n" + 
			"												<li><a href=\"http://scb.dict.cn/?ref=left&scb\" target=\"_blank\" class=\"scb-d\">我的生词本</a></li>\n" + 
			"				<li><a href=\"http://bdc.dict.cn/?ref=left&bdc\" target=\"_blank\" class=\"bdc-d\">在线背单词</a></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"				<dl class=\"mslide_udbox\">\n" + 
			"			<dt><a href=\"javascript:;\">查词历史»</a></dt>\n" + 
			"			<dd>\n" + 
			"				<ul>\n" + 
			"															<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%95%8A\">1.&nbsp;\n" + 
			"														啊													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%8F%82\">2.&nbsp;\n" + 
			"														参													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%90%95\">3.&nbsp;\n" + 
			"														吕													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%B9%85\">4.&nbsp;\n" + 
			"														幅													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E4%B8%80\">5.&nbsp;\n" + 
			"														一													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E4%B8%BA\">6.&nbsp;\n" + 
			"														为													</a>\n" + 
			"					</li>\n" + 
			"														</ul>\n" + 
			"			</dd>\n" + 
			"		</dl>\n" + 
			"			</div>\n" + 
			"	<div class=\"m\" style=\"width:819px;\">\n" + 
			"		<div style=\"margin-bottom:15px;\">\n" + 
			"			<!-- hanyu_topbanner -->\n" + 
			"			<div id='div-gpt-ad-1422601792026-0' style='width:728px; height:90px;'>\n" + 
			"				<script type='text/javascript'>\n" + 
			"				googletag.cmd.push(function() { googletag.display('div-gpt-ad-1422601792026-0'); });\n" + 
			"				</script>  \n" + 
			"			</div>\n" + 
			"		</div>\n" + 
			"		<!--功能设置-->\n" + 
			"<div class=\"tbcont_0 popup1\" style=\"display:none;\">\n" + 
			"	<div>\n" + 
			"		<h5 class=\"fy\">发音按钮选项</h5>\n" + 
			"		<ul class=\"popup01 clearFix\">\n" + 
			"			<li><input type=\"radio\" name=\"sound-opt\" onclick=\"_dict_config.ss = (_dict_config.ss >>1)<<1;saveConfig();\"/>鼠标悬停即发声音</li>\n" + 
			"			<li><input type=\"radio\" name=\"sound-opt\" onclick=\"_dict_config.ss |= 1;saveConfig();\"/>需要点击才发音</li>\n" + 
			"			<li><input type=\"checkbox\" onclick=\"if(this.checked)_dict_config.ss |= 2;else _dict_config.ss &= (~2);saveConfig();\"/>加载页面即发音</li>\n" + 
			"		</ul>\n" + 
			"	</div>\n" + 
			"	<div style=\"border-top:1px solid #d3e1e3;padding-top:10px;\">\n" + 
			"		<h5 class=\"hc\">鼠标划词</h5>\n" + 
			"		<ul class=\"popup01 clearFix\">\n" + 
			"			<li><input type=\"radio\" name=\"huaci-opt\" onclick=\"huaciSwitch(true)\"/>开启</li>\n" + 
			"			<li><input type=\"radio\" name=\"huaci-opt\" onclick=\"huaciSwitch(false)\"/>关闭</li>\n" + 
			"		</ul>\n" + 
			"		<a href=\"###\" class=\"close icon18\">X</a>					</div>\n" + 
			"</div>		\n" + 
			"				<!--bottom-right-->\n" + 
			"<div id=\"cy\" class=\"bottom-right\">\n" + 
			"		<!--字的释义-->\n" + 
			"	<div class=\"t-bgs clearFix\" style=\"padding-right:0;width:819px;\">\n" + 
			"		<h1 id=\"word-key\" class=\"cn-left cn-bg\" seg=\"\" >\n" + 
			"						<img src=\"/static/img/loading.gif\" alt=\"加载中\" style=\"padding-top:52px;\"/>\n" + 
			"			<object type=\"application/x-shockwave-flash\" data=\"http://dict.cn/apis/output.php?id=hanzi_2_11971_0\" style=\"display:none;width:130px;height:130px;\">		\n" + 
			"				<param name=\"movie\" value=\"http://dict.cn/apis/output.php?id=hanzi_2_11971_0\"> 				\n" + 
			"				<param name=\"AllowScriptAccess\" value=\"always\"> 				\n" + 
			"				<param name=\"wmode\" value=\"opaque\"> 				\n" + 
			"				<param name=\"hasPriority\" value=\"true\"> 			\n" + 
			"			</object>\n" + 
			"					</h1>\n" + 
			"		<div style=\"width: 678px;\">\n" + 
			"							<p class=\"hz_pinyin clearFix\"><span class=\"zh_title\">拼&nbsp;&nbsp;音</span><span>ā</span><span>á</span><span>ǎ</span><span>à</span><span>a</span></p>\n" + 
			"						<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title hz_wait\">繁&nbsp;&nbsp;体</span></li>\n" + 
			"				<li style=\"width:60%;\"><span class=\"zh_title hz_wait\">异&nbsp;&nbsp;体</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">部&nbsp;&nbsp;首</span><span>口部</span></li>\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">笔&nbsp;&nbsp;画</span><span>10笔</span></li>\n" + 
			"				<li><span class=\"zh_title \">五&nbsp;&nbsp;笔</span><span>KBSK</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">结&nbsp;&nbsp;构</span><span>左中右结构</span></li>\n" + 
			"				<li><span class=\"zh_title \">造字法</span><span>形声；从口、阿声</span></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"		<span class=\"seach-right st\"></span>\n" + 
			"	</div>\n" + 
			"	<!--tab-切换-字的释义--->\n" + 
			"	<div class=\"tab\" style=\"padding-top:15px;\">\n" + 
			"				<h6 class=\"title\">\n" + 
			"			<span class=\" current\">现代汉语</span>\n" + 
			"		</h6>\n" + 
			"		<div class=\"tabcontent block\">\n" + 
			"							<div id=\"ly\" class=\"cont-1\">\n" + 
			"					<ul class=\"L-L L-L2\">\n" + 
			"													<li style=\"padding:0;\">\n" + 
			"								<h2>啊<span class=\"duyin\">ā																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_11971_1\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em><s></s>&nbsp;[叹]</em>\n" + 
			"																																																																																	<p><span>用在句子开头，表示惊疑或赞叹：<em class=\"hz_sy\">～哟｜～呀｜～，失火了！</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[叹]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>用在句子开头，表示惊疑或赞叹：～哟｜～呀｜～，失火了！:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"													<li style=\"padding:0;padding-top:20px;\">\n" + 
			"								<h2>啊<span class=\"duyin\">á																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_11971_2\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em><s></s>&nbsp;[叹]</em>\n" + 
			"																																																																																	<p><span>用在句子的开头，表示追问：<em class=\"hz_sy\">～，谁说的？｜～，你说谁？｜～，她讲什么？</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[叹]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>用在句子的开头，表示追问：～，谁说的？｜～，你说谁？｜～，她讲什么？:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"													<li style=\"padding:0;padding-top:20px;\">\n" + 
			"								<h2>啊<span class=\"duyin\">ǎ																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_11971_3\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em><s></s>&nbsp;[叹]</em>\n" + 
			"																																																																																	<p><span>用在句子的开头，表示疑惑：<em class=\"hz_sy\">～，他来得这么快？｜～，能这样说吗？｜～，有这样的事吗？</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[叹]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>用在句子的开头，表示疑惑：～，他来得这么快？｜～，能这样说吗？｜～，有这样的事吗？:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"													<li style=\"padding:0;padding-top:20px;\">\n" + 
			"								<h2>啊<span class=\"duyin\">à																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_11971_4\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em><s></s>&nbsp;[叹]</em>\n" + 
			"																																																																																	<p><span>用在句子开头,表示应诺或醒悟（音较短）：<em class=\"hz_sy\">～，好吧！｜～，原来是她！</em></span></p>\n" + 
			"																																																																					<p><span>表示赞叹（音较长）：<em class=\"hz_sy\">～，伟大的祖国！｜～，祖国，我的母亲！｜～，一日千里的时代！</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[叹]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>表示赞叹（音较长）：～，伟大的祖国！｜～，祖国，我的母亲！｜～，一日千里的时代！:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"													<li style=\"padding:0;padding-top:20px;\">\n" + 
			"								<h2>啊<span class=\"duyin\">a																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em><s></s>&nbsp;[助]</em>\n" + 
			"																																																																																	<p><span>表示赞叹、肯定、嘱咐或疑问等语气：<em class=\"hz_sy\">好香～！｜多好的人～！｜你吃不吃～？</em></span></p>\n" + 
			"																																																																					<p><span>用在句中稍作停顿，引人注意，提引后面的话：<em class=\"hz_sy\">厂长～，他早走了｜自打你来以后～，他事事如意</em></span></p>\n" + 
			"																																																																					<p><span>放在列举的事项之后：<em class=\"hz_sy\">书～，杂志～，摆满一书架｜玩具～，画册～，到处都是</em></span></p>\n" + 
			"																																																																					<p><span>用在重复的动词后，表示过程长：<em class=\"hz_sy\">她找～找～，终于找到了！</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[助]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>用在重复的动词后，表示过程长：她找～找～，终于找到了！:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"											</ul>\n" + 
			"				</div>\n" + 
			"					</div>\n" + 
			"		<div class=\"tabcontent \">\n" + 
			"					</div>\n" + 
			"	</div>\n" + 
			"	</div>\n" + 
			"\n" + 
			"	\n" + 
			"					\n" + 
			"			\n" + 
			"				<div style=\"margin-top:80px;\">\n" + 
			"			<!--分享微博-->\n" + 
			"			<div class=\"sc-fx\" id=\"page-share\">\n" + 
			"				<a onclick=\"addFavorite(location.href, document.title);\" href=\"###\">收藏</a>|<span>分享到：</span>\n" + 
			"				<a href=\"javascript:window.open('http://share.renren.com/share/buttonshare.do?link='+encodeURIComponent(document.location.href)+'&amp;title='+encodeURIComponent(document.title));void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"renren fx\" title=\"分享到人人网\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://www.kaixin001.com/repaste/share.php?rtitle='+encodeURIComponent(document.title)+'&amp;rurl='+encodeURIComponent(document.location.href)+'&amp;rcontent=');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"kaixin fx\" title=\"分享到开心网\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://v.t.sina.com.cn/share/share.php?title='+encodeURIComponent(document.title)+'&amp;url='+encodeURIComponent(location.href)+'&amp;source=dict.cn','_blank','width=615,height=505');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"sina fx\" title=\"分享到新浪微博\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://t.163.com/article/user/checkLogin.do?link=http://news.163.com/&amp;source=dict.cn&amp;info='+encodeURIComponent(document.title)+' '+encodeURIComponent(location.href),'_blank','width=510,height=300');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"wangyi fx\" title=\"分享到网易微博\"></a>\n" + 
			"				<span style=\"position:absolute; right:60px;\"><a style=\"color:#326598;\" href=\"http://hanyu.dict.cn/more.php\">《海词汉语词典》 编者的话</a></span>\n" + 
			"			</div>\n" + 
			"			<div>\n" + 
			"				<!-- hanyu_dbanner -->\n" + 
			"				<div id='div-gpt-ad-1422601792026-1' style='width:728px; height:90px;'>\n" + 
			"					<script type='text/javascript'>\n" + 
			"					googletag.cmd.push(function() { googletag.display('div-gpt-ad-1422601792026-1'); });\n" + 
			"					</script>\n" + 
			"				</div>\n" + 
			"			</div>\n" + 
			"		</div>\n" + 
			"	</div>\n" + 
			"	<div class=\"cl\"></div>\n" + 
			"</div>\n" + 
			"<script type=\"text/javascript\" id=\"data-js\">\n" + 
			"	var $dict_id    = \"11971\";\n" + 
			"	var $dict_query = \"\\u554a\";\n" + 
			"	var $dict_dict  = \"han\";\n" + 
			"</script>\n" + 
			"<script type=\"text/javascript\" src=\"http://i1.haidii.com/i1/js/external.js\"></script>\n" + 
			"<div id=\"footer\">\n" + 
			"    <p><a href=\"http://about.dict.cn/introduce\" ref=\"nofollow\">关于海词</a> - <a href=\"http://about.dict.cn/copyrightstatement?cur=1\" ref=\"nofollow\">版权声明</a> - <a href=\"http://about.dict.cn/contact\" ref=\"nofollow\">联系海词</a> - <a target=\"_blank\" href=\"http://dict.cn/dir/\">星级词汇</a> - <a target=\"_blank\" href=\"http://dict.cn/dir/ceindex.html\">汉字列表</a> - <a target=\"_blank\" href=\"http://hr.dict.cn\" ref=\"nofollow\">招贤纳士</a></p>\n" + 
			"	<p>&copy;2003 - 2017 <a href=\"http://dict.cn/\">海词词典</a>(Dict.CN) - 自 2003 年开始服务 &nbsp;<a target=\"_blank\" href=\"http://www.miitbeian.gov.cn\" ref=\"nofollow\">沪ICP备08018881号</a>&nbsp;&nbsp;<a target=\"_blank\" href=\"http://www.sgs.gov.cn/lz/licenseLink.do?method=licenceView&amp;entyId=20120601170952752\" style=\"text-decoration:none;background-color:white;\" ref=\"nofollow\"><img border=\"0\" src=\"http://i1.haidii.com/i1/images/gs_icon.gif\"></a> 		 	<a target=\"_blank\" href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=31011502000490\" style=\"text-decoration:none;background-color:white;\"  ref=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/beian.png\" /><span style=\"display:none; color:#939393;\">沪公网安备 31011502000490号</span></a>\n" + 
			"		 </p>\n" + 
			"	<p style=\"text-align: center;margin-top:10px;\"><a href=\"http://m.dict.cn\" target=\"_blank\">海词词典手机移动站</a></p>\n" + 
			"	</div>\n" + 
			"<script>var langt='';</script>\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"	<script type=\"text/javascript\">\n" + 
			"	var cur_dict = 'hanyu', i1_home='http://i1.haidii.com', xuehai_home='http://xuehai.cn', passport_home='http://passport.dict.cn', $dict_id = null, $dict_query = \"\\u554a\", $dict_dict  = \"han\", $user_id = 0, $dict_ver=1486623708;\n" + 
			"	</script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/i1/js/ddialog/ddialog.1.0.0.min.js\" crossorigin></script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1481554274/i1/js/inputPrompt.min.js\" crossorigin></script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1481554264/i1/js/base.min.js\" crossorigin></script>\n" + 
			"	<div style=\"display:none;\">\n" + 
			"		<script type=\"text/javascript\">\n" + 
			"		var _bdhmProtocol = ((\"https:\" == document.location.protocol) ? \" https://\" : \" http://\");\n" + 
			"		document.write(unescape(\"%3Cscript src='\" + _bdhmProtocol + \"hm.baidu.com/h.js%3F8fd7837425ffd5a7fb88d32ea7060960' type='text/javascript'%3E%3C/script%3E\"));\n" + 
			"		</script>\n" + 
			"	</div>\n" + 
			"	<script type=\"text/javascript\">\n" + 
			"	var _gaq = _gaq || [];\n" + 
			"	_gaq.push(['_setAccount', 'UA-138041-2']);\n" + 
			"	_gaq.push(['_setDomainName', 'dict.cn']);\n" + 
			"	_gaq.push(['_trackPageview']);\n" + 
			"	(function() {\n" + 
			"		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" + 
			"		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" + 
			"		var s  = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" + 
			"	})();\n" + 
			"	</script>\n" + 
			"\n" + 
			"<script type=\"text/javascript\">\n" + 
			"window.onerror = function(errorMsg, url, lineNumber, column, errorObj){\n" + 
			"	$.getScript('http://dict-log.cn-hangzhou.log.aliyuncs.com/logstores/jsreport/track?APIVersion=0.6.0&call=error&ver=' + ($dict_ver || '') + '&url=' + encodeURIComponent(url)+ '&line=' + lineNumber + '&emsg=' + encodeURIComponent(errorMsg) + '&page='+ _href \n" + 
			"			+ '&agent=' + encodeURIComponent(navigator.userAgent) + '&column=' + encodeURIComponent(column) + '&StackTrace=' + encodeURIComponent(errorObj));\n" + 
			"	return true;\n" + 
			"};\n" + 
			"</script>\n" + 
			"</body>\n" + 
			"</html>\n" + 
			"";
	
	private static String html2 = "\n" + 
			"<!DOCTYPE HTML>\n" + 
			"<html>\n" + 
			"	<head>\n" + 
			"		<meta name=\"renderer\" content=\"webkit\">\n" + 
			"				<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7\" />\n" + 
			"				<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" + 
			"		<title>吕是什么意思_吕汉语解释_吕的例句 - 海词汉语</title>\n" + 
			"	\n" + 
			"		<meta name=\"keywords\" content=\"吕,吕是什么意思,吕的解释,吕的例句\" />\n" + 
			"		<meta name=\"description\" content=\"海词汉语频道为广大中文用户提供吕是什么意思、吕的解释、吕的例句，更多吕汉语解释到海词汉语。\" />\n" + 
			"		<meta name=\"author\" content=\"海词词典\" />\n" + 
			"		<link rel=\"canonical\" href=\"http://hanyu.dict.cn/吕\" />\n" + 
			"		<link rel=\"icon\" href=\"http://dict.cn/favicon.ico\" type=\"/image/x-icon\" />\n" + 
			"		<link rel=\"shortcut icon\" href=\"http://dict.cn/favicon.ico\" type=\"/image/x-icon\" />\n" + 
			"		<link href=\"http://i1.haidii.com/v/1484140449/i1/css/base.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" + 
			"		<link href=\"http://i1.haidii.com/v/1484140452/i1/css/obase.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" + 
			"		<script>var cur_dict = 'hanyu';var i1_home='http://i1.haidii.com';var xuehai_home='http://xuehai.cn';var passport_home='http://passport.dict.cn';</script>\n" + 
			"		<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1408420485/i1/js/jquery-1.8.0.min.js\"></script>\n" + 
			"		\n" + 
			"		        <script>var crumb='', dict_homepath = 'http://dict.cn', hc_jspath = 'http://i1.haidii.com/v/1484140447/i1/js/hc3/hc.min.js',use_bingTrans='', multi_langs = '';</script>\n" + 
			"	</head>\n" + 
			"	<body>\n" + 
			"<object style=\"position:absolute;top:-1000%;width:1px;height:1px;opacity:0;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);\" width=\"1\" height=\"1\" id=\"daudio\" type=\"application/x-shockwave-flash\" data=\"http://dict.cn/player/player.swf\">\n" + 
			"	<param name=\"movie\" value=\"http://dict.cn/player/player.swf\">\n" + 
			"	<param name=\"quality\" value=\"high\" />\n" + 
			"	<param name=\"bgcolor\" value=\"#ffffff\" />\n" + 
			"	<param name=\"allowScriptAccess\" value=\"always\" />\n" + 
			"	<param name=\"allowFullScreen\" value=\"true\" />\n" + 
			"	<param name=\"hasPriority\" value=\"true\" />\n" + 
			"	<param name=\"FlashVars\" value=\"volume=100\" />\n" + 
			"	<embed style=\"position:absolute;top:-1000%;width:1px;height:1px;-khtml-opacity:0;-moz-opacity:0;opacity:0;\" src=\"http://dict.cn/player/player.swf\" allowscriptaccess=\"always\" allowfullscreen=\"true\" quality=\"high\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\"></embed>\n" + 
			"</object>\n" + 
			"<div id=\"header\">\n" + 
			"	<div class=\"nav\">\n" + 
			"	<div class=\"links\">\n" + 
			"		<a class=cur href=\"http://dict.cn\">海词</a>\n" + 
			"		<a  onclick=\"cnewClose(this);\"   href=\"http://cidian.dict.cn/center.html?iref=dict-header-center\">权威词典</a>\n" + 
			"				<div id=\"cnewDiv\" style=\"position: relative;float:left;line-height: 40px;height:40px;\"><div style=\"position: absolute;top:-10px;left:-23px;\"><img style=\"vertical-align: middle\" src=\"http://i1.haidii.com/v/1420610131/i1/cidian/images/new.png\" /></div></div>\n" + 
			"		<script>function cnewClose(obj){var Days = 3600;var exp  = new Date();exp.setTime(exp.getTime() + Days*24*60*60*1000);document.cookie = 'cnew' + \"=\"+ escape(1) +\";expires=\"+ exp.toGMTString()+\";path=/;domain=.dict.cn\";document.cookie = 'cnewt' + \"=\"+ escape(1420560000) +\";expires=\"+ exp.toGMTString()+\"path=/;domain=.dict.cn\"; $(\"#cnewDiv\").hide();}</script>\n" + 
			"				<a  href=\"http://fanyi.dict.cn\">翻译</a>\n" + 
			"				<style type=\"text/css\">#header .links .top-download a{width:140px;height:40px;line-height:normal;background:url(http://i1.haidii.com/v/1408420472/i1/images/top-download-icon2.png) 0 0 no-repeat}#header .links .top-download a:hover{background:url(http://i1.haidii.com/v/1408420472/i1/images/top-download-icon2.png) 0 -40px no-repeat}</style>\n" + 
			"		<div class=\"top-download\"><a href=\"http://cidian.dict.cn/home.html?iref=dict-header-button\"></a></div>\n" + 
			"	</div>\n" + 
			"	<div class=\"login\">\n" + 
			"		<em action=\"feedback\" onclick=\"feedBackForm(this);\" title=\"意见反馈\">意见反馈</em>\n" + 
			"		<i>|</i>\n" + 
			"					<a title=\"注册\" href=\"http://passport.dict.cn/register\">注册</a><i>|</i><a title=\"登录\" href=\"http://passport.dict.cn/login\">登录</a>\n" + 
			"									</div>\n" + 
			"</div>\n" + 
			"<script>var pagetype = '';</script>\n" + 
			"	<script type='text/javascript'>\n" + 
			"var googletag = googletag || {};\n" + 
			"googletag.cmd = googletag.cmd || [];\n" + 
			"(function() {\n" + 
			"var gads = document.createElement('script');\n" + 
			"gads.async = true;\n" + 
			"gads.type = 'text/javascript';\n" + 
			"var useSSL = 'https:' == document.location.protocol;\n" + 
			"gads.src = (useSSL ? 'https:' : 'http:') + \n" + 
			"'//www.googletagservices.com/tag/js/gpt.js';\n" + 
			"var node = document.getElementsByTagName('script')[0];\n" + 
			"node.parentNode.insertBefore(gads, node);\n" + 
			"})();\n" + 
			"</script>\n" + 
			"\n" + 
			"<script type='text/javascript'>\n" + 
			"googletag.cmd.push(function() {\n" + 
			"googletag.defineSlot('/146434140/search_tab', [200, 48], 'div-gpt-ad-1422600134018-10').addService(googletag.pubads());\n" + 
			"googletag.pubads().enableSingleRequest();\n" + 
			"googletag.enableServices();\n" + 
			"});\n" + 
			"</script><div class=\"top\">\n" + 
			"			<a href=\"/\" class=\"logo\"><img alt=\"海词词典\" titile=\"海词词典\" src=\"http://i1.haidii.com/v/1408420472/i1/images/hanyu_search_logo.png\" /></a>\n" + 
			"		<div class=\"search\">\n" + 
			"		<div class=\"search_nav\">\n" + 
			"		<a  href=\"http://dict.cn\" data-param=\"zh,en,other\"><b>英　汉</b></a>\n" + 
			"    <a  href=\"http://dict.cn/kr/\" data-param=\"zh,en,other\"><b>韩　汉</b></a>\n" + 
			"    \n" + 
			"							\n" + 
			"	<a href=\"http://dict.cn/jp/\" data-param=\"zh,en,other\"><b>日　汉</b></a>    <a class=\"sbox_morebtn searchnav-morea\" href=\"javascript:;\">更多</a>\n" + 
			"    \n" + 
			"	<em>|</em>\n" + 
			"    <a  href=\"http://juhai.dict.cn\" data-param=\"zh,en\">句海</a>\n" + 
			"    <a class=\"cur\" style=\"background-position-x:-77px\" href=\"http://hanyu.dict.cn\" data-param=\"zh\">汉语</a>\n" + 
			"    <em>|</em>\n" + 
			"	<a  href=\"http://shh.dict.cn\" data-param=\"zh\">上海话</a>\n" + 
			"	<a  href=\"http://gdh.dict.cn\" data-param=\"zh\">广东话</a>\n" + 
			"    <a href=\"http://abbr.dict.cn\" data-param=\"zh,en\">缩略语</a>\n" + 
			"    <a href=\"http://ename.dict.cn\" data-param=\"zh,en\">人名</a>\n" + 
			"</div>\n" + 
			"<div class=\"sbox_more_wrap\">\n" + 
			"    <div class=\"sbox_more\">\n" + 
			"        <div class=\"sbox_marrow\"></div>\n" + 
			"        <div class=\"sbox_mlangs\">\n" + 
			"            <a style=\"display:none\" href=\"http://dict.cn/jp/\" data-param=\"zh,en,other\"><b>日　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/fr/\" data-param=\"zh,en,other\"><b>法　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/de/\" data-param=\"zh,en,other\"><b>德　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/es/\" data-param=\"zh,en,other\"><b>西　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/it/\" data-param=\"zh,en,other\"><b>意　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/ru/\" data-param=\"zh,en,other\"><b>俄　汉</b></a>\n" + 
			"        </div>\n" + 
			"    </div>\n" + 
			"</div>\n" + 
			"    \n" + 
			"<div class=\"search_box\">\n" + 
			"	<form action=\"http://hanyu.dict.cn/search.php\" method=\"get\">\n" + 
			"	<input type=\"text\" id=\"q\" class=\"search_input\" name=\"q\" value=\"吕\" autocomplete=\"off\" placeholder=\"请输入汉语字词\" x-webkit-speech ><input type=\"submit\" class=\"search_submit\" id=\"search\" title=\"查词\" value=\"\">\n" + 
			"	</form>\n" + 
			"</div>	</div>\n" + 
			"	<div class=\"spread\" style=\"padding-left:10px;\">\n" + 
			"		<!-- search_tab -->\n" + 
			"		<div id='div-gpt-ad-1456898554209-4' style='width:200px; height:48px;'>		<script type='text/javascript'>\n" + 
			"		googletag.cmd.push(function() { googletag.display('div-gpt-ad-1456898554209-4'); });\n" + 
			"		</script>		</div>\n" + 
			"\n" + 
			"	</div>\n" + 
			"</div>\n" + 
			"</div>\n" + 
			"<script type='text/javascript'>\n" + 
			"var googletag = googletag || {};\n" + 
			"googletag.cmd = googletag.cmd || [];\n" + 
			"(function() {\n" + 
			"var gads = document.createElement('script');\n" + 
			"gads.async = true;\n" + 
			"gads.type = 'text/javascript';\n" + 
			"var useSSL = 'https:' == document.location.protocol;\n" + 
			"gads.src = (useSSL ? 'https:' : 'http:') + \n" + 
			"'//www.googletagservices.com/tag/js/gpt.js';\n" + 
			"var node = document.getElementsByTagName('script')[0];\n" + 
			"node.parentNode.insertBefore(gads, node);\n" + 
			"})();\n" + 
			"</script>\n" + 
			"\n" + 
			"<script type='text/javascript'>\n" + 
			"googletag.cmd.push(function() {\n" + 
			"googletag.defineSlot('/146434140/hanyu_topbanner', [728, 90], 'div-gpt-ad-1422601792026-0').addService(googletag.pubads());\n" + 
			"googletag.defineSlot('/146434140/hanyu_dbanner', [728, 90], 'div-gpt-ad-1422601792026-1').addService(googletag.pubads());\n" + 
			"googletag.pubads().enableSingleRequest();\n" + 
			"googletag.enableServices();\n" + 
			"});\n" + 
			"</script>\n" + 
			"<div id=\"main\">\n" + 
			"	<div class=\"mslide\">\n" + 
			"		<div class=\"m_nav\">\n" + 
			"			<ul>\n" + 
			"												<li><a href=\"http://scb.dict.cn/?ref=left&scb\" target=\"_blank\" class=\"scb-d\">我的生词本</a></li>\n" + 
			"				<li><a href=\"http://bdc.dict.cn/?ref=left&bdc\" target=\"_blank\" class=\"bdc-d\">在线背单词</a></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"				<dl class=\"mslide_udbox\">\n" + 
			"			<dt><a href=\"javascript:;\">查词历史»</a></dt>\n" + 
			"			<dd>\n" + 
			"				<ul>\n" + 
			"															<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%90%95\">1.&nbsp;\n" + 
			"														吕													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%8F%82\">2.&nbsp;\n" + 
			"														参													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%B9%85\">3.&nbsp;\n" + 
			"														幅													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E4%B8%80\">4.&nbsp;\n" + 
			"														一													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E4%B8%BA\">5.&nbsp;\n" + 
			"														为													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E4%B8%8D%E7%94%A8\">6.&nbsp;\n" + 
			"														不用													</a>\n" + 
			"					</li>\n" + 
			"														</ul>\n" + 
			"			</dd>\n" + 
			"		</dl>\n" + 
			"			</div>\n" + 
			"	<div class=\"m\" style=\"width:819px;\">\n" + 
			"		<div style=\"margin-bottom:15px;\">\n" + 
			"			<!-- hanyu_topbanner -->\n" + 
			"			<div id='div-gpt-ad-1422601792026-0' style='width:728px; height:90px;'>\n" + 
			"				<script type='text/javascript'>\n" + 
			"				googletag.cmd.push(function() { googletag.display('div-gpt-ad-1422601792026-0'); });\n" + 
			"				</script>  \n" + 
			"			</div>\n" + 
			"		</div>\n" + 
			"		<!--功能设置-->\n" + 
			"<div class=\"tbcont_0 popup1\" style=\"display:none;\">\n" + 
			"	<div>\n" + 
			"		<h5 class=\"fy\">发音按钮选项</h5>\n" + 
			"		<ul class=\"popup01 clearFix\">\n" + 
			"			<li><input type=\"radio\" name=\"sound-opt\" onclick=\"_dict_config.ss = (_dict_config.ss >>1)<<1;saveConfig();\"/>鼠标悬停即发声音</li>\n" + 
			"			<li><input type=\"radio\" name=\"sound-opt\" onclick=\"_dict_config.ss |= 1;saveConfig();\"/>需要点击才发音</li>\n" + 
			"			<li><input type=\"checkbox\" onclick=\"if(this.checked)_dict_config.ss |= 2;else _dict_config.ss &= (~2);saveConfig();\"/>加载页面即发音</li>\n" + 
			"		</ul>\n" + 
			"	</div>\n" + 
			"	<div style=\"border-top:1px solid #d3e1e3;padding-top:10px;\">\n" + 
			"		<h5 class=\"hc\">鼠标划词</h5>\n" + 
			"		<ul class=\"popup01 clearFix\">\n" + 
			"			<li><input type=\"radio\" name=\"huaci-opt\" onclick=\"huaciSwitch(true)\"/>开启</li>\n" + 
			"			<li><input type=\"radio\" name=\"huaci-opt\" onclick=\"huaciSwitch(false)\"/>关闭</li>\n" + 
			"		</ul>\n" + 
			"		<a href=\"###\" class=\"close icon18\">X</a>					</div>\n" + 
			"</div>		\n" + 
			"				<!--bottom-right-->\n" + 
			"<div id=\"cy\" class=\"bottom-right\">\n" + 
			"		<!--字的释义-->\n" + 
			"	<div class=\"t-bgs clearFix\" style=\"padding-right:0;width:819px;\">\n" + 
			"		<h1 id=\"word-key\" class=\"cn-left cn-bg\" seg=\"\" >\n" + 
			"							吕					</h1>\n" + 
			"		<div style=\"width: 678px;\">\n" + 
			"							<p class=\"hz_pinyin clearFix\"><span class=\"zh_title\">拼&nbsp;&nbsp;音</span><span>lǚ</span></p>\n" + 
			"						<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title hz_wait\">繁&nbsp;&nbsp;体</span></li>\n" + 
			"				<li style=\"width:60%;\"><span class=\"zh_title hz_wait\">异&nbsp;&nbsp;体</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">部&nbsp;&nbsp;首</span><span>口部</span></li>\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">笔&nbsp;&nbsp;画</span><span>6笔</span></li>\n" + 
			"				<li><span class=\"zh_title \">五&nbsp;&nbsp;笔</span><span>KKF</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">结&nbsp;&nbsp;构</span><span>上下结构</span></li>\n" + 
			"				<li><span class=\"zh_title \">造字法</span><span>象形</span></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"		<span class=\"seach-right st\"></span>\n" + 
			"	</div>\n" + 
			"	<!--tab-切换-字的释义--->\n" + 
			"	<div class=\"tab\" style=\"padding-top:15px;\">\n" + 
			"				<h6 class=\"title\">\n" + 
			"			<span class=\" current\">现代汉语</span>\n" + 
			"		</h6>\n" + 
			"		<div class=\"tabcontent block\">\n" + 
			"							<div id=\"ly\" class=\"cont-1\">\n" + 
			"					<ul class=\"L-L L-L2\">\n" + 
			"													<li style=\"padding:0;\">\n" + 
			"								<h2>吕<span class=\"duyin\">lǚ																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_14210_0\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em><s></s>&nbsp;[名]</em>\n" + 
			"																																																																																	<p><span>我国古代审定乐音高低的标准；以管的长短来确定音的不同高度，从低音管算起，成奇数的六个管称“律”，成偶数的六个管称“吕”，总称“六吕”、“六律”，合称“十二吕”，简称“律吕”。</span></p>\n" + 
			"																																																																					<p><span>（Lǚ）姓</span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[名]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>（Lǚ）姓:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"											</ul>\n" + 
			"				</div>\n" + 
			"					</div>\n" + 
			"		<div class=\"tabcontent \">\n" + 
			"					</div>\n" + 
			"	</div>\n" + 
			"	</div>\n" + 
			"\n" + 
			"	\n" + 
			"					\n" + 
			"			\n" + 
			"				<div style=\"margin-top:80px;\">\n" + 
			"			<!--分享微博-->\n" + 
			"			<div class=\"sc-fx\" id=\"page-share\">\n" + 
			"				<a onclick=\"addFavorite(location.href, document.title);\" href=\"###\">收藏</a>|<span>分享到：</span>\n" + 
			"				<a href=\"javascript:window.open('http://share.renren.com/share/buttonshare.do?link='+encodeURIComponent(document.location.href)+'&amp;title='+encodeURIComponent(document.title));void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"renren fx\" title=\"分享到人人网\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://www.kaixin001.com/repaste/share.php?rtitle='+encodeURIComponent(document.title)+'&amp;rurl='+encodeURIComponent(document.location.href)+'&amp;rcontent=');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"kaixin fx\" title=\"分享到开心网\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://v.t.sina.com.cn/share/share.php?title='+encodeURIComponent(document.title)+'&amp;url='+encodeURIComponent(location.href)+'&amp;source=dict.cn','_blank','width=615,height=505');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"sina fx\" title=\"分享到新浪微博\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://t.163.com/article/user/checkLogin.do?link=http://news.163.com/&amp;source=dict.cn&amp;info='+encodeURIComponent(document.title)+' '+encodeURIComponent(location.href),'_blank','width=510,height=300');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"wangyi fx\" title=\"分享到网易微博\"></a>\n" + 
			"				<span style=\"position:absolute; right:60px;\"><a style=\"color:#326598;\" href=\"http://hanyu.dict.cn/more.php\">《海词汉语词典》 编者的话</a></span>\n" + 
			"			</div>\n" + 
			"			<div>\n" + 
			"				<!-- hanyu_dbanner -->\n" + 
			"				<div id='div-gpt-ad-1422601792026-1' style='width:728px; height:90px;'>\n" + 
			"					<script type='text/javascript'>\n" + 
			"					googletag.cmd.push(function() { googletag.display('div-gpt-ad-1422601792026-1'); });\n" + 
			"					</script>\n" + 
			"				</div>\n" + 
			"			</div>\n" + 
			"		</div>\n" + 
			"	</div>\n" + 
			"	<div class=\"cl\"></div>\n" + 
			"</div>\n" + 
			"<script type=\"text/javascript\" id=\"data-js\">\n" + 
			"	var $dict_id    = \"14210\";\n" + 
			"	var $dict_query = \"\\u5415\";\n" + 
			"	var $dict_dict  = \"han\";\n" + 
			"</script>\n" + 
			"<script type=\"text/javascript\" src=\"http://i1.haidii.com/i1/js/external.js\"></script>\n" + 
			"<div id=\"footer\">\n" + 
			"    <p><a href=\"http://about.dict.cn/introduce\" ref=\"nofollow\">关于海词</a> - <a href=\"http://about.dict.cn/copyrightstatement?cur=1\" ref=\"nofollow\">版权声明</a> - <a href=\"http://about.dict.cn/contact\" ref=\"nofollow\">联系海词</a> - <a target=\"_blank\" href=\"http://dict.cn/dir/\">星级词汇</a> - <a target=\"_blank\" href=\"http://dict.cn/dir/ceindex.html\">汉字列表</a> - <a target=\"_blank\" href=\"http://hr.dict.cn\" ref=\"nofollow\">招贤纳士</a></p>\n" + 
			"	<p>&copy;2003 - 2017 <a href=\"http://dict.cn/\">海词词典</a>(Dict.CN) - 自 2003 年开始服务 &nbsp;<a target=\"_blank\" href=\"http://www.miitbeian.gov.cn\" ref=\"nofollow\">沪ICP备08018881号</a>&nbsp;&nbsp;<a target=\"_blank\" href=\"http://www.sgs.gov.cn/lz/licenseLink.do?method=licenceView&amp;entyId=20120601170952752\" style=\"text-decoration:none;background-color:white;\" ref=\"nofollow\"><img border=\"0\" src=\"http://i1.haidii.com/i1/images/gs_icon.gif\"></a> 		 	<a target=\"_blank\" href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=31011502000490\" style=\"text-decoration:none;background-color:white;\"  ref=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/beian.png\" /><span style=\"display:none; color:#939393;\">沪公网安备 31011502000490号</span></a>\n" + 
			"		 </p>\n" + 
			"	<p style=\"text-align: center;margin-top:10px;\"><a href=\"http://m.dict.cn\" target=\"_blank\">海词词典手机移动站</a></p>\n" + 
			"	</div>\n" + 
			"<script>var langt='';</script>\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"	<script type=\"text/javascript\">\n" + 
			"	var cur_dict = 'hanyu', i1_home='http://i1.haidii.com', xuehai_home='http://xuehai.cn', passport_home='http://passport.dict.cn', $dict_id = null, $dict_query = \"\\u5415\", $dict_dict  = \"han\", $user_id = 0, $dict_ver=1486623708;\n" + 
			"	</script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/i1/js/ddialog/ddialog.1.0.0.min.js\" crossorigin></script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1481554274/i1/js/inputPrompt.min.js\" crossorigin></script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1481554264/i1/js/base.min.js\" crossorigin></script>\n" + 
			"	<div style=\"display:none;\">\n" + 
			"		<script type=\"text/javascript\">\n" + 
			"		var _bdhmProtocol = ((\"https:\" == document.location.protocol) ? \" https://\" : \" http://\");\n" + 
			"		document.write(unescape(\"%3Cscript src='\" + _bdhmProtocol + \"hm.baidu.com/h.js%3F8fd7837425ffd5a7fb88d32ea7060960' type='text/javascript'%3E%3C/script%3E\"));\n" + 
			"		</script>\n" + 
			"	</div>\n" + 
			"	<script type=\"text/javascript\">\n" + 
			"	var _gaq = _gaq || [];\n" + 
			"	_gaq.push(['_setAccount', 'UA-138041-2']);\n" + 
			"	_gaq.push(['_setDomainName', 'dict.cn']);\n" + 
			"	_gaq.push(['_trackPageview']);\n" + 
			"	(function() {\n" + 
			"		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" + 
			"		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" + 
			"		var s  = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" + 
			"	})();\n" + 
			"	</script>\n" + 
			"\n" + 
			"<script type=\"text/javascript\">\n" + 
			"window.onerror = function(errorMsg, url, lineNumber, column, errorObj){\n" + 
			"	$.getScript('http://dict-log.cn-hangzhou.log.aliyuncs.com/logstores/jsreport/track?APIVersion=0.6.0&call=error&ver=' + ($dict_ver || '') + '&url=' + encodeURIComponent(url)+ '&line=' + lineNumber + '&emsg=' + encodeURIComponent(errorMsg) + '&page='+ _href \n" + 
			"			+ '&agent=' + encodeURIComponent(navigator.userAgent) + '&column=' + encodeURIComponent(column) + '&StackTrace=' + encodeURIComponent(errorObj));\n" + 
			"	return true;\n" + 
			"};\n" + 
			"</script>\n" + 
			"</body>\n" + 
			"</html>\n" + 
			"";
	
	private static String html1 = "\n" + 
			"<!DOCTYPE HTML>\n" + 
			"<html>\n" + 
			"	<head>\n" + 
			"		<meta name=\"renderer\" content=\"webkit\">\n" + 
			"				<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7\" />\n" + 
			"				<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n" + 
			"		<title>参是什么意思_参汉语解释_参的例句 - 海词汉语</title>\n" + 
			"	\n" + 
			"		<meta name=\"keywords\" content=\"参,参是什么意思,参的解释,参的例句\" />\n" + 
			"		<meta name=\"description\" content=\"海词汉语频道为广大中文用户提供参是什么意思、参的解释、参的例句，更多参汉语解释到海词汉语。\" />\n" + 
			"		<meta name=\"author\" content=\"海词词典\" />\n" + 
			"		<link rel=\"canonical\" href=\"http://hanyu.dict.cn/参\" />\n" + 
			"		<link rel=\"icon\" href=\"http://dict.cn/favicon.ico\" type=\"/image/x-icon\" />\n" + 
			"		<link rel=\"shortcut icon\" href=\"http://dict.cn/favicon.ico\" type=\"/image/x-icon\" />\n" + 
			"		<link href=\"http://i1.haidii.com/v/1484140449/i1/css/base.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" + 
			"		<link href=\"http://i1.haidii.com/v/1484140452/i1/css/obase.min.css\" rel=\"stylesheet\" type=\"text/css\" />\n" + 
			"		<script>var cur_dict = 'hanyu';var i1_home='http://i1.haidii.com';var xuehai_home='http://xuehai.cn';var passport_home='http://passport.dict.cn';</script>\n" + 
			"		<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1408420485/i1/js/jquery-1.8.0.min.js\"></script>\n" + 
			"		\n" + 
			"		        <script>var crumb='', dict_homepath = 'http://dict.cn', hc_jspath = 'http://i1.haidii.com/v/1484140447/i1/js/hc3/hc.min.js',use_bingTrans='', multi_langs = '';</script>\n" + 
			"	</head>\n" + 
			"	<body>\n" + 
			"<object style=\"position:absolute;top:-1000%;width:1px;height:1px;opacity:0;filter:progid:DXImageTransform.Microsoft.Alpha(opacity=0);\" width=\"1\" height=\"1\" id=\"daudio\" type=\"application/x-shockwave-flash\" data=\"http://dict.cn/player/player.swf\">\n" + 
			"	<param name=\"movie\" value=\"http://dict.cn/player/player.swf\">\n" + 
			"	<param name=\"quality\" value=\"high\" />\n" + 
			"	<param name=\"bgcolor\" value=\"#ffffff\" />\n" + 
			"	<param name=\"allowScriptAccess\" value=\"always\" />\n" + 
			"	<param name=\"allowFullScreen\" value=\"true\" />\n" + 
			"	<param name=\"hasPriority\" value=\"true\" />\n" + 
			"	<param name=\"FlashVars\" value=\"volume=100\" />\n" + 
			"	<embed style=\"position:absolute;top:-1000%;width:1px;height:1px;-khtml-opacity:0;-moz-opacity:0;opacity:0;\" src=\"http://dict.cn/player/player.swf\" allowscriptaccess=\"always\" allowfullscreen=\"true\" quality=\"high\" type=\"application/x-shockwave-flash\" pluginspage=\"http://www.macromedia.com/go/getflashplayer\"></embed>\n" + 
			"</object>\n" + 
			"<div id=\"header\">\n" + 
			"	<div class=\"nav\">\n" + 
			"	<div class=\"links\">\n" + 
			"		<a class=cur href=\"http://dict.cn\">海词</a>\n" + 
			"		<a  onclick=\"cnewClose(this);\"   href=\"http://cidian.dict.cn/center.html?iref=dict-header-center\">权威词典</a>\n" + 
			"				<div id=\"cnewDiv\" style=\"position: relative;float:left;line-height: 40px;height:40px;\"><div style=\"position: absolute;top:-10px;left:-23px;\"><img style=\"vertical-align: middle\" src=\"http://i1.haidii.com/v/1420610131/i1/cidian/images/new.png\" /></div></div>\n" + 
			"		<script>function cnewClose(obj){var Days = 3600;var exp  = new Date();exp.setTime(exp.getTime() + Days*24*60*60*1000);document.cookie = 'cnew' + \"=\"+ escape(1) +\";expires=\"+ exp.toGMTString()+\";path=/;domain=.dict.cn\";document.cookie = 'cnewt' + \"=\"+ escape(1420560000) +\";expires=\"+ exp.toGMTString()+\"path=/;domain=.dict.cn\"; $(\"#cnewDiv\").hide();}</script>\n" + 
			"				<a  href=\"http://fanyi.dict.cn\">翻译</a>\n" + 
			"				<style type=\"text/css\">#header .links .top-download a{width:140px;height:40px;line-height:normal;background:url(http://i1.haidii.com/v/1408420472/i1/images/top-download-icon2.png) 0 0 no-repeat}#header .links .top-download a:hover{background:url(http://i1.haidii.com/v/1408420472/i1/images/top-download-icon2.png) 0 -40px no-repeat}</style>\n" + 
			"		<div class=\"top-download\"><a href=\"http://cidian.dict.cn/home.html?iref=dict-header-button\"></a></div>\n" + 
			"	</div>\n" + 
			"	<div class=\"login\">\n" + 
			"		<em action=\"feedback\" onclick=\"feedBackForm(this);\" title=\"意见反馈\">意见反馈</em>\n" + 
			"		<i>|</i>\n" + 
			"					<a title=\"注册\" href=\"http://passport.dict.cn/register\">注册</a><i>|</i><a title=\"登录\" href=\"http://passport.dict.cn/login\">登录</a>\n" + 
			"									</div>\n" + 
			"</div>\n" + 
			"<script>var pagetype = '';</script>\n" + 
			"	<script type='text/javascript'>\n" + 
			"var googletag = googletag || {};\n" + 
			"googletag.cmd = googletag.cmd || [];\n" + 
			"(function() {\n" + 
			"var gads = document.createElement('script');\n" + 
			"gads.async = true;\n" + 
			"gads.type = 'text/javascript';\n" + 
			"var useSSL = 'https:' == document.location.protocol;\n" + 
			"gads.src = (useSSL ? 'https:' : 'http:') + \n" + 
			"'//www.googletagservices.com/tag/js/gpt.js';\n" + 
			"var node = document.getElementsByTagName('script')[0];\n" + 
			"node.parentNode.insertBefore(gads, node);\n" + 
			"})();\n" + 
			"</script>\n" + 
			"\n" + 
			"<script type='text/javascript'>\n" + 
			"googletag.cmd.push(function() {\n" + 
			"googletag.defineSlot('/146434140/search_tab', [200, 48], 'div-gpt-ad-1422600134018-10').addService(googletag.pubads());\n" + 
			"googletag.pubads().enableSingleRequest();\n" + 
			"googletag.enableServices();\n" + 
			"});\n" + 
			"</script><div class=\"top\">\n" + 
			"			<a href=\"/\" class=\"logo\"><img alt=\"海词词典\" titile=\"海词词典\" src=\"http://i1.haidii.com/v/1408420472/i1/images/hanyu_search_logo.png\" /></a>\n" + 
			"		<div class=\"search\">\n" + 
			"		<div class=\"search_nav\">\n" + 
			"		<a  href=\"http://dict.cn\" data-param=\"zh,en,other\"><b>英　汉</b></a>\n" + 
			"    <a  href=\"http://dict.cn/kr/\" data-param=\"zh,en,other\"><b>韩　汉</b></a>\n" + 
			"    \n" + 
			"							\n" + 
			"	<a href=\"http://dict.cn/jp/\" data-param=\"zh,en,other\"><b>日　汉</b></a>    <a class=\"sbox_morebtn searchnav-morea\" href=\"javascript:;\">更多</a>\n" + 
			"    \n" + 
			"	<em>|</em>\n" + 
			"    <a  href=\"http://juhai.dict.cn\" data-param=\"zh,en\">句海</a>\n" + 
			"    <a class=\"cur\" style=\"background-position-x:-77px\" href=\"http://hanyu.dict.cn\" data-param=\"zh\">汉语</a>\n" + 
			"    <em>|</em>\n" + 
			"	<a  href=\"http://shh.dict.cn\" data-param=\"zh\">上海话</a>\n" + 
			"	<a  href=\"http://gdh.dict.cn\" data-param=\"zh\">广东话</a>\n" + 
			"    <a href=\"http://abbr.dict.cn\" data-param=\"zh,en\">缩略语</a>\n" + 
			"    <a href=\"http://ename.dict.cn\" data-param=\"zh,en\">人名</a>\n" + 
			"</div>\n" + 
			"<div class=\"sbox_more_wrap\">\n" + 
			"    <div class=\"sbox_more\">\n" + 
			"        <div class=\"sbox_marrow\"></div>\n" + 
			"        <div class=\"sbox_mlangs\">\n" + 
			"            <a style=\"display:none\" href=\"http://dict.cn/jp/\" data-param=\"zh,en,other\"><b>日　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/fr/\" data-param=\"zh,en,other\"><b>法　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/de/\" data-param=\"zh,en,other\"><b>德　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/es/\" data-param=\"zh,en,other\"><b>西　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/it/\" data-param=\"zh,en,other\"><b>意　汉</b></a>\n" + 
			"            <a  href=\"http://dict.cn/ru/\" data-param=\"zh,en,other\"><b>俄　汉</b></a>\n" + 
			"        </div>\n" + 
			"    </div>\n" + 
			"</div>\n" + 
			"    \n" + 
			"<div class=\"search_box\">\n" + 
			"	<form action=\"http://hanyu.dict.cn/search.php\" method=\"get\">\n" + 
			"	<input type=\"text\" id=\"q\" class=\"search_input\" name=\"q\" value=\"参\" autocomplete=\"off\" placeholder=\"请输入汉语字词\" x-webkit-speech ><input type=\"submit\" class=\"search_submit\" id=\"search\" title=\"查词\" value=\"\">\n" + 
			"	</form>\n" + 
			"</div>	</div>\n" + 
			"	<div class=\"spread\" style=\"padding-left:10px;\">\n" + 
			"		<!-- search_tab -->\n" + 
			"		<div id='div-gpt-ad-1456898554209-4' style='width:200px; height:48px;'>		<script type='text/javascript'>\n" + 
			"		googletag.cmd.push(function() { googletag.display('div-gpt-ad-1456898554209-4'); });\n" + 
			"		</script>		</div>\n" + 
			"\n" + 
			"	</div>\n" + 
			"</div>\n" + 
			"</div>\n" + 
			"<script type='text/javascript'>\n" + 
			"var googletag = googletag || {};\n" + 
			"googletag.cmd = googletag.cmd || [];\n" + 
			"(function() {\n" + 
			"var gads = document.createElement('script');\n" + 
			"gads.async = true;\n" + 
			"gads.type = 'text/javascript';\n" + 
			"var useSSL = 'https:' == document.location.protocol;\n" + 
			"gads.src = (useSSL ? 'https:' : 'http:') + \n" + 
			"'//www.googletagservices.com/tag/js/gpt.js';\n" + 
			"var node = document.getElementsByTagName('script')[0];\n" + 
			"node.parentNode.insertBefore(gads, node);\n" + 
			"})();\n" + 
			"</script>\n" + 
			"\n" + 
			"<script type='text/javascript'>\n" + 
			"googletag.cmd.push(function() {\n" + 
			"googletag.defineSlot('/146434140/hanyu_topbanner', [728, 90], 'div-gpt-ad-1422601792026-0').addService(googletag.pubads());\n" + 
			"googletag.defineSlot('/146434140/hanyu_dbanner', [728, 90], 'div-gpt-ad-1422601792026-1').addService(googletag.pubads());\n" + 
			"googletag.pubads().enableSingleRequest();\n" + 
			"googletag.enableServices();\n" + 
			"});\n" + 
			"</script>\n" + 
			"<div id=\"main\">\n" + 
			"	<div class=\"mslide\">\n" + 
			"		<div class=\"m_nav\">\n" + 
			"			<ul>\n" + 
			"												<li><a href=\"http://scb.dict.cn/?ref=left&scb\" target=\"_blank\" class=\"scb-d\">我的生词本</a></li>\n" + 
			"				<li><a href=\"http://bdc.dict.cn/?ref=left&bdc\" target=\"_blank\" class=\"bdc-d\">在线背单词</a></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"				<dl class=\"mslide_udbox\">\n" + 
			"			<dt><a href=\"javascript:;\">查词历史»</a></dt>\n" + 
			"			<dd>\n" + 
			"				<ul>\n" + 
			"															<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%8F%82\">1.&nbsp;\n" + 
			"														参													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E5%B9%85\">2.&nbsp;\n" + 
			"														幅													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E4%B8%80\">3.&nbsp;\n" + 
			"														一													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E4%B8%BA\">4.&nbsp;\n" + 
			"														为													</a>\n" + 
			"					</li>\n" + 
			"																				<li>\n" + 
			"						<a href=\"http://hanyu.dict.cn/%E4%B8%8D%E7%94%A8\">5.&nbsp;\n" + 
			"														不用													</a>\n" + 
			"					</li>\n" + 
			"														</ul>\n" + 
			"			</dd>\n" + 
			"		</dl>\n" + 
			"			</div>\n" + 
			"	<div class=\"m\" style=\"width:819px;\">\n" + 
			"		<div style=\"margin-bottom:15px;\">\n" + 
			"			<!-- hanyu_topbanner -->\n" + 
			"			<div id='div-gpt-ad-1422601792026-0' style='width:728px; height:90px;'>\n" + 
			"				<script type='text/javascript'>\n" + 
			"				googletag.cmd.push(function() { googletag.display('div-gpt-ad-1422601792026-0'); });\n" + 
			"				</script>  \n" + 
			"			</div>\n" + 
			"		</div>\n" + 
			"		<!--功能设置-->\n" + 
			"<div class=\"tbcont_0 popup1\" style=\"display:none;\">\n" + 
			"	<div>\n" + 
			"		<h5 class=\"fy\">发音按钮选项</h5>\n" + 
			"		<ul class=\"popup01 clearFix\">\n" + 
			"			<li><input type=\"radio\" name=\"sound-opt\" onclick=\"_dict_config.ss = (_dict_config.ss >>1)<<1;saveConfig();\"/>鼠标悬停即发声音</li>\n" + 
			"			<li><input type=\"radio\" name=\"sound-opt\" onclick=\"_dict_config.ss |= 1;saveConfig();\"/>需要点击才发音</li>\n" + 
			"			<li><input type=\"checkbox\" onclick=\"if(this.checked)_dict_config.ss |= 2;else _dict_config.ss &= (~2);saveConfig();\"/>加载页面即发音</li>\n" + 
			"		</ul>\n" + 
			"	</div>\n" + 
			"	<div style=\"border-top:1px solid #d3e1e3;padding-top:10px;\">\n" + 
			"		<h5 class=\"hc\">鼠标划词</h5>\n" + 
			"		<ul class=\"popup01 clearFix\">\n" + 
			"			<li><input type=\"radio\" name=\"huaci-opt\" onclick=\"huaciSwitch(true)\"/>开启</li>\n" + 
			"			<li><input type=\"radio\" name=\"huaci-opt\" onclick=\"huaciSwitch(false)\"/>关闭</li>\n" + 
			"		</ul>\n" + 
			"		<a href=\"###\" class=\"close icon18\">X</a>					</div>\n" + 
			"</div>		\n" + 
			"				<!--bottom-right-->\n" + 
			"<div id=\"cy\" class=\"bottom-right\">\n" + 
			"		<!--字的释义-->\n" + 
			"	<div class=\"t-bgs clearFix\" style=\"padding-right:0;width:819px;\">\n" + 
			"		<h1 id=\"word-key\" class=\"cn-left cn-bg\" seg=\"\" >\n" + 
			"						<img src=\"/static/img/loading.gif\" alt=\"加载中\" style=\"padding-top:52px;\"/>\n" + 
			"			<object type=\"application/x-shockwave-flash\" data=\"http://dict.cn/apis/output.php?id=hanzi_2_17791_0\" style=\"display:none;width:130px;height:130px;\">		\n" + 
			"				<param name=\"movie\" value=\"http://dict.cn/apis/output.php?id=hanzi_2_17791_0\"> 				\n" + 
			"				<param name=\"AllowScriptAccess\" value=\"always\"> 				\n" + 
			"				<param name=\"wmode\" value=\"opaque\"> 				\n" + 
			"				<param name=\"hasPriority\" value=\"true\"> 			\n" + 
			"			</object>\n" + 
			"					</h1>\n" + 
			"		<div style=\"width: 678px;\">\n" + 
			"							<p class=\"hz_pinyin clearFix\"><span class=\"zh_title\">拼&nbsp;&nbsp;音</span><span>cān</span><span>cēn</span><span>shēn</span></p>\n" + 
			"						<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">繁&nbsp;&nbsp;体</span><strong><a href=\"http://hanyu.dict.cn/參\">參</a></strong></li>\n" + 
			"				<li style=\"width:60%;\"><span class=\"zh_title \">异&nbsp;&nbsp;体</span><strong><a href=\"http://hanyu.dict.cn/叅\">叅</a></strong></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">部&nbsp;&nbsp;首</span><span>彡部</span></li>\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">笔&nbsp;&nbsp;画</span><span>8笔</span></li>\n" + 
			"				<li><span class=\"zh_title \">五&nbsp;&nbsp;笔</span><span>CDER</span></li>\n" + 
			"			</ul>\n" + 
			"			<ul class=\"clearFix\">\n" + 
			"				<li style=\"width:157px;\"><span class=\"zh_title \">结&nbsp;&nbsp;构</span><span>上下结构</span></li>\n" + 
			"				<li><span class=\"zh_title \">造字法</span><span>原为形声</span></li>\n" + 
			"			</ul>\n" + 
			"		</div>\n" + 
			"		<span class=\"seach-right st\"></span>\n" + 
			"	</div>\n" + 
			"	<!--tab-切换-字的释义--->\n" + 
			"	<div class=\"tab\" style=\"padding-top:15px;\">\n" + 
			"				<h6 class=\"title\">\n" + 
			"			<span class=\" current\">现代汉语</span>\n" + 
			"		</h6>\n" + 
			"		<div class=\"tabcontent block\">\n" + 
			"							<div id=\"ly\" class=\"cont-1\">\n" + 
			"					<ul class=\"L-L L-L2\">\n" + 
			"													<li style=\"padding:0;\">\n" + 
			"								<h2>参<span class=\"duyin\">cān																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_17791_1\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em><s></s>&nbsp;[动]</em>\n" + 
			"																																																																																	<p><span>加入：<em class=\"hz_sy\">～加｜～军｜～战</em></span></p>\n" + 
			"																																																																					<p><span>对照考察：<em class=\"hz_sy\">～考｜～看｜～阅</em></span></p>\n" + 
			"																																																																					<p><span>研究；领悟：<em class=\"hz_sy\">～禅｜～破｜～透</em></span></p>\n" + 
			"																																																																					<p><span>恭敬地进见；谒见：<em class=\"hz_sy\">～拜｜～谒｜～见</em></span></p>\n" + 
			"																																																																					<p><span>封建时代指弹劾（tánhé）；检举；揭发：<em class=\"hz_sy\">～劾｜～奏｜～他一本（“本”指奏章）</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[动]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>封建时代指弹劾（tánhé）；检举；揭发：～劾｜～奏｜～他一本（“本”指奏章）:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"													<li style=\"padding:0;padding-top:20px;\">\n" + 
			"								<h2>参<span class=\"duyin\">cēn																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_17791_2\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<div class=\"ciyu_c clearFix\"><em>1.&nbsp;</em><span>参差&nbsp;cēncī</span></div>\n" + 
			"																							<li><em>&nbsp;&nbsp;&nbsp;&nbsp;[形]</em>\n" + 
			"																																																									<p><span>长短、高低、大小不齐的、不一致的：<em class=\"hz_sy\">不齐～</em></span></p>\n" + 
			"																																							</li>\n" + 
			"																														</ul>\n" + 
			"																	<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<div class=\"ciyu_c clearFix\"><em>2.&nbsp;</em><span>参错&nbsp;cēncuò</span></div>\n" + 
			"																							<li><em>&nbsp;&nbsp;&nbsp;&nbsp;[形]</em>\n" + 
			"																																																									<p><span><书>参差交错：<em class=\"hz_sy\">阡陌纵横～</em></span></p>\n" + 
			"																																							</li>\n" + 
			"																							<li><em>&nbsp;&nbsp;&nbsp;&nbsp;[动]</em>\n" + 
			"																																																									<p><span><书>错误脱漏：<em class=\"hz_sy\">传（zhuàn）注～</em></span></p>\n" + 
			"																																							</li>\n" + 
			"																														</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																					<div class=\"ciyu_c clearFix\">3.&nbsp;<span>参错&nbsp;cēncuò</span></div>\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[]</em><p style=\"float:none;padding:0 0 0 28px;\"><span><书>错误脱漏：传（zhuàn）注～:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"													<li style=\"padding:0;padding-top:20px;\">\n" + 
			"								<h2>参<span class=\"duyin\">shēn																<img align=\"absmiddle\" style=\"vertical-align: middle;\" class=\"p_speaker speaker\" src=\"/static/img/cleardot.gif\" title=\"播放读音\" audio=\"hanzi_1_17791_3\"/></span>\n" + 
			"																</h2>\n" + 
			"																									<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em>1.&nbsp;[名]</em>\n" + 
			"																																																																																	<p><span>多年生草本植物。根肥大，略像人形。可入药：<em class=\"hz_sy\">～茸｜血～｜花旗～</em></span></p>\n" + 
			"																																																																					<p><span>各种参类药材的统称：<em class=\"hz_sy\">丹～｜沙～｜苦～</em></span></p>\n" + 
			"																																																																					<p><span>二十八宿之一：<em class=\"hz_sy\">～商｜日月～辰｜斗转～横</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																	<ul class=\"L-L1 L-L3 clearFix\">\n" + 
			"																					<li><em>2.&nbsp;[形]</em>\n" + 
			"																																																																																	<p><span>人参的；出产或销售人参的：<em class=\"hz_sy\">～茶｜～汤｜～场</em></span></p>\n" + 
			"																																				</li>\n" + 
			"																			</ul>\n" + 
			"																<ul class=\"L-L1 L-L3 clearFix\" style=\"display:none;\">\n" + 
			"									<li class=\"ciyu\">\n" + 
			"																				<div style=\"padding-left:15px;\" class=\"clearFix\"><em>[形]</em><p style=\"float:none;padding:0 0 0 28px;\"><span>人参的；出产或销售人参的：～茶｜～汤｜～场:<em class=\"hz_sy\">首～｜万～｜无～不作</em></span></p></div>\n" + 
			"									</li>\n" + 
			"								</ul>\n" + 
			"							</li>\n" + 
			"											</ul>\n" + 
			"				</div>\n" + 
			"					</div>\n" + 
			"		<div class=\"tabcontent \">\n" + 
			"					</div>\n" + 
			"	</div>\n" + 
			"	</div>\n" + 
			"\n" + 
			"	\n" + 
			"					\n" + 
			"			\n" + 
			"				<div style=\"margin-top:80px;\">\n" + 
			"			<!--分享微博-->\n" + 
			"			<div class=\"sc-fx\" id=\"page-share\">\n" + 
			"				<a onclick=\"addFavorite(location.href, document.title);\" href=\"###\">收藏</a>|<span>分享到：</span>\n" + 
			"				<a href=\"javascript:window.open('http://share.renren.com/share/buttonshare.do?link='+encodeURIComponent(document.location.href)+'&amp;title='+encodeURIComponent(document.title));void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"renren fx\" title=\"分享到人人网\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://www.kaixin001.com/repaste/share.php?rtitle='+encodeURIComponent(document.title)+'&amp;rurl='+encodeURIComponent(document.location.href)+'&amp;rcontent=');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"kaixin fx\" title=\"分享到开心网\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://v.t.sina.com.cn/share/share.php?title='+encodeURIComponent(document.title)+'&amp;url='+encodeURIComponent(location.href)+'&amp;source=dict.cn','_blank','width=615,height=505');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"sina fx\" title=\"分享到新浪微博\"></a>\n" + 
			"				<a href=\"javascript:window.open('http://t.163.com/article/user/checkLogin.do?link=http://news.163.com/&amp;source=dict.cn&amp;info='+encodeURIComponent(document.title)+' '+encodeURIComponent(location.href),'_blank','width=510,height=300');void(0)\" rel=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/cleardot.gif\" class=\"wangyi fx\" title=\"分享到网易微博\"></a>\n" + 
			"				<span style=\"position:absolute; right:60px;\"><a style=\"color:#326598;\" href=\"http://hanyu.dict.cn/more.php\">《海词汉语词典》 编者的话</a></span>\n" + 
			"			</div>\n" + 
			"			<div>\n" + 
			"				<!-- hanyu_dbanner -->\n" + 
			"				<div id='div-gpt-ad-1422601792026-1' style='width:728px; height:90px;'>\n" + 
			"					<script type='text/javascript'>\n" + 
			"					googletag.cmd.push(function() { googletag.display('div-gpt-ad-1422601792026-1'); });\n" + 
			"					</script>\n" + 
			"				</div>\n" + 
			"			</div>\n" + 
			"		</div>\n" + 
			"	</div>\n" + 
			"	<div class=\"cl\"></div>\n" + 
			"</div>\n" + 
			"<script type=\"text/javascript\" id=\"data-js\">\n" + 
			"	var $dict_id    = \"17791\";\n" + 
			"	var $dict_query = \"\\u53c2\";\n" + 
			"	var $dict_dict  = \"han\";\n" + 
			"</script>\n" + 
			"<script type=\"text/javascript\" src=\"http://i1.haidii.com/i1/js/external.js\"></script>\n" + 
			"<div id=\"footer\">\n" + 
			"    <p><a href=\"http://about.dict.cn/introduce\" ref=\"nofollow\">关于海词</a> - <a href=\"http://about.dict.cn/copyrightstatement?cur=1\" ref=\"nofollow\">版权声明</a> - <a href=\"http://about.dict.cn/contact\" ref=\"nofollow\">联系海词</a> - <a target=\"_blank\" href=\"http://dict.cn/dir/\">星级词汇</a> - <a target=\"_blank\" href=\"http://dict.cn/dir/ceindex.html\">汉字列表</a> - <a target=\"_blank\" href=\"http://hr.dict.cn\" ref=\"nofollow\">招贤纳士</a></p>\n" + 
			"	<p>&copy;2003 - 2017 <a href=\"http://dict.cn/\">海词词典</a>(Dict.CN) - 自 2003 年开始服务 &nbsp;<a target=\"_blank\" href=\"http://www.miitbeian.gov.cn\" ref=\"nofollow\">沪ICP备08018881号</a>&nbsp;&nbsp;<a target=\"_blank\" href=\"http://www.sgs.gov.cn/lz/licenseLink.do?method=licenceView&amp;entyId=20120601170952752\" style=\"text-decoration:none;background-color:white;\" ref=\"nofollow\"><img border=\"0\" src=\"http://i1.haidii.com/i1/images/gs_icon.gif\"></a> 		 	<a target=\"_blank\" href=\"http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=31011502000490\" style=\"text-decoration:none;background-color:white;\"  ref=\"nofollow\"><img src=\"http://i1.haidii.com/i1/images/beian.png\" /><span style=\"display:none; color:#939393;\">沪公网安备 31011502000490号</span></a>\n" + 
			"		 </p>\n" + 
			"	<p style=\"text-align: center;margin-top:10px;\"><a href=\"http://m.dict.cn\" target=\"_blank\">海词词典手机移动站</a></p>\n" + 
			"	</div>\n" + 
			"<script>var langt='';</script>\n" + 
			"\n" + 
			"\n" + 
			"\n" + 
			"	<script type=\"text/javascript\">\n" + 
			"	var cur_dict = 'hanyu', i1_home='http://i1.haidii.com', xuehai_home='http://xuehai.cn', passport_home='http://passport.dict.cn', $dict_id = null, $dict_query = \"\\u53c2\", $dict_dict  = \"han\", $user_id = 0, $dict_ver=1486623708;\n" + 
			"	</script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/i1/js/ddialog/ddialog.1.0.0.min.js\" crossorigin></script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1481554274/i1/js/inputPrompt.min.js\" crossorigin></script>\n" + 
			"	<script type=\"text/javascript\" src=\"http://i1.haidii.com/v/1481554264/i1/js/base.min.js\" crossorigin></script>\n" + 
			"	<div style=\"display:none;\">\n" + 
			"		<script type=\"text/javascript\">\n" + 
			"		var _bdhmProtocol = ((\"https:\" == document.location.protocol) ? \" https://\" : \" http://\");\n" + 
			"		document.write(unescape(\"%3Cscript src='\" + _bdhmProtocol + \"hm.baidu.com/h.js%3F8fd7837425ffd5a7fb88d32ea7060960' type='text/javascript'%3E%3C/script%3E\"));\n" + 
			"		</script>\n" + 
			"	</div>\n" + 
			"	<script type=\"text/javascript\">\n" + 
			"	var _gaq = _gaq || [];\n" + 
			"	_gaq.push(['_setAccount', 'UA-138041-2']);\n" + 
			"	_gaq.push(['_setDomainName', 'dict.cn']);\n" + 
			"	_gaq.push(['_trackPageview']);\n" + 
			"	(function() {\n" + 
			"		var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;\n" + 
			"		ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';\n" + 
			"		var s  = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);\n" + 
			"	})();\n" + 
			"	</script>\n" + 
			"\n" + 
			"<script type=\"text/javascript\">\n" + 
			"window.onerror = function(errorMsg, url, lineNumber, column, errorObj){\n" + 
			"	$.getScript('http://dict-log.cn-hangzhou.log.aliyuncs.com/logstores/jsreport/track?APIVersion=0.6.0&call=error&ver=' + ($dict_ver || '') + '&url=' + encodeURIComponent(url)+ '&line=' + lineNumber + '&emsg=' + encodeURIComponent(errorMsg) + '&page='+ _href \n" + 
			"			+ '&agent=' + encodeURIComponent(navigator.userAgent) + '&column=' + encodeURIComponent(column) + '&StackTrace=' + encodeURIComponent(errorObj));\n" + 
			"	return true;\n" + 
			"};\n" + 
			"</script>\n" + 
			"</body>\n" + 
			"</html>\n" + 
			"";
}
