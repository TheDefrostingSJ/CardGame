package GameLogic;

import Main.Card;

public interface GUIEvents {
	
	public void hostGame();

	public void joinGame(String ip);
	
	public void playCard(Card card);
	
	public void startRound();
	
	public void startGame();
}
