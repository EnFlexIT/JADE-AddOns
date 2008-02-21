package jade.android;

/**
 * This interface is used in order to get callbacks when an activity connects or disconnects 
 * itself to/from the MicroRuntimeService through the JadeGateway class.
 * The typical usage of this interface is to develop an activity that implements
 * ConnectionListener and includes application specific implementation of callback methods
 * The onConnected implementation shall at least include the code in order to save the returned 
 * JadeGateway instance in a variable to be used when needed  
 * Note that the on Connected method is called from the onServiceConnected() method of MicroRuntimeServiceConnection object
 * after the call of JadeGateway.connect the performs the binding to the MicroRuntimeService
 * The onDisconnected() method shall be used to manage some problem with the MicroruntimeService and it is called
 * by the onServiceDisconnected() method of MicroRuntimeServiceConnection object 

 *  
 * 
 * @author Stefano Semeria Reply Cluster
 * @author Tiziana Trucco Telecomitalia
 *
 */
public interface ConnectionListener {

	/**
	 * Callback method called from onServiceConnection method of 
	 * MicroRuntimeServiceConnection object when JadeGateway.connect 
	 * is called from an activity
	 *  
	 * @param gateway
	 */
	public void onConnected(JadeGateway gateway);

	
	
	/**
	 * Callback method called from onServiceDisconnected() method of 
	 * MicroRuntimeServiceConnection object. Refer to android documentation
	 * in order to understand in which cases the onServiceDisconnected() 
	 * method is called
	 *  
	 * @param gateway
	 */
	public void onDisconnected();
}
