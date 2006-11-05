/**
 * 
 */
package it.pisa.jade.util;

/**
 * @author Domenico Trimboli
 *
 */
public enum Values {
	multicastPort("6030", Parameter.INTEGER),
	multicastIP("127.0.0.1", Parameter.IP), 
	jadePortNumber("1099", Parameter.INTEGER), 
	timeRefreshPlatformMax("20000", Parameter.LONG), 
	timeRefreshPlatformMin("5000", Parameter.LONG), 
	ttlPlatformRecord("2", Parameter.INTEGER), 
	federatorTimeSleep("5000", Parameter.LONG), 
	peerAgentPeriod("5000", Parameter.LONG), 
	reducedMessageSending(Parameter.YES, Parameter.BOOLEAN), 
	promptConsoleMessage(Parameter.NO, Parameter.BOOLEAN,false), 
	sharedDirectory(" ", Parameter.STRING),
	mobileInterplatformService(Parameter.NO, Parameter.BOOLEAN,false), 
	mobileInterplatformPath("C:\\", Parameter.STRING,false), 
	localIP("127.0.0.1", Parameter.IP,false),
	multicastTTL("127", Parameter.INTEGER,true),
	peerAddressPort("6000",Parameter.INTEGER);

	private Values(String s, int type,boolean mandatory) {
		this.value = s;
		this.type = type;
		this.mandatory=mandatory;
	}
	private Values(String s, int type) {
		this.value = s;
		this.type = type;
		this.mandatory=true;
	}

	private boolean mandatory;
	private String value;

	private int type;

	boolean isMandatory(){
		return mandatory;
	}
	
	int getType() {
		return type;
	}

	public String getDefaultValue() {
		return value;
	}
}
