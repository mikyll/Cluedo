package it.mikyll.cluedo.model.game.board;

import it.mikyll.cluedo.model.game.clues.Room;

import java.util.*;

public class Board {
    private static final int BOARD_SIZE = 24;

    private int[] size;
    private List<Room> rooms;
    private List<int[]> startingPoints;
    private List<Door> doors;
    private List<Trapdoor> trapdoors;
    private List<int[]> voidCells;

    private CellType[][] cells;
    //private Set<Room> rooms;
    //private final List<int[]> initPositions = new ArrayList<>();
    //private List<Player> players;

    public Board() {
        this.size = new int[2];
        this.startingPoints = new ArrayList<>();
        this.trapdoors = new ArrayList<>();
        this.doors = new ArrayList<>();
        this.voidCells = new ArrayList<>();
    }

    public int[] getSize() {return size;}
    public void setSize(int[] size) {this.size = size;}
    public List<Room> getRooms() {return rooms;}
    public void setRooms(List<Room> rooms) {this.rooms = rooms;}
    public List<Trapdoor> getTrapdoors() {return trapdoors;}
    public void setTrapdoors(List<Trapdoor> trapdoors) {this.trapdoors = trapdoors;}
    public List<int[]> getStartingPoints() {return startingPoints;}
    public void setStartingPoints(List<int[]> startingPoints) {this.startingPoints = startingPoints;}
    public List<Door> getDoors() {return doors;}
    public void setDoors(List<Door> doors) {this.doors = doors;}
    public CellType[][] getCells() {return cells;}
    public void setCells(CellType[][] cells) {this.cells = cells;}
    public List<int[]> getVoidCells() {return voidCells;}
    public void setVoidCells(List<int[]> voidCells) {this.voidCells = voidCells;}

    // TODO
    public void initCells()
    {
        this.cells = new CellType[this.size[0]][this.size[1]];

        for (int y = 0; y < this.size[0]; y++) {
            for (int x = 0; x < this.size[1]; x++) {
                this.cells[y][x] = CellType.EMPTY;
            }
        }

        // Init rooms
        for (Room r : this.rooms) {
            for (int i = 0; i < r.getCells().size(); i++) {
                int y = r.getCells().get(i)[0];
                int x = r.getCells().get(i)[1];

                if (this.cells[y][x].equals(CellType.EMPTY)) {
                    this.cells[y][x] = CellType.ROOM;
                }
                else {
                    System.out.println("ERROR Room: [" + y + "," + x + "] is not empty!");
                }
            }
        }

        // Init starting points
        for (int[] s : this.startingPoints) {
            int y = s[0];
            int x = s[1];
            if (this.cells[y][x].equals(CellType.EMPTY)) {
                this.cells[y][x] = CellType.INIT;
            }
            else {
                System.out.println("ERROR Starting Point: [" + y + "," + x + "] is not empty!");
            }
        }

        // Doors
        for (Door d : this.doors) {
            int y = d.getPosition()[0];
            int x = d.getPosition()[1];
            if (this.cells[y][x].equals(CellType.EMPTY)) {
                Direction dir = Direction.valueOf(d.getDirection().toUpperCase());
                switch (dir) {
                    case UP:
                        this.cells[y][x] = CellType.DOOR_UP;
                        break;
                    case DOWN:
                        this.cells[y][x] = CellType.DOOR_DOWN;
                        break;
                    case LEFT:
                        this.cells[y][x] = CellType.DOOR_LEFT;
                        break;
                    case RIGHT:
                        this.cells[y][x] = CellType.DOOR_RIGHT;
                        break;
                }
            }
            else {
                System.out.println("ERROR Door: [" + y + "," + x + "] is not empty!");
            }
        }

        // Trapdoors

        for (Trapdoor t : this.trapdoors) {
            int y = t.getPeer1()[0];
            int x = t.getPeer1()[1];
            if (this.cells[y][x].equals(CellType.EMPTY)) {
                this.cells[y][x] = CellType.TRAPDOOR;
            }
            else {
                System.out.println("ERROR Trapdoor.peer1: [" + y + "," + x + "] is not empty!");
            }

            y = t.getPeer2()[0];
            x = t.getPeer2()[1];
            if (this.cells[y][x].equals(CellType.EMPTY)) {
                this.cells[y][x] = CellType.TRAPDOOR;
            }
            else {
                System.out.println("ERROR Trapdoor.peer2: [" + y + "," + x + "] is not empty!");
            }
        }

        // void cells
        for (int[] v : this.voidCells) {
            int y = v[0];
            int x = v[1];
            if (this.cells[y][x].equals(CellType.EMPTY)) {
                this.cells[y][x] = CellType.NONE;
            }
            else {
                System.out.println("ERROR Void: [" + y + "," + x + "] is not empty!");
            }
        }

        // loop over all lists and build the cells (board representation)

        // throw exception/issue warning if a cell is already populated
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

    public static void main(String[] args) {
        Board b = new Board();

        System.out.print(b);
    }

    /*
    public Board() {
        this.cells = new CellType[BOARD_SIZE][BOARD_SIZE];

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

	private boolean checkStep(Cell start, Cell end)
	{
		return false;
	}
	public Set<Cell> getDestinationCells(Cell start, int distance)
	{
		Set<Cell> cells = new HashSet<>();

		// Get adjacent cells
		// For each, check if can reach

		return cells;
	}
    */
}
