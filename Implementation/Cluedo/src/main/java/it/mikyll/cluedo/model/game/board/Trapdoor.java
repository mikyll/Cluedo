package it.mikyll.cluedo.model.game.board;

public class Trapdoor {
    private int[] peer1;
    private int[] peer2;

    public Trapdoor(int[] peer1, int[] peer2) {
        if (peer1.length != 2)
            throw new IllegalArgumentException("peer1 must have 2 elements");
        if (peer2.length != 2)
            throw new IllegalArgumentException("peer2 must have 2 elements");

        this.peer1 = peer1;
        this.peer2 = peer2;
    }

    public int[] getPeer1() {return peer1;}
    public void setPeer1(int[] peer1) {this.peer1 = peer1;}
    public int[] getPeer2() {return peer2;}
    public void setPeer2(int[] peer2) {this.peer2 = peer2;}

    // TODO
    public String toString() {
        return "";
    }
}
