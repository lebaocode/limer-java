package com.lebaor.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtil {
	public static boolean existsFile(String fileName) throws Exception {
		return new java.io.File(fileName).exists();
	}
	
	public static void writeToFile(String text, String fileName, boolean append) throws Exception {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName, append), "utf-8"));
			writer.write(text);
			writer.flush();
		} finally {
			if (writer != null) writer.close();
		}
	}
	
	public static String readFromFile(String fileName, String encoding) throws Exception {
		StringBuffer sb = new StringBuffer();
		BufferedReader reader = null;
		try {
			if (encoding != null) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), encoding));
			} else {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
			}
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (sb.length() > 0) sb.append("\n");
				sb.append(line);
			}
		} catch (Exception e) {
			LogUtil.info("readFromFile exception, file=" + new java.io.File(fileName).getAbsolutePath() + ", " + e.getMessage());
		} finally {
			if (reader != null) reader.close();
		}
		return sb.toString();
	}
	
	public static String readFromFile(String fileName) throws Exception {
		return readFromFile(fileName, "utf-8");
	}
}
