package com.lebaor.utils;

import java.io.File;


//判断操作系统
public class OSCheckUtils {
	private static String TEST_DATA_DIR_LINUX = "";
	private static String TEST_DATA_DIR_WINDOWS = "C:\\work";
	
	public static String getAbsolutePath(String fileName) {
		boolean isLinux = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("linux") != -1) {
			isLinux = true;
		}
		
		if (isLinux) {
			return TEST_DATA_DIR_LINUX + "/" + fileName;
		} else {
			return TEST_DATA_DIR_WINDOWS + "\\" + fileName;
		}
	}
	
	public static String[] listDataDir() {
		boolean isLinux = false;
		String osName = System.getProperty("os.name");
		if (osName.toLowerCase().indexOf("linux") != -1) {
			isLinux = true;
		}
		
		if (isLinux) {
			File dir = new File(TEST_DATA_DIR_LINUX);
			return dir.list();
		} else {
			File dir = new File(TEST_DATA_DIR_WINDOWS);
			return dir.list();
		}
	}
}
