package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;

public abstract class GameAction {
    private GameCluedo game;
    private Player player;

    public GameAction(GameCluedo game, Player player) {
        this.game = game;
    }

    public boolean isAllowed() {
        // TODO
        return false;
    }

    public abstract void execute();
}
