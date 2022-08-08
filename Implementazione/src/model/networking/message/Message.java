package model.networking.message;

import java.io.Serializable;

public class Message implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private MessageType msgType;
	private String timestamp;
	private String username;
	private String content;
	
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
	
	public MessageType getMsgType() {return msgType;}
	public void setMsgType(MessageType msgType) {this.msgType = msgType;}
	public String getTimestamp() {return timestamp;}
	public void setTimestamp(String timestamp) {this.timestamp = timestamp;}
	public String getUsername() {return username;}
	public void setUsername(String username) {this.username = username;}
	public String getContent() {return content;}
	public void setContent(String content) {this.content = content;}
}
