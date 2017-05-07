package com.lantanisa.kfmmg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.lantanisa.kfmmg.KFMMGGenerator.OutputImageTooLargeException;
import com.mysql.jdbc.Buffer;

/**
 * Servlet implementation class KFMMGServer
 */
@WebServlet("/KFMMGServer")
public class KFMMGServer extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static final long startupTime = System.currentTimeMillis();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public KFMMGServer() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		boolean imageTooLarge = false;
		BufferedImage meme = null;
		try{
			boolean logoStyle = false;
			Float fixedRatio = null;
			try{
				logoStyle = Boolean.parseBoolean(request.getParameter("logoStyle"));
			}catch( Exception e){}
			try{
				fixedRatio = Float.parseFloat(request.getParameter("fixedRatio"));
			}catch( Exception e){}
			meme = new KFMMGGenerator(Integer.parseInt(request.getParameter("seed"))).generateMeme(
				request.getParameter("text"),
				Integer.parseInt(request.getParameter("fontSize")),
				Boolean.parseBoolean(request.getParameter("whiteBg")),
				Boolean.parseBoolean(request.getParameter("shuffle")),
				logoStyle, fixedRatio);
		}catch(KFMMGGenerator.OutputImageTooLargeException e){
			imageTooLarge = true;
		}catch(Exception e){			
		}
		
		
		try{
			int upScaleWidth = Integer.parseInt(request.getParameter("upScaleWidth"));
			int upScaleHeight = Integer.parseInt(request.getParameter("upScaleHeight"));
			
			meme = upScaleMeme(meme, upScaleWidth, upScaleHeight, Boolean.parseBoolean(request.getParameter("whiteBg")));
		}catch( KFMMGGenerator.OutputImageTooLargeException e ){
			imageTooLarge = true;
		}catch( Exception e ){}
		
		if( imageTooLarge ){
			meme =  new KFMMGGenerator(0).tooLargeImage();
		}
		
		response.setHeader("Cache-Control", "public");
		if( meme != null ){
			response.setContentType("image/png");
			response.setHeader("Access-Control-Allow-Origin", "*");
			if( imageTooLarge ){
				response.setHeader("KFMMG-Result", "RESULT_IMAGE_TOO_LARGE_OR_TEXT_TOO_LONG");
				response.setHeader("KFMMG-Message", "Result Image should smaller than 1024x1024px and max length of input text is 100 characters.");
			}else{
				response.setHeader("KFMMG-Result", "OK");
			}
			ImageIO.write(meme, "PNG", response.getOutputStream());
		}else{
			response.setHeader("KFMMG-Result", "NO_IMAGE_GENERATED");
			response.setHeader("KFMMG-Message", "Missing required parameters or parameter format incorrect.");
			response.sendRedirect("");			
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	@Override
	protected long getLastModified(HttpServletRequest req) {
		return startupTime;
	}
	
	private BufferedImage upScaleMeme( BufferedImage meme, int upScaleWidth, int upScaleHeight, boolean whiteBg ) throws OutputImageTooLargeException{
		if( upScaleWidth*upScaleHeight<1024*1024 ){
			float scale = Math.min(upScaleWidth/(float)meme.getWidth(), upScaleHeight/(float)meme.getHeight());
			float scaledWidth = meme.getWidth()*scale;
			float scaledHeight = meme.getHeight()*scale;
			
			BufferedImage scaledMeme = new BufferedImage(upScaleWidth, upScaleHeight, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = scaledMeme.createGraphics();
			if(whiteBg){
				g.setColor(new Color(255, 255, 255, 255));
			}else{
				g.setColor(new Color(0, 0, 0, 0));
			}
			g.fillRect(0, 0, upScaleWidth, upScaleHeight);
			
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(meme,
					(int)(upScaleWidth-scaledWidth)/2, (int)(upScaleHeight-scaledHeight)/2, (int)((upScaleWidth-scaledWidth)/2+scaledWidth), (int)((upScaleHeight-scaledHeight)/2+scaledHeight),
					0, 0, meme.getWidth(), meme.getHeight(), null);
			g.dispose();
			return scaledMeme;
		}else{
			throw new KFMMGGenerator.OutputImageTooLargeException();
		}
	}
}
