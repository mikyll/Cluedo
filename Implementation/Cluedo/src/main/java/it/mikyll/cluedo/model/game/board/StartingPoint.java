package it.mikyll.cluedo.model.game.board;

public class StartingPoint {
    private int[] cell;

    public StartingPoint(int[] cell) {
        if (cell.length != 2)
            throw new IllegalArgumentException("cell size must be 2");
        this.cell = cell;
    }

    public int[] getCell() {return cell;}
    public void setCell(int[] cell) {this.cell = cell;}

    // TODO
    public String toString() {
        return "StartingPoint: [" + this.cell[0] + "," + this.cell[1] + "]";
    }
}
