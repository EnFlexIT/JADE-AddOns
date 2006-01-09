package jade.content.lang.xml;

import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import jade.content.abs.*;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.OntologyException;
import jade.content.onto.Ontology;
import jade.content.schema.*;
import jade.lang.acl.ISO8601;
import jade.util.leap.List;
import jade.util.leap.ArrayList;

import org.apache.commons.codec.binary.Base64;

public class XMLDecoder {
	private Ontology ontology;
	
	public void init(Ontology onto) {
		ontology = onto;
	}
	
	public AbsObject decode(String xml) throws CodecException, OntologyException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		Node root = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(new InputSource(new StringReader(xml)));
			root = doc.getDocumentElement();
		} 
		catch (Exception e) {
			// Error generated by the parser
			throw new CodecException("XML parse error", e);
		}		
		// If we get here, root is certainly != null
		return decodeNode(root);
	}
	
	private AbsObject decodeNode(Node n) throws CodecException, OntologyException {
		String typeName = n.getNodeName();
		if (typeName.equals(XMLCodec.PRIMITIVE_TAG)) {
			return decodePrimitive(n);
		}
		else if (typeName.equals(ContentElementListSchema.BASE_NAME)) {
			return decodeContentElementList(n);
		}
		else {
			ObjectSchema schema = ontology.getSchema(typeName);
			if (schema != null) {
				boolean encodeByOrder = schema.getEncodingByOrder();
				if (encodeByOrder) {
					return decodeNodeByOrder(n, schema);
				}
				else {
					return decodeNodeByName(n, schema);
				}
			}
			else {
				throw new OntologyException("No schema found for type name "+typeName);
			}
		}
	}
	
	private AbsObject decodeNodeByName(Node n, ObjectSchema schema) throws CodecException, OntologyException {
		AbsPrimitiveSlotsHolder abs = (AbsPrimitiveSlotsHolder) schema.newInstance();
		
		// Handle primitive slots
		NamedNodeMap attributes = n.getAttributes();
		int length = attributes.getLength();
		for (int i = 0; i < length; ++i) {
			Node slot = attributes.item(i);
			String slotName = slot.getNodeName();
			setPrimitiveSlot(abs, slotName, slot.getNodeValue());
		}
		
		// Handle non-primitive slots
		NodeList list = n.getChildNodes();
		length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node slot = list.item(i);
			if (slot instanceof Element) {
				String slotName = slot.getNodeName();
				AbsObject slotValue = null;
				NodeList slotChildList = slot.getChildNodes();
				// Check if the slot value is a String
				if (slotChildList.getLength() == 1) {
					Node strValue = slotChildList.item(0);
					if (!(strValue instanceof Element)) {
						slotValue = AbsPrimitive.wrap(strValue.getNodeValue());
					}
				}
				if (slotValue == null) {
					// Check if the slot value is an aggregate
					ObjectSchema slotSchema = schema.getSchema(slotName);
					if (slotSchema instanceof AggregateSchema) {
						// The slot schema mandates the value to be an aggregate
						slotValue = decodeAggregate(slotChildList);
					}
					else if (AggregateSchema.getBaseSchema().isCompatibleWith(slotSchema)) {
						// The slot schema allows the value to be an aggregate. If this is the case the "aggregate" attribute is set to true
						attributes = slot.getAttributes();
						length = attributes.getLength();
						if (length == 1) {
							Node attr = attributes.item(0);
							if (attr.getNodeName().equals(XMLCodec.AGGREGATE_ATTR) && attr.getNodeValue().equals("true")) {
								slotValue = decodeAggregate(slotChildList);
							}
						}
					}
				}
				if (slotValue == null) {
					// The slot value is nethier a String nor an aggregate --> It must be a normal Frame
					for (int j = 0; j < slotChildList.getLength(); ++j) {
						Node value = slotChildList.item(j);
						if (value instanceof Element) {
							slotValue = decodeNode(value);
							break;
						}
					}
				}
				abs.set(slotName, slotValue);
			}
		}
		return abs;
	}

	/**
	 * Event if the frame was encoded by order some slots may have been encoded by name in any way. These are:
	 * - slots with non-string primitive values. These are encoded as XML attributes 
	 * - slots with string primitive values. These are encoded as <slot-name>string</slot-name>
	 * - slots with aggregate values. These are encoded as <slot-name><item1..><item2..>..</slot-name>
	 * Therefore we first fill an array of AbsObject with the slots encoded by name each one in its correct position, plus
	 * a list with the slots encoded by order. Then we assign the slots to the frame that is being decoded taking
	 * slot values either from the slotValuesByName array or from the slotValuesByOrder list.
	 */
	private AbsObject decodeNodeByOrder(Node n, ObjectSchema schema) throws CodecException, OntologyException {
		AbsPrimitiveSlotsHolder abs = (AbsPrimitiveSlotsHolder) schema.newInstance();
		String[] slotNames = schema.getNames();
		AbsObject[] slotValuesByName = new AbsObject[slotNames.length];
		List slotValuesByOrder = new ArrayList();
		NamedNodeMap attributes = n.getAttributes();
		int length = attributes.getLength();
		for (int i = 0; i < length; ++i) {
			Node slot = attributes.item(i);
			String slotName = slot.getNodeName();
			setPrimitiveSlot(slotNames, slotValuesByName, slotName, slot.getNodeValue());
		}
		NodeList list = n.getChildNodes();
		length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node slot = list.item(i);
			if (slot instanceof Element) {
				if (!handleStringSlot(slotNames, slotValuesByName, slot)) {
					if (!handleAggregateSlot(slotNames, slotValuesByName, slot, schema)) {
						slotValuesByOrder.add(decodeNode(slot));
					}
				}
			}
		}
	
		int k = 0;
		for (int i = 0; i < slotNames.length; ++i) {
			if (slotValuesByName[i] != null) {
				abs.set(slotNames[i], slotValuesByName[i]);
			}
			else {
				// The value was not encoded by name --> it must be encoded by order
				abs.set(slotNames[i], (AbsObject) slotValuesByOrder.get(k));
				k++;
			}
		}
		return abs;
	}
	
	private AbsObject decodePrimitive(Node n) throws CodecException {
		NamedNodeMap attributes = n.getAttributes();
		int length = attributes.getLength();
		if (length == 1) {
			Node attr = attributes.item(0);
			String attrName = attr.getNodeName();
			if (attrName.equalsIgnoreCase(XMLCodec.VALUE_ATTR)) {
				return AbsPrimitive.wrap(attr.getNodeValue());
			}
			else {
				throw new CodecException("Unexpected attribute "+attrName+" in primitive element "+n);
			}
		}
		else {
			throw new CodecException("Missing \"value\" attribute in primitive element "+n);
		}
	}
	
	private void setPrimitiveSlot(AbsPrimitiveSlotsHolder abs, String slotName, String value) {
  		// Try as a Long
	  	try {
	  		abs.set(slotName, Long.parseLong(value));
			return;
	  	}
	  	catch (Exception e) {}
	  	// Try as a Double
	  	try {
	  		abs.set(slotName, Double.parseDouble(value));
			return;
	  	}
	  	catch (Exception e) {}
	  	// Try as a Date
	  	try {
	  		abs.set(slotName, ISO8601.toDate(value));
			return;
	  	}
	  	catch (Exception e) {}
	  	// Try as a byte[]
	  	if (value.startsWith(XMLCodec.BINARY_STARTER)) {
		  	try {
		  		String base64Str = value.substring(1);
				byte[] binaryValue = Base64.decodeBase64(base64Str.getBytes("US-ASCII")); 
		  		abs.set(slotName, binaryValue);
				return;
		  	}
		  	catch (Exception e) {}
	  	}
	  	// Try as a Boolean (note that Boolean.parseBoolean() returns false for all strings but "true")
	  	if (value.equalsIgnoreCase("true")) {
	  		abs.set(slotName, true);
	  		return;
	  	}
	  	if (value.equalsIgnoreCase("false")) {
	  		abs.set(slotName, false);
	  		return;
	  	}
		abs.set(slotName, value);
	}
	
	private void setPrimitiveSlot(String[] slotNames, AbsObject[] slotValues, String slotName, String value) throws OntologyException {
		for (int i = 0; i < slotNames.length; ++i) {
			if (slotNames[i].equalsIgnoreCase(slotName)) {
		  		// Try as a Long
			  	try {
			  		slotValues[i] = AbsPrimitive.wrap(Long.parseLong(value));
					return;
			  	}
			  	catch (Exception e) {}
			  	// Try as a Double
			  	try {
			  		slotValues[i] = AbsPrimitive.wrap(Double.parseDouble(value));
					return;
			  	}
			  	catch (Exception e) {}
			  	// Try as a Date
			  	try {
			  		slotValues[i] = AbsPrimitive.wrap(ISO8601.toDate(value));
					return;
			  	}
			  	catch (Exception e) {}
			  	// Try as a byte[]
			  	if (value.startsWith(XMLCodec.BINARY_STARTER)) {
				  	try {
				  		String base64Str = value.substring(1);
						byte[] binaryValue = Base64.decodeBase64(base64Str.getBytes("US-ASCII")); 
				  		slotValues[i] = AbsPrimitive.wrap(binaryValue);
						return;
				  	}
				  	catch (Exception e) {}
			  	}
			  	// Try as a Boolean
			  	try {
			  		slotValues[i] = AbsPrimitive.wrap(Boolean.parseBoolean(value));
					return;
			  	}
			  	catch (Exception e) {}
				slotValues[i] = AbsPrimitive.wrap(value);
				return;
			}
		}
		// If we get here, there is an attribute that is not a slot name.
		throw new OntologyException("Attribute "+slotName+" is not a valid slot name.");
	}
	
	private boolean handleStringSlot(String[] slotNames, AbsObject[] slotValues, Node slot) throws CodecException, OntologyException {
		String slotName = slot.getNodeName();
		for (int i = 0; i < slotNames.length; ++i) {
			if (slotNames[i].equalsIgnoreCase(slotName)) {
				NodeList slotChildList = slot.getChildNodes();
				if (slotChildList.getLength() == 1) {
					Node strValue = slotChildList.item(0);
					if (!(strValue instanceof Element)) {
						slotValues[i] = AbsPrimitive.wrap(strValue.getNodeValue());
						return true;
					}
				}
				break;
			}
		}
		return false;
	}
	
	private boolean handleAggregateSlot(String[] slotNames, AbsObject[] slotValues, Node slot, ObjectSchema schema) throws CodecException, OntologyException {
		String slotName = slot.getNodeName();
		for (int i = 0; i < slotNames.length; ++i) {
			if (slotNames[i].equalsIgnoreCase(slotName)) {
				try {
					ObjectSchema slotSchema = schema.getSchema(slotName);
					if (slotSchema instanceof AggregateSchema) {
						// The slot value is certainly an aggregate
						slotValues[i] = decodeAggregate(slot.getChildNodes());
						return true;
					}
					else if (AggregateSchema.getBaseSchema().isCompatibleWith(slotSchema)) {
						// The slot schema allows the value to be an aggregate. If this is the case the "aggregate" attribute is set to true
						NamedNodeMap attributes = slot.getAttributes();
						int length = attributes.getLength();
						if (length == 1) {
							Node attr = attributes.item(0);
							if (attr.getNodeName().equals("aggregate") && attr.getNodeValue().equals("true")) {
								slotValues[i] = decodeAggregate(slot.getChildNodes());
								return true;
							}
						}
					}
				}
				catch (OntologyException oe) {
					// slotName is not the name of a slot, but an inner element encoded by order --> ignore it
				}
				break;
			}
		}
		return false;
	}
	
	private AbsAggregate decodeAggregate(NodeList list) throws CodecException, OntologyException {
		AbsAggregate abs = new AbsAggregate("sequence");
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node item = list.item(i);
			if (item instanceof Element) {
				abs.add((AbsTerm) decodeNode(item));
			}
		}
		return abs;
	}
	
	private AbsContentElementList decodeContentElementList(Node n) throws CodecException, OntologyException {
		AbsContentElementList abs = new AbsContentElementList();
		NodeList list = n.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node item = list.item(i);
			if (item instanceof Element) {
				abs.add((AbsContentElement) decodeNode(item));
			}
		}
		return abs;
	}	
}
