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

import jade.content.lang.Codec;
import jade.content.onto.Ontology;
import jade.content.onto.BasicOntology;
import jade.content.onto.OntologyException;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsContentElementList;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsPrimitive;
import jade.content.schema.ObjectSchema;
import jade.content.schema.Facet;
import jade.content.schema.facets.TypedAggregateFacet;
import jade.lang.acl.ISO8601;
import starlight.util.Base64;
import java.util.Date;

/**
 * @author Filippo Quarta - TELECOM ITALIA LAB
 */

class XMLCoder {
	
	Ontology ontology;
	
	protected void setOntology(Ontology o) {
		ontology = o;
	}
	

	String normalizeString(String text) {
		String temp;
			
		if ((text.indexOf("<")>-1) || (text.indexOf(">")>-1)) {
			temp = new String();
			
			for (int i=0; i < text.length(); i++) 
				switch (text.charAt(i)) {
					case '>':
						temp = temp.concat("&gt;");
						break;
					case '<':
						temp = temp.concat("&lt;");
						break;
					default:
						temp = temp.concat(String.valueOf(text.charAt(i)));
				}
		} else temp = text;
				
		return temp;
	}
		
	void encodeAggregateAsTag(AbsObject content, 
							  String memberExpectedType,
							  String tag, 
							  StringBuffer sb) throws Codec.CodecException {
		
		AbsAggregate absAggregate = (AbsAggregate)content;
		int size = absAggregate.size();		
		if (size>0) {
		for (int i=0; i < absAggregate.size(); i++) 
			encodeAsTag(absAggregate.get(i), null, memberExpectedType, tag, sb);
		} else {
			// The aggregate has zero elements
			sb.append("<"+tag+">");
			sb.append("</"+tag+">");
		}
	}
	
	void encodeAsTag(AbsObject content, ObjectSchema parentSchema, String slotExpectedType, String tag, StringBuffer sb) throws Codec.CodecException{ 
  
    	boolean hasChild = false;
    	boolean hasAttributes = false;
    	String startTag;
    	String closeTag;

		try {    	
			
			// Encoding a ContentElementList
			if (content instanceof AbsContentElementList) {
				//sb.append("<CONTENT>");
				sb.append("<CONTENT_ELEMENT_LIST>");
				AbsContentElementList absCEList = (AbsContentElementList)content;
				for (int i=0; i < absCEList.size(); i++) {
					AbsObject temp = (AbsObject)absCEList.get(i);		
					encodeAsTag(temp, null, temp.getTypeName(), null, sb);
				}
				//sb.append("</CONTENT>");
				sb.append("</CONTENT_ELEMENT_LIST>");
				return;
			}
		
			// Encoding a Primitive 
    		if (content instanceof AbsPrimitive) {
    			
    			AbsPrimitive absPrimitive = (AbsPrimitive)content;
    			String typeName = ((AbsObject)absPrimitive).getTypeName();
    			String temp = null;
    			sb.append("<"+tag+">");
    			Object v = absPrimitive.getObject();
				if (typeName.equals(BasicOntology.DATE))
	    			temp = ISO8601.toString((Date)v);
				else if (typeName.equals(BasicOntology.FLOAT) ||
						 typeName.equals(BasicOntology.INTEGER))
		  			temp = v.toString();
		  		else if (typeName.equals(BasicOntology.BYTE_SEQUENCE))
		  			temp = String.copyValueOf(Base64.encode((byte [])v));
		  		else temp = normalizeString(((AbsObject)absPrimitive).toString());
				sb.append(temp);		
				sb.append("</"+tag+">");
				return;
				
			}
		
			// Encoding an Aggregate
			if (content instanceof AbsAggregate) {
				String memberExpectedType = null;
				
				Facet[] facets = parentSchema.getFacets(tag);
				if (facets!=null) {	
					for (int i = 0; i < facets.length; i++) {
						if (facets[i] instanceof TypedAggregateFacet) {
							memberExpectedType = ((TypedAggregateFacet)facets[i]).getType().getTypeName();
						}
				 	}		
				 }

    			encodeAggregateAsTag(content, memberExpectedType, tag, sb);
    			return;
    		}

		
			// Encoding a Concept
		
			ObjectSchema currSchema = ontology.getSchema(content.getTypeName());	
    	
    		if (tag==null) {
    			startTag = "CONTENT_ELEMENT type=\"" + content.getTypeName() +"\"";
    			closeTag = "CONTENT_ELEMENT";
    		} else {
    			startTag = new String(tag);
    			closeTag = startTag;
    		} 
    	
    		if (slotExpectedType!=null) {
    				if (!(currSchema.getTypeName().equals(slotExpectedType))) 
	   					startTag = startTag.concat(" type=\"" + currSchema.getTypeName() + "\"");
    		} else if ((tag!=null) && (parentSchema==null)) {
    			startTag = startTag.concat(" type=\"" + currSchema.getTypeName() + "\"");
    		}
    	
    		sb.append("<");
			sb.append(startTag);
    		sb.append(">");
    	
    		String[] names = content.getNames();
		
	   		for (int i=0; i < names.length; i++) {
    			AbsObject temp = content.getAbsObject(names[i]);
   				encodeAsTag(temp, currSchema, currSchema.getSchema(names[i]).getTypeName(), names[i], sb);
    		} 
    	
    		sb.append("</");
    		sb.append(closeTag);
    		sb.append(">");
    	
    	 } catch (OntologyException e) {
    		e.printStackTrace();	
		 } catch (Exception e) {
		 	throw new Codec.CodecException(e.getMessage());
		 }
	}	

	
}