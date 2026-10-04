package it.mikyll.cluedo.model.networking;

import it.mikyll.cluedo.model.game.ClueQuestion;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.MurderEnvelope;
import it.mikyll.cluedo.model.game.clues.Character;
import it.mikyll.cluedo.model.game.clues.Clue;
import it.mikyll.cluedo.model.game.clues.Room;
import it.mikyll.cluedo.model.game.clues.Weapon;
import it.mikyll.cluedo.model.game.player.Player;
import it.mikyll.cluedo.model.game.player.PlayerHuman;
import it.mikyll.cluedo.model.networking.message.GameMessage;
import it.mikyll.cluedo.model.networking.message.GameMessageType;

import java.util.ArrayList;
import java.util.List;

/*
 * A player's view of a multiplayer game: a copy of the game hosted by the GameServer, updated with its events.
 * It knows the public information and the clues of the local player only. The requests of the local player
 * are sent to the host, and take effect when the corresponding event comes back.
 */
public class GameClient {
    // Sends a request to the host
    public interface IGameRequestSender {
        void send(GameMessage request);
    }

    private static final int NUM_DICES = 2;

    private final IGameRequestSender sender;
    private GameCluedo game;
    private Player me;
    // Index of the player choosing the character; -1 once the game has started
    private int choosingPlayer = 0;

    public GameClient(IGameRequestSender sender) {
        this.sender = sender;
    }

    public GameCluedo getGame() {return game;}
    public Player getMe() {return me;}
    public boolean isStarted() {return choosingPlayer < 0;}

    // Player choosing the character now, or null
    public Player getChoosingPlayer() {
        return isStarted() || game == null ? null : game.getPlayers().get(choosingPlayer);
    }

    public Player getPlayer(int index) {
        return index >= 0 ? game.getPlayers().get(index) : null;
    }

    public Clue getClue(int index) {
        List<Clue> clues = game.getTotalCluesList();
        return index >= 0 && index < clues.size() ? clues.get(index) : null;
    }

    // Updates the copy of the game with an event of the host
    public void apply(GameMessage event) {
        switch (event.getType()) {
            case SETUP: {
                List<Player> players = new ArrayList<>();
                for (int i = 0; i < event.getUsernames().length; i++) {
                    Player player = new PlayerHuman(event.getUsernames()[i]);
                    player.setTurn(i + 1);
                    player.setPosition(event.getPositions()[i]);
                    players.add(player);
                }
                this.game = new GameCluedo(players, NUM_DICES);
                this.game.prepareCopy();
                this.me = players.get(event.getYou());
                for (int index : event.getClues())
                    me.getClues().add(getClue(index));
                this.choosingPlayer = 0;
                break;
            }
            case CHARACTER_TURN:
                this.choosingPlayer = event.getPlayer();
                break;
            case CHARACTER_CHOSEN: {
                Character character = (Character) getClue(event.getCharacter());
                game.setPlayerCharacter(event.getPlayer(), game.getAvailableCharacterIndex(character.getId()));
                break;
            }
            case GAME_STARTED:
                this.choosingPlayer = -1;
                break;
            case DICE_ROLLED:
                game.applyDiceResult(event.getValue());
                break;
            case MOVED:
                game.moveCurrentPlayer(event.getPosition());
                break;
            case CLUE_ASKED: {
                Player asker = getPlayer(event.getPlayer());
                Character character = (Character) getClue(event.getCharacter());
                Weapon weapon = (Weapon) getClue(event.getWeapon());
                Room room = (Room) getClue(event.getRoom());
                Player answerer = getPlayer(event.getAnswerer());
                // Only the answerer knows which clues can be shown
                List<Clue> options = new ArrayList<>();
                if (me.equals(answerer)) {
                    for (Clue clue : List.of(character, weapon, room)) {
                        if (me.getClues().contains(clue))
                            options.add(clue);
                    }
                }
                game.registerClueQuestion(asker, character, weapon, room, answerer, options);
                break;
            }
            case CLUE_SHOWN:
                game.applyClueAnswered(getClue(event.getClue()));
                break;
            case ACCUSATION_MADE:
                game.applyFinalAccusation(getPlayer(event.getPlayer()), event.isCorrect());
                break;
            case TURN_ENDED:
                game.nextPlayer();
                break;
            case PLAYER_LEFT: {
                Player player = getPlayer(event.getPlayer());
                // Their clues are revealed to everyone
                player.getClues().clear();
                for (int index : event.getClues())
                    player.getClues().add(getClue(index));
                game.removePlayer(player);
                break;
            }
            case GAME_OVER: {
                int[] clues = event.getClues();
                game.setMurderEnvelope(new MurderEnvelope(getClue(clues[0]), getClue(clues[1]), getClue(clues[2])));
                break;
            }
            default:
                break;
        }
    }

    // Requests ===============================================================

    public void chooseCharacter(Character character) {
        sender.send(new GameMessage(GameMessageType.CHOOSE_CHARACTER).setCharacter(indexOf(character)));
    }

    public void rollDice() {
        sender.send(new GameMessage(GameMessageType.ROLL_DICE));
    }

    public void move(int[] position) {
        sender.send(new GameMessage(GameMessageType.MOVE).setPosition(position));
    }

    public void askClue(Character character, Weapon weapon) {
        sender.send(new GameMessage(GameMessageType.ASK_CLUE).setCharacter(indexOf(character)).setWeapon(indexOf(weapon)));
    }

    public void showClue(Clue clue) {
        sender.send(new GameMessage(GameMessageType.SHOW_CLUE).setClue(indexOf(clue)));
    }

    public void finalAccusation(Character character, Weapon weapon, Room room) {
        sender.send(new GameMessage(GameMessageType.FINAL_ACCUSATION)
                .setCharacter(indexOf(character)).setWeapon(indexOf(weapon)).setRoom(indexOf(room)));
    }

    public void endTurn() {
        sender.send(new GameMessage(GameMessageType.END_TURN));
    }

    private int indexOf(Clue clue) {
        return game.getTotalCluesList().indexOf(clue);
    }
}
