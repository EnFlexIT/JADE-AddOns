package jade.core.remoteAgents.impl;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
 * @author Telefónica
 *
 */
public class RafConnecionAgentConector extends RafConnection{
	
	public RafConnecionAgentConector (OutputStream os,InputStream is ){
		this.os = os;
		this.is = is;
	}
	
	public void close () throws Exception{
		super.close();
	}

}
