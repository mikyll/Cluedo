package model.game;

import model.game.clues.Character;

public class PlayerHuman implements Player {
	
	private String nickname;
	private int turn;
	private Character character;
	
	public PlayerHuman(String nickname) {
		this.nickname = nickname;
		this.turn = -1;
		this.character = Character.NONE;
	}
	
	public String getNickname() {return nickname;}
	public void setTurn(int turn) {this.turn = turn;}
	public int getTurn() {return turn;}
	public void setCharacter(Character character) {this.character = character;}
	public Character getCharacter() {return character;}
	
	@Override
	public boolean wasMoved() {
		// TODO Auto-generated method stub
		return false;
	}

}
