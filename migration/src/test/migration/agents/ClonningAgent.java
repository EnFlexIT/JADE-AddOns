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

package test.migration.agents;

import java.util.StringTokenizer;

import jade.core.AID;
import jade.core.Agent;
import jade.core.ContainerID;
import jade.core.Location;
import jade.core.PlatformID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

/**
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Jordi Cucurull-Juan
 * 
 */
public class ClonningAgent extends Agent {

public void setup(){
    Object[] args = getArguments();
    
    if(args != null){
      _testerAID = new AID(
          ((String)args[0]).substring(0,((String)args[0]).indexOf('#')),
          AID.ISGUID);
      _testerAID.addAddresses(
          ((String)args[0]).substring(((String)args[0]).indexOf('#') + 1));
      _failure = new ACLMessage(ACLMessage.FAILURE);
      _failure.addReceiver(_testerAID);
      _inform = new ACLMessage(ACLMessage.INFORM);
      _inform.addReceiver(_testerAID);
      AID remotePltf = new AID(
          ((String)args[1]).substring(0,((String)args[1]).indexOf('#')),
          AID.ISGUID);
      remotePltf.addAddresses(
          ((String)args[1]).substring(((String)args[1]).indexOf('#') + 1));
      _dests[0] = new PlatformID(remotePltf);
      _dests[1] = new ContainerID((String)args[2], null);
      
      addBehaviour(new CyclicBehaviour(this){

        public void action() {
          
          switch(__state){
            case 0:
              __state++;
              myAgent.doMove(_dests[0]);
              break;
            case 1:
              if(!myAgent.getAMS().equals(((PlatformID)_dests[0]).getAmsAID())){
                myAgent.send(_failure);
                myAgent.doDelete();
              }else{
                __state++;
                myAgent.doClone(_dests[1], "myClone");
              }
              break;
            case 2:
              try{
                if(myAgent.getAID().getLocalName().equals("myClone") && 
                    myAgent.getContainerController().getContainerName().equals("Container-1")){
                  System.out.println("Sending inform message to my Tester");
                  myAgent.send(_inform);
                }
              }catch(Exception e){
                myAgent.send(_failure);
              }
                System.out.println("It's the end");
              myAgent.doDelete();
              break;
            default: 
              myAgent.doDelete();
          }
        }
        
        private int __state = 0;
      });
    }
  }

private Location[] _dests = new Location[2];
private AID _testerAID;
private ACLMessage _failure;
private ACLMessage _inform;

}
