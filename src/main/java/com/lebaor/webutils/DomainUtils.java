package com.lebaor.webutils;

import java.util.HashSet;
import java.util.Set;

public class DomainUtils {


    /**
     * 提取一个host的顶级域名.
     * <p>
     * www.abc.com - com<br>
     * www.abc.com.sg - com.sg<br>
     * www.abc.com.cn - com.cn<br>
     * www.abc.bj.cn - bj.cn<br>
     * www.abc.cn - cn
     * 
     * @author houjiliang
     */
    public static String getDomainSuffix(String host) {
        if (host == null) {
            return "";
        }
        host = host.toLowerCase();
        int start = host.lastIndexOf(".");
        if (start >= host.length() - 1) {
            return "";
        }

        String suffix = host.substring(start + 1);
        if (isGroup(suffix)) {
            if (start <= 0) {
                return suffix;
            } // if
            int start2 = host.lastIndexOf(".", start - 1);
            String suffix2 = host.substring(start2 + 1, start);
            if (isCountry(suffix2)) {
                return suffix2 + "." + suffix;
            } else {
                return suffix;
            }
        } // if

        if (isCountry(suffix)) {
            if (start <= 0) {
                return suffix;
            }
            int start2 = host.lastIndexOf(".", start - 1);
            String suffix2 = host.substring(start2 + 1, start);
            if (suffix.equalsIgnoreCase("cn")) {
                if (isProvince(suffix2) || isGroup(suffix2)) {
                    return host.substring(start2 + 1);
                } else {
                    return "cn";
                }
            } else if (isGroup(suffix2)) {
                return host.substring(start2 + 1);
            } // else if
            return suffix;
        } // if

        return "";
    }
    /**
     * Separate the host into different parts by dots.
     * The first part, i.e. parts[0], is the suffix of the domain, and the last
     * part (if any), parts[maxParts - 1], could contain ".".
     * 
     *  e.g.:
     *  host              maxParts  parts                           return
     *  www.a.b.c.com.cn  6         "com.cn", "c", "b", "a", "www"  5
     *  www.a.b.c.com.cn  5         "com.cn", "c", "b", "a", "www"  5
     *  www.a.b.c.com.cn  4         "com.cn", "c", "b", "www.a"     4
     *  www.a.b.c.com.cn  3         "com.cn", "c", "www.a.b."       3
     *  www.a.b.c.com.cn  2         "com.cn", "www.a.b.c"           2
     *  www.a.b.c.com.cn  1         "www.a.b.c.com.cn"              1
     *  a.b.c             3         "c", "b", "a"                   3
     *  
     * @param host  the hostname to be separated. The characters in this string
     *              should be trimmed and lower-cased. host shoud not be null.
     * @param parts  the array for parts. This can be set to null to obtain only
     *               the number of parts
     * @param maxParts  the maximum number of parts. If <parts> is none-null,
     *                  its length should be at least <maxParts>, otherwise
     *                  an ArrayIndexOutOfBoundsException could be thrown for
     *                  some complicated host name. maxParts must be at least
     *                  ONE.
     * @return  the acctually number of parts for this host. This number will
     *          be less than <maxParts>
     */
    public static int separateHost(String host, String[] parts, int maxParts) {
        if (maxParts == 0)
            throw new RuntimeException("maxParts should be at least one: " +
            		"maxParts = " + maxParts);
        
        if (host.length() == 0)
            /*
             * If host is empty, it contains no parts
             */
            return 0;
        
        if (maxParts == 1) {
            /*
             * If maxParts equals 1, the whole host should be as the only part
             */
            if (parts != null)
                parts[0] = host;
            return 1;
        } // if
        
        int count = 0;
        // offs = the position of last "."
        int offs = host.length();
        /*
         * Fill suffix if any
         */
        String suffix = getDomainSuffix(host);
        if (suffix.length() > 0) {
            if (parts != null)
                parts[count] = suffix;
            offs -= suffix.length() + 1;
            count ++;
        } // if
        if (offs > 0) {
            /*
             * The middle parts
             */
            while (count < maxParts - 1) {
                int p = host.lastIndexOf('.', offs - 1);
                if (p == -1)
                    break;
                if (parts != null)
                    parts[count] = host.substring(p + 1, offs);
                count ++;
                offs = p;
            } // while
            /*
             * The last part
             */
            if (parts != null)
                parts[count] = host.substring(0, offs);
            count ++;
        } // if
        return count;
    }
    /**
     * Returns the parent of domain. <br>
     * For example, input:<br>
     * c.b.a.com<br>
     * then returns:<br>
     * b.a.com<br>
     * 
     * @param domain
     * @return parent domain
     */
    public static String getParentDomain(String domain) {
        int start = domain.indexOf(".");
        return domain.substring(start + 1);
    }

    /**
     * 取出hostname中的n级域名, 例如从"a.b.c.edu.cn"中取出1级域名是"c.edu.cn", 2级域名是 "b.c.edu.cn", etc.
     * 
     * @param host
     *            host name
     * @param level
     *            域名的级别
     * @return 第level级别的域名
     */
    public static String getDomain(String host, int level) {

        if (host == null || host.length() == 0)
            return "";
        // Fixed! 2004.4.7
        // quite the list of "." in the end of host
        int dotEnd = host.length() - 1;
        while (dotEnd >= 0 && host.charAt(dotEnd) == '.') {
            dotEnd--;
        }

        host = host.substring(0, dotEnd + 1);

        // if url is an IP, return ip
        if (IpUtils.isIPv4(host)) {
            if (level == 1)
                return host;
            else
                return "";
        }

        String suffix = getDomainSuffix(host);
        if (suffix.length() == 0)
            return "";

        int start = host.length() - suffix.length() - 1;

        int i = 0;
        while (i < level && start >= 0) {
            i++;
            start = host.lastIndexOf(".", start - 1);
        }

        if ((start == -1 && i != level))
            return "";

        return host.substring(start + 1, host.length());
    }

    /**
     * 取出hostname中的一级域名，例如从"www.tsinghua.edu.cn"中取出"tsinghua.edu.cn". 如果输入的是非法的域名，例如空串等，直接返回原始字符串.
     * 
     * @param host
     * @return domain of this host
     */
    public static String getDomain(String host) {
        return getDomain(host, 1);
    }

    /**
     * Returns the level of a host. E.g. : a.com -> 1 a.b.com -> 2 a.b.com.cn -> 2 123.45.67.12 -> 1
     * 
     * @param host
     * @return the level of a host. If the host is an IP, returns 1. author: houjiliang(@rd.netease.com) FIX: by
     *         yemingjiang , 2006.9.3
     */
    public static int getHostLevel(String host) {
        return getHostLevel(host, false);
    }

    /**
     * Returns the level of a host.
     * 
     * @param host
     * @param ignoreWWW
     *            ignor common prefix, that is, www
     * @return the level of a host. If the host is an IP, returns 1. author: houjiliang(@rd.netease.com) FIX: by
     *         yemingjiang , 2006.9.3
     */
    public static int getHostLevel(String host, boolean ignoreWWW) {
        if (host == null || host.length() == 0)
            return 0;
        if (IpUtils.isIPv4(host))
            return 1;

        String suffix = getDomainSuffix(host);
        if (suffix == "")
            return 0;

        String prefix = host.substring(0, host.length() - suffix.length());

        if (ignoreWWW) {
            if (host.startsWith("www."))
                prefix = host.substring(4, host.length());
        }
        return getDotNumInHost(prefix);
    }

    /**
     * Returns the dot num of a string I think prefix.split("\\.").length is slow author: yemingjiang , 2006.9.3
     */
    protected static int getDotNumInHost(String host) {
        int num = 0;
        for (int i = 0; i < host.length(); i++) {
            if (host.charAt(i) == '.')
                num++;
        }
        return num;
    }

    /**
     * 返回域名的核，通常是去掉.com,.com.cn等等这样后缀的内容
     * <p>
     * news.abc.com -> news.abc<br>
     * www.sohu.com -> sohu
     * 
     * @param host
     *            host name
     * @return 域名的核
     */
    public static String getDomainKernel(String host) {
        host = host.toLowerCase().trim();
        if (host.startsWith("www.")) {
            host = host.substring(4);
        } else if (host.startsWith("www1.") || host.startsWith("www2.") || host.startsWith("www3.")) {
            host = host.substring(5);
        }
        String suffix = getDomainSuffix(host);
        if (suffix == "") {
            return host;
        }
        int start = host.lastIndexOf(suffix);
        if (start <= 1) {
            return "";
        }
        return host.substring(0, start - 1);
    }

    /**
     * if host1 = xxx.h.com && host2 = h.com then host2 is the higher order of host2
     * 
     * @return "" if there is not order relation, otherwise one of the hosts
     */
    public static String getHigherOrderKernelHost(String host1, String host2) {
        String k1 = getDomainKernel(host1);
        String k2 = getDomainKernel(host2);

        if (k1.equals(k2)) {
            return "";
        }

        if (k1.endsWith(k2)) {
            return host2;
        } else if (k2.endsWith(k1)) {
            return host1;
        } else {
            return "";
        }
    }

    /**
     * Determines if this host is an IP.
     * 
     * @param host
     * @return whether this host is an ip address like host author: 侯吉亮
     * @deprecated 用UrlUtils.isIPv4() 或者UrlUtils.isIPv6().
     */
    public static boolean isIpHost(String host) {
        if (IpUtils.isIPv4(host) || IpUtils.isIPv6(host))
            return true;
        else
            return false;
    }

    /**
     * 根据主机名本身的格式返回这个是否为一个合法的格式的主机名
     * 
     * @param host
     *            host name
     * @return whether this is a valid format host name
     */
    public static boolean isValidHost(String host) {
        // see RFC 2396
        // host = hostname | IPv4address
        // hostname = *( domainlabel "." ) toplabel [ "." ]
        // domainlabel = alphanum | alphanum *( alphanum | "-" ) alphanum
        // toplabel = alpha | alpha *( alphanum | "-" ) alphanum
        if (host.startsWith(".")) { // remove this: host.startsWith("-")
            return false;
        }
        int pt1 = 0, pt2 = 0;
        while ((pt2 = host.indexOf('.', pt1)) != -1) {
            if (pt2 != host.length() - 1) {
                if (host.charAt(pt2 + 1) == '.') {
                    return false; // 2 periods cannot stand together
                }
            }
            String s = host.substring(pt1, pt2);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '-' || c >= '0' && c <= '9')) {
                    return false;
                }
            }
            pt1 = pt2 + 1;
            if (pt1 >= host.length()) {
                return true;
            }
        }
        pt2 = host.length();
        String s = host.substring(pt1, pt2);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '-' || c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    /**
     * // DomainList and Groups will merge, don't worry.
     */
    public static final String[] GROUPS = {
        "com",  // 商业机构，任何人都可以注册
        "edu",  // 教育机构
        "gov",  // 政府部门
        "int",  // 国际组织
        "mil",  // 美国军事部门
        "net",  // 网络组织，例如因特网服务商和维修商，现在任何人都可以注册。
        "org",  // 非盈利组织，任何人都可以注册
        "biz",  // 商业
        "info", // 网络信息服务组织
        "pro",  // 用于会计、律师和医生。
        "name", // 用于个人。
        "museum", // 用于博物馆。
        "coop", // 用于商业合作团体。
        "aero", // 用于航空工业。
        "xxx",  // 用于成人、色情网站。
        "idv",  // 用于个人。
        "cc",   // commercial company
        "ws",   // website
    };
    public static final String[] COUNTRIES = {
        "ac", "ad", "ae", "af", "ag", "ai", "al", "am", "an", "ao", "aq", "ar", 
        "as", "at", "au", "aw", "az", "ba", "bb", "bd", "be", "bf", "bg", "bh", 
        "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bv", "bw", "by", "bz", 
        "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", 
        "cr", "cs", "cu", "cv", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", 
        "dz", "ec", "ed", "ee", "eg", "eh", "er", "es", "et", "eu", "fi", "fj",
        "fk", "fm", "fo", "fr", "ga", "gb", "gd", "ge", "gf", "gg", "gh", "gi", 
        "gl", "gm", "gn", "go", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", 
        "hk", "hm", "hn", "hr", "ht", "hu", "id", "ie", "il", "im", "in", "io",
        "iq", "ir", "is", "it", "je", "jm", "jo", "jp", "ke", "kg", "kh", "ki", 
        "km", "kn", "kp", "kr", "kw", "ky", "kz", "la", "lb", "lc", "li", "lk", 
        "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "mg", "mh", "mi",
        "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", 
        "mw", "mx", "my", "mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", 
        "np", "nr", "nu", "nz", "om", "or", "pa", "pe", "pf", "pg", "ph", "pk",
        "pl", "pm", "pn", "pr", "ps", "pt", "pw", "py", "qa", "re", "ro", "ru", 
        "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl", 
        "sm", "sn", "so", "sr", "st", "su", "sv", "sy", "sz", "tc", "td", "tf",
        "tg", "th", "tj", "tk", "tm", "tn", "to", "tp", "tr", "tt", "tv", "tw", 
        "tz", "ua", "ug", "uk", "um", "us", "uy", "uz", "va", "vc", "ve", "vg", 
        "vi", "vn", "vu", "wf", "ws", "ye", "yt", "yu", "za", "zm", "zr", "zw"
    };
    public static final String[] PROVINCES = {
      // 北京   福建  上海   江西   天津   山东  重庆   河南   河北   湖北  山西   湖南
        "bj", "fj", "sh", "jx", "tj", "sd", "cq", "ha", "he", "hb", "sx", "hn",
      // 内蒙   广东  辽宁   广西   吉林  海南  黑龙江  四川   江苏   贵州   浙江   云南
        "nm", "gd", "ln", "gx", "jl", "hi", "hl", "sc", "js", "gz", "zj", "yn",
      // 安徽   西藏  陕西   台湾   甘肃  香港   青海   澳门   宁夏  新疆
        "ah", "xz", "sn", "tw", "gs", "hk", "qh", "mo", "nx", "xj"
    };
    public static final Set<String> GROUP_SET = new HashSet<String>();
    public static final Set<String> COUNTRY_SET = new HashSet<String>();
    public static final Set<String> PROVINCE_SET = new HashSet<String>();

    static {
        for (String p: DomainUtils.PROVINCES) {
            DomainUtils.PROVINCE_SET.add(p);
        } // for s
        for (String s: DomainUtils.GROUPS) {
            DomainUtils.GROUP_SET.add(s);
        } // for s
        for (String s: DomainUtils.COUNTRIES) {
            DomainUtils.COUNTRY_SET.add(s);
        } // for s
    }
    /**
     * Check whether the speicified part is the name of a group,
     * e.g. com, net, org
     * @param part  the part to be checked
     * @return  true if <part> is one of the group name, false otherwise
     */
    public static boolean isGroup(String part) {
        return GROUP_SET.contains(part);
    }
    /**
     * Check whether the speicified part is the name of a country,
     * e.g. cn, us
     * @param part  the part to be checked
     * @return  true if <part> is one of the country name, false otherwise
     */
    public static boolean isCountry(String part) {
        if (part.length() == 2)
            return COUNTRY_SET.contains(part);
        return false;
    }
    /**
     * Check whether the speicified part is the name of a provice,
     * e.g. gz,xm
     * @param part  the part to be checked
     * @return  true if <part> is one of the province name, false otherwise
     */
    public static boolean isProvince(String part) {
        if (part.length() == 2)
            return PROVINCE_SET.contains(part);
        return false;
    }
}
