package jade.core.remoteAgents.impl;

import jade.util.leap.Serializable;
/**
 * 
 * @author Telefónica 
 *
 */
public class RafPacket implements Serializable {


	private static final long serialVersionUID = -8889137455646658025L;

	//************** TYPES OF PACKETS *********************
	
	public static final int MESSAGE_JOIN = 0;

	public static final int JOIN_OK = 1;
	
	public static final int JOIN_NOK = 2;
	
	public static final int MESSAGE_OUT= 3;
	
	public static final int MESSAGE_IN = 4; 	
	
	public static final int MESSAGE_LEAVE= 5;
	
	public static final int MESSAGE_REMOTE_AGENT_PROXY_DEAD= 6;
	
	public static final int MESSAGE_RECONNECT= 7;
		
	
	private int packetType;
	
	//***** NAMES ASSOCIATED WITH THE PACKET TYPE ***********
	
	private static final String[] packetTypes = new String[8];
	static { 
		packetTypes[MESSAGE_JOIN]="MESSAGE_JOIN";
		packetTypes[JOIN_OK]="JOIN_OK";
		packetTypes[JOIN_NOK]="JOIN_NOK";
		packetTypes[MESSAGE_OUT]="MESSAGE_OUT";
		packetTypes[MESSAGE_IN]="MESSAGE_IN";
		packetTypes[MESSAGE_LEAVE]="MESSAGE_LEAVE";
		packetTypes[MESSAGE_REMOTE_AGENT_PROXY_DEAD]="MESSAGE_REMOTE_AGENT_PROXY_DEAD";
		packetTypes[MESSAGE_RECONNECT]="MESSAGE_RECONNECT";
	}
	
	// array of String
	private String[] info;
	
	// ACLMessage serialized
	private byte[] aclMessage; 
	
	/// **************************** CONSTRUCTORS *********************
	public RafPacket(){
		
	}
	
	/**
	 * Constructor
	 * @param type type of package we will send: MESSAGE_JOIN,JOIN_OK,JOIN_NOK,MESSAGE_OUT,
	 * MESSAGE_IN, MESSAGE_LEAVE or MESSAGE_REMOTE_AGENT_PROXY_DEAD
	 */
	public RafPacket(int type){
		this.packetType= type;
	}
	
	/**
	 * Constructor
	 * @param type: type of package we will send: MESSAGE_JOIN,JOIN_OK,JOIN_NOK,MESSAGE_OUT,
	 * MESSAGE_IN, MESSAGE_LEAVE or MESSAGE_REMOTE_AGENT_PROXY_DEAD
	 * @param info: array of String
	 */
	public RafPacket(int type,String[]info){
		this.packetType= type;
		this.info = info;
	}
	
	/**
	 * Constructor
	 * @param type: type of package we will send: MESSAGE_JOIN,JOIN_OK,JOIN_NOK,MESSAGE_OUT,
	 * MESSAGE_IN, MESSAGE_LEAVE or MESSAGE_REMOTE_AGENT_PROXY_DEAD
	 * @param aclMessage: serialized message 
	 */
	public RafPacket(int type,byte[] aclMessage){
		this.packetType= type;
		this.aclMessage = aclMessage;
	}
	
	/// **************************** 	METHODS GET/SET *********************
	
	/**
	 * get the type of the packet
	 * 
	 * @return the Packet Type
	 */
	public int getPacketType(){
		return this.packetType;
	}
	
	/**
	 * defined the type of packet 
	 * @param type 
	 */
	public void setPacketType(int type){
		this.packetType= type;
	}
	
	/**
	 * get the name of the type of packet
	 * @return String 
	 */
	public String getStringPacketType(){
		return packetTypes[getPacketType()];
	}
	
	/**
	 * add to the package info
	 * @param info String[] with  packet info
	 */
	public void setInfo(String[] info){
		this.info = info;
	}
	
	/**
	 * get information from a packet
	 * @return  String[] with info packet
	 */
	public String[] getInfo(){
		return info;
	}
	
	/**
	 * get a serialized ACLMessage from a packet
	 * @return serialized ACLMessage
	 */
	public byte[] getACLMessage(){
		return aclMessage;
	}
	
	/**
	 * add to the package a serialized ACLMessage
	 * @param aclMessage : serialized ACLMessage
	 */
	public void setACLMessage(byte[] aclMessage){
		this.aclMessage = aclMessage;
	}
}
