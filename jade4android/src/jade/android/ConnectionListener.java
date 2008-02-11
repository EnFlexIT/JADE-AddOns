package jade.android;

public interface ConnectionListener {
	public void onConnected(JadeGateway gateway);
	public void onDisconnected();
}
