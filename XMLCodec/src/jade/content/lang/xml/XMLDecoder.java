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
package jade.content.lang.xml;

import jade.content.onto.*;
import jade.content.onto.basic.*;
import jade.content.abs.*;
import jade.content.schema.*;
import jade.content.schema.facets.TypedAggregateFacet;
import jade.content.lang.Codec;
import jade.content.lang.StringCodec;
import org.xml.sax.Attributes;
import jade.util.leap.ArrayList;
import jade.lang.acl.ISO8601;
import starlight.util.Base64;

/**
 * @author Filippo Quarta - TELECOM ITALIA LAB
 */

class XMLDecoder {

	Ontology ontology;
	boolean initialited = false;
	boolean ContentElementList = false;
	
	class StackElement {
		public String tag;
		public AbsObject term;
	}

	class SupportStack {
		ArrayList posComplex = new ArrayList();		
		ArrayList stack = new ArrayList();
				
		protected void clear() {
			stack.clear();
			posComplex.clear();
		}
		
		protected int size() {
			return stack.size();
		}
		
		protected void push(StackElement element) {
			if (stack.size()>0) {
				StackElement prev = getPreviousComplexElement(0);
				
				// Verify if the element that we want to push is
				// a child of a previous aggregate. In that 
				// put convetionally his tag equals to "*"
				if ((prev.term instanceof AbsAggregate) &&
					(prev.tag.equals(element.tag))) {
						element.tag="*";
				}
			}

			stack.add(element);
			
			// If the added element isn't a primitive, update the
			// references to complex elements in the stack
			if (!(element.term instanceof AbsPrimitive)) 
					posComplex.add(new Integer(stack.size()));
		}
			
		protected StackElement get(int i) {
			return (StackElement)stack.get(i);
		}
		
		protected int numberOfComplex() {
			return posComplex.size();
		}
		
		protected StackElement getPreviousComplexElement(int offset) {
			int temp = ((Integer)posComplex.get(numberOfComplex()-offset-1)).intValue();
			StackElement element = (StackElement)stack.get(temp-1);
			return  element;
		}
		
		protected StackElement pop() {
			StackElement top = null;
			
			if (stack.size()>0) {
				top = (StackElement)stack.get(stack.size()-1);
				stack.remove(stack.size()-1);
				
				if ((top.term instanceof AbsConcept) || 
				(top.term instanceof AbsPredicate)) 
					posComplex.remove(numberOfComplex()-1);
			}
			return top;
		}
			
	}
	
	AbsContentElement ce = null;
	SupportStack stack = new SupportStack();
			
	
	protected void setOntology(Ontology o) {
		ontology = o;
	}
	
	protected AbsContentElement getDecodedContent() {
		return ce;
	}
	
	protected void openTag(String qname, Attributes attr) throws OntologyException {
		
			ObjectSchema objectSchema = null;
			ObjectSchema objectSchema1 = null;

			// Initialize the decoder		
			if (qname.equals("CONTENT_ELEMENT_LIST")) {
					ContentElementList = true;
					initialited=true;
					stack.clear();
					addToStack("", attr, ContentElementListSchema.getBaseSchema());		
					return;
			}
			
			if (qname.equals("CONTENT_ELEMENT")) {
				qname = attr.getValue("type");
				if (initialited==false) {
					stack.clear();
					initialited=true;
					ContentElementList = false;
				}
			}
			
			// Manage the cast toward specific type. 
			objectSchema1 = getRelatedSchema(qname);
			
			String schemaName = attr.getValue("type");
			if (schemaName == null) {
				schemaName = qname;
			}
			
			// Identify the schema of the current tag
			objectSchema = getRelatedSchema(schemaName);
				
			// Manage the first occurence of an aggregate member
			if (objectSchema1 instanceof AggregateSchema) {
				addToStack(qname, attr, objectSchema1);		
			 	objectSchema = getRelatedSchema(schemaName);	
			 	if (objectSchema!=null) addToStack(qname, attr, objectSchema);		
			} else
				addToStack(qname, attr, objectSchema);		
			
	}
	
	protected void closeTag(String qname, String content) throws OntologyException {
	
		   boolean finalize = false;	
	
		   if (qname.equals("CONTENT_ELEMENT") && !ContentElementList) finalize = true;
		   
		   if (qname.equals("CONTENT_ELEMENT_LIST")) finalize=true;
		    	
		   do {
				StackElement top = stack.pop();

				if (stack.size()==0) {
					ce = (AbsContentElement)top.term;
					initialited=false;
				} else {
		  			if (top.term instanceof AbsPrimitive) 
						decodePrimitive(top, content);
					//try {
						StackElement prevComplex = stack.getPreviousComplexElement(0);
						if (prevComplex.term instanceof AbsAggregate) {
								((AbsAggregate)prevComplex.term).add((AbsTerm)top.term);
						} else if (prevComplex.term instanceof AbsContentElementList) {
							((AbsContentElementList)prevComplex.term).add((AbsContentElement)top.term);
						} else
							AbsHelper.setAttribute(prevComplex.term, top.tag, top.term);
						
					//} catch (Exception e) {
					//}
				}
			} while (finalize && stack.size()>0);
	}
	
	protected ObjectSchema getRelatedSchema(String qname) throws OntologyException {
		ObjectSchema objectSchema = null;
		boolean emptyAggregate = false;
		
		try {
			objectSchema = ontology.getSchema(qname);
		} catch (OntologyException e) {
		}	
		
		while (objectSchema==null && !emptyAggregate) {
				//try {				
					StackElement prevElement = stack.getPreviousComplexElement(0);
					ObjectSchema prevComplexSchema =  getConceptSchema(prevElement.term);
					if (prevComplexSchema instanceof AggregateSchema) {
						if (qname.equals(prevElement.tag)) {
							ObjectSchema temp = getConceptSchema(stack.getPreviousComplexElement(1).term); 
							Facet[] facets = temp.getFacets(qname);
							if (facets!=null)
								objectSchema = getContentType(facets);
							else  {
								objectSchema = null;
								emptyAggregate = true;
							}
						} else {
							stack.pop();
							AbsHelper.setAttribute(stack.getPreviousComplexElement(0).term, prevElement.tag, prevElement.term);
						}	
					} else
						objectSchema = prevComplexSchema.getSchema(qname);
				//} catch (OntologyException e) {
					
				//}	
		}
		
		return objectSchema;
	}
		
	protected void addToStack(String qname, Attributes attr, ObjectSchema objectSchema) throws OntologyException {
			StackElement stackElement = new StackElement();
			stackElement.tag = qname;
			stackElement.term = (AbsObject)objectSchema.newInstance();
			stack.push(stackElement);

	}
		
	protected ObjectSchema getContentType(Facet[] facets) {
		for (int i=0; i<facets.length-1; i++) {
			Facet temp = facets[i];
			if (temp instanceof TypedAggregateFacet) 
				return ((TypedAggregateFacet)temp).getType();
		}
		return null;	
	}
		
	protected ObjectSchema getConceptSchema(AbsObject absObject) throws OntologyException {
		//try {
			return ontology.getSchema(absObject.getTypeName());
		//} catch (OntologyException e) {
		//	System.out.println(e);
		// }
		// return null;
	}

	protected void decodePrimitive(StackElement element, String content) {

		try {
			ObjectSchema objectSchema = getConceptSchema(stack.getPreviousComplexElement(0).term);		
			PrimitiveSchema attributeSchema = (PrimitiveSchema)objectSchema.getSchema(element.tag);

			String type = attributeSchema.getTypeName();
			
			AbsObject abs = null;
	
	    	if (type.equals(BasicOntology.STRING)) {
      			abs = AbsPrimitive.wrap(content);
        	} 

        	if (type.equals(BasicOntology.BOOLEAN)) {
        		boolean value = content.equals("true");
        		abs = AbsPrimitive.wrap(value);
        	} 

        	if (type.equals(BasicOntology.INTEGER)) {
        		long value = Long.parseLong(content);
        		abs = AbsPrimitive.wrap(value);
        	} 

   			if (type.equals(BasicOntology.FLOAT)) {
   				double value = Double.parseDouble(content);
       			abs = AbsPrimitive.wrap(value);
   			} 

   			if (type.equals(BasicOntology.DATE)) {
            	abs = AbsPrimitive.wrap(ISO8601.toDate(content));
       	 	} 
       	 	
       	 	if (type.equals(BasicOntology.BYTE_SEQUENCE)) {
            	abs = AbsPrimitive.wrap(Base64.decode(content.toCharArray()));
       	 	} 
       	 	
       	 	element.term = abs;
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
}	