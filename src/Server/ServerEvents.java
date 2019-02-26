package Server;

import Main.Card;

//Events that occur on the server
public interface ServerEvents {
	
	//Called after a player has connected fully and is ready to send and receive 
	void playerConnectedOnServer( int playerID );
	
	//Called when a tries player plays a card
	void cardPlayedOnServer( int playedByID, Card card );
	
	//Called when a player tries to start a new round
	void roundStartedOnServer( int startedByID );
	
	//Called when a player tries to start a new game
	void gameStartedOnServer( int startedByID );
	
	//Called whenever there's an error and the game needs to be reset
	void error(String error);
}
