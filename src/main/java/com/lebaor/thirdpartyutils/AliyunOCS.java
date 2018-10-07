package com.lebaor.thirdpartyutils;

import java.util.HashMap;
import java.util.Map;

import com.lebaor.utils.LogUtil;
import com.lebaor.thirdpartyutils.LocationUtil.BaiduPoiArray;
import com.lebaor.thirdpartyutils.LocationUtil.BaiduPoi;
import com.lebaor.utils.TextUtil;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.ConnectionFactoryBuilder.Protocol;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;

/**
 * 
 * 阿里云ocs缓存服务
 *
 */
public class AliyunOCS {
	public static final int EXPIRE_MAX = 20 * 24 * 60 * 60;//单位秒，30天
		

	static MemcachedClient cache = null;
	
	public void connect() {
		final String host = "92e8ae6169114d69.m.cnbjalicm12pub001.ocs.aliyuncs.com";//控制台上的“内网地址”
        final String port ="11211"; //默认端口 11211，不用改
        final String username = "92e8ae6169114d69";//控制台上的“访问账号”
        final String password = "d3tIrGk32";//邮件或短信中提供的“密码”
        
        AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"}, new PlainCallbackHandler(username, password));
        
        try {
        	cache = new MemcachedClient(
                           new ConnectionFactoryBuilder().setProtocol(Protocol.BINARY)
                .setAuthDescriptor(ad)
                .build(),
                AddrUtil.getAddresses(host + ":" + port));
        } catch (Exception e) {
        	LogUtil.WEB_LOG.warn("when connect()", e);
        }
	}
	
	public void release() {
		if (cache != null) {
            cache.shutdown();
    	}
	}
		
	public static OperationFuture<Boolean> delete(String key) {
		try {
			if (cache == null) return null;
			LogUtil.VAQ_LOG.debug("[aliyun_ocs] [del] ["+ key +"]");
			return cache.delete(key);
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("when delete("+key+")", e);
			return null;
		}
	}
	
	public static OperationFuture<Boolean> set(String key, int expire, Object val) {
		if (cache == null) return null;
		if (val == null) return null;
		String s = val.toString();
		s =  s.substring(0, Math.min(s.length(), 500));
		try{
			LogUtil.VAQ_LOG.debug("[aliyun_ocs] [set] ["+ key +"] ["+ expire +"] ["+ s +"]");
			return cache.set(key, expire, val);
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("when set("+key+", "+ expire +", "+ s +")", e);
			return null;
		}
	}
	
	public static OperationFuture<Boolean> touch(String key, int expire) {
		try {
			if (cache == null) return null;
			return cache.touch(key, expire);
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("when touch("+key+", "+ expire +")", e);
			return null;
		}
	}
	
	public static Object get(String key) {
		try {
			if (cache == null) return null;
			Object o = cache.get(key);
			
			String s = o == null ? null : o.toString();
			s = s == null ? null : s.substring(0, Math.min(s.length(), 500));
			
			LogUtil.VAQ_LOG.debug("[aliyun_ocs] [get] ["+ key +"] ["+ s +"]");
			return o;
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("when get("+key+")", e);
			return null;
		}
	}
	
	public static Map<String, Object> getBulk(String[] keys) {
//		HashMap<String, Object> map = new HashMap<String, Object>();
//		for (String key : keys) {
//			Object o = get(key);
//			map.put(key, o);
//		}
//		return map;
		
//		//aliyun暂不支持getBulk命令
		try {
			if (cache == null) return null;
			Map<String, Object> o = cache.getBulk(keys);
			
			String s = o == null ? null : o.toString();
			s = s == null ? null : s.substring(0, Math.min(s.length(), 1000));
			
			LogUtil.VAQ_LOG.debug("[aliyun_ocs] [getBulk] ["+ java.util.Arrays.toString(keys) +"] ["+ s +"]");
			return o;
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("when getBulk("+ java.util.Arrays.toString(keys)+")", e);
			return null;
		}
	}
	
	public static Long incr(String key, int amount, long defaultValue, int expire) {
		try {
			if (cache == null) return null;
			long newValue = cache.incr(key, amount, defaultValue, expire);
			LogUtil.VAQ_LOG.debug("[aliyun_ocs] [incr] ["+ key +"] ["+ amount +"] ["+ defaultValue +"] ["+ newValue +"]");
			return newValue;
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("when incr("+key+")", e);
			return null;
		}
	}
	
	public static Long decr(String key, int amount, long defaultValue, int expire) {
		try {
			if (cache == null) return null;
			long newValue = cache.decr(key, amount, defaultValue, expire);
			LogUtil.VAQ_LOG.debug("[aliyun_ocs] [decr] ["+ key +"] ["+ amount +"] ["+ defaultValue +"] ["+ newValue +"]");
			return newValue;
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("when incr("+key+")", e);
			return null;
		}
	}
	
	public static void main(String[] args)  {
		
       
		AliyunOCS ocs = new AliyunOCS();
        try {
        	 
        	 ocs.connect();
			 
			 //向OCS中存一个key为"ocs"的数据，便于后面验证读取数据
			 
			 OperationFuture<Boolean> future;
			 
//			 System.out.println("test get...");
//			 System.out.println(TextUtil.formatTime(System.currentTimeMillis()) + " set, expires 5 second");
//			 OperationFuture<Boolean> future = ocs.getCache().set("ocs", 4, xqu);
//			 future.get();  //  确保之前(mc.set())操作已经结束
//			 
//			 Thread.sleep(2000);
//			 System.out.println(TextUtil.formatTime(System.currentTimeMillis()) + " Get:"+ ocs.getCache().get("ocs"));
//			 
//			 Thread.sleep(3000);
//			 System.out.println(TextUtil.formatTime(System.currentTimeMillis()) + " Get:"+ ocs.getCache().get("ocs"));
			 
			 
//			 System.out.println("\ntest getAndTouch...");
//			 System.out.println(TextUtil.formatTime(System.currentTimeMillis()) + " set, expires 5 second");
//			 future = AliyunOCS.set("ocs_test", 5, xqu);
//			 future.get();  //  确保之前(mc.set())操作已经结束
//			 
//			 Thread.sleep(2000);
//			 
//			 future = AliyunOCS.touch("ocs_test", 5);
//			 future.get();
//			 System.out.println(TextUtil.formatTime(System.currentTimeMillis()) + " touch 5 second");
//			 
//			 Thread.sleep(4000);
//			 System.out.println(TextUtil.formatTime(System.currentTimeMillis()) + " Get:"+ AliyunOCS.get("ocs_test"));
			 
			 Thread.sleep(5000);
			 BaiduPoiArray val = new BaiduPoiArray();
			 BaiduPoi poi = new BaiduPoi();
			 poi.setProvince("北京");
			 val.setArr(new BaiduPoi[]{poi});
			 future = AliyunOCS.set("ocs_test_2", 20, val);
			 future = AliyunOCS.set("ocs_test_1", 20, val);
			 Thread.sleep(5000);
			 future.get();
			 Thread.sleep(3000);
			 System.out.println(TextUtil.formatTime(System.currentTimeMillis()) + " Get:"+ AliyunOCS.get("ocs_test_2"));
             
			 Map<String, Object> res = AliyunOCS.cache.getBulk(new String[]{"ocs_test_2", "ocs_test_1"});
			 System.out.println(res == null ? "null" : res.toString());
        } catch (Exception e) {
               e.printStackTrace();
        } finally {
        	ocs.release();
        }
        
        


	}
	
	
}
