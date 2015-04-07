package jade.misc;

import java.util.Map;
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
	public static final String ROOT_PATH_KEY = "rootPath";
	public static final String DOWNLOAD_BLOCK_SIZE_KEY = "downloadBlockSize";
	
	protected void setup() {
		Object[] args = getArguments();

		String root = null;
		int downloadBlockSize = 0;
		if (args.length == 1 && args[0] != null && args[0] instanceof Map) {
			// Extract the parameters from map (WADE mode) 
			Map params = (Map) args[0];
			root = (String) params.get(ROOT_PATH_KEY);
			String dbs = (String) params.get(DOWNLOAD_BLOCK_SIZE_KEY);
			if (dbs != null) {
				try {
					downloadBlockSize = Integer.parseInt(dbs);
				}
				catch (NumberFormatException nfe) {
					logger.log(Level.WARNING, "Error parsing downloadBlockSize "+dbs, nfe);
				}
			}
		}
		else {
			// Get root path
			if (args.length >= 1 && args[0] != null && args[0] instanceof String) {
				root = (String)args[0];
			}
	
			// Get download block size
			if (args.length >= 2 && args[1] != null && args[1] instanceof Integer) {
				downloadBlockSize = (Integer)args[1];
			}
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
