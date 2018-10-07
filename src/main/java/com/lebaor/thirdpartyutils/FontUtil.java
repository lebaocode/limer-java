package com.lebaor.thirdpartyutils;

import com.google.typography.font.tools.sfnttool.SfntTool;


public class FontUtil {
	public static void genFontFile(String chn, String filePath) throws Exception {
		String s = "-s " + chn + " simkai.ttf " + filePath;
		SfntTool.main(s.split(" "));
	}
	
	public static void main(String[] args) throws Exception {
		String chn = "霜" + 
				"吹" + 
				"落" + 
				"降" + 
				"飘" + 
				"游" + 
				"池" + 
				"入" + 
				"春" + 
				"冬" + 
				"风" + 
				"雪" + 
				"花" + 
				"飞" + 
				"";
		String s = "-s " + chn + " simkai.ttf small-kaiti.ttf";
		SfntTool.main(s.split(" "));
		
		SfntTool.main(args);
	}
}
