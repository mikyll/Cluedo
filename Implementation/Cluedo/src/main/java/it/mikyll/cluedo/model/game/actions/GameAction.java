package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;

/*
 * An action of a player. The GUI (or the server, in multiplayer) builds it from the player input,
 * checks it with isAllowed() and then runs it with execute().
 */
public abstract class GameAction {
    private GameCluedo game;
    private Player player;

    public GameAction(GameCluedo game, Player player) {
        this.game = game;
        this.player = player;
    }

    public GameCluedo getGame() {return game;}
    public Player getPlayer() {return player;}

    public abstract boolean isAllowed();

    public abstract void execute();
}
