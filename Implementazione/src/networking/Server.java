package networking;

public interface Server {
	public void startReceiving();
	public void sendChatMessage();
	public void sendKickMessage();
	public void sendUpdatePlayersList();
}
