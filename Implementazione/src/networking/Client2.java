package networking;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class Client2 {
	
	private SimpleDateFormat tformatter;
	
	private DatagramSocket ds;
	private Thread tReceiver;
	private ByteArrayOutputStream outputStream;
	private ObjectOutputStream os;
	
	private ArrayList<String> ipAddresses;
	private ArrayList<String> nicknames;
	
	private VBox vboxMP;
	private VBox vboxCR;
	private Button buttonB;
	private TextArea textArea;
	private ArrayList<Label> playerList;
	private ArrayList<Label> readyList;
	
	private InetAddress serverIPaddress;
	private String nickname;
	private int playerID;
	
	public Client2(String nickname, InetAddress ipAddress, VBox vboxMP, VBox vboxCR, Button buttonB, TextArea textArea/*, ArrayList<Label> pl, ArrayList<Label> rl*/)
	{
		this.tformatter = new SimpleDateFormat("[HH:mm:ss]");
		this.outputStream = new ByteArrayOutputStream();
		try {
			this.os = new ObjectOutputStream(outputStream);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		this.nickname = nickname;
		this.serverIPaddress = ipAddress;
		this.textArea = textArea;
		/*this.playerList = pl;
		this.readyList = rl;*/
		
		try {
			this.ds = new DatagramSocket();
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		
		this.tReceiver = new Thread(new Runnable() {
			@Override
			public void run()
			{
				System.out.println("Client linstening...");
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
                        	
                            
                            if(m.getMsgType().equals(MessageType.CONNECTION_OK))
                            {
                            	vboxMP.setVisible(false);
                            	vboxCR.setVisible(true);
                            	buttonB.setDisable(false);
                            	
                            	String s = m.getTimestamp() + " Player #" + m.getContent() + " '" + m.getNickname() + "' successfully connected to the room.";
                        		textArea.setText(textArea.getText() + "\n" + s);
                        		
                            	// clean the loading gif, switch vbox, show the connected message on textArea
                            }
                            
							// per prima cosa manda una connect
							
							// if ok, salva playerID
							
							// if not ok, prompt message (example room full, nickname already used, ip already connected)
							
							// if player list, aggiorna player list
							
							// if ready, aggiorna ready list
							
							// if chat, aggiorna chat
							
							// if kick, esci dal multigiocatore + prompt message
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
	
	public void sendConnect(String nickname)
	{
		Date date = new Date(System.currentTimeMillis());
		String timestamp = this.tformatter.format(date);
		
		Message msg = new Message(MessageType.CONNECTION, timestamp, nickname, "");
		
		try {
			this.os.writeObject(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		byte[] data = this.outputStream.toByteArray();
		DatagramPacket sendPacket = new DatagramPacket(data, data.length, this.serverIPaddress, 9876);
		try {
			this.ds.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void sendChatMessage(String nickname, String message)
	{
		Date date = new Date(System.currentTimeMillis());
		String timestamp = this.tformatter.format(date);
		
	}
	public void sendReady(String nickname, String ready)
	{
		Message mdg = new Message(MessageType.READY, "", nickname, ready);
	}
	public void sendDisconnect()
	{
		//Message msg = new Message();
	}
	
	public static void main(String[] args)
	{
		SimpleDateFormat tformatter = new SimpleDateFormat("[HH:mm:ss]");
		Date date = new Date(System.currentTimeMillis());
		System.out.println(tformatter.format(date));
	}
}