package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client implements Runnable {
	
	private Socket socket;
	private int clientID;
	private DataInputStream diStream;
	private DataOutputStream doStream;
	
	private int playerID;
	
	public Client()
	{
		
	}
	
	public void connectToServer(InetAddress ip_address, int port)
	{
		try
		{
			this.socket = new Socket(ip_address, port);
			System.out.println("Client socket created.");
			
			this.diStream = new DataInputStream(socket.getInputStream());
			this.doStream = new DataOutputStream(socket.getOutputStream());
			
			this.clientID = this.diStream.readInt();
			System.out.println("You're player #" + this.clientID);
			
		}
		catch(IOException e) {
			System.out.println("Connection to server " + ip_address + " refused");
			e.printStackTrace();
		}
		
	}
	public void sendMessage(String message)
	{
		try {
			this.doStream.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[])
	{
		Client c = new Client();
		
		try {
			c.connectToServer(InetAddress.getByName("127.0.0.1"), 8765);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		while(true)
		{
			Scanner keyboard = new Scanner(System.in);
			System.out.println("Message to send: ");
			c.sendMessage(keyboard.nextLine());
		}
	}

	@Override
	public void run() {
		
	}

}
