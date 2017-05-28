<%@page import="java.net.URLEncoder"%>
<%@page import="com.lantanisa.common.localization.Localizer"%>
<%@ page pageEncoding="UTF-8"%>
<%
final String VERSION = "1.20";
final String TITLE = Localizer.shared.get("site_title",request);
final String DESCRIPTION = Localizer.shared.get("site_description",request);
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<meta name="mobile-web-app-capable" content="yes">
<%
	switch(Localizer.Language.getLanguage(request)){
	case TRADITIONAL_CHINESE:
		%>
<link rel="manifest" href="manifest_zh.json">
		<%
		break;
	default:
		%>
<link rel="manifest" href="manifest_en.json">
		<%
		break;
	}
%>
<link rel="icon" href="img/icon_32x32.png" type="image/png" />
<link rel="icon" sizes="192x192" href="img/icon_192x192.png">
<link rel="apple-touch-icon" sizes="128x128" href="img/icon_128x128.png">
<meta name="title" content="<%=TITLE%>"/>
<meta name="description" content="<%=DESCRIPTION%>"/>
<meta name="keywords" content="けものフレンズ,Meme Generator,動物好友,動物朋友,獸娘樂園,獸娘動物園,動物朋友標題,動物朋友Logo,產生器,製作器"/>
<meta name="author" content="ANMC"/>
<%
	String thumbnailUrl = request.getParameter("thumbnail");
	if( thumbnailUrl==null ){
		thumbnailUrl = request.getRequestURL()+"KFMMGServer?seed=301&text="+URLEncoder.encode(TITLE+"\nv"+VERSION,"UTF8")+"&fontSize=80&whiteBg=false&shuffle=false&logoStyle=true&fixedRatio=1.91";
	}
%>
<meta property="og:title" content="<%=TITLE%>">
<meta property="og:description" content="<%=DESCRIPTION%>">
<meta property="og:url" content="<%=request.getRequestURL()+"?"+request.getQueryString()%>">
<meta property="og:image" content="<%=thumbnailUrl%>" />

<meta name="twitter:card" content="summary_large_image">
<meta name="twitter:title" content="<%=TITLE%>">
<meta name="twitter:description" content="<%=DESCRIPTION%>">
<meta name="twitter:image" content="<%=thumbnailUrl%>">	
<link rel="icon" 
      type="image/png" 
      href="favicon.png">
<script>
function generateMeme( seed, text, fontSize, whiteBg, shuffle, logoStyle, ratioLock ){
	return "KFMMGServer?"+
			"seed="+seed+
			"&text="+encodeURIComponent(text.length==0?content_text.placeholder:text)+
			"&fontSize="+fontSize+
			"&whiteBg="+whiteBg+
			"&shuffle="+shuffle+
			"&logoStyle="+logoStyle+
			(ratioLock?"&fixedRatio=1.91":"");
}

function onStart(){
	meme_title.onload = function(){
		var width = meme_title.naturalWidth;
		var height = meme_title.naturalHeight;
		meme_title.style.width = width/2+"px";
		meme_title.style.height = height/2+"px";
	}
	output_meme.onload = function(){
		var width = output_meme.naturalWidth;
		var height = output_meme.naturalHeight;
		output_meme.style.width = width/2+"px";
		output_meme.style.height = height/2+"px";
		updateShareUrl();
	}

	meme_title.src = generateMeme(
			parseInt(meme_seed.value),
			"<%=TITLE%>\nv<%=VERSION%>",
			64,
			false,
			false,
			true,
			false
			);
	meme_seed.value = Math.floor(Math.random()*1000);
	updateMeme();
}

function updateMeme(){
	output_meme.src = generateMeme(
			parseInt(meme_seed.value),
			content_text.value,
			parseInt(font_size.value),
			!meme_trans_bg.checked,
			meme_shuffle.checked,
			meme_logo_style.checked,
			meme_ratio_lock.checked
			);	 
}
 
 function updateShareUrl(){
	 share_url.value = output_meme.src;	 
 }
 
function shareMemeFB(){
	var shareUrl = window.location+"?thumbnail="+encodeURIComponent(window.location+generateMeme(
			parseInt(meme_seed.value),
			content_text.value,
			parseInt(font_size.value),
			!meme_trans_bg.checked,
			meme_shuffle.checked,
			meme_logo_style.checked,
			false
			)+"&upScaleWidth=650&upScaleHeight=340");
	window.open("https://www.facebook.com/sharer/sharer.php?u="+encodeURIComponent(shareUrl), "pop", "width=600, height=400, scrollbars=no");
}

function shareMemeTW(){
	var shareUrl = window.location+"?thumbnail="+encodeURIComponent(window.location+generateMeme(
			parseInt(meme_seed.value),
			content_text.value,
			parseInt(font_size.value),
			!meme_trans_bg.checked,
			meme_shuffle.checked,
			meme_logo_style.checked,
			false
			)+"&upScaleWidth=600&upScaleHeight=321");
	window.open("https://twitter.com/intent/tweet?url="+encodeURIComponent(shareUrl), "pop", "width=600, height=400, scrollbars=no");
}

function copyToClipboard(){
	share_url.select();
	document.execCommand('copy');
	alert("<%=Localizer.shared.get("copy_clipboard_prompt",request)%>");
}

</script>
<title><%=TITLE%>v<%=VERSION%></title>
<style>
@font-face {
  font-family: 'cwTeXHei';
  font-style: normal;
  font-weight: 500;
  src: url(//fonts.gstatic.com/ea/cwtexhei/v3/cwTeXHei-zhonly.eot);
  src: url(//fonts.gstatic.com/ea/cwtexhei/v3/cwTeXHei-zhonly.eot?#iefix) format('embedded-opentype'),
       url(//fonts.gstatic.com/ea/cwtexhei/v3/cwTeXHei-zhonly.woff2) format('woff2'),
       url(//fonts.gstatic.com/ea/cwtexhei/v3/cwTeXHei-zhonly.woff) format('woff'),
       url(//fonts.gstatic.com/ea/cwtexhei/v3/cwTeXHei-zhonly.ttf) format('truetype');
}

body{
	color: #55461d;
	text-shadow: -1px 0 #FFFFFF,0 1px #FFFFFF,1px 0 #FFFFFF,0 -1px #FFFFFF;
	font-family: 'cwTeXHei', serif;
}
a{
	text-decoration: none;
}
</style>
</head>
<body onload="onStart()" style="background-image: url(background.png)" alink="black" vlink="black" link="black">
<center>
<img id="meme_title"/><br><br><br>
<div style="display: table-cell; text-align: left"><b><%=Localizer.shared.get("input_text_content",request)%></b><br><textarea id="content_text" type="text" onchange="updateMeme()" onblur="updateMeme()" onkeypress="updateMeme()" rows="5" style="width:320px" maxlength="100" placeholder="女の子の姿になった動物たちが繰り広げる大冒険！
けものフレンズ
KEMONO FRIENDS"></textarea></div>
<div style="display:table">
<div style="display:table-row"><div style="display:table-cell" align="right"><b><%=Localizer.shared.get("font_size",request)%></b></div><div style="display:table-cell" align="left"><input id="font_size" type="number" value="80" onchange="updateMeme()"/></div></div>
<div style="display:table-row"><div style="display:table-cell" align="right"><b><%=Localizer.shared.get("rand_seed",request)%></b></div><div style="display:table-cell" align="left"><input id="meme_seed" type="number" value="1" onchange="updateMeme()" style="width:100px"/><input type="button" value="<%=Localizer.shared.get("randomize",request)%>" onclick="meme_seed.value=Math.floor(Math.random()*1000);updateMeme();"/></div></div>
</div>
<div style="display:table">
<div style="display:table-row"><div style="display:table-cell" align="right"><b><%=Localizer.shared.get("logo_style",request)%></b></div><div style="display:table-cell" align="left"><input id="meme_logo_style" type="checkbox" checked onchange="updateMeme()"/></div></div>
<div style="display:table-row"><div style="display:table-cell" align="right"><b><%=Localizer.shared.get("transparent_background",request)%></b></div><div style="display:table-cell" align="left"><input id="meme_trans_bg" type="checkbox" checked onchange="updateMeme()"/></div></div>
<div style="display:table-row"><div style="display:table-cell" align="right"><b><%=Localizer.shared.get("random_color_order",request)%></b></div><div style="display:table-cell" align="left"><input id="meme_shuffle" type="checkbox" onchange="updateMeme()"/></div></div>
<div style="display:table-row"><div style="display:table-cell" align="right"><b><%=Localizer.shared.get("lock_aspect_ratio",request)%></b></div><div style="display:table-cell" align="left"><input id="meme_ratio_lock" type="checkbox" checked onchange="updateMeme()"/></div></div>
</div>
<br><br>
<b><%=Localizer.shared.get("completed_image",request)%></b><br>
<img id="output_meme" alt="output_meme" src=""/><br><br>
<b><%=Localizer.shared.get("image_url",request)%></b> <input id="share_url" type="text" readonly/> <input type="image" onclick="copyToClipboard()" src="copy-clipboard.png" style="padding: 0px 0px;"/><br><br>
<input type="image" onclick="shareMemeFB()" src="button-fb.png"/>
<input type="image" onclick="shareMemeTW()" src="tweet-button-2015.png"/>
<br><br><br><br><br><br>
<div style="display: table-cell; text-align: left">
<font color="black">
<font size="2"><b><%=Localizer.shared.get("author",request)%> ANMC <a href="mailto:lamylanmc@hotmail.com">lamylanmc@hotmail.com</a><br><br>
<font size="3"><b><%=Localizer.shared.get("credit",request)%></b></font><br>
<b>FriendsFu<%=Localizer.shared.get("font",request)%> <%=Localizer.shared.get("author",request)%> kyoyababa</b><br>
<a href="https://github.com/kyoyababa/font-FriendsFu">https://github.com/kyoyababa/font-FriendsFu</a><br><br>
<div align="right">
<%=Localizer.shared.get("for_developer",request)%><br>
<a href="api-doc.jsp">REST API <%=Localizer.shared.get("document",request)%></a><br></div>
</div><br>
<h7><%=Localizer.shared.get("last_update",request)%> 16-05-2017</h7>
</font>
</center>
</body>
</html>

<!--

Backlog:

copy to clipboard
Chinese fb share button
rotate size headline
 
 -->