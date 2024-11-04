package it.mikyll.cluedo.model.game.board;

import it.mikyll.cluedo.model.game.clues.Room;

public class Door {
    private int[] position;
    private String direction;
    private Room room;

    public Door(int[] position, String direction, Room room) {
        this.position = position;
        this.direction = direction;
        this.room = room;
    }

    public int[] getPosition() {return position;}
    public void setPosition(int[] position) {this.position = position;}
    public String getDirection() {return direction;}
    public void setDirection(String direction) {this.direction = direction;}
    public Room getRoom() {return room;}
    public void setRoom(Room room) {this.room = room;}

    // TODO
    public String toString() {
        return "TODO";
    }
}
