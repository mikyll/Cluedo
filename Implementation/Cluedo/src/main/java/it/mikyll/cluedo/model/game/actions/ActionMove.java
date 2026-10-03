package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;

import java.util.Arrays;

/*
 * Moves the player to dst. Steps is the dices result, or -1 when using the trapdoor.
 */
public class ActionMove extends GameAction {
    private int steps;
    private int[] src;
    private int[] dst;

    public ActionMove(GameCluedo game, int iPlayer, int steps, int[] src, int[] dst) {
        this(game, game.getPlayers().get(iPlayer), steps, src, dst);
    }
    public ActionMove(GameCluedo game, Player player, int steps, int[] src, int[] dst) {
        super(game, player);

        this.steps = steps;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public boolean isAllowed() {
        GameCluedo game = getGame();
        return game.canMove(getPlayer())
                && Arrays.equals(src, getPlayer().getPosition())
                && steps == game.getDiceResult()
                && game.isCurrentDestination(dst);
    }

    @Override
    public void execute() {
        getGame().moveCurrentPlayer(dst);
    }
}
