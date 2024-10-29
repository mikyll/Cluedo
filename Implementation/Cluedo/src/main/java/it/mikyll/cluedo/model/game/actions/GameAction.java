package it.mikyll.cluedo.model.game.actions;

public interface GameAction {
    public boolean isAllowed();
    public void execute();
}
