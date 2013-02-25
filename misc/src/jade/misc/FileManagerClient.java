package jade.misc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;

import jade.content.lang.Codec.CodecException;
import jade.content.lang.leap.LEAPCodec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.OntologyException;
import jade.content.onto.UngroundedException;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;
import jade.util.Logger;
import jade.wrapper.gateway.DynamicJadeGateway;
import jade.wrapper.gateway.JadeGateway;

public class FileManagerClient {

	private static final String ACL_USERDEF_TERMINATED_SESSION = "iterated-fipa-request-terminated-session";
	private static final String TIMEOUT = "timeout";
	private static final long DEFAULT_LISTFILES_TIMEOUT = 30000;
	private static final long DEFAULT_DOWNLOADBLOCK_TIMEOUT = 30000;
	
	private static Logger logger = Logger.getJADELogger(FileManagerClient.class.getName());
	private static long cnt = 0;
	
	private AID server;
	private Connector myConnector;
	private long listFilesTimeout;
	private long dowloadBlockTimeout;
	
	public FileManagerClient(AID server) {
		this(server, JadeGateway.getDefaultGateway());
	}
	
	public FileManagerClient(AID server, DynamicJadeGateway djg) {
		init(server);
		myConnector = new GatewayBasedConnector(djg);
	}
	
	public FileManagerClient(AID server, Agent agent) {
		init(server);
		myConnector = new AgentBasedConnector(agent);
	}

	private void init(AID server) {
		this.server = server;
		this.listFilesTimeout = 0;
		this.dowloadBlockTimeout = 0;
		
		// Initialize java type preservation
		if (System.getProperty(SLCodec.PRESERVE_JAVA_TYPES) == null) {
			System.setProperty(SLCodec.PRESERVE_JAVA_TYPES, "true");
		}
	}
	
	public long getListFilesTimeout() {
		if (listFilesTimeout != 0) {
			return listFilesTimeout;
		} else {
			return DEFAULT_LISTFILES_TIMEOUT;
		}
	}

	public void setListFilesTimeout(long listFilesTimeout) {
		this.listFilesTimeout = listFilesTimeout;
	}

	public long getDowloadBlockTimeout() {
		if (dowloadBlockTimeout != 0) {
			return dowloadBlockTimeout;
		} else {
			return DEFAULT_DOWNLOADBLOCK_TIMEOUT;
		}
	}

	public void setDowloadBlockTimeout(long dowloadBlockTimeout) {
		this.dowloadBlockTimeout = dowloadBlockTimeout;
	}
	
	public List<FileInfo> listFiles(String dirPathName) throws Exception {
		return myConnector.listFiles(dirPathName);
	}
	
	public InputStream download(String filePathName) throws Exception {
		return new FileManagerInputStream(filePathName);
	}
	
	public InputStream downloadMultiple(List<String> filesPathName) throws Exception {
		return new FileManagerInputStream(filesPathName);
	}
	

	private ACLMessage prepareGetFilesListRequest(Agent agent, String dirPathName) throws CodecException, OntologyException {
		ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
		req.setSender(agent.getAID());
		req.addReceiver(server);
		req.setOntology(FileManagementOntology.getInstance().getName());
		req.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		req.setReplyByDate(new Date(System.currentTimeMillis() + getListFilesTimeout()));
		req.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);

		Action action = new Action();
		action.setActor(server);
		action.setAction(new GetFilesListAction(dirPathName));
		
		agent.getContentManager().fillContent(req, action);
		
		return req;
	}

	private static List extractFilesList(Agent agent, ACLMessage msg) throws UngroundedException, CodecException, OntologyException {
		Result result = (Result)agent.getContentManager().extractContent(msg);
		Object value = result.getValue();
		if (value != null) {
			return ((jade.util.leap.ArrayList)value).toList();
		}
		return null;
	}

	private synchronized static String createConversationId(String name) {
		return "C-"+name+'-'+System.currentTimeMillis()+'-'+(cnt++);
	}

	private ACLMessage prepareDownloadRequest(Agent agent, String conversationId, String filePathName) throws CodecException, OntologyException {
		ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
		req.setSender(agent.getAID());
		req.addReceiver(server);
		req.setOntology(FileManagementOntology.getInstance().getName());
		req.setLanguage(LEAPCodec.NAME);
		req.setReplyByDate(new Date(System.currentTimeMillis() + getDowloadBlockTimeout()));
		req.setProtocol(FIPANames.InteractionProtocol.ITERATED_FIPA_REQUEST);
		req.setConversationId(conversationId);

		Action action = new Action();
		action.setActor(server);
		action.setAction(new DownloadFileAction(filePathName));

		agent.getContentManager().fillContent(req, action);
		
		return req;
	}
	
	private ACLMessage prepareDownloadMultipleRequest(Agent agent, String conversationId, List<String> filesPathName) throws CodecException, OntologyException {
		ACLMessage req = new ACLMessage(ACLMessage.REQUEST);
		req.setSender(agent.getAID());
		req.addReceiver(server);
		req.setOntology(FileManagementOntology.getInstance().getName());
		req.setLanguage(LEAPCodec.NAME);
		req.setReplyByDate(new Date(System.currentTimeMillis() + getDowloadBlockTimeout()));
		req.setProtocol(FIPANames.InteractionProtocol.ITERATED_FIPA_REQUEST);
		req.setConversationId(conversationId);

		Action action = new Action();
		action.setActor(server);
		action.setAction(new DownloadMultipleFilesAction(filesPathName));

		agent.getContentManager().fillContent(req, action);
		
		return req;
	}
	
	
	

	private DownloadInfo extractDownloadInfo(ACLMessage msg) {
		byte[] content = msg.getByteSequenceContent();

		String completedString = msg.getUserDefinedParameter(ACL_USERDEF_TERMINATED_SESSION);
		boolean completed = completedString!=null;
		
		return new DownloadInfo(content, completed);
	}
	
	private ACLMessage prepareCancelDownloadRequest(Agent agent, String conversationId) {
		ACLMessage cancel = new ACLMessage(ACLMessage.CANCEL);
		cancel.setSender(agent.getAID());
		cancel.addReceiver(server);
		cancel.setOntology(FileManagementOntology.getInstance().getName());
		cancel.setLanguage(LEAPCodec.NAME);
		cancel.setProtocol(FIPANames.InteractionProtocol.ITERATED_FIPA_REQUEST);
		cancel.setConversationId(conversationId);
		return cancel;
	}

	
	/**
	 * Inner interface Connector.
	 * This interface hides the differences in the code to execute when this FileManagerClient operates 
	 * inside an agent or inside a NON-agent piece of code.
	 */
	private interface Connector {
		public List<FileInfo> listFiles(final String dirPathName) throws Exception;
		public void sendCancelDownload(final String conversationId) throws Exception;
		public DownloadInfo download(final String conversationId, final String filePathName) throws Exception;
		public DownloadInfo downloadMultiple(final String conversationId, final List<String> filesPathName) throws Exception;
	} // END of inner interface Connector

	
	/**
	 * Inner class AgentBasedConnector
	 */
	private class AgentBasedConnector implements Connector {
		private Agent myAgent;
		
		AgentBasedConnector(Agent a) {
			myAgent = a;
			myAgent.getContentManager().registerLanguage(new SLCodec());
			myAgent.getContentManager().registerLanguage(new LEAPCodec());
			myAgent.getContentManager().registerOntology(FileManagementOntology.getInstance());
		}

		public List<FileInfo> listFiles(final String dirPathName) throws Exception {
			ACLMessage request = FileManagerClient.this.prepareGetFilesListRequest(myAgent, dirPathName);
			ACLMessage reply = FIPAService.doFipaRequestClient(myAgent, request);
			if (reply != null) {
				return FileManagerClient.this.extractFilesList(myAgent, reply);
			} else {
				throw new Exception(TIMEOUT);
			}
		}

		public void sendCancelDownload(final String conversationId) throws Exception {
			ACLMessage cancel = FileManagerClient.this.prepareCancelDownloadRequest(myAgent, conversationId);
			myAgent.send(cancel);
		}

		public DownloadInfo download(final String conversationId, final String filePathName) throws Exception {
			ACLMessage request = FileManagerClient.this.prepareDownloadRequest(myAgent, conversationId, filePathName);
			ACLMessage reply = FIPAService.doFipaRequestClient(myAgent, request);
			if (reply != null) {
				return FileManagerClient.this.extractDownloadInfo(reply);
			} else {
				throw new Exception(TIMEOUT);
			}
		}

		@Override
		public DownloadInfo downloadMultiple(String conversationId,
				List<String> filesPathName) throws Exception {
			// TODO Auto-generated method stub
			return null;
		}
	} // END of inner class AgentBasedConnector
	
	
	/**
	 * Inner class AgentBasedConnector
	 */
	private class GatewayBasedConnector implements Connector {
		private DynamicJadeGateway myGateway;
		
		GatewayBasedConnector(DynamicJadeGateway gateway) {
			myGateway = gateway;
		}

		public List<FileInfo> listFiles(final String dirPathName) throws Exception {
			ListFilesBehaviour lfb = new ListFilesBehaviour(dirPathName);
			myGateway.execute(lfb);
			
			return lfb.getListFiles();
		}

		public void sendCancelDownload(final String conversationId) throws Exception {
			myGateway.execute(new OneShotBehaviour() {
				
				@Override
				public void action() {
					ACLMessage cancel = FileManagerClient.this.prepareCancelDownloadRequest(myAgent, conversationId);
					myAgent.send(cancel);
				}
			});
		}

		public DownloadInfo download(final String conversationId, final String filePathName) throws Exception {
			DownloadBehaviour db = new DownloadBehaviour(conversationId, filePathName);
			myGateway.execute(db);
			return new DownloadInfo(db.getContent(), db.isCompleted());
		}
		
		public DownloadInfo downloadMultiple(final String conversationId, final List<String> filesPathName) throws Exception {
			DownloadBehaviour db = new DownloadBehaviour(conversationId, filesPathName);
			myGateway.execute(db);
			return new DownloadInfo(db.getContent(), db.isCompleted());
		}
	} // END of inner class GatewayBasedConnector
	
	
	/**
	 * Inner class ListFilesBehaviour.
	 */
	private class ListFilesBehaviour extends SimpleAchieveREInitiator {

		private static final long serialVersionUID = -22871497292972402L;
		
		private String dirPathName;
		private List<FileInfo> listFiles;
		private Exception exception;
		
		public ListFilesBehaviour(String dirPathName) {
			super(null, null);
			
			this.dirPathName = dirPathName;
		}

		@Override
		public void onStart() {
			super.onStart();
			
			myAgent.getContentManager().registerLanguage(new SLCodec());
			myAgent.getContentManager().registerOntology(FileManagementOntology.getInstance());
		}
		
		@Override
		protected ACLMessage prepareRequest(ACLMessage unused) {
			try {
				return FileManagerClient.this.prepareGetFilesListRequest(myAgent, dirPathName);
			} catch (Exception e) {
				exception = e;
			}
			return null;
		}
		
		@Override
		protected void handleInform(ACLMessage msg) {
			super.handleInform(msg);
			
			try {
				listFiles = FileManagerClient.this.extractFilesList(myAgent, msg);
			} catch (Exception e) {
				exception = e;
			}
		}
		
		@Override
		protected void handleFailure(ACLMessage msg) {
			super.handleFailure(msg);
			
			exception = new Exception(msg.getContent());
		}
		
		@Override
		protected void handleAllResultNotifications(Vector msgs) {
			super.handleAllResultNotifications(msgs);
			
			if (msgs.size() == 0) {
				exception = new Exception(TIMEOUT);
			}
		}
		
		public List<FileInfo> getListFiles() throws Exception {
			if (exception != null) {
				throw exception;
			}
			return listFiles;
		}
	} // END of inner class ListFilesBehaviour
	
	
	/**
	 * Inner class DownloadBehaviour.
	 */	
	private class DownloadBehaviour extends SimpleAchieveREInitiator {

		private static final long serialVersionUID = 298687839670962197L;
		
		private String conversationId;
		private String filePathName;
		private List<String> filesPathName;
		private DownloadInfo downloadInfo;
		private Exception exception;
		
		public DownloadBehaviour(String conversationId, String filePathName) {
			super(null, null);

			this.conversationId = conversationId;
			this.filePathName = filePathName;
		}
		
		public DownloadBehaviour(String conversationId, List<String> filesPathName) {
			super(null, null);

			this.conversationId = conversationId;
			this.filesPathName = filesPathName;
		}
		

		@Override
		public void onStart() {
			super.onStart();
			
			myAgent.getContentManager().registerLanguage(new LEAPCodec());
			myAgent.getContentManager().registerOntology(FileManagementOntology.getInstance());
		}
		
		@Override
		protected ACLMessage prepareRequest(ACLMessage unused) {
			try {
				if (filesPathName!=null) {
					return FileManagerClient.this.prepareDownloadMultipleRequest(myAgent, conversationId, filesPathName);
				}else{
					return FileManagerClient.this.prepareDownloadRequest(myAgent, conversationId, filePathName);
				}	
				
			} catch (Exception e) {
				exception = e;
			}
			return null;
		}
		
		@Override
		protected void handleInform(ACLMessage msg) {
			super.handleInform(msg);
			
			downloadInfo = FileManagerClient.this.extractDownloadInfo(msg);
		}
		
		@Override
		protected void handleFailure(ACLMessage msg) {
			super.handleFailure(msg);
			
			exception = new Exception(msg.getContent());
		}
		
		@Override
		protected void handleAllResultNotifications(Vector msgs) {
			super.handleAllResultNotifications(msgs);
			
			if (msgs.size() == 0) {
				exception = new Exception(TIMEOUT);
			}
		}
		
		public byte[] getContent() throws Exception {
			if (exception != null) {
				throw exception;
			}
			return downloadInfo.content;
		}
		
		public boolean isCompleted() {
			return downloadInfo.completed;
		}
	} // END of inner class ListFilesBehaviour

	
	/**
	 * Inner class FileManagerInputStream.
	 */
	private class FileManagerInputStream extends InputStream {
		
		private ByteBuffer buffer;
		private String conversationId;
		private List<String> filesPathName;
		private String filePathName;
		private boolean finished;
		private boolean closed;
		
		public FileManagerInputStream(String filePathName) {
			this.filePathName = filePathName;
			this.conversationId = createConversationId(filePathName);
			this.finished = false;
			this.closed = false;
		}
		
		public FileManagerInputStream(List<String> filesPathName) {
			this.filesPathName = filesPathName;
			this.conversationId = createConversationId(filesPathName.get(0));
			this.finished = false;
			this.closed = false;
		}
		
		@Override
		public int read() throws IOException {
			if (closed) {
				throw new IOException("FileManagerInputStream closed");
			}
			int i;
			try {
				if (buffer == null) {
					fillBuffer();
				}
				if (buffer == null) {
					i = -1;
				} else {
					i = (int)buffer.get() & 0xff;
				}
			} catch(BufferUnderflowException bue) {
				if (finished) {
					i = -1;
				} else {
					fillBuffer();
					i = (int)buffer.get() & 0xff;
				}
			}
			
			return i;
		}
		
		@Override
		public int available() throws IOException {
			if (closed) {
				throw new IOException("FileManagerInputStream closed");
			}
			if (buffer != null) {
				return buffer.remaining();
			} else {
				return super.available();
			}
		}
		
		@Override
		public long skip(long n) throws IOException {
			throw new IOException("Operation not supported");
		}
		
		@Override
		public void close() throws IOException {
			if (finished) {
				super.close();
			} else {
				try {
					myConnector.sendCancelDownload(conversationId);
				} catch(Exception e) {
					logger.log(Level.SEVERE, "Error sending download user CANCEL to file "+filePathName, e);
				}
			}
			closed = true;
		}

		private void fillBuffer() throws IOException {
			try {
				DownloadInfo di;
				if (filesPathName!=null) {
					di = myConnector.downloadMultiple(conversationId, filesPathName);
				}else{
					di = myConnector.download(conversationId, filePathName);

				}
				if (di.content != null) {
					buffer = ByteBuffer.wrap(di.content);
				}
				finished = di.completed;
				
			} catch(Exception e) {
				logger.log(Level.SEVERE, "Error getting file data from "+filePathName, e);
				throw new IOException(e);
			}
		}
	} // END of inner class FileManagerInputStream
	
	
	/**
	 * Inner class DownloadInfo.
	 */
	private class DownloadInfo {
		public byte[] content;
		public boolean completed;
		
		public DownloadInfo(byte[] content, boolean completed) {
			this.content = content;
			this.completed = completed;
		}
	} // END of inner class DownloadInfo
}
