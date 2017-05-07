function KemonoFriendMemeGenerator(seed){
	this.seed = seed;
}

KemonoFriendMemeGenerator.COLORS = [
                                    ["#14ae67","#90c31f"],
                                    ["#f39800","#ea5420"],
                                    ["#00ac8e","#e4007f"],
                                    ["#01a0e9","#217fc4"],
                                    ["#9fa0a0","#c9caca"],
                                    ["#f39800","#e60012"],
                                    ["#c3d600","#a52e8d"],
                                    ]

KemonoFriendMemeGenerator.prototype.generateMeme = function( text, fontSize, width, height, whiteBg, shuffle ){
	var canvas = document.createElement("canvas");
	canvas.width = width;
	canvas.height = height;
	
	var ctx = canvas.getContext("2d");
	ctx.font = "bold "+fontSize+"px sans-serif";
	
	var metric = ctx.measureText(text);
	width = Math.max(width,metric.width*1.1);
	height = Math.max(height,fontSize*1.8);
	canvas.width = width;
	canvas.height = height;
	ctx.font = "bold "+fontSize+"px sans-serif";
	var offset = { x: (width-metric.width)/2, y: (height+fontSize)/2 };

	ctx.clearRect(0,0,width,height);
	
	ctx.fillStyle = "white";
	ctx.lineWidth = 2;
	ctx.fillText(text,offset.x,offset.y);
	
	ctx.globalCompositeOperation = "source-atop";
	var startX = offset.x;
	var endX = offset.x;
	var lastWidth = 0;
	var appendText = "";
	var shuffledColors = shuffle?
			KemonoFriendMemeGenerator.COLORS.filter(function(){return true}).sort(function(){return this.random()>0.5}.bind(this)):
			KemonoFriendMemeGenerator.COLORS;
	for( var i in text ){
		var c = text[i];
		appendText += c;
		metric = ctx.measureText(appendText);
		endX += metric.width-lastWidth;
		lastWidth = metric.width;
		
		console.log( "character: "+c+" start: "+startX+" end: "+endX );
		
		var colors = shuffledColors[i%shuffledColors.length];
		ctx.fillStyle = colors[0];
		ctx.fillRect( startX, 0, endX-startX, height );
		
		ctx.save();
		ctx.beginPath();
		ctx.rect(startX, 0, endX-startX, height);
		ctx.clip();
		var angle = this.random()*Math.PI*2;
		var center = {
			x: startX+(endX-startX)/2,
			y: height/2
		};
		ctx.fillStyle = colors[1];
		ctx.beginPath();
		if( this.random()>1.0/3.0 ){
			ctx.translate(center.x,center.y);
			ctx.rotate(angle);
			ctx.rect(0,-height*5,width*10,height*10);
		}else{
			ctx.translate(center.x,center.y);
			ctx.rotate(angle);
			var ladderWidth = (endX-startX+height)/4;
			var ladderTopWidth = ladderWidth*this.random();
			var ladderBottomWidth = ladderWidth*(1+this.random()*0.5);
			ctx.moveTo(-ladderTopWidth/2,-height/2);
			ctx.lineTo(ladderTopWidth/2,-height/2);
			ctx.lineTo(ladderBottomWidth/2,height/2);
			ctx.lineTo(-ladderBottomWidth/2,height/2);
			ctx.closePath();
		}
		ctx.fill();
		ctx.restore();
		
		startX = endX;
	}	
	ctx.globalCompositeOperation = "destination-over";
	ctx.lineJoin = "round";
	ctx.lineWidth = 6;
	ctx.strokeStyle = "white";
	ctx.strokeText(text,offset.x,offset.y);
	ctx.lineWidth = 8;
	ctx.strokeStyle = "black";
	ctx.strokeText(text,offset.x+1,offset.y+1);
	
	if( whiteBg ){
		ctx.fillStyle = "white";
		ctx.fillRect(0,0,width,height);
	}

	return canvas;
}

KemonoFriendMemeGenerator.prototype.random = function(){
    var x = Math.sin(this.seed++) * 10000;
    return x - Math.floor(x);
}
