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

package examples.xmlcontent;

import jade.core.*;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;

import jade.content.*;
import jade.content.abs.*;
import jade.content.onto.*;
import jade.content.lang.*;
import jade.content.lang.xml.*;


import examples.xmlcontent.ontology.*;

public class Receiver extends Agent {
    private ContentManager manager     = (ContentManager)getContentManager();
    private Codec          codec       = new XMLCodec();
        
    private Ontology   ontology    = PeopleOntology.getInstance();
    private FatherOf       proposition = null;

    class ReceiverBehaviour extends SimpleBehaviour {
	private boolean finished = false;

	public ReceiverBehaviour(Agent a) { super(a); }

	public boolean done() { return finished; }

	public void action() {
	    //for(int c = 0; c < 2; c++) {
		try {
		    System.out.println( "[" + getLocalName() + "] Waiting for a message...");

		    ACLMessage msg = blockingReceive();

		    if (msg!= null) {
			    ContentElement p = manager.extractContent(msg);
			    System.out.println("Received message  "+ msg.getContent());
			    if(p instanceof FatherOf) {
				proposition = (FatherOf)p;
				System.out.println("[" + getLocalName() + "] Receiver inform message: information stored.");
				//Estrae il contenuto del messaggio e lo stampa
				FatherOf fo =new FatherOf();
				fo.setFather(proposition.getFather());
				fo.setChildren(proposition.getChildren());
				System.out.println("FatherOf :name  "+(fo.getFather()).getName());
				System.out.println("FatherOf :address  "+((fo.getFather()).getAddress()).getCity() +" "+((fo.getFather()).getAddress()).getStreet() +" "+((fo.getFather()).getAddress()).getNumber());
				for (int i=0;i<fo.getChildren().size();i++){
					System.out.println("Children :name    "+ ((Person)(fo.getChildren().get(i))).getName());
					System.out.println("Children :address "+ (((Person)(fo.getChildren().get(i))).getAddress()).getCity());					
			}
				
				}
			else{
				System.out.println("msg null");
				}
		    }
		} catch(Exception e) { 
			System.out.println("Error in extracting message");
			e.printStackTrace(); }
	   // }
	    finished = true;
	}
    }

    protected void setup() {
	manager.registerLanguage(codec);
	manager.registerOntology(ontology);

	addBehaviour(new ReceiverBehaviour(this));
    }
}
