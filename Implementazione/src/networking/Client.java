package networking;

public interface Client {
	public void startReceiving();
	public void sendConnectMessage();
	public void sendDisconnectMessage();
	public void sendChatMessage();
}
