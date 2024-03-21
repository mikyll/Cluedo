package model.game;

public interface IGame {
	public State checkMove(State state, Action action);
	public void endGame(State state);
}
