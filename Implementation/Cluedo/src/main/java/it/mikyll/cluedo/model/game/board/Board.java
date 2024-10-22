package it.mikyll.cluedo.model.game.board;

import it.mikyll.cluedo.model.game.player.Player;

import java.util.*;

public class Board {
    private static final int BOARD_SIZE = 24;

    private int[] size;
    private List<Trapdoor> trapdoors;
    private List<StartingPoint> startingPoints;
    //private List<Room> rooms;
    private List<Door> doors;

    private final CellType[][] cells = new CellType[BOARD_SIZE][BOARD_SIZE];
    //private Set<Room> rooms;
    private final List<int[]> initPositions = new ArrayList<>();
    private List<Player> players;

    public Board() {
        // Init cells
        for (int y = 0; y < cells.length; y++) {
            for (int x = 0; x < cells[y].length; x++) {
                cells[y][x] = CellType.EMPTY;
            }
        }

        initRoomCells();
        initVoidCells();
        initStartingCells();
        initDoors();
        // TODO: init special cells? (random?)
    }

    private void initStudyCells()
    {
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 7; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
        cells[0][6] = CellType.NONE;
        cells[3][0] = CellType.NONE;
        cells[2][0] = CellType.TRAPDOOR;
    }
    private void initLibraryCells()
    {
        for (int y = 6; y < 11; y++) {
            for (int x = 0; x < 7; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
        cells[6][0] = CellType.NONE;
        cells[10][0] = CellType.NONE;
        cells[6][6] = CellType.EMPTY;
        cells[10][6] = CellType.EMPTY;
    }
    private void initBilliardRoomCells()
    {
        for (int y = 13; y < 17; y++) {
            for (int x = 0; x < 7; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
    }
    private void initCourtyardCells()
    {
        for (int y = 19; y < 24; y++) {
            for (int x = 0; x < 7; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
        cells[19][0] = CellType.NONE;
        cells[19][1] = CellType.TRAPDOOR;
    }
    private void initBallRoomCells()
    {
        for (int y = 18; y < 24; y++) {
            for (int x = 9; x < 17; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
        cells[23][9] = CellType.EMPTY;
        cells[23][10] = CellType.EMPTY;
        cells[23][15] = CellType.EMPTY;
        cells[23][16] = CellType.EMPTY;
    }
    private void initKitchenCells()
    {
        for (int y = 19; y < 24; y++) {
            for (int x = 19; x < 24; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
        cells[23][19] = CellType.TRAPDOOR;
    }
    private void initDiningRoomCells()
    {
        for (int y = 8; y < 16; y++) {
            for (int x = 16; x < 24; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
        cells[15][16] = CellType.EMPTY;
        cells[15][17] = CellType.EMPTY;
        cells[15][18] = CellType.EMPTY;
    }
    private void initLivingRoomCells()
    {
        for (int y = 0; y < 5; y++) {
            for (int x = 17; x < 24; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
        cells[0][17] = CellType.NONE;
        cells[4][23] = CellType.TRAPDOOR;
    }
    private void initEntranceCells()
    {
        for (int y = 0; y < 6; y++) {
            for (int x = 9; x < 15; x++) {
                cells[y][x] = CellType.ROOM;
            }
        }
    }
    private void initCluedoRoomCells()
    {
        for (int y = 8; y < 16; y++) {
            for (int x = 9; x < 14; x++) {
                cells[y][x] = CellType.CLUEDO;
            }
        }
    }
    private void initRoomCells()
    {
        initStudyCells();
        initLivingRoomCells();
        initLibraryCells();
        initBilliardRoomCells();
        initCourtyardCells();
        initBallRoomCells();
        initKitchenCells();
        initDiningRoomCells();
        initEntranceCells();
        initCluedoRoomCells();
    }
    private void initVoidCells()
    {
        cells[3][0] = CellType.NONE;
        cells[5][0] = CellType.NONE;
        cells[6][0] = CellType.NONE;
        cells[10][0] = CellType.NONE;
        cells[11][0] = CellType.NONE;
        cells[12][0] = CellType.NONE;
        cells[17][0] = CellType.NONE;
        cells[19][0] = CellType.NONE;
        cells[23][7] = CellType.NONE;
        cells[23][18] = CellType.NONE;
        cells[17][23] = CellType.NONE;
        cells[16][23] = CellType.NONE;
        cells[5][23] = CellType.NONE;
        cells[7][23] = CellType.NONE;
        cells[0][17] = CellType.NONE;
        cells[0][15] = CellType.NONE;
        cells[0][8] = CellType.NONE;
        cells[0][6] = CellType.NONE;
    }
    private void initStartingCells()
    {
        initPositions.add(new int[]{4, 0});
        initPositions.add(new int[]{18, 0});
        initPositions.add(new int[]{23, 10});
        initPositions.add(new int[]{23, 15});
        initPositions.add(new int[]{6, 23});
        initPositions.add(new int[]{0, 16});

        for (int[] pos : initPositions) {
            int y = pos[0];
            int x = pos[1];
            cells[y][x] = CellType.INIT;
        }
    }
    private void initDoors()
    {
        // Study
        cells[4][6] = CellType.DOOR_UP;
        // Library
        cells[8][7] = CellType.DOOR_LEFT;
        cells[11][3] = CellType.DOOR_UP;
        // Billiard Room
        cells[12][1] = CellType.DOOR_DOWN;
        cells[15][7] = CellType.DOOR_LEFT;
        // Courtyard
        cells[19][6] = CellType.DOOR_LEFT;
        // Ball Room
        cells[17][10] = CellType.DOOR_DOWN;
        cells[17][15] = CellType.DOOR_DOWN;
        cells[20][8] = CellType.DOOR_RIGHT;
        cells[20][17] = CellType.DOOR_LEFT;
        // Kitchen
        cells[18][20] = CellType.DOOR_DOWN;
        // Dining Room
        cells[7][17] = CellType.DOOR_DOWN;
        cells[11][15] = CellType.DOOR_RIGHT;
        // Living Room
        cells[5][17] = CellType.DOOR_UP;
        // Entrance
        cells[6][11] = CellType.DOOR_UP;
        cells[6][12] = CellType.DOOR_UP;
        cells[4][8] = CellType.DOOR_RIGHT;
        // Cluedo Room
        cells[7][11] = CellType.DOOR_DOWN;
        cells[11][8] = CellType.DOOR_RIGHT;
        cells[16][11] = CellType.DOOR_UP;
        cells[11][14] = CellType.DOOR_LEFT;
    }

    private void setRectangleCellType(int xStart, int yStart, int xSize, int ySize, CellType type) {
        if (xStart < 0 || yStart < 0 || xSize > BOARD_SIZE || ySize > BOARD_SIZE) {
            throw new IllegalArgumentException("Out of boundaries");
        }

        for (int x = xStart; x < xSize; x++) {
            for (int y = yStart; y < ySize; y++) {
                this.cells[y][x] = type;
            }
        }
    }

    public String toString() {
        String res = "";
        for (int x = 0; x < cells.length; x++) {
            for (int y = 0; y < cells[x].length; y++) {
                res += " " + cells[x][y].getRepresentation() + " ";
            }
            res += "\n";
        }
        return res;
    }


	/*private boolean checkStep(Cell start, Cell end)
	{
		return false;
	}
	public Set<Cell> getDestinationCells(Cell start, int distance)
	{
		Set<Cell> cells = new HashSet<>();

		// Get adjacent cells
		// For each, check if can reach

		return cells;
	}*/

    public static void main(String[] args) {
        Board b = new Board();

        System.out.print(b);
    }
}
