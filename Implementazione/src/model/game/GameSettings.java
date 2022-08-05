package model.game;

public class GameSettings {
	private int playersNumber;
	private int timerDuration;
	
	public GameSettings(int playersNumber, int timerDuration) {
		this.playersNumber = playersNumber;
		this.timerDuration = timerDuration;
	}

	public int getPlayersNumber() {return playersNumber;}
	public void setPlayersNumber(int playersNumber) {this.playersNumber = playersNumber;}
	public int getTimerDuration() {return timerDuration;}
	public void setTimerDuration(int timerDuration) {this.timerDuration = timerDuration;}
	
	
}
