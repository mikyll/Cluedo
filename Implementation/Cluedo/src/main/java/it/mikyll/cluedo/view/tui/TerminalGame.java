package it.mikyll.cluedo.view.tui;

import it.mikyll.cluedo.model.game.ClueQuestion;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.actions.*;
import it.mikyll.cluedo.model.game.board.Board;
import it.mikyll.cluedo.model.game.board.CellType;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerHuman;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

/*
 * Terminal version of the game, for human players sharing the same keyboard (hot-seat).
 * Run it with: ./mvnw compile exec:java -Dexec.mainClass=it.mikyll.cluedo.view.tui.TerminalGame
 * An optional seed can be passed to replay the same game: -Dexec.args=42
 */
public class TerminalGame {
	private static final int NUM_DICES = 2;

	private final Scanner scanner;
	private final Random random;
	private GameCluedo game;
	private boolean quit = false;

	public TerminalGame(Scanner scanner, Random random) {
		this.scanner = scanner;
		this.random = random;
	}

	public static void main(String[] args) {
		try {
			Random random = args.length > 0 ? new Random(Long.parseLong(args[0])) : new Random();
			new TerminalGame(new Scanner(System.in), random).run();
		} catch (EndOfInputException e) {
			System.out.println("Bye!");
		}
	}

	private static class EndOfInputException extends RuntimeException {
	}

	public void run() {
		System.out.println("=== CLUEDO ===");

		// Players
		int numPlayers = readInt("Number of players (2-6): ", 2, 6);
		List<Player> players = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++) {
			String name = readLine("Name of player #" + (i + 1) + ": ").trim();
			players.add(new PlayerHuman(name.isEmpty() ? "Player" + (i + 1) : name));
		}

		game = new GameCluedo(players, NUM_DICES, random);
		game.prepareGame();

		// Character selection, in turn order
		System.out.println("--------------------------------");
		for (int iPlayer = 0; iPlayer < game.getPlayers().size(); iPlayer++) {
			Player p = game.getPlayers().get(iPlayer);
			System.out.println("Player #" + p.getTurn() + " " + p.getUsername() + ", choose your character.");
			System.out.print(game.getAvailableCharactersString(2));
			int iCharacter = readInt("Choice: ", 0, game.getAvailableCharacters().size() - 1);
			game.setPlayerCharacter(iPlayer, iCharacter);
		}

		while (!game.isOver() && !quit) {
			playTurn();
		}

		System.out.println("================================");
		System.out.println(quit ? "Game interrupted.\n" + game.getMurderEnvelope() : game.toString());
	}

	// Turn ===================================================================
	private void playTurn() {
		Player player = game.getCurrentPlayer();
		passTo(player);

		while (!quit && !game.isOver() && game.getCurrentPlayer().equals(player)) {
			System.out.println(toStringBoard(null));
			System.out.println(game);
			System.out.println("Your clues: " + names(player.getClues()));

			// Only the actions allowed right now are shown
			Map<String, String> menu = new LinkedHashMap<>();
			if (game.canRollDices(player))
				menu.put("r", "roll the dices");
			if (game.canMove(player) && game.getDiceResult() < 0)
				menu.put("t", "use the trapdoor to the " + game.getTrapdoorDestination(player).getName());
			if (game.canMove(player) && game.getDiceResult() >= 0)
				menu.put("m", "move");
			if (game.canAskClue(player))
				menu.put("c", "ask a clue question in the " + game.getRoomOf(player).getName());
			if (game.canMakeFinalAccusation(player))
				menu.put("a", "make the final accusation");
			menu.put("n", "open notebook");
			if (game.canEndTurn(player))
				menu.put("e", "end turn");
			menu.put("q", "quit game");

			menu.forEach((key, label) -> System.out.println(key + ") " + label));
			String choice = readLine("Choice: ").trim().toLowerCase();
			if (!menu.containsKey(choice)) {
				System.out.println("Invalid choice");
				continue;
			}

			switch (choice) {
				case "r": rollDices(player); break;
				case "t": useTrapdoor(player); break;
				case "m": move(player); break;
				case "c": askClue(player); break;
				case "a": finalAccusation(player); break;
				case "n": notebook(player); break;
				case "e": new ActionEndTurn(game, player).execute(); break;
				case "q": quit = true; break;
			}
		}
	}

	private void rollDices(Player player) {
		ActionRollDice action = new ActionRollDice(game, player);
		action.execute();
		System.out.println("You rolled " + action.getResult());
	}

	private void useTrapdoor(Player player) {
		Room destination = game.getTrapdoorDestination(player);
		new ActionMove(game, player, -1, player.getPosition(), destination.getCells().get(0)).execute();
		System.out.println("You went through the trapdoor to the " + destination.getName());
	}

	private void move(Player player) {
		int[][] destinations = game.getCurrentDestinations();
		System.out.println(toStringBoard(destinations));

		// Reachable rooms can be chosen by name, corridor cells by coordinates
		List<Room> rooms = new ArrayList<>();
		for (Room room : game.getBoard().getRooms()) {
			int[] cell = room.getCells().get(0);
			if (destinations[cell[0]][cell[1]] > 0)
				rooms.add(room);
		}
		for (int i = 0; i < rooms.size(); i++) {
			System.out.println(i + ") enter the " + rooms.get(i).getName());
		}

		while (true) {
			String input = readLine("Destination (room number, or row,col of a '*' cell): ").trim();
			int[] dst = null;
			if (input.matches("\\d+") && Integer.parseInt(input) < rooms.size()) {
				dst = rooms.get(Integer.parseInt(input)).getCells().get(0);
			}
			else if (input.matches("\\d+\\s*,\\s*\\d+")) {
				String[] parts = input.split(",");
				dst = new int[] {Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim())};
			}

			ActionMove action = dst == null ? null : new ActionMove(game, player, game.getDiceResult(), player.getPosition(), dst);
			if (action != null && action.isAllowed()) {
				action.execute();
				return;
			}
			System.out.println("Unreachable destination");
		}
	}

	private void askClue(Player player) {
		Room room = game.getRoomOf(player);
		System.out.println("Who killed Samuel Black in the " + room.getName() + "?");
		Character character = choose("Character", cluesOfType(Character.class));
		Weapon weapon = choose("Weapon", cluesOfType(Weapon.class));

		ActionAskClue action = new ActionAskClue(game, player, character, weapon);
		action.execute();
		ClueQuestion question = action.getResult();

		if (!question.hasAnswerer()) {
			System.out.println("Nobody could answer!");
			return;
		}

		// The answerer chooses which clue to show, without the others seeing it
		Player answerer = question.getAnswerer();
		passTo(answerer);
		System.out.println(player.getUsername() + " asked: " + character.getName() + ", " + weapon.getName() + ", " + room.getName());
		Clue clue = choose("Clue to show to " + player.getUsername(), question.getAnswerOptions());
		new ActionShowClue(game, answerer, clue).execute();

		passTo(player);
		System.out.println(answerer.getUsername() + " showed you: " + clue.getName());
	}

	private void finalAccusation(Player player) {
		System.out.println("Final accusation: if wrong, you are out of the game!");
		Character character = choose("Character", cluesOfType(Character.class));
		Weapon weapon = choose("Weapon", cluesOfType(Weapon.class));
		Room room = choose("Room", game.getBoard().getClueRooms());

		ActionFinalAccusation action = new ActionFinalAccusation(game, player, room, character, weapon);
		action.execute();
		if (!action.isCorrect()) {
			System.out.println("Wrong! " + player.getUsername() + " is out of the game.");
		}
	}

	private void notebook(Player player) {
		System.out.println("--- Notebook ---\n" + player.getNotebook() + "----------------");
		String line = readLine("Add a note (empty to skip): ");
		if (!line.isBlank())
			player.setNotebook(player.getNotebook() + line + "\n");
	}

	// Helpers ================================================================

	// Hides the previous output, so that the next player can't see the other players' clues
	private void passTo(Player player) {
		System.out.print("\n".repeat(40));
		readLine("=== " + player.getUsername() + " (" + player.getCharacter().getName() + "), press Enter ===");
	}

	/*
	 * Board legend: pawns are shown by turn number, rooms by letter (lowercase if reachable),
	 * '*' marks a reachable corridor cell.
	 */
	private String toStringBoard(int[][] destinations) {
		Board board = game.getBoard();
		List<Room> rooms = board.getRooms();
		StringBuilder res = new StringBuilder("    ");
		for (int x = 0; x < board.getSize()[1]; x++) {
			res.append(x / 10);
		}
		res.append("\n    ");
		for (int x = 0; x < board.getSize()[1]; x++) {
			res.append(x % 10);
		}
		res.append("\n");

		for (int y = 0; y < board.getSize()[0]; y++) {
			res.append(String.format("%2d  ", y));
			for (int x = 0; x < board.getSize()[1]; x++) {
				res.append(toCharCell(y, x, destinations, rooms));
			}
			res.append("\n");
		}

		res.append("Rooms: ");
		for (int i = 0; i < rooms.size(); i++) {
			res.append((char) ('A' + i)).append("=").append(rooms.get(i).getName()).append(i < rooms.size() - 1 ? ", " : "\n");
		}
		res.append("Players: ");
		for (Player p : game.getPlayers()) {
			res.append(p.getTurn()).append("=").append(p.getUsername()).append(p.isEliminated() ? " (out)" : "").append("  ");
		}
		return res.toString();
	}

	private char toCharCell(int y, int x, int[][] destinations, List<Room> rooms) {
		for (Player p : game.getPlayers()) {
			if (p.getPosition()[0] == y && p.getPosition()[1] == x)
				return (char) ('0' + p.getTurn());
		}

		boolean reachable = destinations != null && destinations[y][x] > 0;
		Room room = game.getBoard().getRoomAt(y, x);
		if (room != null) {
			char letter = (char) ('A' + rooms.indexOf(room));
			return reachable ? java.lang.Character.toLowerCase(letter) : letter;
		}
		if (reachable)
			return '*';

		CellType type = game.getBoard().getCells()[y][x];
		if (type.isDoor() || type.equals(CellType.TRAPDOOR))
			return type.getRepresentation().charAt(0);
		return type.equals(CellType.NONE) ? ' ' : '.';
	}

	private <T extends Clue> List<T> cluesOfType(Class<T> type) {
		List<T> result = new ArrayList<>();
		for (Clue clue : game.getTotalCluesList()) {
			if (type.isInstance(clue))
				result.add(type.cast(clue));
		}
		return result;
	}

	private <T extends Clue> T choose(String title, List<T> options) {
		for (int i = 0; i < options.size(); i++) {
			System.out.println("  " + i + ") " + options.get(i).getName());
		}
		return options.get(readInt(title + ": ", 0, options.size() - 1));
	}

	private static String names(List<? extends Clue> clues) {
		List<String> result = new ArrayList<>();
		for (Clue clue : clues) {
			result.add(clue.getName());
		}
		return String.join(", ", result);
	}

	private String readLine(String prompt) {
		System.out.print(prompt);
		if (!scanner.hasNextLine()) {
			// End of input (e.g. Ctrl+D): stop the game
			System.out.println();
			throw new EndOfInputException();
		}
		return scanner.nextLine();
	}

	private int readInt(String prompt, int min, int max) {
		while (true) {
			String input = readLine(prompt).trim();
			if (input.matches("-?\\d+")) {
				int value = Integer.parseInt(input);
				if (value >= min && value <= max)
					return value;
			}
			System.out.println("Insert a number between " + min + " and " + max);
		}
	}
}
