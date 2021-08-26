package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerStream implements Runnable {
	
	private static final int MAX_PLAYERS = 6;
	
	private ServerSocket serverSocket;
	
	private int connectedPlayers;
	private boolean acceptConnections; // ON/OFF
	private ArrayList<Socket> connections;
	
	private DataInputStream diStream;
	private DataOutputStream doStream;
	
	//private ServerSideConnection ssc;
	
	public ServerStream()
	{
		this.acceptConnections = true;
		this.connectedPlayers = 1;
		this.connections = new ArrayList<Socket>();
		
		try {
			this.serverSocket = new ServerSocket(8765, MAX_PLAYERS);
			System.out.println("Server socket created.");
			System.out.println(this.serverSocket.getInetAddress().toString());
			System.out.println(this.serverSocket.getLocalSocketAddress());
		} catch(IOException e) {
			System.out.println("IOException from Server sonstructor");
		}
		
	}
	
	public void acceptConnections()
	{
		try {
			System.out.println("Waiting for connections...");
			
			// test
			Socket s = this.serverSocket.accept();
			System.out.println("Player #2 has connected");
			
			this.diStream = new DataInputStream(s.getInputStream());
			this.doStream = new DataOutputStream(s.getOutputStream());
			
			this.doStream.writeInt(2);
			while(true)
			{
				System.out.println(diStream.readUTF());
			}
			
			/*while(this.acceptConnections)
			{
				Socket s = this.serverSocket.accept();
				this.connections.add(s);
				this.connectedPlayers++;
				System.out.println("Player #" + this.connectedPlayers + "has connected (" + s.getInetAddress() + s.getLocalAddress() + s.getLocalSocketAddress() + ")");
				
				this.diStream = new DataInputStream(s.getInputStream());
				this.doStream = new DataOutputStream(s.getOutputStream());
				
			}*/
		}catch(IOException e) {
			System.out.println("IOException from acceptConnections()");
		}
	}
	
	public static void main(String args[])
	{
		ServerStream s = new ServerStream();
		s.run();
	}

	@Override
	public void run()
	{
		while(this.connectedPlayers < MAX_PLAYERS)
		{
			System.out.println("Waiting for connections...");
			
			try {
				Socket s = this.serverSocket.accept();
				this.connectedPlayers++;
				System.out.println("Player #" + this.connectedPlayers + " has connected");
				this.doStream.writeInt(this.connectedPlayers);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
	}
	
	private class Connection implements Runnable {
		private Socket socket;
		
		private int playerID;
		private DataInputStream diStream;
		private DataOutputStream doStream;
		
		public Connection(Socket s, int id)
		{
			this.socket = s;
			this.playerID = id;
			
			try {
				this.diStream = new DataInputStream(socket.getInputStream());
				this.doStream = new DataOutputStream(socket.getOutputStream());
			} catch(IOException e) {
				System.out.println("IOException from ServerSideConnection constructor");
			}
		}
		
		@Override
		public void run()
		{
			
		}
		
	}
}
