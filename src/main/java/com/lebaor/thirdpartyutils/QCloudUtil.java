package com.lebaor.thirdpartyutils;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.FileUtil;
import com.lebaor.utils.TextUtil;
import com.lebaor.webutils.HttpClientUtil;

public class QCloudUtil {
	public static final String APPID = "1251024336";
	public static final String APP_SECRET_ID = "AKIDkIciYKHHRO7dv0cVwAAwa0CAwVqxxWyC";
	public static final String APP_SECRET_KEY = "96pKbhaFguviHcjeLo47xZy98tQkrkPq";
	
	private static final String NONCE = "268495";//
	private static final int UTF_8_CODE = 2097152;
	
	//Action: LexicalAnalysis分词  TextDependency句法分析 
	
	private static final String CACHE_FILE = "qcloud_res_cache.txt";
	private static HashMap<String, String> CACHE = new HashMap<String, String>();
	private static boolean needSaveCache = false;
	
	public static void initCache() {
		try {
			if (FileUtil.existsFile(CACHE_FILE)) {
				//init cache
				String s = FileUtil.readFromFile(CACHE_FILE);
				JSONArray arr = JSONArray.fromObject(s);
				for (int i = 0; i < arr.size(); i++) {
					JSONObject o = arr.getJSONObject(i);
					String t = o.getString("text");
					String json = o.getString("json");
					CACHE.put(t, json);
				}
			}
			LogUtil.WEB_LOG.debug("read cache " + CACHE.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void saveCacheToDisk() throws Exception {
		if (!needSaveCache) return;
		
		JSONArray arr = new JSONArray();
		for (String text : CACHE.keySet()) {
			String json = CACHE.get(text);
			JSONObject o = new JSONObject();
			o.put("text", text);
			o.put("json", json);
			arr.add(o);
		}
		LogUtil.WEB_LOG.debug("write to file " + CACHE.size());
		
		String s= arr.toString();
		s = s.replaceAll("\\[\\{", "[\n{").replaceAll("},", "},\n").replaceAll("}]", "}]\n");
		
		FileUtil.writeToFile(s, CACHE_FILE, false);
	}
	public static LexicalToken[] lexicalAnalysis(String text) {
		return lexicalAnalysis(text, false);
	}
	public static LexicalToken[] lexicalAnalysis(String text, boolean allowCombo) {
		
		//先从cache里取
		String json = CACHE.get(text);
		if (json != null) {
			LexicalToken[] tokens = readFromJson(json, allowCombo);
			if (tokens != null) {
				LogUtil.WEB_LOG.debug("read from cache");
				return tokens;
			}
		}
		
		LogUtil.WEB_LOG.debug("read from QCloud");
		return lexicalAnalysisInternal(text, allowCombo); 
	}
	
	private static LexicalToken[] lexicalAnalysisInternal(String text, boolean allowCombo) {
		if (text.trim().length() == 0) {
			return new LexicalToken[0];
		}
		
		try {
			String region = "sz";
			int type = 0;//0为基础粒度版分词，倾向于将句子切分的更细，在搜索场景使用为佳。 1为混合粒度版分词，倾向于保留更多基本短语不被切分开。
			long timestamp = System.currentTimeMillis()/1000;
			
			String str = "GETwenzhi.api.qcloud.com/v2/index.php?"
					+ "Action=LexicalAnalysis"
					+ "&Nonce=" + NONCE
					+ "&Region=" + region 
					+ "&SecretId=" + APP_SECRET_ID
					+ "&Timestamp=" + timestamp
					+ "&code="+ UTF_8_CODE +"&text=" + text + "&type="+ type
					;
			
			String sign = TextUtil.base64Encode(TextUtil.hmacSHA1Bytes(str, APP_SECRET_KEY));
//			System.out.println(str);
//			System.out.println(sign);
//			String body = "code="+ UTF_8_CODE +"&type="+ type +"&text=" + URLEncoder.encode(text, "utf-8");
//			HashMap<String, String> headers = new HashMap<String, String>();
			//headers.put("Content-Type", "application/x-www-form-urlencoded");
			
			String url = "https://wenzhi.api.qcloud.com/v2/index.php?"
					+ "Action=LexicalAnalysis"
					+ "&Nonce=" + NONCE
					+ "&Region=" + region 
					+ "&SecretId=" + APP_SECRET_ID
					+ "&Signature=" + URLEncoder.encode(sign, "utf-8")
					+ "&Timestamp=" + timestamp
					+ "&code="+ UTF_8_CODE +"&text=" + URLEncoder.encode(text, "utf-8") + "&type="+ type
					;
//			System.out.println(url);
			String json = HttpClientUtil.doGet(url);
//			String json = HttpClientUtil.doPost(url, body, headers);
//			System.out.println(json);
			LogUtil.WEB_LOG.debug(json);
			
			CACHE.put(text, json);
			needSaveCache = true;
			
			return readFromJson(json, allowCombo);
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("lexicalAnalysis parse error: ["+ text +"]", e);
		}
		return new LexicalToken[0];
	}
	
	private static LexicalToken[] readFromJson(String json, boolean allowCombo) {
		LinkedList<LexicalToken> list = new LinkedList<LexicalToken>();
		JSONObject ret = JSONObject.fromObject(json);
		if (ret.getInt("code") == 0) {
			JSONArray arr = ret.getJSONArray("tokens");
			for (int i = 0; i < arr.size(); i++) {
				JSONObject o = arr.getJSONObject(i);
				LexicalToken t = new LexicalToken();
				t.setLen(Integer.parseInt(o.getString("wlen")));
				t.setPos(o.getInt("pos"));
				t.setWord(o.getString("word"));
				t.setWtype(o.getString("wtype"));
				list.add(t);
			}
			
			if (allowCombo) {
				arr = ret.getJSONArray("combtokens");
				for (int i = 0; i < arr.size(); i++) {
					JSONObject o = arr.getJSONObject(i);
					if (o == null || o.isNullObject()) continue;
					LexicalToken t = new LexicalToken();
					t.setLen(Integer.parseInt(o.getString("wlen")));
					t.setPos(o.getInt("pos"));
					t.setWord(o.getString("word"));
					t.setWtype(o.getString("cls"));
					list.add(t);
				}
			}
			
			//LogUtil.WEB_LOG.debug("lexicalAnalysis parse success: text=" + text + ", ret=" + list);
		} else {
			String msg = ret.getString("message");
//			System.out.println(msg);
			LogUtil.WEB_LOG.warn("lexicalAnalysis parse error:" + msg + " json=" + json);
		}
		return list.toArray(new LexicalToken[0]);
	}
	
	public static class LexicalToken {
		String word;//切分出来的基础词
		int pos;//该基础词在文本中的起始位置
		String wtype;//基础词的词性
		int len; //该基础词的长度
		
		public String toString() {
			return "word=" + word + "&wtype=" + wtype + "&pos=" + pos + "&len=" + len;
		}
		
		public String getWord() {
			return word;
		}
		public void setWord(String word) {
			this.word = word;
		}
		public int getPos() {
			return pos;
		}
		public void setPos(int pos) {
			this.pos = pos;
		}
		public String getWtype() {
			return wtype;
		}
		public void setWtype(String wtype) {
			this.wtype = wtype;
		}
		public int getLen() {
			return len;
		}
		public void setLen(int len) {
			this.len = len;
		}
		
		
	}
	
	public static void main(String[] args) throws Exception {
		LexicalToken[] tokens = QCloudUtil.lexicalAnalysis("下雪啦，下雪啦！\n" + 
				"  雪地里来了一群小画家。\n" + 
				"  小鸡画竹叶，小狗画梅花。\n" + 
				"  小鸭画枫叶，小马画月牙。\n" + 
				"  不用颜料不用笔，\n" + 
				"  几步就成一幅画。\n" + 
				"  青蛙为什么没参加？\n" + 
				"  他在洞里睡着啦。");
		System.out.println(java.util.Arrays.toString(tokens));
		
//		String msg = "GETcvm.api.qcloud.com/v2/index.php?Action=DescribeInstances&Nonce=11886&Region=gz&SecretId=AKIDz8krbsJ5yKBZQpn74WFkmLPx3gnPhESA&Timestamp=1465185768&instanceIds.0=ins-09dx96dg&limit=20&offset=0";
//		byte[] b = TextUtil.hmacSHA1Bytes(msg, "Gu5t9xGARNpq86cd98joQYCN3Cozk1qA");
//		String s = TextUtil.base64Encode(b);
//		System.out.println(s);
	}
}
