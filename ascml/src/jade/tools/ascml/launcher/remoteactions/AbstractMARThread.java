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


package jade.tools.ascml.launcher.remoteactions;

import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;
import jade.tools.ascml.launcher.AgentLauncher;
import jade.tools.ascml.onto.*;

import java.util.Iterator;

public abstract class AbstractMARThread implements Runnable {
	
	public static final int RESOLVE_SOCIETY = 0;
	public static final int RESOLVE_AGENTTYPE = 1;
	public static final int RESOLVE_AGENTINSTANCE = 2;
	
	
	protected Iterator it;
	protected ACLMessage message;
	protected AgentLauncher al;
	protected Thread t;
	protected Action action;
	protected int toResolve;
	
	public AbstractMARThread(AgentLauncher al, Iterator it, ACLMessage message, Action a, AbsModel m) {
		this.it = it;
		this.message = message;
		this.al = al;
		this.action = a;	
		toResolve=-1;
		if(m instanceof AgentInstance)
			toResolve=MARResolverThread.RESOLVE_AGENTINSTANCE;
		else if(m instanceof AgentType)
			toResolve=MARResolverThread.RESOLVE_AGENTTYPE;
		else if(m instanceof SocietyInstance)
			toResolve=MARResolverThread.RESOLVE_SOCIETY;
		t = new Thread(this, "MARThread ("+m+")");
		t.start();
	}	
	
	public abstract void doAction() throws Exception;
	
	public void run() {
		try {
			doAction();
			Iterator it = message.getAllReceiver();
            while (it.hasNext()) {
                jade.core.AID recv = (jade.core.AID)it.next();
            }
			jade.content.onto.basic.Done done = new jade.content.onto.basic.Done();
			done.setAction(action);

			al.getContentManager().fillContent(message, done);
			message.setPerformative(ACLMessage.INFORM);
			System.out.println("AbstractMARThread.run: replying with INFORM-Done");
			al.send(message);
		} catch (Exception e) {
			e.printStackTrace();
			message.setContent(e.getMessage());
			al.send(message);
		}
	}
}
