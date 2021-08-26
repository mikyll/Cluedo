package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ClientStream {
	
	private final int SERVER_PORT = 8001;
	
	private Thread tReceiver;
	private Socket socket;
	private int clientID;
	private DataInputStream diStream;
	private DataOutputStream doStream;
	
	private int playerID;
	
	public ClientStream(InetAddress ip_address)
	{
		try
		{
			this.socket = new Socket(ip_address, SERVER_PORT);
			System.out.println("Client socket created.");
			
			this.diStream = new DataInputStream(socket.getInputStream());
			this.doStream = new DataOutputStream(socket.getOutputStream());
			
			this.clientID = this.diStream.readInt();
			System.out.println("You're player #" + this.clientID);
			
			/*this.receiver = new Receiver(this.socket);
			this.receiver.run();*/
		}
		catch(IOException e) {
			System.out.println("Connection to server " + ip_address + " refused");
			e.printStackTrace();
		}
	}
	
	/*public void connectToServer(InetAddress ip_address, int port)
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
	}*/
	public void sendMessage(String message)
	{
		try {
			this.doStream.writeUTF(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Socket getSocket()
	{
		return this.socket;
	}
	
	
	private class Receiver implements Runnable {
		private Socket socket;
		
		private DataInputStream diStream;
		
		public Receiver(Socket s)
		{
			this.socket = s;
			
			try {
				this.diStream = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			while(true)
			{
				try {
					System.out.println(this.diStream.readUTF());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void main(String args[])
	{
		try {
			ClientStream c = new ClientStream(InetAddress.getByName("127.0.0.1"));
			
			while(true)
			{
				Scanner keyboard = new Scanner(System.in);
				System.out.println("Message to send: ");
				c.sendMessage(keyboard.nextLine());
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		
	}

}
