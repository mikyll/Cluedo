package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;

public class ActionEndTurn extends GameAction {
    public ActionEndTurn(GameCluedo game, Player player) {
        super(game, player);
    }

    @Override
    public boolean isAllowed() {
        // Check player turn
        return getGame().canEndTurn(getPlayer());
    }

    @Override
    public void execute() {
        getGame().nextPlayer();
    }
}
