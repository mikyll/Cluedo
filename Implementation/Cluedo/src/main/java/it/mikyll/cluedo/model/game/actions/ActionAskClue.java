package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;

public class ActionAskClue extends GameAction {
    public ActionAskClue(GameCluedo game, Player player) {
        super(game, player);
    }

    @Override
    public boolean isAllowed() {
        return false;
    }

    @Override
    public void execute() {

    }
}
