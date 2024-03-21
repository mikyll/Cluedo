package it.mikyll.cluedo.model.game.player;

import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.networking.User;

/*
 * An User becomes a Player when the game starts.
 */
public abstract class Player extends User {
	
	private int turn;
	private Character character;
	
	public Player(String username) {
		super(username);
	}
	
	public void setTurn(int turn) {this.turn = turn;}
	public int getTurn() {return turn;}
	public void setCharacter(Character character) {this.character = character;}
	public Character getCharacter() {return character;}

	public boolean wasMoved() {
		return false;
	}
}
