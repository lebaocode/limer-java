package com.lebaor.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


public class ChinaDistrictUtils {
	private static final String[] ZHIXIASHI = {"北京市","上海市","天津市","重庆市"};
	private static final String[] TEBIEXINGZHENGQU = {"香港", "澳门"};
	private static final String PROVINCE_STR = 
		"河北省\n" + 
		"山西省\n" + 
		"辽宁省\n" + 
		"吉林省\n" + 
		"黑龙江省\n" + 
		"江苏省\n" + 
		"浙江省\n" + 
		"安徽省\n" + 
		"福建省\n" + 
		"台湾省\n" + 
		"江西省\n" + 
		"山东省\n" + 
		"河南省\n" + 
		"湖北省\n" + 
		"湖南省\n" + 
		"广东省\n" + 
		"海南省\n" + 
		"四川省\n" + 
		"贵州省\n" + 
		"云南省\n" + 
		"陕西省\n" + 
		"甘肃省\n" + 
		"青海省\n" + 
		""; //23个
	
	private static final String MINZU_STR = "汉族	壮族	满族	回族	苗族	维吾尔族\n" + 
			"土家族	彝族	蒙古族	藏族	布依族	侗族\n" + 
			"瑶族	朝鲜族	白族	哈尼族	哈萨克族	黎族\n" + 
			"傣族	畲族	傈僳族	仡佬族	东乡族	高山族\n" + 
			"拉祜族	水族	佤族	纳西族	羌族	土族\n" + 
			"仫佬族	锡伯族	柯尔克孜族	达斡尔族	景颇族	毛南族\n" + 
			"撒拉族	塔吉克族	阿昌族	普米族	鄂温克族	怒族\n" + 
			"京族	基诺族	德昂族	保安族	俄罗斯族	裕固族\n" + 
			"乌兹别克族	门巴族	鄂伦春族	独龙族	塔塔尔族	赫哲族\n" + 
			"珞巴族	布朗族";
	
	private static HashSet<String> minzuSet = new HashSet<String>();
	private static HashSet<String> provinceSet = new HashSet<String>();
	//一个简称可能会对应多个地区，多数是 xx市有个xx县 导致的
	private static HashMap<String, LinkedList<DistrictInfo>> districtMap = new HashMap<String, LinkedList<DistrictInfo>>();

	static {
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isSameProvince(String province1, String province2) {
		if (province1 == null || province1.trim().length() == 0) return false;
		if (province2 == null || province2.trim().length() == 0) return false;
		
		String pAbbr1 = getAbbr(province1);
		String pAbbr2 = getAbbr(province2);
		return pAbbr1.equals(pAbbr2);
	}
	
	public static boolean isSameCity(String province1, String city1, String province2, String city2) {
		if (province1 == null || province1.trim().length() == 0) return false;
		if (province2 == null || province2.trim().length() == 0) return false;
		if (city1 == null || city1.trim().length() == 0) return false;
		if (city2 == null || city2.trim().length() == 0) return false;
		
		String pAbbr1 = getAbbr(province1);
		String pAbbr2 = getAbbr(province2);
		String cAbbr1 = getAbbr(city1);
		String cAbbr2 = getAbbr(city2);
		return pAbbr1.equals(pAbbr2) && cAbbr1.equals(cAbbr2);
	}
	
	//都不能为空
	public static boolean isSameDistrict(String province1, String city1, String district1,
			String province2, String city2, String district2) {
		if (TextUtil.isEmpty(district1) || TextUtil.isEmpty(district2)) {
			return false;
		}
		
		String dAbbr1 = getAbbr(district1);
		String dAbbr2 = getAbbr(district2);
		if (!dAbbr1.equals(dAbbr2)) return false;
		
		if (!isSameCity(province1, city1, province2, city2)) {
			return false;
		}
		
		return true;
	}
	
	public static DistrictInfo[] getDistrictInfo(String placeName) {
		LinkedList<DistrictInfo> list = districtMap.get(placeName);
		if (list == null) return new DistrictInfo[0];
		
		return list.toArray(new DistrictInfo[0]);
	}
	
	public static boolean isZhixiashi(String city) {
		if (city == null) return false;
		for (String s : ZHIXIASHI) {
			if (s.equals(city)
					|| s.equals(city + "市")){
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTebieXingzhengqu(String city) {
		if (city == null) return false;
		for (String s : TEBIEXINGZHENGQU) {
			if (s.equals(city)
					|| s.equals(city + "特别行政区")) {
				return true;
			}
		}
		return false;
	}
	

	//从address分析出是哪个区
	//如果分析不出来，返回null
	//返回数组，2个值，arr[0]是区，arr[1]是去掉省市区后的地址
	//如果可能为多个行政区划，则第一个值返回null
	public static String[] analyzeAddress(String address, String province, String city) {
		if (province == null || province.trim().length() == 0 || city == null || city.trim().length() == 0) {
			return new String[]{"", address};
		}
		
		String province2;
		String city2;
		if (!province.endsWith("省")) province2 = province + "省";
		else province2 = city;
		if (!city.endsWith("市")) city2 = city + "市";
		else city2 = city;
		
		String prefix;
		if (isZhixiashi(city2) || isTebieXingzhengqu(city2)) {
			prefix = city2;
		} else {
			prefix = province2 + city2;	
		}
		
		String s = address;
		if (s.startsWith(prefix)) {
			s = s.substring(prefix.length());
		}
		
		if (s.startsWith(city2)) {
			s = s.substring(city2.length());
		}
		
		int pos;
		pos = s.indexOf("区");
		if (pos != -1) {
			String temp = s.substring(0, pos);
			if (districtMap.containsKey(temp)) {
				LinkedList<DistrictInfo> infoList = districtMap.get(temp);
				for (DistrictInfo info : infoList) {
					if (info.getProvince().startsWith(province)
							&& info.getCity().startsWith(city)
							&& info.getDistrict().startsWith(temp)) {
						return new String[] {
							info.getDistrictAbbr(),
							s.substring(pos + 1)
						};
						
					}
				}
			}
		}
		
		pos = s.indexOf("市");
		if (pos != -1) {
			String temp = s.substring(0, pos);
			if (districtMap.containsKey(temp)) {
				LinkedList<DistrictInfo> infoList = districtMap.get(temp);
				for (DistrictInfo info : infoList) {
					if (info.getProvince().startsWith(province)
							&& info.getCity().startsWith(city)
							&& info.getDistrict().startsWith(temp)) {
						return new String[] {
							info.getDistrictAbbr(),
							s.substring(pos + 1)
						};
						
					}
				}
			}
		}
		
		return new String[]{
				"", s
		};
	}
		
	public static String getAbbr(String s) {
		if (s.endsWith("县") && s.length() == 2) return s;
		if (s.endsWith("区") && s.length() == 2) return s;
		if (s.equals("鄂温克族自治旗")) return s.substring(0, s.length() - 3);
		if (s.equals("神农架林区")) return s.substring(0, s.length() - 2);
		
		if (s.endsWith("回族区")) return s.substring(0, s.length() - 3);
		if (s.endsWith("达斡尔族自治旗")) return s.substring(0, s.length() - 7);
		if (s.endsWith("达斡尔族区")) return s.substring(0, s.length() - 5);
		if (s.endsWith("各族自治县")) return s.substring(0, s.length() - 5);
		
		if (s.endsWith("族自治县")) {
			for (String mz : minzuSet) {
				int i = s.indexOf(mz);
				if (i != -1) {
					s = s.substring(0, i);
					break;
				}
			}
			
			while (s.endsWith("族")) {
				boolean found = false;
				for (String mz : minzuSet) {
					int i = s.indexOf(mz);
					if (i != -1) {
						s = s.substring(0, i);
						found = true;
						break;
					}
				}
				if (!found) {
					System.out.println("[error_minzu_abbr] " + s);
					break;
				}
			}
		}
		
		if (s.endsWith("哈萨克自治县")) return s.substring(0, s.length() - 6);
		if (s.endsWith("自治县")) return s.substring(0, s.length() - 3);
		if (s.endsWith("地区") || s.endsWith("新区") || s.endsWith("矿区")) return s.substring(0, s.length() - 2);
		if (s.endsWith("省") || s.endsWith("市") || s.endsWith("县") || s.endsWith("区")) return s.substring(0, s.length() -1);
		
		return s;
	}
	
	private static void putIntoMap(String key, DistrictInfo val) {
		LinkedList<DistrictInfo> list = districtMap.get(key);
		if (list == null) {
			list = new LinkedList<DistrictInfo>();
		} else {
			for (DistrictInfo info : list) {
				if (info.toString().equals(val.toString())) {
					return;
				}
			}
			
//			System.out.println("[put_redund] " + key + " " + val + " " + list);
		}
		
		list.add(val);
		districtMap.put(key, list);
	}
	
	private static void init() throws Exception {
		//民族
		String[] minzu_arr = MINZU_STR.split("\\s+");
		for (String mz : minzu_arr) {
			minzuSet.add(mz.trim());
		}
		
		//省
		String[] prov_arr = PROVINCE_STR.split("\\s+");
		for (String p : prov_arr) {
			provinceSet.add(p.trim());
		}
		
		//直辖市 key
		for (String s : ZHIXIASHI) {
			DistrictInfo info = new DistrictInfo(s, s, "");
			putIntoMap(s, info);
		}
		
		//特别行政区 key
		for (String s : TEBIEXINGZHENGQU) {
			DistrictInfo info = new DistrictInfo(s, s, "");
			putIntoMap(s, info);
		}
		
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					ChinaDistrictUtils.class.getClassLoader().getResourceAsStream("district.properties"), "utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().length() == 0) continue;
				String[] arr = line.split("\\|");
				if (arr.length != 3) continue;
								
				String s1 = arr[0];
				String s2 = arr[1];
				String s3 = arr[2];
				
				if (s2.equals("市辖区") || s2.equals("县") || s2.endsWith("行政区划")) {
					//直辖市的区，以区为key
					DistrictInfo info = new DistrictInfo(s1, s1, s3);
					putIntoMap(s3, info);
					putIntoMap(getAbbr(s3), info);
					putIntoMap(s1 + s3, info);
					
//					System.out.println("[直辖市] " + info);
				} else if (s3.equals("其他") || s3.equals("市辖区")) {
					//普通省的市，以市名为key
					DistrictInfo info = new DistrictInfo(s1, s2, "");
					putIntoMap(s2, info);
					putIntoMap(getAbbr(s2), info);
					putIntoMap(s1 + s2, info);
					
//					System.out.println("[普通市] " + info);
				} else {
					//普通省市的区县，以区县名为key
					DistrictInfo info = new DistrictInfo(s1, s2, s3);
					putIntoMap(s3, info);
					putIntoMap(getAbbr(s3), info);
					putIntoMap(s1 + s2 + s3, info);
					putIntoMap(s2 + s3, info);
					
//					System.out.println("[普通区县] " + info);
				}
				
			}
			
		} finally {
			if (reader != null) reader.close();
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		Object[] arr;
		String address;
		String province;
		String city;
		
		province = "江苏";
		city = "苏州";
		
		address = "工业园区玲珑街1号玲珑花园77栋玲珑生活馆西楼2楼";
		arr = ChinaDistrictUtils.analyzeAddress(address, province, city);
		System.out.println(address + "\n0:"+arr[0] + "\n1:" + arr[1] + "\n");
		
		address = "江苏省苏州市昆山市樾河南路863号苏杭时代1层";
		arr = ChinaDistrictUtils.analyzeAddress(address, province, city);
		System.out.println(address + "\n0:"+arr[0] + "\n1:" + arr[1] + "\n");
		
		address = "江苏省苏州市昆山市";
		arr = ChinaDistrictUtils.analyzeAddress(address, province, city);
		System.out.println(address + "\n0:"+arr[0] + "\n1:" + arr[1] + "\n");
		
		address = "苏州市金阊区永福街18号苏州石路国际商城5层";
		arr = ChinaDistrictUtils.analyzeAddress(address, province, city);
		System.out.println(address + "\n0:"+arr[0] + "\n1:" + arr[1] + "\n");
		
		address = "苏州市姑苏区";
		arr = ChinaDistrictUtils.analyzeAddress(address, province, city);
		System.out.println(address + "\n0:"+arr[0] + "\n1:" + arr[1] + "\n");
		
		//北京
		province = "北京";
		city = "北京";
		
		address = "北京市昌平区天通苑东三区56号商铺楼10门3楼";
		arr = ChinaDistrictUtils.analyzeAddress(address, province, city);
		System.out.println(address + "\n0:"+arr[0] + "\n1:" + arr[1] + "\n");
		
		address = "北京市石景山区石景山路31号盛景国际C2层";
		arr = ChinaDistrictUtils.analyzeAddress(address, province, city);
		System.out.println(address + "\n0:"+arr[0] + "\n1:" + arr[1] + "\n");
		
		address = "北京市房山区";
		arr = ChinaDistrictUtils.analyzeAddress(address, province, city);
		System.out.println(address + "\n0:"+arr[0] + "\n1:" + arr[1] + "\n");
		
		System.out.println(getBaiduCityCode("山东", "枣庄"));
		
		System.out.println("end");
	}
	
	public static class DistrictInfo {
		String province;
		String city;
		String district;
		public DistrictInfo(String province, String city, String district) {
			this.setCity(city);
			this.setDistrict(district);
			this.setProvince(province);
		}
		
		public String toString() {
			return province+"|" + city+"|" + district;
		}
		
		public String getProvince() {
			return province;
		}
		public String getProvinceAbbr() {
			return getAbbr(province);
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCity() {
			return city;
		}
		public String getCityAbbr() {
			return getAbbr(city);
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getDistrict() {
			return district;
		}
		public String getDistrictAbbr() {
			return getAbbr(district);
		}
		public void setDistrict(String district) {
			this.district = district;
		}
		public boolean isZhixiashi() {
			for (String s : ZHIXIASHI) {
				if (city.equals(s)) {
					return true;
				}
			}
			return false;
		}
		public boolean isTebieXingzhengqu() {
			for (String s : TEBIEXINGZHENGQU) {
				if (city.equals(s)) {
					return true;
				}
			}
			return false;
		}
		
	}
	
	public static int getBaiduCityCode(String province, String city) {
		Integer code = baiduCityCodeMap.get(city);
		if (code != null) return code;
		
		code = baiduCityCodeMap.get(getAbbr(city));
		if (code != null) return code;
		
		code = baiduCityCodeMap.get(city + "市");
		if (code != null) return code;
		
		code = baiduCityCodeMap.get(province);
		if (code != null) return code;
		
		code = baiduCityCodeMap.get(province + "省");
		if (code != null) return code;
		
		code = baiduCityCodeMap.get(getAbbr(province));
		if (code != null) return code;
		
		return 1;
	}
	
	private static final String BAIDU_CITY_CODE = 
			"中国|1,安徽|23,福建|16,甘肃|6,广东|7,广西|17,贵州|24,海南|21,河北|25,黑龙江|2,河南|30,湖北|15,湖南|26,江苏|18,江西|31,吉林省|9,辽宁|19,内蒙古|22,宁夏|20,青海|11,山东|8,山西|10,陕西|27,四川|32,新疆|12,西藏|13,云南|28,浙江|29,北京|131,天津|332,石家庄|150,唐山|265,秦皇岛|148,邯郸|151,邢台|266,保定|307,张家口|264,承德|207,沧州|149,廊坊|191,衡水|208,太原|176,大同|355,阳泉|357,长治|356,晋城|290,朔州|237,晋中|238,运城|328,忻州|367,临汾|368,吕梁|327,呼和浩特|321,包头|229,乌海|123,赤峰|297,通辽|64,鄂尔多斯|283,呼伦贝尔|61,巴彦淖尔|169,乌兰察布|168,兴安盟|62,锡林郭勒盟|63,阿拉善盟|230,沈阳|58,大连|167,鞍山|320,抚顺|184,本溪|227,丹东|282,锦州|166,营口|281,阜新|59,辽阳|351,盘锦|228,铁岭|60,朝阳|280,葫芦岛|319,长春|53,吉林市|55,四平|56,辽源|183,通化|165,白山|57,松原|52,白城|51,延边朝鲜族自治州|54,哈尔滨|48,齐齐哈尔|41,鸡西|46,鹤岗|43,双鸭山|45,大庆|50,伊春|40,佳木斯|42,七台河|47,牡丹江|49,黑河|39,绥化|44,大兴安岭地区|38,上海|289,南京|315,无锡|317,徐州|316,常州|348,苏州|224,南通|161,连云港|347,淮安|162,盐城|223,扬州|346,镇江|160,泰州|276,宿迁|277,杭州|179,宁波|180,温州|178,嘉兴|334,湖州|294,绍兴|293,金华|333,衢州|243,舟山|245,台州|244,丽水|292,合肥|127,芜湖|129,蚌埠|126,淮南|250,马鞍山|358,淮北|253,铜陵|337,安庆|130,黄山|252,滁州|189,阜阳|128,宿州|370,巢湖|251,六安|298,亳州|188,池州|299,宣城|190,福州|300,厦门|194,莆田|195,三明|254,泉州|134,漳州|255,南平|133,龙岩|193,宁德|192,南昌|163,景德镇|225,萍乡|350,九江|349,新余|164,鹰潭|279,赣州|365,吉安|318,宜春|278,抚州|226,上饶|364,济南|288,青岛|236,淄博|354,枣庄|172,东营|174,烟台|326,潍坊|287,济宁|286,泰安|325,威海|175,日照|173,莱芜|124,临沂|234,德州|372,聊城|366,滨州|235,菏泽|353,郑州|268,开封|210,洛阳|153,平顶山|213,安阳|267,鹤壁|215,新乡|152,焦作|211,濮阳|209,许昌|155,漯河|344,三门峡|212,南阳|309,商丘|154,信阳|214,周口|308,驻马店|269,武汉|218,黄石|311,十堰|216,宜昌|270,襄阳|156,鄂州|122,荆门|217,孝感|310,荆州|157,黄冈|271,咸宁|362,随州|371,恩施土家族苗族自治州|373,仙桃|1713,潜江|1293,天门|2654,神农架林区|2734,长沙|158,株洲|222,湘潭|313,衡阳|159,邵阳|273,岳阳|220,常德|219,张家界|312,益阳|272,郴州|275,永州|314,怀化|363,娄底|221,湘西土家族苗族自治州|274,广州|257,韶关|137,深圳|340,珠海|140,汕头|303,佛山|138,江门|302,湛江|198,茂名|139,肇庆|338,惠州|301,梅州|141,汕尾|339,河源|200,阳江|199,清远|197,东莞|119,中山|187,潮州|201,揭阳|259,云浮|258,南宁|261,柳州|305,桂林|142,梧州|304,北海|295,防城港|204,钦州|145,贵港|341,玉林|361,百色|203,贺州|260,河池|143,来宾|202,崇左|144,海口|125,三亚|121,五指山|1644,琼海|2358,儋州|1215,文昌|2758,万宁|1216,东方|2634,定安|1214,屯昌|1641,澄迈|2757,临高|2033,白沙黎族自治|2359,昌江黎族自治|1642,乐东黎族自治|2032,陵水黎族自治|1643,保亭黎族苗族自治|1217,琼中黎族苗族自治|2031,重庆|132,成都|75,自贡|78,攀枝花|81,泸州|331,德阳|74,绵阳|240,广元|329,遂宁|330,内江|248,乐山|79,南充|291,眉山|77,宜宾|186,广安|241,达州|369,雅安|76,巴中|239,资阳|242,阿坝藏族羌族自治州|185,甘孜藏族自治州|73,凉山彝族自治州|80,贵阳|146,六盘水|147,遵义|262,安顺|263,铜仁地区|205,黔西南布依族苗族自治州|343,毕节地区|206,黔东南苗族侗族自治州|342,黔南布依族苗族自治州|306,昆明|104,曲靖|249,玉溪|106,保山|112,昭通|336,丽江|114,临沧|110,楚雄彝族自治州|105,红河哈尼族彝族自治州|107,文山壮族苗族自治州|177,普洱|108,西双版纳傣族自治州|109,大理白族自治州|111,德宏傣族景颇族自治州|116,怒江傈僳族自治州|113,迪庆藏族自治州|115,拉萨|100,昌都地区|99,山南地区|97,日喀则地区|102,那曲地区|101,阿里地区|103,林芝地区|98,西安|233,铜川|232,宝鸡|171,咸阳|323,渭南|170,延安|284,汉中|352,榆林|231,安康|324,商洛|285,兰州|36,嘉峪关|33,金昌|34,白银|35,天水|196,武威|118,张掖|117,平凉|359,酒泉|37,庆阳|135,定西|136,陇南|256,临夏回族自治州|182,甘南藏族自治州|247,西宁|66,海东地区|69,海北藏族自治州|67,黄南藏族自治州|70,海南藏族自治州|68,果洛藏族自治州|72,玉树藏族自治州|71,海西蒙古族藏族自治州|65,银川|360,石嘴山|335,吴忠|322,固原|246,中卫|181,乌鲁木齐|92,克拉玛依|95,吐鲁番地区|89,哈密地区|91,昌吉回族自治州|93,博尔塔拉蒙古自治州|88,巴音郭楞蒙古自治州|86,阿克苏地区|85,克孜勒苏柯尔克孜自治州|84,喀什地区|83,和田地区|82,伊犁哈萨克自治州|90,塔城地区|94,阿勒泰地区|96,石河子|770,阿拉尔|731,图木舒克|792,五家渠|789,香港特别行政区|2912,澳门特别行政区|2911";

	private static HashMap<String, Integer> baiduCityCodeMap = new HashMap<String, Integer>();
	static {
		String[] arr = BAIDU_CITY_CODE.split(",");
		for (String s : arr) {
			String[] ta = s.split("\\|", 2);
			baiduCityCodeMap.put(ta[0], Integer.parseInt(ta[1]));
		}
	}
}
