package jade.misc;

import java.util.Date;
import java.util.Vector;

import jade.content.ContentElement;
import jade.content.ContentElementList;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.ContainerID;
import jade.domain.AMSService;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.InternalError;
import jade.domain.JADEAgentManagement.CreateAgent;
import jade.domain.JADEAgentManagement.JADEManagementOntology;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import jade.util.Logger;

public class CreateFileManagerAgentBehaviour extends AchieveREInitiator {

	private static Logger logger = Logger.getJADELogger(CreateFileManagerAgentBehaviour.class.getName());
	private static final String DEFAULT_ROOT = ".";
	private static final long TIMEOUT = 10000;
	public static final String TIMEOUT_ERROR = "TIMEOUT";
	public static enum Status {UNKNOWN, SUCCESS, FAILURE};
	
	private String agentName;
	private String root;
	private Integer downloadBlockSize;
	private String errorMessage;
	private Status status;
	
	public CreateFileManagerAgentBehaviour(String agentName, String root) {
		this(agentName, root, null);
	}
	
	public CreateFileManagerAgentBehaviour(String agentName, String root, Integer downloadBlockSize) {
		super(null, null);
		
		this.agentName = agentName;
		this.root = root;
		this.downloadBlockSize = downloadBlockSize;
		this.status = Status.UNKNOWN;
	}

	public Status getStatus() {
		return status;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public void onStart() {
	   super.onStart();
	   
	   myAgent.getContentManager().registerOntology(JADEManagementOntology.getInstance());
       if (myAgent.getContentManager().lookupLanguage(FIPANames.ContentLanguage.FIPA_SL) == null) {
      	 myAgent.getContentManager().registerLanguage(new SLCodec());
       }
	}

	@Override
	protected Vector prepareRequests(ACLMessage request) {
		Vector v = new Vector(1);
		ACLMessage msg = prepareRequest();
		if (msg != null) {
			v.addElement(msg);
		}
		return v;
	}

	private ACLMessage prepareRequest() {
		try {
			CreateAgent ca = new CreateAgent();
			ca.setAgentName(agentName);
			ca.setClassName(FileManagerAgent.class.getName());
			ca.setContainer(new ContainerID(myAgent.getContainerController().getContainerName(), null));
			if (root == null) {
				root = DEFAULT_ROOT;
			}
			ca.addArguments(root);
			if (downloadBlockSize != null) {
				ca.addArguments(downloadBlockSize);
			}
	
			Action a = new Action();
			a.setActor(myAgent.getAMS());
			a.setAction(ca);
			
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setSender(myAgent.getAID());
			msg.addReceiver(myAgent.getAMS());
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
			msg.setReplyByDate(new Date(System.currentTimeMillis() + TIMEOUT));
			msg.setOntology(JADEManagementOntology.NAME);
			
			myAgent.getContentManager().fillContent(msg, a);
			return msg;
		} catch (Exception e) {
			logger.log(Logger.SEVERE, "Error creating FileManagerAgent ", e);
			status = Status.FAILURE;
			errorMessage = e.getMessage();
		} 
		return null;
	}
	
	@Override
	protected void handleInform(ACLMessage inform) {
		status = Status.SUCCESS;
	}
	
	@Override
	protected void handleFailure(ACLMessage failure) {
		handleError(failure);
	}
	
	@Override
	protected void handleRefuse(ACLMessage refuse) {
		handleError(refuse);
	}
	
	@Override
	protected void handleNotUnderstood(ACLMessage notUnderstood) {
		handleError(notUnderstood);
	}
	
	@Override
	protected void handleAllResultNotifications(Vector notifications) {
		if (notifications.size() == 0) {
			errorMessage = TIMEOUT_ERROR;
		}
	}
	
	protected void handleError(ACLMessage msg){
		status = Status.FAILURE;
		if (msg != null) {
			ContentElement content;
			try {
				content = myAgent.getContentManager().extractContent(msg);
				if (content instanceof ContentElementList) {
					ContentElement element = ((ContentElementList) content).get(1);
					if (element instanceof InternalError){
						InternalError ie = (InternalError)element;
						String  errorMsg = ie.getErrorMessage();
						int i = errorMsg.indexOf(java.lang.ClassNotFoundException.class.getSimpleName());
						if (i!=-1){
							//this should happen only when the AMS is required to create an agent
							errorMessage = errorMsg.substring(i);
						}
						else{
							errorMessage = errorMsg;
						}
					}
					else if (element instanceof FailureException){
						errorMessage = ((FailureException)element).getMessage();
					}
					else {
						errorMessage = element.toString();
					}
				}
			} catch (Exception e) {
				// Possibly we get a FAILURE from the platform due to message delivery problems
				if (msg.getPerformative() == ACLMessage.FAILURE) {
					try {
						AID target = AMSService.getFailedReceiver(myAgent, msg);
						String failureReason = AMSService.getFailureReason(myAgent, msg);
						errorMessage = "Cannot deliver message to agent "+target.getName()+"["+failureReason+"]";
						return;
					}
					catch (Exception e1) {
						// This is not a FAILURE delivery message
					}
				}
				logger.log(Logger.WARNING, "Error retrieving the error message", e);
				errorMessage = msg.getContent();
			} 			
		}
	}
}
