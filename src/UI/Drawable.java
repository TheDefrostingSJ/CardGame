package UI;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.Driver;

public class Drawable {

	String imageURL;
	
	int x;
	int y;
	
	int w;
	int h;
	
	Image image;
	
	public boolean shouldDraw = true;
	
	public Drawable( int x, int y, int w, int h, String imageURL ) {
		this.x = x;
		this.y = y;
		
		this.w = w;
		this.h = h;
		
		this.imageURL = imageURL;
		try {
			image = ImageIO.read( new File( imageURL ) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		//Register us as drawable
		GuiPanel.drawables.add( this );
		
	}
	
	//Just in case we don't want to give it an image
	public Drawable( int x, int y, int w, int h ) {
		this.x = x;
		this.y = y;
		
		this.w = w;
		this.h = h;
		
		//Register us as drawable
		GuiPanel.drawables.add( this );
		
	}
	
	//Draws the image at the X and Y
	public void draw( Graphics g ) {
		
		//Don't draw if we're disabled
		if( !shouldDraw ){
			return;
		}
		
		g.drawImage( image, x, y, w, h, null );
	}
	
}
