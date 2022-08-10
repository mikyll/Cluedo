package model.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import controller.ControllerMenu;
import javafx.scene.control.Alert.AlertType;
import model.game.Game;
import model.networking.User;
import model.networking.message.IMessageHandler;
import model.networking.message.Message;
import model.networking.message.MessageType;

public class ServerStream {
	public static final int DEFAULT_PORT = 4321;
	
	private ServerListener serverListener;
	
	private MPState serverState = MPState.LOBBY;
	
	private String username;
	private int port;
	private int minUsers;
	private int maxUsers;
	private IMessageHandler messageHandler;
	
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<ObjectOutputStream> writers = new ArrayList<ObjectOutputStream>();
	private ArrayList<String> bannedUsernames = new ArrayList<String>();
	private ArrayList<InetAddress> bannedIPaddresses = new ArrayList<InetAddress>();
	
	private Game game;
	
	public ServerStream(String username, int port, int minUsers, int maxUsers, IMessageHandler msgHandler) throws IOException
	{
		this.username = username;
		this.port = port;
		this.minUsers = minUsers;
		this.maxUsers = maxUsers;
		this.messageHandler = msgHandler;
		
		User u = new User(username);
		u.setReady(true);
		this.users.add(u);
		// add the server writer (placeholder)
		writers.add(null);
		
		this.serverListener = new ServerListener(port);
		this.serverListener.start();
		
		// If we reach there, it means that everything went fine, therefore we can switch view (from the ControllerMenu)
	}
	public ServerStream() {
		this.port = DEFAULT_PORT;
		this.minUsers = 2;
		this.maxUsers = 6;
	}
	
	public void setMessageHandler(IMessageHandler msgHandler) {this.messageHandler = msgHandler;}
	
	public void startGame() {
		this.serverState = MPState.GAME;
		
		//this.game = new Game();
		
	}
	
	public void kickUser(String username) {
		// TO-DO
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
			try {
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
			}
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
	
	private class Handler extends Thread {
		private Socket socket;
		
		private InputStream is;
		private ObjectInputStream input;
		private OutputStream os;
        private ObjectOutputStream output;
        
        public Handler(Socket socket)
		{
			System.out.println("Server (" + this.getId() + "): connection accepted");
			this.socket = socket;
		}
		
		@Override
		public void run()
		{
			try {
				// NB: the order of these is important (server: input -> output)
				this.is = this.socket.getInputStream();
				this.input = new ObjectInputStream(this.is);
				this.os = this.socket.getOutputStream();
				this.output = new ObjectOutputStream(this.os);
				
				while(this.socket.isConnected())
				{
					Message incomingMsg = (Message) this.input.readObject();
					if(incomingMsg != null)
					{
						System.out.println("Server (" + this.getId() + "): received " + incomingMsg.toString());
						
						switch(incomingMsg.getMsgType())
						{
							case CONNECT_REQUEST:
							{
								Message mReply = new Message();
								
								// the user is banned?
								
								// the room is closed?
								
								// the room is full?
								
								// duplicate username?
								
								// connection can be accepted
								{
									User u = new User(incomingMsg.getUsername(), this.socket.getInetAddress());
									users.add(u);
									writers.add(this.output);
									
									mReply.setMsgType(MessageType.USER_JOINED);
									mReply.setUsername(incomingMsg.getUsername());
									forwardMessageToOthers(mReply);
									
									mReply.setMsgType(MessageType.CONNECT_OK);
									mReply.setUsername(username);
									mReply.setContent(User.userListToString(users));
									
									this.output.writeObject(mReply);
								}
								/*{
									// add user and writer to list
									User u = new User(incomingMsg.getUsername(), this.socket.getInetAddress());
									users.add(u);
									writers.add(this.output);
									controller.addUser(u);
									
									// forward to other users the new user joined
									mReply.setMsgType(MessageType.USER_JOINED);
									mReply.setNickname(incomingMsg.getNickname());
									forwardMessage(mReply);
									
									// create OK message, containing the updated user list
									mReply.setMsgType(MessageType.CONNECT_OK);
									mReply.setNickname(nickname);
									mReply.setContent(getUserList());
									
									// add the message to the chat textArea
									controller.addToTextArea(mReply.getTimestamp() + " " + incomingMsg.getNickname() + " has joined the room");
								}*/
								
								// 
								
								break;
							}
							case CHAT:
							{
								
								// TO-DO: forward
								
								break;
							}
							case READY:
							{
								for(User u : users)
								{
									if(u.getUsername().equals(incomingMsg.getUsername()))
									{
										u.setReady(Boolean.parseBoolean(incomingMsg.getContent()));
										break;
									}
								}
								
								forwardMessageToOthers(incomingMsg);
								
								break;
							}
							case DISCONNECT:
							{
								// remove user and writer from the list
								for(int i = 1; i < users.size(); i++)
								{
									if(users.get(i).getUsername().equals(incomingMsg.getUsername()))
									{
										users.remove(i);
										writers.remove(i);
										break;
									}
								}
								
								// forward
								forwardMessageToOthers(incomingMsg);
								
								socket.close();
								
								break;
							}
							default:
								break;
						}
						messageHandler.handleMessage(incomingMsg);
					}
				}
			} catch(SocketException e) {
				// "Connection reset" when the other endpoint disconnects
				if(e.getMessage().contains("Connection reset"))
					System.out.println("Stream closed");
				// "java.net.SocketException: Socket closed" - received DISCONNECT
				else if(e.getMessage().contains("Socket closed"))
					System.out.println("Socket closed");
				else e.printStackTrace();
			} catch (IOException e) {
				// if we close the socket when kick/disconnect is received, then:
				// when the server kicks an user, IOException is thrown because the thread which is listening, tries to read from the stream, but the socket has been closed from the other endpoint
				System.out.println("Errore stream (" + this.getId() + ")");
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void sendMessage(Message message)
	{
		// send the message to each user except the server
		for(int i = 1; i < this.writers.size(); i++)
		{
			try {
				this.writers.get(i).writeObject(message);
			} catch (IOException e) {
				System.out.println("IOException while trying to send message to client");
				e.printStackTrace();
			}
		}
	}
	
	public void sendChatMessage(String content)
	{
		Message msg = new Message(MessageType.CHAT, Message.getCurrentTimestamp(), this.username, content);
		
		// send the chat message to everyone
		this.sendMessage(msg);
	}
	
	private void forwardMessageToOthers(Message msg)
	{
		// forward the message to each connected client, except the one that sent the message first
		for(int i = 1; i < this.users.size(); i++)
		{
			if(!msg.getUsername().equals(this.users.get(i).getUsername()))
			{
				try {
					this.writers.get(i).writeObject(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Send a DISCONNECT message to each connected client, and then close the listen socket.
	 */
	public void sendClose()
	{
		Message msg = new Message(MessageType.DISCONNECT, Message.getCurrentTimestamp(), this.username, "Server room closed");

		// send the message to each user except the server (NB: it's not a normal sendMessage)
		for(int i = 1; i < this.writers.size(); i++)
		{
			msg.setUsername(this.users.get(i).getUsername());
			try {
				this.writers.get(i).writeObject(msg);
			} catch (IOException e) {
				// remove the writer at index i?
				e.printStackTrace();
			}
		}
		
		// close the socket
		this.serverListener.closeSocket();
	}
	
	/*
	 * Check if there are enough connected users to start a game, and they are all ready.
	 */
	public boolean canStart()
	{	
		for(User u : this.users)
		{
			if(!u.isReady())
				return false;
		}
		
		return this.users.size() >= this.minUsers;
	}
	
	public InetAddress getAddressFromUsername(String username)
	{
		for(User u : this.users)
		{
			if(u.getUsername().equals(username))
				return u.getAddress();
		}
		
		return null;
	}
	
	public String getUsernameFromAddress(InetAddress address)
	{
		for(User u : this.users)
		{
			if(u.getAddress().equals(address))
				return u.getUsername();
		}
		
		return "";
	}
}
