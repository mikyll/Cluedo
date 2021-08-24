package networking;

// https://www.geeksforgeeks.org/creating-an-asynchronous-multithreaded-chat-application-in-java/

import java.io.*;
import java.net.*;
import java.util.Scanner;
  
public class ChatClient {
  
    public static void main(String args[])
        throws IOException, InterruptedException
    {
  
        // create DatagramSocket and get ip
        DatagramSocket cs
            = new DatagramSocket(5334);
        InetAddress ip
            = InetAddress.getLocalHost();
  
        System.out.println("Running UnSyncChatClient.java");
  
        System.out.println("Client is Up....");
  
        // create a sender thread with a nested
        // runnable class definition
        Thread csend;
        csend = new Thread(new Runnable() {
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
  
                            // create datagram packet
                            // for new message
                            DatagramPacket sp
                                = new DatagramPacket(
                                    sd,
                                    sd.length,
                                    ip,
                                    1234);
  
                            // send the new packet
                            cs.send(sp);
  
                            String msg = new String(sd);
                            System.out.println("Client says: "
                                               + msg);
                            // exit condition
                            if (msg.equals("bye")) {
                                System.out.println("client "
                                                   + "exiting... ");
                                break;
                            }
                            System.out.println("Waiting for "
                                               + "server response...");
                        }
                    }
                }
                catch (IOException e) {
                    System.out.println("Exception occured");
                }
            }
        });
  
        // create a receiver thread with a nested
        // runnable class definition
        Thread creceive;
        creceive = new Thread(new Runnable() {
            @Override
            public void run()
            {
                try {
  
                    while (true) {
                        synchronized (this)
                        {
  
                            byte[] rd = new byte[1000];
  
                            // receive new message
                            DatagramPacket sp1
                                = new DatagramPacket(
                                    rd,
                                    rd.length);
                            cs.receive(sp1);
  
                            // convert byte data to string
                            String msg = (new String(rd)).trim();
                            System.out.println("Server: " + msg);
  
                            // exit condition
                            if (msg.equals("bye")) {
                                System.out.println("Server"
                                                   + " Stopped....");
                                break;
                            }
                        }
                    }
                }
                catch (IOException e) {
                    System.out.println("Exception occured");
                }
            }
        });
  
        csend.start();
        creceive.start();
  
        csend.join();
        creceive.join();
    }
}
