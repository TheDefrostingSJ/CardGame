package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import Main.Card;
import UI.GameLauncher;

public class ServerToClientThread extends Thread {

	
	//The socket to the client
	private Socket socket;
	
	//Indicates which player this thread is for
	public int playerID;

	//Sends data to the client
	PrintWriter out;

	//Gets data from the client
	BufferedReader in;
	
	//Where the server events are
	private ServerEvents events;

	//Constructor
	public ServerToClientThread( ServerEvents serverEvents, Socket clientSocket, int playerID ){

		//Set socket to the socket that is already connected to this thread's client
		socket = clientSocket;
		
		//Set this player's player number
		this.playerID = playerID;
		
		//Set our server events object
		this.events = serverEvents;

	}

	//Main thread code
	public void run(){

		//Try to set up the in and out streams to and from the client
		try {
			out = new PrintWriter( socket.getOutputStream(), true);
			in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			
			writeLine( playerID );
			
		} catch (IOException e) {

			//If we couldn't establish a proper connection to the client, we can't move forward
			error( "Could not open streams to player " + playerID );
			
			return;
		}
		
		//Call the player connection event
		events.playerConnectedOnServer( playerID );

		//Begin listening for network data
		while( true ){
			
			String line = "nothing";
			
			try {
				
				//Read in the next line
				line = in.readLine();
			
				//Check for a played card
				if( line.contains( "cardplayed" ) ) {
					System.out.println("See if played card test");
					//Read in the value
					String stringValue = in.readLine();
					int value = Integer.parseInt( stringValue );
					
					//Read in the suit
					String stringSuit = in.readLine();
					int suit = Integer.parseInt( stringSuit );
					
					//Make a card out of that information
					Card card = new Card( value, suit );
					
					//Call the card played event
					events.cardPlayedOnServer( playerID, card );
					
					continue;
				}
				
				//Check for round start
				if( line.contains( "roundstart" ) ) {
					
					//Call the round started event
					events.roundStartedOnServer( playerID );
					
					continue;
				}
				
				//Check for game start
				if( line.contains( "gamestart" ) ) {
					
					//Call the round started event
					events.gameStartedOnServer( playerID );
					
					continue;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				System.exit( 1 );
				
				return;
			}
			
			System.out.println( "Networking heard '" + line + "' and did not understand it" );
			
		}
		
	}
	
	//Sends a line to the client
	private void writeLine( String str ){
		out.write( str + "\n" );
		out.flush();
	}
	
	//Writes an integer as a string for convenience sake
	private void writeLine( int num ){
		writeLine( Integer.toString( num ) );
	}

	//Sends a dealt card to this player's hand
	public void sendDealtCard( Card card ){
		//StartGame.print( "Dealing " + card + " to player " + playerID );
		writeLine( "carddealt" );
		writeLine( card.getValue() );
		writeLine( card.getSuit() );
	}
	
	//Sends a card played by another player to this player
	public void sendPlayedCard( int playedByID, Card card ){
		
		writeLine( "cardplayed" );
		writeLine( playedByID );
		writeLine( card.getValue() );
		writeLine( card.getSuit() );
		
	}
	
	//Sends the player a notice that the game has started
	public void sendGameStart( int startedByID ){
		
		System.out.println( "Sending game start to " + playerID );
		
		writeLine( "gamestart" );
		writeLine( startedByID );
	}
	
	//Sends the player a notice that the round has started
	public void sendRoundStart( int startedByID ){
		
		writeLine( "roundstart" );
		writeLine( startedByID );
	}
	
	//Logs the error and calls the error event
	public void error( String error ) {
		
		//Call the event
		events.error( error );
	}
	
}
