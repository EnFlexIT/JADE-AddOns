/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
package jade.misc;

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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class FileManagerServer implements Serializable {

	private static final long serialVersionUID = 784792949576972917L;

	private static final int DEFAULT_BLOCK_SIZE = 1024 * 100;
	private static Logger logger = Logger.getJADELogger(FileManagerServer.class.getName());
	
	private int downloadBlockSize = 0;
	private String zipTmpDir = System.getProperty("java.io.tmpdir");
	
	public FileManagerServer() {		
	}
		
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
						logger.log(Level.SEVERE, "Error serving GetFilesList action, message= "+msg, e);
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
					private File file;
					private boolean deleteOnExit = false;
										
					@Override
					public void onStart() {
						super.onStart();

						try {
							Action actExpr = (Action)myAgent.getContentManager().extractContent(initiationMsg);
							 
							//when a single file is going to be downloaded
							if (actExpr.getAction() instanceof DownloadFileAction) {								
								DownloadFileAction dfa = (DownloadFileAction)actExpr.getAction();								
								filePathName = dfa.getFilePathName();
								if (root != null) {
									filePathName = root+File.separator+filePathName;
								}									
							}
							//when multiples files are going to be downloaded
							else {
								DownloadMultipleFilesAction dmfa = (DownloadMultipleFilesAction)actExpr.getAction();
								List<String> filesPathName = dmfa.getFilesPathName();
								if (root != null) {
									for (int i=0; i<filesPathName.size(); i++) {
										filesPathName.set(i, root+File.separator+filesPathName.get(i));
									}	
								}	

								filePathName = "Log-" + System.currentTimeMillis()+".zip";
								if (zipTmpDir != null) {
									filePathName =  zipTmpDir+File.separator+filePathName;
								}
								
								zip(filesPathName, filePathName);
								deleteOnExit = true;
							}

							file = new File(filePathName);
							bufferedInput = new BufferedInputStream(new FileInputStream(file));
							buffer = new byte[getDownloadBlockSize()];								
							fileSize = file.length();
							totalBytesRead = 0;	

						} catch(Exception e) {
							logger.log(Level.SEVERE, "Error downloading file "+filePathName, e);
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
						if (bytesRead > 0) {
							if (bytesRead < getDownloadBlockSize()) {
								byte[] partialBuffer = new byte[bytesRead];
								System.arraycopy(buffer, 0, partialBuffer, 0, bytesRead);
								reply.setByteSequenceContent(partialBuffer);
							} else {
								reply.setByteSequenceContent(buffer);
							}
						}
						
						if (bytesRead == -1 || totalBytesRead >= fileSize) {
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

						finally{
							if (deleteOnExit && file!=null) {
								file.delete();
							}
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
	
	public void setZipTmpDir(String zipTmpDir) {
		this.zipTmpDir = zipTmpDir;
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
	
	private void zip(List<String> sourceFileNames, String zipFileName) {
		ZipOutputStream zos = null;
		try
		{
			byte[] buffer = new byte[1024];			
			FileOutputStream fos = new FileOutputStream(zipFileName);
			zos = new ZipOutputStream(fos);			

			for (String fileName : sourceFileNames) {
				File f = new File(fileName);
				if (f.exists()) {
					FileInputStream fis = null;
					try {
						fis = new FileInputStream(f);
						zos.putNextEntry(new ZipEntry(f.getName()));
						int length;
						while((length = fis.read(buffer)) > 0)
						{
							zos.write(buffer, 0, length);
						}
						zos.closeEntry();
					}
					finally {
						if (fis != null) {
							fis.close();
						}
					}
				}
			}			
		}
		catch(Exception e)
		{
			logger.log(Level.WARNING, "Error zipping files", e);
		}
		finally {
			try {
				if (zos != null) {
					zos.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
