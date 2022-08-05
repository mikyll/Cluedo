package model.game;

import model.game.clues.Character;

public interface Player {
	
	public String getNickname();
	public void setTurn(int turn);
	public int getTurn();
	public void setCharacter(Character character);
	public Character getCharacter();
	
	public boolean wasMoved(); // returns true if the player was moved by another player since his last turn

}
