package it.mikyll.cluedo.model.game;

import java.util.*;

import it.mikyll.cluedo.model.game.board.Board;
import it.mikyll.cluedo.model.game.board.Position;
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
		players.add(new PlayerArtificial("Comp1"));

		GameCluedo game = new GameCluedo(players);

		game.initCluesList();

		//game.startGame();

		// TODO
	}

	private Board board;
	private List<Clue> totalCluesList;
	private List<Player> players;
	private MurderEnvelope murderEnvelope;

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
		this.players = players;
	}

	public void startGame() {
		// Init board
		board = new Board();

		// Init murder envelope
		totalCluesList = new ArrayList<>();
		totalCluesList.addAll(AssetLoader.loadCharacters());
		totalCluesList.addAll(AssetLoader.loadRooms());
		totalCluesList.addAll(AssetLoader.loadWeapons());
		List<Clue> assignableCluesList = new ArrayList<>(totalCluesList);
		murderEnvelope = new MurderEnvelope(assignableCluesList);

		// Assign player turns
		Collections.shuffle(this.players);
		for (int i = 0; i < this.players.size(); i++) {
			this.players.get(i).setTurn(i+1);
		}

		for (int i = 0; !assignableCluesList.isEmpty(); i++) {
			Clue clue = assignableCluesList.get(0);
			Player player = players.get(i);

			player.getClues().add(clue);
			assignableCluesList.remove(0);
		}

		// Init Players positions
		List<Position> initPos = Arrays.asList(new Position(1, 1), new Position(1, 2));
		Collections.shuffle(initPos);
		for (Player p : this.players) {
			p.setPosition(initPos.get(0));
			initPos.remove(0);
		}

		// Assign clue cards


		// Preparation phase


		// Prepare cards & murdererEnvelope

		// Game start
	}

	public void initCluesList()
	{
		List<Character> characters = AssetLoader.loadCharacters();

		// TODO: test
		for (Character character : characters)
		{
			System.out.println(character.toString());
		}
	}

	public void initMurderEnvelope()
	{

	}

	public void initPreparationPhase() {

	}

	public void movePlayer(Player player, Position newPos) {

	}
	
	// everybody lost
	public void endGame() {
		
	}
	
	// player wins
	public void endGame(Player winner) {
		
	}
	
	public void preparationPhase()
	{
		// Shuffle players list
		Collections.shuffle(this.players);

		for (int i = 0; i < this.players.size(); i++)
		{
			this.players.get(i).setTurn(i+1);
		}

		// roll dice
		
		// choose character
			// + timer
		
		// roll initial cards
	}
	
	public void setCharacter(Player p, Characters character) {
		
	}
	
	// get random number (2-12)
	public int rollDice() {
		return -1;
	}
	
	
	
	public Clue askClue(Player p, Characters who, Weapons what, Rooms where) {
		// ask the first player after p (by turns) if he has one of the clues.
		
		// Example:
		// Does p+1 have one of the clues? No -> "Player p+1 cannot answer"
		// Does p+2 have one of the clues? Yes -> shows him the clue (just one). "Player p+2 reveals a clue"
		
		// If no one has none of the clues you asked, then you know the answer and must wait the next turn
		
		return null;
	}
	
	public boolean accuse(Player p, Characters who, Weapons what, Rooms where) {
		// correct? The game ends, and p wins;
		
		// wrong? The game continues, and p is eliminated
		return false;
	}


	public String toString() {
		// TODO: prints the game state



		return "";
	}
}
