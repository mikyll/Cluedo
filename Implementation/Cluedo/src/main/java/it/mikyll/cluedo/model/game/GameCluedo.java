package it.mikyll.cluedo.model.game;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerArtificial;

/*
 * Game class
 */
public class GameCluedo {
	private List<Player> players;

	private MurderEnvelope murderEnvelope;
	// Timer
	// User list
	// Settings
	
	// characters
	// weapons
	// places/rooms
	
	// current turn
	
	// envelope (solution)

	/*
	 * Constructor. It takes a list of users and the game settings
	 */
	public GameCluedo(List<Player> players, int playerNumber) {
		this.players = players;
		
		// Add artificial players if there aren't enough players
		for(int i = players.size(); i < playerNumber; i++) {
			Player p = new PlayerArtificial("AI " + i);
			this.players.add(p);
		}
	}
	
	public void startGame() {
		// Preparation phase
		// shuffle players list multiple times for N seconds, each time sleeping M seconds (and update GUI, so it's nice to see them shuffled) 
		
		// Game start
	}
	
	// everybody lost
	public void endGame() {
		
	}
	
	// player wins
	public void endGame(Player winner) {
		
	}
	
	public void preparationPhase()
	{
		// Shuffle players list
		Collections.shuffle(this.players);

		for (int i = 0; i < this.players.size(); i++)
		{
			this.players.get(i).setTurn(i+1);
		}

		// roll dice
		
		// choose character
			// + timer
		
		// roll initial cards
	}
	
	public void setCharacter(Player p, Character character) {
		
	}
	
	// get random number (2-12)
	public int rollDice() {
		return -1;
	}
	
	
	
	public Clue askClue(Player p, Character who, Weapon what, Room where) {
		// ask the first player after p (by turns) if he has one of the clues.
		
		// Example:
		// Does p+1 have one of the clues? No -> "Player p+1 cannot answer"
		// Does p+2 have one of the clues? Yes -> shows him the clue (just one). "Player p+2 reveals a clue"
		
		// If no one has none of the clues you asked, then you know the answer and must wait the next turn
		
		return null;
	}
	
	public boolean accuse(Player p, Character who, Weapon what, Room where) {
		// correct? The game ends, and p wins;
		
		// wrong? The game continues, and p is eliminated
		return false;
	}
}
