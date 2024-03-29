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

package examples.rdfcontent;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import jade.content.*;
import jade.content.abs.*;
import jade.content.onto.*;
import jade.content.lang.*;
import jade.util.Logger;
import it.unipr.aot.rdf.RDFCodec;

import examples.rdfcontent.ontology.*;

public class Receiver extends Agent {
    private ContentManager manager     = (ContentManager)getContentManager();
    private Codec          codec       = new RDFCodec();
    private Ontology   ontology    = PeopleOntology.getInstance();
    private FatherOf       proposition = null;
    
    //logging
    private static Logger logger = Logger.getMyLogger(Receiver.class.getName());

    class ReceiverBehaviour extends SimpleBehaviour {
	private boolean finished = false;

	public ReceiverBehaviour(Agent a) { super(a); }

	public boolean done() { return finished; }

	public void action() {
	    for(int c = 0; c < 2; c++) {
		try {
		    logger.log(Logger.INFO, "[" + getLocalName() + "] Waiting for a message...");

		    ACLMessage msg = blockingReceive();

		    if (msg!= null) {
			switch(msg.getPerformative()) {
			case ACLMessage.INFORM:
			    ContentElement p = manager.extractContent(msg);
			    if(p instanceof FatherOf) {
				proposition = (FatherOf)p;
				logger.log(Logger.INFO,"[" + getLocalName() + "] Receiver inform message: information stored.");
				break;
			    }
			case ACLMessage.QUERY_REF:
			    AbsContentElement abs = manager.extractAbsContent(msg);
			    if(abs instanceof AbsIRE) {
				AbsIRE ire = (AbsIRE)abs;

				ACLMessage reply = msg.createReply();
				reply.setPerformative(ACLMessage.INFORM);
				
				//AID sender = new AID("sender", false);

				//reply.setSender(getAID());
				//reply.addReceiver(sender);
				reply.setLanguage(codec.getName());
				reply.setOntology(ontology.getName());

				AbsConcept absFather = (AbsConcept)ontology.fromObject(proposition.getFather());

				AbsPredicate absEquals = new AbsPredicate(BasicOntology.EQUALS);
				absEquals.set(BasicOntology.EQUALS_LEFT, ire);
				absEquals.set(BasicOntology.EQUALS_RIGHT, absFather);

				manager.fillContent(msg, absEquals);

				send(reply);

				logger.log(Logger.INFO,"[" + getLocalName() + "] Received query-ref message: reply sent:");
				System.out.println(absEquals);
				break;
			    }
			default:
			    logger.log(Logger.INFO,"[" + getLocalName() + "] Malformed message.");
			}
		    }
		} catch(Exception e) { e.printStackTrace(); }
	    }
	    finished = true;
	}
    }

    protected void setup() {
	manager.registerLanguage(codec);
	manager.registerOntology(ontology);

	addBehaviour(new ReceiverBehaviour(this));
    }
}
