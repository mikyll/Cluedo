package model.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

import controller.ControllerMenu;
import model.networking.message.IMessageHandler;
import model.networking.message.Message;
import model.networking.message.MessageType;

public class ClientStream {
	private ClientListener clientListener;
	
	private OutputStream os;
    private ObjectOutputStream output;
    private InputStream is;
    private ObjectInputStream input;
	
    private String username;
    
    private IMessageHandler messageHandler;
    
    public ClientStream(String username, String address, int port, IMessageHandler messageHandler)
    {
    	this.username = username;
    	this.messageHandler = messageHandler;
    	
    	this.clientListener = new ClientListener(address, port);
    	this.clientListener.start();
    }
    
	private class ClientListener extends Thread {
		private Socket socket;
		private String address;
		private int port;
		
		public ClientListener(String address, int port)
		{
			this.address = address;
			this.port = port;
		}
		
		@Override
		public void run()
		{
			System.out.println("Client: running. Username: " + username);
			try {
				this.socket = new Socket(address, port);
				
				// NB: the order of these is important (client: output -> input)
				os = this.socket.getOutputStream();
				output = new ObjectOutputStream(os);
				is = this.socket.getInputStream();
				input = new ObjectInputStream(is);
				
				// send CONNECT_REQUEST message
				Message msg = new Message(MessageType.CONNECT_REQUEST, username, "");
				output.writeObject(msg);
				
				while(this.socket.isConnected())
				{
					Message incomingMsg = (Message) input.readObject();
					if(incomingMsg != null)
					{
						System.out.println("Client (" + this.getId() + "): received " + incomingMsg.toString()); // test
						switch(incomingMsg.getMsgType())
						{
							case CONNECT_OK:
							{
								messageHandler.handleMessage(incomingMsg);
								
								break;
							}
							case CONNECT_REFUSED:
							{
								messageHandler.handleMessage(incomingMsg);
								
								break;
							}
							case USER_JOINED:
							{
								messageHandler.handleMessage(incomingMsg);
								
								break;
							}
							case CHAT:
							{
								messageHandler.handleMessage(incomingMsg);
								
								break;
							}
							case DISCONNECT:
							{
								messageHandler.handleMessage(incomingMsg);
								
								break;
							}
							case READY:
							{
								messageHandler.handleMessage(incomingMsg);
								
								break;
							}
							case KICK:
							{
								messageHandler.handleMessage(incomingMsg);
								
								break;
							}
							case BAN:
							{
								messageHandler.handleMessage(incomingMsg);
								
								break;
							}
							default:
							{
								break;
							}
						}
					}
				}
			} catch(SocketException e) {
				System.out.println("Socket exception");
				if(e instanceof ConnectException)
				{
					messageHandler.handleMessage(
							new Message(MessageType.CONNECT_REFUSED, "", "", e.getMessage()));
				}
				else if(e.getMessage().equals("Connection reset"))
				{
					System.out.println("Stream closed");
				}
				else e.printStackTrace();
			} catch (IOException e) {
				System.out.println("Stream closed");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		
		}
	}
	
	public void sendReady(boolean ready)
	{
		Message msg = new Message(MessageType.READY, this.username, "" + ready);
		
		// send ready message
		this.sendMessage(msg);
	}
	
	private void sendMessage(Message message)
	{
		try {
			this.output.writeObject(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendChatMessage(String content)
	{
		Message msg = new Message(MessageType.CHAT, this.username, content);
		
		// send the chat message to everyone
		this.sendMessage(msg);
	}
	
	public void sendClose()
	{
		Message msg = new Message(MessageType.DISCONNECT, this.username, "");
		
		// send disconnect message
		this.sendMessage(msg);
	}
}
