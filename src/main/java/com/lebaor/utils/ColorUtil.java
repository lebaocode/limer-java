package com.lebaor.utils;

public class ColorUtil {
	public static float[] rgb2hsb(int rgbR, int rgbG, int rgbB) { 
	    assert 0 <= rgbR && rgbR <= 255; 
	    assert 0 <= rgbG && rgbG <= 255; 
	    assert 0 <= rgbB && rgbB <= 255; 
	    int[] rgb = new int[] { rgbR, rgbG, rgbB }; 
	    java.util.Arrays.sort(rgb); 
	    int max = rgb[2]; 
	    int min = rgb[0]; 
	 
	    float hsbB = max / 255.0f; 
	    float hsbS = max == 0 ? 0 : (max - min) / (float) max; 
	 
	    float hsbH = 0; 
	    if (max == rgbR && rgbG >= rgbB) { 
	        hsbH = (rgbG - rgbB) * 60f / (max - min) + 0; 
	    } else if (max == rgbR && rgbG < rgbB) { 
	        hsbH = (rgbG - rgbB) * 60f / (max - min) + 360; 
	    } else if (max == rgbG) { 
	        hsbH = (rgbB - rgbR) * 60f / (max - min) + 120; 
	    } else if (max == rgbB) { 
	        hsbH = (rgbR - rgbG) * 60f / (max - min) + 240; 
	    } 
	 
	    return new float[] { hsbH, hsbS, hsbB }; 
	} 
	
	public static void main(String[] args) {
		System.out.println(java.util.Arrays.toString(rgb2hsb(255, 83, 123)));
	}
}
