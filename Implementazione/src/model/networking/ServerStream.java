package model.networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;

import model.game.Game;
import model.networking.message.IMessageHandler;

public class ServerStream {
	public static final int DEFAULT_PORT = 4321;
	
	private MPState serverState = MPState.LOBBY;
	
	private int port;
	private IMessageHandler messageHandler;
	
	private Game game;
	
	public ServerStream(IMessageHandler msgHandler) {
		this.messageHandler = msgHandler;
	}
	public ServerStream() {
		
	}
	
	public void setMessageHandler(IMessageHandler msgHandler) {this.messageHandler = msgHandler;}
	
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
	
	private class ServerListener extends Thread {
		private ServerSocket socketListener;
		
		public ServerListener(int port) throws IOException
		{
			this.socketListener = new ServerSocket(port);
			System.out.println("Server (" + this.getId() + "): listening for connections on port " + port);
		}
		
		@Override
		public void run()
		{
			/*try {
				while(true)
				{
					new Handler(this.socketListener.accept()).start();
				}
			} catch(SocketException e) {
				System.out.println("Server (" + this.getId() + "): " + e.getMessage());
			} catch (IOException e) {
				System.out.println("Server: error while trying to accept a new connection");
				e.printStackTrace();
			} finally {
				try {
					this.socketListener.close(); // CHECK
				} catch (IOException e) {
					e.printStackTrace();
				}
			}*/
		}
		
		public void closeSocket()
		{
			try {
				this.socketListener.close();
			} catch (IOException e) {
				System.out.println("IOException while closing the socket");
				e.printStackTrace();
			}
		}
	}
}
