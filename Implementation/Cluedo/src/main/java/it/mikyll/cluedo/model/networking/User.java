package it.mikyll.cluedo.model.networking;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class User {
	private static final Pattern PATTERN_USERNAME = Pattern.compile("^[a-zA-Z0-9]{3,15}$");
	private static final Pattern PATTERN_IP = Pattern.compile("^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	private String username;
	private boolean isReady;
	private InetAddress address;
	
	public User(String username)
	{
		this.username = username;
		this.isReady = false;
	}
	
	public User(String username, InetAddress address)
	{
		this.username = username;
		this.address = address;
		this.isReady = false;
	}
	public User(String username, String address)
	{
		this.username = username;
		try {
			this.address = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			System.out.println("Invalid address");
		}
		this.isReady = false;
	}
	
	public String getUsername() {return username;}
	public void setUsername(String username) {this.username= username;}
	public boolean isReady() {return isReady;}
	public void setReady(boolean isReady) {this.isReady = isReady;}
	public InetAddress getAddress() {return address;}
	public void setAddress(InetAddress address) {this.address = address;}
	
	public static List<User> stringToUserList(String s)
	{
		List<User> result = new ArrayList<User>();
		
		String[] split = s.split(";");
		for(int i = 0; i < split.length; i++)
		{
			String[] sReady = split[i].split(",");
			User u = new User(sReady[0]);
			u.setReady(Boolean.parseBoolean(sReady[1]));
			result.add(u);
		}
		
		return result;
	}
	public static String userListToString(List<User> users)
	{
		String result = "";
		
		for(int i = 0; i < users.size(); i++)
		{
			User u = users.get(i);
			result += u.getUsername() + "," + u.isReady();
			result += (i == users.size() - 1 ? "" : ";");
		}
		
		return result;
	}

	public static boolean validateUsername(String username)
	{
		return PATTERN_USERNAME.matcher(username).matches();
	}
	public static boolean validateIPv4(String address)
	{
		return PATTERN_IP.matcher(address).matches();
	}

}
