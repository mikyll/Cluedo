package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;

public class ActionEndTurn implements GameAction {
    private GameCluedo game;
    private Player player;

    public ActionEndTurn(GameCluedo game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public boolean isAllowed() {
        // Check player turn

        return true;
    }

    @Override
    public void execute() {

    }
}
