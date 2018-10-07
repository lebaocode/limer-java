function configWeixinShare(title, content, imgUrl, targetUrl, locationFunc) {
	if (isIOS()) return;

	var curUrl = window.location.href;
	$.ajax({
		dataType:"json",
		url: "/wxsign",
		data: {url: curUrl},
		//headers: LEBAO_HEADERS,
		success: function(data){
    		if (data.status == "ok") {
    			var sign = data.sign;
    			var timestamp = data.timestamp;
    			var nonceStr = data.nonceStr;
    			var appId = data.appId;
    			configWeixinShareImpl(appId, timestamp, nonceStr, sign, title, content, imgUrl, targetUrl, locationFunc);
    		}	
    	}
	});
}

function configWeixinDefault(){
	if (isIOS()) return;
	
	var host = window.location.host;
	configWeixinShare("乐宝亲子辅导", "和孩子一起，从一个小目标开始", "http://" + host + "/images/qinzi5.jpg", "http://" + host + "/login");
}

function configWeixinDownload(){
	if (isIOS()) return;
	
	var host = window.location.host;
	configWeixinShare("乐宝亲子辅导", "和孩子一起，从一个小目标开始", "http://" + host + "/images/qinzi5.jpg", "http://" + host + "/download");
}

function configWeixinShareImpl(appId, timestamp, nonceStr, sign, title, content, imgUrl, targetUrl, locationFunc) {
	wx.config({
	    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	    appId: appId, // 必填，公众号的唯一标识
	    timestamp: timestamp, // 必填，生成签名的时间戳
	    nonceStr: nonceStr, // 必填，生成签名的随机串
	    signature: sign,// 必填，签名，见附录1
	    jsApiList: [
	    	"onMenuShareTimeline",
	    	"onMenuShareAppMessage",
	    	"onMenuShareQQ",
	    	"onMenuShareWeibo",
	    	"getLocation",
	    	"startRecord",
	    	"stopRecord",
	    	"onVoiceRecordEnd",
	    	"playVoice",
	    	"pauseVoice",
	    	"stopVoice",
	    	"onVoicePlayEnd",
	    	"uploadVoice",
	    	"downloadVoice",
	    	"chooseWXPay"
	    ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	});

	wx.ready(function() {
		// config信息验证后会执行ready方法，所有接口调用都必须在config接口获得结果之后，config是一个客户端的异步操作，所以如果需要在页面加载时就调用相关接口，则须把相关接口放在ready函数中调用来确保正确执行。对于用户触发时才调用的接口，则可以直接调用，不需要放在ready函数中。
	
		wx.onMenuShareTimeline({
		    title: content, // 分享标题
		    link: targetUrl, // 分享链接
		    imgUrl: imgUrl, // 分享图标
		    success: function () { 
		        // 用户确认分享后执行的回调函数
		    },
		    cancel: function () { 
		        // 用户取消分享后执行的回调函数
		    }
		});
		
		wx.onMenuShareAppMessage({
		    title: title, // 分享标题
		    desc: content, // 分享描述
		    link: targetUrl, // 分享链接
		    imgUrl: imgUrl, // 分享图标
		    type: 'link', // 分享类型,music、video或link，不填默认为link
		    dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
		    success: function () { 
		        // 用户确认分享后执行的回调函数
		    },
		    cancel: function () { 
		        // 用户取消分享后执行的回调函数
		    }
		});
		
		wx.onMenuShareQQ({
		    title: title, // 分享标题
		    desc: content, // 分享描述
		    link: targetUrl, // 分享链接
		    imgUrl: imgUrl, // 分享图标
		    success: function () { 
		       // 用户确认分享后执行的回调函数
		    },
		    cancel: function () { 
		       // 用户取消分享后执行的回调函数
		    }
		});
	
		wx.getLocation({
		    type: 'wgs84', // 默认为wgs84的gps坐标，如果要返回直接给openLocation用的火星坐标，可传入'gcj02'
		    success: function(res){
		    	locationFunc(res);
		    }
		});
	});
	
	wx.error(function(res){
	    // config信息验证失败会执行error函数，如签名过期导致验证失败，具体错误信息可以打开config的debug模式查看，也可以在返回的res参数中查看，对于SPA可以在这里更新签名。
		showWindowsToast("微信签名失败" + JSON.stringify(res));
	});
}