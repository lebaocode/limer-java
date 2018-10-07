package com.lebaor.thirdpartyutils;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;

import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LocationUtil {
	private static final double EARTH_RADIUS = 6378.137;//地球半径
//	private static final String BAIDU_MAP_AK = "Cv0FAvrvoKP4CvRCgWnAMDyG";
	private static final String BAIDU_MAP_AK = "dzyC1OCWDcvtmTAfZQtFhzv1";
	
	
	private static double rad(float d) {
		return d * Math.PI / 180.0;
	}
	
	/**
	 * 这是直线距离，不准。应该按照地图线路规划，求步行距离。
	 * 根据经纬度，计算两点间距离，返回单位：米
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return
	 */
	public static int straightLineDistanceOfTwoPoints(float lat1, float lng1,
			float lat2,float lng2) {
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
		+ Math.cos(radLat1) * Math.cos(radLat2)
		* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS * 1000;
		s = Math.round(s * 10000) / 10000;
		//double ss = s * 1.0936132983377; 码
		
		return (int)s;
	}
	
	public static String getStaticMapPicUrl(int width, int height, String lat, String lng, String label, int zoom) {
		try{
			return "http://api.map.baidu.com/staticimage/v2?" 
					+ "ak=" + BAIDU_MAP_AK
					+"&width="+ width +"&height="+ height 
					+"&center=" + URLEncoder.encode(lng+","+lat, "utf-8")
					+"&markers=" + URLEncoder.encode(label, "utf-8")
					+"&zoom=" + zoom
//					+"&scale=2"
					+"&dpiType=ph"
					+"&markerStyles=l,,0xff0000"
					;
		} catch (Exception e){
			
		}
		return "";
	}
	
	public static BaiduLatLng axisGcjToBaidu(String lat, String lng) throws Exception {
		String url = "http://api.map.baidu.com/geoconv/v1/?"
				+ "coords=" + URLEncoder.encode(lng+","+lat, "utf-8")
				+ "&ak=" + BAIDU_MAP_AK
				+"&from=3"
				+"&to=5"
				+"&output=json"
				;
		String json = HttpClientUtil.doGet(url);
		JSONObject o = JSONObject.fromObject(json);
		
		int status = o.getInt("status");
		if (status == 0) {
			JSONObject axis = o.getJSONArray("result").getJSONObject(0);
			String baiduLng = axis.getString("x");
			String baiduLat = axis.getString("y");
			return new BaiduLatLng(baiduLat, baiduLng);
		}
		return null;
	}
	
	public static BaiduLatLng axisWgsToBaidu(String lat, String lng) throws Exception {
		String url = "http://api.map.baidu.com/geoconv/v1/?"
				+ "coords=" + URLEncoder.encode(lng+","+lat, "utf-8")
				+ "&ak=" + BAIDU_MAP_AK
				+"&from=1"
				+"&to=5"
				+"&output=json"
				;
		String json = HttpClientUtil.doGet(url);
		JSONObject o = JSONObject.fromObject(json);
		
		int status = o.getInt("status");
		if (status == 0) {
			JSONObject axis = o.getJSONArray("result").getJSONObject(0);
			String baiduLng = axis.getString("x");
			String baiduLat = axis.getString("y");
			return new BaiduLatLng(baiduLat, baiduLng);
		}
		return null;
	}
	
	//radius单位米
	public static BaiduPoi[] getPoisByCenter(String keyword, LatLng latLng, int radius, boolean readAllResults) throws Exception {
		String url = "http://api.map.baidu.com/place/v2/search?query="
				+ URLEncoder.encode(keyword, "utf-8")
				+ "&location=" + URLEncoder.encode(latLng.getLat()+","+latLng.getLng(), "utf-8")
				+ "&radius=" + radius
				+ "&scope=2&output=json&ak=" + BAIDU_MAP_AK;
		return getPoisByUrl(url, 20, readAllResults);
	}
	
	public static BaiduPoi[] getPoisByKeyword(String keyword, String region, boolean readAllResults) throws Exception {
		String url = "http://api.map.baidu.com/place/v2/search?query="
				+ URLEncoder.encode(keyword, "utf-8")
				+ "&region=" + URLEncoder.encode(region, "utf-8")
				+ "&scope=2&output=json&ak=" + BAIDU_MAP_AK;
		
		return getPoisByUrl(url, 20, readAllResults);
		
	}
	
	//region可以为城市名称
	public static BaiduPoi[] getPoiGroupons(String keyword, String region, BaiduLatLng latLng) throws Exception {
		String url = "http://api.map.baidu.com/place/v2/eventsearch?query=" 
				+ URLEncoder.encode(keyword, "utf-8")
				+ "&region=" + URLEncoder.encode(region, "utf-8")
				+ "&event=groupon" 
				+ "&location=" + URLEncoder.encode(latLng.getLat()+","+latLng.getLng(), "utf-8")
				+ "&radius=2000&output=json&page_size=20&ak=" + BAIDU_MAP_AK;
		
		LinkedList<BaiduPoi> poiList = new LinkedList<BaiduPoi>();
		int curPageNo = 0;
		String nextUrl = url + "&page_num=" + curPageNo;
		
		while (nextUrl != null) {
			String json = HttpClientUtil.doGet(nextUrl);
			JSONObject o = JSONObject.fromObject(json);
			
			int status = o.getInt("status");
			String msg = o.getString("message");
			if (status != 0) {
				LogUtil.WEB_LOG.debug("ERROR_" + status + "_" + msg + "_" + url);
				break;
			}
			
			JSONArray jsonArr = o.getJSONArray("results");
			for (int i = 0; i < jsonArr.size(); i++) {
				try {
					JSONObject obj = jsonArr.getJSONObject(i);
					String name = obj.getString("name");
					JSONObject loc = obj.getJSONObject("location");
					String lat = loc.getString("lat");
					String lng = loc.getString("lng");
					String address = obj.getString("address");
					String uid = obj.getString("uid");
					String tel = obj.containsKey("telephone") ? obj.getString("telephone") : ""; //(010)59105299,(010)59105957
					
					if (!obj.containsKey("events")) {
						continue;
					}
					
					BaiduPoi poi = new BaiduPoi();
					poi.setAddress(address);
					poi.setAxis(new BaiduLatLng(lat, lng));
					poi.setName(name);
					poi.setUid(uid);
					if (tel != null && tel.trim().length() > 0) {
						poi.setTel(tel);
					}
					
					JSONArray eventsArr = obj.getJSONArray("events");
					for (int j = 0; j < eventsArr.size(); j++) {
						JSONObject eventObj = eventsArr.getJSONObject(j);
						BaiduPoiGroupon gn = new BaiduPoiGroupon();
						gn.setBrandTag(eventObj.getString("groupon_brandtag"));
						gn.setEndDate(eventObj.getString("groupon_end"));
						gn.setGrouponId(eventObj.getString("groupon_id"));
						gn.setGrouponImageUrl(eventObj.getString("groupon_image"));
						gn.setGrouponName(eventObj.getString("cn_name"));
						gn.setGrouponNum(eventObj.getString("groupon_num"));
						gn.setGrouponPrice(eventObj.getString("groupon_price"));
						gn.setGrouponRebate(eventObj.getString("groupon_rebate"));
						gn.setGrouponShortTitle(eventObj.getString("groupon_short_title"));
						gn.setGrouponSite(eventObj.getString("groupon_site"));
						gn.setGrouponSrcUrl(eventObj.getString("groupon_url_mobile"));
						gn.setGrouponTitle(eventObj.getString("groupon_title"));
						gn.setGroupType(eventObj.getString("groupon_type"));
						gn.setRegularPrice(eventObj.getString("regular_price"));
						gn.setStartDate(eventObj.getString("groupon_start"));
						poi.addGroupon(gn);
					}
					
					poiList.add(poi);
				} catch(Exception e) {
					LogUtil.WEB_LOG.debug("ERROR_" + status + "_" + msg + "_" + url, e);
				}
			}
			
			if (jsonArr.size() == 0) {
				nextUrl = null;
			} else {
				curPageNo++;
				nextUrl = url + "&page_num=" + curPageNo;
			}
			
		} 
		
		return poiList.toArray(new BaiduPoi[0]);	
	}
	
	/**
	 * 根据关键词，返回可能对应的poi
	 * @param keyword
	 * @param region
	 * @return
	 * @throws Exception
	 */
	private static BaiduPoi[] getPoisByUrl(String url, int pageSize, boolean readAllResults) throws Exception {
		if (pageSize < 10) pageSize = 10;
		if (pageSize > 20) pageSize = 20;
		
		LinkedList<BaiduPoi> poiList = new LinkedList<BaiduPoi>();
//		int totalResultNum = 0;
//		int realTotalResultNum = 0;
		int curPageNo = 0;
		String nextUrl = url + "&page_size=" + pageSize + "&page_num=" + curPageNo;
		
		while (nextUrl != null) {
			String json = HttpClientUtil.doGet(nextUrl);
			JSONObject o = JSONObject.fromObject(json);
			
			int status = o.getInt("status");
			String msg = o.getString("message");
//			totalResultNum = o.getInt("total");
			if (status != 0) {
				LogUtil.WEB_LOG.debug("ERROR_" + status + "_" + msg + "_" + url);
				break;
			}
			
			JSONArray jsonArr = o.getJSONArray("results");
			for (int i = 0; i < jsonArr.size(); i++) {
				try {
					JSONObject obj = jsonArr.getJSONObject(i);
					String name = obj.getString("name");
					JSONObject loc = obj.getJSONObject("location");
					String lat = loc.getString("lat");
					String lng = loc.getString("lng");
					String address = obj.getString("address");
					String uid = obj.getString("uid");
					String tel = obj.containsKey("telephone") ? obj.getString("telephone") : ""; //(010)59105299,(010)59105957
					
					JSONObject detail = obj.containsKey("detail_info") ? obj.getJSONObject("detail_info") : null;
					String tag = (detail != null && detail.containsKey("tag")) 
							? detail.getString("tag") : "";//房地产;住宅区  购物;商铺 生活服务;照相馆  旅游景点;游乐园  教育培训;亲子教育  运动健身;健身中心  教育培训;幼儿园
					String poiType = (detail != null && detail.containsKey("type")) 
							? detail.getString("type") : "";//house  shopping life  education
					
					BaiduPoi poi = new BaiduPoi();
					poi.setAddress(address);
					poi.setAxis(new BaiduLatLng(lat, lng));
					poi.setName(name);
					poi.setUid(uid);
					if (tag != null && tag.trim().length() > 0) {
						poi.setTag(tag);
					}
					if (tel != null && tel.trim().length() > 0) {
						poi.setTel(tel);
					}
					if (poiType != null && poiType.trim().length() > 0) {
						poi.setType(poiType);
					}
					poiList.add(poi);
				} catch(Exception e) {
					LogUtil.WEB_LOG.debug("ERROR_" + status + "_" + msg + "_" + url, e);
				}
			}
			
			
			if (readAllResults) {
				if (jsonArr.size() == 0) {
					nextUrl = null;
				} else {
					curPageNo++;
					nextUrl = url + "&page_size=" + pageSize + "&page_num=" + curPageNo;
				}
			} else {
				nextUrl = null;
			}
		} 
		
		return poiList.toArray(new BaiduPoi[0]);	
	}
	
	public static String[] getRegionByLatLng(String lat, String lng) throws Exception {
		BaiduPoi[] pois = getPoisByLatLng(lat, lng);
		
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (BaiduPoi p : pois) {
			String key = p.getProvince() + "," + p.getCity() + "," + p.getDistrict();
			Integer n = map.get(key);
			if (n == null) n = 0;
			n++;
			map.put(key, n);
		}
		
		int max = 0;
		String region = "";
		for (String key : map.keySet()) {
			int n = map.get(key);
			if (n > max) {
				max = n;
				region = key;
			}
		}
		return region.split(",");
	}
		
	/**
	 * 只返回100米内的小区，太少，由于地理偏移经常找不到小区
	 * 根据坐标，返回可能对应的poi
	 * @param lat
	 * @param lng
	 * @return
	 */
	public static BaiduPoi[] getPoisByLatLng(String lat, String lng) throws Exception {
		LinkedList<BaiduPoi> poiList = new LinkedList<BaiduPoi>();
		String url = "http://api.map.baidu.com/geocoder/v2/?"
				+ "output=json&ak="+ BAIDU_MAP_AK +"&callback=showLocation"
				+ "&coordtype=bd09ll"
//				+ "&coordtype=gcj02ll"
				+ "&location=" + URLEncoder.encode(lat + "," + lng, "utf-8")
				+ "&pois=1"
				;
		String json = HttpClientUtil.doGet(url);
		json = json.trim();
		if (!json.startsWith("{")) {
			int start = json.indexOf("{");
			int end = json.lastIndexOf("}");
			json = json.substring(start, end + 1);
		}
		
		JSONObject o = JSONObject.fromObject(json);
		
		int status = o.getInt("status");
		
		if (status == 0) {
			String province = null;
			String city = null;
			String district = null;
			JSONObject resObj = o.getJSONObject("result");
			
			JSONObject addrObj = resObj.getJSONObject("addressComponent");
			if (addrObj != null) {
				province = addrObj.getString("province");
				city = addrObj.getString("city");
				district = addrObj.getString("district");
			}
			
			JSONArray jsonArr = resObj.getJSONArray("pois");
			for (int i = 0; i < jsonArr.size(); i++) {
				try {
					JSONObject obj = jsonArr.getJSONObject(i);
					String name = obj.getString("name");
					JSONObject loc = obj.getJSONObject("point");
					String poiLng = loc.getString("x");
					String poiLat = loc.getString("y");
					String address = obj.getString("addr");
					String uid = obj.getString("uid");
					String type = obj.getString("poiType");
					
					BaiduPoi poi = new BaiduPoi();
					poi.setAddress(address);
					poi.setAxis(new BaiduLatLng(poiLat, poiLng));
					poi.setName(name);
					poi.setUid(uid);
					poi.setType(type);
					poi.setProvince(province);
					poi.setCity(city);
					poi.setDistrict(district);
					poiList.add(poi);
				} catch(Exception e) {
					LogUtil.WEB_LOG.debug("ERROR_" + status + "_" + lat + "," + lng, e);
				}
			}
		} else {
			LogUtil.WEB_LOG.debug("ERROR_" + status + "_" + lat + "," + lng);
		}
		return poiList.toArray(new BaiduPoi[0]);	
	}
	
	public static BaiduPoi getPoiDetail(String poiUid) throws Exception {
		
		String url = "http://api.map.baidu.com/place/v2/detail?"
				+ "output=json&ak="+ BAIDU_MAP_AK
				+ "&scope=2"
				+ "&uid=" + URLEncoder.encode(poiUid, "utf-8")
				;
		String json = HttpClientUtil.doGet(url);
		json = json.trim();
		
		JSONObject o = JSONObject.fromObject(json);
		
		int status = o.getInt("status");
		String msg = o.getString("message");
		
		if (status != 0) {
			LogUtil.WEB_LOG.debug("ERROR_" + status + "_" + msg + "_" + url);
			return null;
		}
		
		JSONObject obj = o.getJSONObject("result");
		
		String name = obj.getString("name");
		JSONObject loc = obj.getJSONObject("location");
		String lat = loc.getString("lat");
		String lng = loc.getString("lng");
		String address = obj.getString("address");
		String uid = obj.getString("uid");
		String tel = obj.containsKey("telephone") ? obj.getString("telephone") : ""; //(010)59105299,(010)59105957
		
		JSONObject detail = obj.containsKey("detail_info") ? obj.getJSONObject("detail_info") : null;
		String tag = (detail != null && detail.containsKey("tag")) 
				? detail.getString("tag") : "";//房地产;住宅区  购物;商铺 生活服务;照相馆  旅游景点;游乐园  教育培训;亲子教育  运动健身;健身中心  教育培训;幼儿园
		String poiType = (detail != null && detail.containsKey("type")) 
				? detail.getString("type") : "";//house  shopping life  education
		
		BaiduPoi poi = new BaiduPoi();
		poi.setAddress(address);
		poi.setAxis(new BaiduLatLng(lat, lng));
		poi.setName(name);
		poi.setUid(uid);
		if (tag != null && tag.trim().length() > 0) {
			poi.setTag(tag);
		}
		if (tel != null && tel.trim().length() > 0) {
			poi.setTel(tel);
		}
		if (poiType != null && poiType.trim().length() > 0) {
			poi.setType(poiType);
		}
		
		return poi;
	}
	
	/**
	 * 根据百度地图，求两点间的步行距离
	 */
	public static int distanceOfTwoPoints(BaiduLatLng poi1, BaiduLatLng poi2, String region) {
		if (poi1.getLat() == null || poi1.getLng() == null || poi2.getLat() == null || poi2.getLng() == null) return -1;
		if (poi1.getLat().trim().length() == 0 
				|| poi1.getLng().trim().length() == 0  
				|| poi2.getLat().trim().length() == 0 
				|| poi2.getLng().trim().length() == 0 ) return -1;
		
		try {
			
			String url = "http://api.map.baidu.com/direction/v1?mode=walking"
					+ "&origin=" 
					+ URLEncoder.encode(poi1.getLat() +"," + poi1.getLng(), "utf-8")
					+ "&destination="
					+ URLEncoder.encode(poi2.getLat() +"," + poi2.getLng(), "utf-8")
					+ "&region="
					+ URLEncoder.encode(region, "utf-8")
					+ "&origin_region="
					+ URLEncoder.encode(region, "utf-8")
					+ "&destination_region="
					+ URLEncoder.encode(region, "utf-8")
					+ "&output=json&ak=" + BAIDU_MAP_AK
					+ "&coord_type=bd09ll"
					;
			
			String json = HttpClientUtil.doGet(url);
			JSONObject o = JSONObject.fromObject(json);
			
			int status = o.getInt("status");
			String msg = o.getString("message");
			if (status == 0) {//成功
				JSONArray arr = o.getJSONObject("result").getJSONArray("routes");
				if (arr != null && arr.size() > 0) {
					String distance = arr.getJSONObject(0).getString("distance");
					return Integer.parseInt(distance);
				}
			} else {
				LogUtil.WEB_LOG.debug("distanceOfTwoPoints("+ poi1 +", " + poi2 +", "+ region +")"
						+ " status:" + status + " msg:"+msg);
				if (status == 401) {
					return -2;
				}
			}
			
		} catch (Exception e) {
			LogUtil.WEB_LOG.debug("distanceOfTwoPoints("+ poi1 +", " + poi2 +", "+ region +")", e);
		}
		return -1;
	}
	public static BaiduPoi[] getPlaceSuggestions(String keyword, String region, boolean cityLimit) {
		return getPlaceSuggestions(keyword, region, cityLimit, null);
	}
	public static BaiduPoi[] getPlaceSuggestions(String keyword, String region, boolean cityLimit, BaiduLatLng axis) {
		LinkedList<BaiduPoi> poiList = new LinkedList<BaiduPoi>();
		try {
			
			String url = "http://api.map.baidu.com/place/v2/suggestion"
					+ "?q=" 
					+ URLEncoder.encode(keyword, "utf-8")
					+ (axis != null ? ("&location="+ URLEncoder.encode(axis.getLat() +"," + axis.getLng(), "utf-8")) : "")
					+ "&region="
					+ URLEncoder.encode(region, "utf-8")
					+ "&output=json&city_limit="+ cityLimit +"&ak=" + BAIDU_MAP_AK
					;
			
			String json = HttpClientUtil.doGet(url);
//			LogUtil.WEB_LOG.debug("suggestion:"+json);
			JSONObject o = JSONObject.fromObject(json);
			
			int status = o.getInt("status");
			String msg = o.getString("message");
			if (status == 0) {//成功
				JSONArray arr = o.getJSONArray("result");
				if (arr != null && arr.size() > 0) {
					
					for (int i = 0; i < arr.size(); i++) {
						try {
							JSONObject obj = arr.getJSONObject(i);
							String name = obj.getString("name");
							JSONObject loc = obj.getJSONObject("location");
							String poiLng = loc.getString("lng");
							String poiLat = loc.getString("lat");
							String district = obj.getString("district");
							String uid = obj.getString("uid");
							String city = obj.getString("city");
							
							BaiduPoi poi = new BaiduPoi();
							poi.setAxis(new BaiduLatLng(poiLat, poiLng));
							poi.setName(name);
							poi.setUid(uid);
							poi.setCity(city);
							poi.setDistrict(district);
							poiList.add(poi);
						} catch(Exception e) {
							LogUtil.WEB_LOG.debug("ERROR_" + status + "_" + keyword + "," + region, e);
						}
					}
				}
			} else {
				LogUtil.WEB_LOG.debug("getPlaceSuggestions("+ keyword +", "+ region +")"
						+ " status:" + status + " msg:"+msg);
			}
			
		} catch (Exception e) {
			LogUtil.WEB_LOG.debug("getPlaceSuggestions("+ keyword + ", "+ region +")", e);
		}
		return poiList.toArray(new BaiduPoi[0]);
	}
	
	private static final double x_pi = 3.14159265358979324 * 3000.0 / 180.0; 
	public static GCJLatLng baiduAxis2Gcj(BaiduLatLng baiduAxis) {
		double x = baiduAxis.getLngDouble() - 0.0065;
		double y = baiduAxis.getLatDouble() - 0.006;  
	    double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);  
	    double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);  
	    double gcjLng = z * Math.cos(theta);  
	    double gcjLat = z * Math.sin(theta);
	    GCJLatLng gcj = new GCJLatLng(gcjLat, gcjLng);
	    return gcj;
	}
	
	public static final String AXIS_BAIDU = "bd09ll";
	public static final String AXIS_GCJ = "gcj02ll";
	public static final String AXIS_GPS = "wgs84ll";
	
	public static abstract class LatLng {
		String axisType;
		String lat;
		String lng;
		
		public String toString() {
			return axisType +":" + lat+ "," + lng;
		}
		public static LatLng createAxis(String s) {
			if (s.indexOf(":") == -1 || s.indexOf(",") == -1) {
				return null;
			}
			
			String axisType = null;
			if (s.startsWith(AXIS_BAIDU + ":")) {
				axisType = AXIS_BAIDU;
			} else if (s.startsWith(AXIS_GCJ + ":")) {
				axisType = AXIS_GCJ;
			} else if (s.startsWith(AXIS_GPS + ":")) {
				axisType = AXIS_GPS;
			} else {
				return null;
			}
			
			String[] arr = s.split(":", 2);
			if (arr.length != 2) return null;
			
			String[] arr2 = arr[1].split(",", 2);
			if (arr2.length != 2) return null;
			
			String lat = arr2[0].trim();
			String lng = arr2[1].trim();
			
			if (axisType.equals(AXIS_BAIDU)) {
				return new BaiduLatLng(lat, lng);
			} else if (axisType.equals(AXIS_GCJ)) {
				return new GCJLatLng(lat, lng);
			} 
			
			return null;
		}
		public LatLng(double lat, double lng) {
			this.lat = Double.toString(lat);
			this.lng = Double.toString(lng);
		}
		
		public LatLng(String lat, String lng) {
			this.lat = lat;
			this.lng = lng;
		}
		
		public String getAxisType() {
			return axisType;
		}
		
		public String getLat() {
			return lat;
		}
		public void setLat(String lat) {
			this.lat = lat;
		}
		public String getLng() {
			return lng;
		}
		public void setLng(String lng) {
			this.lng = lng;
		}
		public double getLatDouble() {
			return Double.parseDouble(lat);
		}
		public double getLngDouble() {
			return Double.parseDouble(lng);
		}
		public float getLatFloat() {
			return Float.parseFloat(lat);
		}
		public float getLngFloat() {
			return Float.parseFloat(lng);
		}
	}
	
	public static class GCJLatLng extends LatLng implements Serializable {
		private static final long serialVersionUID = 1L;
		public GCJLatLng(double lat, double lng) {
			super(lat, lng);
			this.axisType = AXIS_GCJ;
		}
		public GCJLatLng(String lat, String lng) {
			super(lat, lng);
			this.axisType = AXIS_GCJ;
		}
	}
	
	public static class BaiduLatLng extends LatLng implements Serializable {
		private static final long serialVersionUID = 1L;
		public BaiduLatLng(double lat, double lng) {
			super(lat, lng);
			this.axisType = AXIS_BAIDU;
		}
		public BaiduLatLng(String lat, String lng) {
			super(lat, lng);
			this.axisType = AXIS_BAIDU;
		}
	}
	
	public static class BaiduPoiArray implements Serializable {
		private static final long serialVersionUID = 1L;
		BaiduPoi[] arr;
		
		public String toString() {
			return java.util.Arrays.toString(arr);
		}

		public BaiduPoi[] getArr() {
			return arr;
		}

		public void setArr(BaiduPoi[] arr) {
			this.arr = arr;
		}
		
		
	}
	
	public static class BaiduPoiGrouponArray implements Serializable {
		private static final long serialVersionUID = 1L;
		BaiduPoiGroupon[] arr;
		
		public String toString() {
			return java.util.Arrays.toString(arr);
		}

		public BaiduPoiGroupon[] getArr() {
			return arr;
		}

		public void setArr(BaiduPoiGroupon[] arr) {
			this.arr = arr;
		}
		
		
	}
		
	public static class BaiduPoiGroupon implements Serializable {
		private static final long serialVersionUID = 1L;
		String grouponName; //婴儿游泳！环境怡人，服务贴心，交通四通八达！
		String grouponShortTitle;//同name
		String grouponTitle;//长title：仅售55元，价值256元婴儿游泳！设施齐全，专业技师，精心呵护，备加关爱，从小做起，强健身体，开阔眼界，环境干净整洁，服务热情，交通便利，节假日通用！
		
		String brandTag;//鑫博士婴儿游泳专业会所
		String startDate;//2014-11-08
		String endDate;//2015-03-31
		String grouponId;//2371777
		String grouponImageUrl;
		String grouponNum;//98
		String grouponPrice;//55
		String regularPrice;//256
		String grouponRebate;//折扣 2.1
		
		String grouponSite;//http://www.nuomi.com
		String groupType;//1: 餐饮 2：生活 3：娱乐 4：旅游住宿
		String grouponSrcUrl;//http://m.nuomi.com/deal/view?tinyurl=zirwuigv
		
		public String toString() {
			JSONObject o = new JSONObject();
			o.put("grouponName", grouponName);
			o.put("grouponShortTitle", grouponShortTitle);
			o.put("grouponTitle", grouponTitle);
			o.put("brandTag", brandTag);
			o.put("startDate", startDate);
			o.put("endDate", endDate);
			o.put("grouponId", grouponId);
			o.put("grouponImageUrl", grouponImageUrl);
			o.put("grouponNum", grouponNum);
			o.put("grouponPrice", grouponPrice);
			o.put("regularPrice", regularPrice);
			o.put("grouponRebate", grouponRebate);
			o.put("grouponSite", grouponSite);
			o.put("groupType", groupType);
			o.put("grouponSrcUrl", grouponSrcUrl);
			return o.toString();
		}
			
		
		public String getGrouponName() {
			return grouponName;
		}
		public void setGrouponName(String grouponName) {
			this.grouponName = grouponName;
		}
		public String getBrandTag() {
			return brandTag;
		}
		public void setBrandTag(String brandTag) {
			this.brandTag = brandTag;
		}
		public String getStartDate() {
			return startDate;
		}
		public void setStartDate(String startDate) {
			this.startDate = startDate;
		}
		public String getEndDate() {
			return endDate;
		}
		public void setEndDate(String endDate) {
			this.endDate = endDate;
		}
		public String getGrouponId() {
			return grouponId;
		}
		public void setGrouponId(String grouponId) {
			this.grouponId = grouponId;
		}
		public String getGrouponImageUrl() {
			return grouponImageUrl;
		}
		public void setGrouponImageUrl(String grouponImageUrl) {
			this.grouponImageUrl = grouponImageUrl;
		}
		public String getGrouponNum() {
			return grouponNum;
		}
		public void setGrouponNum(String grouponNum) {
			this.grouponNum = grouponNum;
		}
		public String getGrouponPrice() {
			return grouponPrice;
		}
		public void setGrouponPrice(String grouponPrice) {
			this.grouponPrice = grouponPrice;
		}
		public String getRegularPrice() {
			return regularPrice;
		}
		public void setRegularPrice(String regularPrice) {
			this.regularPrice = regularPrice;
		}
		public String getGrouponRebate() {
			return grouponRebate;
		}
		public void setGrouponRebate(String grouponRebate) {
			this.grouponRebate = grouponRebate;
		}
		public String getGrouponShortTitle() {
			return grouponShortTitle;
		}
		public void setGrouponShortTitle(String grouponShortTitle) {
			this.grouponShortTitle = grouponShortTitle;
		}
		public String getGrouponTitle() {
			return grouponTitle;
		}
		public void setGrouponTitle(String grouponTitle) {
			this.grouponTitle = grouponTitle;
		}
		public String getGrouponSite() {
			return grouponSite;
		}
		public void setGrouponSite(String grouponSite) {
			this.grouponSite = grouponSite;
		}
		public String getGroupType() {
			return groupType;
		}
		public void setGroupType(String groupType) {
			this.groupType = groupType;
		}
		public String getGrouponSrcUrl() {
			return grouponSrcUrl;
		}
		public void setGrouponSrcUrl(String grouponSrcUrl) {
			this.grouponSrcUrl = grouponSrcUrl;
		}
		
		
	}
	
	public static class BaiduPoi implements Serializable {
		private static final long serialVersionUID = 1L;
		String province;
		String city;
		String district;
		
		String name;
		String address;
		LatLng axis;
		String uid;
		String type;
		String tag;
		String tel;
		
		LinkedList<BaiduPoiGroupon> groupons = new LinkedList<BaiduPoiGroupon>();
		
		public String toString() {
			JSONObject o = new JSONObject();
			o.put("province", province);
			o.put("city", city);
			o.put("district", district);
			o.put("name", name);
			o.put("address", address);
			o.put("axis", axis);
			o.put("uid", uid);
			o.put("type", type);
			o.put("tag", tag);
			o.put("tel", tel);
			
			JSONArray arr = new JSONArray();
			for (BaiduPoiGroupon gn : groupons) {
				arr.add(JSONObject.fromObject(gn.toString()));
			}
			o.put("groupons", arr);
			return o.toString();
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getLat() {
			return axis.getLat();
		}
		public String getLng() {
			return axis.getLng();
		}
		public void setAxis(LatLng axis) {
			this.axis = axis;
		}
		public String getUid() {
			return uid;
		}
		public void setUid(String uid) {
			this.uid = uid;
		}
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		public String getDistrict() {
			return district;
		}
		public void setDistrict(String district) {
			this.district = district;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getTag() {
			return tag;
		}
		public void setTag(String tag) {
			this.tag = tag;
		}
		public String getTel() {
			return tel;
		}
		public void setTel(String tel) {
			this.tel = tel;
		}

		public LinkedList<BaiduPoiGroupon> getGroupons() {
			return groupons;
		}

		public void setGroupons(LinkedList<BaiduPoiGroupon> groupons) {
			this.groupons = groupons;
		}
		public void addGroupon(BaiduPoiGroupon gn) {
			this.groupons.add(gn);
		}
		
	}

	
	
	public static void main(String[] args) throws Exception {
//		baiduAxis2Gcj(new BaiduLatLng(40.077176, 116.339225));
//		BaiduPoi[] pois = LocationUtil.getPoisByLatLng("40.086252", "116.387605");
//		BaiduPoi[] pois = LocationUtil.getPoisByCenter(LebaoConstants.BAIDU_MAP_XIAOQU_KEYWORD, 
//				new BaiduLatLng("31.304551", "120.745126"), 800, true);
//		for (BaiduPoi p : pois) {
//			System.out.println(p.toString());	
//		}
//		BaiduPoi[] pois = LocationUtil.getPoiGroupons("婴儿", "北京市", new BaiduLatLng(40.078578,116.331719));
//		for (BaiduPoi p : pois) {
//			System.out.println(p.toString());	
//		}
		
		BaiduPoi[] pois = LocationUtil.getPoisByCenter("培训机构", 
				new BaiduLatLng("40.078616", "116.331718" ), 1000, false);
		for (BaiduPoi p : pois) {
			System.out.println(p.toString());	
		}
	}
	
}
