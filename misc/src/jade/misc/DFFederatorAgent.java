/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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

package jade.misc;

import jade.core.Agent;
import jade.core.AID;
import jade.domain.FIPANames;
import jade.domain.DFGUIManagement.*;
import jade.content.ContentManager;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;
import jade.domain.JADEAgentManagement.ShowGui;
import jade.domain.JADEAgentManagement.*;	   

import java.io.FileInputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This agent uses a property file to request a subDF to federate with a rootDF. <br>
 * The first argument passed to this agent on the command line is the name
 * of a file with a list of properties in the form <br>
 * <code>
 SubDF1 = df@jade.cselt.it:1099/JADE , IOR:00002233 <br>
 RootDF = df@teschet.it:1099/JADE, http://teschet.it:8088/acc <br>
 SubDF2 = agentName, agentAddress <br>
 RootDF2 = agentName, agentAddress <br
   </code>
 * <br> The strings <code>SubDF</code> and <code>RootDF</code> MUST be used
 * as a prefix to identify couples of subDF (i.e. the DF that must be federated
 * with) and rootDF. <br>
 * The value for each key is in the form agentName, agentAddress separated
 * by a comma. <br>
 * Fhe file DFFederatorAgent.properties in the resources directory is
 * an example of a property file for this agent. <br>
 * A suggested command line to better understand this agent is the following:
 * <br><code>
   java jade.Boot -gui subDF:jade.domain.df r:examples.DFFederator.DFFederatorAgent(resouces\DFFederatorAgent.properties) </code>
 * 
 * @author Fabio Bellifemine - TILAB
 * @version $Date$ $Revision$
 * @see <a href="../../../../resources/DFFederatorAgent.properties">sample properties file </a>
 **/
public class DFFederatorAgent extends Agent {

    /** constructor **/
	
	 
    public DFFederatorAgent() {
	federate = new Federate();
	action = new Action();
	action.setAction(federate);
	request = new ACLMessage(ACLMessage.REQUEST);
	request.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);	
	request.setOntology(DFAppletOntology.NAME);
	
	
	// action ShowGui requested to df belonging to the federation
				  
	showGui = new ShowGui();
	actionShowGui = new Action();
	actionShowGui.setAction(showGui);
	requestShowGui = new ACLMessage(ACLMessage.REQUEST);
	requestShowGui.setLanguage(FIPANames.ContentLanguage.FIPA_SL0);
	requestShowGui.setOntology(JADEManagementOntology.NAME);
	// this vector contains the df that have already received a request
	// to show a gui
	aidDfVec = new Vector();
    }

    /* This is the prefix string that identifies the key of a property
     * that represent a sub DF. The postfix after this key is appended
     * also to RootDFKeyPrefix to get the key of the property that
     * represents the rootDF for this subDF.
     */
    private static String SubDFKeyPrefix = "SubDF";
    /* @see SubDFKeyPrefix **/
    private static String RootDFKeyPrefix = "RootDF";

    protected void setup() {
	// Register the DFAppletOntology with the content manager
	getContentManager().registerOntology(DFAppletOntology.getInstance());
	// Register the JADEManagementOntology with the content manager
	getContentManager().registerOntology(JADEManagementOntology.getInstance());
	// Register the SL0 content language
	getContentManager().registerLanguage(new SLCodec(0), FIPANames.ContentLanguage.FIPA_SL0);	

	// read the name of the file that contains the list of properties
	Object[] args = getArguments();
	String fileName = (String)(args[0]);
	// load the list of properties from the file
	Properties p = new Properties();
	try {
	    p.load(new FileInputStream(fileName));
	} catch (Exception e) {
	    System.err.println("Some problems in reading the list of properties for "+getLocalName());
	    e.printStackTrace();
	}
	// dump this list of properties on standard output
	//System.out.println("List of properties for "+getLocalName());
	//p.list(System.out);    

	for (Enumeration e=p.propertyNames(); e.hasMoreElements(); ) {
	    String key = (String)e.nextElement();
	    if (key.startsWith(SubDFKeyPrefix)) {
		String value = p.getProperty(key);
		AID childDF = createAID(value);
		String rootKey = createRootDFKey(key);
		value = p.getProperty(rootKey);
		AID parentDF = createAID(value);
		//System.out.println("FEDERATE " + childDF.toString() + "\n WITH " + parentDF.toString());
		federate(childDF, parentDF);
	    }
	}

	// task terminated, kill this agent
	//doDelete();
    }


    private Federate federate; 
    private ACLMessage request;
    private Action action;

    private void federate(AID childDF, AID parentDF) {
	showGui(childDF);
	showGui(parentDF);
	federate.setParentDF(parentDF);
	action.setActor(childDF);
	request.clearAllReceiver();
	request.addReceiver(childDF);
	try {
	    getContentManager().fillContent(request, action);
	    //System.out.println(request);
	    send(request);
	    System.out.println(getLocalName()+" sent to "+childDF+ "\n a request to federate with "+parentDF);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
	
	/**
	  * Send a request to perform action ShowGui to all the federated dfs
	  *
	  **/
	private jade.domain.JADEAgentManagement.ShowGui showGui;
	private ACLMessage requestShowGui;
	private Action actionShowGui;
	private Vector aidDfVec; 
	private void showGui(AID df) {
		// send a request only to a new df to showgui
		if (!aidDfVec.contains(df)) 
		{
			aidDfVec.addElement(df);
			actionShowGui.setActor(df);
			requestShowGui.clearAllReceiver();
			requestShowGui.addReceiver(df);
			try {
			    getContentManager().fillContent(requestShowGui, actionShowGui);
			    //System.out.println(request);
			    send(requestShowGui);
			    System.out.println(getLocalName() + " sent to " + df);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
    }


    /**
     * Parses the passed string containing an agent name and an agent address
     * separated by a comma and creates an AID for that agent.
     * @param value a String in the form <name>,<value> For instance
     * df@sharon.cselt.it:1099/JADE , IOR:000002233
     * @return the agent AID
     **/
    private AID createAID(String value) {
	AID aid;
	// before to comma is the AID.name, after the comma is the AID.address
	int ind = value.indexOf(',');
	if (ind < 0) {
	    // if no comma was found, then assume is a local agent
	    aid = new AID(value, (value.indexOf('@')<0 ? AID.ISLOCALNAME : AID.ISGUID));
	} else {
	    String aidName = value.substring(0,ind).trim();
	    String aidAddress = value.substring(ind+1).trim();
	    aid = new AID(aidName);
	    aid.addAddresses(aidAddress);
	}
	return aid;
    }

    /**
     * parses the key for a subDF and return the corresponding key for the rootDF
     **/
    private String createRootDFKey(String subDFKey) {
		return RootDFKeyPrefix + subDFKey.substring(SubDFKeyPrefix.length());
    }
}
