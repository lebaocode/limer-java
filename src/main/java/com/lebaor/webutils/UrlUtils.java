package com.lebaor.webutils;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class UrlUtils {

    private static final Pattern PROTOCOL_PATTERN = Pattern.compile("" +
    		"(^[a-zA-Z]+)://.+");

    /**
     * Get a URL from a String that is probably a URL or a shortened URL
     * 
     * @param input
     * @return null if the String is probably not a URL
     */
    public static URI probablyUrl(String input) {
        if (input == null) {
            return null;
        }
        input = input.trim();
        Matcher protocolMatcher = PROTOCOL_PATTERN.matcher(input);
        String protocol = "http";
        int hostStart = 0;
        if (protocolMatcher.matches()) {
            protocol = protocolMatcher.group(1);
            hostStart = protocol.length() + 3;
        }
        try {
            URI url = new URI(protocol + "://" + input.substring(hostStart));
            String host = url.getHost();
            if(IpUtils.isIPv4(host)) {
                try {
                    InetAddress addr = InetAddress.getByName(host);
                    if(IpUtils.isWideAreaEndpoint(addr)) {
                        return url;
                    }
                } catch (Exception e) {
                    return null;
                }
            }
            String[] words = host.split("\\.");
            if (words.length < 2) {
                return null;
            }
            String lastWord = words[words.length - 1].toLowerCase();
            if (!DomainUtils.COUNTRY_SET.contains(lastWord) && !DomainUtils.GROUP_SET.contains(lastWord)) {
                return null;
            }
            return url;
        } catch (Exception e) {
            return null;
        }
    }

    /*
     * Split the url into protocol(0), hostname(1), path(2). @param url @return a string array (length=3), e.g.
     * http://xxx.com/yy/zz/kk protocol: http hostname: xxx.com path: yy/zz/kk
     */
    public static String[] splitUrl(String url) {
        String[] result = new String[3];

        int i1 = url.indexOf("//");
        if (i1 == -1) {
            result[0] = "http";
            i1 = 0;
        } else {
            if (i1 > 0) {
                result[0] = url.substring(0, i1 - 1);
            } else {
                result[0] = "http";
            }
            i1 = i1 + 2;
        }

        int i2 = url.indexOf('/', i1);
        if (i2 == -1) {
            result[1] = url.substring(i1);
            i2 = url.length();
            result[2] = "";
        } else {
            result[1] = url.substring(i1, i2);
            i2++;
            result[2] = url.substring(i2);
        }
        return result;
    }

    /**
     * 把protocol, hostname, path连在一起成为一个url
     */
    public static String combineUrl(String protocol, String hostname, String path) {
        String completeUrl;
        if (path.startsWith("/")) {
            completeUrl = protocol + "://" + hostname + path;
        } else {
            completeUrl = protocol + "://" + hostname + "/" + path;
        }

        return completeUrl;
    }

    static BitSet legalChars = new BitSet(1024);
    static {
        legalChars.set('?');
        legalChars.set('/');
        legalChars.set(';');
        legalChars.set(':');
        legalChars.set('@');
        legalChars.set('&');
        legalChars.set('=');
        legalChars.set('+');
        legalChars.set('$');
        legalChars.set(',');
        
        // alpha
        for (int i = 'a'; i <= 'z'; i++) {
            legalChars.set(i);
        }
        for (int i = 'A'; i <= 'Z'; i++) {
            legalChars.set(i);
        }
        
        // digit
        for (int i = '0'; i <= '9'; i++) {
            legalChars.set(i);
        }
        
        // mark
        legalChars.set('-');
        legalChars.set('_');
        legalChars.set('.');
        legalChars.set('!');
        legalChars.set('~');
        legalChars.set('*');
        legalChars.set('\'');
        legalChars.set('(');
        legalChars.set(')');

        legalChars.set('%');
    }

    private static final char[] HEX = new char[] { '0', '1', '2', '3', '4',
            '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    
    /**
     * 将url中的非ascii部分用UTF8表示.
     * @deprecated use {@link #encodeUrl(String, String)} instead.
     * @param url
     * @return encode成UTF8以后的字符串
     * @throws UnsupportedEncodingException
     */
    public static String encodeToUTF8(String url) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder(url.length() * 9);
        int strlen = url.length();

        char c;
        byte b;
        for (int i = 0; i < strlen; i++) {
            c = url.charAt(i);
            if (legalChars.get(c)) {
                builder.append(c);
            } else {
                if ((c >= 0x0001) && (c <= 0x007F)) {
                    b = (byte) c;
                    builder.append('%').append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
                } else if (c > 0x07FF) {
                    b = (byte) (0xE0 | ((c >> 12) & 0x0F));
                    builder.append('%').append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
                    b = (byte) (0x80 | ((c >> 6) & 0x3F));
                    builder.append('%').append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
                    b = (byte) (0x80 | ((c >> 0) & 0x3F));
                    builder.append('%').append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
                } else {
                    b = (byte) (0xC0 | ((c >> 6) & 0x1F));
                    builder.append('%').append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
                    b = (byte) (0x80 | ((c >> 0) & 0x3F));
                    builder.append('%').append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
                }
            }
        }
        return builder.toString();
    }

    private static final int ENCODE_BATCH_SIZE = 8;
    
    /**
     * Encode the url by gbk charset.
     * @param url
     * @return
     */
    public static String encodeUrl(String url, String charset) {
        StringBuilder builder = new StringBuilder(url.length() * 9);
        int strlen = url.length();
        
        CharsetEncoder encoder = Charset.forName(charset).newEncoder();
        
        CharBuffer charBuffer = CharBuffer.allocate(ENCODE_BATCH_SIZE);
        ByteBuffer byteBuffer = ByteBuffer.allocate(ENCODE_BATCH_SIZE * 4);

        int index = 0;
        while (index < strlen) {
            char ch = url.charAt(index++);
            if (legalChars.get(ch)) {
                builder.append(ch);
            } else {
                charBuffer.clear();
                charBuffer.put(ch);
                int bufLen = 1;
                while (index < strlen && bufLen < ENCODE_BATCH_SIZE) {
                    ch = url.charAt(index++);
                    if (legalChars.get(ch)) {
                        // put back the legal character
                        index --;
                        break;
                    } else {
                        charBuffer.put(ch);
                        bufLen ++;
                    }
                }
                byteBuffer.clear();
                charBuffer.flip();
                encoder.encode(charBuffer, byteBuffer, true);
                byteBuffer.flip();
                int byteNumber = byteBuffer.limit();
                for (int i=0; i<byteNumber; i++) {
                    byte b = byteBuffer.get();
                    builder.append('%').append(HEX[(b >> 4) & 0x0f])
                    .append(HEX[b & 0x0f]);
                }
            }
        }
        return builder.toString();
    }
    
    /**
     * 得到一个url的path部分 e.g., "http://www.sohu.com/index.jsp?sid=1", returns "/index.jsp".
     * 
     * @param url
     *            the url
     * @return the path part of the url
     */
    public static String getPathFromUrl(String url) {
        int i1 = url.indexOf("//");
        if (i1 == -1) {
            i1 = 0;
        } else {
            i1 = i1 + 2;
        }

        int i2 = url.indexOf('/', i1);
        if (i2 < 0) {
            return "";
        } else {
            int i3 = url.indexOf('?', i2);
            if (i3 < 0)
                return url.substring(i2);
            else
                return url.substring(i2, i3);
        }
    }

    /**
     * Return the whole query part of a url e.g., "http://www.sohu.com/index.jsp?sid=1", returns "/index.jsp?sid=1".
     * "http://www.sohu.com/index.jsp", returns "/index.jsp".
     * 
     * @param url
     *            the url
     * @return the query part of a url
     */
    public static String getQueryFromUrl(String url) {
        int pos = url.indexOf(':');
        if (pos >= 0) {
            pos = pos + 3;
        } else {
            pos = 0;
        }
        if (pos < url.length() - 1) {
            int pos1 = url.indexOf('/', pos);
            if (pos1 >= 0) {
                return url.substring(pos1);
            } else {
                return "/";
            }
        } else {
            return "/";
        }
    }

    /**
     * Returns the hostname from the url.
     * <p>
     * http://www.abc.com/news/1.html -> www.abc.com<br>
     * news.123.com.cn/who.com -> news.123.com.cn<br>
     * 
     * @param url
     *            the url
     * @return hostname in the url
     */
    public static String getHostFromUrl(String url) {
        // get host name part of URL
        int i1 = url.indexOf("//");

        int start = 0;
        if (i1 >= 0)
            start = i1 + 2;
        if (start >= url.length())
            return "";

        int i2 = url.indexOf('/', i1 + 2);
        int i3 = url.indexOf(':', i1 + 2);
        i2 = i2 == -1 ? url.length() : i2;
        i3 = i3 == -1 ? url.length() : i3;
        int end = i2 < i3 ? i2 : i3;

        return url.substring(start, end);
    }

    /**
     * Returns protocol and host in a url
     * 
     * @param url
     *            the url
     * @return the protocol and host
     */
    public static String getProtocolHostFromUrl(String url) {
        int i1 = url.indexOf("//");
        if (i1 == -1)
            return null;
        int i2 = url.indexOf('/', i1 + 2);
        int i3 = url.indexOf(':', i1 + 2);
        if (i2 == -1)
            i2 = url.length();
        if (i3 == -1)
            i3 = url.length();
        int end = i2 < i3 ? i2 : i3;
        return url.substring(0, end);
    }

    /**
     * Returns host and port in a url
     * 
     * @param url
     *            the url
     * @return the host and port
     */
    public static String getProtocolHostPortFromUrl(String url) {
        int i1 = url.indexOf("//");
        if (i1 == -1)
            return null;
        int i2 = url.indexOf('/', i1 + 2);
        if (i2 == -1)
            i2 = url.length();
        return url.substring(0, i2);
    }

    /**
     * returns protocol in the url
     */
    public static String getProtocol(String url) {
        int i = url.indexOf("://");
        if (i == -1)
            return null;
        else
            return url.substring(0, i);
    }

    /**
     * Returns the level of host in a url.
     * 
     * @see DomainUtils#getHostLevel(String)
     */
    public static int getHostLevel(String url) {
        String host = getHostFromUrl(url);
        return DomainUtils.getHostLevel(host);
    }

    /**
     * Returns the dir level of a url www.sohu.com/user -> 0 www.oblog.com/user1/ ->1 www.oblog.com/1/2/3/ ->3 author:
     * yemingjiang
     */
    public static int getPathLevel(String url) {
        String path = getPathFromUrl(url);
        int num = 0;
        for (int i = 1; i < path.length(); i++) {
            if (path.charAt(i) == '/')
                num++;
        }
        return num;
    }

    /**
     * whether two url are from the same host
     */
    public static boolean isSameHost(String url1, String url2) {
        String host1 = getHostFromUrl(url1);
        String host2 = getHostFromUrl(url2);
        if (host1 == null || host2 == null)
            // This is a wrong url
            return false;
        return host1.equals(host2);
    }

    /**
     * whether two url are from the same host using the same protocol
     */
    public static boolean isSameProtocolHost(String url1, String url2) {
        String host1 = getProtocolHostFromUrl(url1);
        String host2 = getProtocolHostFromUrl(url2);
        if (host1 == null || host2 == null)
            // This is a wrong url
            return false;
        return host1.equals(host2);
    }

    /**
     * Gets domain of a URL until to specific level. For example, if:<br>
     * http://news.abc.com/index.html or news.abc.com (not legal or normalized) <br>
     * The result is: abc.com
     * 
     * @param url
     *            the url
     * @param level
     *            of domain wanted
     * @return the domain of this url author: houjiliang(@rd.netease.com)
     */
    public static String getDomainFromUrl(String url, int level) {
        String host = getHostFromUrl(url);
        return host == null ? null : DomainUtils.getDomain(host, level);
    }

    /**
     * Gets domain of a URL. For example, if input: http://news.abc.com The result is: abc.com
     * 
     * @param url
     * @return the domain of this url author: 侯吉亮
     */
    public static String getDomainFromUrl(String url) {
        return getDomainFromUrl(url, 1);
    }

    /**
     * Gets domain name of a url. For example: http://abc.good.com.cn/index.html -> good news.163.com -> 163
     */
    public static String getDomainNameFromUrl(String url) {
        String domain = getDomainFromUrl(url, 1);
        if (IpUtils.isIPv4(domain)) {
            return domain;
        }
        int dot = domain.indexOf(".");
        if (dot > 0) {
            return domain.substring(0, dot);
        }
        return "";
    }

    private static Pattern HOSTNAME_PATTERN = Pattern.compile("^[^\\.]?[\\w\\-\\_\\.]+\\.[a-zA-Z]+$");

    public static boolean isLegalHostname(String url) {
        return HOSTNAME_PATTERN.matcher(url).matches();
    }

    /**
     * Whether a url looks like a chinese url
     */
    public static boolean isLikeChineseUrl(String link) {
        link = link.toLowerCase();
        try {
            URL url = new URL(link);
            String host = url.getHost();
            if (containKeyword(host, "cn"))
                return true;
            if (containKeyword(host, "chinese"))
                return true;
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * This method is used to check if a string A contains a keyword B. The differece between this method withc
     * String.indexOf() is: if a char-sequence S1 contains a char-sequence S2, S2 may not a single word. For example,
     * String S1 "aabbcna" contains String "cn", but we cannot say "cn" is a keyword in S1. But if S1 is like
     * "aa.cn-ccb", then, ok, we can say S1 has a word "cn". So this method is used to find keyword, not just
     * char-sequence matching.
     * 
     * @param str
     *            The string in which you want to find
     * @param keyword
     *            The string you want to find from the "str"
     * @return if it find return true. otherwise false.
     */
    public static boolean containKeyword(String str, String keyword) {
        int pos = str.indexOf(keyword);
        if (pos >= 0) {
            if (pos == 0) {
                if (pos + 2 >= str.length())
                    return true;
                char c1 = str.charAt(pos + 2);
                if ((c1 >= '0' && c1 <= '9') || (c1 >= 'a' && c1 <= 'z'))
                    return false;
                else
                    return true;
            } else {
                char c1 = str.charAt(pos - 1);
                if ((c1 >= '0' && c1 <= '9') || (c1 >= 'a' && c1 <= 'z'))
                    return false;
                if (pos + 2 >= str.length())
                    return true;
                char c2 = str.charAt(pos + 2);
                if ((c2 >= '0' && c2 <= '9') || (c2 >= 'a' && c2 <= 'z'))
                    return false;
                return true;
            }
        } else
            return false;
    }

    private static int getHostIndex(String url) {
        int i1 = url.indexOf("//");
        if (i1 == -1)
            return -1;
        int i2 = url.indexOf('/', i1 + 2);
        int i3 = url.indexOf(':', i1 + 2);
        if (i2 == -1)
            i2 = url.length();
        if (i3 == -1)
            i3 = url.length();
        return i2 < i3 ? i2 : i3;
    }

    private static int getHostPortIndex(String url) {
        int i1 = url.indexOf("//");
        if (i1 == -1)
            return -1;
        int i2 = url.indexOf('/', i1 + 2);
        if (i2 == -1)
            i2 = url.length();
        return i2;
    }

    private static int getPageIndex(String url, int host_index) {
        if (host_index == url.length())
            // the page is empty
            return host_index;
        int index = url.indexOf("?");
        if (index == -1)
            return url.length();
        else
            return index;
    }

    private static String arrageParameters(String url, int index) {
        if (index == url.length() || index == url.length() - 1)
            return "";
        int now = index + 1;
        ArrayList<String> pset = new ArrayList<String>();
        while (true) {
            int next = url.indexOf("&", now);
            if (next == -1)
                next = url.length();
            String ps = url.substring(now, next);
            int pindex = ps.indexOf("=");
            if (pindex == -1)
                pindex = ps.length();
            pset.add(ps.substring(0, pindex));
            if (next == url.length())
                break;
            else
                now = next + 1;
        }
        Collections.sort(pset);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < pset.size(); i++) {
            result.append(pset.get(i));
            if (i != pset.size() - 1)
                result.append("&");
        }
        return result.toString();
    }

    /**
     * Is this a dynamic url
     */
    public static boolean isDynamicURL(String url) {
        int host_index = getHostPortIndex(url);
        if (host_index == -1)
            return false;
        int page_index = getPageIndex(url, host_index);
        return page_index != url.length() && page_index != url.length() - 1;
    }

    /**
     * Get the host template part of a url. For example, give this url "http://www.a.com/a1234/ab23/a.html?b=1&a=2", it
     * returns "http://www.a.com"
     * 
     * @param url
     *            The url to be processed
     * @return The host template
     */
    public static String getHostTemplate(String url) {
        int host_index = getHostIndex(url);
        if (host_index == -1)
            return null;
        else
            return url.substring(0, host_index);
    }

    /**
     * Get the static template part of a url. For example, give this url "http://www.a.com/a1234/ab23/a.html?b=1&a=2",
     * it returns "http://www.a.com/a1234/ab23/a.html"
     * 
     * @param url
     *            The url to be processed
     * @return The static part of a url as a template
     */
    public static String getStaticTemplate(String url) {
        int host_index = getHostPortIndex(url);
        if (host_index == -1)
            return null;
        int page_index = getPageIndex(url, host_index);
        return url.substring(0, page_index);
    }

    /**
     * Get the dynamic template part of a url. For example, give this url "http://www.a.com/a1234/ab23/a.html?b=1&a=2",
     * it returns "http://www.a.com/a1234/ab23/a.html?a&b". Note that the parameters in the return value are sorted
     * 
     * @param url
     *            The url to be processed
     * @return The whole dynamic template by removing parameter values
     */
    public static String getDynamicTemplate(String url) {
        int host_index = getHostPortIndex(url);
        if (host_index == -1)
            return null;
        int page_index = getPageIndex(url, host_index);
        String parameters = arrageParameters(url, page_index);
        // sort parameter names
        if (parameters.length() != 0)
            return url.substring(0, page_index) + "?" + parameters;
        else
            return url.substring(0, page_index);
    }

    /**
     * Get the query template part of a url. For example, give this url "http://www.a.com/a1234/ab23/a.html?b=1&a=2", it
     * returns "/a1234/ab23/a.html?a&b". Note that the parameters in the return value are sorted.
     * 
     * @param url
     *            The url to be processed
     * @return The query template by removing parameter values
     */
    public static String getQueryTemplate(String url) {
        int host_index = getHostPortIndex(url);
        if (host_index == -1)
            return null;
        int page_index = getPageIndex(url, host_index);
        String parameters = arrageParameters(url, page_index);
        // sort parameter names
        if (parameters.length() != 0)
            return url.substring(host_index, page_index) + '?' + parameters;
        else
            return url.substring(host_index, page_index);
    }

    /**
     * 作用：1. 除去path中的数字; 2. remove the file name. 3. re-organize the parameters.
     * 
     * @param url
     * @return
     * @throws MalformedURLException
     */
    public static String calcTemplateForClean(String str) throws MalformedURLException {
        URL url = new URL(str);
        char[] chars = url.getPath().toCharArray();
        StringBuilder sb = new StringBuilder();

        int i = chars.length;
        while (--i >= 0) { // skip
            if (chars[i] == '/')
                break;
        }
        for (int j = 0; j <= i; j++) {
            if (chars[j] >= '0' && chars[j] <= '9')
                continue;
            sb.append(chars[j]);
        }
        String path = sb.toString();

        String query = url.getQuery();
        if (query == null) {
            return url.getProtocol() + "://" + url.getHost() + path;
        } else {
            String pairs[] = query.split("&");
            PriorityQueue<String> q = new PriorityQueue<String>();
            for (String pair: pairs) {
                String[] parts = pair.split("=");
                if (parts.length > 0) {
                    String name = parts[0];
                    q.add(name);
                }
            }
            String q1 = "";
            while (q.size() > 0) {
                if (q.size() > 1)
                    q1 += q.poll() + "&";
                else
                    q1 += q.poll();
            }

            return url.getProtocol() + "://" + url.getHost() + path + "?" + q1;
        }
    }

    /**
     * get the file type of a url such as: html, mp3, jpg, etc.
     * 
     * @param url
     *            url must be a legal and normalized URL.
     * @return the url type
     */
    public static String getUrlType(String url) {
        int lastSlash = url.lastIndexOf('/');
        int lastPeriod = url.lastIndexOf('.');
        int dslash = url.lastIndexOf("//");
        int queMark = url.lastIndexOf('?');
        if (lastSlash > lastPeriod || dslash + 1 == lastSlash)
            return "";
        else if (queMark != -1 && lastPeriod > queMark)
            return "";
        else {
            if (queMark == -1)
                return url.substring(lastPeriod + 1);
            else
                return url.substring(lastPeriod + 1, queMark);
        }
    }

    /**
     * if url = http://xxx.yyy.com/zzz/aaa.html then return : aaa.html please make sure the url IS a url
     */
    public static String getUrlFile(String url) {
        if (url == null || url.length() == 0) {
            return "";
        }
        int lindex = url.lastIndexOf('/');
        if (lindex == -1) {
            return "";
        }
        if (lindex != 0 && url.charAt(lindex - 1) != '/') {
            return url.substring(lindex + 1);
        }

        return "";
    }

    /**
     * return the string while trimming the tail of the url here tail means the urlFile, for example the tail of
     * http://xxx.com/a.htm is a.htm
     */
    public static String trimTail(String url) {
        if (url == null || url.length() == 0) {
            return "";
        }
        int lindex = url.lastIndexOf('/');
        if (lindex == -1) {
            return "";
        }
        if (lindex != 0 && url.charAt(lindex - 1) != '/') {
            return url.substring(0, lindex + 1);
        }

        return url;
    }

    private static final String[] HomePagePrefix = new String[] {
        "index", "default", "main"
    };

    private static final String[] HomePageSuffix = new String[] {
        "asp", "php", "jsp", "html", "htm", "shtml", "shtm", "aspx"
    };

    private static Set<String> HOMEPAGE_URLS = new HashSet<String>();
    static {
        HOMEPAGE_URLS.add("/");
        for (String prefix: HomePagePrefix) {
            for (String suffix: HomePageSuffix)
                HOMEPAGE_URLS.add("/" + prefix + "." + suffix);
        }

    }

    /**
     * whether this link is the home page of a host, such as: www.163.com
     */
    public static boolean isHomePageForHost(String link) {
        try {
            URL url = new URL(link);
            String path = url.getPath();
            if (path.length() == 0 || HOMEPAGE_URLS.contains(path))
                return true;
        } catch (MalformedURLException e) {
            return false;
        }
        return false;
    }

    /**
     * normalize the index url by remove the index suffix such as "index.htm" etc.
     */
    public static String indexUrlNormalize(String link) {
        try {
            URL url = new URL(link);
            String path = url.getPath();
            if (path.length() == 0)
                return link;
            else {
                for (String suffix: HOMEPAGE_URLS) {
                    if (path.endsWith(suffix)) {
                        int i = link.lastIndexOf('/');
                        if (i != -1) {
                            link = link.substring(0, i + 1);
                        }
                        return link;
                    }
                }
            }
        } catch (MalformedURLException e) {
            return null;
        }

        return link;
    }
    
    /** 
     * determine if a url is a index url such as Http://www.sina.com.cn/ or 
     * http://www.sina.com.cn/index.htm
     */
    public static boolean isIndexUrl(String link) {
        try {
            URL url = new URL(link);
            String path = url.getPath();
            if (path.length() == 0)
                return false;
            else {
                for (String suffix: HOMEPAGE_URLS) {
                    if (link.endsWith(suffix)) {
                        return true;
                    }
                }
            }
        } catch (MalformedURLException e) {
            return false;
        }
        
        return false;
    }
    
    public static String getCurUrl(HttpServletRequest req) {
    	String entryUrl = req.getScheme() + "://" + req.getHeader("host") + req.getRequestURI();
    	if (req.getQueryString() != null) {
    		entryUrl = entryUrl + "?" + req.getQueryString();
    	}
    	return entryUrl;
    }
}
