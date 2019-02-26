package UI;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PlayerTurnIcon extends Drawable {

	//The image for when it's not our turn
	private Image defaultImage;
	
	//The image for when it's our turn
	private Image turnImage;
	
	//Whether or not it is our turn
	private boolean isTurn;
	
	public PlayerTurnIcon(int x, int y, int w, int h, String defaultImageURL, String turnImageURL ) {
		super( x, y, w, h );
		
		//Load and save the images
		try {
			defaultImage = ImageIO.read( new File( defaultImageURL ) );
			turnImage = ImageIO.read( new File( turnImageURL ) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
	}
	
	//Sets whether this icon is lit or not
	public void setIsTurn( boolean isTurn ) {
		
		this.isTurn = isTurn;
		
	}
	
	//Draw overwrite
	public void draw( Graphics g ) {
		
		Image img;
		
		//Get which image we should draw
		if( isTurn ) {
			img = turnImage;
		}else {
			img = defaultImage;
		}
		
		//Draw it
		g.drawImage( img, x, y, w, h, null );
		
	}
	
}
