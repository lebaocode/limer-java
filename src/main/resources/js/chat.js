var conn = null;
var typing = false;
var mLastTryConnectTime = 0;
var mMsgQueue = [];

var sendMsg = function(msg) {
	//如果是自己给自己发，就不发了。
	if (chat_to == username) return;
	if (prohibitStatus) {
		showAndroidToast("您当前处于禁言状态，无法发消息。");
		return;
	}
	
	//队列 TODO
	var curTime = $.now() + TIME_MINUS;
	var msgId = curTime + "_" + userid;

	var options = {
		to: chat_to,
		msg: msg,
		type : chat_type,
		ext: {
			from_nickname: nickname,
			from_userid: userid,
			from_userlogo: userlogourl,
			to: chat_to,
			type: chat_type,
			msg_id: msgId,
			user_sign: usersign,
			send_time: curTime
			}
	};
	
	mMsgQueue.push(options);
	
	try {
		var logchat = {
			to: chat_to,
			msg: msg,
			msg_id: msgId,
			type : chat_type,
			from_username: username,
			from_nickname: nickname
		};
		$.ajax({
			dataType:"json",
			url: "/json/logchat",
			data: logchat,
			headers: LEBAO_HEADERS,
			success: function(data) {
	    		if (data.data && data.data.isProhibited){
	    			if (data.data.isProhibited == "true"){
	    				prohibitStatus = true;
	    			} else if (data.data.isProhibited == "false"){
	    				prohibitStatus = false;
	    			}
	    		}
	    	}
		});
	} catch (e) {}
};

var log = function(msg) {
	//$("#chat_div").append("<div class='sys-msg'>"+ msg +"</div>");
	//adjustChatBottom();
};

var displaySysMsg = function(msg) {
	$("#chat_div").append("<div class='sys-msg'>"+ msg +"</div>");
	adjustChatBottom();
};

var handleOnOpen = function() {
	//log("onopened");
	conn.setPresence();
};

var handleOnTextMessage = function(message) {
	//处理太多消息的情况 TODO
	if (!message) return;
	
	//可能会收到非当前会话的消息，需要过滤掉
	var cur_type = message.ext.type;
	if (cur_type && cur_type != chat_type) return;
	
	var msg_to = message.ext.to;
	var textMsg = message.data;
	var from_userid = message.ext.from_userid;
	var from_usernickname = message.ext.from_nickname;
	var from_userlogo = message.ext.from_userlogo;
	var msg_id = message.ext.msg_id;
	var send_time = message.ext.send_time;
	var from_usersign = message.ext.user_sign;
		
	if (chat_type == "chat") {
		//如果是单聊，则只接收 当前聊天对象发来 的消息
		if (chat_to != message.from) return;
	} else {
		//如果是群聊，则只接收 对象是本群的 消息
		if (msg_to && msg_to != groupid) return;
	}
	
	//如果时间比这些msg的小，就不用展示了
	try {
		if (displayedHistoryMsgIds.length > 0) {
			var tempTime = send_time + "";
			if (tempTime < displayedHistoryMsgIds[0].split("_")[0]) {
				return;
			}
		}
	} catch (e) {}
	
	if ($.inArray(msg_id, displayedHistoryMsgIds) == -1){
		//显示在自己的窗口里
		
		if (from_userid != userid) {
			//别人的消息
			var contentType = message.ext.content_type;
			if (contentType && contentType == "url") {
				//结构化url消息
				$("#chat_div").append(
					"<div class='user-msg-div'>" +
					    "<div class='user-logo' onclick='window.location=\"/user/profile?id="+ from_userid +"\";'><img src='"+ from_userlogo + "?imageView2/1/w/40/h/40" +"' onerror='useCachedData(\""+ from_userid +"\", this);' /></div>" +
					    "<div class='user-name'>"+ from_usernickname + (from_usersign && from_usersign.length>0 ? ("("+ from_usersign +")") : "" ) +"</div>" +
					    "<div class='user-msg'>"+ textMsg +"</div>" +
					    "<div class='user-msg-url' onclick='window.location=\""+ message.ext.url +"\";'>"+ 
					    	"<div class='user-msg-url-title'>"+ message.ext.url_title + "</div>" +
					    	"<div class='user-msg-url-desc'>"+ message.ext.url_desc + "</div>" +
					    "</div>" +
					"</div>" +
					"<div class='cb'></div>"
				);
			} else {
				$("#chat_div").append(
					"<div class='user-msg-div'>" +
					    "<div class='user-logo' onclick='window.location=\"/user/profile?id="+ from_userid +"\";'><img src='"+ from_userlogo + "?imageView2/1/w/40/h/40" +"' onerror='useCachedData(\""+ from_userid +"\", this);' /></div>" +
					    "<div class='user-name'>"+ from_usernickname + (from_usersign && from_usersign.length>0 ? ("("+ from_usersign +")") : "" ) +"</div>" +
					    "<div class='user-msg'>"+ textMsg +"</div>" +
					"</div>" +
					"<div class='cb'></div>"
				);
			}
		} else {
			//自己发的消息
			var contentType = message.ext.content_type;
			if (contentType && contentType == "url") {
				//结构化url消息
				$("#chat_div").append(
					"<div class='self-msg-div'>" +
					    "<div class='self-logo'><img src='"+ from_userlogo +"?imageView2/1/w/40/h/40" +"' onerror='useCachedData(\""+ from_userid +"\", this);' /></div>" +
					    "<div class='self-msg'>"+ textMsg +"</div>" +
					    
					    "<div class='self-msg-url' onclick='window.location=\""+ message.ext.url +"\";'>"+ 
					    	"<div class='self-msg-url-title'>"+ message.ext.url_title + "</div>" +
					    	"<div class='self-msg-url-desc'>"+ message.ext.url_desc + "</div>" +
					    "</div>" +
					"</div>" + 
					"<div class='cb'></div>"
				);
			} else {
				$("#chat_div").append(
					"<div class='self-msg-div'>" +
					    "<div class='self-logo'><img src='"+ from_userlogo +"?imageView2/1/w/40/h/40" +"' onerror='useCachedData(\""+ from_userid +"\", this);' /></div>" +
					    "<div class='self-msg'>"+ textMsg +"</div>" +
					"</div>" + 
					"<div class='cb'></div>"
				);
			}
		}
		
		adjustChatBottom();
	
	
		//记录消息状态
		try {
			var logchat = {
				msg_id: msg_id,
				from_conv_id: chat_to,
				msg_type: (chat_type=="groupchat" ? 1 : 0) 
			};
			$.ajax({
				dataType:"json",
				url: "/json/receivemsg",
				data: logchat,
				headers: LEBAO_HEADERS
			});
			
		} catch (e) {}
	}
};

var handleOnError = function(message) {
	//log("onError:"+JSON.stringify(message));
};

var handleOnClosed = function() {
	//log("onClosed");
};

var openConnection = function(){
	//log("try to open conn");
	mLastTryConnectTime = $.now() + TIME_MINUS;
	conn.open({
		user: username,
		pwd: password,
		appKey: 'lebaoapp#lebao' 
	});
};

var sendMsgToServer = function(msg){
	conn.sendTextMessage(msg);
};

$(window).unload(function(){
	if (conn) {
		conn.close();
	}
});

conn = new Easemob.im.Connection();

conn.init({
	onOpened: function(){
		handleOnOpen();
	},
	onTextMessage: function(message) {
		handleOnTextMessage(message);
	},
	onError: function(message){
		handleOnError(message);
	},
	onClosed: function(){
		handleOnClosed();
	}
});
openConnection();

$("#btn_send").on('click', function(){
	$("#my_msg").focus();
	var msg = $("#my_msg").val();
	if (msg.length == 0) return;
	
	sendMsg(msg);
	
	//显示在自己的窗口里
	$("#chat_div").append(
		"<div class='self-msg-div'>" +
		    "<div class='self-logo'><img src='"+ userlogourl +"?imageView2/1/w/40/h/40" +"' onerror='/native_app_local_assets/bb_demo.jpg' /></div>" +
		    "<div class='self-msg'>"+ msg +"</div>" +
		"</div>" + 
		"<div class='cb'></div>"
	);
	
	//清空已经发送的文字
	$("#my_msg").val("");
	
	adjustChatBottom();
});

$("#my_msg").keypress(function(e){
	typing = true;
	var key = e.which;
	if (key == 13) {
		$("#btn_send").trigger("click");
		typing = false;
	}
});


$("#my_msg").on('focus', function(){
	adjustChatBottom();
});

setInterval(function(){
	if (mMsgQueue.length == 0) return;
	
	if (!conn) conn = new Easemob.im.Connection();

	if (conn.isOpened()){
		var len = mMsgQueue.length;
		for (var i = 0; i < len; i++) {
			var msg = mMsgQueue.shift();
			sendMsgToServer(msg);
		}
		
	} else if (!conn.isOpening() && !conn.isClosing()){
		var curTime = $.now() + TIME_MINUS;
		if (curTime - mLastTryConnectTime > 30000) {
			openConnection();
		}
	}
	
	
}, 1000);

