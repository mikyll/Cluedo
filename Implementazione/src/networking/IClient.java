package networking;

public interface IClient {
	public void startReceiving();
	public void sendConnectMessage();
	public void sendDisconnectMessage();
	public void sendChatMessage();
}
