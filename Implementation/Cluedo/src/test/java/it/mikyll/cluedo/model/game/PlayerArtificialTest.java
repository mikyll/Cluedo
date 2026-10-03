package it.mikyll.cluedo.model.game;

import static org.junit.jupiter.api.Assertions.*;

import it.mikyll.cluedo.model.game.actions.ActionShowClue;
import it.mikyll.cluedo.model.game.actions.GameAction;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerArtificial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

class PlayerArtificialTest {
	private static final int MAX_ACTIONS = 20_000;

	@Test
	void botsOnlyGamesEndWithTheRightWinner() {
		for (int seed = 0; seed < 50; seed++) {
			int numPlayers = 2 + seed % 5;
			GameCluedo game = playBotsOnlyGame(numPlayers, seed);

			assertTrue(game.isOver(), "seed " + seed + ": the game must end");
			assertNotNull(game.getWinner(), "seed " + seed + ": bots only accuse when sure");
		}
	}

	@Test
	void everyBotActionIsAllowed() {
		Random random = new Random(7);
		GameCluedo game = newBotsGame(4, random);

		for (int i = 0; i < MAX_ACTIONS && !game.isOver(); i++) {
			PlayerArtificial bot = (PlayerArtificial) game.getCurrentPlayer();
			GameAction action = bot.nextAction(game);
			assertTrue(action.isAllowed(), action.getClass().getSimpleName() + " not allowed");
			action.execute();
			answerIfNeeded(game);
		}
	}

	@Test
	void learnsSolutionFromShownClues() {
		GameCluedo game = newBotsGame(2, new Random(3));
		PlayerArtificial bot = (PlayerArtificial) game.getPlayers().get(0);
		Player other = game.getPlayers().get(1);
		assertFalse(bot.knowsSolution(game));

		// The other player shows all their clues: what's left is the envelope
		for (Clue clue : other.getClues()) {
			ClueQuestion question = new ClueQuestion(bot, null, null, null, other, List.of(clue));
			question.setShownClue(clue);
			bot.onClueQuestionAnswered(question);
		}

		assertTrue(bot.knowsSolution(game));
	}

	@Test
	void nobodyAnsweringRevealsEnvelopeClues() {
		GameCluedo game = newBotsGame(3, new Random(5));
		PlayerArtificial bot = (PlayerArtificial) game.getPlayers().get(0);
		MurderEnvelope envelope = game.getMurderEnvelope();

		bot.onClueQuestionAnswered(new ClueQuestion(bot, (Character) envelope.getMurderer(),
				(Weapon) envelope.getWeapon(), (Room) envelope.getRoom(), null, List.of()));

		assertTrue(bot.knowsSolution(game));
	}

	private static GameCluedo playBotsOnlyGame(int numPlayers, long seed) {
		GameCluedo game = newBotsGame(numPlayers, new Random(seed));
		for (int i = 0; i < MAX_ACTIONS && !game.isOver(); i++) {
			PlayerArtificial bot = (PlayerArtificial) game.getCurrentPlayer();
			bot.nextAction(game).execute();
			answerIfNeeded(game);
		}
		if (game.getWinner() != null) {
			MurderEnvelope envelope = game.getMurderEnvelope();
			assertTrue(envelope.makeAccusation(envelope.getMurderer(), envelope.getWeapon(), envelope.getRoom()));
		}
		return game;
	}

	private static GameCluedo newBotsGame(int numPlayers, Random random) {
		List<Player> players = new ArrayList<>();
		for (int i = 0; i < numPlayers; i++) {
			players.add(new PlayerArtificial("bot" + i, random));
		}
		GameCluedo game = new GameCluedo(players, 2, random);
		game.prepareGame();
		for (int i = 0; i < numPlayers; i++) {
			game.setPlayerCharacter(i, 0);
		}
		return game;
	}

	private static void answerIfNeeded(GameCluedo game) {
		if (game.isWaitingForAnswer()) {
			ClueQuestion question = game.getClueQuestion();
			PlayerArtificial answerer = (PlayerArtificial) question.getAnswerer();
			new ActionShowClue(game, answerer, answerer.chooseClueToShow(question)).execute();
		}
	}
}
