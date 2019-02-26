package Main;
import java.io.IOException;

import java.util.ArrayList;

import UI.Clickable;
import UI.Drawable;
import UI.GameLauncher;

public class Driver {

	public static final int PORT = 4544;
	
	public static GameLauncher startGame;
	
	public static void main(String[] args) throws IOException {
		
	startGame = new GameLauncher();
	
	}

}
