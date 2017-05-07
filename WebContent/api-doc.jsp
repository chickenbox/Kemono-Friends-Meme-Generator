<%
final String VERSION = "1.0";
%>
<html>
<head>
<title>Kemono Friends Meme Generator REST API Document v<%=VERSION%></title>
</head>
<body>
<pre>
-------------------------------------------------------------
Kemono Friends Meme Generator REST API Document v<%=VERSION%>
-------------------------------------------------------------
Author: ANMC
last update: 7-5-2017

API Domain: http://kfmmg.lantansia.com

==============
Get Meme Image
==============

~~~~~~
*Path:
~~~~~~
 /kfmmg/KFMMGServer

~~~~~~~~
*Method:
~~~~~~~~
 GET

~~~~~~~~~~~~
*Parameters:
~~~~~~~~~~~~
-seed			Integer
-text			String
-fontSize		Integer
-whiteBg		Boolean
-shuffle		Boolean
-logoStyle(Optional)	Boolean

~~~~~~~~~~~~~~~~~~~~~~~~
*Parameters Description:
~~~~~~~~~~~~~~~~~~~~~~~~
-seed			Integer
Random seed number for image generation.

-text			String
Input text message for image generation. Max length 100 characters(before uriencoding).

-fontSize		Integer
Font size for image generation.

-whiteBg		Boolean
If true, output image with white background, otherwise, output image with transparent background.

-shuffle		Boolean
If true, order of text colors is in random order, otherwise, a static color order is applied.

-logoStyle(Optional)	Boolean
If true, generate image with logo style, otherwise, image as multiline text.
Default is false.

~~~~~~~~~~
*Response:
~~~~~~~~~~
On a successful response, the response body is a png Image.
With response header value "KFMMG-Result" equals to "OK".

On a failure response, the response body is a error image in png format.
With response header value "KFMMG-Result" equals to "RESULT_IMAGE_TOO_LARGE_OR_TEXT_TOO_LONG".
Or
A redirect response to http://kfmmg.lantansia.com/kfmmg.
With response header value "KFMMG-Result" equals to "NO_IMAGE_GENERATED".
If no image can be generated with input parameters.

Example API call:
http://kfmmg.lantansia.com/kfmmg/KFMMGServer?seed=701&text=%E5%A5%B3%E3%81%AE%E5%AD%90%E3%81%AE%E5%A7%BF%E3%81%AB%E3%81%AA%E3%81%A3%E3%81%9F%E5%8B%95%E7%89%A9%E3%81%9F%E3%81%A1%E3%81%8C%E7%B9%B0%E3%82%8A%E5%BA%83%E3%81%92%E3%82%8B%E5%A4%A7%E5%86%92%E9%99%BA%EF%BC%81%0A%E3%81%91%E3%82%82%E3%81%AE%E3%83%95%E3%83%AC%E3%83%B3%E3%82%BA%0AKEMONO%20FRIENDS&fontSize=80&whiteBg=false&shuffle=false&logoStyle=true

</pre>
</body>
</html>