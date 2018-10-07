package com.lebaor.webutils;

import java.net.URI;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.Element;

import com.lebaor.utils.TextUtil;

public class ViewUtils {

    public static final String defaultEncoding = "UTF-8";
    private ThreadLocal<SimpleDateFormat> dateFormat = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };
    public static final long HUNDRED_MILLION = 100000000L;
    public static final long TEN_THOUSAND = 10000L;
    protected static ViewUtils singleton;
    
    static {
        singleton = new ViewUtils();
    }
    protected ViewUtils() {
        
    }
    public static ViewUtils getInstance() {
        return singleton;
    }
    
    /**
     * Encode URL using defaultEncoding
     * @param s
     * @return
     */
    public static String encodeURL(String s) {
        return encodeURL(s, defaultEncoding);
    }
    /**
     * Encode URL using given charset
     * @param s
     * @param charset
     * @return the encoded URL in the format "%xx%xx..". If failed, return ""
     */
    public static String encodeURL(String s, String charset) {
        try {
            return URLEncoder.encode(s, charset);
        } catch (Exception e) {
            return "";
        }
    }
    
    public static String escapeDoubleQuote(String s) {
    	return s.replace("\"", "\\\"");
    }
    public static String escapeSingleQuote(String s) {
    	return s.replace("'", "\\'");
    }

    /**
     * Convert special characters into HTML format.
     * will return "" if input is null
     */
    public static String getHTMLValidText(String s) {
        if(s == null) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        int len = s.length();
        for(int i = 0; i < len; i++) {
            if(s.charAt(i) == '"') {
                buffer.append("&quot;");
            } else if(s.charAt(i) == '&') {
                buffer.append("&amp;");
            } else if(s.charAt(i) == '<') {
                buffer.append("&lt;");
            } else if(s.charAt(i) == '>') {
                buffer.append("&gt;");
            } else if(s.charAt(i) == '\n') {
                buffer.append("<br/>");
            } else if(s.charAt(i) == '\r') {
                
            } else if(s.charAt(i) == ' ') {
                buffer.append("&nbsp;");
            } else {
                buffer.append(s.charAt(i));
            }
        }
        return buffer.toString();
    }

    public static String revertHTMLText(String s){
        return s.replaceAll("&lt;","<").replaceAll("&gt;",">").replaceAll("&quot;","\"").replaceAll("&amp;","&");
    }

    public static String truncateURL(String url, int length) {
        String result = url;
        if(url.startsWith("http://")) {
            result = url.substring("http://".length());
        }
        if(result.length() <= length) {
            return result;
        }
        return result.substring(0, length - 4) + " ...";
    }
    public String formatSize(long size) {
        if(size < 1024) {
            return Long.toString(size);
        }
        return size/1024 + "K";
    }
    public String formatDate(Date date) {
        return dateFormat.get().format(date);
    }
    public String formatBigNumber(long number) {
        String numStr = Long.toString(number);
        StringBuilder buffer = new StringBuilder();
        for(int i = 0; i < numStr.length(); i++) {
            if(i % 4 == 0 && i > 0) {
                buffer.append(" ");
            }
            buffer.append(numStr.charAt(numStr.length() - i - 1));
        }
        return buffer.reverse().toString();
    }

    public String formatDouble(double value, int precision) {
        String orig = String.format("%." + precision + "f", value);
        int dot = orig.indexOf(".");
        if(dot == -1) {
            return orig;
        }
        int tail = orig.length() - 1;
        while(tail > 0 && orig.charAt(tail) == '0') {
            tail --;
        }
        if(tail == dot) {
            tail --;
        }
        return orig.substring(0, tail + 1);
    }
    public static <T> String printArray(T[] array) {
        StringBuilder buffer = new StringBuilder();
        for(T t : array) {
            if(buffer.length() > 0) {
                 buffer.append(" ,");
            } else {
                 buffer.append("{");
            }
            buffer.append(t.toString());
        }
        buffer.append("}");
        return buffer.toString();
    }
    /**
     * Display the number in a Chinese-style segmented format
     * @param num
     * @return
     */
    public static String formatNumber(long num){
        if(num == 0) {
            return "0";
        }
        int significantDigit = 3;
        final String[] units = new String[]{"", "万", "亿"};
        String orig = Long.toString(num);
        if(significantDigit > orig.length()) {
            significantDigit = orig.length();
        }
        StringBuilder ret = new StringBuilder();
        boolean seenNoneZero = false;
        for(int i = 0; i < orig.length(); i++) {
            int left = orig.length() - i - 1;
            int seg = left / 4;
            if(i > significantDigit - 1) {
                if(seenNoneZero) {
                    ret.append('0');
                }
            } else {
				char theChar = orig.charAt(i);
                if(theChar != '0' || seenNoneZero) {
                	ret.append(theChar);
					if(theChar != '0') {
	                    seenNoneZero = true;
    	            }
				}
            }
            if(left % 4 == 0 && seg < units.length) {
                seenNoneZero = false;
                // output a segment unit
                ret.append(units[seg]);
                if(i >= significantDigit - 1) {
                    break;
                }
                if(left > 0 && Long.parseLong(orig.substring(i + 1, significantDigit)) == 0) {
                    // the rest digits are all zero
                    break;
                }
            }
        }
        return ret.toString();
    }

    /**
     * Split the string by " "
     * @param s
     * @return
     */
    public static ArrayList<String> splitString(String s){
    	if(s == null) {
    		return null;
    	}
    	ArrayList<String> list = new ArrayList<String>();
    	String []str = s.split(" +");
    	for(int i = 0; i < str.length; i++){
    		list.add(str[i]);
    	}
    	return list;
    }

    /**
     * Delete duplicate items
     */
    public static ArrayList<String> unique(Collection<String> s) {
        ArrayList<String> result = new ArrayList<String>(s.size());
        for(String item : s) {
            if(!result.contains(item)) {
                result.add(item);
            }
        }
        return result;
    }
    
    public static ArrayList<String> unique(String []s){
            List<String> list = Arrays.asList(s);
            return unique(list);
    }

    /**
     * 为百科设计的
     * <result><movie>...</movie></result>
     * <result><advertise>...</advertise></result>
     * @param resultDocuments
     * @param type
     * @return
     */
    public Vector<Element> getSelectedTypeElements(Vector<Document> resultDocuments, String type){
    	Vector<Element> result = new Vector<Element>();
		if (resultDocuments == null)
			return result;
		outer: for (Document doc : resultDocuments) {
			// just judge the doc type
			Element root = doc.getRootElement();
			for (@SuppressWarnings("rawtypes")
			Iterator it = root.elementIterator(); it.hasNext();) {
				Element element = (Element) it.next();
				String name = element.getName();
				if (name.equals(type)) {
					result.add(element);
					// now we just need one result
					break outer;
				}
			}
		}
		return result;
    }

    /**
	 * Estimate the probability of an encoded String to be UTF-8 encoded
	 *
	 * @return the probability between 0 and 100
	 * @author Xu Dongqi
	 */
    public boolean probablyUTF8(byte[] rawtext) {
        int score = 0;
        int i, rawtextlen = 0;
        int goodbytes = 0, asciibytes = 0;

        // Maybe also use UTF8 Byte Order Mark: EF BB BF

        // Check to see if characters fit into acceptable ranges
        rawtextlen = rawtext.length;
        for (i = 0; i < rawtextlen; i++) {
            if ((rawtext[i] & (byte) 0x7F) == rawtext[i]) { // One byte
                asciibytes++;
                // Ignore ASCII, can throw off count
            } else if (-64 <= rawtext[i] && rawtext[i] <= -33
                    && // Two bytes
                    i + 1 < rawtextlen && -128 <= rawtext[i + 1]
                    && rawtext[i + 1] <= -65) {
                goodbytes += 2;
                i++;
            } else if (-32 <= rawtext[i]
                    && rawtext[i] <= -17
                    && // Three bytes
                    i + 2 < rawtextlen && -128 <= rawtext[i + 1]
                    && rawtext[i + 1] <= -65 && -128 <= rawtext[i + 2]
                    && rawtext[i + 2] <= -65) {
                goodbytes += 3;
                i += 2;
            }
        }

        if (asciibytes == rawtextlen) {
            return false;
        }

        score = (int) (100 * ((float) goodbytes / (float) (rawtextlen - asciibytes)));
        // If not above 98, reduce to zero to prevent coincidental matches
        // Allows for some (few) bad formed sequences
        if (score > 98) {
            return true;
        } else if (score > 95 && goodbytes > 30) {
            return true;
        } else {
            return false;
        }
    }

    public static URI probablyUrl(String input) {
        return UrlUtils.probablyUrl(input);
    }

    
    /**
     * split the query string
     * <br/>
     * "test" -> "test"
     * <br/>
     * "" test1 test2" test3" -> " test1 test2", "test3"
     *
     * @param query
     * @return
     */
    public static String[] parseQueryString(String query){
        if(query == null || query.length() == 0)
            return new String[0]; 
        String []result;
        try {
            result = parseString(query);
        } catch (RuntimeException e) {
            e.printStackTrace();
            result = query.split(" +");
        }
        
        return result;
    }
    
    private static boolean allSpace(String s){
        return s.trim().length() == 0;
    }

    private static String[] parseString(String query){
        char []queryChars = query.toCharArray();
        int queryLength = queryChars.length;

        ArrayList<String> list = new ArrayList<String>();
        int quotFlag = -1;
        int quotStart = 0;
        int quotEnd = 0;
        int spaceStart = 0;
        for(int i = 0; i < queryLength; i++ ){
            char c = queryChars[i];
            if(c == '"' || c == '“' || c == '”'){
                //
                if(quotFlag == -1){
                    quotFlag = 1; 
                    quotStart = i + 1;
                    if(spaceStart < i){
                        String tmp = query.substring(spaceStart, i);
                        list.add(tmp); 
                        spaceStart = i + 1;
                    }
                }
                quotFlag = 1 - quotFlag;
            
            }
            //end of quot
            if(quotFlag == 1){
                quotFlag = -1;
                quotEnd = i; 
                if(quotStart < quotEnd){
                    String tmp = query.substring(quotStart, quotEnd);
                    if(!allSpace(tmp))
                        list.add(tmp);
                }
                quotStart = quotEnd + 1;
                spaceStart = quotStart;
            }
            if(c == ' '){
                //space within quot
                if(quotFlag == 0){
                    continue;
                }else{
                    if(spaceStart < i){
                        String tmp = query.substring(spaceStart, i);
                        list.add(tmp);
                    }
                    spaceStart = i + 1;
                }
            }
        }
        
        //left one quot
        if(quotFlag == 0){ 
            if(quotStart < queryLength){
                String tmp = query.substring(quotStart, queryLength);
                String []str = tmp.split(" +");
                for(String s : str){
                    if(!s.trim().equals(""))
                        list.add(s);
                }
            }
        }
        if(quotFlag == -1){ 
            if(spaceStart < queryLength){
                String tmp = query.substring(spaceStart, queryLength);
                list.add(tmp);
            }
        }
        
        return list.toArray(new String[0]);
    }
    
    public enum CharType{
        DELIMITER,
        NUM,
        LETTER, // latin-1 letters and fullwidth letters
        OTHER,
        CHINESE;
    }
    /**
     * Get the type of the character
     */
    public static CharType getCharType(char c) {
        CharType ct =null;

        //Chinese, 0x4e00-0x9fbb
        if ((c >= 0x4e00)&&(c <= 0x9fbb)){
            ct = CharType.CHINESE;
        }

        //Halfwidth and Fullwidth Forms 0xff00-0xffef
        else if ( (c >= 0xff00) &&(c <= 0xffef)) {
            // 2 byte letters
            if ((( c >= 0xff21 )&&( c <= 0xff3a)) ||
                    (( c >= 0xff41 )&&( c <= 0xff5a))){
                ct = CharType.LETTER;
            }

            //2 byte digits
            else if (( c >= 0xff10 )&&( c <= 0xff19)  ){
                ct = CharType.NUM;
            }

            else ct = CharType.DELIMITER;
        }

        //basic latin， 0000-007f
        else if ( (c >= 0x0021) &&(c <= 0x007e)){
            //1 byte digit
            if (( c >= 0x0030 )&&( c <= 0x0039)  ){
                ct = CharType.NUM;
            }
            //1 byte letter
            else if ((( c >= 0x0041 )&&( c <= 0x005a)) ||
                    (( c >= 0x0061 )&&( c <= 0x007a)))        {
                ct = CharType.LETTER;
            }
            else ct = CharType.DELIMITER;
        }

        //latin-1 0080-00ff
        else if ( (c >= 0x00a1) &&(c <= 0x00ff)){
            if (( c >= 0x00c0 )&&( c <= 0x00ff)){
                ct = CharType.LETTER;
            }
            else ct = CharType.DELIMITER;
        }
        else ct = CharType.OTHER;
        return ct;
    }
    /**
     * Generate a List of integers which represents the page numbers should be shown in a page turning bar.
     * The page number is one-based.
     * @param page current page number
     * @param maxPage maximum available page number
     * @param leftCount the max number of page numbers to be shown at the left of current page
     * @param rightCount the max number of page numbers to be shown at the right of current page
     * @return a List if integers representing the page numbers, in the right order. NOTE: -1 will be given
     *         instead of 1 if the second number is not 2.
     */
    public static List<Integer> generatePageTurningBar(int page, int maxPage, int leftCount, int rightCount) {
        // greatest page number to show
        int rightPage = page + rightCount;
        // smallest page number to show
        int leftPage = page - leftCount;
        int allCount = leftCount + rightCount;
        if (leftPage < 1) leftPage = 1;
        if (rightPage - leftPage < allCount) rightPage = leftPage + allCount;
        if (rightPage > maxPage) rightPage = maxPage;
        if (rightPage - leftPage < allCount) leftPage = rightPage - allCount;
        if (leftPage < 1) leftPage = 1;
        List<Integer> pages = new ArrayList<Integer>();
        if(leftPage >= 2) {
            pages.add(-1);
            leftPage += 2;
        }
        for (int i = leftPage; i <= rightPage; i++) {
            pages.add(i);
        }
        return pages;
    }
    
    public static String formatTime(long time) {
    	return String.format("%1$tF %1$tT", time);
    }
    
    private static final long DAY = 24*60*60*1000L;
    public static String formatDisplayedTime(long time) {
    	Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		long todayZeroClock = cal.getTimeInMillis();
		
		cal.setTimeInMillis(time);
		if (time >= todayZeroClock) {
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
			return hour + ":" + (min < 10 ? "0" : "") + min;
		} else if (time >= todayZeroClock - DAY){
			return "昨天";
		} else {
			return (cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日";
		}
    }
    
    public static boolean isEmptyString(Object o) {
    	if (o == null) return true;
    	String s = o.toString();
    	return s.trim().length() == 0;
    }
    
    public static String getToday() {
    	return TextUtil.formatDay(System.currentTimeMillis());
    }
    
    public static String getTodayDate() {
    	return TextUtil.formatDay2(System.currentTimeMillis());
    }
    
}
