package networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class Client2 {
	
	private DatagramSocket ds;
	private Thread tReceiver;
	
	private ArrayList<String> ipAddresses;
	private ArrayList<String> nicknames;
	
	private TextArea textArea;
	private ArrayList<Label> playerList;
	private ArrayList<Label> readyList;
	
	private int playerID;
	
	public Client2(String ipAddress, TextArea textArea, ArrayList<Label> playerList, ArrayList<Label> readyList)
	{
		this.tReceiver = new Thread(new Runnable() {
			@Override
			public void run()
			{
				
				
			}
		});
		
		this.tReceiver.start();
		try {
			this.tReceiver.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendChatMessage(String message)
	{
		
	}
	public void sendReady(boolean ready)
	{
		
	}
	public void sendDisconnect()
	{
		
	}
}
