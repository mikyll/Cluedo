package it.mikyll.cluedo.model.game;

import java.util.*;

import it.mikyll.cluedo.model.game.board.Board;
import it.mikyll.cluedo.model.game.board.CellType;
import it.mikyll.cluedo.model.game.board.Door;
import it.mikyll.cluedo.model.game.clues.*;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.persistence.AssetLoader;

/*
 * Game class.
 *
 * A turn goes like this:
 * 1. the player rolls the dices, or uses the trapdoor of the room they are in;
 * 2. the player moves (entering a room ends the movement);
 * 3. if the player is in a room, they can ask a clue question about that room;
 * 4. the player ends the turn.
 * The final accusation can be made at any moment of the turn (by default only from the accusation room).
 */
public class GameCluedo {
	// If true, the final accusation can only be made from the accusation room ("accusation_room_id" in board.json).
	// Set it to false to allow it at any moment of the player's turn, as in the classic rules.
	public static final boolean ACCUSATION_ONLY_IN_ACCUSATION_ROOM = true;

	// Possible moves in each direction (up, down, left, right)
	private static final int[][] DIRECTIONS = {
			{-1, 0}, // up
			{1, 0},  // down
			{0, -1}, // left
			{0, 1}   // right
	};

	private int numDices;
	private Random random;
	private Board board;
	private List<Clue> totalCluesList;
	private List<Player> players;
	private MurderEnvelope murderEnvelope;
	private List<Character> availableCharacters;
	private int currentPlayer;
	private int[][] playerCells;

	// Current turn
	private int diceResult;
	private boolean hasMoved;
	private boolean hasAskedClue;
	private ClueQuestion clueQuestion;

	// End of the game
	private boolean over;
	private Player winner;

	/*
	 * Constructor. It takes a list of users and the game settings
	 */
	public GameCluedo(List<Player> players, int numDices) {
		this(players, numDices, new Random());
	}

	public GameCluedo(List<Player> players, int numDices, Random random) {
		if (players.size() < 2 || players.size() > 6)
			throw new IllegalArgumentException("Players must be between 2 and 6");
		if (numDices < 1)
			throw new IllegalArgumentException("At least one dice is needed");

		this.random = random;
		this.players = players;
		this.numDices = numDices;
	}

	public Random getRandom() {return random;}
	public void setRandom(Random random) {this.random = random;}
	public List<Player> getPlayers() {return players;}
	public void setPlayers(List<Player> players) {this.players = players;}
	public List<Character> getAvailableCharacters() {return availableCharacters;}
	public void setAvailableCharacters(List<Character> availableCharacters) {this.availableCharacters = availableCharacters;}
	public Board getBoard() {return board;}
	public List<Clue> getTotalCluesList() {return new ArrayList<>(totalCluesList);}
	public int getDiceResult() {return diceResult;}
	public ClueQuestion getClueQuestion() {return clueQuestion;}
	public boolean isOver() {return over;}
	public Player getWinner() {return winner;}

	// NB: the envelope should be revealed only when the game is over
	public MurderEnvelope getMurderEnvelope() {return murderEnvelope;}
	void setMurderEnvelope(MurderEnvelope murderEnvelope) {this.murderEnvelope = murderEnvelope;}

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

		// Init clues. Rooms come from the board, except the accusation room (which is not a clue)
		totalCluesList = new ArrayList<>();
		totalCluesList.addAll(AssetLoader.loadCharacters());
		totalCluesList.addAll(board.getClueRooms());
		totalCluesList.addAll(AssetLoader.loadWeapons());

		// Init available characters (players may have already chosen one)
		this.availableCharacters = new ArrayList<>();
		for (Clue c : totalCluesList) {
			if (c.getType().equals(ClueType.CHARACTER) && !isCharacterTaken((Character) c)) {
				this.availableCharacters.add((Character) c);
			}
		}

		// Init murder envelope
		List<Clue> assignableCluesList = new ArrayList<>(totalCluesList);
		murderEnvelope = new MurderEnvelope(assignableCluesList, random);

		// Assign player turns
		Collections.shuffle(this.players, random);
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).setTurn(i+1);
		}

		// Assign clue cards: deal them one at a time, until there are none left
		Collections.shuffle(assignableCluesList, random);
		for (Player player : players) {
			player.getClues().clear();
		}
		for (int i = 0; i < assignableCluesList.size(); i++) {
			players.get(i % players.size()).getClues().add(assignableCluesList.get(i));
		}

		// Init player positions
		List<int[]> initPos = new ArrayList<>(this.board.getStartingPoints());
		Collections.shuffle(initPos, random);
		for (int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			player.setPosition(null);
			player.setEliminated(false);
			player.setMoved(false);
			movePlayer(player, initPos.get(i));
		}

		this.currentPlayer = 0;
		this.over = false;
		this.winner = null;
		resetTurn();
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

	private boolean isCharacterTaken(Character character) {
		for (Player p : players) {
			if (character.equals(p.getCharacter()))
				return true;
		}
		return false;
	}

	// Players & positions ====================================================
	public Player getCurrentPlayer() {
		return players.get(currentPlayer);
	}

	public boolean isTurnOf(Player player) {
		return !over && getCurrentPlayer().equals(player);
	}

	// Returns the room the player is in, or null if the player is in a corridor
	public Room getRoomOf(Player player) {
		return board.getRoomAt(player.getPosition());
	}

	public boolean isInAccusationRoom(Player player) {
		return board.isAccusationRoom(getRoomOf(player));
	}

	public void movePlayer(int iPlayer, int[] newPos) {
		this.movePlayer(players.get(iPlayer), newPos);
	}

	/*
	 * Puts the player pawn on the given cell, without checking the game rules.
	 * Many pawns can stay in a room: if the room cell is taken, another free cell of the same room is used.
	 */
	public void movePlayer(Player player, int[] newPos) {
		int[] dst = newPos;
		Room room = board.getRoomAt(newPos);
		if (playerCells[dst[0]][dst[1]] != -1 && playerCells[dst[0]][dst[1]] != player.getTurn()) {
			if (room == null)
				throw new IllegalArgumentException("Cell [" + dst[0] + "," + dst[1] + "] is already occupied");
			dst = getFreeCell(room);
		}

		int[] src = player.getPosition();
		if (src != null) {
			this.playerCells[src[0]][src[1]] = -1;
		}
		player.setPosition(new int[] {dst[0], dst[1]});
		this.playerCells[dst[0]][dst[1]] = player.getTurn();
	}

	private void moveToRoom(Player player, Room room) {
		if (!room.equals(getRoomOf(player)))
			movePlayer(player, getFreeCell(room));
	}

	private int[] getFreeCell(Room room) {
		for (int[] cell : room.getCells()) {
			if (playerCells[cell[0]][cell[1]] == -1)
				return cell;
		}
		throw new IllegalStateException("No free cell in room " + room.getName());
	}

	private boolean isFree(int y, int x) {
		return playerCells[y][x] == -1;
	}

	// Dices & movement =======================================================

	// get random number (numDices - numDices*6), summing each dice
	public int rollDices() {
		int result = 0;
		for (int i = 0; i < numDices; i++) {
			result += this.random.nextInt(6) + 1;
		}
		return result;
	}

	public boolean canRollDices(Player player) {
		return isTurnOf(player) && diceResult < 0 && !hasMoved && !isWaitingForAnswer();
	}

	public int rollDicesForTurn() {
		if (!canRollDices(getCurrentPlayer()))
			throw new IllegalStateException("Dices can't be rolled now");

		this.diceResult = rollDices();
		return this.diceResult;
	}

	// Returns the room reachable with the trapdoor of the room the player is in, or null
	public Room getTrapdoorDestination(Player player) {
		Room room = getRoomOf(player);
		return room == null ? null : board.getTrapdoorDestination(room);
	}

	public boolean canMove(Player player) {
		return isTurnOf(player) && !hasMoved && !isWaitingForAnswer()
				&& (diceResult >= 0 || getTrapdoorDestination(player) != null);
	}

	/*
	 * Returns the cells the current player can move to: after rolling the dices, the cells reachable
	 * with that number of steps; before rolling, the room reachable with the trapdoor (if any).
	 * See getAvailableDestinations for the meaning of the values.
	 */
	public int[][] getCurrentDestinations() {
		Player player = getCurrentPlayer();
		int[][] result = new int[board.getSize()[0]][board.getSize()[1]];
		if (!canMove(player))
			return result;

		if (diceResult >= 0)
			return getAvailableDestinations(player.getPosition(), diceResult);

		for (int[] cell : getTrapdoorDestination(player).getCells()) {
			result[cell[0]][cell[1]] = 1;
		}
		return result;
	}

	public boolean isCurrentDestination(int[] dst) {
		return board.isInBounds(dst[0], dst[1]) && getCurrentDestinations()[dst[0]][dst[1]] > 0;
	}

	public void moveCurrentPlayer(int[] dst) {
		if (!isCurrentDestination(dst))
			throw new IllegalArgumentException("Cell [" + dst[0] + "," + dst[1] + "] is not reachable");

		movePlayer(getCurrentPlayer(), dst);
		this.hasMoved = true;
	}

	/*
	 * Returns a matrix with the cells reachable from srcPos with at most the given steps.
	 * For each cell, the value is 0 if it is not reachable, otherwise the remaining steps + 1.
	 * Rules:
	 * - pawns move up, down, left and right, but can't walk through other pawns;
	 * - a room can be entered only from one of its doors, in the direction of the door, and that ends the movement
	 *   (all the cells of the room are marked as reachable);
	 * - a pawn in a room leaves it from one of its doors, and can't enter it again in the same turn.
	 */
	public int[][] getAvailableDestinations(int[] srcPos, int steps) {
		int rows = board.getSize()[0];
		int cols = board.getSize()[1];
		int[][] reachable = new int[rows][cols];
		// Most remaining steps with which each cell has been reached so far
		int[][] bestRemaining = new int[rows][cols];
		for (int[] row : bestRemaining) {
			Arrays.fill(row, -1);
		}

		// Use a queue to track positions and remaining steps
		Queue<int[]> queue = new LinkedList<>();
		Room startRoom = board.getRoomAt(srcPos);
		if (startRoom == null) {
			queue.add(new int[] {srcPos[0], srcPos[1], steps});
			bestRemaining[srcPos[0]][srcPos[1]] = steps;
		}
		else if (steps > 0) {
			// Leaving a room: the first step goes on one of its doors
			for (Door door : board.getDoors(startRoom)) {
				int[] pos = door.getPosition();
				if (isFree(pos[0], pos[1])) {
					queue.add(new int[] {pos[0], pos[1], steps - 1});
					bestRemaining[pos[0]][pos[1]] = steps - 1;
					reachable[pos[0]][pos[1]] = steps;
				}
			}
		}

		while (!queue.isEmpty()) {
			int[] current = queue.poll();
			int row = current[0];
			int col = current[1];
			int remainingSteps = current[2];

			// If no more steps, skip to the next position in the queue
			if (remainingSteps == 0) continue;

			CellType currentCellType = board.getCells()[row][col];

			// Explore each direction
			for (int[] dir : DIRECTIONS) {
				int newRow = row + dir[0];
				int newCol = col + dir[1];

				// Check if the new position is within board bounds
				if (!board.isInBounds(newRow, newCol))
					continue;

				// Entering a room ends the movement
				Room room = board.getRoomAt(newRow, newCol);
				if (room != null) {
					if (currentCellType.isDoor() && Arrays.equals(currentCellType.getDir(), dir) && !room.equals(startRoom)) {
						for (int[] cell : room.getCells()) {
							reachable[cell[0]][cell[1]] = Math.max(reachable[cell[0]][cell[1]], remainingSteps);
						}
					}
					continue;
				}

				// Check if we can reach this cell with more remaining steps than before
				if (board.isWalkable(newRow, newCol) && isFree(newRow, newCol)
						&& bestRemaining[newRow][newCol] < remainingSteps - 1) {
					bestRemaining[newRow][newCol] = remainingSteps - 1;
					reachable[newRow][newCol] = remainingSteps;
					queue.add(new int[] {newRow, newCol, remainingSteps - 1});
				}
			}
		}

		// Staying still is not a move
		reachable[srcPos[0]][srcPos[1]] = 0;

		return reachable;
	}

	// Clue questions =========================================================

	/*
	 * A player can ask a clue question once per turn, about the room they are in (except the accusation room),
	 * after entering it, or at the start of the turn if their pawn was moved there by someone else's question.
	 */
	public boolean canAskClue(Player player) {
		Room room = getRoomOf(player);
		return isTurnOf(player)
				&& room != null
				&& !board.isAccusationRoom(room)
				&& !hasAskedClue
				&& !isWaitingForAnswer()
				&& (hasMoved || (player.wasMoved() && diceResult < 0));
	}

	/*
	 * Asks a clue question (a "suggestion" in the official rules).
	 * The pawn of the named character is moved into the room. Then the other players are asked in turn order,
	 * starting from the next one: the first one owning at least one of the clues must show one of them.
	 * Eliminated players still answer.
	 */
	public ClueQuestion makeAccusation(Player player, Room room, Character character, Weapon weapon) {
		if (!canAskClue(player))
			throw new IllegalStateException(player.getUsername() + " can't ask a clue question now");
		if (!room.equals(getRoomOf(player)))
			throw new IllegalArgumentException("The clue question must be about the room the player is in");

		this.hasAskedClue = true;

		// The pawn of the named character is moved into the room
		for (Player p : players) {
			if (character.equals(p.getCharacter()) && !p.equals(player) && !room.equals(getRoomOf(p))) {
				moveToRoom(p, room);
				p.setMoved(true);
			}
		}

		// Ask the other players, in turn order
		for (int offset = 1; offset < players.size(); offset++) {
			Player other = players.get((currentPlayer + offset) % players.size());
			List<Clue> options = new ArrayList<>();
			for (Clue clue : List.of(character, weapon, room)) {
				if (other.getClues().contains(clue))
					options.add(clue);
			}
			if (!options.isEmpty()) {
				this.clueQuestion = new ClueQuestion(player, character, weapon, room, other, options);
				return this.clueQuestion;
			}
		}

		// Nobody could answer
		this.clueQuestion = new ClueQuestion(player, character, weapon, room, null, List.of());
		player.onClueQuestionAnswered(this.clueQuestion);
		return this.clueQuestion;
	}

	public boolean isWaitingForAnswer() {
		return clueQuestion != null && !clueQuestion.isAnswered();
	}

	// The answerer shows one of the clues to the player who asked the question
	public void answerClueQuestion(Player answerer, Clue clue) {
		if (!isWaitingForAnswer())
			throw new IllegalStateException("There is no clue question to answer");
		if (!answerer.equals(clueQuestion.getAnswerer()))
			throw new IllegalArgumentException(answerer.getUsername() + " is not the one who must answer");
		if (!clueQuestion.getAnswerOptions().contains(clue))
			throw new IllegalArgumentException("The clue must be one of the asked clues owned by " + answerer.getUsername());

		clueQuestion.setShownClue(clue);
		clueQuestion.getAsker().onClueQuestionAnswered(clueQuestion);
	}

	// Final accusation & turns ===============================================

	public boolean canMakeFinalAccusation(Player player) {
		return isTurnOf(player)
				&& !isWaitingForAnswer()
				&& (!ACCUSATION_ONLY_IN_ACCUSATION_ROOM || isInAccusationRoom(player));
	}

	/*
	 * Correct? The game ends, and the player wins.
	 * Wrong? The player is eliminated and the turn passes to the next player. If every player is eliminated,
	 * the game ends without a winner.
	 */
	public boolean makeFinalAccusation(Player player, Character who, Weapon what, Room where) {
		if (!canMakeFinalAccusation(player))
			throw new IllegalStateException(player.getUsername() + " can't make the final accusation now");

		if (murderEnvelope.makeAccusation(who, what, where)) {
			this.winner = player;
			this.over = true;
			return true;
		}

		player.setEliminated(true);
		boolean allEliminated = true;
		for (Player p : players) {
			allEliminated &= p.isEliminated();
		}
		if (allEliminated) {
			this.over = true;
		}
		else {
			nextPlayer();
		}
		return false;
	}

	public boolean canEndTurn(Player player) {
		return isTurnOf(player) && !isWaitingForAnswer();
	}

	// Passes the turn to the next player that is not eliminated
	public void nextPlayer() {
		if (over)
			throw new IllegalStateException("The game is over");
		if (isWaitingForAnswer())
			throw new IllegalStateException("The clue question must be answered first");

		// The "moved by a clue question" bonus lasts until the end of the player's turn
		getCurrentPlayer().setMoved(false);
		do {
			currentPlayer = (currentPlayer + 1) % players.size();
		} while (getCurrentPlayer().isEliminated());

		resetTurn();
	}

	private void resetTurn() {
		this.diceResult = -1;
		this.hasMoved = false;
		this.hasAskedClue = false;
		this.clueQuestion = null;
	}

	// Strings ================================================================

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
					if (playerCells[y][x] == getCurrentPlayer().getTurn())
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

	public String toStringBoardWithAvailableMoves(int iPlayer, int steps)
	{
		return toStringBoardWithAvailableMoves(iPlayer, steps, false);
	}

	public String toStringBoardWithAvailableMoves(int iPlayer, int steps, boolean printRemainingSteps) {
		StringBuilder res = new StringBuilder();
		CellType[][] cells = board.getCells();
		int[][] destList = this.getAvailableDestinations(players.get(iPlayer).getPosition(), steps);

		for (int y = 0; y < board.getSize()[0]; y++) {
			for (int x = 0; x < board.getSize()[1]; x++) {
				String value;
				if (playerCells[y][x] != -1) {
					if (playerCells[y][x] == getCurrentPlayer().getTurn())
						value = "[" + playerCells[y][x] + "]";
					else
						value = "{" + playerCells[y][x] + "}";
				}
				else if (destList[y][x] > 0) {
					value = "(" + (printRemainingSteps ? destList[y][x]-1 : cells[y][x].getRepresentation()) + ")";
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
		if (over) {
			return "Game over. " + (winner == null ? "Nobody won." : winner.getUsername() + " won!") + "\n"
					+ murderEnvelope;
		}

		Player player = getCurrentPlayer();
		Room room = getRoomOf(player);
		return "Player turn: " + player.getTurn() + ") " + player.getUsername()
				+ (player.getCharacter() == null ? "" : " (" + player.getCharacter().getName() + ")") + "\n"
				+ "Position: " + (room == null ? "corridor" : room.getName())
				+ " [" + player.getPosition()[0] + "," + player.getPosition()[1] + "]\n"
				+ "Dices: " + (diceResult < 0 ? "not rolled" : diceResult);
	}

	public String toStringActions() {
        return "1) print game state\n" +
                "2) move\n" +
				"3) open clue sheet\n" +
				"4) open notebook\n" +
				"5) ask clue\n" +
				"6) make final accusation\n" +
				"N) end turn\n" +
				"Choice: ";
	}
}
