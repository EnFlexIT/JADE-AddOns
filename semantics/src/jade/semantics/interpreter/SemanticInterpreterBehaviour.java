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
package jade.semantics.interpreter;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.semantics.lang.sl.content.ContentParser;
import jade.semantics.lang.sl.grammar.ActionContentExpressionNode;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ActionExpressionNode;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.FalseNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.SymbolNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TrueNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.lang.sl.tools.SLPatternManip.WrongTypeException;
import jade.util.Logger;
import jade.util.leap.ArrayList;

/**
 * Class that represents the main behaviour of a semantic agent.
 * @author Vincent Pautret - France Telecom
 * @version Date: 2004/11/30 Revision: 1.0 
 */
public class SemanticInterpreterBehaviour extends CyclicBehaviour {
    
    /**
     * NotUnderstood content pattern
     */
    private Content NotUnderstoodContentPattern;
    
    /**
     * NotUnderstandable pattern
     */
    private Formula NotUnderstandablePattern;
    
    /**
     * NotUnderstandable default pattern
     */
    private Formula NotUnderstandableDefaultPattern = SLPatternManip.fromFormula("(B myself (not-understandable not-understandable null))");
    
    /**
     * Current semantic representation list
     */
    private ArrayList currentSRList;
    
    /**
     * New semantic representation list
     */
    private ArrayList newSRList;
    
    /**
     * Current semantic interpretation principle
     */
    private SemanticInterpretationPrinciple currentSemanticInterpretationPrinciple;
    
    /**
     * Logger
     */
    private Logger logger;
    
    /**
     * List of behaviours to be added to the agent 
     */
    private ArrayList behaviourToAdd;
    
    /**
     * List of behaviours to be removed from the agent
     */
    private ArrayList behaviourToRemove;
    
    /**
     * List of formulae to be asserted in the knowledge base 
     */
    private ArrayList formulaToAssert;
    
    /**
     * List of events (external or internal)
     */
    private ArrayList eventList;
    
    /**
     * Pattern for matching incoming ACL messages
     */
    private MessageTemplate messageTemplate;
    
    /*********************************************************************/
    /**				 			CONSTRUCTOR								**/
    /*********************************************************************/
    
    /**
     * Creates a new semantic interpretation behaviour.
     * @param msgTemplate a pattern for matching incoming ACL messages
     */
    public SemanticInterpreterBehaviour(MessageTemplate msgTemplate) {
        super();
        NotUnderstandablePattern = SLPatternManip.fromFormula("(B ??agent (not-understandable ??reason ??object))");
        NotUnderstoodContentPattern = SLPatternManip.fromContent("(??act (??reason ??object))");
        currentSRList = new ArrayList();
        newSRList = new ArrayList();
        behaviourToAdd = new ArrayList();
        behaviourToRemove = new ArrayList();
        formulaToAssert = new ArrayList();
        eventList = new ArrayList();
        messageTemplate = msgTemplate;
        logger = Logger.getMyLogger("jade.core.behaviours.SemanticInterpreterBehaviour");
    } // End of SemanticInterpreterBehaviour/0
    
    /*********************************************************************/
    /**				 			LOCAL METHODS						    **/
    /*********************************************************************/
    /**
     * Receives an <b>ACL</b> message matching the template given in the 
     * constructor.
     * @return the incoming message
     * @see jade.core.Agent#receive(jade.lang.acl.MessageTemplate)
     * @see jade.core.Agent#receive()
     */
    ACLMessage receiveNextMessage()
    {
        ACLMessage msg = null;
        if (messageTemplate != null) {
            msg = myAgent.receive(messageTemplate);
        }
        else {
            msg = myAgent.receive();
        }
        return msg;
    }
    
    /**
     * Returns a semantic representation that represents the ACL message passed
     * in parameter. Returns null if an exception occurs.
     * @param msg an ACL message
     * @return semantic representation that represents the ACL message passed in
     *         parameter.
     */
    public SemanticRepresentation directSRFromACLMessage(ACLMessage msg) {
        try {
            SemanticRepresentation sr = new SemanticRepresentation();
            sr.setMessage(msg);
            if (msg.getPerformative() == ACLMessage.NOT_UNDERSTOOD) {
                sr.setSLRepresentation(new TrueNode());
            }
            else {
                Formula formulaPattern = SLPatternManip.fromFormula("(B ??agent (done ??act))");
                Formula formula = (Formula)SLPatternManip.instantiate(formulaPattern,
                        "agent", ((SemanticAgent)myAgent).getSemanticCapabilities().getAgentName(),
                        "act",   ((SemanticAgent)myAgent).getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(msg).toActionExpression()
                );
                sr.setSLRepresentation(formula);
            }
            return sr;
        }
        catch (SemanticInterpretationException sie) {
            return createNotUnderstandableSR(sie.getReason(), sie.getObject(), msg);
        }
        catch (WrongTypeException wte) {
            wte.printStackTrace();
            return null;
        }
    } // End of directSRFromACLMessage/2
    /*********************************************************************/
    /**				 			METHODS									**/
    /*********************************************************************/
    
    /**
     * An infinite loop which applies the semantic interpretation principles
     * to all the messages or internal events. 
     * @see jade.core.behaviours.Behaviour#action()
     */
    public void action() {        
        try {
            ACLMessage msg = receiveNextMessage();
            SemanticRepresentation sr = null;
            if (msg != null) {
                sr = directSRFromACLMessage(msg);
                eventList.add(sr);
            }
            while (eventList.size() != 0) {
                if (logger.isLoggable(Logger.FINER)) logger.log(Logger.FINER, ((SemanticAgent)myAgent).getSemanticCapabilities().getMySemanticActionTable().toString());
                behaviourToAdd.clear();
                behaviourToRemove.clear();
                formulaToAssert.clear();
                currentSRList.clear();
                newSRList.clear();
                sr = (SemanticRepresentation)eventList.remove(0);
                if (sr != null) {
                    currentSRList.add(sr);
                    if (!checkNotUnderstandable(currentSRList)) {
                        int minIndex = getMin(currentSRList);
                        while(minIndex < ((SemanticAgent)myAgent).getSemanticCapabilities().getMySemanticInterpretationTable().size() && minIndex >= 0) {
                            for (int i= 0; i < newSRList.size(); i++) {
                                currentSRList.add(newSRList.get(i));
                            }
                            newSRList.clear();
                            currentSemanticInterpretationPrinciple = ((SemanticAgent)myAgent).getSemanticCapabilities().getMySemanticInterpretationTable().getSemanticInterpretationPrinciple(minIndex);
                            if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "CURRENT SEMANTIC INTERPRETATION PRINCIPLE : " + currentSemanticInterpretationPrinciple);
                            while(!currentSRList.isEmpty()) {
                                SemanticRepresentation currentSR = (SemanticRepresentation)currentSRList.remove(0);
                                if (logger.isLoggable(Logger.FINE)) logger.log(Logger.FINE, "CURRENT SR : " + currentSR);
                                if (currentSR.getSemanticInterpretationPrincipleIndex() == minIndex) {
                                    ArrayList srListResult = null;                        
//                                  try {
                                    srListResult = currentSemanticInterpretationPrinciple.apply(currentSR);
//                                  } catch (Exception sie) {
//                                  srListResult = new ArrayList();
//                                  srListResult.add(currentSR);
//                                  }
                                    if (srListResult != null) {
                                        if (logger.isLoggable(Logger.FINE)) {
                                            logger.log(Logger.FINE, " SIP: " + currentSemanticInterpretationPrinciple + " succeeded !");
                                            logger.log(Logger.FINE, " applies on: " + currentSR.getSLRepresentation());
                                        }
                                        
                                        for (int j = 0; j < srListResult.size(); j++) {
                                            ((SemanticRepresentation)srListResult.get(j)).setSLRepresentation(((SemanticRepresentation)srListResult.get(j)).getSLRepresentation().getSimplifiedFormula());
                                            if (!newSRList.contains(srListResult.get(j)) &&
                                                    !currentSRList.contains(srListResult.get(j))) {
                                                newSRList.add(srListResult.get(j));
                                            }
                                            if (logger.isLoggable(Logger.FINE)) {
                                                logger.log(Logger.FINE, "SIP " + currentSemanticInterpretationPrinciple.getClass() + " produces:");
                                                logger.log(Logger.FINE, "NEW SR : " + j + " : " + srListResult.get(j));
                                            }
                                        }
                                        if (checkNotUnderstandable(srListResult)) {
                                            newSRList.clear();
                                            currentSRList.clear();
                                            behaviourToAdd.clear();
                                            behaviourToRemove.clear();
                                            formulaToAssert.clear();
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
                            ((SemanticAgent)myAgent).getSemanticCapabilities().getMyKBase().assertFormula(((SemanticRepresentation)newSRList.get(i)).getSLRepresentation());
                        }
                        for (int i = 0; i < behaviourToAdd.size(); i++) {			            
                            myAgent.addBehaviour((Behaviour)behaviourToAdd.get(i));
                        }
                        for (int i = 0; i < behaviourToRemove.size(); i++) {                       
                            myAgent.removeBehaviour((Behaviour)behaviourToRemove.get(i));
                        }
                        for (int i = 0; i < formulaToAssert.size(); i++) { 
                            ((SemanticAgent)myAgent).getSemanticCapabilities().getMyKBase().assertFormula((Formula)formulaToAssert.get(i));
                        }
                    }
                }
            }
            if (msg == null) block();
        } catch (Exception e) {
            e.printStackTrace();
        }
    } // End of action/0
    
    /**
     * Returns the minimum index of a semantic interpretation principle
     * in the list of semantic representation. Returns -1 if the list is 
     * <code>null</code>.
     * @param srList a list (ArrayList) of SemanticRepresentation objects.
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
        return min;
    } // End of getMin/1
    
    /**
     * Returns true if one of the Semantic Representation contained in the list 
     * is false. Sends a Not Understood message in this case.
     * @param list a list of Semantic Representation
     * @return true if one of the Semantic Representation contained in the list 
     * is false, false if not.
     */
    private boolean checkNotUnderstandable(ArrayList list) {
        for(int i = 0; i < list.size(); i++) {
            SemanticRepresentation sr = (SemanticRepresentation)list.get(i);
            MatchResult matchResult = null;
            if( sr.getSLRepresentation() instanceof FalseNode || 
                    (matchResult = SLPatternManip.match(NotUnderstandablePattern, sr.getSLRepresentation())) != null ) {
                ACLMessage reply = sr.getMessage().createReply();
                reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
                ActionExpression msg = new ActionExpressionNode(SLPatternManip.fromTerm(sr.getMessage().getSender().toString()),
                        SLPatternManip.fromTerm(sr.getMessage().toString()));
                Content content = null;
                try {
                    content = (Content)SLPatternManip.instantiate(NotUnderstoodContentPattern, "act", new ActionContentExpressionNode(msg));
                    if ( matchResult == null ) {
                        content = (Content)SLPatternManip.instantiate(content, 
                                "reason", new SymbolNode("inconsistent"),
                                "object", new WordConstantNode(""));
                    }
                    else {
                        content = (Content)SLPatternManip.instantiate(content, 
                                "reason", new SymbolNode(((StringConstantNode)matchResult.getTerm("reason")).lx_value()),
                                "object", matchResult.getTerm("object"));
                    }
                    
                    ContentParser cp = ((SemanticAgent)myAgent).getSemanticCapabilities().getContentParser(sr.getMessage().getLanguage());
                    if ( cp == null ) {
                        cp = ((SemanticAgent)myAgent).getSemanticCapabilities().getContentParser("fipa-sl");
                        reply.setLanguage("fipa-sl");
                    }
                    reply.setContent(cp.unparseContent(content));
                }
                catch(Exception e) {
                    e.printStackTrace();
                    reply.setContent("((action "+sr.getMessage().getSender().toString()+" "+sr.getMessage().toString()+") true)");
                    reply.setLanguage("fipa-sl");
                }
                myAgent.send(reply);
                return true;
            }
        }
        return false;
    } // End of checkNotUnderstandable/1
    
    /**
     * Creates a Semantic Representation. The SL representation of this SR means 
     * that the agent regards the incoming act as not understandable. 
     * @param reason the reason 
     * @param object the object 
     * @param msg the message 
     * @return a semantic representation
     */
    public SemanticRepresentation createNotUnderstandableSR(String reason, Term object, ACLMessage msg) {
        try {
            return new SemanticRepresentation(
                    msg,
                    (Formula)SLPatternManip.instantiate(NotUnderstandablePattern,
                            "agent", ((SemanticAgent)myAgent).getSemanticCapabilities().getAgentName(),
                            "reason", new StringConstantNode(reason),
                            "object", (object == null) ? new StringConstantNode("") : object),
                            0);
        }
        catch(Exception e) {
            e.printStackTrace();
            return new SemanticRepresentation(
                    msg,
                    NotUnderstandableDefaultPattern,
                    0);		
        }
    }
    
    /**
     * Returns the list of behaviours to be added.
     * @return the list of behaviours to be added.
     */
    public ArrayList getBehaviourToAdd() {
        return behaviourToAdd;
    }
    
    /**
     * Returns the list of behaviours to be removed.
     * @return the list of behaviours to be removed.
     */
    public ArrayList getBehaviourToRemove() {
        return behaviourToRemove;
    }
    
    /**
     * Returns the list of formulae to be asserted.
     * @return the list of formulae to be asserted.
     */
    public ArrayList getFormulaToAssert() {
        return formulaToAssert;
    }
    
    /**
     * Interprets a SemanticRepresentation, i.e. adds this SR in the internal 
     * event list.
     * @param sr a SemanticRepresentation that represents the event
     */
    public void interpret(SemanticRepresentation sr) {
        eventList.add(sr);
        this.restart();
    } // End of interpret/1
    
    /**
     * Interprets a formula, i.e. creates an event (a SemanticRepresentation) 
     * in the internal event list from the given formula.
     * @param formula a formula to be interpreted
     */
    public void interpret(Formula formula) {
        interpret(new SemanticRepresentation(formula));
    } // End of interpret/1
    
    /**
     * Creates an event (a SemanticRepresentation) in the internal event list from
     * the string that represents an SL formula.
     * @param s the string representation of an SL formula.
     */
    public void interpret(String s) {
        try {
            Formula f = SLPatternManip.fromFormula(s);
            interpret(new SemanticRepresentation((Formula)SLPatternManip.instantiate(f, "agent", ((SemanticAgent)myAgent).getSemanticCapabilities().getAgentName())));
        } catch (Exception e) {e.printStackTrace();}
    } // End of interpret/1
    
} // End of class SemanticInterpreterBehaviour