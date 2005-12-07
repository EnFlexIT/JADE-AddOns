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

import java.util.Vector;

import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.FIPAProtocolNames;
import jade.tools.ascml.absmodel.*;
import jade.tools.ascml.events.*;
import jade.tools.ascml.launcher.behaviours.LongTimeActionBehaviour;
import jade.tools.ascml.onto.*;

public class ListenerManagerInterface implements ToolTakeDownListener, ExceptionListener, ModelChangedListener, LongTimeActionStartListener  {

    AgentLauncher al;
    
    public ListenerManagerInterface(AgentLauncher launcher) {
        al=launcher;
    }

    /**
     * This method is called, when the ASCML should be taken down. The Launcher
     * implements the ToolTakeDownListener-interface (by implementing this
     * method) and registers itself at the GUI-components to be informed when
     * the user closes the GUI
     * 
     * @param event
     *            The ToolTakeDown-event (till now, no logic is implemented in
     *            this event but later on, some properties might be passed to
     *            the launcher.
     * @see jade.core.Agent#takeDown()
     */
    public void toolTakeDown(ToolTakeDownEvent event) {
        al.toolTakeDown();
    }

    /**
     * This method is called, when an Exception occurs in some of the GUI- or
     * repository-elements. Normally all Exceptions are handled by the GUI (an
     * ExceptionDialog is presented to the user) but in case the launcher also
     * needs to take some action, the exceptionHandling might be implemented in
     * the method.
     * 
     * @param exc
     *            The ExceptionEvent containing the exception-object.
     */
    public void exceptionThrown(ExceptionEvent exc) {
        // HACK, a more sophisticated exceptionHandling is needed, depending on
        // the kind of exception the launcher has to be shut down...
        // this.doDelete();

        // Exception e = exc.getException();
        // e.printStackTrace();
    }

    /**
     * Start an action, that is possibly going to last some time. The reason for
     * starting long-lasting actions this way is, that most of these actions are
     * working on elements which are displayed on the gui. And in order to be
     * independent of the gui's event-thread these actions are executed in the
     * agent's thread. Note: Using JADE's GUIAgent class is not possible,
     * because the ASCML needs to extend ToolAgent.
     * 
     * @param event
     *            The event, that contains the ID of the long-time action.
     */
    public void startLongTimeAction(LongTimeActionStartEvent event) {
        al.addBehaviour(new LongTimeActionBehaviour(al.getRepository(), event));
    }


    /**
     * This method is implemented, because the AgentLauncher is registered as a
     * ModelChangedListener. It is called everytime a model changed.
     * 
     * @param event
     *            The ModelChangedEvent, which contains the eventCode and the
     *            model itself.
     */
    public synchronized void modelChanged(ModelChangedEvent event) {
        
        ContentManager cm;
        Codec sl;
        Ontology onto;
        cm = new ContentManager();
        sl = new SLCodec();
        cm.registerLanguage(sl);
        onto = ASCMLOntology.getInstance();
        cm.registerOntology(onto);          

        //FIXME: Rework this code
        String eventCode = event.getEventCode();
        if ((eventCode == ModelChangedEvent.STATUS_CHANGED) || (eventCode == ModelChangedEvent.RUNNABLE_REMOVED)){
            Object eventModel;
            if (eventCode == ModelChangedEvent.STATUS_CHANGED) {
                eventModel=event.getModel();
            } else {
                eventModel=event.getUserObject();
            }
            if (eventModel instanceof IAbstractRunnable) {
                // System.err.println("ListenerManagerInterface.modelChanged: received statusChanged-event. model=" + event.getModel() + " status=" + ((IAbstractRunnable) event.getModel()).getStatus());
                AbsModel model;
                IAbstractRunnable absRunnable = (IAbstractRunnable)eventModel;
                al.getSubscriptionManager().notify(absRunnable);
                
                //RunnableRemoteSocietyInstanceReferenceModel
                if (absRunnable instanceof IRunnableSocietyInstance) {
                    model = new SocietyInstance();               
                } else if (absRunnable instanceof IRunnableAgentInstance) {
                    model = new AgentInstance();
                } else {
                    //System.err.println("absRunnable.getClass()="+absRunnable.getClass().getName());
                }
                // maybe do something with that model, for example send
                // informStatus-messages to subscribers.
                
                if (al.subscribedASCMLS.containsKey(absRunnable.getFullyQualifiedName())) {
                    Vector<AID> ascmlAIDs = al.subscribedASCMLS.get(absRunnable.getFullyQualifiedName());

                    if (ascmlAIDs.size()==0) {
                        return;
                    }                
                    
                    Status status = new Status();
		    // I will tweak this to compile, I've not touched this code before, it might be wrong
                    Status ms;
                    SocietyInstance socI = new SocietyInstance();
                    socI.setFullQuallifiedName(absRunnable.getFullyQualifiedName());
                    if (absRunnable.getStatus() instanceof Known) 
                        //ms = new STATUS_CREATED();
			ms = new Known();
                    else if (absRunnable.getStatus() instanceof jade.tools.ascml.onto.Error) 
                        //ms = new STATUS_ERROR();
			ms = new jade.tools.ascml.onto.Error();
		    /* //The following three possibilities are not covered by the new Status mechanism
                    else if (absRunnable.getStatus() == AbstractRunnable.STATUS_NOT_CREATED) 
                        //ms = new STATUS_NOT_CREATED();
			// I guess this means we CAN create an instance if we need to, so it's available
			ms = new Available();
                    else if (absRunnable.getStatus() == AbstractRunnable.STATUS_NOT_RUNNING) 
                        //ms = new STATUS_NOT_RUNNING();
			// this is nonexistant in the modified ontology, this whole code block needs to be rewritten
			// I'll just make it compile for now
			ms = new jade.tools.ascml.onto.Error();
                    else if (absRunnable.getStatus() == AbstractRunnable.STATUS_PARTLY_RUNNING) 
                        //ms = new STATUS_PARTLY_RUNNING();
			// I think there's no such thing as partly running
			ms = new Started();*/
                    else if (absRunnable.getStatus() instanceof Functional) 
                        //ms = new STATUS_RUNNING();
			ms = new Functional();
                    else if (absRunnable.getStatus() instanceof Starting) 
                        //ms = new STATUS_STARTING();
			ms = new Starting();
                    else if (absRunnable.getStatus() instanceof Stopping) 
                        //ms = new STATUS_STOPPING();
			ms = new Stopping();
                    else
                        //ms = new STATUS_ERROR();
			ms = new jade.tools.ascml.onto.Error();
		    /* Now this part needs some more work...
                    status.setModelsStatus(ms);                    
                    status.setModel(socI);
		     **/
                    ACLMessage msg = new ACLMessage(ACLMessage.INFORM); 
                    msg.setProtocol(FIPAProtocolNames.FIPA_SUBSCRIBE);
                    msg.setLanguage(sl.getName());
                    msg.setOntology(onto.getName());
                    for (int i = 0; i < ascmlAIDs.size(); i++) {
                        AID oneAID = ascmlAIDs.get(i);
                        msg.addReceiver(oneAID);
                    }
                    try {                        
                        // FIXME cm.fillContent(msg,status);
                    } catch (Exception e) {
                        System.err.println("Could not inform remote ASCMLs. Reason:");
                        System.err.println(e.toString());
                        e.printStackTrace();
                        return;
                    }
                    al.send(msg);
                }
            }
        }
    }

 


}
