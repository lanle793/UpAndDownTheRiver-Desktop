package game;

import java.util.LinkedList;

import javax.swing.JOptionPane;

import GUI.GameWindow;
import cardDeck.Card;
import cardDeck.CardDeck;

public class Game {
	public static final int NUM_PLAYERS = 5;
	public static final int NUM_ROUNDS = 19;
	
	private CardDeck cardDeck;
	private int roundNum;
	private int initNumTricks;
	private LinkedList<Player> players;
	private Card trump;
	private Card first;
	private int totalGuess;
	private GameWindow gameWindow;
	private int userNum;
	private GameController controller;
	private int startIndex;
	private int endIndex;
	private String[][] guessInfo;
	private String[][] pointInfo;
	private int numTricks;
	private boolean downTheRiver;
	private boolean endOfRound;
	
	public Game(){
		
		totalGuess = 0;
		controller = new GameController();
		players = new LinkedList<Player>();
		guessInfo = new String[NUM_ROUNDS][NUM_PLAYERS];
		pointInfo = new String[NUM_ROUNDS][NUM_PLAYERS];
		downTheRiver = false;
		
		controller.enterPlayers(players, NUM_PLAYERS);
		gameWindow = new GameWindow();
		gameWindow.setPlayers(players);
		userNum = 1;
		initNumTricks = 1;
		roundNum = 1;
		
		startNewRound();

	}
	
	private void startNewRound(){
		endOfRound = false;
		
		if (roundNum > NUM_ROUNDS) {
			controller.findGameWinner(players);
			return;
		}
		
		//create a card deck and shuffle it
		cardDeck = new CardDeck();
		cardDeck.shuffle(30);
		
		//deal card(s) to each player
		controller.dealCards(initNumTricks, cardDeck, players);
		
		//determine the trump card
		trump = cardDeck.deal();
		System.out.println("\nTrump: " + trump);
		gameWindow.displayTrumpCard(trump);
		
		//display user's hand
		gameWindow.displayUserHand(players.get(userNum - 1));
		
		//get players' guesses
		totalGuess = controller.getGuesses(totalGuess, initNumTricks, roundNum, players);
		
		//start laying down cards
		JOptionPane.showMessageDialog(null, "Let's Start!");
		gameWindow.enableCardsOnHand();
		
		startIndex = 1;
		
		startNewTrick(initNumTricks);
		
		if (downTheRiver) {
			initNumTricks--;
		} else {
			initNumTricks++;
			if (initNumTricks == 5) {
				downTheRiver = true;
			}
		}
		
		roundNum++;
		
		gameWindow.displayNextRdBtn(this);
		
	}
	
	public void startNextRound() {
		startNewRound();
	}
	
	private void startNewTrick(int numTricks){
		this.numTricks = numTricks;
		
		if (this.numTricks > 0) {
			int firstIndex = startIndex - 1;
			if (firstIndex == -1) {
				firstIndex = 4;
			}
			layFirstCard(players.get(firstIndex));
		} else {
			JOptionPane.showMessageDialog(null, "End of round");
			
			endOfRound = true;
			trump = null;
			gameWindow.removeTrumpCard();
			
			//add points to players after each round
			for (Player p : players) {
				controller.addPoints(p);
			}
			
			//record guesses and points
			fillGuessInfo(roundNum);
			gameWindow.setGuessInfo(guessInfo);
			fillPointInfo(roundNum);
			gameWindow.setPointInfo(pointInfo);
			gameWindow.enableGameStats();
			
			//reset number of tricks won and cards on table
			controller.resetAfterRound(players);
			
			//the first player becomes the last player in line
			Player last = players.getFirst();
			players.removeFirst();
			players.addLast(last);
			
			userNum--;
			if (userNum == 0) {
				userNum = players.size();
			}
		}
			
	}
	
	public void continueAfterUserTurn(){
		//for the rest of the players...
		layFollowingCards(players);
		
		//find trick winner
		int winner = controller.findTrickWinner(players, first, trump);
		startIndex = winner + 1;
		if (startIndex == players.size()) {
			startIndex = 0;
		}
		
		//declare trick winner
		gameWindow.declareTrickWinner(players.get(winner));
		System.out.println("Trick winner: " + players.get(winner));
		
		//decrement number of tricks left
		numTricks--;
		
		//reset cards on table
		controller.resetAfterTrick(players);
		gameWindow.removeCardOnTable();
		
		startNewTrick(numTricks);
	}
	
	private void layFirstCard(Player player){
		endIndex = startIndex - 2;
		if (endIndex == -1) {
			endIndex = 4;
		} else if (endIndex == -2) {
			endIndex = 3;
		}
		
		System.out.println("\nFirst turn: " + player.getName());
		System.out.println(player.getName() + " has " + player.getCardsOnHand().size() + " left\n");
		
		//first turn player pick any card
		if(player.isHuman()){
			JOptionPane.showMessageDialog(null, "Your turn - Pick any card to lay down");
			
			//enable cards on hand
			gameWindow.enableCardsOnHand();
			
			//add end turn button
			gameWindow.displayEndTurnBtn(this, player, true);
			
		} else{
			player.getRandomCard();
			setFirst(player.getCardOnTable());
			System.out.println(player.getName() + " : " + player.getCardOnTable());
			gameWindow.displayCardOnTable(player);
			layFollowingCards(players);
		}

	}
	
	private void layFollowingCards(LinkedList<Player> players){
		boolean passUser = false;
		
		//if user goes the last turn
		if (startIndex == -1) {
			return;
		}
		
		if (startIndex == players.size()) {
			startIndex = 0;
		}
		
		JOptionPane.showMessageDialog(null, "Continue");
		
		if (startIndex > endIndex) {
			for(int j = startIndex; j < players.size(); j++){
				System.out.println("Index: " + j);
				controller.checkValidCards(players.get(j).getCardsOnHand(), first, trump);
				
				//users get to choose the card to lay down
				if(players.get(j).isHuman()){
					JOptionPane.showMessageDialog(null, "Your turn");
					passUser = true;
					
					//reset start index
					startIndex = j + 1;
					
					//disable invalid cards
					gameWindow.disableInvalidCards(players.get(j));
					
					//add end turn button
					gameWindow.displayEndTurnBtn(this, players.get(j), false);
					
					break;
					
				} else{
					
					//each computer player lays down one card
					JOptionPane.showMessageDialog(null, players.get(j).getName() + "'s turn");
					players.get(j).layCardOnTable();
					System.out.println(players.get(j).getName() + " : " + players.get(j).getCardOnTable());
					gameWindow.displayCardOnTable(players.get(j));
				}
				
			}
			
			for(int j = 0; j <= endIndex; j++){
				//if user's turn has already passed
				if (passUser) {
					break;
				}
				
				System.out.println("Index: " + j);
				controller.checkValidCards(players.get(j).getCardsOnHand(), first, trump);
				
				//users get to choose the card to lay down
				if(players.get(j).isHuman()){
					JOptionPane.showMessageDialog(null, "Your turn");
					
					//reset start index
					if (endIndex == j) {
						startIndex = -1;
					} else {
						startIndex = j + 1;
					}
					
					//disable invalid cards
					gameWindow.disableInvalidCards(players.get(j));
					
					//add end turn button
					gameWindow.displayEndTurnBtn(this, players.get(j), false);
					
					break;
					
				} else{
					
					//each computer player lays down one card
					JOptionPane.showMessageDialog(null, players.get(j).getName() + "'s turn");
					players.get(j).layCardOnTable();
					System.out.println(players.get(j).getName() + " : " + players.get(j).getCardOnTable());
					gameWindow.displayCardOnTable(players.get(j));
				}
				
			}
		
		} else {
			for(int j = startIndex; j <= endIndex; j++){
				System.out.println("Index: " + j);
				controller.checkValidCards(players.get(j).getCardsOnHand(), first, trump);
				
				//users get to choose the card to lay down
				if(players.get(j).isHuman()){
					JOptionPane.showMessageDialog(null, "Your turn");
					
					//reset start index
					if (endIndex == j) {
						startIndex = -1;
					} else {
						startIndex = j + 1;
					}
					
					//disable invalid cards
					gameWindow.disableInvalidCards(players.get(j));
					
					//add end turn button
					gameWindow.displayEndTurnBtn(this, players.get(j), false);
					
					break;
					
				} else {
					//each computer player lays down one card
					JOptionPane.showMessageDialog(null, players.get(j).getName() + "'s turn");
					players.get(j).layCardOnTable();
					System.out.println(players.get(j).getName() + " : " + players.get(j).getCardOnTable());
					gameWindow.displayCardOnTable(players.get(j));
				}
				
			}
		}
		
	}
	
	private void fillGuessInfo(int roundNum) {
		int index = 0;
		
		for (int i = userNum - 1; i < players.size(); i++) {
			guessInfo[roundNum - 1][index] = Integer.toString(players.get(i).getGuess());
			index++;
		}
		
		if (userNum - 1 != 0) {
			for (int i = 0; i < userNum - 1; i++) {
				guessInfo[roundNum - 1][index] = Integer.toString(players.get(i).getGuess());
				index++;
			}
		}
	}
	
	private void fillPointInfo(int roundNum) {
		int index = 0;
		
		for (int i = userNum - 1; i < players.size(); i++) {
			pointInfo[roundNum - 1][index] = Integer.toString(players.get(i).getNumPoints());
			index++;
		}
		
		if (userNum - 1 != 0) {
			for (int i = 0; i < userNum - 1; i++) {
				pointInfo[roundNum - 1][index] = Integer.toString(players.get(i).getNumPoints());
				index++;
			}
		}
	}

	public Card getFirst() {
		return first;
	}

	public void setFirst(Card first) {
		this.first = first;
		System.out.println("First card laid down: " + first);
	}

	public LinkedList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}

	public String[][] getGuessInfo() {
		return guessInfo;
	}

	public void setGuessInfo(String[][] guessInfo) {
		this.guessInfo = guessInfo;
	}

	public boolean isEndOfRound() {
		return endOfRound;
	}

	public void setEndOfRound(boolean endOfRound) {
		this.endOfRound = endOfRound;
	}

}
