package UI;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import UI.GuiPanel;

public class ClickableButton extends Clickable{

	//When we mouse down on a button, we store that button here so we can reset it when we mouse up
	public static ClickableButton heldButton;
	
	public boolean locked = false;
	
	//Image to switch to when this button is held down
	Image heldImage;
	
	//Image to change to when this button is at rest
	Image baseImage;
	
	//Image to change to when this button is locked
	Image lockedImage;
	
	public ClickableButton(int x, int y, int w, int h, String imageURL, String heldImageURL, String lockedImageURL ) {
		super(x, y, w, h, imageURL);
		
		//Load and cache all the images
		try {
			this.heldImage = ImageIO.read( new File( heldImageURL ) );
			this.baseImage = ImageIO.read( new File( imageURL ) );
			this.lockedImage = ImageIO.read( new File( lockedImageURL ) );
		} catch (IOException e) {
			e.printStackTrace();
		}
		//Adds the button to all the relevant ArrayLists
		GuiPanel.clickables.add( this );
		GuiPanel.drawables.add( this );
		GuiPanel.buttons.add( this );
		
	}
	
	//Locks this button and changes it's image
	public void lock() {
		
		//Lock the button
		locked = true;
		
	}
	
	//Unlocks this button
	public void unlock() {
		
		//Unlock the button
		locked = false;
		
	}
	
	//Resets the held button to its normal state
	public static void onMouseUp() {
		
		//If there's a held button, reset it
		heldButton = null;
		
	}
	
	//Should be overridden for actual button functionality
	public void onClicked() {}
	
	//When the mouse goes down here, toggle to our held image
	public void onMouseDown() {
		
		//If we're locked, then we can't be clicked
		if( locked ) {
			return;
		}
		
		//Set ourselves as the held button
		heldButton = this;
		
	}
	
	//Buttons need their own draw method for their various states
	public void draw( Graphics g ) {
		
		Image img = baseImage;
		
		//If we're held down
		if( ClickableButton.heldButton == this ){
			//Draw the held down button
			img = heldImage;
		}
		
		//If we're locked
		if( locked ){
			//Draw the held down button
			img = lockedImage;
		}
		
		g.drawImage( img, x, y, w, h, null );
		
	}

}
