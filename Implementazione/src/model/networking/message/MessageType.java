package model.networking.message;

public enum MessageType {
	NONE, CONNECT_REQUEST, CONNECT_OK, CONNECT_REFUSED, USER_JOINED, USER_LIST, READY, DISCONNECT, KICK, BAN, CHAT, START_GAME;
}