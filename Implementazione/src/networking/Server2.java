package networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class Server2 {
	private final int SERVER_PORT = 9876;
	private final int CLIENT_PORT = 9875;
	
	private SimpleDateFormat tformatter;
	
	private DatagramSocket ds;
	private Thread tReceiver;
	private ByteArrayOutputStream outputStream;
	private ObjectOutputStream os;
	
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
		this.tformatter = new SimpleDateFormat("[HH:mm:ss]");
		this.outputStream = new ByteArrayOutputStream();
		try {
			this.os = new ObjectOutputStream(outputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		this.ipAddresses = new ArrayList<InetAddress>();
		this.nicknames = new ArrayList<String>();
		
		this.playerList = playerList;
		this.readyList = readyList;
		
		this.connectedPlayers = 1;
		this.playerID = 1;
		this.nickname = nickname;
		
		this.textArea = textArea;
		
		try {
			this.ds = new DatagramSocket(SERVER_PORT);
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
                                                        
                            ds.receive(incomingPacket);
                            
                            byte[] data = incomingPacket.getData();
                            
                            ByteArrayInputStream in = new ByteArrayInputStream(data);
                            ObjectInputStream is = new ObjectInputStream(in);
                            
                            Message m = (Message) is.readObject();
                            System.out.println("Server: message object received. " + m.getMsgType() + ", " + m.getTimestamp() + ", " + m.getNickname() + ", " + m.getContent()); // test
                            
                            if(m.getMsgType().equals(MessageType.CONNECTION))
                            {
                            	System.out.println("Received CONNECTION " + m.getNickname() + " (" + incomingPacket.getAddress().toString() + ")");
                            	
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
                            	else // CONNECTION_OK
                            	{
                            		connectedPlayers++;
                            		ipAddresses.add(incomingPacket.getAddress());
                            		String s = m.getTimestamp() + " Player #" + connectedPlayers + " '" + m.getNickname() + "' successfully connected to the room.";
                            		textArea.setText(textArea.getText() + "\n" + s);
                            		m = new Message(MessageType.CONNECTION_OK, "", nickname, "" + connectedPlayers);
                            	}
                            	// send the message
                            	os.writeObject(m);
                            	data = outputStream.toByteArray();
                        		DatagramPacket sendPacket = new DatagramPacket(data, data.length, incomingPacket.getAddress(), CLIENT_PORT);
                        		try {
                        			ds.send(sendPacket);
                        		} catch (IOException e) {
                        			e.printStackTrace();
                        		}
                        		
                            	// forward the connection to other clients
                            		
                            	// Server invia la nuova lista dei player a tutti
                            }
                            if(m.getMsgType().equals(MessageType.DISCONNECTION))
                            {
                            	System.out.println("Player disconnesso");
                            	
                            	// Server invia la nuova lista dei player
                            }
                            if(m.getMsgType().equals(MessageType.READY))
                            {
                            	boolean ready = m.getContent().equals("yes") ? true : false;
                            	System.out.println("Player ready/not read");
                            	
                            	// Server invia la nuova lista dei player
                            }
                            if(m.getMsgType().equals(MessageType.CHAT))
                            {
                            	System.out.println("Player " + m.getNickname() + " sent the message: " + m.getContent());
                            	
                            	textArea.setText(textArea.getText() + "\n" + m.getTimestamp() + " " + m.getNickname() + ": " + m.getContent());
                            	
                            	Date date = new Date(System.currentTimeMillis());
                        		String timestamp = tformatter.format(date);
                        		String forwardMessage = m.getContent();
                            	m = new Message(MessageType.CHAT, timestamp, nickname, forwardMessage);
                            	                            	
                            	// forward a tutti tranne quello che l'ha inviato
                            	os.writeObject(m);
                            	data = outputStream.toByteArray();
                            	for(int i = 1; i < playerList.size(); i++)
                            	{
                            		if(!playerList.get(i).getText().substring(4).equals(m.getNickname()))
                            		{
                            			DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddresses.get(i), CLIENT_PORT);
                                		try {
                                			ds.send(sendPacket);
                                		} catch (IOException e) {
                                			e.printStackTrace();
                                		}
                            		}
                            	}
                            	
                            }
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
	
	public void startReceiving()
	{
		
	}
	public void sendChatMessage(String nickname, String content)
	{
		if(this.connectedPlayers == 1)
			return;
		
		Date date = new Date(System.currentTimeMillis());
		String timestamp = tformatter.format(date);
    	Message m = new Message(MessageType.CHAT, timestamp, nickname, content);
    	// forward a tutti tranne quello che l'ha inviato
    	try {
			os.writeObject(m);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
    	byte[] data = outputStream.toByteArray();
    	for(int i = 1; i < this.playerList.size(); i++)
    	{
    		DatagramPacket sendPacket = new DatagramPacket(data, data.length, ipAddresses.get(i), CLIENT_PORT);
        	try {
        		ds.send(sendPacket);
        	} catch (IOException e) {
        		e.printStackTrace();
        	}
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
