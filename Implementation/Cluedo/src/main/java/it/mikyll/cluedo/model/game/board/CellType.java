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
    BONUS ("B");

    private final String representation;
    private Rooms room;

    CellType(String s) {
        representation = s;
    }

    public boolean isDoor() {
        return this.equals(DOOR_UP) || this.equals(DOOR_DOWN) || this.equals(DOOR_LEFT) || this.equals(DOOR_RIGHT);
    }
    public boolean isReachable(CellType src, int ySrc, int xSrc, int yDst, int xDst) {
        return this.equals(EMPTY)
                || this.equals(INIT)
                || this.equals(BONUS)
                || (this.equals(ROOM)
                    && (src.equals(DOOR_UP) && ySrc - yDst == 1)
                    && (src.equals(DOOR_DOWN) && ySrc - yDst == -1)
                    && (src.equals(DOOR_LEFT) && xSrc - xDst == 1)
                    && (src.equals(DOOR_RIGHT) && xSrc - xDst == -1));
    }

    public String getRepresentation() {
        return this.representation;
    }
}
