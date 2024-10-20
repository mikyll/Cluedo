package it.mikyll.cluedo.model.game.board;

import java.util.ArrayList;
import java.util.List;

public class Position {
    public int x;
    public int y;
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static List<Position> getPositionList(int[]... posList)
    {
        List<Position> result = new ArrayList<>();

        for (int[] pos : posList) {
            if (pos.length != 2) {
                // TODO: throw exception
                System.out.println("TODO: Position must have length = 2");
            }
            result.add(new Position(pos[0], pos[1]));
        }

        return result;
    }
}