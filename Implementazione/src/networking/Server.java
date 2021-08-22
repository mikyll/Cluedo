package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	private ServerSocket ss;
	private int connectedPlayers;
	
	private ServerSideConnection ssc;
	
	public Server()
	{
		System.out.println("Game Server init...");
		
		this.connectedPlayers = 1;
		
		try {
			this.ss = new ServerSocket(51234);
		} catch(IOException e) {
			System.out.println("IOException from Server sonstructor");
		}
		
	}
	
	public void acceptConnections()
	{
		try {
			System.out.println("Waiting for connections...");
			
			while(this.connectedPlayers < 2)
			{
				Socket s = ss.accept();
				this.connectedPlayers++;
				System.out.println("Player #" + this.connectedPlayers + "has connected.");
				
				ServerSideConnection ssc = new ServerSideConnection(s, this.connectedPlayers);
				
				// roba?
				
				Thread t = new Thread(ssc);
				t.start();
				
			}
		}catch(IOException e) {
			System.out.println("IOException from acceptConnections()");
		}
	}
	
	private class ServerSideConnection implements Runnable {
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		private int playerID;
		
		public ServerSideConnection(Socket s, int id)
		{
			this.socket = s;
			this.playerID = id;
			
			try {
				this.dataIn = new DataInputStream(socket.getInputStream());
				this.dataOut = new DataOutputStream(socket.getOutputStream());
			} catch(IOException e) {
				System.out.println("IOException from ServerSideConnection constructor");
			}
		}
		
		@Override
		public void run() {
			try {
				dataOut.writeInt(this.playerID); // gli invia il suo ID
				dataOut.flush();
				
				while(true)
				{
					
				}
			} catch(IOException e) {
				System.out.println("IOException from run() in ServerSideConnection");
			}
			
		}
	}
	
	public static void main(String[] args)
	{
		Server s = new Server();
		s.acceptConnections();
	}
}
