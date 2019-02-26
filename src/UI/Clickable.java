package UI;

import Main.Driver;

public class Clickable extends Drawable {
	
	int w, h;
	
	public boolean isClickable = true;
	
	public Clickable(int x, int y, int w, int h, String imageURL) {
		super(x, y, w, h, imageURL);
		
		this.w = w;
		this.h = h;
		
		GuiPanel.clickables.add( this );
		
	}
	
	//Returns if a point is within this object.  Used for checking clicks
	public boolean pointWithin( int pointX, int pointY ) {
		
		//Check for X
		if( pointX >= x && pointX <= (x + w) ) {
			//Check for Y
			if( pointY >= y && pointY <= (y + h) ) {
				return true;
				
				
			}
		}
		
		return false;
	}
	
	//Called when this item is selected by the mouse going down and coming up on it.  Should be overridden
	public void onClicked() {}
	
	//Called when the mouse is pressed over this button.  Should be overridden
	public void onMouseDown() {}
	
}
