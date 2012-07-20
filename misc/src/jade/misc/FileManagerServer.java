package jade.misc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import jade.content.AgentAction;
import jade.content.ContentElement;
import jade.content.lang.leap.LEAPCodec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.SSIteratedAchieveREResponder;
import jade.proto.SSResponderDispatcher;
import jade.util.Logger;
import jade.util.leap.Serializable;

public class FileManagerServer implements Serializable {

	private static final long serialVersionUID = 784792949576972917L;

	private static final int DEFAULT_BLOCK_SIZE = 1024 * 100;
	private static Logger logger = Logger.getJADELogger(FileManagerServer.class.getName());
	
	private int downloadBlockSize = 0;
	
	public void init(Agent myAgent, final String root) {
		
		myAgent.getContentManager().registerLanguage(new SLCodec());
		myAgent.getContentManager().registerLanguage(new LEAPCodec());
		myAgent.getContentManager().registerOntology(FileManagementOntology.getInstance());
		
		// Add behaviour to serve the GetFilesList action
		myAgent.addBehaviour(new CyclicBehaviour(myAgent) {

			private MessageTemplate template = MessageTemplate.and(
					MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST),
					MessageTemplate.MatchOntology(FileManagementOntology.getInstance().getName()));

			@Override
			public void action() {
				ACLMessage msg = myAgent.receive(template);
				if (msg != null) {
					ACLMessage reply;
					
					try {
						Action actExpr = (Action)myAgent.getContentManager().extractContent(msg);
						GetFilesListAction gfla = (GetFilesListAction)actExpr.getAction();
	
						String dirPathName = gfla.getDirPathName();
						if (dirPathName == null) {
							dirPathName = ".";
						}
						
						reply = msg.createReply();
						reply.setPerformative(ACLMessage.INFORM);

						List<FileInfo> fileInfos = new ArrayList<FileInfo>();
						
						String path;
						if (root == null) {
							path = dirPathName;
						} else {
							path = root+File.separator+dirPathName;
						}

						File pathFile = new File(path);
						String[] fileNames = pathFile.list();
						if (fileNames != null) {
							for (String fileName : fileNames) {
								File file = new File(path+File.separator+fileName);
								fileInfos.add(new FileInfo(fileName, dirPathName, file.isDirectory(), new Date(file.lastModified()), file.length()));
							}
						} else {
							throw new Exception(path+" not present");
						}
						
						logger.log(Level.FINE, "Served GetFilesList action "+fileInfos);
						
						sendResponse(myAgent, msg, ACLMessage.INFORM, gfla, fileInfos);
						
					} catch(Exception e) {
						logger.log(Level.SEVERE, "Error serving GetFilesList action", e);
						sendResponse(myAgent, msg, ACLMessage.FAILURE, null, e.getMessage());
					}
				} else {
					block();
				}
			}
		});
		
		// Add behaviour to serve the Download action
		myAgent.addBehaviour(new SSResponderDispatcher(myAgent, 
				MessageTemplate.and(
						MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.ITERATED_FIPA_REQUEST), 
						MessageTemplate.MatchOntology(FileManagementOntology.getInstance().getName()))) {

			@Override
			protected Behaviour createResponder(final ACLMessage initiationMsg) {
				return new SSIteratedAchieveREResponder(myAgent, initiationMsg) {

					private Exception exception;
					private byte[] buffer;
					private BufferedInputStream bufferedInput;
					private long fileSize;
					private long totalBytesRead;
					private String filePathName;
					
					@Override
					public void onStart() {
						super.onStart();
						
						try {
							Action actExpr = (Action)myAgent.getContentManager().extractContent(initiationMsg);
							DownloadFileAction dfa = (DownloadFileAction)actExpr.getAction();
		
							filePathName = dfa.getFilePathName();
							if (root != null) {
								filePathName = root+File.separator+filePathName;
							}
							
							File file = new File(filePathName);
							bufferedInput = new BufferedInputStream(new FileInputStream(file));
							buffer = new byte[getDownloadBlockSize()];
							
							fileSize = file.length();
							totalBytesRead = 0;
						} catch(Exception e) {
							logger.log(Level.SEVERE, "Serving DownloadFile action: "+(filePathName==null?"Error decoding ACLMessage":"Error opening file "+filePathName), e);
							exception = e;
						}
					}
					
					@Override
					protected ACLMessage handleRequest(ACLMessage request) throws RefuseException, FailureException, NotUnderstoodException {
						if (exception != null) {
							// Failed OnStart method
							throw new FailureException(prepareFailureMessage(request, exception.getMessage()));
						}
						
						int bytesRead = 0;
						try {
							bytesRead = bufferedInput.read(buffer);
						} catch (IOException e) {
							logger.log(Level.SEVERE, "Error reading file "+filePathName, e);
							throw new FailureException(prepareFailureMessage(request, exception.getMessage()));
						}
						totalBytesRead += bytesRead;
						
						ACLMessage reply = request.createReply();
						reply.setPerformative(ACLMessage.INFORM);
						if (bytesRead < getDownloadBlockSize()) {
							byte[] partialBuffer = new byte[bytesRead];
							System.arraycopy(buffer, 0, partialBuffer, 0, bytesRead);
							reply.setByteSequenceContent(partialBuffer);
						} else {
							reply.setByteSequenceContent(buffer);
						}
						
						if (totalBytesRead >= fileSize) {
							closeSessionOnNextReply();
						}

						return reply;
					}
					
					@Override
					protected void handleCancel(ACLMessage cancel) {
						logger.log(Level.WARNING, "Download of file "+filePathName+" cancelled by client");
					}
					
					@Override
					public int onEnd() {
			            try {
			            	if (bufferedInput != null) {
			            		bufferedInput.close();
			            	}
			            } catch (IOException e) {
			            	logger.log(Level.SEVERE, "Error closing file "+filePathName, e);
			            }
						
						return super.onEnd();
					}
				};
			}
		});
		
		logger.log(Level.FINE, "FileManagerServer initialized in agent "+myAgent.getName()+" with root "+root);
	}
		
	public void setDownloadBlockSize(int downloadBlockSize) {
		this.downloadBlockSize = downloadBlockSize;
	}
	
	private int getDownloadBlockSize() {
		if (downloadBlockSize == 0) {
			return DEFAULT_BLOCK_SIZE;
		}
		return downloadBlockSize;
	}
	
	private static final void sendResponse(Agent myAgent, ACLMessage request, int performative, AgentAction agentAction, Object result) {
		ACLMessage reply = request.createReply();
		reply.setPerformative(performative);

		if (performative == ACLMessage.INFORM) {
			if (result != null) {
				if (result instanceof byte[]) {
					reply.setByteSequenceContent((byte[])result);
				} else {
					if (agentAction != null) {
						
						if (result instanceof ArrayList) {
							// Convert java list into jade list
							result = new jade.util.leap.ArrayList((ArrayList)result);
						}
				
						Action action = new Action(myAgent.getAID(), agentAction);
						ContentElement ce = new Result(action, result);
						try {
							myAgent.getContentManager().fillContent(reply, ce);
						} catch (Exception e) {
							// Should never happen
							performative = ACLMessage.FAILURE;
							reply.setContent("Unexpected error: "+e.getMessage());
							logger.log(Level.SEVERE, "Error coding ACLMessage", e);
						}
					}
				}
			}
		} else {
			// FAILURE response
			if (result != null && result instanceof String) {
				reply.setContent((String)result);
			}
		}
		
		myAgent.send(reply);
	}	
	
	private ACLMessage prepareFailureMessage(ACLMessage request, String message) {
		ACLMessage failureReply = request.createReply();
		failureReply.setPerformative(ACLMessage.FAILURE);
		failureReply.setContent(message);
		return failureReply;
	}
}
