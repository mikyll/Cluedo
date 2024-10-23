package it.mikyll.cluedo.model.game.clues;

import it.mikyll.cluedo.model.game.board.CellType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room extends Clue {
    // TODO
    //private String description;
    private String id;
    private List<int[]> cells;

    public Room(String id, String name) {
        super(name, ClueType.ROOM);

        this.id = id;
        this.cells = new ArrayList<>();
    }
    public Room(String id, String name, int[]... cells) {
        this(id, name);

        this.cells = Arrays.asList(cells);
    }

    public String getId() {return id;}
    public void setId(String id) {this.id = id;}
    public List<int[]> getCells() {return cells;}
    public void setCells(List<int[]> cells) {this.cells = cells;}

    public String toString()
    {
        /*String strCells = "";
        int yMin = 1000, yMax = 0, xMin = 1000, xMax = 0;
        for (int i = 0; i < this.cells.size(); i++) {
            int y = this.cells.get(i)[0];
            int x = this.cells.get(i)[1];
            if (y > yMax)
                yMax = y;
            if (y < yMin)
                yMin = y;
            if (x > xMax)
                xMax = x;
            if (x < xMin)
                xMin = x;
        }

        CellType[][] cellTypes = new CellType[yMax-yMin][xMax-xMin];

        for(int y = 0; y < cellTypes.length; y++) {
            for(int x = 0; x < cellTypes[0].length; x++) {
                cellTypes =
            }
        }*/

        String strCells = "[ ";
        for (int i = 0; i < this.cells.size(); i++) {
            int[] cell = this.cells.get(i);
            strCells += "[" + cell[0] + "," + cell[1] + "]";
            if (i < this.cells.size() - 1)
                strCells += ", ";
        }
        strCells += " ]";

        // TODO
        return "Name: " + this.getName() + "\n" +
                "Cells: " + strCells;
    }
}
