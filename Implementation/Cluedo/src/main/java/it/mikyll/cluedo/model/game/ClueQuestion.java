package it.mikyll.cluedo.model.game;

import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;

import java.util.ArrayList;
import java.util.List;

/*
 * A question about a character, a weapon and a room (a "suggestion" in the official rules).
 * The first player after the asker who owns at least one of the clues must show one of them
 * to the asker: which one is up to them.
 */
public class ClueQuestion {
	private final Player asker;
	private final Character character;
	private final Weapon weapon;
	private final Room room;
	// Player who must answer, null if nobody owns any of the clues
	private final Player answerer;
	// Clues the answerer can choose from
	private final List<Clue> answerOptions;
	private Clue shownClue;
	// In multiplayer the shown clue is known only by the asker and the answerer: the others only know it was shown
	private boolean answered;

	public ClueQuestion(Player asker, Character character, Weapon weapon, Room room, Player answerer, List<Clue> answerOptions) {
		this.asker = asker;
		this.character = character;
		this.weapon = weapon;
		this.room = room;
		this.answerer = answerer;
		this.answerOptions = new ArrayList<>(answerOptions);
	}

	public Player getAsker() {return asker;}
	public Character getCharacter() {return character;}
	public Weapon getWeapon() {return weapon;}
	public Room getRoom() {return room;}
	public Player getAnswerer() {return answerer;}
	public List<Clue> getAnswerOptions() {return new ArrayList<>(answerOptions);}
	public Clue getShownClue() {return shownClue;}
	void setShownClue(Clue shownClue) {
		this.shownClue = shownClue;
		this.answered = true;
	}
	void setAnswered() {this.answered = true;}

	public boolean hasAnswerer() {
		return answerer != null;
	}

	public boolean isAnswered() {
		return answerer == null || answered;
	}
}
