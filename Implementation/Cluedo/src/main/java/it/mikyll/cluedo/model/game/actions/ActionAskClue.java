package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;

public class ActionAskClue implements GameAction {
    private GameCluedo game;

    public ActionAskClue(GameCluedo game) {
        this.game = game;
    }

    @Override
    public boolean isAllowed() {
        return false;
    }

    @Override
    public void execute() {

    }
}
