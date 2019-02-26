package GameLogic;
import java.util.ArrayList;
import java.util.Collections;

import Client.ClientEvents;
import Client.ClientThread;
import Main.Card;
import Server.ServerEvents;
import Server.ServerThread;
import UI.GUIInterface;
import UI.GUIStartInterface;
import UI.GameLauncher;

public class MainControl implements ClientEvents, ServerEvents, GUIEvents {
	
	//Variable for the ID of this player
	public int playerId = 0;

	//List of all players hands to check for legal moves
	private ArrayList<ArrayList<Card>> playersHands = new ArrayList<ArrayList<Card>>();
	//Contain the cards played in the current round
	private Card[] playedCards = new Card[3];
	//The scores of the players
	private int[] playerScores = new int[3];
	//Running total of tie breaker score in case it is needed
	private int[] tieBreakerScores = new int[3];
	//The hand for this player
	private ArrayList<Card> myCards = new ArrayList<Card>();

	//Is used if joining a game
	private ClientThread clientThread;
	//Is used if hosting a game
	private ServerThread serverThread;
	//Reference to main interface
	private GUIInterface guiInterface;
	//Reference to starting interface
	private GUIStartInterface guiStartInterface;
	
	//Number of cards played in the current round
	private int numberOfCardsPlayed = 0;
	//ID of the current players turn
	private int currentPlayerTurn = 1;
	//ID of the player that leads the round
	private int leadingPlayer = 1;
	//ID of the player that won the previous round
	private int previousWinner = 1;

	public MainControl( GUIInterface guiInterface, GUIStartInterface guiStartInterface ) {
		
		//Update the GUIInterface to be a real one
		this.guiInterface = guiInterface;
		
		//Update guiStartInterface to be a real one
		this.guiStartInterface = guiStartInterface;
		
		for (int i = 0; i < 3; i++) {
			playersHands.add(new ArrayList<Card>());
		}
	}

	//Client methods

	//Called by GUI
	public void hostGame() {
		serverThread = new ServerThread( this );
		playerId = 1;
	}
	
	public void joinGame(String ip) {
		clientThread = new ClientThread( this, ip);
	}
	

	//Called by the GUI when playing a card
	public void playCard(Card card) {
		myCards.remove(card);
		
		if (playerId == 1) {
			serverThread.broadcastCardPlayed(1, card);
			cardPlayed(1, card);
		} else {
			clientThread.playCard(card);
		}
	}
	
	//Called by the GUI when starting a round
	public void startRound() {
		if (playerId == 1) {
			serverThread.broadcastRoundStart(1);
			serverRoundStart(1);
		} else {
			clientThread.startRound();
		}
	}

	//Called by the GUI when starting a game
	public void startGame() {
		
		//If statement for host or client
		if (playerId == 1) {
			gameSetup(1);
		} else {
			clientThread.startGame();
		}
	}
	
	//Ran at the start of a game to create a deck and deal the cards
	private void dealCards() {
		ArrayList<Card> deck = new ArrayList<Card>();
		
		for (int i = 0; i < 4; i++) {
			for (int j = 2; j < 15; j++) {
				deck.add(new Card(j, i));
			}
		}
		Collections.shuffle(deck);
		//17
		for (int i = 0; i < 17; i++) {
			for (int j = 1; j < 4; j++) {
				Card nextCard = deck.get(0);
				deck.remove(0);
				playersHands.get(j - 1).add(nextCard);
				if (j == 1) {
					myCards.add(nextCard);
				} else {
					serverThread.sendCardDealt(j, nextCard);
				}
			}
		}
		
		guiInterface.startingHand( myCards );
	}
	

	//Called by Network

	//Network method called when client tries to play a card
	public void cardPlayedOnServer(int player, Card card) {
		Card hasCard = playerHasCard(playersHands.get(player - 1), card);
		if (hasCard != null) {
			serverThread.broadcastCardPlayed(player, card);
			playersHands.get(player - 1).remove(hasCard);
			cardPlayed(player, hasCard);
		}
	}
		
	//Network method called when host starts a game
	public void gameStartedOnClient(int startedByID) {
		playerId = clientThread.playerID;
		guiInterface.gameStarted();
		int winner = gameWinner();
		resetGame();
		if (winner == playerId) {
			guiInterface.playableCards(playableCards());
		}
	}

	//Network method called when a card has been played
	public void cardDealtOnClient(Card card) {
		myCards.add(card);
		if (myCards.size() == 17) {
			guiInterface.startingHand(myCards);
		}
	}
	
	//Method for verifying player has the card they tried to play
	private Card playerHasCard(ArrayList<Card> playerCards, Card card) {
		for (Card pCard : playerCards) {
			if (pCard.getSuit() == card.getSuit() && pCard.getValue() == card.getValue()) {
				return pCard;
			}
		}
		return null;
	}


	public void cardPlayedOnClient(int player, Card card) {
		cardPlayed(player, card);
	}
	
	public void playerConnectedOnServer(int playerID) {
		guiStartInterface.playerConnected(playerID);
	}

	public void roundStartedOnServer(int startedByID) {
		if (previousWinner == startedByID) {
			serverRoundStart(startedByID);
		}
	}
	
	private void serverRoundStart(int startedByID) {
		serverThread.broadcastRoundStart(startedByID);
		resetRound();
		guiInterface.roundStarted();
		if (previousWinner == playerId) {
			guiInterface.playableCards(playableCards());
		}
		
	}

	public void gameStartedOnServer(int startedByID) {
		gameSetup(startedByID);
		
	}
	
	private void gameSetup(int startedByID) {
		serverThread.broadcastGameStart(startedByID);
		
		resetGame();
		dealCards();
		guiInterface.gameStarted();
		guiInterface.playableCards( myCards );
	}

	public void roundStartedOnClient(int startedByID) {
		resetRound();
		guiInterface.roundStarted();
		if (previousWinner == playerId) {
			guiInterface.playableCards(playableCards());
		}
	}
	
	public void connectedToServerOnClient() {
		guiStartInterface.connectedToServer(clientThread.playerID);
	}

	public void error(String error) {
		guiInterface.error(error);
	}
	
	private void cardPlayed(int player, Card card) {		
		guiInterface.cardPlayed(player, card);
		
		playedCards[player - 1] = card;
		numberOfCardsPlayed++;
		currentPlayerTurn = nextPlayerId();

		if (numberOfCardsPlayed == 3) {
			//Runs at the end of a round
			int winnerId = findRoundWinner();
			guiInterface.roundWinner(winnerId);
			calculateScoring(winnerId);
			guiInterface.updateScores(playerScores);
			currentPlayerTurn = winnerId;
			if (myCards.size() == 0) {
				guiInterface.gameWinner(gameWinner());
			}
		} else if (currentPlayerTurn == playerId) {
			//Runs if it is this players turn
			guiInterface.playableCards(playableCards());
		}
	}

	private int findRoundWinner() {
		int winner = leadingPlayer;
		if (playedCards[leadingPlayer - 1].getSuit() == playedCards[nextPlayerId() - 1].getSuit() &&
				playedCards[leadingPlayer - 1].getValue() < playedCards[nextPlayerId() - 1].getValue()) {
			winner = nextPlayerId();
		}
		if (playedCards[winner - 1].getSuit() == playedCards[nextNextPlayerId() - 1].getSuit() &&
				playedCards[winner - 1].getValue() < playedCards[nextNextPlayerId() - 1].getValue()) {
			winner = nextNextPlayerId();
		}
		previousWinner = winner;
		return winner;
	}

	private int gameWinner() {
		int winner = 1;
		if (playerScores[0] < playerScores[1]) {
			winner = 2;
		} else if (playerScores[0] == playerScores[1] && tieBreakerScores[0] < tieBreakerScores[1]) {
			winner = 2;
		}
		if (playerScores[winner - 1] < playerScores[2]) {
			winner = 3;
		} else if (playerScores[winner - 1] == playerScores[2] && tieBreakerScores[winner - 1] < tieBreakerScores[2]) {
			winner = 3;
		}
		return winner;
	}

	//Returns playable cards by checking leading card if necessary
	private ArrayList<Card> playableCards() {
		ArrayList<Card> cards = new ArrayList<>();
		if (previousWinner == playerId) {
			return myCards;
		} else {
			for (Card card:myCards) {
				if (card.getSuit() == playedCards[previousWinner - 1].getSuit()) {
					cards.add(card);
				}
			}
			if (cards.isEmpty()) {
				return myCards;
			}
			return cards;
		}
	}

	private void calculateScoring(int winnerId) {
		playerScores[winnerId - 1]++;
		for (int i = 0; i < 3; i++) {
			tieBreakerScores[i] += playedCards[i].getValue();
		}
	}

	private void resetRound() {
		leadingPlayer = previousWinner;
	    numberOfCardsPlayed = 0;
	    playedCards = new Card[3];
    }

    private void resetGame() {
		numberOfCardsPlayed = 0;
		leadingPlayer = 1;
		playedCards = new Card[3];
		playerScores = new int[3];
		tieBreakerScores = new int[3];
		currentPlayerTurn = 1;
		previousWinner = 1;
    }

	private int nextPlayerId() {
		return (currentPlayerTurn % 3) + 1;
	}

	private int nextNextPlayerId() {
		return ((currentPlayerTurn + 1) % 3) + 1;
	}
	
	//Returns all the players scores
		public int[] getScores() {
			return playerScores;
		}
}
