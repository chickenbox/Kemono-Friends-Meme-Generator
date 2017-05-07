package com.lantanisa.kfmmg;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.font.TransformAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import javax.print.AttributeException;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Text;

public class KFMMGGenerator {
	private int seed;
	
	static public final int MAX_TEXT_LENGTH = 100;
	
	static public final Color [] HEADLINE_COLORS = new Color[]{
		new Color(0x0dbc95),
		new Color(0xabd631),
		new Color(0xffa41e),
		new Color(0xff6b47),
		new Color(0x09b792),
		new Color(0xff4eaa),
		new Color(0x0e61c9),
		new Color(0x009bee),
		new Color(0x97949d),
		new Color(0xfc9c00),
		new Color(0xd7091e),
		new Color(0xc3e000),
		new Color(0x81236f)
	};
	static public final Color [][] COLORS = new Color[][]{
        new Color[]{new Color(0x14ae67),new Color(0x90c31f)},
        new Color[]{new Color(0xf39800),new Color(0xea5420)},
		new Color[]{new Color(0x00ac8e),new Color(0xe4007f)},
		new Color[]{new Color(0x01a0e9),new Color(0x217fc4)},
		new Color[]{new Color(0x9fa0a0),new Color(0xc9caca)},
		new Color[]{new Color(0xf39800),new Color(0xe60012)},
		new Color[]{new Color(0xc3d600),new Color(0xa52e8d)},
	};
	static public final Color SUBTITLE_COLOR = new Color(0x917835);
	static public final Color CONTENT_COLOR = Color.BLACK;
	
	static public final float SUBTITLE_SCALE = 3.5f;
	static public final int LINE_SPACING = -15;
	static public final int WHITE_BORDER_WIDTH = 12;
	static public final int BLACK_BORDER_WIDTH = 16;
	static public final int PADDING = BLACK_BORDER_WIDTH+2;
	static public final int PADDING_EXTRA = 10;
	
	enum UseColor{
		HEADLINE,
		TITLE,
		SUBTITLE,
		CONTENT
	}
									
	static public class OutputImageTooLargeException extends Exception {
		
	}
	
	public KFMMGGenerator( int seed ){
		this.seed = seed;
	}
	
	public BufferedImage generateMeme( String text, int fontSize, boolean whiteBg, boolean shuffle, boolean logoStyle) throws OutputImageTooLargeException{
		if( text.length()>MAX_TEXT_LENGTH){
			throw new OutputImageTooLargeException();
		}
		
		String [] lines = text.split("\n");
		
		ArrayList<BufferedImage> images = new ArrayList<>();
		
		int width = 1;
		int height = 1;
		for (String line : lines) {
			if( !line.isEmpty() ){
				int fntSize = fontSize;
				UseColor useColor = UseColor.TITLE;
				boolean useTitleFont = true;
				if( logoStyle ){
					if( lines.length==2 ){
						if( images.size()==1){
							fntSize /= SUBTITLE_SCALE;
							useColor = UseColor.SUBTITLE;
						}
					}else{
						if( images.size()!=1 && lines.length>1){
							fntSize /= SUBTITLE_SCALE;
							switch( images.size() ){
							case 0:
								useColor = UseColor.HEADLINE;
								break;
							case 2:
								useColor = UseColor.SUBTITLE;
								break;
							default:
								useColor = UseColor.CONTENT;
								break;
							}
							useTitleFont = false;
						}
					}
				}
				
				BufferedImage image = generateMemeSingleLine(line, fntSize, shuffle, useColor, useTitleFont); 
				images.add(image);			
				height += image.getHeight();
				width = Math.max(width, image.getWidth());
			}
		}
		height += LINE_SPACING*(images.size()-1); 
		
		if( width*height>1024*1024 || text.length()>MAX_TEXT_LENGTH){
			throw new OutputImageTooLargeException();
		}
		
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D g = image.createGraphics();
		
		if( whiteBg ){
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, width, height);
		}
		
		int offsetY = 0;
		for (BufferedImage bufferedImage : images) {
			int offsetX = (width-bufferedImage.getWidth())/2;
			
			g.drawImage(bufferedImage, offsetX, offsetY, null);
			
			offsetY += bufferedImage.getHeight()+LINE_SPACING;
		}
		
		g.dispose();
		
		return image;
	}
	
	private BufferedImage generateMemeSingleLine( String text, int fontSize, boolean shuffle, UseColor useColor, boolean useTitleFont ) throws OutputImageTooLargeException{
		int width = 0;
		int height = 0;
		
		Font font = new Font("MS Gothic", Font.BOLD, fontSize);
		//Font font = new Font("FriendsFu Calligraphr", Font.BOLD, fontSize);		
		//Font font = new Font(Font.SANS_SERIF, Font.BOLD, fontSize);
		AttributedString attrText = createAttributedText(text, fontSize, useTitleFont, useColor==UseColor.HEADLINE);
		
		// measure text width
		BufferedImage tmpImage = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = tmpImage.createGraphics();
		TextLayout tmpLayout = new TextLayout(attrText.getIterator(), g.getFontRenderContext());
		Rectangle2D bounds = tmpLayout.getBounds();
		int stringWidth = (int)bounds.getWidth();
		width = (int) Math.max(width, (stringWidth+PADDING+PADDING_EXTRA*2));
		height = (int) Math.max(height, (bounds.getHeight()+PADDING+PADDING_EXTRA*2));
		g.dispose();
		
		if( width*height>1024*1024 || text.length()>MAX_TEXT_LENGTH){
			throw new OutputImageTooLargeException();
		}
		
		BufferedImage outputImage = new BufferedImage(width, height,	BufferedImage.TYPE_INT_ARGB);
		g = outputImage.createGraphics();
		g.setColor(new Color(0, 0, 0, 0));
		g.fillRect(0, 0, width, height);
		
		Point offset = new Point((int)(-bounds.getX()+PADDING/2+(width-stringWidth)/2),(int)(-bounds.getY()+PADDING/2+(height-bounds.getHeight())/2));
		
		
		g.setComposite(AlphaComposite.getInstance( AlphaComposite.DST_OVER ));
		int startX = offset.x;
		int endX = offset.x;
		int lastWidth = 0;
		Color [][] shuffledColors;
		switch( useColor ){
		case HEADLINE:
			shuffledColors = new Color[HEADLINE_COLORS.length][];
			for( int i=0; i<HEADLINE_COLORS.length; i++ ){
				shuffledColors[i] = new Color[]{
						HEADLINE_COLORS[i],
						HEADLINE_COLORS[i]
				};
			}
			if( shuffle ){
				Arrays.sort(shuffledColors,new Comparator<Color[]>() {
					@Override
					public int compare(Color[] o1, Color[] o2) {
						return KFMMGGenerator.this.random()>0.5?1:-1;
					}
				});
			}
			break;
		case SUBTITLE:
			shuffledColors = new Color[][]{
				new Color[]{ SUBTITLE_COLOR, SUBTITLE_COLOR }
			};
			break;
		case CONTENT:
			shuffledColors = new Color[][]{
				new Color[]{ CONTENT_COLOR, CONTENT_COLOR }
			};
			break;
		default:
			shuffledColors = shuffle?this.shuffledColors():KFMMGGenerator.COLORS;
			break;
		}

		for( int i = 0; i<text.length(); i++ ){
			tmpLayout = new TextLayout(attrText.getIterator(null, 0, i+1), g.getFontRenderContext());
			bounds = tmpLayout.getBounds();
			int tmpWidth = (int)bounds.getWidth();
			endX += tmpWidth-lastWidth;
			lastWidth = tmpWidth;
			
			Color [] colors = shuffledColors[i%shuffledColors.length];
			
			g.translate(offset.x, offset.y);
			g.setClip(tmpLayout.getOutline(null));
			g.setTransform(AffineTransform.getTranslateInstance(0, 0));
			
			
			g.setColor(colors[1]);
			double angle = this.random()*Math.PI*2;
			Point center = new Point( startX+(endX-startX)/2, height/2 );
			g.translate(center.x,center.y);
			g.rotate(angle);				
			if( this.random()>1.0/3.0 ){
				g.fillRect(0,-height*5,width*10,height*10);
			}else{
				float ladderWidth = (endX-startX+height)/4;
				double ladderTopWidth = ladderWidth*this.random();
				double ladderBottomWidth = ladderWidth*(1+this.random()*0.5);
				GeneralPath path = new GeneralPath();
				path.moveTo(-ladderTopWidth/2,-height/2);
				path.lineTo(ladderTopWidth/2,-height/2);
				path.lineTo(ladderBottomWidth/2,height/2);
				path.lineTo(-ladderBottomWidth/2,height/2);
				path.closePath();
				g.fill(path);
			}
			g.setTransform(AffineTransform.getTranslateInstance(0, 0));
			
			g.setColor(colors[0]);
			g.fillRect(0, 0, width, height);
			
//			g.setColor(colors[0]);
//			g.fillRect((int)(startX+bounds.getX()), 0, endX-startX, height);

			
			startX = endX;
		}
		
		g.setClip(null);
		
		TextLayout layout = new TextLayout(attrText.getIterator(), g.getFontRenderContext());
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(WHITE_BORDER_WIDTH,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND ));
		g.draw(layout.getOutline(AffineTransform.getTranslateInstance(offset.x, offset.y)));
		g.setColor(Color.BLACK);
		g.setStroke(new BasicStroke(BLACK_BORDER_WIDTH,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
		g.draw(layout.getOutline(AffineTransform.getTranslateInstance(offset.x+2, offset.y+2)));
		
		g.dispose();
		
		return outputImage;
	}
	
	private AttributedString createAttributedText( String text, int fontSize, boolean useTitleFont, boolean randomTransform ){
		AttributedString attrStr = new AttributedString(text);
		
		Font [] fonts = new Font[]{
             	new Font("FriendsFu Calligraphr", Font.BOLD, fontSize),
             	new Font("MS Gothic", Font.BOLD, fontSize),
             	new Font("Microsoft MHei", Font.BOLD, fontSize),
             	new Font(Font.SANS_SERIF, Font.BOLD, fontSize)
		};
		
		if(!useTitleFont){
			fonts = Arrays.copyOfRange(fonts, 1, fonts.length);
		}
		
	AffineTransform inventLastTransform = new AffineTransform();
		for( int i=0; i<text.length(); i++ ){
			for( int j=0; j<fonts.length; j++ ){
				Font font = fonts[j];
				if( font.canDisplay(text.charAt(i)) || j>=fonts.length-1 ){
					attrStr.addAttribute(TextAttribute.FONT, font, i, i+1);
					if( randomTransform ){
						double scale = (random()*0.2)+0.8;
						AffineTransform t = new AffineTransform();
						t.rotate((Math.random()*6-3)*Math.PI/180);
						t.scale(scale,scale);
						AffineTransform appliedT = (AffineTransform)t.clone();
						appliedT.concatenate(inventLastTransform);
						attrStr.addAttribute(TextAttribute.FONT, font.deriveFont(appliedT), i, i+1);
						inventLastTransform = (AffineTransform)t.clone();
						try {
							inventLastTransform.invert();
						} catch (NoninvertibleTransformException e) {}
					}else{
						attrStr.addAttribute(TextAttribute.FONT, font, i, i+1);
					}
					break;
				}
			}
		}
		
		return attrStr;
	}
	
	private Color [][] shuffledColors(){
		Color [][] cloned = KFMMGGenerator.COLORS.clone();		
		
		Arrays.sort( cloned, new Comparator<Color[]>(){
			@Override
			public int compare(Color[] o1, Color[] o2) {
				return KFMMGGenerator.this.random()>0.5?1:-1;
			}
		} );
		return cloned;
	}
	
	private double random(){
		double x = Math.sin(this.seed++) * 10000;
	    return x - Math.floor(x);
	}
	
	public BufferedImage tooLargeImage(){
		try {
			return generateMeme("圖片大小上限是 1024x1024px\n文字內容不超出100字", 24, true, false, false);
		} catch (OutputImageTooLargeException e) {
			return null;
		}
	}
}
