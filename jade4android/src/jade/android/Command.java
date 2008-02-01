package jade.android;

import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

public interface Command {
	public void execute(Object command) throws StaleProxyException,ControllerException, InterruptedException;

}
