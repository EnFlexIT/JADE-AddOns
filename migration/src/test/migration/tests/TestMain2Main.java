/*****************************************************************
 JADE - Java Agent DEvelopment Framework is a framework to develop 
 multi-agent systems in compliance with the FIPA specifications.
 Copyright (C) 2000 CSELT S.p.A. 
 
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

// Created on 10-jun-2004

package test.migration.tests;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import test.common.Test;
import test.common.TestException;
import jade.core.AID;
import test.common.*;
import test.migration.MigrationTesterAgent;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * 
 */
public class TestMain2Main extends Test {

	/* (non-Javadoc)
	 * @see test.common.Test#load(jade.core.Agent)
	 */
	public Behaviour load(Agent a) throws TestException {
		AID remoteAMS = (AID) getGroupArgument(MigrationTesterAgent.REMOTE_AMS_KEY);
		AID remoteAMS2 = (AID) getGroupArgument(MigrationTesterAgent.REMOTE_AMS_KEY_2);
		
		/*
		// Prepare the arguments for the GenericMovingAgent: 
		// - Arg-0: tester agent's AID
		// - Arg-1 .... Arg-n: A list of locations to visit
		Object[] args = new Object[2];
		args[0] = a.getAID();
		args[1] = new PlatformID(remoteAMS2);
		*/
		String[] args = new String[2];
		args[0] = a.getName() + "#" + a.getAID().getAddressesArray()[0];
		args[1] = remoteAMS2.getName() + "#" + remoteAMS2.getAddressesArray()[0];
		AID resp = TestUtility.createAgent(a, "pep", "test.migration.agents.GenericMovingAgent",args, remoteAMS, null);
		return new ResponderBehaviour(a, resp);
	}
	
	private class ResponderBehaviour extends SimpleBehaviour {
		private AID _movAgent;
		private boolean _finished = false;
		
		public ResponderBehaviour(Agent a, AID resp){
			super(a);
			_movAgent = resp;
		}
		
		/* (non-Javadoc)
		 * @see jade.core.behaviours.Behaviour#action()
		 */
		public void action() {
			ACLMessage msg = myAgent.receive(MessageTemplate.MatchSender(_movAgent));
			if(msg != null){
				if(msg.getPerformative() == ACLMessage.INFORM)
					passed("--- Agent migrated correctly");
				else
					failed("--- Migration error");
				_finished=true;
			}else block();
		}
		
		public boolean done(){
			return _finished;
		}
	}
}
