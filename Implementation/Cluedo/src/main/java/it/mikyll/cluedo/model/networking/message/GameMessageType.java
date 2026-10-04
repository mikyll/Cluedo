package it.mikyll.cluedo.model.networking.message;

public enum GameMessageType {
	// Requests (client -> host)
	CHOOSE_CHARACTER, ROLL_DICE, MOVE, ASK_CLUE, SHOW_CLUE, FINAL_ACCUSATION, END_TURN,

	// Events (host -> clients)
	SETUP, CHARACTER_TURN, CHARACTER_CHOSEN, GAME_STARTED, DICE_ROLLED, MOVED, CLUE_ASKED, CLUE_SHOWN,
	ACCUSATION_MADE, TURN_ENDED, PLAYER_LEFT, GAME_OVER, ERROR;
}
