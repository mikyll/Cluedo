package networking;

import java.net.*;
import java.io.*;
import java.util.*;
  
public class ChatServer {
  
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