package it.mikyll.cluedo.model.game.board;

import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Rooms;

public class Door {
    private int[] pos;
    private Direction dir;
    private Room room;

    public Door(int[] pos, Direction direction, Room room) {
        this.pos = pos;
        this.dir = direction;
        this.room = room;
    }

    public int[] getPos() {return pos;}
    public void setPos(int[] pos) {this.pos = pos;}
    public Direction getDir() {return dir;}
    public void setDir(Direction dir) {this.dir = dir;}
    public Room getRoom() {return room;}
    public void setRoom(Room room) {this.room = room;}

    // TODO
    public String toString() {
        return "TODO";
    }
}
