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

import jade.content.lang.StringCodec;

import java.io.StringReader;

import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.abs.AbsContentElement;
import jade.content.abs.AbsObject;

import javax.xml.parsers.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;


public class XMLCodec extends StringCodec {
	public static final String NAME = "XML";
	
	public static final String PRIMITIVE_TAG = "primitive";
	public static final String VALUE_ATTR = "value";
	public static final String AGGREGATE_ATTR = "aggregate";
	public static final String BINARY_STARTER = "#";
	
    public XMLCodec() {
    	super(NAME);
    }
    
	/**
	 * Encodes a content into a string.
	 * @param content the content as an abstract descriptor.
	 * @return the content as a string.
	 * @throws CodecException
	 */
	public String encode(AbsContentElement content) throws CodecException {
		throw new CodecException("Not supported");
	}
	
	/**
	 * Encodes a content into a string using a given ontology.
	 * @param ontology the ontology 
	 * @param content the content as an abstract descriptor.
	 * @return the content as a string.
	 * @throws CodecException
	 */
	public String encode(Ontology ontology, AbsContentElement content) throws CodecException {
		try {
			return encodeAbsObject(ontology, content, false);
		}
		catch (OntologyException oe) {
			throw new CodecException("Ontology error", oe);
		}
	}
	
	public String encodeAbsObject(Ontology ontology, AbsObject abs, boolean indent) throws CodecException, OntologyException {
		XMLEncoder encoder = new XMLEncoder();
		StringBuffer sb = new StringBuffer();
		encoder.init(ontology, sb);
		encoder.setIndentEnabled(indent);
		encoder.encode(abs);
		return sb.toString();
	}
	
	/**
	 * Encode a generic ontological entity in XML form
	 */
	public String encodeObject(Ontology ontology, Object obj, boolean indent) throws CodecException, OntologyException {
		AbsObject abs = ontology.fromObject(obj);
		return encodeAbsObject(ontology, abs, indent);
	}
	
	/**
	 * Decodes the content to an abstract description.
	 * @param content the content as a string.
	 * @return the content as an abstract description.
	 * @throws CodecException
	 */
	public AbsContentElement decode(String content) throws CodecException {
		throw new CodecException("Not supported");
	}
	
	/**.
	 * Decodes the content to an abstract description using a 
	 * given ontology.
	 * @param ontology the ontology.
	 * @param content the content as a string.
	 * @return the content as an abstract description.
	 * @throws CodecException
	 */
	public AbsContentElement decode(Ontology ontology, String content) throws CodecException {
		try {
			AbsObject abs = decodeAbsObject(ontology, content);
			if (abs instanceof AbsContentElement) {
				return (AbsContentElement) abs;
			}
			else {
				throw new CodecException(abs.getTypeName()+" is not a content element");
			}
		}
		catch (OntologyException oe) {
			throw new CodecException("Ontology error", oe);
		}
	}
	
	public AbsObject decodeAbsObject(Ontology ontology, String xml) throws CodecException, OntologyException {
		XMLDecoder decoder = new XMLDecoder();
		decoder.init(ontology);
		return decoder.decode(xml);
	}
	
	/**
	 * Decode a generic ontological entity from an XML form
	 */
	public Object decodeObject(Ontology ontology, String xml) throws CodecException, OntologyException {
		AbsObject abs = decodeAbsObject(ontology, xml);
		return ontology.toObject(abs);
	}
}