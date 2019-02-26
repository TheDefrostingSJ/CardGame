
package Client;
//package Client;

import java.io.IOException;

import Main.Card;

//Events that occur on the client
public interface ClientEvents {
	
	//Called when we fully connect to the server
	void connectedToServerOnClient();
	
	//Called when another player plays a card
	void cardPlayedOnClient( int playedByID, Card card );
	
	//Called when the server deals you a card
	void cardDealtOnClient( Card card );	
	
	//Called when a player starts a new round
	void roundStartedOnClient( int startedByID );
	
	//Called when a player starts a new game
	void gameStartedOnClient( int startedByID ) throws IOException;
	
	//Called whenever there's an error and the game needs to be reset
	void error(String error);
	
}
