package com.lebaor.utils;

import java.util.HashMap;

public class HanziUtil {
	
	static String[][] PINYIN_YINDIAO = {
		{"ā", "a", "1"},
		{"á", "a", "2"},
		{"ǎ", "a", "3"},
		{"à", "a", "4"},
		{"ō", "o", "1"},
		{"ó", "o", "2"},
		{"ǒ", "o", "3"},
		{"ò", "o", "4"},
		{"ē", "e", "1"},
		{"é", "e", "2"},
		{"ě", "e", "3"},
		{"è", "e", "4"},
		{"ê", "e", "5"},
		{"ī", "i", "1"},
		{"í", "i", "2"},
		{"ǐ", "i", "3"},
		{"ì", "i", "4"},
		{"ū", "u", "1"},
		{"ú", "u", "2"},
		{"ǔ", "u", "3"},
		{"ù", "u", "4"},
		{"ǖ", "v", "1"},
		{"ǘ", "v", "2"},
		{"ǚ", "v", "3"},
		{"ǜ", "v", "4"}
	};
		
	static HashMap<String, Character> pinyins = new HashMap<String, Character>(); 
	static {
		pinyins.put("a1", 'ā');
        pinyins.put("a2", 'á');
        pinyins.put("a3", 'ǎ');
        pinyins.put("a4", 'à');
        pinyins.put("o1", 'ō');
        pinyins.put("o2", 'ó');
        pinyins.put("o3", 'ǒ');
        pinyins.put("o4", 'ò');
        pinyins.put("e1", 'ē');
        pinyins.put("e2", 'é');
        pinyins.put("e3", 'ě');
        pinyins.put("e4", 'è');
        pinyins.put("e5", 'ê');
        pinyins.put("i1", 'ī');
        pinyins.put("i2", 'í');
        pinyins.put("i3", 'ǐ');
        pinyins.put("i4", 'ì');
        pinyins.put("u1", 'ū');
        pinyins.put("u2", 'ú');
        pinyins.put("u3", 'ǔ');
        pinyins.put("u4", 'ù');
        pinyins.put("v1", 'ǖ');
        pinyins.put("v2", 'ǘ');
        pinyins.put("v3", 'ǚ');
        pinyins.put("v4", 'ǜ');
        pinyins.put("v0", 'ü');
        pinyins.put("v", 'ü');
        pinyins.put("ü1", 'ǖ');
        pinyins.put("ü2", 'ǘ');
        pinyins.put("ü3", 'ǚ');
        pinyins.put("ü4", 'ǜ');
        pinyins.put("ü0", 'ü');
        pinyins.put("ü", 'ü');
    }
	public static char[] BIAODIAN_FUHAO_CHN = "，。！？“”‘’；：……、《》（）【】".toCharArray();
	public static boolean isBiaodianChn(char ch) {
		for (char bd : BIAODIAN_FUHAO_CHN) {
			if (ch == bd) return true;
		}
		return false;
	}
	
	public static String[] SHENGMU_QIAOSHE = {"zh", "ch", "sh"};
	public static String[] SHENGMU_PINGSHE = {"z", "c", "s"};
	public static String[] YUNMU_QIANBIYIN = {"an", "en", "in", "un", "uan", "ian"};
	public static String[] YUNMU_HOUBIYIN = {"ang", "eng", "ing", "ong", "uang", "iang"};
	public static String[] SHENGMU_QING = {"p", "f", "t", "l", "k", "q", "x", "h"};
	public static String[] SHENGMU_ZHUO = {"b", "m", "d", "n", "g", "j", "j", "n"};
	public static String[] YICUO_YINDIAO_WEIZHI = {"ai", "ia", "ie", "ei", "iu", "ui", "uo", "ou"};
	public static HashMap<String, String[]> SIMILAR_PAIRS = new HashMap<String, String[]>();
	static {
		SIMILAR_PAIRS.put("zh", new String[]{"z"});
		SIMILAR_PAIRS.put("z", new String[]{"zh"});
		SIMILAR_PAIRS.put("ch", new String[]{"c"});
		SIMILAR_PAIRS.put("c", new String[]{"ch"});
		SIMILAR_PAIRS.put("sh", new String[]{"s"});
		SIMILAR_PAIRS.put("s", new String[]{"sh"});
		
		SIMILAR_PAIRS.put("an", new String[]{"ang", "uan", "ian", "ai"});
		SIMILAR_PAIRS.put("ang", new String[]{"an", "iang"});
		SIMILAR_PAIRS.put("uan", new String[]{"an", "uang"});
		SIMILAR_PAIRS.put("ian", new String[]{"an", "iang"});
		SIMILAR_PAIRS.put("iang", new String[]{"ian", "ang"});
		
		SIMILAR_PAIRS.put("en", new String[]{"eng", "in", "ei"});
		SIMILAR_PAIRS.put("eng", new String[]{"en", "ong", "ing"});
		
		SIMILAR_PAIRS.put("in", new String[]{"ing", "en"});
		SIMILAR_PAIRS.put("ing", new String[]{"in", "eng"});
		
		SIMILAR_PAIRS.put("un", new String[]{"ong", "en", "ui"});
		SIMILAR_PAIRS.put("ong", new String[]{"un", "eng"});
		
		SIMILAR_PAIRS.put("ai", new String[]{"an"});
		SIMILAR_PAIRS.put("ei", new String[]{"en"});
		SIMILAR_PAIRS.put("ui", new String[]{"un", "iu"});
		
		SIMILAR_PAIRS.put("iu", new String[]{"ui"});
		SIMILAR_PAIRS.put("ie", new String[]{"ei", "e"});
		SIMILAR_PAIRS.put("ve", new String[]{"uo", "e"});
		
		SIMILAR_PAIRS.put("o", new String[]{"e"});
		SIMILAR_PAIRS.put("e", new String[]{"o", "ve"});
		SIMILAR_PAIRS.put("u", new String[]{"v"});
		
		SIMILAR_PAIRS.put("p", new String[]{"b", "q"});
		SIMILAR_PAIRS.put("n", new String[]{"m"});
		SIMILAR_PAIRS.put("t", new String[]{"d"});
		SIMILAR_PAIRS.put("l", new String[]{"n"});
		SIMILAR_PAIRS.put("f", new String[]{"h"});
				
		SIMILAR_PAIRS.put("b", new String[]{"p"});
		SIMILAR_PAIRS.put("q", new String[]{"p"});
		SIMILAR_PAIRS.put("m", new String[]{"n"});
		SIMILAR_PAIRS.put("d", new String[]{"t"});
		SIMILAR_PAIRS.put("n", new String[]{"l"});
		SIMILAR_PAIRS.put("h", new String[]{"f"});
	}
	
	public static String[] getSimilarYunmus(String yunmu) {
		String tym = translatePinyin(yunmu);
		return SIMILAR_PAIRS.get(tym);
	}
	public static String[] getSimilarShengmus(String shengmu) {
		String tym = translatePinyin(shengmu);
		return SIMILAR_PAIRS.get(tym);
	}
	
	public static String getShengmu(String pinyin) {
		for (String sm : SHENGMU_QIAOSHE) {
			if (pinyin.startsWith(sm)) {
				return sm;
			}
		}
		
		String s = translatePinyin(pinyin);
		char lastCh = s.charAt(s.length() -1);
		if (lastCh >= '0' && lastCh <= '5') {
			 s = s.substring(0, s.length() - 1);
		}
		if (s.length() == 1) return "";//可能有一位字母的拼音，比如e, a
		
		return pinyin.substring(0, 1);
	}
	//输入需要是pīn，而不能是pin1
	public static String getYunmu(String pinyin) {
		String s = translatePinyin(pinyin);
    	char lastCh = s.charAt(s.length() -1);
		if (lastCh >= '0' && lastCh <= '5') {
			 s = s.substring(0, s.length() - 1);
		}
		
		String sm = getShengmu(s);
		return s.substring(sm.length());
	}
		
    protected HanziUtil() {
    	
    }
    //将pīn改为pin1这种字样
    public static String translatePinyin(String pinyin) {
    	if (pinyin == null) return null;
    	String s = pinyin.replace('ü', 'v');
    	
    	for (String[] arr : PINYIN_YINDIAO) {
    		if (s.indexOf(arr[0]) != -1) {
    			s = s.replace(arr[0], arr[1]);
    			if (!arr[2].equals("0")) s += arr[2];
    			return s;
    		}
    	}
    	return s;
    }
    
    public static String getSentencePinyin(String sentence) {
    	StringBuffer sb = new StringBuffer();
    	String[] arr = sentence.split(" ");
    	for (String s : arr) {
    		if (sb.length() > 0) sb.append(" ");
    		sb.append(getPinyin(s));
    	}
    	return sb.toString();
    }
    
    /**
     * 输入feng2, 输出féng
     * 输入yao4，输出yào
     * @param input
     * @return
     */
    public static String getPinyin(String input) {
//    	System.out.print(input + "=");
    	try {
	    	int yindiao = -1;
	    	char lastCh = input.charAt(input.length() -1);
			if (lastCh >= '0' && lastCh <= '5') yindiao = lastCh - '0';
			
			//先替换v为ü
			input = input.replace('v', 'ü');
						
			//没有音调，则不需要标
			if (yindiao == -1) {
				return input;
			}
			input = input.substring(0, input.length() - 1);
			
			//如果有音调
	    	
			//有a不放过
	    	if (input.indexOf('a') != -1) {
    			return input.replace('a', pinyins.get("a" + yindiao));
	    	}
	    	
	    	//没a找o、e
	    	if (input.indexOf('o') != -1) {
	    		return input.replace('o', pinyins.get("o" + yindiao));
	    	}
	    	if (input.indexOf('e') != -1) {
	    		return input.replace('e', pinyins.get("e" + yindiao));
	    	}
	    	
	    	//i、u并列标在后
	    	if (input.indexOf("iu") != -1) {
	    		return input.replace("iu", "i"+pinyins.get("u" + yindiao));
	    	}
	    	if (input.indexOf("ui") != -1) {
	    		return input.replace("ui", "u"+pinyins.get("i" + yindiao));
	    	}
	    	
	    	//单韵母不必说
	    	if (input.indexOf('i') != -1) {
	    		return input.replace('i', pinyins.get("i" + yindiao));
	    	}
	    	if (input.indexOf('u') != -1) {
	    		return input.replace('u', pinyins.get("u" + yindiao));
	    	}
	    	if (input.indexOf('ü') != -1) {
	    		return input.replace('ü', pinyins.get("v" + yindiao));
	    	}
    	} catch (Exception e) {
    		
    	}
    	return input;
    }
    
    public static void main(String[] args) {
    	System.out.println(getPinyin("a"));
    	System.out.println(getPinyin("er"));
    	
    	System.out.println(getPinyin("a0"));
    	System.out.println(getPinyin("er0"));
    	
    	System.out.println(getPinyin("ba3"));
    	System.out.println(getPinyin("wo3"));
    	System.out.println(getPinyin("le4"));
    	System.out.println(getPinyin("qi2"));
    	System.out.println(getPinyin("pu1"));
    	System.out.println(getPinyin("lv3"));
    	
    	System.out.println(getPinyin("bai1"));
    	System.out.println(getPinyin("bei4"));
    	System.out.println(getPinyin("gui3"));
    	System.out.println(getPinyin("yao1"));
    	System.out.println(getPinyin("you2"));
    	System.out.println(getPinyin("niu3"));
    	
    	System.out.println(getPinyin("lie4"));
    	System.out.println(getPinyin("lve4"));
    	System.out.println(getPinyin("er2"));
    	
    	System.out.println(getPinyin("lan4"));
    	System.out.println(getPinyin("fen1"));
    	System.out.println(getPinyin("pin1"));
    	System.out.println(getPinyin("lun4"));
    	System.out.println(getPinyin("jun4"));//jxq 挖yu
    	
    	System.out.println(getPinyin("lang4"));
    	System.out.println(getPinyin("feng1"));
    	System.out.println(getPinyin("ping1"));
    	System.out.println(getPinyin("long2"));
   
    	System.out.println(getPinyin("zhi1"));
    	System.out.println(getPinyin("chi1"));
    	System.out.println(getPinyin("shi1"));
    	System.out.println(getPinyin("ri4"));
    	
    	System.out.println(getPinyin("yi1"));
    	System.out.println(getPinyin("wu1"));
    	System.out.println(getPinyin("yv2"));
    	
    	System.out.println(getPinyin("ye2"));
    	System.out.println(getPinyin("yue4"));
    	System.out.println(getPinyin("yuan1"));
    	
    	System.out.println(getPinyin("yin1"));
    	System.out.println(getPinyin("yun2"));
    	System.out.println(getPinyin("ying2"));
    	
    	System.out.println(getPinyin("luo4"));
    	System.out.println(getPinyin("gui4"));
    	System.out.println(getPinyin("jia1"));
    	System.out.println(getPinyin("jie1"));
    	
    	
    	
    	//ü  ün  üe
    	
    	
    	
    	System.out.println(getPinyin("gui3"));
    	System.out.println(getPinyin("ao4"));
    	
    	System.out.println(translatePinyin("guǐ"));
    	System.out.println(translatePinyin("lüè"));
    	
    }
}
