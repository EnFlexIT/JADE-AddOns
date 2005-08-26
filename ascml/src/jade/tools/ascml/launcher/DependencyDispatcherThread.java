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

import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.events.ExceptionEvent;
import jade.tools.ascml.exceptions.ModelActionException;
import java.util.Vector;


public class DependencyDispatcherThread implements Runnable
{
	AgentLauncher al;;
	DispatcherThread dt;
	Vector <ModelActionException>errors; //ModelActionExceptions
		
	private Thread t;
	private ModelActionException mae;
	private IRunnableSocietyInstance instance;
	
	static int agentTimeout = 10000;
	static int socTimeout	= 50000;
	private boolean notifyGui;
	
	public DependencyDispatcherThread(AgentLauncher al, IRunnableSocietyInstance instance, boolean notifyGUI)
	{
		this.notifyGui = notifyGUI;
		this.al=al;
		this.instance=instance;				
		errors=new Vector<ModelActionException>();
		t=new Thread(this,"DependencyDispatcher Thread for " + instance.getName());
		t.start();
	}
		
	
	public void error(ModelActionException e)
	{
		errors.add(e);
	}

	private boolean privateResolveSocieties() {
		IRunnableRemoteSocietyInstanceReference[] remoteReferences = instance.getRemoteRunnableSocietyInstanceReferences();
		if (remoteReferences.length == 0) {
			return true;
		}
		Vector <DispatcherThread> dts = new Vector<DispatcherThread>();// DispatcherThreads
				
		// Start DispatcherThreads simultanously (almost)
		for (int i = 0; i < remoteReferences.length; i++) {
				dt = new DispatcherThread(
						remoteReferences[i],
						socTimeout, al, this);
			dts.add(dt);
		}
		
		// check all DispatcherThreads for errors
		DispatcherThread runningOne;
		do // Don't stop while threads at least one thread is running
		{
			runningOne = null;
			for (int i = 0; i < dts.size(); i++) {
				DispatcherThread tmpdt = (DispatcherThread) dts.get(i);			
				if (tmpdt.getResult() == DispatcherThread.STATUS_RUNNING) {
					runningOne=tmpdt;
				} else if (tmpdt.getResult() != DispatcherThread.STATUS_SUCCESSFULL) {
					System.err.println(tmpdt.getReferenceName());
				}		
			}	
			if (runningOne!=null) {
				try {
					runningOne.join();
				} catch (InterruptedException e) {
				}
			}
		} while (runningOne!=null);

		return true;
	}
	

	private boolean privateResolveAgents()
	{
		DispatcherThread dt = new DispatcherThread(instance,agentTimeout,al,this);

		while(dt.getResult()==DispatcherThread.STATUS_RUNNING) {
			try {
				dt.join();
			} catch (InterruptedException e) {
			}
		}		
		return dt.getResult()==DispatcherThread.STATUS_SUCCESSFULL;	

	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		boolean society_success=false;
		boolean agent_success=false;
		society_success = privateResolveSocieties();
		if (society_success) {
			agent_success = privateResolveAgents();
		}

		if(society_success && agent_success)
		{
            // instance.setStatus(IAbstractRunnable.STATUS_RUNNING);
			instance.setDetailedStatus("The society successfully started and is now running.");
		} else if (society_success) 
        {
		    // instance.setStatus(IAbstractRunnable.STATUS_ERROR);
            instance.setDetailedStatus("References were resolved but the agents did not start.");
        } else if (agent_success)
        {
            // instance.setStatus(IAbstractRunnable.STATUS_ERROR);
            instance.setDetailedStatus("The agents are running but the references were not resolved.");                
        } else
        {
            // instance.setStatus(IAbstractRunnable.STATUS_ERROR);
            instance.setDetailedStatus("Neither were the references resolved nor did the agents start.");                                
        }	
		if (notifyGui) {
			ModelActionException actionException = new ModelActionException("Error while resolving the dependencies for '"+instance.getName()+"'.", "For some reason, either the agentinstance- or the societyinstance-dependencies could not be resolved. Please take a look at the following exceptions in order to get to know more about the problem");
			for (int i=0;i<errors.size();i++) {
				actionException.addNestedException((ModelActionException)errors.get(i));
			}
			if (errors.size()>0) {
				ExceptionEvent ee = new ExceptionEvent(actionException);
				al.gui.exceptionThrown(ee);
			}
		}
	}


	/**
	 * @throws ModelActionException 
	 * 
	 */
	public void join() throws ModelActionException {
		try
		{
			t.join();
		} catch (InterruptedException e)
		{
		    e.printStackTrace();
		}
		if (!notifyGui)
		{
			String errorStr = "";
			for (int i=0;i<errors.size();i++)
			{
				ModelActionException tmpMae = (ModelActionException)errors.get(i);
				System.err.println("DependencyDispatcherThread.join: error = " + tmpMae);
				errorStr += tmpMae.getMessage() +"\n";
			}
			if (errors.size()>0)
			{
				System.err.println("DependencyDispatcherThread.join: throw exception = " + errorStr);
				ModelActionException mae = new ModelActionException(errorStr,instance);
				throw (mae);
			}			
		}
	}

	


}


