package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
	
	private ClientSideConnection csc;
	
	private int playerID;
	
	public Client()
	{
		
	}
	
	public void connectToServer() {
		this.csc = new ClientSideConnection();
		
	}
	
	private class ClientSideConnection {
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		
		public ClientSideConnection() {
			System.out.println("Client");
			try {
				this.socket = new Socket("localhost", 51234);
				this.dataIn = new DataInputStream(socket.getInputStream());
				this.dataOut = new DataOutputStream(socket.getOutputStream());
				
				playerID = this.dataIn.readInt();
				System.out.println("Connected to server as Player #" + playerID);
				
			} catch(IOException e) {
				System.out.println("IOException from ClientSideConnection constructor");
			}
		}
	}
	public static void main(String[] args) {
		Client c = new Client();
		c.connectToServer();
	}
}
