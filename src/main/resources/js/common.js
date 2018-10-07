var LOGO_IMG_URL = "http://www.limer.cn/images/logo_small.png";
var opts = {            
            lines: 8, // 花瓣数目
            length: 4, // 花瓣长度
            width: 3, // 花瓣宽度
            radius: 5, // 花瓣距中心半径
            corners: 1, // 花瓣圆滑度 (0-1)
            rotate: 0, // 花瓣旋转角度
            direction: 1, // 花瓣旋转方向 1: 顺时针, -1: 逆时针
            color: '#B9B9B9', // 花瓣颜色
            speed: 1, // 花瓣旋转速度
            trail: 60, // 花瓣旋转时的拖影(百分比)
            shadow: false, // 花瓣是否显示阴影
            hwaccel: false, //spinner 是否启用硬件加速及高速旋转            
            className: 'spinner', // spinner css 样式名称
            zIndex: 2e9, // spinner的z轴 (默认是2000000000)
            top: '50%', // spinner 相对父容器Top定位 单位 px
            left: '50%'// spinner 相对父容器Left定位 单位 px
        };
var spinner = new Spinner(opts);
function showLoadingIcon() {
	try {
		var target = $(".w-load .spin").get(0);
		spinner.spin(target);
	} catch(e) {}
}
function stopLoadingIcon() {
	spinner.spin();
}

function showAndroidToast(toast) {
	if (typeof(Android) == "undefined") {
		showWindowsToast(toast);
		return;
	}

	if (Android) {
    	Android.showToast(toast);
    } 
}

function showWindowsToast(toast) {
	if ($("#pop-short-toast").length > 0) {
		$("#pop-short-toast").html(toast);
		$("#pop-short-toast").offset({ left: ($(window).width() - $("#pop-short-toast").outerWidth())/2});
		$("#pop-short-toast").toggleClass("dn");
		setTimeout(function(){
			$("#pop-short-toast").toggleClass("dn");
		}, 3000);
		return;
	}
	
	$("body").append(
		"<div class='pop-short-toast pop-short-toast-text dn' id='pop-short-toast'>"
		+""+ toast +""
		+"</div>"
	);
	
	$("#pop-short-toast").offset({ left: ($(window).width() - $("#pop-short-toast").outerWidth())/2});
	$("#pop-short-toast").toggleClass("dn");
	setTimeout(function(){
		$("#pop-short-toast").toggleClass("dn");	
	}, 3000);
}


function toggleMaskDownload() {
	$("#pop-mask-layer-download").toggleClass("dn");
	$("#pop-main-download").toggleClass("dn");
}

function showDownloadToast(toast) {
	if ($("#pop-main-download").length > 0) {
		$("#pop-main-download .toast-text").html(toast);
		toggleMaskDownload();
		return;
	}

	var ua = window.navigator.userAgent.toLowerCase();
	if (ua.indexOf("micromessenger") != -1) {
		
		$("body").append(
			"<div class='pop-mask-layer dn' id='pop-mask-layer-download' onclick='toggleMaskDownload()'></div>"
			+ "<div class='pop-main dn' id='pop-main-download' onclick='window.location=\"/download\";'>"
				+ "<div class='toast-text'>" + toast + "</div>"
				+ "<div class='download-button'>下载乐宝APP</div>"
			+ "</div>"
			);
		logAction("DOWNLOAD_WX_FROM_THREAD");
	} else {
		$("body").append(
			"<div class='pop-mask-layer dn' id='pop-mask-layer-download' onclick='toggleMaskDownload()'></div>"
			+ "<div class='pop-main dn' id='pop-main-download' onclick='window.location=\"/res/lebao.apk\";'>"
				+ "<div class='toast-text'>" + toast + "</div>"
				+ "<div class='download-button'>下载乐宝APP</div>"
			+ "</div>"
			);
	
		logAction("DOWNLOAD_FROM_THREAD");
	}
	toggleMaskDownload();
}

//menuItems: [{name:'', url:''}, {}]
function showRightTopMenu(menuItems) {
	if ($("#pop-righttop-menu").length > 0) {
		toggleMenu("pop-righttop-menu");
		return;
	}
	
	var html = "<div class='pop-righttop-menu dn' id='pop-righttop-menu'>";
	
	$.each(menuItems, function(i, val){
		html += "<div class='pop-righttop-menu-item' url='"+ val.url +"'>"+ val.name +"</div>";
	});
	
	html += "</div>";
	
	genMaskLayerMenuHtml("pop-righttop-menu");
	
	$("body").append(html);
	toggleMenu("pop-righttop-menu");
	
	$(".pop-righttop-menu-item").each(function(){
		$(this).on("tap", function(){
			event.preventDefault();
			showDivHighlightMenu($(this));
		
			var url = $(this).attr("url");
			if (url) {
				setTimeout(function(){
					window.location = url;
				}, 300);
			}
			
			
		});
	});
}
function showLeftTopMenu(menuItems) {
	if ($("#pop-lefttop-menu").length > 0) {
		toggleMenu("pop-lefttop-menu");
		return;
	}
	
	var html = "<div class='pop-lefttop-menu dn' id='pop-lefttop-menu'>";
	
	$.each(menuItems, function(i, val){
		html += "<div class='pop-lefttop-menu-item' url='"+ val.url +"'>"+ val.name +"</div>";
	});
	
	html += "</div>";
	genMaskLayerMenuHtml("pop-lefttop-menu");
	
	$("body").append(html);
	toggleMenu("pop-lefttop-menu");
	
	$(".pop-lefttop-menu-item").each(function(){
		$(this).on("tap", function(){
			event.preventDefault();
			showDivHighlightMenu($(this));
		
			var url = $(this).attr("url");
			if (url) {
				setTimeout(function(){
					window.location = url;
				}, 300);
			}
			
			
		});
	});
}
function toggleMenu(menuId) {
	$("#" + menuId).toggleClass("dn");
	$("#mask-layer-" + menuId).toggleClass("dn");
}

function showMidOpMenu(menuItems) {
	if ($("#pop-midop-menu").length > 0) {
		toggleMenu("pop-midop-menu");
		return;
	}
	
	var html = "<div class='pop-mid-menu dn' id='pop-midop-menu'>";
	
	$.each(menuItems, function(i, val){
		html += "<div class='pop-mid-menu-item' url='"+ val.url +"'>"+ val.name +"</div>";
	});
	
	html += "</div>";
	genMaskLayerMenuHtml("pop-midop-menu");
	
	$("body").append(html);
	toggleMenu("pop-midop-menu");
	
	$(".pop-mid-menu-item").each(function(){
		$(this).on("tap", function(){
			event.preventDefault();
			showDivHighlightMenu($(this));
		
			var url = $(this).attr("url");
			if (url) {
				setTimeout(function(){
					window.location = url;
				}, 300);
			}
			
			
		});
	});
}

function hideDivHighlightMenu(){
	if ($("#highlight-div-mask").length > 0) {
		$("#highlight-div-mask").addClass("dn");
		return;
	}
}
function showDivHighlightMenu(ele) {
	if ($("#highlight-div-mask").length <= 0) {
		var html = "<div class='div-highlight-mask dn' id='highlight-div-mask'></div>";
		$("body").append(html);
	}

	$("#highlight-div-mask").removeClass("dn");
	var top = ele.offset().top;
	var left = ele.offset().left;
	var h = ele.height();
	$("#highlight-div-mask").height(ele.height());
	$("#highlight-div-mask").width(ele.width());
	$("#highlight-div-mask").offset({top: top, left: left});
	if (ele.css("border-radius") || ele.css("-webkit-border-radius") || ele.css("-moz-border-radius")) {
		$("#highlight-div-mask").css("border-radius", ele.css("border-radius"));
		$("#highlight-div-mask").css("-webkit-border-radius", ele.css("-webkit-border-radius"));
		$("#highlight-div-mask").css("-moz-border-radius", ele.css("-moz-border-radius"));
	}
	
	setTimeout(function(){
		hideDivHighlightMenu();
	}, 300);
}

function lastStep() {
    window.history.back();
}
function backToUrl(url) {
	if (url != "") {
		window.location = url;
	} else {
		window.history.back();
	}
}

function gotoTab(tabName) {
	if (tabName == "cal"){
		window.location = "/plan/list";
	} else if (tabName == "me"){
		window.location = "/ucenter/list";
	} else if (tabName == "fudao"){
		window.location = "/fudao/index";
	} 
}

function refreshTab(tabName) {
	gotoTab(tabName);
}

function changeSelectValue(menuId, eleId, eleVal, hiddenId, hiddenVal) {
	toggleMenu(menuId);
	$("#" + eleId).html(eleVal); //这个是div
	$("#" + eleId).css("color", "#333333");
	$("#" + hiddenId).val(hiddenVal); //这个是input
}
function showSelectMenu(menuId, eleId, hiddenId, menuItems) {
	if ($("#" + menuId).length > 0) {
		toggleMenu(menuId);
		return;
	}
	
	var html = "<div class='pop-mid-menu dn' id='"+ menuId +"'>";
	
	$.each(menuItems, function(i, val){
		html += "<div class='pop-mid-menu-item' onclick='changeSelectValue(\""+ menuId +"\", \""+ eleId +"\", \""+ val.eleVal +"\", \""+ hiddenId +"\", \""+ val.hiddenVal +"\");'>"+ val.eleVal +"</div>";
	});
	
	html += "</div>";
	genMaskLayerMenuHtml(menuId);
	
	$("body").append(html);
	toggleMenu(menuId);
}
function genMaskLayerMenuHtml(menuId){
	var maskId = "mask-layer-" + menuId;
	if ($("#" + maskId).length > 0) {
		$("#" + maskId).unbind("click");
		$("#" + maskId).bind("click", function(){
			toggleMenu(menuId);
			event.preventDefault();
		});
		return;
	}

	var html = "";
	html += "<div class='mask dn' id='"+ maskId +"'>";
	html += "</div>";
	
	$("body").append(html);
	$("#" + maskId).bind("click", function(){
		toggleMenu(menuId);
		event.preventDefault();
	});
}

function addAutoFocusEffect(ele, hint, normalColor, hintColor, normalFontSize, hintFontSize) {
	ele.on({
		"focus": function(){
			if (ele.val() == hint) {
				ele.val("");
			}
			ele.css("color", normalColor);
			if (normalFontSize) {
				ele.css("font-size", normalFontSize);
			}
		},
		"blur": function(){
			if (ele.val() == "") {
				ele.val(hint);
				ele.css("color", hintColor);
				if (hintFontSize) {
					ele.css("font-size", hintFontSize);
				}
			} 
		}
	});
}

function shareApp(userId) {
	if (Android) {
		Android.share(userId);
	}
}

//v1.0.10才增加此接口
function shareContent(title, content, imgUrl, targetUrl) {
	try {
		if (typeof(Android) == "undefined") {
			showWindowsToast("请点击右上角分享");
			return;
		}
	
		if (Android) {
			Android.shareContent(title, content, imgUrl, targetUrl);
		}
	} catch (e) {
		shareApp(-1);
	}
}

function getDeviceToken() {
	if (typeof(Android) != "undefined") {
		var token = Android.getDeviceToken();
		if (token && token.length > 0) return token;
	}
	return "";
}

function getAppVersion() {
	try{
		if (typeof(Android) != "undefined") {
			var s = Android.getAppVersion();
			if (s && s.length > 0) return s;
		}
		
		if (typeof(NativeBridge) == "undefined") {
			//showWindowsToast("NativeBridge undefined");
		} else {
	    	NativeBridge.getAppInfoWithCallback(function(data){
	    		//showWindowsToast(data);
		    });
	    } 
    } catch (e) {
    	//showWindowsToast(e);
    }
	
	return "0.1";
}

function getMobileInfo() {
	if (typeof(Android) != "undefined") {
		var s = Android.getMobileInfo();
		if (s && s.length > 0) return s;
	}
	return "";
}

//var LEBAO_HEADERS = {Cookie: "LEBAO_COOKIE=" + $.cookie("LEBAO_COOKIE")};
function lebaoCommonJsAction() {
	if (Android) {
		var deviceToken = Android.getDeviceToken();
		if (deviceToken && deviceToken.length > 0) {
			$.ajax({
				dataType:"json",
				url: "/json/adddevice",
				data: { "device_token" : deviceToken }
			});
		}
	}
}

function adjustChatBottom() {
	$(document).scrollTop($(document).height());
};


function useCachedData(userId, logoImgEle) {
	$.ajax({
		dataType:"json",
		url: "/json/getCachedLogo",
		data: {user_id : userId},
		//headers: LEBAO_HEADERS,
		success: function(data){
    		if (data.status == "ok" && data.data && data.data.data) {
    			var cachedData = data.data.data;
				if (cachedData.length > 0) {
					$(logoImgEle).attr("src", cachedData);
				}
    		} 
    	}
	});
}

function useCachedThreadPicData(logoImgEle) {
	$.ajax({
		dataType:"json",
		url: "/json/getCachedThreadPics",
		data: {pic_url: $(logoImgEle).attr("src")},
		//headers: LEBAO_HEADERS,
		success: function(data){
    		if (data.status == "ok" && data.data && data.data.data) {
    			var cachedData = data.data.data;
				if (cachedData.length > 0) {
					$(logoImgEle).attr("src", cachedData);
				}
    		} 
    	}
	});
}

function logAction(pageDef, ext) {
	$.ajax({
		dataType:"json",
		url: "/json/logAction",
		data: {
			url: window.location.href,
			page: pageDef,
			ext: JSON.stringify(ext)
		}
	});
}

function logAction2(pageDef, ext) {
	$.ajax({
		dataType:"json",
		url: "/json/logAction2",
		data: {
			deviceToken: getDeviceToken(),
			url: window.location.href,
			page: pageDef,		
			appVersion : getAppVersion(),
			mobileInfo : getMobileInfo(),
			ext: JSON.stringify(ext)
		}
	});
}
function setImgLazyLoad() {
	$("img").lazyload({effect: "fadeIn", threshold :400});
	    	
	$.each($("img"), function(){
		var imgEle = this;
		var src = $(imgEle).attr("src");
		if (src.indexOf("glb.clouddn.com") != -1) {//qiniu
    		$(imgEle).on("error", function(){
    			useCachedThreadPicData(imgEle);
    		});
		}
	});
}
function getCurDate(){
   var mydate = new Date();
   var month = mydate.getMonth()+1;
   var day = mydate.getDate();
   
   
   var str = "" + mydate.getFullYear();
   str += month >= 10 ? (""+month) : ("0"+month);
   str += day >= 10 ? (""+day):("0" + day);
   return str;
}
function getCurDate2(){
   var mydate = new Date();
   var month = mydate.getMonth()+1;
   var day = mydate.getDate();
   
   
   var str = "" + mydate.getFullYear() + "-";
   str += month >= 10 ? (""+month) : ("0"+month);
   str += "-";
   str += day >= 10 ? (""+day):("0" + day);
   return str;
}
function showIOSToast(text){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
    	NativeBridge.showToastWithMessage(text);
    }
}


function showAppInfo(){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
    	NativeBridge.getAppInfoWithCallback(function(data){
	                           alert(data);
	    });
    }
}

function showFeedback(){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
    	NativeBridge.showFeedback();
    }
}

function startAudioRecord(){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
    	NativeBridge.startAudioRecord();
    }
}
                                                         
function stopAudioRecord(){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
    	NativeBridge.stopAudioRecordWithCallback(function(data){
	                                              alert(data);
	    });
    }
}

function showCamera(){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
	    NativeBridge.showCameraWithCallback(
	    	function(data){
	           alert(data);
	    });
	}
}

function showPhotoLibrary(){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
	    NativeBridge.showPhotoLibraryWithCallback(function(data){
	                                              alert(data);
	    });
    }
}

function showVideo(){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
	    NativeBridge.showVideoPickerWithCallback(function(data){
	        alert(data);
	    });
    }
}

function shareWithText(title, content, url){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
    	var o = {"title": title, "content": content, "url": url};
    	NativeBridge.shareWithContent(JSON.stringify(o));
    }
}

function getCurrentLocation(){
	if (typeof(NativeBridge) == "undefined") {
    	showWindowsToast("NativeBridge is undefined.");
    } else {
		NativeBridge.getCurrentLocationWithCallback(function(data){
                                                 alert(data);
                                                          });
    }
}

function setDeviceToken(mobile){
	
	if (typeof(NativeBridge) == "undefined") {
    	//showWindowsToast("NativeBridge is undefined.");
    } else {
    	try{
			NativeBridge.getTokenWithCallback(function(data){
				//showWindowsToast("token callback: "+data);
				if (data  && data.length > 0) {
	            	$.ajax({
						dataType:"json",
						url: "/json/updatetoken",
						data: {token: data, mobile: mobile },
						//headers: LEBAO_HEADERS,
						success: function(data){
				    	}
					});
				}
                                                          });
        } catch(e) {
        	//showWindowsToast(e);
        }
    }
}
function isIOS() {
	if (typeof(NativeBridge) == "undefined") {
		return false;
	} else {
		return true;
	}
}
function isInWeixin(){
	var ua = navigator.userAgent.toLowerCase();  
    if(ua.match(/MicroMessenger/i)=="micromessenger") {  
        return true;  
    } else {  
        return false;  
    } 
}
