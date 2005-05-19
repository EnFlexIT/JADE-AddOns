/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2004 France Télécom

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

/*
 * SemanticInterpreterBehaviour.java
 * Created on 28 oct. 2004
 * Author : Vincent Pautret
 */
package jade.semantics.behaviours;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.semantics.interpreter.SemanticAgent;
import jade.semantics.interpreter.SemanticInterpretationPrinciple;
import jade.semantics.interpreter.SemanticRepresentation;
import jade.semantics.interpreter.SemanticRepresentationExtraction;
import jade.semantics.kbase.KbaseImpl_List;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.util.Logger;
import jade.util.leap.ArrayList;

/**
 * Class that represents the main behaviour of a semantic agent.
 * @author Vincent Pautret
 * @version Date: 2004/11/30 17:00:00  Revision: 1.0 
 */
public class SemanticInterpreterBehaviour extends CyclicBehaviour {
    
    /**
     * Current semantic representation list
     */
    ArrayList currentSRList;
    
    /**
     * New semantic presentation list
     */
    ArrayList newSRList;
    
    /**
     * Current deductive step
     */
    SemanticInterpretationPrinciple currentSemanticInterpretationPrinciple;
    
    /**
     * Logger
     */
    Logger logger;
    
    
    ArrayList behaviourToAdd;
    
    ArrayList behaviourToRemove;

    ArrayList formulaToAssert;

    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/

    /**
     * Constructor
     */
    public SemanticInterpreterBehaviour() {
        super();
        currentSRList = new ArrayList();
        newSRList = new ArrayList();
        behaviourToAdd = new ArrayList();
        behaviourToRemove = new ArrayList();
        formulaToAssert = new ArrayList();
        logger = Logger.getMyLogger("jade.core.behaviours.SemanticInterpreterBehaviour");
    } // End of SemanticInterpreterBehaviour/0

    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/

    
    /**
     * An infinite loop which applies the deductive steps to all the messages or 
     * internal events. 
     * @see jade.core.behaviours.Behaviour#action()
     */
	public void action() {
	    try {
	        ACLMessage msg = myAgent.receive();
	        SemanticRepresentation sr = null;
	        while(msg != null) {
	            sr = SemanticRepresentationExtraction.extract(msg, myAgent.getName());
	            ((SemanticAgent)myAgent).getEventList().add(sr);
	            msg = myAgent.receive();
	        }
		    if (((SemanticAgent)myAgent).getEventList().size() != 0) {
		        if (logger.isLoggable(Logger.FINER)) logger.log(Logger.FINER, ((SemanticAgent)myAgent).getMySemanticActionTable().toString());
                behaviourToAdd.clear();
                behaviourToRemove.clear();
                formulaToAssert.clear();
		        currentSRList.clear();
		        newSRList.clear();
		        sr = (SemanticRepresentation)((SemanticAgent)myAgent).getEventList().remove(0);
			    if (sr != null) {
			        currentSRList.add(sr);
			        int minIndex = getMin(currentSRList);
			        while(minIndex < ((SemanticAgent)myAgent).getMySemanticInterpretationTable().getSemanticInterpretationPrincipleList().size() && minIndex >= 0) {
			            for (int i= 0; i < newSRList.size(); i++) {
			                currentSRList.add(newSRList.get(i));
			            }
			            newSRList.clear();
			            currentSemanticInterpretationPrinciple = ((SemanticAgent)myAgent).getMySemanticInterpretationTable().getSemanticInterpretationPrinciple(minIndex);
			            if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "CURRENT DEDUCTIVE STEP : " + currentSemanticInterpretationPrinciple);
			            while(!currentSRList.isEmpty()) {
			                SemanticRepresentation currentSR = (SemanticRepresentation)currentSRList.remove(0);
			                if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "CURRENT SR : " + currentSR);
			                if (currentSR.getSemanticInterpretationPrincipleIndex()-1 == minIndex) {
		                        ArrayList srListResult = currentSemanticInterpretationPrinciple.apply(currentSR);
		                        if (srListResult != null) {
		                            if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, " STEP : " + currentSemanticInterpretationPrinciple + " succeeded !");
		                            for (int j = 0; j < srListResult.size(); j++) {
		                                ((SemanticRepresentation)srListResult.get(j)).setSLRepresentation(((SemanticRepresentation)srListResult.get(j)).getSLRepresentation().getSimplifiedFormula());
		                                newSRList.add(srListResult.get(j));
		                                if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "NEW SR : " + j + " : " + srListResult.get(j));
		                            }
		                            if (hasFalse(srListResult)) {
		                                newSRList.clear();
		                                currentSRList.clear();
		                                behaviourToAdd.clear();
                                        behaviourToRemove.clear();
                                        formulaToAssert.clear();
		            			        ACLMessage reply = sr.getMessage().createReply();
		            			        reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
		            			        reply.setContent("(inconsistent)");
		            			    	myAgent.send(reply);
		                            }
		                        } else {
		                            if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, " STEP : " + currentSemanticInterpretationPrinciple + " not succeeded !");
			                        currentSR.setSemanticInterpretationPrincipleIndex(currentSR.getSemanticInterpretationPrincipleIndex() + 1);
			                        newSRList.add(currentSR);
		                        }
			                } else {
			                    newSRList.add(currentSR);
			                }
			            }
			            minIndex = getMin(newSRList);
			        }
			        for (int i = 0; i < newSRList.size(); i++) {
			            ((SemanticAgent)myAgent).getMyKBase().assertFormula(((SemanticRepresentation)newSRList.get(i)).getSLRepresentation());
			        }
                    //Commit
			        for (int i = 0; i < behaviourToAdd.size(); i++) {			            
			            myAgent.addBehaviour((Behaviour)behaviourToAdd.get(i));
			        }
                    for (int i = 0; i < behaviourToRemove.size(); i++) {                       
                        myAgent.removeBehaviour((Behaviour)behaviourToRemove.get(i));
                    }
                    for (int i = 0; i < formulaToAssert.size(); i++) { 
                        ((SemanticAgent)myAgent).getMyKBase().assertFormula((Formula)formulaToAssert.get(i));
                    }
			    }
		    } else {
		        block();
		    }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	} // End of action/0

    /**
     * Returns the minimum index of a deductive step in the list of semantic 
     * representation
     * @param srList a list (ArrayList) of SemanticRepresentaion objects.
     * @return the minimum index
     */
    public int getMin(ArrayList srList) {
        if (srList == null) return -1;
        int min = -1;
        if (srList.size() > 0) {
            min = ((SemanticRepresentation)srList.get(0)).getSemanticInterpretationPrincipleIndex();
            for (int i = 1; i < srList.size(); i++) {
                if (((SemanticRepresentation)srList.get(i)).getSemanticInterpretationPrincipleIndex() < min ) {
                    min = ((SemanticRepresentation)srList.get(i)).getSemanticInterpretationPrincipleIndex();
                }
            }
        }
        return min - 1;
    } // End of getMin/1
    
    
    /**
     * Returns true if one of the Semantic Representation contained in the list 
     * is false.
     * @param list a list of Semantic Representation
     * @return true if one of the Semantic Representation contained in the list 
     * is false, false if not.
     */
    private boolean hasFalse(ArrayList list) {
        for(int i = 0; i < list.size(); i++) {
            if(((SemanticRepresentation)list.get(i)).getSLRepresentation() instanceof FalseNode) {
                return true;
            }
        }
        return false;
    } // End of hasFalse/1
    
    
    
    /**
     * @return Returns the behaviourToAdd.
     */
    public ArrayList getBehaviourToAdd() {
        return behaviourToAdd;
    }
    /**
     * @return Returns the behaviourToRemove.
     */
    public ArrayList getBehaviourToRemove() {
        return behaviourToRemove;
    }
    /**
     * @return Returns the formulaToAssert.
     */
    public ArrayList getFormulaToAssert() {
        return formulaToAssert;
    }
} // End of class SemanticInterpreterBehaviour

