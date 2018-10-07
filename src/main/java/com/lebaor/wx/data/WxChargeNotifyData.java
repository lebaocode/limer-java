package com.lebaor.wx.data;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import org.apache.commons.configuration.XMLConfiguration;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.LogFormatter;

public class WxChargeNotifyData {
private static final Logger LOG = LogFormatter.getLogger(WxPayNotifyData.class);
	
	String returnCode;
	String returnMsg;
	String resultCode;
	String errCode;
	String errCodeDesc;
	String transactionId;//微信支付订单号
	String orderId;//商户订单号out_trade_no
	String chargeEndTime;//支付完成时间 20141030133525
	
	public boolean isSuccess() {
		return resultCode!=null && resultCode.equalsIgnoreCase("SUCCESS")
				&& returnCode != null && returnCode.equalsIgnoreCase("SUCCESS")
				&& this.transactionId != null && this.transactionId.trim().length() > 0;
	}
	
	public static String genSuccessResultXml() {
		return genResultXml(true, "");
	}
	
	public static String genErrorResultXml(String msg) {
		return genResultXml(false, msg);
	}
	
	private static String genResultXml(boolean success, String msg) {
		String returnCode = "SUCCESS";
		String returnMsg = "";
		if (!success) {
			returnCode = "FAIL";
			returnMsg = msg;
		}
		
		try {
			XMLConfiguration xmlConfig = new XMLConfiguration();
			xmlConfig.setRootElementName("xml");
			xmlConfig.setDelimiterParsingDisabled(true);
			xmlConfig.setProperty("return_code",  returnCode);
			xmlConfig.setProperty("return_msg", returnMsg);
			
			StringWriter sw = new StringWriter();
			xmlConfig.save(sw);
			return sw.toString();
		} catch (Exception e) {
			LogUtil.WEB_LOG.warn("toXml error: ", e);
			return null;
		}
	}
	
	public static WxChargeNotifyData loadXml(String xml) throws Exception {
		LOG.info(xml);
		
		XMLConfiguration resultXmlConfig = new XMLConfiguration();
		resultXmlConfig.load(new StringReader( xml));
		
		WxChargeNotifyData data = new WxChargeNotifyData();
		
		String returnCode = resultXmlConfig.getString("return_code");
		String returnMsg = resultXmlConfig.getString("return_msg");
		
		data.setReturnCode(returnCode);
		data.setReturnMsg(returnMsg);
		
		if (returnCode == null || !returnCode.equalsIgnoreCase("SUCCESS")) {
			LOG.info(returnMsg);
			return data;
		}
		
		String resultCode = resultXmlConfig.getString("result_code");
		data.setResultCode(resultCode);
		if (resultCode == null || !resultCode.equalsIgnoreCase("SUCCESS")) {
			data.setErrCode(resultXmlConfig.getString("err_code"));
			data.setErrCodeDesc(resultXmlConfig.getString("err_code_des"));
			LOG.info(returnMsg);
			return data;
		}
		
		data.setOrderId(resultXmlConfig.getString("partner_trade_no"));
		data.setChargeEndTime(resultXmlConfig.getString("payment_time"));
		data.setTransactionId(resultXmlConfig.getString("payment_no"));
		
		return data;
	}

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getReturnMsg() {
		return returnMsg;
	}

	public void setReturnMsg(String returnMsg) {
		this.returnMsg = returnMsg;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrCodeDesc() {
		return errCodeDesc;
	}

	public void setErrCodeDesc(String errCodeDesc) {
		this.errCodeDesc = errCodeDesc;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getChargeEndTime() {
		return chargeEndTime;
	}

	public void setChargeEndTime(String chargeEndTime) {
		this.chargeEndTime = chargeEndTime;
	}
	
	
}
