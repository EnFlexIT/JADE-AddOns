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

// Created on 30-jun-2004
// Authors Joan Ametller, Jordi Cucurull
package test.migration.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.ContainerID;
import jade.core.PlatformID;
import jade.core.AID;
import jade.core.Location;
import jade.lang.acl.ACLMessage;
/**
 * Description here
 * 
 * @author <a href="mailto:Joan.Ametller@uab.es">Joan Ametller Esquerra</a>
 * @author Carles Garrigues
 * 
 */
public class GenericMovingAgent extends Agent{

  private Object[] args = null; 
  private AID _testerAID;
  private ACLMessage _failure;
  private ACLMessage _inform;
  public void setup () {
    args = getArguments();
    if(args == null) System.out.println("the arguments are null");
    
    _testerAID = new AID(((String)args[0]).substring(0,((String)args[0]).indexOf('#')),AID.ISGUID);
    _testerAID.addAddresses(((String)args[0]).substring(((String)args[0]).indexOf('#') + 1));
    _failure = new ACLMessage(ACLMessage.FAILURE);
    _failure.addReceiver(_testerAID);
    _inform = new ACLMessage(ACLMessage.INFORM);
    _inform.addReceiver(_testerAID);
    
    addBehaviour(new CyclicBehaviour(this){
      private int i=1;
      public void action(){
        if(i < args.length){
            Location dest = parseString((String)args[i]);
            if(i != 1){
              Location ant = parseString((String)args[i-1]);
              if(ant instanceof PlatformID){
                if(!((PlatformID)ant).getAmsAID().equals(myAgent.getAMS())){
                  myAgent.send(_failure);
                  myAgent.doDelete();
                }
              }else if(ant instanceof ContainerID){
                try{
                  if(!((ContainerID)ant).getName().equals(myAgent.getContainerController().getContainerName())){
                    myAgent.send(_failure);
                    myAgent.doDelete();
                  }
                }catch(Exception e){
                  e.printStackTrace();
                  myAgent.doDelete();
                }
              }else{
                myAgent.send(_failure);
                myAgent.doDelete();
              }
            }
            i++;
            System.out.println("GenericMovingAgent: Migrating to: " + args[i-1]);
            myAgent.doMove(dest);
        }else{
          Location ant = parseString((String)args[i-1]);
          if(ant instanceof PlatformID){
            if(!((PlatformID)ant).getAmsAID().equals(myAgent.getAMS())){
              myAgent.send(_failure);
              myAgent.doDelete();
            }else{
              System.out.println("GenericMovingAgent: Itinerary finished, send INFORM");
              myAgent.send(_inform);
              myAgent.doDelete();
            }
          }else if(ant instanceof ContainerID){
            try{
              if(!((ContainerID)ant).getName().equals(myAgent.getContainerController().getContainerName())){
                myAgent.send(_failure);
                myAgent.doDelete();
              } else {
                System.out.println("GenericMovingAgent: Itinerary finished, send INFORM");
                myAgent.send(_inform);
                myAgent.doDelete();
              }
            }catch(Exception e){
              e.printStackTrace();
              myAgent.doDelete();
            }
          }else{
            myAgent.send(_failure);
            myAgent.doDelete();
          }          
        } 
      }
      
      private Location parseString(String arg){
        if (arg.indexOf("@") == -1){
          return new ContainerID(arg, null);
        }else{
          AID a = new AID(arg.substring(0,arg.indexOf('#')),AID.ISGUID);
          a.addAddresses(arg.substring(arg.indexOf('#') + 1));
          return new PlatformID(a);
        }
      }
      });
  }

}
