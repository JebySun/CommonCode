$(function() {
	//解决jQuery从1.9 版开始，移除了 $.browser的问题。 
	(function(jQuery) {
		if (jQuery.browser)
			return;
		jQuery.browser = {};
		jQuery.browser.mozilla = false;
		jQuery.browser.webkit = false;
		jQuery.browser.opera = false;
		jQuery.browser.msie = false;
		var nAgt = navigator.userAgent;
		jQuery.browser.name = navigator.appName;
		jQuery.browser.fullVersion = '' + parseFloat(navigator.appVersion);
		jQuery.browser.majorVersion = parseInt(navigator.appVersion, 10);
		var nameOffset, verOffset, ix;
		// In Opera, the true version is after "Opera" or after "Version"
		if ((verOffset = nAgt.indexOf("Opera")) != -1) {
			jQuery.browser.opera = true;
			jQuery.browser.name = "Opera";
			jQuery.browser.fullVersion = nAgt.substring(verOffset + 6);
			if ((verOffset = nAgt.indexOf("Version")) != -1)
				jQuery.browser.fullVersion = nAgt.substring(verOffset + 8);
		}
		// In MSIE, the true version is after "MSIE" in userAgent
		else if ((verOffset = nAgt.indexOf("MSIE")) != -1) {
			jQuery.browser.msie = true;
			jQuery.browser.name = "Microsoft Internet Explorer";
			jQuery.browser.fullVersion = nAgt.substring(verOffset + 5);
		}
		// In Chrome, the true version is after "Chrome"
		else if ((verOffset = nAgt.indexOf("Chrome")) != -1) {
			jQuery.browser.webkit = true;
			jQuery.browser.name = "Chrome";
			jQuery.browser.fullVersion = nAgt.substring(verOffset + 7);
		}
		// In Safari, the true version is after "Safari" or after "Version"
		else if ((verOffset = nAgt.indexOf("Safari")) != -1) {
			jQuery.browser.webkit = true;
			jQuery.browser.name = "Safari";
			jQuery.browser.fullVersion = nAgt.substring(verOffset + 7);
			if ((verOffset = nAgt.indexOf("Version")) != -1)
				jQuery.browser.fullVersion = nAgt.substring(verOffset + 8);
		}
		// In Firefox, the true version is after "Firefox"
		else if ((verOffset = nAgt.indexOf("Firefox")) != -1) {
			jQuery.browser.mozilla = true;
			jQuery.browser.name = "Firefox";
			jQuery.browser.fullVersion = nAgt.substring(verOffset + 8);
		}
		// In most other browsers, "name/version" is at the end of userAgent
		else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) < (verOffset = nAgt.lastIndexOf('/'))) {
			jQuery.browser.name = nAgt.substring(nameOffset, verOffset);
			jQuery.browser.fullVersion = nAgt.substring(verOffset + 1);
			if (jQuery.browser.name.toLowerCase() == jQuery.browser.name.toUpperCase()) {
				jQuery.browser.name = navigator.appName;
			}
		}
		// trim the fullVersion string at semicolon/space if present
		if ((ix = jQuery.browser.fullVersion.indexOf(";")) != -1)
			jQuery.browser.fullVersion = jQuery.browser.fullVersion.substring(0, ix);
		if ((ix = jQuery.browser.fullVersion.indexOf(" ")) != -1)
			jQuery.browser.fullVersion = jQuery.browser.fullVersion.substring(0, ix);
		jQuery.browser.majorVersion = parseInt('' + jQuery.browser.fullVersion,10);
		if (isNaN(jQuery.browser.majorVersion)) {
			jQuery.browser.fullVersion = '' + parseFloat(navigator.appVersion);
			jQuery.browser.majorVersion = parseInt(navigator.appVersion, 10);
		}
		jQuery.browser.version = jQuery.browser.majorVersion;
	})(jQuery);

	//检查浏览器版本,如果IE浏览器，并且低于IE9，则提示用户升级浏览器
	if($.browser.msie) {
		var msieVersion = $.browser.version; 
		if(msieVersion<9.0){
			var titleStr = "WEB前端开发攻城师的提示";
			var msgStr = "您的IE浏览器版本为IE"+msieVersion+"，已经严重落后，为了提供给您更加安全和舒畅的浏览体验，强烈建议您升级IE浏览器或者使用Chrome，Firefox，360极速浏览器等更加优秀的浏览器，否则，您很可能不能正常浏览本网站。谢谢您的支持！";
			var warmingStr = "<div style='padding:8px;background-color:#FFDD00;color:#FF0000;font-size:16px;'><p style='font-weight:bold; margin-bottom:10px;'>"+titleStr+"</p>"+msgStr+"</div>";
			$(warmingStr).prependTo("body");
		}
	}
});

