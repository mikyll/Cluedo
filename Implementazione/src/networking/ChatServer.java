package networking;

import java.net.*;
import java.io.*;
import java.util.*;

import javafx.scene.control.TextArea;
  
public class ChatServer {
	
	DatagramSocket ds;
	TextArea textArea;
	
	Thread threadReceiver;
	
	ArrayList<String> connectedPlayers;
	ArrayList<String> ipList;
	
	public ChatServer(TextArea textArea)
	{
		this.ds = new DatagramSocket(8765);
		InetAddress ip = InetAddress.getLocalHost();
		
		this.connectedPlayers = new ArrayList<String>();
		this.ipList = new ArrayList<String>();
		this.connectedPlayers.add("Miky");
		this.ipList.add("192.168.1.3");
		for(int i = 1; i < 6; i++)
		{
			this.connectedPlayers.add("");
		}
			
		this.textArea = textArea;
		System.out.printf("Server is running...");
		
		Thread sreceive;
        sreceive = new Thread(new Runnable() {
        	@Override
            public void run()
            {
                try {
                    while (true) {
                        synchronized (this)
                        {
  
                            byte[] rd = new byte[1000];
  
                            // Receive new message
                            DatagramPacket sp1 = new DatagramPacket(rd, rd.length);
                            ds.receive(sp1);
  
                            // Convert byte data to string
                            String msg = (new String(rd)).trim();
                            
                            if(msg.startsWith("Connection"))
                            {
                            	boolean alreadyConnected = false;
                            	int freePos = -1;
                            	for(int i = 5; i > 0; i++)
                            	{
                            		String slot = connectedPlayers.get(i);
                            		if(slot.isEmpty())
                            			freePos = i;
                            		else if(slot.split("_")[0].equals(sp1.getAddress().toString()))
                            		{
                            			alreadyConnected = true;
                            			break;
                            		}
                            	}
                            	
                            	if(!alreadyConnected && freePos != -1)
                            	{
                            		connectedPlayers.set(freePos, msg)
                            		// gli invia il suo playerID
                            	}
                            		
                            }
                            
                            System.out.println("Client (" + sp1.getPort() + "):" + msg);
                            
                            textArea.setText(textArea.getText() + "\n" + msg);
                            
                            // Exit condition
                            if (msg.equals("bye")) {
                                System.out.println("Client" + " connection closed.");
                                break;
                            }
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println("Exception occured");
                }
            }
        });
	}
	
	public synchronized void sendMessage(String msg)
	{
		byte[] sd = msg.getBytes();
		
		DatagramPacket sp = new DatagramPacket(sd, sd.length, ip, 5334);

            // send the new packet
            ss.send(sp);

            String msg = new String(sd);
            System.out.println("Server says: "
                               + msg);

            // exit condition
            if ((msg).equals("bye")) {
                System.out.println("Server"
                                   + " exiting... ");
                break;
            }
            System.out.println("Waiting for"
                               + " client response... ");
        }
	}
	
    public static void main(String args[])
        throws IOException, InterruptedException
    {
  
        // Create DatagramSocket and get ip
        DatagramSocket ss = new DatagramSocket(1234);
        InetAddress ip = InetAddress.getLocalHost();
  
        System.out.println("Running UnSyncChatServer.java");
  
        System.out.println("Server is Up....");
  
        // Create a sender thread
        // with a nested runnable class definition
        Thread ssend;
        ssend = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    Scanner sc = new Scanner(System.in);
                    while (true) {
                        synchronized (this)
                        {
                            byte[] sd = new byte[1000];
  
                            // scan new message to send
                            sd = sc.nextLine().getBytes();
                            DatagramPacket sp
                                = new DatagramPacket(
                                    sd,
                                    sd.length,
                                    ip,
                                    5334);
  
                            // send the new packet
                            ss.send(sp);
  
                            String msg = new String(sd);
                            System.out.println("Server says: "
                                               + msg);
  
                            // exit condition
                            if ((msg).equals("bye")) {
                                System.out.println("Server"
                                                   + " exiting... ");
                                break;
                            }
                            System.out.println("Waiting for"
                                               + " client response... ");
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println("Exception occured");
                }
            }
        });
  
        Thread sreceive;
        sreceive = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
                    while (true) {
                        synchronized (this)
                        {
  
                            byte[] rd = new byte[1000];
  
                            // Receive new message
                            DatagramPacket sp1
                                = new DatagramPacket(
                                    rd,
                                    rd.length);
                            ss.receive(sp1);
  
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
                            }
                        }
                    }
                }
                catch (Exception e) {
                    System.out.println("Exception occured");
                }
            }
        });
  
        ssend.start();
        sreceive.start();
  
        ssend.join();
        sreceive.join();
    }
}