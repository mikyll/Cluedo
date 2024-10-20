package it.mikyll.cluedo.model.game.board;

import it.mikyll.cluedo.model.game.clues.Rooms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Room {
    Rooms name;
    List<int[]> cells;
    List<int[]> doors;

    public Room() {
        cells = new ArrayList<>();
        doors = new ArrayList<>();
    }
    public Room(int[]... cells) {
        this.cells = Arrays.asList(cells);
        doors = new ArrayList<>();
    }

    public void addDoors() {

    }
}
