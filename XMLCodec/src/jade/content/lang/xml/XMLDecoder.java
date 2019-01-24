package jade.content.lang.xml;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import jade.content.abs.*;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SL0Vocabulary;
import jade.content.onto.BasicOntology;
import jade.content.onto.OntologyException;
import jade.content.onto.Ontology;
import jade.content.schema.*;
import jade.lang.acl.ISO8601;
import jade.util.leap.Iterator;
import jade.util.leap.List;
import jade.util.leap.ArrayList;

import org.apache.commons.codec.binary.Base64;

class XMLDecoder {
	private Ontology ontology;
	private boolean preserveJavaTypes;

	public void init(Ontology onto, boolean preserveJavaTypes) {
		ontology = onto;
		this.preserveJavaTypes = preserveJavaTypes;
	}

	public AbsObject decode(String xml) throws CodecException, OntologyException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false);
		factory.setNamespaceAware(false);
		Node root = null;
		try {
			root = parseXML(factory, xml);
		} 
		catch (SAXException e) {
			// Possibly parsing failed since the xml content has the form 
			// <Foo>
			//   ...
			// </Foo>
			// <Foo>
			//   ...
			// </Foo>
			// ...
			// i.e. it represents an Aggregate --> Try to manage this case by transforming the xml content as
			// <sequence>
			//   <Foo>
			//     ...
			//   </Foo>
			//   ...
			// </sequence>
			// If parsing still fails, rethrow the original exception
			try {
				xml = "<" + SL0Vocabulary.SEQUENCE + ">" + xml + "</" + SL0Vocabulary.SEQUENCE + ">";
				root = parseXML(factory, xml);
			}
			catch (SAXException e1) {
				throw new CodecException("XML parse error", e);
			}
			catch (Exception e2) {
				throw new CodecException("XML parse error", e2);
			}
		}
		catch (Exception e) {
			// Error generated by the parser
			throw new CodecException("XML parse error", e);
		}		
		// If we get here, root is certainly != null
		return decodeNode(root);
	}
	
	private Node parseXML(DocumentBuilderFactory factory, String xml) throws Exception {
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new InputSource(new StringReader(xml)));
		return doc.getDocumentElement();
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
				if (schema instanceof AggregateSchema) {
					return decodeAggregate(n.getChildNodes(), n.getAttributes(), schema.getTypeName());
				}
				
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
			String slotTypeName = schema.getSchema(slotName).getTypeName();
			setPrimitiveSlot(abs, slotName, slotTypeName, slot.getNodeValue());
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
				// Check if the slot value is an aggregate
				ObjectSchema slotSchema = schema.getSchema(slotName);
				if (slotSchema instanceof AggregateSchema) {
					// The slot schema mandates the value to be an aggregate
					slotValue = decodeAggregate(slotChildList, slot.getAttributes(), slotSchema.getTypeName());
				}
				else if (AggregateSchema.getBaseSchema().isCompatibleWith(slotSchema)) {
					// The slot schema allows the value to be an aggregate. If this is the case the "aggregate" attribute is set to true
					attributes = slot.getAttributes();
					Node attr = attributes.getNamedItem(XMLCodec.AGGREGATE_ATTR);
					if (attr != null && attr.getNodeValue().equals("true")) {
						slotValue = decodeAggregate(slotChildList, attributes, null);
					}
				}
				if (slotValue == null) {
					// Check if the slot value is a String
					if (slotChildList.getLength() == 1 && slot.getAttributes().getLength() == 0) {
						Node strValue = slotChildList.item(0);
						if (!(strValue instanceof Element)) {
							slotValue = AbsPrimitive.wrap(strValue.getNodeValue());
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
	 * Even if the frame was encoded by order some slots may have been encoded by name in any way. These are:
	 * - slots with non-string primitive values. These are encoded as XML attributes 
	 * - slots with string primitive values. These are encoded as <slot-name>string</slot-name>
	 * - slots with aggregate values. These are encoded as <slot-name><item1..><item2..>..</slot-name>
	 * Therefore we first fill an array of AbsObject with the slots encoded by name each one in its correct position, plus
	 * a list with the slots encoded by order. Then we assign the slots to the frame that is being decoded taking
	 * slot values either from the slotValuesByName array or from the slotValuesByOrder list. In the latter case we 
	 * get the first element that is compatible with the schema of the slot to be filled.
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
			String slotTypeName = schema.getSchema(slotName).getTypeName();
			setPrimitiveSlot(slotNames, slotValuesByName, slotName, slotTypeName, slot.getNodeValue());
		}
		NodeList list = n.getChildNodes();
		length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node slot = list.item(i);
			if (slot instanceof Element) {
				if (!handleAggregateSlot(slotNames, slotValuesByName, slot, schema)) {
					if (!handleStringSlot(slotNames, slotValuesByName, slot)) {
						slotValuesByOrder.add(decodeNode(slot));
					}
				}
			}
		}

		for (int i = 0; i < slotNames.length; ++i) {
			if (slotValuesByName[i] != null) {
				abs.set(slotNames[i], slotValuesByName[i]);
			}
			else {
				// The value was not encoded by name --> it must be encoded by order
				ObjectSchema slotSchema = schema.getSchema(slotNames[i]);
				
				// Find the first element in the slotValuesByOrder list 
				// with type and slotSchema compatible 
				AbsObject slotValue = findValueByType(slotValuesByOrder, slotSchema);
				if (slotValue != null) {
					// Set value 
					abs.set(slotNames[i], slotValue);
					
					// Remove used slotValue from list
					slotValuesByOrder.remove(slotValue);
				}
			}
		}
		return abs;
	}

	private AbsObject findValueByType(List slotValues, ObjectSchema slotSchema) throws OntologyException {
		Iterator it = slotValues.iterator();
		while(it.hasNext()) {
			AbsObject slotValue = (AbsObject)it.next();
			ObjectSchema valueSchema = ontology.getSchema(slotValue.getTypeName());
			if (valueSchema.isCompatibleWith(slotSchema)) {
				return slotValue;
			}
		}
		return null;
	}
	
	private AbsObject decodePrimitive(Node n) throws CodecException {
		NamedNodeMap attributes = n.getAttributes();
		Node attrType = attributes.getNamedItem(XMLCodec.TYPE_ATTR);
		Node attrValue = attributes.getNamedItem(XMLCodec.VALUE_ATTR);
		if (attrType != null && attrValue != null) {
			return decodeAbsPrimitive(attrType.getNodeValue(), attrValue.getNodeValue());
		}
		else {
			throw new CodecException("Missing \"value\" and \"type\" attributes in primitive element "+n);
		}
	}

	private AbsPrimitive decodeAbsPrimitive(String typeName, String value) {
		// FIXME: with typeName is possible optimize the function
		
		// For type string decode xml text in java text (second equals is for backward compatibility) 
		if (XMLCodec.STRING.equals(typeName) || BasicOntology.STRING.equals(typeName)) {
			value = XMLCodec.fromXML(value);
			return AbsPrimitive.wrap(value);
		}
		
		if (preserveJavaTypes) {
			int length = value.length();
			char lastChar = length > 0 ? value.charAt(length-1) : '#'; // '#' is used just as a character different from 'L' and 'F'
			if (lastChar == 'L') {
				// Try as a long
				try {
					return AbsPrimitive.wrap(Long.parseLong(value.substring(0, length -1)));
				}
				catch (Exception e) {}
			}
			if (lastChar == 'F') {
				// Try as a Float
				try {
					return AbsPrimitive.wrap(Float.parseFloat(value.substring(0, length -1)));
				}
				catch (Exception e) {}
			}
			// Try as an Integer
			try {
				return AbsPrimitive.wrap(Integer.parseInt(value));
			}
			catch (Exception e) {}
		}
		else {
			// Try as a Long
			try {
				return AbsPrimitive.wrap(Long.parseLong(value));
			}
			catch (Exception e) {}
		}
		
		// Try as a Double
		try {
			return AbsPrimitive.wrap(Double.parseDouble(value));
		}
		catch (Exception e) {}
		
		// Try as a Date
		try {
			return AbsPrimitive.wrap(ISO8601.toDate(value));
		}
		catch (Exception e) {}
		
		// Try as a byte[]
		if (value.startsWith(XMLCodec.BINARY_STARTER)) {
			try {
				String base64Str = value.substring(1);
				return AbsPrimitive.wrap(Base64.decodeBase64(base64Str.getBytes("US-ASCII"))); 
			}
			catch (Exception e) {}
		}
		
		// Try as a Boolean (note that Boolean.parseBoolean() returns false for all strings but "true")
		if (value.equalsIgnoreCase("true")) {
			return AbsPrimitive.wrap(true);
		}
		if (value.equalsIgnoreCase("false")) {
			return AbsPrimitive.wrap(false);
		}
		
		// It must be a String
		return AbsPrimitive.wrap(value.toString());
	}
	
	private void setPrimitiveSlot(AbsPrimitiveSlotsHolder abs, String slotName, String slotTypeName, String value) {
		AbsPrimitive slotValue = decodeAbsPrimitive(slotTypeName, value);
		abs.set(slotName, slotValue);
	}

	private void setPrimitiveSlot(String[] slotNames, AbsObject[] slotValues, String slotName, String slotType, String value) throws OntologyException {
		for (int i = 0; i < slotNames.length; ++i) {
			if (slotNames[i].equalsIgnoreCase(slotName)) {
				slotValues[i] = decodeAbsPrimitive(slotType, value);
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
				if (slotChildList.getLength() == 1 && slot.getAttributes().getLength() == 0) {
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
						slotValues[i] = decodeAggregate(slot.getChildNodes(), slot.getAttributes(), slotSchema.getTypeName());
						return true;
					}
					else if (AggregateSchema.getBaseSchema().isCompatibleWith(slotSchema)) {
						// The slot schema allows the value to be an aggregate. If this is the case the "aggregate" attribute is set to true
						NamedNodeMap attributes = slot.getAttributes();
						Node attr = attributes.getNamedItem(XMLCodec.AGGREGATE_ATTR);
						if (attr != null && attr.getNodeValue().equals("true")) {
							slotValues[i] = decodeAggregate(slot.getChildNodes(), attributes, null);
							return true;
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

	private AbsAggregate decodeAggregate(NodeList list, NamedNodeMap attributes, String typeName) throws CodecException, OntologyException {
		Node attr = attributes.getNamedItem(XMLCodec.AGGREGATE_TYPE_ATTR);
		if (attr != null) {
			typeName = attr.getNodeValue();
		}
		if (typeName == null) {
			typeName = SL0Vocabulary.SEQUENCE;
		}
		AbsAggregate abs = new AbsAggregate(typeName);
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
