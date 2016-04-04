package game;

import java.util.LinkedList;

import javax.swing.JOptionPane;

import GUI.GameWindow;
import cardDeck.Card;
import cardDeck.CardDeck;

public class Game {
	public static final int NUM_PLAYERS = 5;
	public static final int NUM_ROUNDS = 10;	//round # that has the max number of cards
	
	private CardDeck cardDeck;
	//private int numPlayers;
	//private int numRounds;
	private LinkedList<Player> players;
	private Card trump;
	private int totalGuess;
	private String invalidGuess;
	private GameWindow gameWindow;
	private int userNum;
	
	public Game(){
		
		totalGuess = 0;
		invalidGuess = "";
		trump = null;
		userNum = 0;
		
		enterPlayers();
		
		/*for(int i = 1; i <= NUM_ROUNDS; i++){
			startNewRound(i, i);
		}
		
		for(int i = 1; i < NUM_ROUNDS; i++){
			startNewRound(10 - i, 10 + i);
		}*/
		
		gameWindow = new GameWindow();
		
		startNewRound(5, 5);
		
		players.getLast().showCardsOnHand();

	}
	
	private void enterPlayers(){
		players = new LinkedList<Player>();
		String userName = JOptionPane.showInputDialog(null, "Enter your name", 
				"Player Information", JOptionPane.QUESTION_MESSAGE);
		Player user = new Player(userName, true);
		players.add(user);
		for(int i = 1; i < NUM_PLAYERS; i++){
			String computerName = JOptionPane.showInputDialog(null, "Enter your opponent name (Player " + i + ")", 
					"Player Information", JOptionPane.QUESTION_MESSAGE);
			Player computer = new Player(computerName, false);
			players.add(computer);
		}
		userNum = players.size();
	}
	
	private void startNewRound(int numTricks, int roundNum){
		
		//create a card deck and shuffle it
		cardDeck = new CardDeck();
		cardDeck.shuffle(30);
		
		//deal card(s) to each player
		for(int i = 0; i < players.size(); i++){
			for(int j = 0; j < numTricks; j++){
				players.get(i).getCardsOnHand().add(cardDeck.deal());
			}
		}
		
		//display user's hand
		gameWindow.displayUserHand(players.get(userNum - 1));
		
		/*//determine the trump card
		trump = cardDeck.deal();
		
		//get players' guesses
		getGuesses(numTricks, roundNum);
		
		//start laying down cards
		*/
		
		userNum++;
		if(userNum > players.size()){
			userNum -= players.size();
		}
	}
	
	private boolean isValidGuess(int guess, int totalGuess, int numTricks, Player player){
		if(guess > totalGuess){
			invalidGuess = "Guess is larger than the number of tricks";
			return false;
		}
		
		if(player.isDealer()){
			if(guess + totalGuess == numTricks){
				invalidGuess = "The total guess cannot be equal to the number of tricks";
				return false;
			}
		}
		
		return true;
	}
	
	private void getGuesses(int numTricks, int roundNum){
		
		//get players' guesses
		for(int i = 0; i < players.size(); i++){
			int guess;
			
			//get user's guess
			if(players.get(i).isHuman()){
				
				guess = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter your guess for round " + roundNum
						+ ")", "Player Guess", JOptionPane.QUESTION_MESSAGE));
				
				while(!isValidGuess(guess, totalGuess, numTricks, players.get(i))){
					JOptionPane.showMessageDialog(null, "Invalid Guess - " + invalidGuess);
					guess = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter your guess for round " + roundNum  
							+ ")", "Player Guess", JOptionPane.QUESTION_MESSAGE));
				}
				
				players.get(i).setGuess(guess);
				totalGuess += guess;
				
			} else{
				//get computer's guess
				guess = (int) (Math.random() * (numTricks + 1));
				
				while(!isValidGuess(guess, totalGuess, numTricks, players.get(i))){
					guess = (int) (Math.random() * (numTricks + 1));
				}
				
				players.get(i).setGuess(guess);
				totalGuess += guess;
			}
			
		}
	}

}
