package it.mikyll.cluedo.model.game;

import static org.junit.jupiter.api.Assertions.*;

import it.mikyll.cluedo.model.game.actions.ActionMove;
import it.mikyll.cluedo.model.game.actions.ActionRollDice;
import it.mikyll.cluedo.model.game.board.Board;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.ClueType;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerHuman;
import it.mikyll.cluedo.persistence.AssetLoader;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class GameCluedoTest {
	// Cells of board.json used by the tests
	private static final int[] CORRIDOR = {5, 3};
	private static final int[] BELOW_STUDY_DOOR = {5, 6};
	private static final int[] STUDY_DOOR = {4, 6};
	private static final int[] NEXT_TO_STUDY_WALL = {4, 5};
	private static final int[] STUDY_CELL = {1, 1};
	private static final int[] FAR_AWAY_1 = {16, 20};
	private static final int[] FAR_AWAY_2 = {17, 20};

	private GameCluedo game;
	private Board board;
	// In turn order
	private Player p1;
	private Player p2;
	private Player p3;

	@BeforeEach
	void setUp() {
		List<Player> players = new ArrayList<>(List.of(new PlayerHuman("alice"), new PlayerHuman("bob"), new PlayerHuman("carol")));
		game = new GameCluedo(players, 2, new Random(42));
		game.prepareGame();
		board = game.getBoard();
		p1 = game.getPlayers().get(0);
		p2 = game.getPlayers().get(1);
		p3 = game.getPlayers().get(2);

		// Characters in characters.json order: p1 = scarlet, p2 = mustard, p3 = white
		game.setPlayerCharacter(0, 0);
		game.setPlayerCharacter(1, 0);
		game.setPlayerCharacter(2, 0);

		// Known positions, hands and solution
		game.movePlayer(p1, CORRIDOR);
		game.movePlayer(p2, FAR_AWAY_1);
		game.movePlayer(p3, FAR_AWAY_2);
		game.setMurderEnvelope(new MurderEnvelope(character("plum"), weapon("Rope"), room("kitchen")));
		hand(p1, character("green"), weapon("Wrench"));
		hand(p2, room("study"), weapon("Pistol"));
		hand(p3, character("peacock"), room("library"));
	}

	// Clues ==================================================================
	@Test
	void deckHas21CluesWithoutAccusationRoom() {
		List<Clue> clues = game.getTotalCluesList();
		assertEquals(21, clues.size());
		assertEquals(9, clues.stream().filter(c -> c.getType() == ClueType.ROOM).count());
		assertFalse(clues.contains(board.getRoomById(board.getAccusationRoomId())));
	}

	@Test
	void cluesFromDifferentLoadsAreEqual() {
		Character first = AssetLoader.loadCharacters().get(0);
		Character second = AssetLoader.loadCharacters().get(0);
		List<Clue> list = new ArrayList<>(List.of(first));

		assertEquals(first, second);
		assertEquals(first.hashCode(), second.hashCode());
		assertTrue(list.remove(second));
	}

	@ParameterizedTest
	@ValueSource(ints = {2, 3, 4, 5, 6})
	void everyClueIsDealtOrInTheEnvelope(int numPlayers) {
		List<Player> players = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++) {
			players.add(new PlayerHuman("player" + i));
		}
		GameCluedo g = new GameCluedo(players, 2, new Random(numPlayers));
		g.prepareGame();

		List<Clue> all = new ArrayList<>();
		MurderEnvelope envelope = g.getMurderEnvelope();
		all.addAll(List.of(envelope.getMurderer(), envelope.getWeapon(), envelope.getRoom()));
		int min = Integer.MAX_VALUE;
		int max = 0;
		for (Player p : g.getPlayers()) {
			all.addAll(p.getClues());
			min = Math.min(min, p.getClues().size());
			max = Math.max(max, p.getClues().size());
		}

		assertEquals(21, all.size(), "no clue lost or duplicated");
		assertEquals(new HashSet<>(g.getTotalCluesList()), new HashSet<>(all));
		assertTrue(max - min <= 1, "hand sizes differ by more than one");
	}

	@Test
	void envelopeChangesWithSeed() {
		Set<String> solutions = new HashSet<>();
		for (int seed = 0; seed < 20; seed++) {
			GameCluedo g = new GameCluedo(new ArrayList<>(List.of(new PlayerHuman("a"), new PlayerHuman("b"))), 2, new Random(seed));
			g.prepareGame();
			solutions.add(g.getMurderEnvelope().toString());
		}
		assertTrue(solutions.size() > 1);
	}

	// Dices ==================================================================
	@Test
	void twoDicesGoFrom2To12() {
		Set<Integer> values = new HashSet<>();
		for (int i = 0; i < 10_000; i++) {
			int value = game.rollDices();
			assertTrue(value >= 2 && value <= 12, "out of range: " + value);
			values.add(value);
		}
		assertEquals(11, values.size(), "every value from 2 to 12 must occur");
	}

	@Test
	void dicesCanBeRolledOncePerTurn() {
		assertTrue(game.canRollDices(p1));
		assertFalse(game.canRollDices(p2));
		game.rollDicesForTurn();
		assertFalse(game.canRollDices(p1));
		assertThrows(IllegalStateException.class, game::rollDicesForTurn);
	}

	// Board ==================================================================
	@Test
	void trapdoorsLinkOppositeCorners() {
		assertEquals(room("kitchen"), board.getTrapdoorDestination(room("study")));
		assertEquals(room("study"), board.getTrapdoorDestination(room("kitchen")));
		assertEquals(room("living_room"), board.getTrapdoorDestination(room("courtyard")));
		assertNull(board.getTrapdoorDestination(room("library")));
	}

	// Movement ===============================================================
	@Test
	void oneStepReachesAdjacentCorridorCells() {
		int[][] reachable = game.getAvailableDestinations(CORRIDOR, 1);

		assertEquals(Set.of("4,3", "5,2", "5,4"), reachableCells(reachable));
	}

	@Test
	void roomIsEnteredOnlyThroughItsDoor() {
		assertFalse(isReachable(game.getAvailableDestinations(NEXT_TO_STUDY_WALL, 1), STUDY_CELL),
				"the room wall can't be crossed");
		assertFalse(isReachable(game.getAvailableDestinations(BELOW_STUDY_DOOR, 1), STUDY_CELL),
				"one step only reaches the door");
		assertTrue(isReachable(game.getAvailableDestinations(BELOW_STUDY_DOOR, 2), STUDY_CELL));
	}

	@Test
	void pawnsBlockTheWay() {
		int[] target = {5, 5};
		assertTrue(isReachable(game.getAvailableDestinations(CORRIDOR, 2), target));

		game.movePlayer(p2, new int[] {5, 4});

		assertFalse(isReachable(game.getAvailableDestinations(CORRIDOR, 2), target));
		assertFalse(isReachable(game.getAvailableDestinations(CORRIDOR, 2), new int[] {5, 4}));
	}

	@Test
	void pawnLeavesRoomFromDoorAndCantComeBack() {
		game.movePlayer(p1, STUDY_CELL);

		int[][] oneStep = game.getAvailableDestinations(STUDY_CELL, 1);
		assertEquals(Set.of(STUDY_DOOR[0] + "," + STUDY_DOOR[1]), reachableCells(oneStep));

		int[][] manySteps = game.getAvailableDestinations(STUDY_CELL, 6);
		assertTrue(isReachable(manySteps, BELOW_STUDY_DOOR));
		for (int[] cell : room("study").getCells()) {
			assertFalse(isReachable(manySteps, cell), "can't enter the same room again");
		}
	}

	@Test
	void moveFollowsDiceResult() {
		game.rollDicesForTurn();
		int steps = game.getDiceResult();

		assertFalse(new ActionMove(game, p1, steps, CORRIDOR, new int[] {0, 23}).isAllowed(), "too far");
		assertFalse(new ActionMove(game, p1, steps + 1, CORRIDOR, new int[] {5, 4}).isAllowed(), "wrong steps");
		ActionMove move = new ActionMove(game, p1, steps, CORRIDOR, new int[] {5, 4});
		assertTrue(move.isAllowed());
		move.execute();

		assertArrayEquals(new int[] {5, 4}, p1.getPosition());
		assertFalse(game.canMove(p1), "only one move per turn");
	}

	@Test
	void trapdoorReplacesTheDices() {
		game.movePlayer(p1, STUDY_CELL);
		assertTrue(game.canMove(p1));

		int[] kitchenCell = room("kitchen").getCells().get(0);
		game.moveCurrentPlayer(kitchenCell);

		assertEquals(room("kitchen"), game.getRoomOf(p1));
		assertFalse(game.canRollDices(p1), "the trapdoor is the movement of the turn");
	}

	@Test
	void cantMoveBeforeRollingInCorridor() {
		assertFalse(game.canMove(p1));
		assertThrows(IllegalArgumentException.class, () -> game.moveCurrentPlayer(new int[] {5, 4}));
	}

	// Clue questions =========================================================
	@Test
	void clueQuestionOnlyAfterEnteringRoom() {
		assertFalse(game.canAskClue(p1), "corridor");
		game.movePlayer(p1, STUDY_CELL);
		assertFalse(game.canAskClue(p1), "must enter the room this turn");

		enterKitchenWithTrapdoor(p1);
		assertTrue(game.canAskClue(p1));
	}

	@Test
	void noClueQuestionInAccusationRoom() {
		// Enter the accusation room from its door at [7,11], which faces down
		game.movePlayer(p1, new int[] {7, 11});
		game.rollDicesForTurn();
		game.moveCurrentPlayer(new int[] {8, 11});

		assertTrue(game.isInAccusationRoom(p1));
		assertFalse(game.canAskClue(p1));
	}

	@Test
	void nextPlayerOwningAClueAnswers() {
		enterKitchenWithTrapdoor(p1);

		ClueQuestion question = game.makeAccusation(p1, room("kitchen"), character("plum"), weapon("Pistol"));

		assertEquals(p2, question.getAnswerer());
		assertEquals(List.of(weapon("Pistol")), question.getAnswerOptions());
		assertTrue(game.isWaitingForAnswer());
	}

	@Test
	void playersWithoutCluesAreSkipped() {
		enterKitchenWithTrapdoor(p1);

		ClueQuestion question = game.makeAccusation(p1, room("kitchen"), character("peacock"), weapon("Rope"));

		assertEquals(p3, question.getAnswerer());
	}

	@Test
	void questionsWrapAroundTheTable() {
		game.nextPlayer();
		game.nextPlayer();
		assertEquals(p3, game.getCurrentPlayer());
		enterKitchenWithTrapdoor(p3);

		ClueQuestion question = game.makeAccusation(p3, room("kitchen"), character("green"), weapon("Rope"));

		assertEquals(p1, question.getAnswerer());
	}

	@Test
	void nobodyAnswers() {
		enterKitchenWithTrapdoor(p1);

		ClueQuestion question = game.makeAccusation(p1, room("kitchen"), character("plum"), weapon("Rope"));

		assertFalse(question.hasAnswerer());
		assertFalse(game.isWaitingForAnswer());
		assertTrue(game.canEndTurn(p1));
	}

	@Test
	void answerMustBeOneOfTheOptions() {
		enterKitchenWithTrapdoor(p1);
		game.makeAccusation(p1, room("kitchen"), character("plum"), weapon("Pistol"));

		assertFalse(game.canEndTurn(p1), "waiting for the answer");
		assertThrows(IllegalArgumentException.class, () -> game.answerClueQuestion(p2, room("study")));
		assertThrows(IllegalArgumentException.class, () -> game.answerClueQuestion(p3, weapon("Pistol")));

		game.answerClueQuestion(p2, weapon("Pistol"));

		assertEquals(weapon("Pistol"), game.getClueQuestion().getShownClue());
		assertTrue(game.canEndTurn(p1));
		assertFalse(game.canAskClue(p1), "one question per turn");
	}

	@Test
	void questionMustBeAboutCurrentRoom() {
		enterKitchenWithTrapdoor(p1);

		assertThrows(IllegalArgumentException.class,
				() -> game.makeAccusation(p1, room("study"), character("plum"), weapon("Rope")));
	}

	@Test
	void namedCharacterIsMovedIntoTheRoom() {
		enterKitchenWithTrapdoor(p1);

		// p3 plays white
		game.makeAccusation(p1, room("kitchen"), character("white"), weapon("Rope"));

		assertEquals(room("kitchen"), game.getRoomOf(p3));
		assertTrue(p3.wasMoved());

		// On their turn, p3 can ask about the kitchen without moving
		game.nextPlayer();
		game.nextPlayer();
		assertEquals(p3, game.getCurrentPlayer());
		assertTrue(game.canAskClue(p3));

		// The bonus is lost at the end of the turn
		game.nextPlayer();
		assertFalse(p3.wasMoved());
	}

	// Final accusation =======================================================
	@Test
	void finalAccusationOnlyFromAccusationRoom() {
		assertFalse(game.canMakeFinalAccusation(p1));
		game.movePlayer(p1, accusationRoomCell());
		assertTrue(game.canMakeFinalAccusation(p1));
	}

	@Test
	void correctFinalAccusationWins() {
		game.movePlayer(p1, accusationRoomCell());

		assertTrue(game.makeFinalAccusation(p1, character("plum"), weapon("Rope"), room("kitchen")));
		assertTrue(game.isOver());
		assertEquals(p1, game.getWinner());
		assertFalse(game.canEndTurn(p1));
	}

	@Test
	void wrongFinalAccusationEliminates() {
		game.movePlayer(p1, accusationRoomCell());

		assertFalse(game.makeFinalAccusation(p1, character("green"), weapon("Rope"), room("kitchen")));

		assertTrue(p1.isEliminated());
		assertFalse(game.isOver());
		assertEquals(p2, game.getCurrentPlayer());

		// Eliminated players are skipped, but still answer clue questions
		game.nextPlayer();
		assertEquals(p3, game.getCurrentPlayer());
		enterKitchenWithTrapdoor(p3);
		ClueQuestion question = game.makeAccusation(p3, room("kitchen"), character("green"), weapon("Rope"));
		assertEquals(p1, question.getAnswerer());
		game.answerClueQuestion(p1, character("green"));
		game.nextPlayer();
		assertEquals(p2, game.getCurrentPlayer());
	}

	@Test
	void everybodyWrongMeansNoWinner() {
		for (Player p : List.of(p1, p2, p3)) {
			game.movePlayer(p, accusationRoomCell());
		}
		game.makeFinalAccusation(p1, character("green"), weapon("Rope"), room("kitchen"));
		game.makeFinalAccusation(p2, character("green"), weapon("Rope"), room("kitchen"));
		assertFalse(game.isOver());
		game.makeFinalAccusation(p3, character("green"), weapon("Rope"), room("kitchen"));

		assertTrue(game.isOver());
		assertNull(game.getWinner());
	}

	@Test
	void rollDiceActionChecksTurn() {
		assertFalse(new ActionRollDice(game, p2).isAllowed());
		ActionRollDice roll = new ActionRollDice(game, p1);
		assertTrue(roll.isAllowed());
		roll.execute();
		assertEquals(game.getDiceResult(), roll.getResult());
	}

	// Helpers ================================================================
	private void enterKitchenWithTrapdoor(Player player) {
		game.movePlayer(player, STUDY_CELL);
		game.moveCurrentPlayer(room("kitchen").getCells().get(0));
	}

	private int[] accusationRoomCell() {
		return board.getRoomById(board.getAccusationRoomId()).getCells().get(0);
	}

	private void hand(Player player, Clue... clues) {
		player.getClues().clear();
		player.getClues().addAll(List.of(clues));
	}

	private Character character(String id) {
		return game.getTotalCluesList().stream()
				.filter(c -> c instanceof Character && ((Character) c).getId().equals(id))
				.map(c -> (Character) c).findFirst().orElseThrow();
	}

	private Weapon weapon(String name) {
		return game.getTotalCluesList().stream()
				.filter(c -> c instanceof Weapon && c.getName().equals(name))
				.map(c -> (Weapon) c).findFirst().orElseThrow();
	}

	private Room room(String id) {
		return Objects.requireNonNull(board.getRoomById(id), id);
	}

	private static boolean isReachable(int[][] reachable, int[] cell) {
		return reachable[cell[0]][cell[1]] > 0;
	}

	private static Set<String> reachableCells(int[][] reachable) {
		Set<String> result = new HashSet<>();
		for (int y = 0; y < reachable.length; y++) {
			for (int x = 0; x < reachable[y].length; x++) {
				if (reachable[y][x] > 0)
					result.add(y + "," + x);
			}
		}
		return result;
	}
}
