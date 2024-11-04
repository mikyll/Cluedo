package it.mikyll.cluedo.model.game.actions;

import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.game.player.Player;

public class ActionMove implements GameAction {
    private GameCluedo game;
    private Player player;
    private int steps;
    private int[] src;
    private int[] dst;

    public ActionMove(GameCluedo game, int iPlayer, int steps, int[] src, int[] dst) {
        this(game, game.getPlayers().get(iPlayer), steps, src, dst);
    }
    public ActionMove(GameCluedo game, Player player, int steps, int[] src, int[] dst) {
        this.game = game;
        this.player = player;
        this.steps = steps;
        this.src = src;
        this.dst = dst;
    }

    @Override
    public boolean isAllowed() {
        // Do calculations

        return false;
    }

    @Override
    public void execute() {

    }
}
