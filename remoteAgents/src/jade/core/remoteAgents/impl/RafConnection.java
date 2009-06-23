package jade.core.remoteAgents.impl;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * 
 * @author Telefónica
 *
 */
public  class RafConnection {
	protected OutputStream  os;
	protected InputStream is;
	
	/**
	 * 
	 * @param msg packet to be sent 
	 * @throws Exception
	 */
	public void sendPacket(RafPacket msg)throws Exception{
		if (os!=null){
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(msg);
		}else{
			System.err.println("I can't send the packet");
		}
	}
		
	
	/**
	 * 
	 * @param type  type of packet
	 * @param msg packet to be sent
	 * @throws Exception
	 */
	public void sendPacket (int type, RafPacket msg)throws Exception{
		msg.setPacketType(type);
		sendPacket(msg);
	}
	
	/**
	 * Receive a packet
	 * @return packet received
	 * @throws Exception if we do not receive a packet, throw Exception
	 */
	public RafPacket receivePacket()throws Exception{
		RafPacket received;
		if (is!= null){
			ObjectInputStream ois = new ObjectInputStream(is);
			received =  (RafPacket) ois.readObject();
			return received;
		}else{
			Exception e = new Exception();
			throw ( e);
		}
	}
	
	/**
	 * Close the Connection
	 * @throws Exception
	 */
	public void close() throws Exception{
		if (is != null) {
			is.close();
			is = null;
		}
		if (os != null) {
			os.close();
			os = null;
		}
	}
	
}