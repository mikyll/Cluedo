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
	
	private ArrayList<String> ipAddresses;
	private ArrayList<String> nicknames;
	
	private TextArea textArea;
	private ArrayList<Label> playerList;
	private ArrayList<Label> readyList;
	
	private String nickname;
	private int playerID;
	
	public Server2(String nickname, TextArea textArea)
	{
		this.ipAddresses = new ArrayList<String>();
		this.nicknames = new ArrayList<String>();
		
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
                            	System.out.println("Player connesso: " + m.getContent());
                            	
                            	textArea.setText(textArea.getText() + m.getContent());
                            	// Server invia OK con numero del giocatore
                            	
                            	// Server invia la nuova lista dei player
                            	
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
	public void sendMessage(MessageType type, String nickname, String content)
	{
		if(type.equals(MessageType.CHAT))
		{
			// add timestamp to message
			
			// forward to every player connected
		}
			
	}
}
