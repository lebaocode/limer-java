package com.lebaor.wx.data;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Logger;

import org.apache.commons.configuration.XMLConfiguration;

import com.lebaor.utils.LogUtil;
import com.lebaor.utils.LogFormatter;

public class WxPayNotifyData {
	private static final Logger LOG = LogFormatter.getLogger(WxPayNotifyData.class);
	String rawXml;
	String returnCode;
	String returnMsg;
	String resultCode;
	String errCode;
	String errCodeDesc;
	String openId;
	boolean isSubscribe;//是否订阅公众号
	String bankType;//付款银行
	int totalFee;//订单金额，单位分
	String transactionId;//微信支付订单号
	String orderId;//商户订单号out_trade_no
	String attach;//附加信息
	String payEndTime;//支付完成时间 20141030133525
	
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
	
	public static WxPayNotifyData loadXml(String xml) throws Exception {
		LOG.info(xml);
		
		XMLConfiguration resultXmlConfig = new XMLConfiguration();
		resultXmlConfig.load(new StringReader( xml));
		
		String returnCode = resultXmlConfig.getString("return_code");
		String returnMsg = resultXmlConfig.getString("return_msg");
		
		if (returnCode == null || !returnCode.equalsIgnoreCase("SUCCESS")) {
			LOG.info(returnMsg);
			return null;
		}
		
		WxPayNotifyData data = new WxPayNotifyData();
		data.setRawXml(xml);
		data.setReturnCode(returnCode);
		data.setReturnMsg(returnMsg);
		data.setAttach(resultXmlConfig.getString("attach"));
		data.setBankType(resultXmlConfig.getString("bank_type"));
		data.setErrCode(resultXmlConfig.getString("err_code"));
		data.setErrCodeDesc(resultXmlConfig.getString("err_code_des"));
		
		data.setOpenId(resultXmlConfig.getString("openid"));
		data.setOrderId(resultXmlConfig.getString("out_trade_no"));
		data.setPayEndTime(resultXmlConfig.getString("time_end"));
		data.setResultCode(resultXmlConfig.getString("result_code"));
		
		String isSub = resultXmlConfig.getString("is_subscribe");
		data.setSubscribe(isSub != null && isSub.equalsIgnoreCase("Y"));
		
		data.setTotalFee(resultXmlConfig.getInt("total_fee"));
		data.setTransactionId(resultXmlConfig.getString("transaction_id"));
		
		//TODO 商户系统对于支付结果通知的内容一定要做签名验证,并校验返回的订单金额是否与商户侧的订单金额一致，防止数据泄漏导致出现“假通知”，造成资金损失。 
		
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

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public boolean isSubscribe() {
		return isSubscribe;
	}

	public void setSubscribe(boolean isSubscribe) {
		this.isSubscribe = isSubscribe;
	}

	public String getBankType() {
		return bankType;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public int getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
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

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getPayEndTime() {
		return payEndTime;
	}

	public void setPayEndTime(String payEndTime) {
		this.payEndTime = payEndTime;
	}

	public String getRawXml() {
		return rawXml;
	}

	public void setRawXml(String rawXml) {
		this.rawXml = rawXml;
	}
	
	
}
