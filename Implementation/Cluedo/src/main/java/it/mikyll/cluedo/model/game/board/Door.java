package it.mikyll.cluedo.model.game.board;

import com.google.gson.annotations.SerializedName;
import it.mikyll.cluedo.model.game.clues.Room;

public class Door {
    private int[] position;
    private String direction;
    private Room room;
    // board.json uses camelCase for this key, while the loader expects lower_case_with_underscores
    @SerializedName("roomId")
    private String roomId;

    public Door(int[] position, String direction, Room room) {
        this.position = position;
        this.direction = direction;
        this.room = room;
    }

    public int[] getPosition() {return position;}
    public void setPosition(int[] position) {this.position = position;}
    public String getDirection() {return direction;}
    public void setDirection(String direction) {this.direction = direction;}
    public String getRoomId() {return roomId;}
    public void setRoomId(String roomId) {this.roomId = roomId;}
    public Room getRoom() {return room;}
    public void setRoom(Room room) {this.room = room;}

    // TODO
    public String toString() {
        return "TODO";
    }
}
