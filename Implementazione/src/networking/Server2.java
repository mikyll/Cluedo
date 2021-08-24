package networking;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

import javafx.scene.control.TextArea;

public class Server2 {
	DatagramSocket ds;
	Thread tReceiver;
	
	ArrayList<String> ipAddresses;
	ArrayList<String> nicknames;
	
	public Server2(TextArea textArea)
	{
		this.ipAddresses = new ArrayList<String>();
		this.nicknames = new ArrayList<String>();
		
		this.tReceiver = new Thread(new Runnable() {
			@Override
			public void run()
			{
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
                            	System.out.println("Player connesso");
                            	
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
                }
			}
		});
		this.tReceiver.start();
		try {
			this.tReceiver.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
