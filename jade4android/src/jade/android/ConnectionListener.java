package jade.android;

public interface ConnectionListener {
	public void onConnected(boolean isStarted);
	public void onDisconnected();
}
