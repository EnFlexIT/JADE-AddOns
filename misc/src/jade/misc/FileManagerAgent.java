package jade.misc;

import java.util.logging.Level;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

public class FileManagerAgent extends Agent {

	private static Logger logger = Logger.getJADELogger(FileManagerAgent.class.getName());
	public static final String SERVICE_TYPE = "file-manager-service"; 
	
	protected void setup() {
		Object[] args = getArguments();
		
		// Get root path
		String root = null;
		if (args.length >= 1 && args[0] != null && args[0] instanceof String) {
			root = (String)args[0];
		}

		// Get download block size
		int downloadBlockSize = 0;
		if (args.length >= 2 && args[1] != null && args[1] instanceof Integer) {
			downloadBlockSize = (Integer)args[1];
		}

		// Init the FileManagerServer 
		FileManagerServer fileManagerServer = new FileManagerServer();
		fileManagerServer.init(this, root);
		if (downloadBlockSize > 0) {
			fileManagerServer.setDownloadBlockSize(downloadBlockSize);
		}
		
		// Register agent into DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType(SERVICE_TYPE);
		sd.setName(getLocalName());
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			logger.log(Level.SEVERE, "Error registering agent "+getAID()+" into DF", fe);
		}
	}
}
