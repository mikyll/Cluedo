package it.mikyll.cluedo.model.game.clues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room extends Clue {
    private String id;
    private String description;
    private List<int[]> cells;
    private List<int[]> doors;

    public Room(String name) {
        super(name, ClueType.ROOM);
        this.

        cells = new ArrayList<>();
        doors = new ArrayList<>();
    }
    /*public Room(int[]... cells) {
        this.cells = Arrays.asList(cells);
        doors = new ArrayList<>();
    }*/

    public void addDoors() {

    }

    public String toString()
    {
        // TODO
        return "Name: " + this.getName();
    }
}
