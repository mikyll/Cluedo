package it.mikyll.cluedo.model.game.player;

import it.mikyll.cluedo.model.game.ClueQuestion;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.actions.*;
import it.mikyll.cluedo.model.game.board.Board;
import it.mikyll.cluedo.model.game.board.Door;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.ClueType;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;

import java.util.*;

/*
 * Player controller by Artificial Intelligence.
 *
 * Simple strategy:
 * - remember every clue seen (own clues and clues shown by the others): those are not in the murder envelope;
 * - go to the nearest room that could still be the murder room, and ask about clues that are still unknown;
 * - if nobody can answer a question, the asked clues not owned by the bot are in the envelope;
 * - once a single clue per type is left, go to the accusation room and make the final accusation.
 */
public class PlayerArtificial extends Player {
	// Clues that are surely NOT in the murder envelope
	private final Set<Clue> knownClues = new HashSet<>();
	// Clues that are surely in the murder envelope
	private final Set<Clue> envelopeClues = new HashSet<>();
	private final Random random;

	public PlayerArtificial(String nickname) {
		this(nickname, new Random());
	}

	public PlayerArtificial(String nickname, Random random) {
		super(nickname);
		this.random = random;
	}

	private String getRandomNickname() {
		// returns a random nickname

		return "";
	}

	// Knowledge ==============================================================

	@Override
	public void onClueQuestionAnswered(ClueQuestion question) {
		if (question.getShownClue() != null) {
			knownClues.add(question.getShownClue());
			return;
		}
		// Nobody could answer: the asked clues the bot doesn't own are in the envelope
		for (Clue clue : List.of(question.getCharacter(), question.getWeapon(), question.getRoom())) {
			if (!getClues().contains(clue))
				envelopeClues.add(clue);
		}
	}

	// Clues of the given type that could still be in the envelope
	private <T extends Clue> List<T> getCandidates(GameCluedo game, Class<T> type) {
		List<T> result = new ArrayList<>();
		for (Clue clue : game.getTotalCluesList()) {
			if (type.isInstance(clue) && !knownClues.contains(clue) && !getClues().contains(clue)) {
				if (envelopeClues.contains(clue))
					return List.of(type.cast(clue));
				result.add(type.cast(clue));
			}
		}
		return result;
	}

	public boolean knowsSolution(GameCluedo game) {
		return getCandidates(game, Character.class).size() == 1
				&& getCandidates(game, Weapon.class).size() == 1
				&& getCandidates(game, Room.class).size() == 1;
	}

	// Decisions ==============================================================

	/*
	 * Returns the next action the bot wants to perform in its turn.
	 * The caller executes it and asks again, until the turn passes to another player.
	 */
	public GameAction nextAction(GameCluedo game) {
		if (knowsSolution(game) && game.canMakeFinalAccusation(this)) {
			return new ActionFinalAccusation(game, this,
					getCandidates(game, Room.class).get(0),
					getCandidates(game, Character.class).get(0),
					getCandidates(game, Weapon.class).get(0));
		}

		if (game.canAskClue(this) && !knowsSolution(game)) {
			return new ActionAskClue(game, this, pick(game, Character.class), pick(game, Weapon.class));
		}

		if (game.canMove(this) && game.getDiceResult() < 0) {
			// Use the trapdoor if it leads to an interesting room
			Room destination = game.getTrapdoorDestination(this);
			if (getTargetRooms(game).contains(destination))
				return new ActionMove(game, this, -1, getPosition(), destination.getCells().get(0));
		}

		if (game.canRollDices(this))
			return new ActionRollDice(game, this);

		if (game.canMove(this) && game.getDiceResult() >= 0) {
			int[] destination = chooseDestination(game);
			if (destination != null)
				return new ActionMove(game, this, game.getDiceResult(), getPosition(), destination);
		}

		return new ActionEndTurn(game, this);
	}

	// Which clue to show when answering a question: any of the options is fine
	public Clue chooseClueToShow(ClueQuestion question) {
		List<Clue> options = question.getAnswerOptions();
		return options.get(random.nextInt(options.size()));
	}

	// Prefers clues that could still be in the envelope
	private <T extends Clue> T pick(GameCluedo game, Class<T> type) {
		List<T> candidates = getCandidates(game, type);
		if (!candidates.isEmpty())
			return candidates.get(random.nextInt(candidates.size()));

		List<T> all = new ArrayList<>();
		for (Clue clue : game.getTotalCluesList()) {
			if (type.isInstance(clue))
				all.add(type.cast(clue));
		}
		return all.get(random.nextInt(all.size()));
	}

	// Rooms worth visiting: the accusation room once the solution is known, otherwise the possible murder rooms
	private Set<Room> getTargetRooms(GameCluedo game) {
		Board board = game.getBoard();
		Set<Room> targets = new HashSet<>();
		if (knowsSolution(game)) {
			targets.add(board.getRoomById(board.getAccusationRoomId()));
		}
		else {
			targets.addAll(getCandidates(game, Room.class));
		}
		targets.remove(game.getRoomOf(this));

		// The murder room may be known (and be the current one) while the character or the weapon are not:
		// any other room is fine to keep asking questions
		if (targets.isEmpty() && !knowsSolution(game)) {
			targets.addAll(board.getClueRooms());
			targets.remove(game.getRoomOf(this));
		}
		return targets;
	}

	/*
	 * Enters a target room if possible, otherwise gets as close as possible to the door of one.
	 */
	private int[] chooseDestination(GameCluedo game) {
		Board board = game.getBoard();
		int[][] destinations = game.getCurrentDestinations();
		Set<Room> targets = getTargetRooms(game);
		if (targets.isEmpty())
			return null;

		int[][] distances = getDistancesToDoors(board, targets);
		int[] best = null;
		int bestDistance = Integer.MAX_VALUE;
		for (int y = 0; y < destinations.length; y++) {
			for (int x = 0; x < destinations[y].length; x++) {
				if (destinations[y][x] <= 0)
					continue;

				Room room = board.getRoomAt(y, x);
				int distance;
				if (room != null)
					distance = targets.contains(room) ? -1 : Integer.MAX_VALUE;
				else
					distance = distances[y][x] < 0 ? Integer.MAX_VALUE : distances[y][x];

				if (distance < bestDistance) {
					bestDistance = distance;
					best = new int[] {y, x};
				}
			}
		}
		return best;
	}

	// Walking distance (ignoring pawns) from every corridor cell to the nearest door of the given rooms, -1 if unreachable
	private static int[][] getDistancesToDoors(Board board, Set<Room> rooms) {
		int rows = board.getSize()[0];
		int cols = board.getSize()[1];
		int[][] distances = new int[rows][cols];
		for (int[] row : distances) {
			Arrays.fill(row, -1);
		}

		Queue<int[]> queue = new LinkedList<>();
		for (Room room : rooms) {
			for (Door door : board.getDoors(room)) {
				int[] pos = door.getPosition();
				distances[pos[0]][pos[1]] = 0;
				queue.add(pos);
			}
		}

		int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
		while (!queue.isEmpty()) {
			int[] current = queue.poll();
			for (int[] dir : directions) {
				int y = current[0] + dir[0];
				int x = current[1] + dir[1];
				if (board.isInBounds(y, x) && board.isWalkable(y, x) && distances[y][x] < 0) {
					distances[y][x] = distances[current[0]][current[1]] + 1;
					queue.add(new int[] {y, x});
				}
			}
		}
		return distances;
	}
}
