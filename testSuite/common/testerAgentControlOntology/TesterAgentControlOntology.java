/**
 * ***************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop
 * multi-agent systems in compliance with the FIPA specifications.
 * Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation,
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA  02111-1307, USA.
 * **************************************************************
 */
package test.common.testerAgentControlOntology;

import jade.content.onto.*;
import jade.content.schema.*;

/**
 * Ontology containing concepts and predicates used to control the 
 * execution of a TesterAgent. This ontology is generally used by 
 * the JADE test suite agent.
 * @author Giovanni Caire - TILAB
 */
public class TesterAgentControlOntology extends Ontology {
	// NAME
  public static final String ONTOLOGY_NAME = "Tester-agent-control-ontology";
	
	// VOCABULARY
  public static final String CONFIGURE = "CONFIGURE";
  public static final String CONFIGURE_DEBUG_MODE = "debug-mode";
  public static final String CONFIGURE_REMOTE_CONTROL_MODE = "remote-control-mode";
  public static final String CONFIGURE_REMOTE_CONTROLLER_NAME = "remote-controller-name";

  public static final String RESUME = "RESUME";
  
  public static final String TERMINATED = "TERMINATED";
  public static final String TERMINATED_ID = "id";

  // The singleton instance of this ontology
	private static Ontology theInstance = new TesterAgentControlOntology();
	
	public static Ontology getInstance() {
		return theInstance;
	}
	
  /**
   * Constructor
   */
  private TesterAgentControlOntology() {
  	super(ONTOLOGY_NAME, BasicOntology.getInstance(), new ReflectiveIntrospector());

    try {
    	add(new AgentActionSchema(CONFIGURE), Configure.class);
    	add(new AgentActionSchema(RESUME), Resume.class);
    	add(new PredicateSchema(TERMINATED), Terminated.class);
    	
    	AgentActionSchema as = (AgentActionSchema) getSchema(CONFIGURE);
    	as.add(CONFIGURE_DEBUG_MODE, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN), ObjectSchema.OPTIONAL);
    	as.add(CONFIGURE_REMOTE_CONTROL_MODE, (PrimitiveSchema) getSchema(BasicOntology.BOOLEAN), ObjectSchema.OPTIONAL);
    	as.add(CONFIGURE_REMOTE_CONTROLLER_NAME, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);

    	PredicateSchema ps = (PredicateSchema) getSchema(TERMINATED);
    	ps.add(TERMINATED_ID, (ConceptSchema) getSchema(BasicOntology.AID));
    } 
    catch (OntologyException oe) {
    	oe.printStackTrace();
    } 
	}

}
