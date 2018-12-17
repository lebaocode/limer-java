package com.lebaor.wx;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import org.apache.commons.configuration.XMLConfiguration;

import com.lebaor.utils.LogFormatter;
import com.lebaor.utils.TextUtil;
import com.lebaor.webutils.HttpClientUtil;
import com.lebaor.wx.data.WxChargeNotifyData;

//微信支付（公众号内H5）
public class WxPayUtil {
	private static final Logger LOG = LogFormatter.getLogger(WxPayUtil.class);
	
	public static String unifiedOrderJsApi(String attach, String orderId, int priceFen, String userIp, String notifyUrl, 
			String userOpenId, String wxPayDesc, String detailDesc) throws Exception {
		try {
			XMLConfiguration xmlConfig = new XMLConfiguration();
			xmlConfig.setRootElementName("xml");
			xmlConfig.setDelimiterParsingDisabled(true);
			xmlConfig.setProperty("appid", WxConstants.WX_APPID);
			xmlConfig.setProperty("attach", attach);
			xmlConfig.setProperty("body", wxPayDesc);
			xmlConfig.setProperty("detail", detailDesc);
			xmlConfig.setProperty("device_info", "WEB");
			xmlConfig.setProperty("mch_id", WxConstants.WX_MCH_ID);
			xmlConfig.setProperty("nonce_str", WxConstants.WX_NONCESTR);
			xmlConfig.setProperty("notify_url", notifyUrl);
			xmlConfig.setProperty("openid", userOpenId);
			xmlConfig.setProperty("out_trade_no", orderId);
			xmlConfig.setProperty("sign_type", "MD5");
			xmlConfig.setProperty("spbill_create_ip", userIp);
			xmlConfig.setProperty("total_fee", priceFen);
			xmlConfig.setProperty("trade_type", "JSAPI");
			
			String param = "appid=" + WxConstants.WX_APPID
					+ "&attach=" + attach
					+ "&body=" + wxPayDesc
					+ "&detail=" + detailDesc
					+ "&device_info=WEB"
					+ "&mch_id=" + WxConstants.WX_MCH_ID
					+ "&nonce_str=" + WxConstants.WX_NONCESTR
					+ "&notify_url=" + notifyUrl
					+ "&openid=" + userOpenId
					+ "&out_trade_no=" + orderId
					+ "&sign_type=MD5"
					+ "&spbill_create_ip=" + userIp
					+ "&total_fee=" + priceFen
					+ "&trade_type=JSAPI"
			;
			param += "&key=" + WxConstants.WX_MCHSECRET;
			
			String sign = TextUtil.MD5(param).toUpperCase();
			xmlConfig.setProperty("sign", sign);
			
			StringWriter sw = new StringWriter();
			xmlConfig.save(sw);
			String postBody = sw.toString();
			debug(postBody);
			
			String resultXml = HttpClientUtil.doPost("https://api.mch.weixin.qq.com/pay/unifiedorder", postBody);
			debug("pay_wx_ret: "+resultXml);
			
			XMLConfiguration resultXmlConfig = new XMLConfiguration();
			resultXmlConfig.load(new StringReader(resultXml));
			
			String returnCode = resultXmlConfig.getString("return_code");
			String returnMsg = resultXmlConfig.getString("return_msg");
			
			if (returnCode == null || !returnCode.equalsIgnoreCase("SUCCESS")) {
				debug(returnMsg);
				return null;
			}
			
			String retAppId = resultXmlConfig.getString("appid");
			String retMchId = resultXmlConfig.getString("mch_id");
			String retNonce = resultXmlConfig.getString("nonce_str");
			String retSign = resultXmlConfig.getString("sign");
			String resultCode = resultXmlConfig.getString("result_code");
			String errCode = resultXmlConfig.getString("err_code");
			String errCodeDesc = resultXmlConfig.getString("err_code_des");
			if (resultCode == null || !resultCode.equalsIgnoreCase("SUCCESS")) {
				debug(errCode + "_" + errCodeDesc);
				return null;
			}
			
			if (!retAppId.equals(WxConstants.WX_APPID)
					|| !retMchId.equals(WxConstants.WX_MCH_ID)
					) {
				debug(retAppId + " " + retMchId + " " + retNonce + " " + retSign);
				return null;
			}
			
			String retTradeType = resultXmlConfig.getString("trade_type");
			if (!retTradeType.equalsIgnoreCase("JSAPI")) {
				debug(retTradeType);
				return null;
			}
			String prepayId = resultXmlConfig.getString("prepay_id");
			
			return prepayId;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	//返回transactionId
	public static WxChargeNotifyData chargeToUser(String openId, String orderId, int price, String desc) {
		try {
			XMLConfiguration xmlConfig = new XMLConfiguration();
			xmlConfig.setRootElementName("xml");
			xmlConfig.setDelimiterParsingDisabled(true);
			xmlConfig.setProperty("amount", price);
			xmlConfig.setProperty("check_name", "NO_CHECK");
			xmlConfig.setProperty("desc", desc);
			xmlConfig.setProperty("mch_appid", WxConstants.WX_APPID);
			xmlConfig.setProperty("mchid", WxConstants.WX_MCH_ID);
			xmlConfig.setProperty("nonce_str", WxConstants.WX_NONCESTR);
			xmlConfig.setProperty("openid", openId);
			xmlConfig.setProperty("partner_trade_no", orderId);
			xmlConfig.setProperty("spbill_create_ip", WxConstants.SERVER_IP);
			
			String param = "amount=" + price
					+ "&check_name=NO_CHECK"
					+ "&desc=" + desc
					+ "&mch_appid=" + WxConstants.WX_APPID
					+ "&mchid=" + WxConstants.WX_MCH_ID
					+ "&nonce_str=" + WxConstants.WX_NONCESTR
					+ "&openid=" + openId
					+ "&partner_trade_no=" + orderId
					+ "&spbill_create_ip=" + WxConstants.SERVER_IP
			;
			param += "&key=" + WxConstants.WX_MCHSECRET;
			
			String sign = TextUtil.MD5(param).toUpperCase();
			xmlConfig.setProperty("sign", sign);
			
			StringWriter sw = new StringWriter();
			xmlConfig.save(sw);
			String postBody = sw.toString();
			debug(postBody);
		
			String resultXml = WxPayHttpClientUtil.post("https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers", 
					postBody,
					WxConstants.WX_MCH_ID);
			debug("pay_touser_ret: "+resultXml);
			
			WxChargeNotifyData data = WxChargeNotifyData.loadXml(resultXml);
			return data;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static void debug(String s) {
		LOG.info(s);
	}
	
	public static void main(String[] args) {
		System.setProperty("catalina.base", ".");
		
		String s = "appid=wxd930ea5d5a258f4f&body=test&device_info=1000&mch_id=10000100&nonce_str=ibuaiVcKdpRxkhJA&key=192006250b4c09247ec02edce69f6a2d";
		System.out.println(TextUtil.MD5(s).toUpperCase().equals("9A0A8659F005D6984697E2CA0A9CF3B7"));
		
		WxPayUtil.chargeToUser("obsyjwR0ewe9qbea2_w3ShSlhdf8", "20170417193802012345", 100, "测试");
	}
}
