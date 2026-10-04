package it.mikyll.cluedo.model.networking.message;

import java.io.ObjectInputFilter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
	private static final long serialVersionUID = 1L;

	// Only Message objects can be read from the network: deserializing arbitrary classes would be unsafe.
	// NB: no maxrefs/maxbytes limits, since they count everything read during the whole connection
	public static final ObjectInputFilter FILTER = ObjectInputFilter.Config.createFilter(
			"it.mikyll.cluedo.model.networking.message.Message;"
			+ "it.mikyll.cluedo.model.networking.message.MessageType;"
			+ "java.lang.Enum;java.lang.String;maxdepth=5;!*");
	
	private static SimpleDateFormat tformatter = new SimpleDateFormat("[HH:mm:ss]");
	
	private MessageType msgType = MessageType.NONE;
	private String timestamp = "";
	private String username = "";
	private String content = "";
	
	public Message(String type, String timestamp, String username, String content)
	{
		this.msgType = MessageType.valueOf(type);
		this.timestamp = timestamp;
		this.username = username;
		this.content = content;
	}
	public Message(MessageType type, String timestamp, String username, String content)
	{
		this.msgType = type;
		this.timestamp = timestamp;
		this.username = username;
		this.content = content;
	}
	public Message(String type, String username, String content)
	{
		this.msgType = MessageType.valueOf(type);
		this.timestamp = getCurrentTimestamp();
		this.username = username;
		this.content = content;
	}
	public Message(MessageType type, String username, String content)
	{
		this.msgType = type;
		this.timestamp = getCurrentTimestamp();
		this.username = username;
		this.content = content;
	}
	public Message()
	{
		this.msgType = MessageType.NONE;
		this.timestamp = getCurrentTimestamp();
		this.username = "";
		this.content = "";
	}
	
	public MessageType getMsgType() {return msgType;}
	public void setMsgType(MessageType msgType) {this.msgType = msgType;}
	public String getTimestamp() {return timestamp;}
	public void setTimestamp(String timestamp) {this.timestamp = timestamp;}
	public String getUsername() {return username;}
	public void setUsername(String username) {this.username = username;}
	public String getContent() {return content;}
	public void setContent(String content) {this.content = content;}
	
	public String toString()
	{
		return this.timestamp + " " + this.username + "(" + this.msgType.toString() + "): " + this.content;
	}
	
	public static String getCurrentTimestamp()
	{
		Date date = new Date(System.currentTimeMillis());
		String timestamp = tformatter.format(date);
		
		return timestamp;
	}
}
