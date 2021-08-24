package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	
	private static final int MAX_LENGTH = 5;
	
	private ServerSocket serverSocket;
	
	private int connectedPlayers;
	private boolean acceptConnections; // ON/OFF
	private ArrayList<Socket> connections;
	
	private DataInputStream diStream;
	private DataOutputStream doStream;
	
	//private ServerSideConnection ssc;
	
	public Server()
	{
		this.acceptConnections = true;
		this.connectedPlayers = 1;
		this.connections = new ArrayList<Socket>();
		
		try {
			this.serverSocket = new ServerSocket(8765, MAX_LENGTH);
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
	
	
	/*private class ServerSideConnection implements Runnable {
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
	}*/
	
	public static void main(String args[])
	{
		Server s = new Server();
		s.acceptConnections();
	}
}
