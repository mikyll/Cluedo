package networking;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class Server2 {
	private DatagramSocket ds;
	private Thread tReceiver;
	
	private int connectedPlayers;
	private int maxConnectedPlayers = 6;
	private ArrayList<InetAddress> ipAddresses;
	private ArrayList<String> nicknames;
	
	private TextArea textArea;
	private ArrayList<Label> playerList;
	private ArrayList<Label> readyList;
	
	private String nickname;
	private int playerID;
	
	public Server2(String nickname, TextArea textArea, ArrayList<Label> playerList, ArrayList<Label> readyList)
	{
		this.ipAddresses = new ArrayList<InetAddress>();
		this.nicknames = new ArrayList<String>();
		
		this.connectedPlayers = 1;
		this.playerID = 1;
		this.nickname = nickname;
		
		this.textArea = textArea;
		
		try {
			this.ds = new DatagramSocket(9876);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
        //InetAddress ip = InetAddress.getLocalHost();
		this.tReceiver = new Thread(new Runnable() {
			@Override
			public void run()
			{
				System.out.println("Server linstening...");
				try {
                    while (true) {
                        synchronized (this)
                        {
  
                            byte[] incomingData = new byte[1024];
                            
                            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                            
                            // ricezione oggetto
                            ds.receive(incomingPacket);
                            
                            byte[] data = incomingPacket.getData();
                            
                            ByteArrayInputStream in = new ByteArrayInputStream(data);
                            ObjectInputStream is = new ObjectInputStream(in);
                            
                            Message m = (Message) is.readObject();
                            System.out.println("Message object received: " + m.getMsgType() + ", " + m.getNickname() + ", " + m.getContent());
                            
                            if(m.getMsgType().equals(MessageType.CONNECTION))
                            {
                            	System.out.println("Received connection request from player " + m.getNickname() + " (" + incomingPacket.getAddress().toString() + ")");
                            	
                            	// Server invia OK con numero del giocatore
                            	if(connectedPlayers == maxConnectedPlayers)
                            	{
                            		m = new Message(MessageType.CONNECTION_FAILED, "", nickname, "The room is full.");
                            	}
                            	if(!checkNicknameOk(m.getNickname()))
                            	{
                            		
                            		m = new Message(MessageType.CONNECTION_FAILED, "", nickname, "The nickname " + m.getNickname() + " is already taken.");
                            	}
                            	if(!checkIPOk(incomingPacket.getAddress()))
                            	{
                            		m = new Message(MessageType.CONNECTION_FAILED, "", nickname, "The address " + incomingPacket.getAddress() + " is already connected.");
                            	}
                            	else
                            	{
                            		connectedPlayers++;
                            		String s = m.getTimestamp() + " Player #" + connectedPlayers + " '" + m.getNickname() + "' successfully connected to the room.";
                            		textArea.setText(textArea.getText() + "\n" + s);
                            		
                            		m = new Message(MessageType.CONNECTION_OK, "", nickname, "" + connectedPlayers);
                            		
                            		
                            		
                            		// forward the connection to other clients
                            		
                            		// Server invia la nuova lista dei player
                            	}
                            }
                            if(m.getMsgType().equals(MessageType.DISCONNECTION))
                            {
                            	System.out.println("Player disconnesso");
                            	
                            	// Server invia la nuova lista dei player
                            }
                            if(m.getMsgType().equals(MessageType.READY))
                            {
                            	
                            	System.out.println("Player disconnesso");
                            	
                            	// Server invia la nuova lista dei player
                            }
                            if(m.getMsgType().equals(MessageType.CHAT))
                            {
                            	System.out.println("Player ha mandato un messaggio in chat");
                            	
                            	// forward a tutti tranne quello che l'ha inviato
                            	
                            	// aggiorna la text area del server
                            	String chatEntry = m.getTimestamp() + m.getNickname() + ": " + m.getContent();
                            	textArea.setText(textArea.getText() + "\n" + chatEntry);
                            }
                            
                            // Receive new message
                           /* DatagramPacket packet = new DatagramPacket(                          rd,
                                    rd.length);
                            ds.receive(packet);
  
                            // Convert byte data to string
                            String msg
                                = (new String(rd)).trim();
                            System.out.println("Client ("
                                               + sp1.getPort()
                                               + "):"
                                               + " "
                                               + msg);
  
                            // Exit condition
                            if (msg.equals("bye")) {
                                System.out.println("Client"
                                                   + " connection closed.");
                                break;
                            }*/
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println("Exception occured");
                    e.printStackTrace();
                }
				
			}
		});
		this.tReceiver.start();
	}
	public void sendChatMessage(MessageType type, String nickname, String content)
	{
		if(type.equals(MessageType.CHAT))
		{
			// add timestamp to message
			
			// forward to every player connected
		}
	}
	public void sendKick()
	{
		// send kick to the player
		
		// send update list to everyone else
	}
	public void stopServer()
	{
		
	}
	private boolean checkNicknameOk(String nickname)
	{
		boolean result = true;
		for(Label l : this.playerList)
			result = !l.getText().substring(2).equals(nickname);
		return result;
	}
	private boolean checkIPOk(InetAddress address)
	{
		boolean result = true;
		for(InetAddress ia : this.ipAddresses)
			result = !ia.equals(address);
		return result;
	}
}
