<%@ page pageEncoding="UTF-8"%>
<%
final String VERSION = "1.16";
final String TITLE = "動物好友貼圖生成器";
final String DESCRIPTION = "快樂地分享貼圖";
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
<meta name="viewport" content="width=device-width, initial-scale=1"/>
<meta name="title" content="<%=TITLE%>"/>
<meta name="description" content="<%=DESCRIPTION%>"/>
<meta name="keywords" content="けものフレンズ,Meme Generator,動物好友,動物朋友,獸娘樂園,獸娘動物園"/>
<meta name="author" content="ANMC"/>
<%
	String thumbnailUrl = request.getParameter("thumbnail");
	if( thumbnailUrl!=null ){
%>
<meta property="og:image" content="<%=thumbnailUrl%>" />

<meta name="twitter:card" content="summary_large_image">
<meta name="twitter:title" content="<%=TITLE%>">
<meta name="twitter:description" content="<%=DESCRIPTION%>">
<meta name="twitter:image" content="<%=thumbnailUrl%>">	
<%
	}
%>
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
			"動物好友貼圖生成器\nv<%=VERSION%>",
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
	alert("已把圖像連結複製到剪貼簿。");
}

</script>
<title>動物好友貼圖生成器v<%=VERSION%></title>
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
<div style="display: table-cell; text-align: left"><b>輸入文字內容(最多100字):</b><br><textarea id="content_text" type="text" onchange="updateMeme()" onblur="updateMeme()" onkeypress="updateMeme()" rows="5" style="width:320px" maxlength="100" placeholder="女の子の姿になった動物たちが繰り広げる大冒険！
けものフレンズ
KEMONO FRIENDS"></textarea></div>
<div style="display:table">
<div style="display:table-row"><div style="display:table-cell" align="right"><b>字型大小:</b></div><div style="display:table-cell" align="left"><input id="font_size" type="number" value="80" onchange="updateMeme()"/></div></div>
<div style="display:table-row"><div style="display:table-cell" align="right"><b>隨機種子:</b></div><div style="display:table-cell" align="left"><input id="meme_seed" type="number" value="1" onchange="updateMeme()" style="width:100px"/><input type="button" value="換一個" onclick="meme_seed.value=Math.floor(Math.random()*1000);updateMeme();"/></div></div>
</div>
<div style="display:table">
<div style="display:table-row"><div style="display:table-cell" align="right"><b>Logo樣式:</b></div><div style="display:table-cell" align="left"><input id="meme_logo_style" type="checkbox" checked onchange="updateMeme()"/></div></div>
<div style="display:table-row"><div style="display:table-cell" align="right"><b>透明背景:</b></div><div style="display:table-cell" align="left"><input id="meme_trans_bg" type="checkbox" checked onchange="updateMeme()"/></div></div>
<div style="display:table-row"><div style="display:table-cell" align="right"><b>隨機顏色排序:</b></div><div style="display:table-cell" align="left"><input id="meme_shuffle" type="checkbox" onchange="updateMeme()"/></div></div>
<div style="display:table-row"><div style="display:table-cell" align="right"><b>鎖定2:1圖像:</b></div><div style="display:table-cell" align="left"><input id="meme_ratio_lock" type="checkbox" checked onchange="updateMeme()"/></div></div>
</div>
<br><br>
<b>完成圖像:</b><br>
<img id="output_meme" alt="output_meme" src=""/><br><br>
<b>圖像網址:</b> <input id="share_url" type="text" readonly/> <input type="image" onclick="copyToClipboard()" src="copy-clipboard.png" style="padding: 0px 0px;"/><br><br>
<input type="image" onclick="shareMemeFB()" src="button-fb.png"/>
<input type="image" onclick="shareMemeTW()" src="tweet-button-2015.png"/>
<br><br><br><br><br><br>
<div style="display: table-cell; text-align: left">
<font color="black">
<font size="2"><b>作者 ANMC <a href="mailto:lamylanmc@hotmail.com">lamylanmc@hotmail.com</a><br><br>
<font size="3"><b>感謝:</b></font><br>
<b>FriendsFu字型 作者 kyoyababa</b><br>
<a href="https://github.com/kyoyababa/font-FriendsFu">https://github.com/kyoyababa/font-FriendsFu</a><br><br>
<div align="right">
開發者資訊:<br>
<a href="api-doc.jsp">REST API 文件</a><br></div>
</div><br>
<h7>最後更新 07-05-2017</h7>
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