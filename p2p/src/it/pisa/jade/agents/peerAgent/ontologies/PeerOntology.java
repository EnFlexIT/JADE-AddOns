/*****************************************************************JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent systems in compliance with the FIPA specifications.Copyright (C) 2000 CSELT S.p.A. GNU Lesser General Public LicenseThis library is free software; you can redistribute it and/ormodify it under the terms of the GNU Lesser General PublicLicense as published by the Free Software Foundation, version 2.1 of the License. This library is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNULesser General Public License for more details.You should have received a copy of the GNU Lesser General PublicLicense along with this library; if not, write to theFree Software Foundation, Inc., 59 Temple Place - Suite 330,Boston, MA  02111-1307, USA.*****************************************************************/package it.pisa.jade.agents.peerAgent.ontologies;import jade.content.onto.BasicOntology;import jade.content.onto.Ontology;import jade.content.onto.OntologyException;import jade.content.schema.AgentActionSchema;import jade.content.schema.ConceptSchema;import jade.content.schema.ObjectSchema;import jade.content.schema.PrimitiveSchema;/** *  * @author Fabrizio Marozzo * @author Domenico Trimboli * */@SuppressWarnings("serial")public class PeerOntology extends Ontology implements PeerVocabulary {	// ----------> The name identifying this ontology	public static final String ONTOLOGY_NAME = "Peer-Ontology";	// ----------> The singleton instance of this ontology	private static Ontology instance = new PeerOntology();	// ----------> Method to access the singleton ontology object	public static Ontology getInstance() {		return instance;	}	// Private constructor	private PeerOntology() {		super(ONTOLOGY_NAME, BasicOntology.getInstance());		try {			// ------- Add Concepts			// Found			ConceptSchema cs = new ConceptSchema(FOUND);			add(cs, Found.class);			cs.add(FOUND_SEARCH_KEY,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			cs.add(FOUND_NAME,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			cs.add(FOUND_URL,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			cs.add(FOUND_AGENT,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			cs.add(FOUND_EXTENSION,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			cs.add(FOUND_TYPE,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.OPTIONAL);			cs.add(FOUND_SUMMARY,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.OPTIONAL);			// Refuse Choose			add(cs = new ConceptSchema(REFUSECHOOSE), RefuseChoose.class);			cs.add(REFUSECHOOSE_SEARCH_KEY,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			cs.add(REFUSECHOOSE_URL,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			// AGREE Choose			add(cs = new ConceptSchema(AGREECHOOSE), AgreeChoose.class);			cs.add(AGREECHOOSE_SEARCH_KEY,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			cs.add(AGREECHOOSE_URL,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			// NoFound Message			add(cs = new ConceptSchema(NOFOUND), NoFound.class);			cs.add(NOFOUND_SEARCH_KEY,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			// ------- Add AgentActions			// Look For			AgentActionSchema as = new AgentActionSchema(LOOKFOR);			add(as, LookFor.class);			as.add(LOOKFOR_SEARCH_KEY,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			as.add(LOOKFOR_SEARCH_STRING,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			as.add(LOOKFOR_EXTENSION,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.OPTIONAL);			as.add(LOOKFOR_TYPE,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.OPTIONAL);			// Choose			add(as = new AgentActionSchema(CHOOSE), Choose.class);			as.add(CHOOSE_SEARCH_KEY,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			as.add(CHOOSE_URL,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			as.add(CHOOSE_ADDRESS_IP,					(PrimitiveSchema) getSchema(BasicOntology.STRING),					ObjectSchema.MANDATORY);			as.add(CHOOSE_ADDRESS_PORT,					(PrimitiveSchema) getSchema(BasicOntology.INTEGER),					ObjectSchema.MANDATORY);		} catch (OntologyException oe) {			oe.printStackTrace();		}	}}