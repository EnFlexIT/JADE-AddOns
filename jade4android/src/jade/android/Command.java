package jade.android;

import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

public interface Command {
	public void execute(Object command) throws StaleProxyException,ControllerException, InterruptedException;
	public void execute(Object command, long timeout) throws StaleProxyException,ControllerException, InterruptedException;
	public void checkJADE() throws StaleProxyException,ControllerException,Exception;
	public void shutdownJADE();
	public String getAgentName();
	
}
