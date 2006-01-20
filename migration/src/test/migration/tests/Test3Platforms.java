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

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import test.common.Test;
import test.common.TestException;
import test.common.TestUtility;
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
public class Test3Platforms extends Test {
  
  public static final String AGENTS_PATH = "migration" + File.separator + "path4";

  public Behaviour load(Agent a) throws TestException {
    AID remoteAMS = (AID) getGroupArgument(MigrationTesterAgent.REMOTE_AMS_KEY);
    AID remoteAMS2 = (AID) getGroupArgument(MigrationTesterAgent.REMOTE_AMS_KEY_2);
    String[] args = new String[3];
    args[0] = a.getName() + "#" + a.getAID().getAddressesArray()[0];
    args[2] = remoteAMS2.getName() + "#" + remoteAMS2.getAddressesArray()[0];
    String hostname;
    try{
      hostname = InetAddress.getLocalHost().getHostName();
    }catch(UnknownHostException e){
      hostname = "localhost";
    }
    args[1] = "ams@Remote-Platform-3#http://"+hostname+":9999/acc";
    _jc3 = TestUtility.launchJadeInstance("Remote-Platform-3", 
                                    null,
                                   "-name Remote-Platform-3 "
                                   + "-port 6666 "
                                   + " "
                                   + " "+TestUtility.HTTP_MTP_ARG+" "
                                   + "-mtp "
                                   + "jade.mtp.http.MessageTransportProtocol(http://"+hostname+":9999/acc) " 
                                   + MigrationTesterAgent.AGENTS_PATH_KEY + " "
                                   + AGENTS_PATH + " -services "
                                   + MigrationTesterAgent.MIGRATION_SERVICE + ";" 
                                   + MigrationTesterAgent.MOBILITY_SERVICE,null);
    
    AID resp = TestUtility.createAgent(a, "pep", "test.migration.agents.GenericMovingAgent",args, remoteAMS, null);
    return new ResponderBehaviour(a, resp);
  }
  
  private class ResponderBehaviour extends SimpleBehaviour {
    public ResponderBehaviour(Agent a, AID resp){
      super(a);
      _movAgent = resp;
      _block = true;
    }
    /* (non-Javadoc)
     * @see jade.core.behaviours.Behaviour#action()
     */
    public void action() {
      if(_block) _msg = myAgent.receive(MessageTemplate.MatchSender(_movAgent));
      if(_msg != null || !_block){
        if(_block){
          block(5000); //block 5 seconds waiting finalisation of migration process
          System.out.println("Blocking tester agent for five seconds");
          _block = false;
          return;
        }
        if(_msg.getPerformative() == ACLMessage.INFORM)
          passed("Agent has migrated correctly");
        else
          failed("Some error has ocurred while agent's migration");
        _finished=true;
      }else block();
    }
    public boolean done(){
      return _finished;
    }
    private boolean _block;
    private ACLMessage _msg = null;
    private AID _movAgent;
    private boolean _finished = false;

  }
  /* (non-Javadoc)
   * @see test.common.Test#clean(jade.core.Agent)
   */
  public void clean(Agent a) {
    _jc3.kill();
    
  }
  
  private JadeController _jc3;

}
