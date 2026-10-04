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

import java.net.ServerSocket;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

/*
 * A real host (ServerStream) and two clients (ClientStream) on localhost play a random game:
 * every copy of the game must stay in sync with the host's one.
 */
class NetworkGameTest {
	private static final String HOST = "host";
	private static final List<String> CLIENTS = List.of("alice", "bob");

	// Everything coming from the network is handled on the test thread, in order
	private final LinkedBlockingQueue<Runnable> tasks = new LinkedBlockingQueue<>();
	private final Map<String, GameClient> gameClients = new HashMap<>();
	private final Map<String, ClientStream> streams = new HashMap<>();
	private GameServer gameServer;
	private ServerStream server;

	@Test
	void randomGameOverTheNetwork() throws Exception {
		connect();
		Random random = new Random(7);
		GameCluedo game = gameServer.getGame();

		chooseCharacters();
		for (int step = 0; step < 80 && !game.isOver(); step++) {
			if (game.isWaitingForAnswer()) {
				GameClient answerer = gameClients.get(game.getClueQuestion().getAnswerer().getUsername());
				ClueQuestion question = answerer.getGame().getClueQuestion();
				answerer.showClue(question.getAnswerOptions().get(0));
			}
			else {
				act(gameClients.get(game.getCurrentPlayer().getUsername()), random);
			}
			waitQuiet();
			assertInSync();
		}

		// A client vanishes without saying goodbye: the others are told and get the clues
		Player bob = findPlayer(game, "bob");
		closeAbruptly("bob");
		waitQuiet();
		assertTrue(bob.hasLeft(), "the host noticed the lost connection");
		for (String username : List.of(HOST, "alice")) {
			Player copy = findPlayer(gameClients.get(username).getGame(), "bob");
			assertTrue(copy.hasLeft());
			assertEquals(new HashSet<>(bob.getClues()), new HashSet<>(copy.getClues()));
		}
		gameClients.remove("bob");
		assertInSync();
		server.sendClose();
	}

	private void connect() throws Exception {
		int port;
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		}
		server = new ServerStream(HOST, port, 2, 6, true);
		server.startServer();

		for (String username : CLIENTS) {
			ClientStream client = new ClientStream(username, "127.0.0.1", port);
			streams.put(username, client);
			GameClient gameClient = new GameClient(request -> client.sendGameMessage(request.toJson()));
			gameClients.put(username, gameClient);
			client.setGameMessageHandler(msg -> tasks.add(() -> gameClient.apply(GameMessage.fromJson(msg.getContent()))));
			client.setReadyMessageHandler(msg -> {});
			client.startClient();
		}
		// Wait for the connections, then everybody is ready
		for (int i = 0; i < 100 && server.getUsernames().size() < 3; i++)
			Thread.sleep(50);
		assertEquals(3, server.getUsernames().size(), "both clients connected");

		GameClient hostClient = new GameClient(request -> tasks.add(() -> gameServer.handleMessage(HOST, request)));
		gameClients.put(HOST, hostClient);
		gameServer = new GameServer(server.getUsernames(), (username, message) -> {
			if (username.equals(HOST))
				tasks.add(() -> hostClient.apply(message));
			else
				server.sendGameMessage(username, message.toJson());
		}, new Random(3));
		server.setGameMessageHandler(msg -> tasks.add(() -> gameServer.handleMessage(msg.getUsername(), GameMessage.fromJson(msg.getContent()))));
		server.setDisconnectMessageHandler(msg -> tasks.add(() -> gameServer.handleDisconnect(msg.getUsername())));
		server.startGame();
		gameServer.start();
		waitQuiet();
	}

	private void chooseCharacters() throws Exception {
		GameClient any = gameClients.get(HOST);
		while (!any.isStarted()) {
			GameClient choosing = gameClients.get(any.getChoosingPlayer().getUsername());
			choosing.chooseCharacter(choosing.getGame().getAvailableCharacters().get(0));
			waitQuiet();
		}
		assertInSync();
	}

	private void act(GameClient client, Random random) {
		GameCluedo copy = client.getGame();
		Player me = client.getMe();
		if (copy.canAskClue(me))
			client.askClue(pick(copy, Character.class, random), pick(copy, Weapon.class, random));
		else if (copy.canRollDices(me))
			client.rollDice();
		else if (copy.canMove(me)) {
			int[][] destinations = copy.getCurrentDestinations();
			List<int[]> cells = new ArrayList<>();
			for (int y = 0; y < destinations.length; y++)
				for (int x = 0; x < destinations[y].length; x++)
					if (destinations[y][x] > 0) cells.add(new int[] {y, x});
			if (cells.isEmpty()) client.endTurn();
			else client.move(cells.get(random.nextInt(cells.size())));
		}
		else if (copy.canMakeFinalAccusation(me) && random.nextInt(3) == 0)
			client.finalAccusation(pick(copy, Character.class, random), pick(copy, Weapon.class, random), pick(copy, Room.class, random));
		else
			client.endTurn();
	}

	// Runs the queued tasks until nothing arrives for a while
	private void waitQuiet() throws InterruptedException {
		Runnable task;
		while ((task = tasks.poll(80, TimeUnit.MILLISECONDS)) != null)
			task.run();
	}

	private void closeAbruptly(String username) throws Exception {
		// Close the socket of the client without sending DISCONNECT
		java.lang.reflect.Field listenerField = ClientStream.class.getDeclaredField("clientListener");
		listenerField.setAccessible(true);
		Object listener = listenerField.get(streams.get(username));
		java.lang.reflect.Field socketField = listener.getClass().getDeclaredField("socket");
		socketField.setAccessible(true);
		((java.net.Socket) socketField.get(listener)).close();
	}

	private void assertInSync() {
		GameCluedo real = gameServer.getGame();
		for (GameClient client : gameClients.values()) {
			GameCluedo copy = client.getGame();
			for (int i = 0; i < real.getPlayers().size(); i++) {
				Player r = real.getPlayers().get(i);
				Player c = copy.getPlayers().get(i);
				assertArrayEquals(r.getPosition(), c.getPosition());
				assertEquals(r.getCharacter(), c.getCharacter());
				assertEquals(r.isEliminated(), c.isEliminated());
			}
			assertEquals(real.isOver(), copy.isOver());
			if (!real.isOver()) {
				assertEquals(real.getCurrentPlayer().getUsername(), copy.getCurrentPlayer().getUsername());
				assertEquals(real.getDiceResult(), copy.getDiceResult());
			}
		}
	}

	private static Player findPlayer(GameCluedo game, String username) {
		for (Player p : game.getPlayers())
			if (p.getUsername().equals(username)) return p;
		throw new AssertionError(username);
	}

	private static <T extends Clue> T pick(GameCluedo game, Class<T> type, Random random) {
		List<T> list = new ArrayList<>();
		for (Clue clue : game.getTotalCluesList())
			if (type.isInstance(clue)) list.add(type.cast(clue));
		return list.get(random.nextInt(list.size()));
	}
}
