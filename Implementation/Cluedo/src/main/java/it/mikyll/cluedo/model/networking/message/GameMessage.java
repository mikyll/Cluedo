package it.mikyll.cluedo.model.networking.message;

import com.google.gson.Gson;

/*
 * Content of the GAME messages, sent as JSON. Players are identified by their index in the turn order,
 * clues by their index in GameCluedo.getTotalCluesList() (the same for everyone).
 * Only the fields needed by each type are set.
 */
public class GameMessage {
	private static final Gson GSON = new Gson();

	private GameMessageType type;
	// Player the message is about (who acts, chooses, leaves, wins...)
	private int player = -1;
	// Dices result
	private int value = -1;
	// Destination of a move
	private int[] position;
	// Clues of a question or accusation
	private int character = -1;
	private int weapon = -1;
	private int room = -1;
	// Clue questions: who must answer (-1 if nobody) and the shown clue (-1 if hidden to the receiver)
	private int answerer = -1;
	private int clue = -1;
	// Final accusation result
	private boolean correct;
	// Own clues (SETUP), revealed clues (PLAYER_LEFT), murder envelope (GAME_OVER)
	private int[] clues;
	// SETUP: index of the receiver, and the players in turn order
	private int you = -1;
	private String[] usernames;
	private int[][] positions;
	// ERROR
	private String text;

	public GameMessage(GameMessageType type) {
		this.type = type;
	}

	public static GameMessage fromJson(String json) {
		return GSON.fromJson(json, GameMessage.class);
	}

	public String toJson() {
		return GSON.toJson(this);
	}

	public GameMessageType getType() {return type;}
	public int getPlayer() {return player;}
	public GameMessage setPlayer(int player) {this.player = player; return this;}
	public int getValue() {return value;}
	public GameMessage setValue(int value) {this.value = value; return this;}
	public int[] getPosition() {return position;}
	public GameMessage setPosition(int[] position) {this.position = position; return this;}
	public int getCharacter() {return character;}
	public GameMessage setCharacter(int character) {this.character = character; return this;}
	public int getWeapon() {return weapon;}
	public GameMessage setWeapon(int weapon) {this.weapon = weapon; return this;}
	public int getRoom() {return room;}
	public GameMessage setRoom(int room) {this.room = room; return this;}
	public int getAnswerer() {return answerer;}
	public GameMessage setAnswerer(int answerer) {this.answerer = answerer; return this;}
	public int getClue() {return clue;}
	public GameMessage setClue(int clue) {this.clue = clue; return this;}
	public boolean isCorrect() {return correct;}
	public GameMessage setCorrect(boolean correct) {this.correct = correct; return this;}
	public int[] getClues() {return clues;}
	public GameMessage setClues(int[] clues) {this.clues = clues; return this;}
	public int getYou() {return you;}
	public GameMessage setYou(int you) {this.you = you; return this;}
	public String[] getUsernames() {return usernames;}
	public GameMessage setUsernames(String[] usernames) {this.usernames = usernames; return this;}
	public int[][] getPositions() {return positions;}
	public GameMessage setPositions(int[][] positions) {this.positions = positions; return this;}
	public String getText() {return text;}
	public GameMessage setText(String text) {this.text = text; return this;}

	public String toString() {
		return toJson();
	}
}
