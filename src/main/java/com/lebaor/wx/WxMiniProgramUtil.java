package com.lebaor.wx;

import com.lebaor.limer.data.LimerConstants;
import com.lebaor.utils.JSONUtil;
import com.lebaor.utils.LogUtil;
import com.lebaor.webutils.HttpClientUtil;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.json.JSONObject;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.Arrays;

public class WxMiniProgramUtil {
	public static JSONObject code2Session(String code) {
		String json = HttpClientUtil.doGet(LimerConstants.CODE2SESSION_URL + "?appid=" + LimerConstants.MINIPROGRAM_APPID
				+ "&secret=" + LimerConstants.MINIPROGRAM_APPSECRET 
				+ "&js_code=" + code
				+ "&grant_type=authorization_code");
		
		try {
			JSONObject o = new JSONObject(json);
			if (!o.has("session_key")) {
				String errCode = JSONUtil.getString(o, "errcode");
				if (!errCode.equals("0")) {
					LogUtil.WEB_LOG.warn("code2session error: " + " code="+ code +", return json=" + json);
					return null;
				}
			}
			
//			String openId = o.getString("openid");
//			String sessionKey = o.getString("session_key");
//			String unionId = o.getString("unionid");
			return o;
		} catch (Exception e ) {
			LogUtil.WEB_LOG.warn("code2session error: " + " code="+ code +", return json=" + json, e);
			return null;
		}
	}
	
	public static JSONObject getUserInfo(String encryptedData, String sessionKey, String iv){
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);
 
        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding","BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return new JSONObject(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
