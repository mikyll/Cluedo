package it.mikyll.cluedo.model.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import it.mikyll.cluedo.controller.ControllerMenu;
import it.mikyll.cluedo.model.game.GameCluedo;
import it.mikyll.cluedo.model.networking.message.IMessageHandler;
import it.mikyll.cluedo.model.networking.message.Message;
import it.mikyll.cluedo.model.networking.message.MessageType;

public class ServerStream {
	public static final int DEFAULT_PORT = 4321;
	
	private ServerListener serverListener;
	
	private User ownerUser;
	private int port;
	private final int minUsers;
	private final int maxUsers;
	private boolean isOpen;

	// Message handlers
	private IMessageHandler connectRequestHandler;
	private IMessageHandler chatHandler;
	private IMessageHandler readyHandler;
	private IMessageHandler disconnectHandler;
	private IMessageHandler genericHandler;
	
	private ArrayList<User> users = new ArrayList<User>();
	private ArrayList<ObjectOutputStream> writers = new ArrayList<ObjectOutputStream>();
	
	private ArrayList<String> bannedUsernames = new ArrayList<String>();
	private ArrayList<InetAddress> bannedIPaddresses = new ArrayList<InetAddress>();
	
	private GameCluedo game;
	
	public ServerStream(String username, int port, int minUsers, int maxUsers, boolean isOpen) throws IOException
	{
		this.port = port;
		this.minUsers = minUsers;
		this.maxUsers = maxUsers;
		this.isOpen = isOpen;

		ownerUser = new User(username);
		ownerUser.setReady(true);
		this.users.add(ownerUser);
		// add the server writer (placeholder)
		writers.add(null);
		
		this.serverListener = new ServerListener(port);
	}

	public void startServer()
	{
		if (!this.serverListener.isAlive())
		{
			this.serverListener.start();
		}
		else
		{
			// TODO
			// Edit error handling?
			System.out.println("ServerStream.java: the server is already listening!");
		}
	}

	public void setConnectRequestMessageHandler(IMessageHandler msgHandler)
	{
		this.connectRequestHandler = msgHandler;
	}
	public void setChatMessageHandler(IMessageHandler msgHandler)
	{
		this.chatHandler = msgHandler;
	}
	public void setReadyMessageHandler(IMessageHandler msgHandler)
	{
		this.readyHandler = msgHandler;
	}
	public void setDisconnectMessageHandler(IMessageHandler msgHandler)
	{
		this.disconnectHandler = msgHandler;
	}
	public void setGenericMessageHandler(IMessageHandler msgHandler)
	{
		this.genericHandler = msgHandler;
	}

	public void startGame() {
		// TODO
		// this.game = new Game();
	}
	
	/*
	 * Kick out the User from the lobby, closing the connection and removing its writer stream from the list.
	 */
	public void sendKickUser(String username)
	{
		Message msg = new Message(MessageType.KICK, username, "You have been kicked out from the lobby");
		
		// send kick to everyone (the nickname indicates which user is getting kicked)
		this.sendMessage(msg);
		
		// remove user and writer
		for(int i = 1; i < this.users.size(); i++)
		{
			if(this.users.get(i).getUsername().equals(username))
			{
				this.users.remove(i);
				
				try {
					this.writers.get(i).close();
				} catch (IOException e) {}
				
				this.writers.remove(i);
				break;
			}
		}
	}
	
	public void sendBanUser(String username, String address)
	{
		if(username == null && address == null)
			return;
		
		InetAddress iAddress = null;
		Message msg = new Message();
		msg.setMsgType(MessageType.BAN);
		msg.setContent("You have been banned from the lobby");
		
		if(username != null)
		{
			this.bannedUsernames.add(username);
			msg.setUsername(username);
		}
		if(address != null)
		{
			try {
				iAddress = InetAddress.getByName(address);
			} catch (UnknownHostException e) {System.out.println("Address not found");}
			
			this.bannedIPaddresses.add(iAddress);
			if(msg.getUsername().isEmpty())
				msg.setUsername(this.getUsernameFromAddress(iAddress));
				
		}
		
		// send ban to everyone (the username indicates which user is getting banned)
		this.sendMessage(msg);
		
		// remove user and writer
		for(int i = 1; i < this.users.size(); i++)
		{
			if(this.users.get(i).getUsername().equals(username) || this.users.get(i).getAddress().equals(iAddress))
			{
				this.users.remove(i);
				this.writers.remove(i);
				break;
			}
		}
	}
	
	public boolean isBanned(String username, InetAddress address)
	{
		try {

		} catch (Exception e) {
			System.out.println("Error while checking ban: " + e.getMessage());
			return true;
		}

		if(username != null)
		{
			if (this.bannedUsernames.contains(username))
				return true;
		}
		if(address != null)
		{
			if (this.bannedIPaddresses.contains(address))
					return true;
		}
		return false;
	}
	
	public void removeBan(String username, String address)
	{
		if(username != null)
		{
			this.bannedUsernames.remove(username);
		}
		if(address != null)
		{
			try {
				this.bannedIPaddresses.remove(InetAddress.getByName(address));
			} catch (UnknownHostException e) {System.out.println("Address not found");}
		}
	}
	
	private class ServerListener extends Thread {
		private ServerSocket socketListener;
		
		public ServerListener(int port) throws IOException
		{
			this.socketListener = new ServerSocket(port);
			}
		
		@Override
		public void run()
		{
			System.out.println("Server (" + this.getId() + "): listening for connections on port " + port);
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
								if(isBanned(incomingMsg.getUsername(), socket.getInetAddress()))
								{
									mReply.setMsgType(MessageType.CONNECT_REFUSED);
									mReply.setContent("You've been banned from this lobby");
								}
								// the lobby is closed?
								else if(!isOpen)
								{
									mReply.setMsgType(MessageType.CONNECT_REFUSED);
									mReply.setContent("The lobby is closed");
								}
								// the lobby is full
								else if(users.size() == maxUsers)
								{
									mReply.setMsgType(MessageType.CONNECT_REFUSED);
									mReply.setContent("The lobby is full");
								}
								// duplicate username
								else if(checkDuplicateUsername(incomingMsg.getUsername()))
								{
									mReply.setMsgType(MessageType.CONNECT_REFUSED);
									mReply.setContent("Username '" + incomingMsg.getUsername() + "' already present");
								}	
								// invalid username
								else if(!ControllerMenu.validateUsername(incomingMsg.getUsername()))
								{
									mReply.setMsgType(MessageType.CONNECT_REFUSED);
									mReply.setContent("Invalid username. The username must be from 3 to 15 alphanumeric characters long.");
								}
								// connection can be accepted
								else {
									User u = new User(incomingMsg.getUsername(), this.socket.getInetAddress());
									users.add(u);
									writers.add(this.output);
									
									mReply.setMsgType(MessageType.USER_JOINED);
									mReply.setUsername(incomingMsg.getUsername());
									forwardMessageToOthers(mReply);
									
									mReply.setMsgType(MessageType.CONNECT_ACCEPTED);
									mReply.setUsername(ownerUser.getUsername());
									mReply.setContent(User.userListToString(users));

									if (connectRequestHandler != null)
										connectRequestHandler.handleMessage(incomingMsg);

									// TODO
									// remove after checking
									// messageHandler.handleMessage(incomingMsg);
								}
								
								// send reply
								this.output.writeObject(mReply);
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
									controller.addToTextArea(mReply.getTimestamp() + " " + incomingMsg.getNickname() + " has joined the lobby");
								}*/
								
								break;
							}
							case CHAT:
							{
								
								// TO-DO: forward

								if (chatHandler != null)
									chatHandler.handleMessage(incomingMsg);
								
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

								if (readyHandler != null)
									readyHandler.handleMessage(incomingMsg);
								
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

								if (disconnectHandler != null)
									disconnectHandler.handleMessage(incomingMsg);
								
								break;
							}
							default:
								if (genericHandler != null)
									genericHandler.handleMessage(incomingMsg);
								break;
						}
						
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
	
	public void setPrivacy(boolean open)
	{
		this.isOpen = open;
	}
	
	private boolean checkDuplicateUsername(String username)
	{
		for(User u : this.users)
		{
			if(u.getUsername().equals(username))
				return true; // username already present
		}
		return false;
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
		Message msg = new Message(MessageType.CHAT, ownerUser.getUsername(), content);
		
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
		Message msg = new Message(MessageType.DISCONNECT, ownerUser.getUsername(), "Server lobby closed");

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
