package GUI;

import java.awt.BorderLayout;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import game.Game;
import game.Player;

public class InfoWindow {
	private JFrame infoWindow;
	private JTable guessTable;
	private JTable pointTable;
	
	public InfoWindow() {
		infoWindow = new JFrame();
		infoWindow.setVisible(true);
		infoWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
	}
	
	public void displayGuessTable(Game game){
		String[] columnHeader = new String[Game.NUM_PLAYERS];
		LinkedList<Player> players = game.getPlayers();
		for (int i = 0; i < players.size(); i++) {
			columnHeader[i] = players.get(i).getName();
		}
		
		TableModel model = new DefaultTableModel(game.getGuessInfo(), columnHeader);
		guessTable = new JTable(model);
		
		infoWindow.add(guessTable, BorderLayout.CENTER);
	}
	
	public void displayPointTable() {
		
	}

}
