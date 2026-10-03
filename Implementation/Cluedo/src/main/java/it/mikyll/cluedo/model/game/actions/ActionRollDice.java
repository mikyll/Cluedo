package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;

public class ActionRollDice extends GameAction {
    private int result = -1;

    public ActionRollDice(GameCluedo game, Player player) {
        super(game, player);
    }

    public int getResult() {return result;}

    @Override
    public boolean isAllowed() {
        return getGame().canRollDices(getPlayer());
    }

    @Override
    public void execute() {
        this.result = getGame().rollDicesForTurn();
    }
}
