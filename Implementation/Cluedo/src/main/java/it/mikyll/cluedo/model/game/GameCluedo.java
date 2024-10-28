package it.mikyll.cluedo.model.game;

import java.util.*;

import it.mikyll.cluedo.model.game.board.Board;
import it.mikyll.cluedo.model.game.board.CellType;
import it.mikyll.cluedo.model.game.board.Position;
import it.mikyll.cluedo.model.game.board.cell.Cell;
import it.mikyll.cluedo.model.game.clues.*;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerArtificial;
import it.mikyll.cluedo.model.game.player.PlayerHuman;
import it.mikyll.cluedo.persistence.AssetLoader;

/*
 * Game class
 */
public class GameCluedo {
	public static void main(String[] args)
	{
		List<Player> players = new ArrayList<>();
		players.add(new PlayerHuman("mikyll"));
		players.add(new PlayerHuman("tanky"));
		players.add(new PlayerArtificial("Comp1"));
		players.add(new PlayerArtificial("Comp2"));

		GameCluedo game = new GameCluedo(players);

		game.prepareGame();
		System.out.println("--------------------------------");

		// Loop over players list and if a player is AI choose random character
		/*for (int iPlayer = 0; iPlayer < game.getPlayers().size(); iPlayer++) {
			Player p = game.getPlayers().get(iPlayer);

			if (p instanceof PlayerHuman) {
				Scanner scanner = new Scanner(System.in);
				System.out.println("Player #" + (iPlayer + 1) + " " + p.getUsername() + ", choose your character.");
				System.out.println("Available:\n" + game.getAvailableCharactersString(2));
				System.out.print("Choice: ");

				int iCharacter = Integer.parseInt(scanner.nextLine());
				System.out.println("--------------------------------");
				game.setPlayerCharacter(iPlayer, iCharacter);
			} else if (p instanceof PlayerArtificial) {
				game.setPlayerCharacter(iPlayer, game.getRandom().nextInt(game.getAvailableCharacters().size()));
				System.out.println("Player #" + (iPlayer + 1) + " " + p.getUsername() + " (AI), chose " + p.getCharacter().getName());
				System.out.println("--------------------------------");
			}
		}*/

		// Test
		for (Player p : game.getPlayers()) {
			game.setPlayerCharacter(p.getTurn()-1, game.getRandom().nextInt(game.getAvailableCharacters().size()));
		}

		System.out.println("Players list:");
		for (Player p : game.getPlayers()) {
			System.out.println(p.getTurn() + "] " + p.getUsername() + ((p instanceof PlayerArtificial) ? " (AI): " : ": ") +
					p.getCharacter().getName());
		}

		// test
		//System.out.println(game.toStringBoard());
		//System.out.println(game.toStringBoardWithPlayers());

		int iPlayer = 0;
		System.out.println("Available moves for player #" + (iPlayer+1));
		System.out.println(game.toStringBoardWithAvailableMoves(iPlayer, 10));
		// start

		// TODO
	}

	private Random random;
	private Board board;
	private List<Clue> totalCluesList;
	private List<Player> players;
	private MurderEnvelope murderEnvelope;
	private List<Character> availableCharacters;
	private boolean canStart;
	private int currentTurn;
	private int[][] playerCells;

	// Timer
	// User list
	// Settings
	
	// characters
	// weapons
	// places/rooms
	
	// current turn
	
	// envelope (solution)

	/*
	 * Constructor. It takes a list of users and the game settings
	 */
	public GameCluedo(List<Player> players) {
		this.random = new Random(1);
		this.players = players;

		this.canStart = false;
		this.currentTurn = 0;
	}

	public Random getRandom() {return random;}
	public void setRandom(Random random) {this.random = random;}
	public List<Player> getPlayers() {return players;}
	public void setPlayers(List<Player> players) {this.players = players;}
	public List<Character> getAvailableCharacters() {return availableCharacters;}
	public void setAvailableCharacters(List<Character> availableCharacters) {this.availableCharacters = availableCharacters;}


	public void prepareGame() {
		// Init board
		board = AssetLoader.loadBoard();
		board.initCells();
		this.playerCells = new int[board.getSize()[0]][board.getSize()[1]];
		for (int y = 0; y < board.getSize()[0]; y++) {
			for (int x = 0; x < board.getSize()[1]; x++) {
				this.playerCells[y][x] = -1;
			}
		}

		// Init murder envelope
		totalCluesList = new ArrayList<>();
		totalCluesList.addAll(AssetLoader.loadCharacters());
		totalCluesList.addAll(AssetLoader.loadBoard().getRooms());
		totalCluesList.addAll(AssetLoader.loadWeapons());
		List<Clue> assignableCluesList = new ArrayList<>(totalCluesList);
		System.out.println("Initial clue list: " + assignableCluesList.size());
		murderEnvelope = new MurderEnvelope(assignableCluesList);

		System.out.println("assignable clue list: " + assignableCluesList.size());

		// Init available characters
		this.availableCharacters = new ArrayList<>();
		for (Clue c : totalCluesList) {
			if (c.getType().equals(ClueType.CHARACTER)) {
				this.availableCharacters.add((Character) c);
			}
		}

		// Assign player turns
		Collections.shuffle(this.players);
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).setTurn(i+1);
		}

		// Assign clue cards
		for (int i = 0; i < players.size() && !assignableCluesList.isEmpty(); i++) {
			Clue clue = assignableCluesList.get(0);
			Player player = players.get(i);

			player.getClues().add(clue);
			assignableCluesList.remove(0);
		}

		// Init player positions
		List<int[]> initPos = this.board.getStartingPoints();
		Collections.shuffle(initPos);
		for (int i = 0; i < this.players.size(); i++) {
			int[] pos = initPos.get(i);
			Player player = this.players.get(i);
			player.setPosition(pos);
			this.playerCells[pos[0]][pos[1]] = player.getTurn();
		}
	}

	public void setPlayerCharacter(int iPlayer, int iCharacter) {
		if (iPlayer > players.size() - 1)
			throw new IllegalArgumentException("Player number out of bounds");
		if (iCharacter > availableCharacters.size() - 1)
			throw new IllegalArgumentException("Character number out of bounds");

		Character character = availableCharacters.get(iCharacter);
		this.players.get(iPlayer).setCharacter(character);

		// Remove the character from the available characters
		this.availableCharacters.remove(character);
	}

	public boolean[][] getAvailableDestinations(int[] srcPos, int steps) {
		int rows = board.getSize()[0];
		int cols = board.getSize()[1];
		boolean[][] reachable = new boolean[rows][cols];

		// Possible moves in each direction (up, down, left, right)
		int[][] directions = {
				{-1, 0}, // up
				{1, 0},  // down
				{0, -1}, // left
				{0, 1}   // right
		};

		// Use a queue to track positions and remaining steps
		Queue<int[]> queue = new LinkedList<>();
		queue.add(new int[] {srcPos[0], srcPos[1], steps});

		while (!queue.isEmpty()) {
			int[] current = queue.poll();
			int row = current[0];
			int col = current[1];
			int remainingSteps = current[2];

			// Mark the cell as reachable
			reachable[row][col] = true;

			// If no more steps, skip to the next position in the queue
			if (remainingSteps == 0) continue;

			// Explore each direction
			for (int[] dir : directions) {
				int newRow = row + dir[0];
				int newCol = col + dir[1];

				// Check if the new position is within board bounds
				if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
					// Check cell types: allow moving into "Room" only if the current cell is a "Door"
					CellType nextCellType = board.getCells()[newRow][newCol];
					CellType currentCellType = board.getCells()[row][col];

					if (!reachable[newRow][newCol] && nextCellType.equals(CellType.EMPTY) ||
							nextCellType.isDoor()) {
						queue.add(new int[] {newRow, newCol, remainingSteps - 1});
					}
				}
			}
		}

		return reachable;
	}

	/*public boolean[][] getAvailableDestinations(int[] srcPos, int steps) {
		int rows = board.getSize()[0];
		int cols = board.getSize()[1];
		boolean[][] reachable = new boolean[rows][cols];

		// Possible moves with associated direction labels
		int[][] directions = {
				{-1, 0}, // up
				{1, 0},  // down
				{0, -1}, // left
				{0, 1}   // right
		};
		String[] directionLabels = {"UP", "DOWN", "LEFT", "RIGHT"};

		// Use a queue to track positions and remaining steps
		Queue<int[]> queue = new LinkedList<>();
		queue.add(new int[] {srcPos[0], srcPos[1], steps});

		while (!queue.isEmpty()) {
			int[] current = queue.poll();
			int row = current[0];
			int col = current[1];
			int remainingSteps = current[2];

			// Mark the cell as reachable
			reachable[row][col] = true;

			// If no more steps, skip to the next position in the queue
			if (remainingSteps == 0) continue;

			// Explore each direction
			for (int i = 0; i < directions.length; i++) {
				int newRow = row + directions[i][0];
				int newCol = col + directions[i][1];
				String directionLabel = directionLabels[i];

				// Check if the new position is within board bounds
				if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
					// Retrieve the cell types for the current and next cells
					CellType nextCellType = board.getCells()[newRow][newCol];
					CellType currentCellType = board.getCells()[row][col];

					// Check if the next cell is reachable based on type and allowed directions
					if (!reachable[newRow][newCol]) {
						// If moving to a "Room" cell, ensure the current cell is a "Door" with an allowed direction
						if (nextCellType.equals(CellType.ROOM) && currentCellType.isDoor()) {
							Set<String> allowedDirections = board.getAllowedDirections(row, col);
							if (allowedDirections.contains(directionLabel)) {
								queue.add(new int[] {newRow, newCol, remainingSteps - 1});
							}
						}
						// If moving to a "Door" cell, it's always reachable
						else if (nextCellType.isDoor()) {
							queue.add(new int[] {newRow, newCol, remainingSteps - 1});
						}
					}
				}
			}
		}

		return reachable;
	}*/

	public void movePlayer(Player player, int[] newPos) {
		int[] src = player.getPosition();
		this.playerCells[src[0]][src[1]] = -1;
	}
	
	// everybody lost
	public void endGame() {
		
	}
	
	// player wins
	public void endGame(Player winner) {
		
	}
	
	public void setCharacter(Player p, Characters character) {
		
	}
	
	// get random number (2-12)
	public int rollDice() {
		return -1;
	}
	
	
	
	public Clue makeAccusation(Player player, Room room, Character character, Weapon weapon) {
		// ask the first player after p (by turns) if he has one of the clues.
		
		// Example:
		// Does p+1 have one of the clues? No -> "Player p+1 cannot answer"
		// Does p+2 have one of the clues? Yes -> shows him the clue (just one). "Player p+2 reveals a clue"
		
		// If no one has none of the clues you asked, then you know the answer and must wait the next turn
		
		return null;
	}
	
	public boolean makeFinalAccusation(Player p, Characters who, Weapons what, Rooms where) {
		// correct? The game ends, and p wins;
		
		// wrong? The game continues, and p is eliminated
		return false;
	}

	public String getAvailableCharactersString(int leftPadding) {
		StringBuilder result = new StringBuilder();

		for (int i = 0; i < availableCharacters.size(); i++) {
			for (int j = 0; j < leftPadding; j++) {
				result.append(" ");
			}
			result.append(i).append(") ").append(availableCharacters.get(i).getName()).append("\n");
		}

		return result.toString();
	}

	public String toStringBoard() {
		return this.board.toString();
	}

	public String toStringBoardWithPlayers() {
		StringBuilder res = new StringBuilder();
		CellType[][] cells = board.getCells();

		for (int y = 0; y < board.getSize()[0]; y++) {
			for (int x = 0; x < board.getSize()[1]; x++) {
				String value;
				if (playerCells[y][x] != -1) {
					if (playerCells[y][x] == currentTurn)
						value = "[" + playerCells[y][x] + "]";
					else
						value = "{" + playerCells[y][x] + "}";
				}
				else {
					value = " " + cells[y][x].getRepresentation() + " ";
				}
				res.append(value);
			}
			res.append("\n");
		}

		return res.toString();
	}

	public String toStringBoardWithAvailableMoves(int iPlayer, int steps) {
		StringBuilder res = new StringBuilder();
		CellType[][] cells = board.getCells();
		boolean[][] destList = this.getAvailableDestinations(players.get(iPlayer).getPosition(), steps);

		for (int y = 0; y < board.getSize()[0]; y++) {
			for (int x = 0; x < board.getSize()[1]; x++) {
				String value;
				if (playerCells[y][x] != -1) {
					if (playerCells[y][x] == currentTurn)
						value = "[" + playerCells[y][x] + "]";
					else
						value = "{" + playerCells[y][x] + "}";
				}
				else if (destList[y][x]) {
					value = "(" + cells[y][x].getRepresentation() + ")";
				} else {
					value = " " + cells[y][x].getRepresentation() + " ";
				}
				res.append(value);
			}
			res.append("\n");
		}

		return res.toString();
	}

	public String toString() {
		// TODO: prints the game state

		// Game Phase
		String result = "Phase: ...\n"
				+ "Player turn: ...";
		// Player turn


		return "";
	}
}
