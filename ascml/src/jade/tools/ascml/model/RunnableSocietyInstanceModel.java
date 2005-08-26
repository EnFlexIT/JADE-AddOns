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


package jade.tools.ascml.model;

import java.util.*;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.onto.*;
/**
 *  Model-object containing all required information about a runnable SocietyInstance. 
 */
public class RunnableSocietyInstanceModel extends AbstractRunnable implements IRunnableSocietyInstance
{
	private Vector runnableAgentInstances			= new Vector();
	private Vector localRunnableSocietyInstanceRefs	= new Vector();
	private Vector remoteSocietyInstanceRefs		= new Vector();
	
	private int childrenAvailable		= 0;
//	private int childrenCreated			= 0;
	private int childrenNotRunning		= 0;
	private int childrenStarting		= 0;
	private int childrenRunning			= 0;
	private int childrenPartlyRunning		= 0;
	private int childrenStopping		= 0;
	private int childrenError			= 0;
	
	private HashMap<String, Vector> debugStatusMap = new HashMap();
	private boolean debug = true;
	
	
	/**
	 *  Instantiate a new model and initialize some variables 
	 */
	public RunnableSocietyInstanceModel(String name, ISocietyInstance parentModel, IAbstractRunnable parentRunnable)
	{
		super(name, parentModel, null, parentModel.getParentSocietyType().getModelChangedListener(), parentRunnable); // SocietyInstances themselves have no dependencies, therefore 'null'
	}
	
	public void setStatus(Status newStatus)
	{
		/* toDO: HACK !!!, Agenten sollten nicht zwangsläufig den
		 * gleichen Status erhalten wie die Society, sondern jeweils ihren eigenen
		 */
		/*try
		 {
		 throw new Exception(getName());
		 }
		 catch(Exception e)
		 {
		 e.printStackTrace();
		 }*/
		Status oldStatus = getStatus();
		
		/*IRunnableAgentInstance[] agentInstances = getRunnableAgentInstances();
		 for (int i=0; i < agentInstances.length; i++)
		 {
		 if (agentInstances[i].getStatus() != newStatus)
		 agentInstances[i].setStatus(newStatus);
		 }
		 
		 IRunnableSocietyInstance[] societyInstances = getLocalRunnableSocietyInstanceReferences();
		 for (int i=0; i < societyInstances.length; i++)
		 {
		 if (societyInstances[i].getStatus() != newStatus)
		 societyInstances[i].setStatus(newStatus);
		 }*/
		
		if ((oldStatus==null) || (!oldStatus.equals(newStatus)))
			super.setStatus(newStatus);
	}
	
	/**
	 * Runnable AgentInstances have to inform their parent-RunnableSocietyInstance whenever
	 * their status changed.
	 * @param runnableName The name of the runnable-model, whose status changed (only used for debugging)
	 * @param childNewStatus  New status of the agent
	 * @param childOldStatus  Old status of the agent
	 */
	public synchronized void informStatus(String runnableName, Status childNewStatus, Status childOldStatus)
	{
		// System.err.println("RunnableSocietyInstanceModel.informStatus: oldStatus=" + childOldStatus + " newStatus=" + childNewStatus);
		if (childNewStatus==null) 
			System.err.println("RunnableSocietyInstanceModel.informStatus("+this.getFullyQualifiedName() +"): oldStatus=" + childOldStatus + " newStatus=" + childNewStatus);
		if ((childNewStatus==null) || (childNewStatus.equals(childOldStatus)))
			return;
		
		if (childNewStatus instanceof Functional)
			childrenRunning++;
		else if (childNewStatus instanceof Known)
			childrenAvailable++;
		else if (childNewStatus instanceof Starting)
			childrenStarting++;
		else if (childNewStatus instanceof Stopping)
			childrenStopping++;
		else if (childNewStatus instanceof jade.tools.ascml.onto.Error)
			childrenError++;
		
		if (childOldStatus instanceof Functional)
			childrenRunning--;
		else if (childOldStatus instanceof Known)
			childrenAvailable++;
		else if (childOldStatus instanceof Starting)
			childrenStarting--;
		else if (childOldStatus instanceof Stopping)
			childrenStopping--;
		else if (childOldStatus instanceof jade.tools.ascml.onto.Error)
			childrenError--;
		
		// now check, if the status has changed and if so, update the status
		// and throw the appropiate event.
		Status oldStatus = getStatus();

		Status newSocStatus = null;
		if (childrenError > 0) {
			newSocStatus = new jade.tools.ascml.onto.Error();
		} else if (childrenStarting > 0) {
			newSocStatus = new Starting();	
		} else if (childrenStopping > 0) {
			newSocStatus = new Stopping();
		} else if ((childrenRunning > 0) && ((new Starting()).equals(oldStatus))) {
			newSocStatus = new Functional();	
		} else if ((childrenAvailable > 0) && (childrenRunning==0)) {
			newSocStatus = new Known();
		} else if (oldStatus==null) {
			newSocStatus = new Known();
		} else if ((childrenRunning > 0) && (childrenAvailable > 0)) {
			newSocStatus = new Stopping();
		}
		if (newSocStatus==null)
			newSocStatus=oldStatus;
			
		if (debug)
		{
			if (debugStatusMap.containsKey(childNewStatus.toString()))
			{
				Vector modelStatusVector = debugStatusMap.get(childNewStatus.toString());
				modelStatusVector.add(runnableName);
			}
			else
			{
				Vector modelStatusVector = new Vector();
				modelStatusVector.add(runnableName);
				debugStatusMap.put(childNewStatus.toString(), modelStatusVector);
			}
			if (childOldStatus != null)
			{
				Vector modelStatusVector = debugStatusMap.get(childOldStatus.toString());
				modelStatusVector.remove(runnableName);
			}
			// STATUS FIXME: This looks strange due to the Status model change. I don't know how to properly do this.
			System.err.println(getName() + " informStatus: childrenAvailable       = " + childrenAvailable + " " + debugStatusMap.get((new Known()).toString()));
			System.err.println(getName() + " informStatus: childrenStarting      = " + childrenStarting + " " + debugStatusMap.get((new Starting()).toString()));
			System.err.println(getName() + " informStatus: childrenStarted       = " + childrenRunning + " " + debugStatusMap.get((new Functional()).toString()));
			System.err.println(getName() + " informStatus: childrenStopping      = " + childrenStopping + " " + debugStatusMap.get((new Stopping()).toString()));
			System.err.println(getName() + " informStatus: childrenError         = " + childrenError + " " + debugStatusMap.get((new jade.tools.ascml.onto.Error()).toString()));
			
			if ((oldStatus==null) || (!oldStatus.equals(newSocStatus)))
				System.err.println(getName() + " informStatus: inform lead to new status: " + newSocStatus);
			else
				System.err.println(getName() + " informStatus: despite inform old status: " + newSocStatus);
			
		}
		
		if ((oldStatus==null) || (!oldStatus.equals(newSocStatus)))
			super.setStatus(newSocStatus);
	}
	
	/**
	 * Get the status of this RunnableSocietyInstance.
	 * The status results of the status of all contained RunnableAgentInstances
	 * plus all referenced RunnableSocietyInstances (and their agents and references).
	 * @return
	 */
	public Status getStatus()
	{
		return super.getStatus();
		
		
		/*// System.err.println("RunnableSocietyInstanceModel.getStatus: thisStatus = " + thisStatus);
		 boolean atLeastOneSubSocietyCreated         = false;
		 boolean atLeastOneSubSocietyNotRunning      = false;
		 boolean atLeastOneSubSocietyStarting        = false;
		 boolean atLeastOneSubSocietyRunning         = false;
		 boolean atLeastOneSubSocietyPartlyRunning   = false;
		 boolean atLeastOneSubSocietyStopping        = false;
		 boolean atLeastOneSubSocietyException       = false;
		 
		 IRunnableSocietyInstance[] societyReferences = getLocalRunnableSocietyInstanceReferences();
		 for (int i=0; i < societyReferences.length; i++)
		 {
		 String status = societyReferences[i].getStatus();
		 // System.err.println("RunnableSocietyInstanceModel.getStatus: subsociety=" + societyReferences[i] + " status"+status);
		  if (status == IAbstractRunnable.STATUS_RUNNING)
		  atLeastOneSubSocietyRunning = true;
		  else if (status == IAbstractRunnable.STATUS_NOT_RUNNING)
		  atLeastOneSubSocietyNotRunning = true;
		  else if (status == IAbstractRunnable.STATUS_CREATED)
		  atLeastOneSubSocietyCreated = true;
		  else if (status == IAbstractRunnable.STATUS_PARTLY_RUNNING)
		  atLeastOneSubSocietyPartlyRunning = true;
		  else if (status == IAbstractRunnable.STATUS_STARTING)
		  atLeastOneSubSocietyStarting = true;
		  else if (status == IAbstractRunnable.STATUS_STOPPING)
		  atLeastOneSubSocietyStopping = true;
		  else if (status == IAbstractRunnable.STATUS_ERROR)
		  atLeastOneSubSocietyException = true;
		  }
		  
		  IRunnableRemoteSocietyInstanceReference[] remoteSocietyReferences = getRemoteRunnableSocietyInstanceReferences();
		  for (int i=0; i < remoteSocietyReferences.length; i++)
		  {
		  String status = remoteSocietyReferences[i].getStatus();
		  // System.err.println("RunnableSocietyInstanceModel.getStatus: subsociety=" + societyReferences[i] + " status"+status);
		   if (status == IAbstractRunnable.STATUS_RUNNING)
		   atLeastOneSubSocietyRunning = true;
		   else if (status == IAbstractRunnable.STATUS_NOT_RUNNING)
		   atLeastOneSubSocietyNotRunning = true;
		   else if (status == IAbstractRunnable.STATUS_CREATED)
		   atLeastOneSubSocietyCreated = true;
		   else if (status == IAbstractRunnable.STATUS_PARTLY_RUNNING)
		   atLeastOneSubSocietyPartlyRunning = true;
		   else if (status == IAbstractRunnable.STATUS_STARTING)
		   atLeastOneSubSocietyStarting = true;
		   else if (status == IAbstractRunnable.STATUS_STOPPING)
		   atLeastOneSubSocietyStopping = true;
		   else if (status == IAbstractRunnable.STATUS_ERROR)
		   atLeastOneSubSocietyException = true;
		   }
		   
		   String subsocietyStatus = null;
		   
		   if (atLeastOneSubSocietyCreated)
		   subsocietyStatus = IAbstractRunnable.STATUS_CREATED;
		   
		   if (atLeastOneSubSocietyCreated && atLeastOneSubSocietyStarting)
		   subsocietyStatus = IAbstractRunnable.STATUS_STARTING;
		   
		   // test if all elements are running, and none is starting, stopping, partly_running, etc.
		    if (!atLeastOneSubSocietyCreated && !atLeastOneSubSocietyStarting &&
		    atLeastOneSubSocietyRunning && !atLeastOneSubSocietyPartlyRunning &&
		    !atLeastOneSubSocietyStopping && !atLeastOneSubSocietyException)
		    subsocietyStatus = IAbstractRunnable.STATUS_RUNNING;
		    
		    if (atLeastOneSubSocietyRunning &&
		    (atLeastOneSubSocietyCreated || atLeastOneSubSocietyStarting ||
		    atLeastOneSubSocietyPartlyRunning || atLeastOneSubSocietyStopping ||
		    atLeastOneSubSocietyNotRunning))
		    subsocietyStatus = IAbstractRunnable.STATUS_PARTLY_RUNNING;
		    
		    if (atLeastOneSubSocietyNotRunning &&
		    (!atLeastOneSubSocietyCreated || !atLeastOneSubSocietyStarting ||
		    !atLeastOneSubSocietyPartlyRunning || !atLeastOneSubSocietyStopping ||
		    !atLeastOneSubSocietyRunning))
		    subsocietyStatus = IAbstractRunnable.STATUS_NOT_RUNNING;
		    
		    if (atLeastOneSubSocietyException)
		    subsocietyStatus = IAbstractRunnable.STATUS_ERROR;
		    
		    // starting & stopping -status changes are omitted.
		     
		     System.err.println("RunnableSocietyInstanceModel.getStatus: thisStatus=" + thisStatus + " subsocietyStatus = " + subsocietyStatus);
		     if ((subsocietyStatus != null) && (subsocietyStatus != thisStatus))
		     {
		     if (thisStatus == IAbstractRunnable.STATUS_CREATED)
		     {
		     if (subsocietyStatus != STATUS_CREATED)
		     super.setStatus(subsocietyStatus);
		     }
		     else if (thisStatus == IAbstractRunnable.STATUS_STARTING)
		     {
		     if ((subsocietyStatus != STATUS_CREATED) && (subsocietyStatus != STATUS_STARTING))
		     super.setStatus(subsocietyStatus);
		     }
		     else if (thisStatus == IAbstractRunnable.STATUS_PARTLY_RUNNING)
		     {
		     if (subsocietyStatus == IAbstractRunnable.STATUS_ERROR)
		     super.setStatus(IAbstractRunnable.STATUS_ERROR);
		     else
		     super.setStatus(IAbstractRunnable.STATUS_PARTLY_RUNNING);
		     }
		     else if (thisStatus == IAbstractRunnable.STATUS_RUNNING)
		     {
		     if (subsocietyStatus == IAbstractRunnable.STATUS_ERROR)
		     super.setStatus(IAbstractRunnable.STATUS_ERROR);
		     else
		     super.setStatus(IAbstractRunnable.STATUS_PARTLY_RUNNING);
		     }
		     else // if (thisStatus == IAbstractRunnable.STATUS_PARTLY_RUNNING)
		     {
		     if (subsocietyStatus == IAbstractRunnable.STATUS_ERROR)
		     {
		     detailedStatus = "At least one referenced society-instance is errorneous. Please have a look at the status-details of contained references.";
		     super.setStatus(subsocietyStatus);
		     }
		     }
		     }
		     // System.err.println("RunnableSocietyInstanceModel.getStatus: model=" + this + " status=" + super.getStatus());
		      return super.getStatus();
		      */
	}
	
	/**
	 * Get the amount of local runnable agentinstances, that are defined by this model
	 * and all local runnable subsocieties.
	 * @return  runnable agentinstance-count..
	 */
	public int getLocalAgentInstanceCount()
	{
		int agentCount = getRunnableAgentInstances().length;
		IRunnableSocietyInstance[] references = getLocalRunnableSocietyInstanceReferences();
		
		for (int i=0; i < references.length; i++)
		{
			agentCount += references[i].getLocalAgentInstanceCount();
		}
		
		return agentCount;
	}
//	------------------------------------------------------
	
	public void addRunnableAgentInstance(IRunnableAgentInstance runnableInstance)
	{
		runnableAgentInstances.add(runnableInstance);
	}
	
	public IRunnableAgentInstance[] getRunnableAgentInstances()
	{
		if (runnableAgentInstances == null)
			return new IRunnableAgentInstance[0];
		IRunnableAgentInstance[] returnArray = new IRunnableAgentInstance[runnableAgentInstances.size()];
		runnableAgentInstances.toArray(returnArray);
		return returnArray;
	}
	
//	------------------------------------------------------
	
	public void addLocalRunnableSocietyInstanceReference(IRunnableSocietyInstance runnableInstance)
	{
		localRunnableSocietyInstanceRefs.add(runnableInstance);
	}
	
	public IRunnableSocietyInstance[] getLocalRunnableSocietyInstanceReferences()
	{
		if (localRunnableSocietyInstanceRefs == null)
			return new IRunnableSocietyInstance[0];
		
		IRunnableSocietyInstance[] returnArray = new RunnableSocietyInstanceModel[localRunnableSocietyInstanceRefs.size()];
		localRunnableSocietyInstanceRefs.toArray(returnArray);
		return returnArray;
	}
	
//	------------------------------------------------------
	
	public void addRemoteRunnableSocietyInstanceReference(IRunnableRemoteSocietyInstanceReference oneReference)
	{
		remoteSocietyInstanceRefs.add(oneReference);
	}
	
	public IRunnableRemoteSocietyInstanceReference[] getRemoteRunnableSocietyInstanceReferences()
	{
		if (remoteSocietyInstanceRefs == null)
			return new IRunnableRemoteSocietyInstanceReference[0];
		
		IRunnableRemoteSocietyInstanceReference[] returnArray = new IRunnableRemoteSocietyInstanceReference[remoteSocietyInstanceRefs.size()];
		remoteSocietyInstanceRefs.toArray(returnArray);
		return returnArray;
	}
	
	public String toFormattedString()
	{
		String str = "";
		str += "RunnableSocietyInstance: name = " + getName() + "\n";
		str += "runnableAgentInstances=" + runnableAgentInstances + "\n";
		str += "localRunnableSocietyInstanceRefs=" + localRunnableSocietyInstanceRefs + "\n";
		str += "remoteSocietyInstanceRefs=" + remoteSocietyInstanceRefs + "\n";
		return str;
	}
}
