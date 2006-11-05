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
package it.pisa.jade.data.activePlatform;

import it.pisa.jade.data.activePlatform.ConstantRecordPlatform.AttributeRecordPlatform;
import it.pisa.jade.util.WrapperErrori;
import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ConceptSchema;
import jade.content.schema.PrimitiveSchema;

/**
 * @author Domenico Trimboli
 * @author Fabrizio Marozzo
 * 
 */
@SuppressWarnings("serial")
public class ActivePlatformOntology extends Ontology {

	// ----------> The name identifying this ontology
	public static final String ONTOLOGY_NAME = "ActivePlatform-Ontology";

	// ----------> The singleton instance of this ontology
	private static Ontology instance = new ActivePlatformOntology();

	// ----------> Method to access the singleton ontology object
	public static Ontology getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	public ActivePlatformOntology() {
		super(ONTOLOGY_NAME, BasicOntology.getInstance());
		try {
			//-------------add concept
			//RecordPlatform 
			ConceptSchema cs = new ConceptSchema(
					ConstantRecordPlatform.RECORDPLATFORM.getValue());
			add(cs, RecordPlatform.class);
			AttributeRecordPlatform[] values = ConstantRecordPlatform.RECORDPLATFORM.getAttribute();
			for (int i = 0; i < values.length; i++) {
				cs.add(values[i].getValue(),
						(PrimitiveSchema) getSchema(values[i].getSchema()),
						values[i].getType());
			}
			//-----------add action
			/*AgentActionSchema as = new AgentActionSchema(LOOKFOR);
	        
	         add(as, LookFor.class);
	         as.add(LOOKFOR_SEARCH_KEY, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
	         as.add(LOOKFOR_SEARCH_STRING, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.MANDATORY);
	         as.add(LOOKFOR_EXTENSION, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
	         as.add(LOOKFOR_TYPE, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);*/
	         
			
		} catch (OntologyException e) {
			WrapperErrori.wrap("", e);
		}

	}

}
