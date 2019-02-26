package UI;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Main.Card;

public class PlayedCardImage extends Drawable {

	//The image for this card
	Image cardImage;
	
	//For when there isn't a card played
	boolean shouldDraw = false;
	
	public PlayedCardImage(int x, int y, int w, int h) {
		super(x, y, w, h);
	}
	
	//Changes the card's image to show this card
	public void changeCard( Card card ) {
		
		try {
			cardImage = ImageIO.read( new File( ClickableCard.getAddress( card ) ) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	//Overwritten draw
	public void draw( Graphics g ) {
		
		//Don't draw if we shouldn't
		if( !shouldDraw ) {
			return;
		}
		
		g.drawImage( cardImage, x, y, w, h, null );
	}

	
	
}
