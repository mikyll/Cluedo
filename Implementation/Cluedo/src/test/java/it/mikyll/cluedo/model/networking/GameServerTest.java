package it.mikyll.cluedo.model.networking;

import static org.junit.jupiter.api.Assertions.*;

import it.mikyll.cluedo.model.game.ClueQuestion;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.networking.message.GameMessage;
import it.mikyll.cluedo.model.networking.message.GameMessageType;

import java.util.*;

import org.junit.jupiter.api.Test;

/*
 * A host and its clients connected through in-memory queues: random games are played through requests and events,
 * and after every event each client's copy of the game must match the host's game.
 */
class GameServerTest {
	private static final int MAX_STEPS = 5_000;

	private GameServer server;
	private final Map<String, GameClient> clients = new LinkedHashMap<>();
	// Messages are delivered in order, after the current one has been handled (like on a network)
	private final Deque<Runnable> network = new ArrayDeque<>();
	private final Map<String, List<GameMessage>> received = new HashMap<>();
	private final Set<String> disconnected = new HashSet<>();

	private void setUp(int numPlayers, long seed) {
		List<String> usernames = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++)
			usernames.add("user" + i);

		server = new GameServer(usernames, (username, message) -> {
			if (disconnected.contains(username))
				return;
			// Messages go through JSON, as on the network
			String json = message.toJson();
			network.add(() -> {
				GameMessage event = GameMessage.fromJson(json);
				received.computeIfAbsent(username, k -> new ArrayList<>()).add(event);
				clients.get(username).apply(event);
			});
		}, new Random(seed));

		for (String username : usernames) {
			clients.put(username, new GameClient(request -> {
				String json = request.toJson();
				network.add(() -> server.handleMessage(username, GameMessage.fromJson(json)));
			}));
		}
		server.start();
	}

	private void deliverAll() {
		while (!network.isEmpty()) {
			network.poll().run();
			assertCopiesMatch();
		}
	}

	@Test
	void randomGamesKeepEveryCopyInSync() {
		for (int seed = 0; seed < 40; seed++) {
			network.clear(); received.clear(); clients.clear(); disconnected.clear();
			setUp(2 + seed % 5, seed);
			playRandomly(new Random(seed), false);
			assertTrue(server.getGame().isOver() || stepsExhausted, "seed " + seed);
		}
	}

	@Test
	void playersCanLeaveDuringTheGame() {
		for (int seed = 0; seed < 40; seed++) {
			network.clear(); received.clear(); clients.clear(); disconnected.clear();
			setUp(3 + seed % 4, seed);
			playRandomly(new Random(seed), true);
		}
	}

	@Test
	void eachPlayerOnlyKnowsTheirOwnClues() {
		setUp(4, 1);
		deliverAll();
		for (GameClient client : clients.values()) {
			for (Player p : client.getGame().getPlayers()) {
				Player real = serverPlayer(p.getUsername());
				if (p.equals(client.getMe()))
					assertEquals(new HashSet<>(real.getClues()), new HashSet<>(p.getClues()));
				else
					assertTrue(p.getClues().isEmpty(), "the clues of " + p.getUsername() + " must be secret");
			}
			assertNull(client.getGame().getMurderEnvelope());
		}
	}

	@Test
	void leavingRevealsTheClues() {
		setUp(3, 2);
		deliverAll();
		String leaving = server.getGame().getPlayers().get(1).getUsername();
		disconnected.add(leaving);
		server.handleDisconnect(leaving);
		deliverAll();

		Player real = serverPlayer(leaving);
		for (GameClient client : clients.values()) {
			if (disconnected.contains(client.getMe().getUsername()))
				continue;
			Player copy = client.getGame().getPlayers().get(server.getGame().getPlayers().indexOf(real));
			assertTrue(copy.hasLeft());
			assertEquals(new HashSet<>(real.getClues()), new HashSet<>(copy.getClues()));
		}
	}

	@Test
	void requestsOfOtherPlayersAreRefused() {
		setUp(3, 3);
		deliverAll();
		chooseAllCharacters();
		Player notCurrent = server.getGame().getPlayers().get(1);
		clients.get(notCurrent.getUsername()).rollDice();
		deliverAll();

		assertTrue(server.getGame().getDiceResult() < 0, "only the current player can roll the dices");
		List<GameMessage> messages = received.get(notCurrent.getUsername());
		assertEquals(GameMessageType.ERROR, messages.get(messages.size() - 1).getType());
	}

	// Random play ============================================================
	private boolean stepsExhausted;

	private void playRandomly(Random random, boolean withDisconnections) {
		stepsExhausted = false;
		deliverAll();
		chooseAllCharacters();
		GameCluedo game = server.getGame();
		for (int step = 0; step < MAX_STEPS && !game.isOver(); step++) {
			if (withDisconnections && random.nextInt(150) == 0) {
				List<Player> active = new ArrayList<>();
				for (Player p : game.getPlayers())
					if (!p.hasLeft()) active.add(p);
				String leaving = active.get(random.nextInt(active.size())).getUsername();
				disconnected.add(leaving);
				server.handleDisconnect(leaving);
				deliverAll();
				continue;
			}
			// The current player (or the one who must answer) decides on their own copy of the game
			if (game.isWaitingForAnswer()) {
				GameClient answerer = clients.get(game.getClueQuestion().getAnswerer().getUsername());
				ClueQuestion question = answerer.getGame().getClueQuestion();
				answerer.showClue(question.getAnswerOptions().get(random.nextInt(question.getAnswerOptions().size())));
			}
			else {
				playRandomAction(clients.get(game.getCurrentPlayer().getUsername()), random);
			}
			deliverAll();
		}
		stepsExhausted = !game.isOver();
	}

	private void playRandomAction(GameClient client, Random random) {
		GameCluedo copy = client.getGame();
		Player me = client.getMe();
		if (copy.canMakeFinalAccusation(me) && random.nextInt(4) == 0) {
			client.finalAccusation(pick(copy, Character.class, random), pick(copy, Weapon.class, random), pick(copy, Room.class, random));
		}
		else if (copy.canAskClue(me)) {
			client.askClue(pick(copy, Character.class, random), pick(copy, Weapon.class, random));
		}
		else if (copy.canRollDices(me) && random.nextBoolean()) {
			client.rollDice();
		}
		else if (copy.canMove(me)) {
			int[][] destinations = copy.getCurrentDestinations();
			List<int[]> cells = new ArrayList<>();
			for (int y = 0; y < destinations.length; y++)
				for (int x = 0; x < destinations[y].length; x++)
					if (destinations[y][x] > 0) cells.add(new int[] {y, x});
			if (cells.isEmpty())
				client.endTurn();
			else
				client.move(cells.get(random.nextInt(cells.size())));
		}
		else if (copy.canRollDices(me)) {
			client.rollDice();
		}
		else {
			client.endTurn();
		}
	}

	private void chooseAllCharacters() {
		while (!clients.values().iterator().next().isStarted()) {
			Player choosing = null;
			for (GameClient c : clients.values())
				if (!disconnected.contains(c.getMe().getUsername())) { choosing = c.getChoosingPlayer(); break; }
			GameClient client = clients.get(choosing.getUsername());
			client.chooseCharacter(client.getGame().getAvailableCharacters().get(0));
			deliverAll();
		}
	}

	private static <T extends Clue> T pick(GameCluedo game, Class<T> type, Random random) {
		List<T> list = new ArrayList<>();
		for (Clue clue : game.getTotalCluesList())
			if (type.isInstance(clue)) list.add(type.cast(clue));
		return list.get(random.nextInt(list.size()));
	}

	// Checks =================================================================
	private Player serverPlayer(String username) {
		for (Player p : server.getGame().getPlayers())
			if (p.getUsername().equals(username)) return p;
		throw new AssertionError(username);
	}

	private void assertCopiesMatch() {
		if (!network.isEmpty())
			return;  // check only when every message has been delivered
		GameCluedo real = server.getGame();
		for (GameClient client : clients.values()) {
			if (disconnected.contains(client.getMe().getUsername()) || client.getGame() == null)
				continue;
			GameCluedo copy = client.getGame();
			for (int i = 0; i < real.getPlayers().size(); i++) {
				Player r = real.getPlayers().get(i);
				Player c = copy.getPlayers().get(i);
				assertEquals(r.getUsername(), c.getUsername());
				assertArrayEquals(r.getPosition(), c.getPosition(), "position of " + r.getUsername());
				assertEquals(r.getCharacter(), c.getCharacter());
				assertEquals(r.isEliminated(), c.isEliminated());
				assertEquals(r.hasLeft(), c.hasLeft());
			}
			assertEquals(real.isOver(), copy.isOver());
			if (!real.isOver()) {
				assertEquals(real.getCurrentPlayer().getUsername(), copy.getCurrentPlayer().getUsername());
				assertEquals(real.getDiceResult(), copy.getDiceResult());
				assertEquals(real.isWaitingForAnswer(), copy.isWaitingForAnswer());
			}
			else {
				assertEquals(real.getWinner() == null ? null : real.getWinner().getUsername(),
						copy.getWinner() == null ? null : copy.getWinner().getUsername());
				assertNotNull(copy.getMurderEnvelope(), "the envelope is revealed at the end");
			}
			// A shown clue is known only by the asker and the answerer
			ClueQuestion question = copy.getClueQuestion();
			if (question != null && question.getShownClue() != null) {
				Player me = client.getMe();
				assertTrue(me.getUsername().equals(question.getAsker().getUsername())
						|| me.getUsername().equals(question.getAnswerer().getUsername()));
			}
		}
	}
}
