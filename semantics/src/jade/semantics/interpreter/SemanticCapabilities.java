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
* SemanticCapabilities.java
* Created on 13 mai 2005
* Author : Vincent Pautret
*/
package jade.semantics.interpreter;

import jade.lang.acl.MessageTemplate;
import jade.semantics.actions.CommunicativeAction;
import jade.semantics.actions.SemanticActionTable;
import jade.semantics.actions.SemanticActionTableImpl;
import jade.semantics.actions.performatives.AcceptProposal;
import jade.semantics.actions.performatives.Agree;
import jade.semantics.actions.performatives.CallForProposal;
import jade.semantics.actions.performatives.Cancel;
import jade.semantics.actions.performatives.Confirm;
import jade.semantics.actions.performatives.Disconfirm;
import jade.semantics.actions.performatives.Failure;
import jade.semantics.actions.performatives.Inform;
import jade.semantics.actions.performatives.NotUnderstood;
import jade.semantics.actions.performatives.Propose;
import jade.semantics.actions.performatives.QueryIf;
import jade.semantics.actions.performatives.QueryRef;
import jade.semantics.actions.performatives.Refuse;
import jade.semantics.actions.performatives.RejectProposal;
import jade.semantics.actions.performatives.Request;
import jade.semantics.actions.performatives.RequestWhen;
import jade.semantics.actions.performatives.RequestWhenever;
import jade.semantics.actions.performatives.Subscribe;
import jade.semantics.kbase.KBase;
import jade.semantics.kbase.FilterKBaseImpl;
import jade.semantics.lang.sl.content.ContentParser;
import jade.semantics.lang.sl.content.DefaultContentParser;
import jade.semantics.lang.sl.content.UnparseContentException;
import jade.semantics.lang.sl.grammar.ActionExpression;
import jade.semantics.lang.sl.grammar.Content;
import jade.semantics.lang.sl.grammar.ContentNode;
import jade.semantics.lang.sl.grammar.Formula;
import jade.semantics.lang.sl.grammar.IdentifyingExpression;
import jade.semantics.lang.sl.grammar.ListOfTerm;
import jade.semantics.lang.sl.grammar.StringConstantNode;
import jade.semantics.lang.sl.grammar.Term;
import jade.semantics.lang.sl.grammar.operations.DefaultNodeOperations;
import jade.semantics.lang.sl.tools.SLPatternManip;
import jade.semantics.planner.Planner;
import jade.util.leap.HashMap;

/**
* Capabilities of a Semantic Agent. This class provides methods to access all the
* components of a Semantic Agent: knowledge base, semantic interpretation table,
* semantic action table, planner, user customisation(StandardCustomisation), and
* the semantic interpreter behaviour.<br>
* A user can also create an application-specific SemanticCapabilities by 
* extending this class. In this case he has to override the different 
* <code>setupXXX</code> methods, depending of his needs.
* @author Vincent Pautret - France Telecom
* @version Date: 2005/05/13 Revision: 1.0
*/
public class SemanticCapabilities {
   
   /**
    * The semantic agent these capabilities are installed in 
    */
    protected SemanticAgent myAgent;
   
   /**
    * Knowledge base for this agent
    */
   protected KBase myKBase;
   
   /**
    * Semantic interpretation principles table of this agent
    */
   protected SemanticInterpretationPrincipleTable mySemanticInterpretationTable;
   
   /**
    * Semantic action table of this agent
    */
   protected SemanticActionTable mySemanticActionTable;
   
   /**
    * Semantic interpreter behaviour
    */
   protected SemanticInterpreterBehaviour myBehaviour;
   
   /**
    * Semantic agent action planner
    */
   protected Planner myPlanner;
   
   /**
    * Semantic agent customization
    */
   protected StandardCustomization myStandardCustomization;
   
   /**
    * Semantic agent customization
    */
   protected HashMap myContentParsers = new HashMap();
   
   /**
    * The SL Formula representation of this agent
    */
   protected Term agentName;
   
   
   static {
       DefaultNodeOperations.installOperations();
   }
   
   /*********************************************************************/
   /**                         CONSTRUCTOR                             **/
   /*********************************************************************/
   /**
    * Creates a new SemanticCapabilities
    * @param template a pattern for matching incoming ACL messages
    */
   public SemanticCapabilities(MessageTemplate template) {
       myBehaviour = new SemanticInterpreterBehaviour(template);
   } // End of SemanticCapabilities/2
   
   /**
    * Creates a new SemanticCapabilities without template for matching incoming
    * ACL message 
    */
   public SemanticCapabilities() {
       this(null);
   } // End of SemanticCapabilities/1
   
   /*********************************************************************/
   /**                         METHODS                             **/
   /*********************************************************************/
   /**
    * Calls all the setupXXX methods to initialise the capabilities.
    * @param agent the owner of this capabilities 
    */
   public void install(SemanticAgent agent) {
       //
       // Set the agent on which to install these capabilities
       myAgent = agent;
       agentName = Tools.AID2Term(myAgent.getAgent().getAID()); 
       
       //
       // Setup all the customization for this agent
       setupStandardCustomization();
       setupKbase();
       setupSemanticInterpretationPrinciples();
       setupPlanner();
       setupSemanticActions();
       setupContentParsers();
       //
       // Add the semantic behaviour to the semantic agent
       myAgent.getAgent().addBehaviour(myBehaviour);
   } // End of setup/0
   
   /**
    * Setup of the semantic actions. Creates the semantic action table and loads
    * the communicative actions. If the user overrides this method, it is 
    * highly recommended to call the <code>super()</code> method. Otherwise, 
    * the agent created will not have any communicative action if the user do not
    * loads them explicitly. 
    */
   protected void setupSemanticActions() {
       mySemanticActionTable = new SemanticActionTableImpl(this);
       mySemanticActionTable.loadTable();
   } // End of setupSemanticActions/0
   
   /**
    * Setup of the semantic interpretation principles. 
    * Creates the semantic interpretation principle table and loads
    * the principles. If the user overrides this method, it is 
    * highly recommended to call the <code>super()</code> method. Otherwise, 
    * the agent created will not have any principle if the user do not
    * loads them explicitly.      */
   protected void setupSemanticInterpretationPrinciples() {
       mySemanticInterpretationTable = new SemanticInterpretationPrincipleTableImpl();
       mySemanticInterpretationTable.loadTable(this);
   } // End of setupSemanticInterpretationPrinciples/0
   
   /**
    * Setup of the knowledge base. By default, a <code>KBaseFilterImpl</code>.
    */
   protected void setupKbase() {
       myKBase = new FilterKBaseImpl(myAgent);
   } // End of setupKbase/0
   
   /**
    * Setup of the planner. By default, sets the planner at <code>null</code>. 
    */
   protected void setupPlanner() {
       myPlanner = null;
   }// End of setupPlanner/0
   
   /**
    * Setup of the standard customization. By default, sets the customization
    * with an instance of <code>StandardCustomizationAdapter</code>.
    */
   protected void setupStandardCustomization() {
       myStandardCustomization = new StandardCustomizationAdapter();
   } // End of setupStandardCustomization/0
   
   /**
    * Extends this method to accept manager other content languages than SL
    */
   protected void setupContentParsers() {
       ContentParser cp = new DefaultContentParser();
       myContentParsers.put(cp.getLanguage(), cp);
   } // End of setupContentParsers/0
   
   /**
    * Returns the semantic interpreter behaviour associated to the agent
    * @return the semantic interpreter behaviour associated to the agent
    */
   public SemanticInterpreterBehaviour getSemanticInterpreterBehaviour() {
       return myBehaviour;
   } // End of getSemanticInterpreterBehaviour/0
   
   /**
    * Returns the semantic agent
    * @return the semantic agent.
    */
   public SemanticAgent getAgent() {
       return myAgent;
   } // End of getAgent/0
   
   /**
    * Returns the SL representation of this agent
    * @return the agentName.
    */
   public Term getAgentName() {
       return agentName;
   } // End of getAgentName/0
   
   /**
    * Returns the knowledge base of this agent.
    * @return the knowledge base.
    */
   public KBase getMyKBase() {
       return myKBase;
   } // End of getMyKBase/0
   
   /**
    * Returns the planner of this agent. Can return <code>null</code>.
    * @return the planner.
    */
   public Planner getMyPlanner() {
       return myPlanner;
   } // End of getMyPlanner/0
   
   /**
    * Returns the content manager which handle the given language
    * @param language the language handle by the content manager to be returned
    * @return the content manager which handle the given language or null if there is no such a content manager
    */
   public ContentParser getContentParser(String language) {
       return (ContentParser)myContentParsers.get(language == null ? "fipa-sl" : language);
   } // End of getContentParser/1
   
   /**
    * Sets the planner.
    * @param myPlanner The myPlanner to set.
    */
   public void setMyPlanner(Planner myPlanner) {
       this.myPlanner = myPlanner;
   } // End of setMyPlanner/1
   
   /**
    * Returns the mySemanticActionTable.
    * @return the mySemanticActionTable.
    */
   public SemanticActionTable getMySemanticActionTable() {
       return mySemanticActionTable;
   } // End of getMySemanticActionTable/0
   
   /**
    * Sets the semantic action table.
    * @param mySemanticActionTable The Semantic Action Table to set.
    */
   public void setMySemanticActionTable(
           SemanticActionTable mySemanticActionTable) {
       this.mySemanticActionTable = mySemanticActionTable;
   } // End of setMySemanticActionTable/1
   
   /**
    * Returns the semantic interpretation table.
    * @return Returns the Semantic interpretation Table.
    */
   public SemanticInterpretationPrincipleTable getMySemanticInterpretationTable() {
       return mySemanticInterpretationTable;
   } // End of getMySemanticInterpretationTable/0
   
   /**
    * Sets the semantic interpretation table.
    * @param mySemanticInterpretationTable The mySemanticInterpretationTable to set.
    */
   public void setMySemanticInterpretationTable(
           SemanticInterpretationPrincipleTable mySemanticInterpretationTable) {
       this.mySemanticInterpretationTable = mySemanticInterpretationTable;
   } // End of setMySemanticInterpretationTable/1
   
   /**
    * Returns the standard customisation
    * @return the StandardCustomization.
    */
   public StandardCustomization getMyStandardCustomization() {
       return myStandardCustomization;
   } // End of getMyStandardCustomization/0
   
   /**
    * Sets the standard customisation
    * @param myStandardCustomization The Standard Customization to set.
    */
   public void setMyStandardCustomization(StandardCustomization myStandardCustomization) {
       this.myStandardCustomization = myStandardCustomization;
   } // End of setMyStandardCustomization/1
   
   /**
    * Sets the knowledge base.
    * @param myKBase The Knowledge base to set.
    */
   public void setMyKBase(KBase myKBase) {
       this.myKBase = myKBase;
   } // End of setMyKBase/1
   
   /*********************************************************************/
   /**                     CONVENIENT METHODS                          **/
   /*********************************************************************/
   
   /**
    * Sends a communicative action.
    * @param action the action to send
    */
   public void sendCommunicativeAction(CommunicativeAction action) {
       try {
           myAgent.getAgent().send(action.toAclMessage());
       } catch (UnparseContentException e) {
           e.printStackTrace();
       }
   } // End of sendCommunicativeAction/1
   
   /**
    * Prototype of AcceptProposal action
    */
   static AcceptProposal ACCEPT_PROPOSAL_PROTOTYPE = null;
   /**
    * Creates a communicative action: AcceptProposal. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param condition a formula (second element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action AcceptProposal
    */

    public CommunicativeAction createAcceptProposal(ActionExpression action, Formula condition, Term receiver) {
        return createAcceptProposal(action, condition, new Term[] {receiver});  
    }
   /**
    * Creates a communicative action: AcceptProposal. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param condition a formula (second element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action AcceptProposal
    */
    public CommunicativeAction createAcceptProposal(ActionExpression action, Formula condition, Term[] receivers) {
       if ( ACCEPT_PROPOSAL_PROTOTYPE == null ) {
           ACCEPT_PROPOSAL_PROTOTYPE = new AcceptProposal(getMySemanticActionTable());
       }
       try {
           Content content = new ContentNode();
           content.setContentElements(2);
           content.setContentElement(0, action);
           content.setContentElement(1, condition);            
           return (CommunicativeAction)ACCEPT_PROPOSAL_PROTOTYPE.newAction(getAgentName(), 
                   new ListOfTerm(receivers),
                   content, null);
       }
       catch(SemanticInterpretationException sie) {
           sie.printStackTrace();
       }
       return null;
   } // End of createAcceptProposal/3
   
   /**
    * Prototype of Agree action
    */
   static Agree AGREE_PROTOTYPE = null;
   /**
    * Creates a communicative action: Agree. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param condition a formula (second element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action Agree
    */

    public CommunicativeAction createAgree(ActionExpression action, Formula condition, Term receiver) {
        return createAgree(action, condition, new Term[] {receiver});
    }
   /**
    * Creates a communicative action: Agree. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param condition a formula (second element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action Agree
    */  
    public CommunicativeAction createAgree(ActionExpression action, Formula condition, Term[] receivers) {  
       if ( AGREE_PROTOTYPE == null ) {
           AGREE_PROTOTYPE = new Agree(getMySemanticActionTable());
       }
       try {
           Content content = new ContentNode();
           content.setContentElements(2);
           content.setContentElement(0, action);
           content.setContentElement(1, condition);            
           return (CommunicativeAction)AGREE_PROTOTYPE.newAction(getAgentName(), 
                   new ListOfTerm(receivers),
                   content, null);
       }
       catch(SemanticInterpretationException sie) {
           sie.printStackTrace();
       }
       return null;
   } // End of createAgree/3
   
   /**
    * Prototype of Cancel action
    */
   static Cancel CANCEL_PROTOTYPE = null;
   
   /**
    * Creates a communicative action: Cancel. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (element of the content of this 
    * kind of message)
    * @param receiver a receiver
    * @return a communicative action Cancel
    */
    public CommunicativeAction createCancel(ActionExpression action, Term receiver) {
        return createCancel(action, new Term[] {receiver}); 
    }
   /**
    * Creates a communicative action: Cancel. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (element of the content of this 
    * kind of message)
    * @param receivers list of receivers
    * @return a communicative action Cancel
    */  
    public CommunicativeAction createCancel(ActionExpression action, Term[] receivers) {
       if ( CANCEL_PROTOTYPE == null ) {
           CANCEL_PROTOTYPE = new Cancel(getMySemanticActionTable());
       }
       try {
           Content content = new ContentNode();
           content.setContentElements(1);
           content.setContentElement(0, action);
           return (CommunicativeAction)CANCEL_PROTOTYPE.newAction(getAgentName(), 
                   new ListOfTerm(receivers),
                   content, null);
       }
       catch(SemanticInterpretationException sie) {
           sie.printStackTrace();
       }
       return null;
   } // End of createCancel/2
   
   
   
   /**
    * Prototype of CFP action
    */
   static CallForProposal CFP_PROTOTYPE = null;
   
   /**
    * Creates a communicative action: CFP. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param ire an Identifying Expression (second element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action CFP
    */
    public CommunicativeAction createCFP(ActionExpression action, IdentifyingExpression ire, Term receiver) {
        return createCFP(action, ire, new Term[] {receiver} );  
    }
   /**
    * Creates a communicative action: CFP. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param ire an Identifying Expression (second element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action CFP
    */  
    public CommunicativeAction createCFP(ActionExpression action, IdentifyingExpression ire, Term[] receivers) {
       if ( CFP_PROTOTYPE == null ) {
           CFP_PROTOTYPE = new CallForProposal(getMySemanticActionTable());
       }
       try {
           Content cfpContent = new ContentNode();
           cfpContent.setContentElements(2);
           cfpContent.setContentElement(0, action);
           cfpContent.setContentElement(1, ire);            
           return (CommunicativeAction)CFP_PROTOTYPE.newAction(getAgentName(), 
                   new ListOfTerm(receivers),
                   cfpContent, null);
       }
       catch(SemanticInterpretationException sie) {
           sie.printStackTrace();
       }
       return null;
   } // End of createCFP/3
   
   /**
    * Prototype of Confirm action
    */
   static Confirm CONFIRM_PROTOTYPE = null;
   
   /**
    * Creates a communicative action: Confirm. This method should be use 
    * only to send this kind of ACL Message.
    * @param formula a formula (element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action Confirm
    */
    public CommunicativeAction createConfirm(Formula formula, Term receiver) {
        return createConfirm(formula, new Term[] {receiver});   
    }
   /**
    * Creates a communicative action: Confirm. This method should be use 
    * only to send this kind of ACL Message.
    * @param formula a formula (element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action Confirm
    */  
    public CommunicativeAction createConfirm(Formula formula, Term[] receivers) {
       if ( CONFIRM_PROTOTYPE == null ) {
           CONFIRM_PROTOTYPE = new Confirm(getMySemanticActionTable());
       }
       try {
           Content content = new ContentNode();
           content.setContentElements(1);
           content.setContentElement(0, formula);
           return (CommunicativeAction)CONFIRM_PROTOTYPE.newAction(getAgentName(), 
                   new ListOfTerm(receivers),
                   content, null);
       }
       catch(SemanticInterpretationException sie) {
           sie.printStackTrace();
       }
       return null;
   } // End of createConfirm/2
   
   /**
    * Prototype of Disconfirm action
    */
   static Disconfirm DISCONFIRM_PROTOTYPE = null;
   
   /**
    * Creates a communicative action: Disconfirm. This method should be use 
    * only to send this kind of ACL Message.
    * @param formula a formula (element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action Disconfirm
    */
    public CommunicativeAction createDisconfirm(Formula formula, Term receiver) {
        return createDisconfirm(formula, new Term[] {receiver});
    }
   /**
    * Creates a communicative action: Disconfirm. This method should be use 
    * only to send this kind of ACL Message.
    * @param formula a formula (element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action Disconfirm
    */  
    public CommunicativeAction createDisconfirm(Formula formula, Term[] receivers) {
        if ( DISCONFIRM_PROTOTYPE == null ) {
            DISCONFIRM_PROTOTYPE = new Disconfirm(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(1);
            content.setContentElement(0, formula);
            return (CommunicativeAction)DISCONFIRM_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createDisconfirm/2
    
    /**
     * Prototype of Failure action
     */
    static Failure FAILURE_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: Failure. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param formula a formula (second element of the content of this kind of
    * message)
    * @param receiver a receievr
    * @return a communicative action Failure
     */
     public CommunicativeAction createFailure(ActionExpression action, Formula formula, Term receiver) {
        return createFailure(action, formula, new Term[] {receiver});
     }
    /**
    * Creates a communicative action: Failure. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param formula a formula (second element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action Failure
     */  
     public CommunicativeAction createFailure(ActionExpression action, Formula formula, Term[] receivers) {
        if ( FAILURE_PROTOTYPE == null ) {
            FAILURE_PROTOTYPE = new Failure(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(2);
            content.setContentElement(0, action);
            content.setContentElement(1, formula);
            return (CommunicativeAction)FAILURE_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createFailure/3
    
    /**
     * Prototype of Inform action
     */
    static Inform INFORM_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: Inform. This method should be use 
    * only to send this kind of ACL Message.
    * @param formula a formula (element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action CFP
     */
    public CommunicativeAction createInform(Formula formula, Term receiver) {
        return createInform(formula, new Term[] {receiver});    
    }
    /**
    * Creates a communicative action: Inform. This method should be use 
    * only to send this kind of ACL Message.
    * @param formula a formula (element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action CFP
     */ 
    public CommunicativeAction createInform(Formula formula, Term[] receivers) {
        if ( INFORM_PROTOTYPE == null ) {
            INFORM_PROTOTYPE = new Inform(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(1);
            content.setContentElement(0, formula);
            return (CommunicativeAction)INFORM_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createInform/2
    
    /**
     * Prototype of NotUnderstood action
     */
    static NotUnderstood NOT_UNDERSTOOD_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: NotUnderstood. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param reason a formula (second element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action NotUnderstood
     */
    public CommunicativeAction createNotUnderstood(ActionExpression action, Formula reason, Term receiver) {
        return createNotUnderstood(action, reason, new Term[] {receiver});
    }
    /**
    * Creates a communicative action: NotUnderstood. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param reason a formula (second element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action NotUnderstood
     */ 
    public CommunicativeAction createNotUnderstood(ActionExpression action, Formula reason, Term[] receivers) {
        if ( NOT_UNDERSTOOD_PROTOTYPE == null ) {
            NOT_UNDERSTOOD_PROTOTYPE = new NotUnderstood(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(2);
            content.setContentElement(0, action);
            content.setContentElement(1, reason);
            return (CommunicativeAction)NOT_UNDERSTOOD_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createNotUnderstood/3
    
    /**
     * Prototype of Propose action
     */
    static Propose PROPOSE_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: Propose. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param condition a formula (second element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action Propose
     */
    public CommunicativeAction createPropose(ActionExpression action, Formula condition, Term receiver) {
        return createPropose(action, condition, new Term[] {receiver});
    }
    /**
    * Creates a communicative action: Propose. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param condition a formula (second element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action Propose
     */ 
    public CommunicativeAction createPropose(ActionExpression action, Formula condition, Term[] receivers) {
        if ( PROPOSE_PROTOTYPE == null ) {
            PROPOSE_PROTOTYPE = new Propose(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(2);
            content.setContentElement(0, action);
            content.setContentElement(1, condition);
            return (CommunicativeAction)PROPOSE_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createPropose/3
    
    /**
     * Prototype of QueryIf action
     */
    static QueryIf QUERYIF_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: QueryIf. This method should be use 
    * only to send this kind of ACL Message.
    * @param formula a formula (element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action QueryIf
     */
    public CommunicativeAction createQueryIf(Formula formula, Term receiver) {
        return createQueryIf( formula, new Term[] {receiver});
    }
    /**
    * Creates a communicative action: QueryIf. This method should be use 
    * only to send this kind of ACL Message.
    * @param formula a formula (element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action QueryIf
     */
    public CommunicativeAction createQueryIf(Formula formula, Term[] receivers) {
        if ( QUERYIF_PROTOTYPE == null ) {
            QUERYIF_PROTOTYPE = new QueryIf(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(1);
            content.setContentElement(0, formula);
            return (CommunicativeAction)QUERYIF_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createQueryIf/2
    
    /**
     * Prototype of QueryRef action
     */
    static QueryRef QUERYREF_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: QueryRef. This method should be use 
    * only to send this kind of ACL Message.
    * @param ire an Identifying Expression (element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action QueryRef
     */
    public CommunicativeAction createQueryRef(IdentifyingExpression ire, Term receiver) {
        return createQueryRef( ire, new Term[] {receiver});
    }
    /**
    * Creates a communicative action: QueryRef. This method should be use 
    * only to send this kind of ACL Message.
    * @param ire an Identifying Expression (element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action QueryRef
     */ 
    public CommunicativeAction createQueryRef(IdentifyingExpression ire, Term[] receivers) {
        if ( QUERYREF_PROTOTYPE == null ) {
            QUERYREF_PROTOTYPE = new QueryRef(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(1);
            content.setContentElement(0, ire);
            return (CommunicativeAction)QUERYREF_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createQueryRef/2
    
    /**
     * Prototype of Refuse action
     */
    static Refuse REFUSE_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: Refuse. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param reason a formula (second element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action Refuse
     */
    public CommunicativeAction createRefuse(ActionExpression action, Formula reason, Term receiver) {
        return createRefuse(action, reason, new Term[] {receiver});
    }
    /**
    * Creates a communicative action: Refuse. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param reason a formula (second element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action Refuse
     */ 
    public CommunicativeAction createRefuse(ActionExpression action, Formula reason, Term[] receivers) {
        if ( REFUSE_PROTOTYPE == null ) {
            REFUSE_PROTOTYPE = new Refuse(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(2);
            content.setContentElement(0, action);
            content.setContentElement(1, reason);
            return (CommunicativeAction)REFUSE_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createRefuse/2
    
    /**
     * Prototype of RejectProposal action
     */
    static RejectProposal REJECT_PROPOSAL_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: RejectProposal. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param condition a formula (second element of the content of this kind of
    * message)
    * @param reason a formula (third element of the content of this kind of
    * message)
    * @param receiver a receiver
    * @return a communicative action RejectProposal
     */
    public CommunicativeAction createRejectProposal(ActionExpression action, Formula condition, Formula reason, Term receiver) {
        return createRejectProposal(action, condition, reason, new Term[] {receiver});
    }
    /**
    * Creates a communicative action: RejectProposal. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (first element of the content of this 
    * kind of message)
    * @param condition a formula (second element of the content of this kind of
    * message)
    * @param reason a formula (third element of the content of this kind of
    * message)
    * @param receivers list of receivers
    * @return a communicative action RejectProposal
     */ 
    public CommunicativeAction createRejectProposal(ActionExpression action, Formula condition, Formula reason, Term[] receivers) {
        if ( REJECT_PROPOSAL_PROTOTYPE == null ) {
            REJECT_PROPOSAL_PROTOTYPE = new RejectProposal(getMySemanticActionTable());
        }
        try {
            Content rejectProposalContent = new ContentNode();
            rejectProposalContent.setContentElements(3);
            rejectProposalContent.setContentElement(0, action);
            rejectProposalContent.setContentElement(1, condition);
            rejectProposalContent.setContentElement(2, reason);
            return (CommunicativeAction)REJECT_PROPOSAL_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    rejectProposalContent,
                    null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createRejectProposal
    
    /**
     * Prototype of Request action
     */
    static Request REQUEST_PROTOTYPE = null;
    
    /**
    * Creates a communicative action: Request. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (element of the content of this 
    * kind of message)
    * @param receiver a receiver
    * @return a communicative action Request
     */
    public CommunicativeAction createRequest(ActionExpression action, Term receiver) {
        return createRequest(action, new Term[] {receiver});    
    }   
    /**
    * Creates a communicative action: Request. This method should be use 
    * only to send this kind of ACL Message.
    * @param action an Action Expression (element of the content of this 
    * kind of message)
    * @param receivers list of receivers
    * @return a communicative action Request
     */ 
    public CommunicativeAction createRequest(ActionExpression action, Term[] receivers) {
        if ( REQUEST_PROTOTYPE == null ) {
            REQUEST_PROTOTYPE = new Request(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(1);
            content.setContentElement(0, action);
            return (CommunicativeAction)REQUEST_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createRequest/2
    
    /**
     * Prototype of RequestWhen action
     */
    static RequestWhen REQUEST_WHEN_PROTOTYPE = null;
    
    /**
     * Creates a communicative action: RequestWhen. This method should be use 
     * only to send this kind of ACL Message.
     * @param action an Action Expression (first element of the content of this 
     * kind of message)
     * @param condition a formula (second element of the content of this kind of
     * message)
     * @param receiver a receiver
     * @return a communicative action RequestWhen
     */
    public CommunicativeAction createRequestWhen(ActionExpression action, Formula condition, Term receiver) {
        return createRequestWhen(action, condition, new Term[] {receiver});
    }
    /**
     * Creates a communicative action: RequestWhen. This method should be use 
     * only to send this kind of ACL Message.
     * @param action an Action Expression (first element of the content of this 
     * kind of message)
     * @param condition a formula (second element of the content of this kind of
     * message)
     * @param receivers list of receivers
     * @return a communicative action RequestWhen
     */ 
    public CommunicativeAction createRequestWhen(ActionExpression action, Formula condition, Term[] receivers) {
        if ( REQUEST_WHEN_PROTOTYPE == null ) {
            REQUEST_WHEN_PROTOTYPE = new RequestWhen(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(2);
            content.setContentElement(0, action);
            content.setContentElement(1, condition);
            return (CommunicativeAction)REQUEST_WHEN_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createRequestWhen/2
    
    /**
     * Prototype of RequestWhenever action
     */
    static RequestWhenever REQUEST_WHENEVER_PROTOTYPE = null;
    
    /**
     * Creates a communicative action: RequestWhenever. This method should be use 
     * only to send this kind of ACL Message.
     * @param action an Action Expression (first element of the content of this 
     * kind of message)
     * @param condition a formula (second element of the content of this kind of
     * message)
     * @param receiver a receiver
     * @return a communicative action RequestWhenever
     */
    public CommunicativeAction createRequestWhenever(ActionExpression action, Formula condition, Term receiver) {
        return createRequestWhenever(action, condition, new Term[] {receiver});
    }
    /**
     * Creates a communicative action: RequestWhenever. This method should be use 
     * only to send this kind of ACL Message.
     * @param action an Action Expression (first element of the content of this 
     * kind of message)
     * @param condition a formula (second element of the content of this kind of
     * message)
     * @param receivers list of receivers
     * @return a communicative action RequestWhenever
     */ 
    public CommunicativeAction createRequestWhenever(ActionExpression action, Formula condition, Term[] receivers) {
        if ( REQUEST_WHENEVER_PROTOTYPE == null ) {
            REQUEST_WHENEVER_PROTOTYPE = new RequestWhenever(getMySemanticActionTable());
        }
        try {
            Content content = new ContentNode();
            content.setContentElements(2);
            content.setContentElement(0, action);
            content.setContentElement(1, condition);
            return (CommunicativeAction)REQUEST_WHENEVER_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    content, null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    } // End of createPropose/2
    
    
    /**
     * Prototype of Subscribe action
     */
    static Subscribe SUBSCRIBE_PROTOTYPE = null;
    
    /**
     * Creates a communicative action: Subscribe. This method should be use 
     * only to send this kind of ACL Message.
     * @param ire an Identifying expression (element of the content of this kind of
     * message)
     * @param receiver a receiver
     * @return a communicative action Subscribe
     */
    public CommunicativeAction createSubscribe(IdentifyingExpression ire, Term receiver) {
        return createSubscribe(ire, new Term[] {receiver});
    }
    /**
     * Creates a communicative action: Subscribe. This method should be use 
     * only to send this kind of ACL Message.
     * @param ire an Identifying expression (element of the content of this kind of
     * message)
     * @param receivers list of receivers
     * @return a communicative action Subscribe
     */ 
    public CommunicativeAction createSubscribe(IdentifyingExpression ire, Term[] receivers) {
        if ( SUBSCRIBE_PROTOTYPE == null ) {
            SUBSCRIBE_PROTOTYPE = new Subscribe(getMySemanticActionTable());
        }
        try {
            Content subscribeContent = new ContentNode();
            subscribeContent.setContentElements(1);
            subscribeContent.setContentElement(0, ire);
            return (CommunicativeAction)SUBSCRIBE_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    subscribeContent,
                    null);
        }
        catch(SemanticInterpretationException sie) {
            sie.printStackTrace();
        }
        return null;
    }
    /**
     * Prototype of Unsubscribe action (Inform)
     */
    static Inform UNSUBSCRIBE_PROTOTYPE = null;
    /**
     * Content of an Unsubscribe action
     */
    static final Content UNSUBSCRIBE_CONTENT1 = SLPatternManip.fromContent("((not (I ??agent (done (action ??receiver (INFORM-REF :sender ??receiver :receiver (set ??agent) :content ??ire))))))");
    
    /**
     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
     * only to send this kind of ACL Message.
     * @param ire an Identifying Expression (element of the content of this kind of
     * message)
     * @param receiver a receiver
     * @return a communicative action Unsubscribe (Inform)
     */
    public CommunicativeAction createUnsubscribe(IdentifyingExpression ire, Term receiver) {
        return createUnsubscribe(ire, new Term[] {receiver});   
    }
    /**
     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
     * only to send this kind of ACL Message.
     * @param ire an Identifying Expression (element of the content of this kind of
     * message)
     * @param receivers list of receivers
     * @return a communicative action Unsubscribe (Inform)
     */ 
    public CommunicativeAction createUnsubscribe(IdentifyingExpression ire, Term[] receivers) {
        if ( UNSUBSCRIBE_PROTOTYPE == null ) {
            UNSUBSCRIBE_PROTOTYPE = new Inform(getMySemanticActionTable());
        }
        try {
            return ((CommunicativeAction)UNSUBSCRIBE_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    (Content)SLPatternManip.instantiate(UNSUBSCRIBE_CONTENT1, 
                            "ire", new StringConstantNode("("+ire+")"),
                            "agent", getAgentName(),
                            "receiver", receivers[0]),
                            null));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of createUnsubscribe/2
    
    /**
     * Content of an Unsubscribe action
     */
    static final Content UNSUBSCRIBE_CONTENT2 = SLPatternManip.fromContent("((not (I ??agent (done ??action))))");
    
    /**
     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
     * only to send this kind of ACL Message.
     * @param action an Action Expression (element of the content of this kind of
     * message)
     * @param receiver a receiver
     * @return a communicative action Unsubscribe (Inform)
     */
    public CommunicativeAction createUnsubscribe(ActionExpression action, Term receiver) {
        return createUnsubscribe(action, new Term[] {receiver});    
    }   
    /**
     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
     * only to send this kind of ACL Message.
     * @param action an Action Expression (element of the content of this kind of
     * message)
     * @param receivers list of receivers
     * @return a communicative action Unsubscribe (Inform)
     */ 
    public CommunicativeAction createUnsubscribe(ActionExpression action, Term[] receivers) {
        if ( UNSUBSCRIBE_PROTOTYPE == null ) {
            UNSUBSCRIBE_PROTOTYPE = new Inform(getMySemanticActionTable());
        }
        try {
            return (CommunicativeAction)UNSUBSCRIBE_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    (Content)SLPatternManip.instantiate(UNSUBSCRIBE_CONTENT2, 
                            "agent", getAgentName(),
                            "action", action), 
                            null);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of createUnsubscribe/2
    
    /**
     * Content of an Unsubscribe action
     */
    static final Content UNSUBSCRIBE_CONTENT3 = SLPatternManip.fromContent("((or " +
            "     (forall ?e (not (done ?e (not (B ??receiver ??property)))))" +
            "     (or   (not (B ??receiver ??property))" +
    "(not (I ??agent (done ??action))))))");
    
    /**
     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
     * only to send this kind of ACL Message.
     * @param action an Action Expression (first element of the content of this kind of
     * message)
     * @param property a Formula (second element of the content of this kind of
     * message)
     * @param receiver a receiver
     * @return a communicative action Unsubscribe (Inform)
     */
    public CommunicativeAction createUnsubscribe(ActionExpression action, Formula property, Term receiver) {
        return createUnsubscribe(action, property, new Term[] {receiver});  
    }
    /**
     * Creates a communicative action: Unsubscribe (Inform). This method should be use 
     * only to send this kind of ACL Message.
     * @param action an Action Expression (first element of the content of this kind of
     * message)
     * @param property a Formula (second element of the content of this kind of
     * message)
     * @param receivers list of receivers
     * @return a communicative action Unsubscribe (Inform)
     */ 
    public CommunicativeAction createUnsubscribe(ActionExpression action, Formula property, Term[] receivers) {
        if ( UNSUBSCRIBE_PROTOTYPE == null ) {
            UNSUBSCRIBE_PROTOTYPE = new Inform(getMySemanticActionTable());
        }
        try {
            return (CommunicativeAction)UNSUBSCRIBE_PROTOTYPE.newAction(getAgentName(), 
                    new ListOfTerm(receivers),
                    (Content)SLPatternManip.instantiate(UNSUBSCRIBE_CONTENT3, 
                            "agent", getAgentName(),
                            "property", property,
                            "action", action,
                            "receiver", receivers[0]), null);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    } // End of createUnsubscribe/3
    
} // End of class SemanticCapabilities
