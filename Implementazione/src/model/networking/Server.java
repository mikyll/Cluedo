package model.networking;

import model.game.Game;

public class Server {
	private MPState serverState = MPState.LOBBY;
	
	private Game game;
	
	public Server() {
		
	}
	
	public void startGame() {
		this.serverState = MPState.GAME;
		
		//this.game = new Game();
		
	}
	
	public void kickUser(User user) {
		
	}
	
	public void banUser(String nickname, String address) {
		// NB: nickname XOR address can be empty
	}
	
	public void handleMessage() {
		
	}
}
