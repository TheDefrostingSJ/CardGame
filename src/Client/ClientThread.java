package Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import Main.Card;
import Main.Driver;
import UI.GameLauncher;

public class ClientThread extends Thread {
	
	//The server's IP address
	String serverAddress = "";
	
	//Where the client events are
	private ClientEvents events;
	
	//The server's port
	int port = Driver.PORT;
	
	//Socket connecting this client to the server
	private Socket socket;
	
	//Sends data to the server
	PrintWriter out;

	//Gets data from the server
	BufferedReader in;
	
	//Our player ID
	public int playerID;
	
	//Constructor
	public ClientThread( ClientEvents clientEvents, String serverAddress ) {
		this.serverAddress = serverAddress;
		
		//Start ourselves
		this.start();
		
		//Set our client events object
		this.events = clientEvents;
	}
	
	//Main thread code
	public void run(){
		
		//Try to connect to the server
		try {
			socket = new Socket( serverAddress, port );
			
		} catch (IOException e) {
			
			//If we can't connect, display an error and stop the thread
			error( "Could not connect to server at " + serverAddress + " on port " + port );
			
			return;
			
		}
		
		//Try to set up the input and output streams
		try {
			//Create the in and out streams
			in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
			out = new PrintWriter(socket.getOutputStream(), true);
			
			//Listen for our player number
			String playerIDString = in.readLine();
			playerID = Integer.parseInt( playerIDString );
			
		} catch (IOException e) {
			
			//If we can't create the streams, display an error and stop the thread
			error( "Could not create streams" );
			
			return;
		}
		
		//Call the client connection event
		events.connectedToServerOnClient();
		
		//Start listening for network input
		while( true ){
			String line;
			
			try {
				
				//Read the next line of network data
				line = in.readLine();
				
				//Check for the server dealing us cards
				if( line.contains( "carddealt" ) ){
					
					//StartGame.print( "Got card" );
					
					//Read in the next two lines, which will be ints for card value and card suit, in that order
					try {
						
						//Read and parse the value
						String cardValueString = in.readLine();
						int cardValue = Integer.parseInt( cardValueString );
						
						//Read and parse the suit
						String cardSuitString = in.readLine();
						int cardSuit = Integer.parseInt( cardSuitString );
						
						//Recreate the dealt card object
						Card dealtCard = new Card( cardValue, cardSuit );
						
						//Call the dealt card event
						events.cardDealtOnClient( dealtCard );
						
					} catch (IOException e) {
						error( "Encountered an error while reading dealt card values. Exiting..." );
						return;
					}
				}
				
				//Check for someone playing a card
				if( line.contains( "cardplayed" ) ){
					
					//Read in the next three lines, which will be ints for playedByID, card value and card suit, in that order
					try {
						
						//Read and parse the playedByID
						String playedByIDString = in.readLine();
						int playedByID = Integer.parseInt( playedByIDString );
						
						//Read and parse the value
						String cardValueString = in.readLine();
						int cardValue = Integer.parseInt( cardValueString );
						
						//Read and parse the suit
						String cardSuitString = in.readLine();
						int cardSuit = Integer.parseInt( cardSuitString );
						
						//Recreate the dealt card object
						Card card = new Card( cardValue, cardSuit );
						
						//Call card played event
						events.cardPlayedOnClient(playedByID, card);
						
					} catch (IOException e) {
						error( "Encountered an error while reading played card values. Exiting..." );
						return;
					}
				}
				
				//Check for round start
				if( line.contains( "roundstart" ) ){
					
					//Read in the next line to get the ID of who started the round
					try {
						
						//Read and parse the startedByID
						String startedByIDString = in.readLine();
						int startedByID = Integer.parseInt( startedByIDString );
						
						//Fire the event
						events.roundStartedOnClient( startedByID );
						
					} catch (IOException e) {
						error( "Encountered an error while reading round start. Exiting..." );
						return;
					}
				}
				
			} catch (IOException e) {
				error( "Encountered an error while reading. Exiting..." );
				
				System.exit( 1 );
				
				return;
			}
			
			//Check for game start
			if( line.contains( "gamestart" ) ){
				
				//Read in the next line to get the ID of who started the game
				try {
					
					//Read and parse the startedByID
					String startedByIDString = in.readLine();
					int startedByID = Integer.parseInt( startedByIDString );
					
					//Fire the event
					events.gameStartedOnClient( startedByID );
					
				} catch (IOException e) {
					error( "Encountered an error while reading game start. Exiting..." );
					return;
				}
			}
			
		}
		
	}
	//Sends a line to the server
	private void writeLine( String str ){
		out.write( str + "\n" );
		out.flush();
	}
	
	//Writes an integer as a string for convenience sake
	private void writeLine( int num ){
		writeLine( Integer.toString( num ) );
	}
	
	//Logs the error and calls the error event
	private void error( String error ) {
		
		//Call the event
		events.error( error );
	}
	
	//Tries to play a card
	public void playCard( Card card ) {
		System.out.println("Client played");
		writeLine( "cardplayed" );
		writeLine( card.getValue() );
		writeLine( card.getSuit() );
	}
	
	//Tries to start the round
	public void startRound() {
		
		writeLine( "roundstart" );
		
	}
	
	//Tries to start the game
	public void startGame() {
		
		writeLine( "gamestart" );
		
	}
	
}
