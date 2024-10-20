package it.mikyll.cluedo.model.game.board;

import it.mikyll.cluedo.model.game.clues.Rooms;

public enum CellType {
    NONE (" "),
    EMPTY ("-"),
    INIT ("X"),
    ROOM ("R"),
    CLUEDO ("C"),
    TRAPDOOR ("T"),
    DOOR_UP ("^"),
    DOOR_DOWN ("v"),
    DOOR_LEFT ("<"), // \u23F4 ⏴
    DOOR_RIGHT (">"),
    SPECIAL ("S");

    private final String representation;
    private Rooms room;

    CellType(String s) {
        representation = s;
    }

    public String getRepresentation() {
        return this.representation;
    }
}
