package networking;

public interface IServer {
	public void startReceiving();
	public void sendChatMessage();
	public void sendKickMessage();
	public void sendUpdatePlayersList();
}
