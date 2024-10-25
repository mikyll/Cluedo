package it.mikyll.cluedo.model.game.board.cell;

import it.mikyll.cluedo.model.game.board.CellType;

public abstract class Cell {
    private CellType type;
    private int[] position;

    public CellType getType() {return type;}
    public void setType(CellType type) {this.type = type;}
    public int[] getPosition() {return position;}
    public void setPosition(int[] position) {this.position = position;}
}
