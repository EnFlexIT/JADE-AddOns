/*
 * Copyright (C) 2005 Chair of Computer Science 4
 * Aachen University of Technology
 *
 * Copyright (C) 2005 Dpt. of Communcation and Distributed Systems
 * University of Hamburg
 *
 * This file is part of the ASCML.
 *
 * The ASCML is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * The ASCML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ASCML; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


package jade.tools.ascml.launcher;

import jade.content.onto.basic.Action;
import jade.core.*;
import jade.lang.acl.ACLMessage;
import jade.tools.ascml.absmodel.*;
import jade.domain.FIPANames;
import jade.tools.ascml.launcher.abstracts.AbstractMARWaitThread;
import jade.tools.ascml.onto.Error;
import jade.tools.ascml.onto.*;
import jade.tools.ascml.exceptions.ModelActionException;
import jade.tools.ascml.launcher.behaviours.*;
import java.util.HashMap;
import java.util.Vector;

import sun.reflect.generics.scope.Scope;

public class DispatcherThread  extends AbstractMARWaitThread{

	private DependencyDispatcherThread dd;
	private HashMap<String,IRunnableAgentInstance> nameToTypeMap;
	private HashMap<String,Integer> typeCountMap;
	private HashMap<String,Vector<IRunnableAgentInstance>> typeMap;
	private long start;


	DispatcherThread(IAbstractRunnable s, long timeout, AgentLauncher al, DependencyDispatcherThread dd) {
		super(s,al,timeout);
		this.dd = dd;
		t.start();
	}
	
	public void error(ModelActionException e) {
		super.error(e);
		dd.error(e);
	}

	public void run() {
		IRunnableRemoteSocietyInstanceReference reference=null;
		IRunnableSocietyInstance instance=null;		
		result = STATUS_RUNNING;
		start = System.currentTimeMillis();
		if (ar instanceof IRunnableSocietyInstance) {
			instance=(IRunnableSocietyInstance) ar;
		} else {
			reference=(IRunnableRemoteSocietyInstanceReference) ar;
		}
		if (instance != null) // Models to start are agents
		{
			IRunnableAgentInstance[] agents = instance.getRunnableAgentInstances();
			nameToTypeMap = new HashMap<String,IRunnableAgentInstance>();
			typeCountMap = new HashMap<String,Integer>();
			typeMap = new HashMap<String,Vector<IRunnableAgentInstance>>();
                        if (agents.length==0) {
                            result = STATUS_SUCCESSFULL;
                        }
			for (int j = 0; j < agents.length; j++) {
                                agents[j].setDetailedStatus("The agentinstance is starting.");
                                agents[j].setStatus(new Starting());

				nameToTypeMap.put(agents[j].getName(), agents[j]);
				Integer typeCount;
				if (typeCountMap.containsKey(agents[j].getClassName())) {
					typeCount = typeCountMap.get(agents[j].getClassName());
					typeCount = typeCount+1;
				} else {
					typeCount = 1;
				}
				typeCountMap.put(agents[j].getClassName(), typeCount);

				Vector<IRunnableAgentInstance> typeVec;
				if (typeMap.containsKey(agents[j].getClassName())) {
					typeVec = typeMap.get(agents[j].getClassName());
				} else {
					typeVec = new Vector<IRunnableAgentInstance>();
				}
				typeVec.add(agents[j]);
				typeMap.put(agents[j].getClassName(), typeVec);

			}

			//FIXME: ToolOptions for societies
			HashMap<String, Vector<String>> socToolOptions = null;
			if (socToolOptions==null) {
				socToolOptions = new HashMap<String, Vector<String>>();
			}
			for (int j = 0; j < agents.length; j++) {
				// System.out.println("DispatcherThread is going to start: " + agents[j]);
				startThisAgent(agents[j],socToolOptions);
				result = STATUS_SUCCESSFULL;
			}
		} else // Models to start are societies
		{
			startThisSociety(reference.getFullyQualifiedName(),reference.getLauncherName(),reference.getLauncherAddresses());
		}
		while ((System.currentTimeMillis() - start < timeout) && (result == STATUS_RUNNING)) {
			try {				
				Thread.sleep(Math.max(1,timeout-(System.currentTimeMillis()-start)-10));				
			} catch (InterruptedException e) {
			}
		}

		if (result == STATUS_RUNNING) {
			result = STATUS_TIMEDOUT;
		}
		
		switch (result) {
			case STATUS_TIMEDOUT:
				if (instance != null)
				{
					error(new ModelActionException("Timeout while dispatching dependencies for instance '"+instance.getName()+"'.", "For some reason the dependencies couldn't be dispatched, (@Sven: Kannst Du hier nochmal m�gliche Gr�nde fuer den TimeOut eintragen ?)"));
					instance.setDetailedStatus("The occured a timeout while dispatching the dependencies. For some reason the dependencies couldn't be dispatched, (@Sven: Kannst Du hier nochmal m�gliche Gr�nde fuer den TimeOut eintragen ?)");
					instance.setStatus(new jade.tools.ascml.onto.Error());
				}
				else
				{
					error(new ModelActionException("Timeout while dispatching dependencies for reference '"+reference.getName()+"'.", "For some reason the dependencies couldn't be dispatched, (@Sven: Kannst Du hier nochmal m�gliche Gr�nde fuer den TimeOut eintragen ?)"));
					reference.setDetailedStatus("The occured a timeout while dispatching the dependencies. For some reason the dependencies couldn't be dispatched, (@Sven: Kannst Du hier nochmal m�gliche Gr�nde fuer den TimeOut eintragen ?)");
					reference.setStatus(new jade.tools.ascml.onto.Error());
				}
				break;
			case STATUS_ERROR:
				if (instance != null)
				{
					error(new ModelActionException("Error while dispatching dependencies for instance '"+instance.getName()+"'.", "For some reason the dependencies couldn't be dispatched."));
					instance.setDetailedStatus("Error while dispatching dependencies for this instance. For some reason the dependencies couldn't be dispatched.");
					instance.setStatus(new jade.tools.ascml.onto.Error());
				}
				else
				{
					error(new ModelActionException("Error while dispatching dependencies for reference '"+reference.getName()+"'.", "For some reason the dependencies couldn't be dispatched. Please make sure, that the launcher-address is correct and the remote launcher is running."));
					reference.setDetailedStatus("Error while dispatching dependencies for this instance. For some reason the dependencies couldn't be dispatched. Please make sure, that the launcher-address is correct and the remote launcher is running.");
					reference.setStatus(new jade.tools.ascml.onto.Error());
				}
				break;
		}

	}

	/**
	 * @param instance IRunnableAgentInstance to be started 
	 * @param socToolOptions The societies's tooloptions
	 */
	private boolean startThisAgent(IRunnableAgentInstance instance, HashMap<String, Vector<String>> socToolOptions) {
		if (al.isAgentStarted(instance.getName())) {
			return true;
		}
		IDependency[] deps = instance.getDependencies();
		for (int i = 0; i < deps.length; i++) {
			if (!deps[i].isActive()) {
				continue;
			}
			if (deps[i].getType() == IDependency.AGENTINSTANCE_DEPENDENCY) {
				String depName = ((IAgentInstanceDependency)deps[i]).getName();
				if (!al.isAgentStarted(depName)) {
					IRunnableAgentInstance newInstance = nameToTypeMap.get(depName);
					if (!startThisAgent(newInstance,socToolOptions))
						return false;
				}
			} else if (deps[i].getType() == IDependency.AGENTTYPE_DEPENDENCY) {
				String countStr = ((IAgentTypeDependency)deps[i]).getQuantity();
				int toStartCount;
				if (countStr == IAgentTypeDependency.ALL) {
					toStartCount = typeCountMap.get(((IAgentTypeDependency)deps[i]).getName());
				} else if (countStr == IAgentTypeDependency.ANY) {
					toStartCount = 1;
				} else {
					toStartCount = Integer.parseInt(countStr);
				}
				int startedCount = al.getTypeStartedCount(((IAgentTypeDependency)deps[i]).getName());
				if (startedCount < toStartCount) {
					String dependencyName = ((IAgentTypeDependency)deps[i]).getName();			
					Vector<IRunnableAgentInstance> typeVec = typeMap.get(dependencyName);
					if (typeVec == null)
						al.getRepository().throwExceptionEvent(new ModelActionException("The AgentType-dependency '"+dependencyName+"' could not be resolved.", "There has been an agenttype-dependency defined, but the agenttype on which the startup depends, could not be found. Please check the spelling within the dependency-tag (XML-file).", instance));

					for (int k = 0; k < typeVec.size() && startedCount < toStartCount; k++) {
						startThisAgent(typeVec.get(k),socToolOptions);
						startedCount = al.getTypeStartedCount(((IAgentTypeDependency)deps[i]).getName());
					}
				}
			} else if (deps[i].getType() == IDependency.SOCIETYINSTANCE_DEPENDENCY) {
				ISocietyInstanceDependency socDep = (ISocietyInstanceDependency)deps[i];				
				if (socDep.isActive()) {
					startThisSociety(socDep.getFullyQualifiedName(),socDep.getProvider().getName(),(String[]) socDep.getProvider().getAddresses().toArray());
					//Warten bis die Society gestartet wurde
					long start=System.currentTimeMillis();
					while ((System.currentTimeMillis() - start < timeout) && (result == STATUS_RUNNING)) {
						try {				
							Thread.sleep(Math.max(1,timeout-(System.currentTimeMillis()-start)-10));				
						} catch (InterruptedException e) {
						}
					}					
					if (result == STATUS_RUNNING) {
						result = STATUS_TIMEDOUT;
					}					
					if (result==STATUS_TIMEDOUT) {
						instance.setStatus(new jade.tools.ascml.onto.Error());
						instance.setDetailedStatus("Dependency "+socDep.getFullyQualifiedName()+" timed out.");		
						return false;
					}					
				}
			}
		}
		AgentLauncherThread alt = new AgentLauncherThread(instance, al, socToolOptions);
		try {
			alt.getResult();
			instance.setStatus(new Functional());
			instance.setDetailedStatus("Agent is running.");
			return true;			
		} catch (ModelActionException e) {
			error(e);
			instance.setStatus(new jade.tools.ascml.onto.Error());
			instance.setDetailedStatus(e.getMessage());
			return false;
		}
	}
	
	private void startThisSociety(String fqn, String launcherName, String[] adresses) {
		System.out.println("DispatcherThread: asking for remote society status.");
		// First ask the remote ASCML via GetStatus about the current status
		// we can recycle the launchername for the actual starting later
		AID receiver = new AID(launcherName, AID.ISGUID);
		
		// set up us the message		
		SocietyInstance newsoc = new SocietyInstance();
		newsoc.setName(fqn);
		Status srbResult = new Error();
		try {
			ACLMessage  getStatusMsg = al.createSubscription(receiver,newsoc);
			getStatusMsg.setPerformative(ACLMessage.QUERY_REF);
			getStatusMsg.setProtocol(FIPANames.InteractionProtocol.FIPA_QUERY);		
			for (int i=0; i < adresses.length; i++)
			{
				receiver.addAddresses(adresses[i]); // FIXME
			}
			getStatusMsg.addReceiver(receiver);
			
			// Create a MARGetStatusBehaviou
			// Now add the GetStatusBehaviour to the Agentlauncher
			MARGetStatusBehaviour srb = new MARGetStatusBehaviour(getStatusMsg, this, al);
			al.addBehaviour(srb);
			//al.QueryRemoteSociety(getStatusMsg, this, result);
			//Now we should wait until result changes
			while (srb.getResult() == null) {
				try {				
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
			// If it's not running,we start it
			srbResult = srb.getResult();
			System.out.println("Status: "+srbResult);
			
		} catch (Exception e) {
			e.printStackTrace();			
		}
		if(!(srbResult instanceof Functional)) {
			System.out.println("Remote society did not return STATUS_Functional. Will attempt to start a new instance.");
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
			msg.setOntology(ASCMLOntology.ONTOLOGY_NAME);
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_REQUEST);
			
			// for(int t =0;t<soc.getLauncherAddresses().length;t++)
			// System.out.println(soc.getLauncherAddresses()[t]);
			
			msg.addReceiver(receiver);
			Action contentAction = new jade.content.onto.basic.Action();
			Start action = new Start();
			action.setActor(receiver);		
			
			System.out.println("DispatcherThread: sending MSG to: " + receiver.getName() + " to start:");
			// System.out.print("     ");
			
			//FIXME: ToolOptions for Societies
			
			action.addModels(newsoc);
			
			contentAction.setAction(action);
			contentAction.setActor(al.getAID());
			
			try {
				al.getContentManager().setValidationMode(true);
				al.getContentManager().fillContent(msg, contentAction);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			System.err.println("DispatcherThread: sending Start-request");
			al.StartRemoteSociety(msg, this);		
		} else {			
			System.out.println("Remote society already running, request subscribe");		
		}		
		al.subscribeTo(receiver,newsoc);			
	}

	/**
	 * @return
	 */
	public String getReferenceName() {
		return ar.getName();
	}

}
