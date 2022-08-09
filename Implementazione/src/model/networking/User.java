package model.networking;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class User {
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
}
