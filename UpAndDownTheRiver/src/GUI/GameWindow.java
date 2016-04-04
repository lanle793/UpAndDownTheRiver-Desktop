package GUI;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cardDeck.Card;
import game.Player;

public class GameWindow {
	private JFrame gameWindow;
	private JPanel cardDisplay;
	
	public GameWindow(){
		gameWindow = new JFrame("New Game");
		cardDisplay = new JPanel();
		gameWindow.setVisible(true);
		gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameWindow.setPreferredSize(new Dimension(400,300));
		gameWindow.add(cardDisplay);
		gameWindow.pack();
		
	}
	
	public void displayUserHand(Player player){
		for(Card c : player.getCardsOnHand()){
			JButton card = new JButton(c.getIcon());
			card.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					player.setCardOnTable(c);
					cardDisplay.remove(card);
					cardDisplay.revalidate();
					cardDisplay.repaint();
					player.getCardsOnHand().remove(c);
					
				}
				
			});
			cardDisplay.add(card);
		}
		
	}

}
