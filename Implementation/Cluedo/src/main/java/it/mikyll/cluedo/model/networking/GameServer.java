package it.mikyll.cluedo.model.networking;

import it.mikyll.cluedo.model.game.ClueQuestion;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.actions.*;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.MurderEnvelope;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerHuman;
import it.mikyll.cluedo.model.networking.message.GameMessage;
import it.mikyll.cluedo.model.networking.message.GameMessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameServer {
    /*
     * 1. Waits for connections and a START command
     * 2. Creates a list of players
     * 3. Creates a new Game object
     * 4. Starts the PREPARATION phase
     * 5. Sends back to clients their turn
     * 6. Waits for each player character's choice
     *
     * NB:
     *  - the players list can be built and shown on the client GUIs
     *  - player pawns can be set on the board
     *
     * 7. Creates the MurderEnvelope object
     * 8. Assigns the remaining cards to the players
     * 9. Sends to each player their cards
     *
     * The host holds the real game: clients send requests, the server checks them with the game actions,
     * runs them and sends the resulting events to everyone. Each player only receives their own clues,
     * a shown clue is sent only to the asker and the answerer, and the envelope only at the end.
     */

    // Sends a message to the user with the given username (the host itself included)
    public interface IGameMessageSender {
        void send(String username, GameMessage message);
    }

    private static final int NUM_DICES = 2;

    private final GameCluedo game;
    private final List<Player> players;
    private final IGameMessageSender sender;
    // Index of the player choosing the character; players.size() once everyone has chosen
    private int choosingPlayer = 0;

    public GameServer(List<String> usernames, IGameMessageSender sender, Random random) {
        this.sender = sender;
        List<Player> list = new ArrayList<>();
        for (String username : usernames)
            list.add(new PlayerHuman(username));

        // Creates the game: turns, murder envelope, clues and starting squares
        this.game = new GameCluedo(list, NUM_DICES, random);
        this.game.prepareGame();
        this.players = game.getPlayers();
    }

    public GameCluedo getGame() {return game;}

    public boolean isStarted() {
        return choosingPlayer >= players.size();
    }

    // Sends to each player the turn order, the starting squares and their own clues, then starts the character choice
    public synchronized void start() {
        String[] usernames = new String[players.size()];
        int[][] positions = new int[players.size()][];
        for (int i = 0; i < players.size(); i++) {
            usernames[i] = players.get(i).getUsername();
            positions[i] = players.get(i).getPosition();
        }

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            sender.send(player.getUsername(), new GameMessage(GameMessageType.SETUP)
                    .setYou(i).setUsernames(usernames).setPositions(positions).setClues(indexesOf(player.getClues())));
        }
        sendToAll(new GameMessage(GameMessageType.CHARACTER_TURN).setPlayer(choosingPlayer));
    }

    // Handles a request of a player
    public synchronized void handleMessage(String username, GameMessage request) {
        Player player = getPlayer(username);
        if (player == null || player.hasLeft())
            return;

        switch (request.getType()) {
            case CHOOSE_CHARACTER: chooseCharacter(player, request); break;
            case ROLL_DICE: rollDice(player); break;
            case MOVE: move(player, request); break;
            case ASK_CLUE: askClue(player, request); break;
            case SHOW_CLUE: showClue(player, request); break;
            case FINAL_ACCUSATION: finalAccusation(player, request); break;
            case END_TURN: endTurn(player); break;
            default: sendError(player, "Unexpected request: " + request.getType()); break;
        }
    }

    /*
     * A player disconnected: their clues are revealed to everyone and the game goes on without them.
     * If they had to answer a clue question, one of their clues is shown to the asker.
     */
    public synchronized void handleDisconnect(String username) {
        Player player = getPlayer(username);
        if (player == null || player.hasLeft() || game.isOver())
            return;

        ClueQuestion question = game.getClueQuestion();
        if (game.isWaitingForAnswer() && player.equals(question.getAnswerer()))
            answer(player, question.getAnswerOptions().get(0));

        int index = players.indexOf(player);
        game.removePlayer(player);
        sendToAll(new GameMessage(GameMessageType.PLAYER_LEFT).setPlayer(index).setClues(indexesOf(player.getClues())));

        if (!isStarted() && choosingPlayer == index)
            nextChoosingPlayer();
        checkGameOver();
    }

    // Requests ===============================================================

    private void chooseCharacter(Player player, GameMessage request) {
        Clue clue = getClue(request.getCharacter());
        int index = players.indexOf(player);
        if (isStarted() || index != choosingPlayer || !(clue instanceof Character)
                || game.getAvailableCharacterIndex(((Character) clue).getId()) < 0) {
            sendError(player, "You can't choose this character now");
            return;
        }

        game.setPlayerCharacter(index, game.getAvailableCharacterIndex(((Character) clue).getId()));
        sendToAll(new GameMessage(GameMessageType.CHARACTER_CHOSEN).setPlayer(index).setCharacter(request.getCharacter()));
        nextChoosingPlayer();
    }

    private void nextChoosingPlayer() {
        do {
            choosingPlayer++;
        } while (choosingPlayer < players.size() && players.get(choosingPlayer).hasLeft());

        if (isStarted())
            sendToAll(new GameMessage(GameMessageType.GAME_STARTED));
        else
            sendToAll(new GameMessage(GameMessageType.CHARACTER_TURN).setPlayer(choosingPlayer));
    }

    private void rollDice(Player player) {
        ActionRollDice action = new ActionRollDice(game, player);
        if (!checkAllowed(player, action))
            return;

        action.execute();
        sendToAll(new GameMessage(GameMessageType.DICE_ROLLED).setPlayer(players.indexOf(player)).setValue(action.getResult()));
    }

    private void move(Player player, GameMessage request) {
        ActionMove action = new ActionMove(game, player, game.getDiceResult(), player.getPosition(), request.getPosition());
        if (request.getPosition() == null || !checkAllowed(player, action))
            return;

        action.execute();
        sendToAll(new GameMessage(GameMessageType.MOVED).setPlayer(players.indexOf(player)).setPosition(request.getPosition()));
    }

    private void askClue(Player player, GameMessage request) {
        Clue character = getClue(request.getCharacter());
        Clue weapon = getClue(request.getWeapon());
        if (!(character instanceof Character) || !(weapon instanceof Weapon)) {
            sendError(player, "Invalid clue question");
            return;
        }
        ActionAskClue action = new ActionAskClue(game, player, (Character) character, (Weapon) weapon);
        if (!checkAllowed(player, action))
            return;

        action.execute();
        ClueQuestion question = action.getResult();
        sendToAll(new GameMessage(GameMessageType.CLUE_ASKED).setPlayer(players.indexOf(player))
                .setCharacter(indexOf(question.getCharacter())).setWeapon(indexOf(question.getWeapon()))
                .setRoom(indexOf(question.getRoom()))
                .setAnswerer(question.hasAnswerer() ? players.indexOf(question.getAnswerer()) : -1));
    }

    private void showClue(Player player, GameMessage request) {
        ActionShowClue action = new ActionShowClue(game, player, getClue(request.getClue()));
        if (!checkAllowed(player, action))
            return;

        answer(player, getClue(request.getClue()));
    }

    // Shows the clue to the asker: only the asker and the answerer know which clue it is
    private void answer(Player answerer, Clue clue) {
        Player asker = game.getClueQuestion().getAsker();
        new ActionShowClue(game, answerer, clue).execute();

        for (Player p : players) {
            if (p.hasLeft())
                continue;
            boolean knows = p.equals(asker) || p.equals(answerer);
            sender.send(p.getUsername(), new GameMessage(GameMessageType.CLUE_SHOWN).setPlayer(players.indexOf(answerer))
                    .setAnswerer(players.indexOf(answerer)).setClue(knows ? indexOf(clue) : -1));
        }
    }

    private void finalAccusation(Player player, GameMessage request) {
        Clue character = getClue(request.getCharacter());
        Clue weapon = getClue(request.getWeapon());
        Clue room = getClue(request.getRoom());
        if (!(character instanceof Character) || !(weapon instanceof Weapon) || !(room instanceof Room)) {
            sendError(player, "Invalid accusation");
            return;
        }
        ActionFinalAccusation action = new ActionFinalAccusation(game, player, (Room) room, (Character) character, (Weapon) weapon);
        if (!checkAllowed(player, action))
            return;

        action.execute();
        sendToAll(new GameMessage(GameMessageType.ACCUSATION_MADE).setPlayer(players.indexOf(player))
                .setCharacter(request.getCharacter()).setWeapon(request.getWeapon()).setRoom(request.getRoom())
                .setCorrect(action.isCorrect()));
        checkGameOver();
    }

    private void endTurn(Player player) {
        ActionEndTurn action = new ActionEndTurn(game, player);
        if (!checkAllowed(player, action))
            return;

        action.execute();
        sendToAll(new GameMessage(GameMessageType.TURN_ENDED).setPlayer(players.indexOf(player)));
    }

    // At the end, the envelope is revealed to everyone
    private void checkGameOver() {
        if (!game.isOver())
            return;

        MurderEnvelope envelope = game.getMurderEnvelope();
        Player winner = game.getWinner();
        sendToAll(new GameMessage(GameMessageType.GAME_OVER).setPlayer(winner == null ? -1 : players.indexOf(winner))
                .setClues(new int[] {indexOf(envelope.getMurderer()), indexOf(envelope.getWeapon()), indexOf(envelope.getRoom())}));
    }

    // Utilities ==============================================================

    private boolean checkAllowed(Player player, GameAction action) {
        if (action.isAllowed())
            return true;
        sendError(player, "Action not allowed: " + action.getClass().getSimpleName());
        return false;
    }

    private void sendError(Player player, String text) {
        sender.send(player.getUsername(), new GameMessage(GameMessageType.ERROR).setText(text));
    }

    private void sendToAll(GameMessage message) {
        for (Player p : players) {
            if (!p.hasLeft())
                sender.send(p.getUsername(), message);
        }
    }

    private Player getPlayer(String username) {
        for (Player p : players) {
            if (p.getUsername().equals(username))
                return p;
        }
        return null;
    }

    private Clue getClue(int index) {
        List<Clue> clues = game.getTotalCluesList();
        return index >= 0 && index < clues.size() ? clues.get(index) : null;
    }

    private int indexOf(Clue clue) {
        return game.getTotalCluesList().indexOf(clue);
    }

    private int[] indexesOf(List<Clue> clues) {
        int[] result = new int[clues.size()];
        for (int i = 0; i < clues.size(); i++)
            result[i] = indexOf(clues.get(i));
        return result;
    }
}
