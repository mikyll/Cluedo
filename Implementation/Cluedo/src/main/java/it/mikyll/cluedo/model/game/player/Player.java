package it.mikyll.cluedo.model.game.player;

import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.networking.User;

import java.util.ArrayList;
import java.util.List;

/*
 * A User becomes a Player when the game starts.
 */
public abstract class Player extends User {
	
	private int turn = -1;
	private Character character = null;
	private int[] position;
	private List<Clue> clues;
	private String notebook = "";
	// True after a wrong final accusation: the player can't play anymore, but still answers clue questions
	private boolean eliminated = false;
	// True if the pawn was moved into a room by someone else's clue question, since the player's last turn
	private boolean moved = false;

	public Player(User user) {
		super(user.getUsername());
		this.clues = new ArrayList<>();
	}
	public Player(String username) {
		super(username);
		this.clues = new ArrayList<>();
	}

	public List<Clue> getClues() {return clues;}
	public void setClues(List<Clue> clues) {this.clues = clues;}
	public String getNotebook() {return notebook;}
	public void setNotebook(String notebook) {this.notebook = notebook;}
	public int getTurn() {return turn;}
	public void setTurn(int turn) {this.turn = turn;}
	public Character getCharacter() {return character;}
	public void setCharacter(Character character) {this.character = character;}
	public int[] getPosition() {return position;}
	public void setPosition(int[] position) {this.position = position;}

	public boolean isEliminated() {return eliminated;}
	public void setEliminated(boolean eliminated) {this.eliminated = eliminated;}
	public void setMoved(boolean moved) {this.moved = moved;}

	public boolean wasMoved() {
		return moved;
	}
}
