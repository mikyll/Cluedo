package it.mikyll.cluedo.model.game.board.cell;

import it.mikyll.cluedo.model.game.board.CellType;

public class Trapdoor extends Cell {
    private Trapdoor otherPeer;

    public Trapdoor(int[] pos) {
        super.setType(CellType.TRAPDOOR);
        super.setPosition(pos);
    }

    public Trapdoor getOtherPeer() {
        return otherPeer;
    }

    public void setOtherPeer(Trapdoor otherPeer) {
        this.otherPeer = otherPeer;
    }
}
