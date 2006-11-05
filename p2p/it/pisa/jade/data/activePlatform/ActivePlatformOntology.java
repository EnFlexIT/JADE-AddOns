/*
 * Created on 8-ott-2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
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
