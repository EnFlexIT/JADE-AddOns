package test.common.testSuite.gui;

/**
   @author Giovanni Caire - TILAB
 */
class TSDaemonConnectionConfiguration {

	private String hostName = null;
	private boolean connect = false;
	private boolean changed = false;
	
	public TSDaemonConnectionConfiguration() {
	}
	
	public boolean getConnect() {
		return connect;
	}
	
	public void setConnect(boolean connect) {
		this.connect = connect;
	}
	
	public String getHostName() {
		return hostName;
	}
	
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}		
	
	public boolean getChanged() {
		return changed;
	}
	
	public void setChanged(boolean changed) {
		this.changed = changed;
	}
}
