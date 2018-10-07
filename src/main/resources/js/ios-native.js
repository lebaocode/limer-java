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