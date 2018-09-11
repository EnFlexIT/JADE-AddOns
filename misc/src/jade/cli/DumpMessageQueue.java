package jade.cli;

import java.util.Properties;

import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.ContainerMonitorAgent;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPAService;
import jade.domain.JADEAgentManagement.WhereIsAgentAction;
import jade.lang.acl.ACLMessage;
import jade.proto.SimpleAchieveREInitiator;

public class DumpMessageQueue extends CLICommand {
	
	@Option(value="<container name>", description="The container where the target agent lives (optional: if missing it will be automatically retrieved)")
	public static final String CONTAINER_OPTION = "container";
	@Option(value="<agent local name>", description="The agent that must be dumped")
	public static final String AGENT_OPTION = "agent";
	@Option(value="<limit>", description="The maximum number of messages to dump (optional: default = all messages)")
	public static final String LIMIT_OPTION = "limit";

	private String agentName = null;
	private String containerName = null;
	private int limit = -1;
	
	public static void main(String[] args) {
		CLIManager.execute(new DumpMessageQueue(), args);
	}

	@Override
	public Behaviour getBehaviour(Properties pp) throws IllegalArgumentException {
		agentName = CLIManager.getMandatoryOption(AGENT_OPTION, pp);
		containerName = pp.getProperty(CONTAINER_OPTION);
		String limitStr = pp.getProperty(LIMIT_OPTION);
		try {
			limit = Integer.parseInt(limitStr);
		}
		catch (Exception e) {
			// Ignore and keep default
		}
		
		return new SimpleAchieveREInitiator(null, null) {
			@Override
			protected ACLMessage prepareRequest(ACLMessage unused) {
				ACLMessage req = null;
				try {
					if (containerName == null) {
						containerName = retrieveContainerName(agentName, myAgent); 
					}
					req = new ACLMessage(ACLMessage.REQUEST);
					req.setSender(myAgent.getAID());
					req.addReceiver(new AID("monitor-"+containerName, AID.ISLOCALNAME));
					req.setOntology(ContainerMonitorAgent.CONTAINER_MONITOR_ONTOLOGY);
					String content = ContainerMonitorAgent.DUMP_MESSAGEQUEUE_ACTION+" "+agentName;
					if (limit > 0) {
						content = content + " " + String.valueOf(limit);
					}
					req.setContent(content);
				}
				catch (Exception e) {
					out.println(e.getMessage());
				}
				return req;
			}

			@Override
			protected void handleInform(ACLMessage inform) {
				out.println(inform.getContent());
			}

			@Override
			protected void handleFailure(ACLMessage failure) {
				out.println("ERROR: "+failure.getContent());
			}
		};
	}
	
	private String retrieveContainerName(String agentName, Agent a) throws Exception {
		WhereIsAgentAction wia = new WhereIsAgentAction();
		wia.setAgentIdentifier(new AID(agentName, AID.ISLOCALNAME));
		ACLMessage request = CLIManager.createAMSRequest(a, wia);
		ACLMessage response = FIPAService.doFipaRequestClient(a, request, 10000);
		if (response != null) {
			Result result = (Result) a.getContentManager().extractContent(response);
			ContainerID cid = (ContainerID) result.getValue();
			return cid.getName();
		}
		else {
			throw new Exception("Timeout expired while retrieving container for agent "+agentName+" from the AMS");
		}
	}
}
