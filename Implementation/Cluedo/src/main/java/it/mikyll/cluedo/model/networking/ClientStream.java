package it.mikyll.cluedo.model.networking;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;

import it.mikyll.cluedo.model.networking.message.IMessageHandler;
import it.mikyll.cluedo.model.networking.message.Message;
import it.mikyll.cluedo.model.networking.message.MessageType;

public class ClientStream {
	private ClientListener clientListener;
	
	private OutputStream os;
    private ObjectOutputStream output;
    private InputStream is;
    private ObjectInputStream input;
	
    private String username;

	// Message handlers
	private IMessageHandler connectAcceptedHandler;
	private IMessageHandler connectRefusedHandler;
	private IMessageHandler userJoinedHandler;
	private IMessageHandler chatHandler;
	private IMessageHandler disconnectHandler;
	private IMessageHandler readyHandler;
	private IMessageHandler kickHandler;
	private IMessageHandler banHandler;
    private IMessageHandler genericHandler;
    private IMessageHandler startGameHandler;
    private IMessageHandler gameHandler;
    // True after sending DISCONNECT: the following stream error is expected
    private volatile boolean closing = false;
    
    public ClientStream(String username, String address, int port)
    {
    	this.username = username;
    	
    	this.clientListener = new ClientListener(address, port);
    }

	public void startClient()
	{
		if (!this.clientListener.isAlive())
		{
			this.clientListener.start();
		}
		else
		{
			// TODO
			System.out.println("ClientStream.java: the client is already listening!");
		}
	}

	public void setConnectAcceptedMessageHandler(IMessageHandler msgHandler)
	{
		this.connectAcceptedHandler = msgHandler;
	}
	public void setConnectRefusedMessageHandler(IMessageHandler msgHandler)
	{
		this.connectRefusedHandler = msgHandler;
	}
	public void setUserJoinedMessageHandler(IMessageHandler msgHandler)
	{
		this.userJoinedHandler = msgHandler;
	}
	public void setChatMessageHandler(IMessageHandler msgHandler)
	{
		this.chatHandler = msgHandler;
	}
	public void setDisconnectMessageHandler(IMessageHandler msgHandler)
	{
		this.disconnectHandler = msgHandler;
	}
	public void setReadyMessageHandler(IMessageHandler msgHandler)
	{
		this.readyHandler = msgHandler;
	}
	public void setKickMessageHandler(IMessageHandler msgHandler)
	{
		this.kickHandler = msgHandler;
	}
	public void setBanMessageHandler(IMessageHandler msgHandler)
	{
		this.banHandler = msgHandler;
	}
	public void setGenericMessageHandler(IMessageHandler msgHandler)
	{
		this.genericHandler = msgHandler;
	}
	public void setStartGameMessageHandler(IMessageHandler msgHandler)
	{
		this.startGameHandler = msgHandler;
	}
	public void setGameMessageHandler(IMessageHandler msgHandler)
	{
		this.gameHandler = msgHandler;
	}

	public String getUsername() {return username;}
    
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
			System.out.println("Client (" + this.getId() + "): running. Username: " + username);
			try {
				this.socket = new Socket(address, port);
				
				// NB: the order of these is important (client: output -> input)
				os = this.socket.getOutputStream();
				output = new ObjectOutputStream(os);
				is = this.socket.getInputStream();
				input = new ObjectInputStream(is);
				input.setObjectInputFilter(Message.FILTER);
				
				// send CONNECT_REQUEST message
				Message msg = new Message(MessageType.CONNECT_REQUEST, username, "");
				output.writeObject(msg);
				
				while(this.socket.isConnected())
				{
					Message incomingMsg = (Message) input.readObject();
					if(incomingMsg != null)
					{
						System.out.println("Client (" + this.getId() + "): received " + incomingMsg.toString()); // test
						// We are leaving (host closed, kicked or banned): the connection will be closed by the host
						if((incomingMsg.getMsgType() == MessageType.DISCONNECT || incomingMsg.getMsgType() == MessageType.KICK
								|| incomingMsg.getMsgType() == MessageType.BAN) && username.equals(incomingMsg.getUsername()))
							closing = true;
						switch(incomingMsg.getMsgType())
						{
							case CONNECT_ACCEPTED:
							{
								if (connectAcceptedHandler != null)
									connectAcceptedHandler.handleMessage(incomingMsg);

								break;
							}
							case CONNECT_REFUSED:
							{
								if (connectRefusedHandler != null)
									connectRefusedHandler.handleMessage(incomingMsg);

								break;
							}
							case USER_JOINED:
							{
								if (userJoinedHandler != null)
									userJoinedHandler.handleMessage(incomingMsg);
								
								break;
							}
							case CHAT:
							{
								if (chatHandler != null)
									chatHandler.handleMessage(incomingMsg);
								
								break;
							}
							case DISCONNECT:
							{
								if (disconnectHandler != null)
									disconnectHandler.handleMessage(incomingMsg);
								
								break;
							}
							case READY:
							{
								if (readyHandler != null)
									readyHandler.handleMessage(incomingMsg);
								
								break;
							}
							case KICK:
							{
								if (kickHandler != null)
									kickHandler.handleMessage(incomingMsg);
								
								break;
							}
							case BAN:
							{
								if (banHandler != null)
									banHandler.handleMessage(incomingMsg);
								
								break;
							}
							case START_GAME:
							{
								if (startGameHandler != null)
									startGameHandler.handleMessage(incomingMsg);

								break;
							}
							case GAME:
							{
								if (gameHandler != null)
									gameHandler.handleMessage(incomingMsg);

								break;
							}
							default:
							{
								if (genericHandler != null)
									genericHandler.handleMessage(incomingMsg);

								break;
							}
						}
					}
				}
			} catch(SocketException e) {
				System.out.println("Socket exception");
				if(e instanceof ConnectException)
				{
					connectRefusedHandler.handleMessage(
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

			// The connection with the server was lost (not because we left): tell it as a DISCONNECT of ourselves
			if (!closing && output != null && disconnectHandler != null)
				disconnectHandler.handleMessage(new Message(MessageType.DISCONNECT, username, "Connection with the host lost"));
		}
	}
	
	public void sendReady(boolean ready)
	{
		Message msg = new Message(MessageType.READY, this.username, "" + ready);
		
		// send ready message
		this.sendMessage(msg);
	}
	
	// Messages may be sent from different threads: writes must not overlap
	private synchronized void sendMessage(Message message)
	{

		try {
			if (output != null) {
				this.output.writeObject(message);
				this.output.reset();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendGameMessage(String content)
	{
		this.sendMessage(new Message(MessageType.GAME, this.username, content));
	}
	
	public void sendChatMessage(String content)
	{
		Message msg = new Message(MessageType.CHAT, this.username, content);
		
		// send the chat message to everyone
		this.sendMessage(msg);
	}
	
	public void sendClose()
	{
		this.closing = true;
		Message msg = new Message(MessageType.DISCONNECT, this.username, "");
		
		// send disconnect message
		this.sendMessage(msg);
	}
}
