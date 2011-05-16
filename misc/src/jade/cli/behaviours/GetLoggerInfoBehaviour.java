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

package jade.cli.behaviours;

import java.util.logging.Level;

import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.content.onto.basic.Result;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAService;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.tools.logging.JavaLoggingLogManagerImpl;
import jade.tools.logging.LogManager;
import jade.tools.logging.ontology.GetAllLoggers;
import jade.tools.logging.ontology.LogManagementOntology;
import jade.tools.logging.ontology.LoggerInfo;
import jade.util.leap.ArrayList;
import jade.util.leap.Iterator;
import jade.util.leap.List;

/**
 * Get all loggers info
 * 
 * Search for a remote loggerController,
 * then send a jade.tools.logging.ontology.GetAllLoggers action
 * and then send the list of logLevel, logger couple to the standard output
 * 
 * @author Salvatore Soldatini - TILAB
 *
 */

public class GetLoggerInfoBehaviour extends OneShotBehaviour {

	private static final long serialVersionUID = 8667957551752836265L;
	private LogManager myLogManager;
	AID helperController;
	
	public GetLoggerInfoBehaviour(){
		// Initialize the default LogManager
		myLogManager = new JavaLoggingLogManagerImpl();
		
		helperController = null;
	}
	
	@Override
	public void action() {

		try {
			AMSAgentDescription template = new AMSAgentDescription();
			template.setName(new AID("loggerController", AID.ISLOCALNAME));

			SearchConstraints sc = new SearchConstraints();
			sc.setMaxResults(new Long(1));
			AMSAgentDescription[] result = AMSService.search(myAgent, template, sc);
			if(result.length==0){
				throw new Exception("Remote logger controller not found");	
			}
			
			helperController = ((AMSAgentDescription)result[0]).getName();

			List logList = retrieveLogInfo();
			
			StringBuffer sb = new StringBuffer();
			sb.append("LEVEL    LOGGER-NAME\n");
			sb.append("--------------------\n");
			Iterator iterator = logList.iterator();
			while(iterator.hasNext()){
				LoggerInfo logInfo = (LoggerInfo)iterator.next();
				Level level = Level.parse(String.valueOf(logInfo.getLevel()));
				sb.append(String.format("%-8s %s\n", level.getName(), logInfo.getName()));
			}
			System.out.println(sb.toString());
		}
		catch (FIPAException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			// Should never happen
			e.printStackTrace();
		}
	}

	private List retrieveLogInfo() throws FIPAException {
		List tmp = null;
		if (helperController != null) {
			tmp = remoteRetrieveLogInfo(helperController);
		}
		else {
			tmp = myLogManager.getAllLogInfo();
		}
		// Now sort log info in alphabetical order
		List infos = new ArrayList(tmp.size());
		Iterator it = tmp.iterator();
		while (it.hasNext()) {
			LoggerInfo li = (LoggerInfo) it.next();
			String name = li.toString();
			int i = 0;
			while (i < infos.size() && name.compareTo(infos.get(i).toString()) >= 0) {
				++i;
			}
			infos.add(i, li);
		}
		return infos;
	}

	private List remoteRetrieveLogInfo(AID helper) throws FIPAException {
		ACLMessage request = createHelperRequest(helper);
		
		GetAllLoggers gal = new GetAllLoggers(myLogManager.getClass().getName(), null);
		
		Action act = new Action();
		act.setActor(helper);
		act.setAction(gal);
		
		try {
			myAgent.getContentManager().registerLanguage(new SLCodec());
			myAgent.getContentManager().registerOntology(LogManagementOntology.getInstance());

			myAgent.getContentManager().fillContent(request, act);
			ACLMessage inform = FIPAService.doFipaRequestClient(myAgent, request, 10000);
			if (inform != null) {
				Result res = (Result) myAgent.getContentManager().extractContent(inform);
				return res.getItems();
			}
			else {
				throw new FIPAException("Response timeout expired");
			}
		}
		catch (FIPAException fe) {
			throw fe;
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new FIPAException(e.getMessage());
		}
	}		
	
	private ACLMessage createHelperRequest(AID helper) {
		ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
		request.addReceiver(helper);
		request.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
		request.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
		request.setOntology(LogManagementOntology.getInstance().getName());
		return request;
	}
}
