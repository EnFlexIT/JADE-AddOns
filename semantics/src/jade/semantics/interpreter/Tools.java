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
 * Tools.java
 * Created on 23 juin 2005
 * Author : Vincent Pautret
 */
package jade.semantics.interpreter;

import jade.core.AID;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticAction;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.TermSequenceNode;
import jade.semantics.lang.sl.grammar.TermSetNode;
import jade.semantics.lang.sl.grammar.WordConstantNode;
import jade.semantics.lang.sl.tools.MatchResult;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.util.leap.Iterator;

/**
 * Utility class.
 * @author Vincent Pautret - France Telecom
 * @version Date:1.0 2005/06/23 Revision: 1.0
 */
public class Tools {
    
    /**
     * General pattern used to recognize functional terms denoting
     * agent descriptors
     */
    static final private Term AGENT_IDENTIFIER_PATTERN = SLPatternManip
    .fromTerm("(agent-identifier " +
                "(::? :addresses ??adresses) " +
                ":name ??name " +
                "(::? :resolvers ??resolvers))");
    
    /***************************************************************************
     * STATIC METHODS /
     **************************************************************************/
    
    /**
     * Returns the AID corresponding to the term representing an agent. Returns 
     * null if the term is not an agent or if an exception occurs.
     * @param agentTerm a term representing an agent 
     * @return the AID corresponding to the term representing an agent 
     */
    public static AID term2AID(Term agentTerm) {
        AID result = null;
        MatchResult matchResult = SLPatternManip.match(AGENT_IDENTIFIER_PATTERN, agentTerm);
        if (matchResult != null) {
            try {
                result = new AID(((WordConstantNode)matchResult.getTerm("name")).lx_value(), true);
                Term addresses = matchResult.getTerm("addresses");
                if (addresses != null && addresses instanceof TermSequenceNode) {
                    for (int i = 0 ; i < ((TermSequenceNode)addresses).as_terms().size() ; i++) {
                        result.addAddresses(((StringConstantNode)((TermSequenceNode)addresses).as_terms().get(i)).lx_value());
                    }
                }
                Term resolvers = matchResult.getTerm("resolvers");
                if (resolvers != null && resolvers instanceof TermSequenceNode) {
                    for (int i = 0 ; i < ((TermSequenceNode)resolvers).as_terms().size() ; i++) {
                        result.addResolvers(term2AID((Term)((TermSequenceNode)resolvers).as_terms().get(i)));
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
        }
        return result;
    } // End of term2AID/1
    
    /**
     * Returns the term representing an agent to the corresponding AID. Returns 
     * null if an exception occurs.
     * @param aid an AID
     * @return a term representing an agent, or null.
     */
    public static Term AID2Term(AID aid) {
        TermSetNode addresses = null;
        TermSetNode resolvers = null;
        Iterator aidIterator = aid.getAllAddresses();
        if (aidIterator.hasNext()) {
            addresses = new TermSetNode(new ListOfTerm());
            while (aidIterator.hasNext()) {
                addresses.as_terms().add(new StringConstantNode((String)aidIterator.next()));
            }
        }
        aidIterator = aid.getAllResolvers();
        if (aidIterator.hasNext()) {
            resolvers = new TermSetNode(new ListOfTerm());
            while (aidIterator.hasNext()) {
                resolvers.as_terms().add(AID2Term((AID)aidIterator.next()));
            }
        }
        try {
            Term result = (Term)AGENT_IDENTIFIER_PATTERN.getClone();            
            SLPatternManip.set(result, "name", new WordConstantNode(aid.getName()));
            if (addresses != null) SLPatternManip.set(result, "addresses", addresses);
            if (resolvers != null) SLPatternManip.set(result, "resolvers", resolvers);
            SLPatternManip.substituteMetaReferences(result);
            SLPatternManip.removeOptionalParameter(result);
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    } // End of AID2Term/1
    
    
    /**
     * Tests if the action expression given in parameter is a communicative 
     * action from the semantic agent (me) to the specified receiver.
     * @param actionExp an action expression
     * @param receiver the receiver
     * @param me the current semantic agent 
     * @return true if the the action expression given in parameter is a 
     * communicative action from the semantic agent (me) to the specified 
     * receiver, false if not.
     */
    public static boolean isCommunicativeActionFromMeToReceiver(ActionExpression actionExp, Term receiver, SemanticAgent me) {
        try {
            SemanticAction action = me.getSemanticCapabilities().getMySemanticActionTable().getSemanticActionInstance(actionExp);
            if (action instanceof CommunicativeAction) {
                return (action.getAuthor().equals(me.getSemanticCapabilities().getAgentName()) &&
                        ((CommunicativeAction)action).getReceiverList().contains(receiver));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    } // End of isCommunicativeActionFromMeToReceiver/3
    
} // End of class Tools
