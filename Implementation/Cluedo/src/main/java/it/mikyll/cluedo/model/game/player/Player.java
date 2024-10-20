package it.mikyll.cluedo.model.game.player;

import it.mikyll.cluedo.model.game.board.Position;
import it.mikyll.cluedo.model.game.clues.Characters;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.networking.User;

import java.util.Set;

/*
 * An User becomes a Player when the game starts.
 */
public abstract class Player extends User {
	
	private int turn = -1;
	private Characters character = null;
	private Position position;
	private Set<Clue> clues;
	private String notebook = "";

	public Player(User user) {
		super(user.getUsername());
	}
	public Player(String username) {
		super(username);
	}
	
	public void setTurn(int turn) {this.turn = turn;}
	public int getTurn() {return turn;}
	public void setCharacter(Characters character) {this.character = character;}
	public Characters getCharacter() {return character;}
	public Position getPosition() {return position;}
	public void setPosition(Position position) {this.position = position;}

	public boolean wasMoved() {
		return false;
	}
}
