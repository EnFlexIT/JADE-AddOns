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
package it.unipr.aot.rdf;

import java.util.List;
import java.util.Vector;
import java.util.Iterator;
import java.io.*;

import org.xml.sax.SAXException;

import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.BasicOntology;
import jade.content.lang.*;
import jade.content.lang.Codec.CodecException;
import jade.content.abs.*;
import jade.content.schema.*;


/**
  The codec class for the <b><i>RDF</i></b> language. This class
  implements the <code>Codec</code> interface and allows converting
  back and forth between arrays of byte in RDF format and AbsContentElement.

  @author Paola Turci - Universita` di Parma
 */
public class RDFCodec extends Codec {

  /* A symbolic constant, containing the name of this language.  */
  //  public static final String NAME = "FIPA-RDF0";

  //header and tail of the RDF file
  static final String XML_VERSION = "<?xml version=\"1.0\"?>\n";
  static final String NAMESPACE   = "<rdf:RDF \n"+
                     "  xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"" +
                     "  xmlns:rdfs=\"http://www.w3.org/TR/1999/PR-rdf-schema-19990303#\"" +
                     "  xmlns:FIPA-RDF=\"http://www.fipa.org/schemas/FIPA-RDF#\">\n";
  static final String TAIL = "</rdf:RDF>";

  /* Class paths for parsers */
  static final String DEFAULT_RDF_PARSER = "it.unipr.aot.rdf.RDFFrameConsumer";

  public static final String NAME = "RDFCodec";

  public static final String  RESOURCE_ID = "id";
  public static final String  RESOURCE_ABOUT = "about";

  //RDF tags
  private static final String  FIPA_RDF = 	"FIPA-RDF:";

  private static final String  TAG_REFERENCE = 	"<"+FIPA_RDF+"Reference>";
  private static final String  END_TAG_REFERENCE = "</"+FIPA_RDF+"Reference>";
  private static final String  TAG_OBJECT = "<"+FIPA_RDF+"Object>";
  private static final String  TAG_OBJECT_WITH_ABOUT = "<"+FIPA_RDF+"Object rdf:about=\"";
  private static final String  TAG_OBJECT_WITH_ID = "<"+FIPA_RDF+"Object rdf:id=\"";
  private static final String  END_TAG_ABOUT_ID = "\">";
  private static final String  END_TAG_OBJECT = "</"+FIPA_RDF+"Object>";
  private static final String  TAG_ATTRIBUTE_DESCRIPTION = "<"+FIPA_RDF+"AttributeDescription>";
  private static final String  END_TAG_ATTRIBUTE_DESCRIPTION = "</"+FIPA_RDF+"AttributeDescription>";
  private static final String  TAG_AGGREGATE = "<"+FIPA_RDF+"Aggregate>";
  private static final String  END_TAG_AGGREGATE = "</"+FIPA_RDF+"Aggregate>";
  private static final String  TAG_PRIMITIVE = "<"+FIPA_RDF+"Primitive>";
  private static final String  END_TAG_PRIMITIVE = "</"+FIPA_RDF+"Primitive>";
  private static final String  TAG_CONTENT_ELEMENT_LIST = "<"+FIPA_RDF+"ContentElementList>" ;
  private static final String  END_TAG_CONTENT_ELEMENT_LIST = "</"+FIPA_RDF+"ContentElementList>" ;

  private static final String  TAG_OBJECT_TYPE = "<"+FIPA_RDF+"type>";
  private static final String  END_TAG_OBJECT_TYPE = "</"+FIPA_RDF+"type>";
  private static final String  TAG_ATTRIBUTE = "<"+FIPA_RDF+"attribute>";
  private static final String  END_TAG_ATTRIBUTE = "</"+FIPA_RDF+"attribute>";
  private static final String  TAG_ATTRIBUTE_VALUE = "<"+FIPA_RDF+"attributeValue>";
  private static final String  END_TAG_ATTRIBUTE_VALUE = "</"+FIPA_RDF+"attributeValue>";
  private static final String  TAG_AGGREGATE_ELEMENT = "<"+FIPA_RDF+"aggregateElement>";
  private static final String  END_TAG_AGGREGATE_ELEMENT= "</"+FIPA_RDF+"aggregateElement>";
  private static final String  TAG_PRIMITIVE_VALUE = "<"+FIPA_RDF+"primitiveValue>";
  private static final String  END_TAG_PRIMITIVE_VALUE = "</"+FIPA_RDF+"primitiveValue>";
  private static final String  TAG_CONTENT_ELEMENT = "<"+FIPA_RDF+"contentElement>" ;
  private static final String  END_TAG_CONTENT_ELEMENT= "</"+FIPA_RDF+"contentElement>" ;

  //RDF elements
  private static final String  REFERENCE = 	FIPA_RDF+"Reference";
  private static final String  OBJECT = FIPA_RDF+"Object";
  private static final String  ATTRIBUTE_DESCRIPTION = FIPA_RDF+"AttributeDescription";
  private static final String  AGGREGATE = FIPA_RDF+"Aggregate";
  private static final String  PRIMITIVE = FIPA_RDF+"Primitive";
  private static final String  CONTENT_ELEMENT_LIST = FIPA_RDF+"ContentElementList" ;

  private static final String  OBJECT_TYPE = FIPA_RDF+"type";
  private static final String  ATTRIBUTE = FIPA_RDF+"attribute";
  private static final String  ATTRIBUTE_VALUE = FIPA_RDF+"attributeValue";
  private static final String  AGGREGATE_ELEMENT = FIPA_RDF+"aggregateElement";
  private static final String  PRIMITIVE_VALUE = FIPA_RDF+"primitiveValue";
  private static final String  CONTENT_ELEMENT = FIPA_RDF+"contentElement" ;

  private static final String  URI_NAMESPACE = 	"http://www.fipa.org/schemas/FIPA-RDF#";

  private static final String  URI_REFERENCE = URI_NAMESPACE+"Reference";
  private static final String  URI_OBJECT = URI_NAMESPACE+"Object";
  private static final String  URI_ATTRIBUTE_DESCRIPTION = URI_NAMESPACE+"AttributeDescription";
  private static final String  URI_AGGREGATE = URI_NAMESPACE+"Aggregate";
  private static final String  URI_PRIMITIVE = URI_NAMESPACE+"Primitive";
  private static final String  URI_CONTENT_ELEMENT_LIST =URI_NAMESPACE+"ContentElementList" ;

  private static final String  URI_OBJECT_TYPE = URI_NAMESPACE+"type";
  private static final String  URI_ATTRIBUTE = URI_NAMESPACE+"attribute";
  private static final String  URI_ATTRIBUTE_VALUE = URI_NAMESPACE+"attributeValue";
  private static final String  URI_AGGREGATE_ELEMENT = URI_NAMESPACE+"aggregateElement";
  private static final String  URI_PRIMITIVE_VALUE = URI_NAMESPACE+"primitiveValue";
  private static final String  URI_CONTENT_ELEMENT = URI_NAMESPACE+"contentElement" ;


  // IOTA operator
  public static final String  IOTA = "IOTA";

	/**
    Constructor
   */
  public RDFCodec(){
	 super(NAME);
	 parserName=DEFAULT_RDF_PARSER;
  }

/*
  public RDFCodec(String name){
	  parserName=name;
  }
*/

  /**
     From AbsContentElement to a stream of bytes in RDF format
    */
    public byte[] encode(AbsContentElement content) throws CodecException {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintWriter           stream = new PrintWriter(buffer);

            //header of RDF file
            stream.print(XML_VERSION);
            stream.print(NAMESPACE);

            //references = new Vector(); //FIXME
            //counter = 0;

            write(stream, content);

			//tail of RDF file
            stream.print(TAIL);
            stream.close();

            return buffer.toByteArray();
        }catch (IOException ioe) {
            throw new CodecException(ioe.getMessage());
        }
    }

  /**
    From AbsContentElement to a stream of bytes in RDF format, with ontology reference
   */
   public byte[] encode(Ontology ontology,
                     AbsContentElement content) throws CodecException {

      // TODO: check content against ontology.getElementSchema(content.getTypeName())
      return encode(content);
   }

  /**
    From a stream of bytes in RDF format to a AbsContentElement
   */

  public AbsContentElement decode(Ontology ontology, byte[] rdfContent)
                                                  throws CodecException{
    try {
      List parseTreeList = createParser().parse(new String(rdfContent));
      if (parseTreeList==null) return null;

      //the list is supposed to have only one element
      Iterator i=parseTreeList.iterator();
      ParseTree node = (ParseTree) i.next();

      // System.out.println("\n Tree Dump ");
      // node.dump();

      AbsObject obj = read(node, ontology);
      return (AbsContentElement) obj;

    }catch(SAXException pe) {
        //pe.printStackTrace();
        throw new CodecException("SAXParse exception");
    }catch (OntologyException oe) {
       throw new CodecException(oe.getMessage());
    }catch (IOException ioe) {
       throw new CodecException(ioe.getMessage());
    }catch (ClassCastException cce) {
       throw new CodecException(cce.getMessage());
    }
  }

  /**
    Not supported
   */
   public AbsContentElement decode(byte[] content) throws CodecException {
      throw new CodecException("Not supported");
   }


  private RDFParserWrapper createParser()throws CodecException{
	try{
      //parserName=DEFAULT_RDF_PARSER;
	  return (RDFParserWrapper)Class.forName(parserName).newInstance();
    }catch(ClassNotFoundException cnfe){
      throw new  CodecException("Parse not found exception");
    }catch(InstantiationException ie){
	   throw new  CodecException("Parser instantiation exception");
	}catch(IllegalAccessException iae){
	   throw new  CodecException("Parser illegal access exception");
    }
  }

  /*
    Return the AbsObject represented by the ParseTree object
   */
   private synchronized AbsObject read(ParseTree node, Ontology ontology)
                                        throws IOException,OntologyException {
        String     tag;
        AbsObject absObj;
        try {

            String kind = node.getName();

            //System.out.println("\n\nKIND:  "+kind);

            if (kind.equals(URI_REFERENCE)||kind.equals(REFERENCE)) {
/*
                int reference = stream.readByte();   //***********  ????
                AbsObject abs = (AbsObject) references.elementAt(reference);  //**********   ????

                if (abs != null) {
                    return abs;
                }
                else {
                    throw new IOException("Corrupted stream");
                }
*/
            }

            if (kind.equals(URI_PRIMITIVE)||kind.equals(PRIMITIVE)) {

                if(node.size()!=2) throw new OntologyException("error in primitive type");

                String  type;
                String  value;

                if((node.getNode(0).getName().equals(OBJECT_TYPE)||
                   node.getNode(0).getName().equals(URI_OBJECT_TYPE)) &&
                   (node.getNode(1).getName().equals(PRIMITIVE_VALUE)||
                    node.getNode(1).getName().equals(URI_PRIMITIVE_VALUE))){
                        type = ((Leaf) node.getNode(0)).getValue();//reads type
                       value = ((Leaf) node.getNode(1)).getValue();//reads value
			    }else if((node.getNode(1).getName().equals(OBJECT_TYPE)||
                   node.getNode(1).getName().equals(URI_OBJECT_TYPE)) &&
                   (node.getNode(0).getName().equals(PRIMITIVE_VALUE)||
                    node.getNode(0).getName().equals(URI_PRIMITIVE_VALUE))){
                        type = ((Leaf) node.getNode(1)).getValue();//reads type
                       value = ((Leaf) node.getNode(0)).getValue();//reads value
				}else throw new OntologyException("error in primitive type");

                //System.out.println("\nType:  "+type+"   Value: "+value);
                AbsPrimitive abs = null;

                if (type.equalsIgnoreCase("String")) {
                    abs = AbsPrimitive.wrap(removeCDATASection(value));
                }

                if (type.equalsIgnoreCase("Boolean")) {
                    abs = AbsPrimitive.wrap((new Boolean(value)).booleanValue());
                }

                if (type.equalsIgnoreCase("Integer")) {
                    abs = AbsPrimitive.wrap((new Integer(value)).intValue());
                }

                if (type.equalsIgnoreCase("Float")) {
                    abs = AbsPrimitive.wrap((new Float(value)).floatValue());
                }

                return abs;
            }

            if (kind.equals(URI_AGGREGATE)||kind.equals(AGGREGATE)) {

                //if(node.size()!=2) throw new OntologyException("error in aggregate type");
                String   typeName = null;

                for (int i=0; i<node.size(); i++){
                  if (node.getNode(i).getName().equals(OBJECT_TYPE)||
                      node.getNode(i).getName().equals(URI_OBJECT_TYPE)){
                    typeName = ((Leaf) node.getNode(i)).getValue();   //reads type
                    node.removeNode(i);
				  				}
                }

                if (typeName==null)throw new OntologyException("error in aggregate type");

                AbsAggregate abs = new AbsAggregate(typeName);

                for (int i=0; i<node.size(); i++){
                   //System.out.println("\n\nElement - Aggregate\n");
                   if (!(node.getNode(i).getName().equals(AGGREGATE_ELEMENT)||
                         node.getNode(i).getName().equals(URI_AGGREGATE_ELEMENT)))
                         throw new OntologyException("error in aggregate element");
                   //AGGREGATE_ELEMENT has only one node
                   AbsObject elementValue = read((ParseTree)((ParseTree)node.getNode(i)).getNode(0), ontology);
                   if (elementValue != null)
                      abs.add((AbsTerm) elementValue);
                }
                return abs;
            }

            if (kind.equals(CONTENT_ELEMENT_LIST)||kind.equals(URI_CONTENT_ELEMENT_LIST)) {
                AbsContentElementList abs = new AbsContentElementList();

                for (int i=0; i<node.size(); i++){
                   //System.out.println("\n\nElement - Aggregate\n");
                   if (!(node.getNode(i).getName().equals(CONTENT_ELEMENT)||
                         node.getNode(i).getName().equals(URI_CONTENT_ELEMENT)))
                         throw new OntologyException("error in content element");
                   //CONTENT_ELEMENT has only one node
                   AbsObject elementValue = read((ParseTree)((ParseTree)node.getNode(i)).getNode(0), ontology);
                   if (elementValue != null)
                      abs.add((AbsContentElement) elementValue);
                }
                return abs;
            }

            //System.out.println("\n\n Node dump");
            //node.dump();

            String   typeName = null;

            for (int i=0; i<node.size(); i++){
               if (node.getNode(i).getName().equals(OBJECT_TYPE)||
                   node.getNode(i).getName().equals(URI_OBJECT_TYPE)){
                      typeName = ((Leaf) node.getNode(i)).getValue();   //reads type
                      node.removeNode(i);
   		       }
            }

            if (typeName==null)throw new OntologyException("error in object type");

            ObjectSchema schema = ontology.getSchema(typeName);
            AbsObject    abs = schema.newInstance();

            //System.out.println("\n\nTYPENAME  absObject:  "+abs.getTypeName());

			//object (resource) with ID
            if (((ParseTree)node).getID()!=null) 
            	Ontology.setAttribute(abs, RESOURCE_ID, AbsPrimitive.wrap(((ParseTree)node).getID()));

            //object (resource) with reference (about)
            if (((ParseTree)node).getAbout()!=null) 
            	Ontology.setAttribute(abs, RESOURCE_ABOUT, AbsPrimitive.wrap(((ParseTree)node).getAbout()));
            //references.add(abs);
            //counter++;

		    for (int i=0; i<node.size(); i++){
              ParseTree attributeNode = (ParseTree)((ParseTree) node.getNode(i)).getNode(0);//node "ATTRIBUTE_DESCRIPTION"

              if(attributeNode.size()!=2) throw new OntologyException("error in objectAttribute type");

                String   slotName;
                ParseTree attributeValueNode;

                if((attributeNode.getNode(0).getName().equals(OBJECT_TYPE)||
                    attributeNode.getNode(0).getName().equals(URI_OBJECT_TYPE)) &&
                   (attributeNode.getNode(1).getName().equals(ATTRIBUTE_VALUE)||
                    attributeNode.getNode(1).getName().equals(URI_ATTRIBUTE_VALUE))){
                      slotName = ((Leaf) attributeNode.getNode(0)).getValue();   //reads type
                      attributeValueNode = (ParseTree) attributeNode.getNode(1); //node "ATTRIBUTE_VALUE"
   			    }else if((attributeNode.getNode(1).getName().equals(OBJECT_TYPE)||
                    attributeNode.getNode(1).getName().equals(URI_OBJECT_TYPE)) &&
                   (attributeNode.getNode(0).getName().equals(ATTRIBUTE_VALUE)||
                    attributeNode.getNode(0).getName().equals(URI_ATTRIBUTE_VALUE))){
                      slotName = ((Leaf) attributeNode.getNode(1)).getValue();//reads type
                      attributeValueNode = (ParseTree) attributeNode.getNode(0); //node "ATTRIBUTE_VALUE"
		        }else throw new OntologyException("error in objectAttribute type");

                if(attributeValueNode.size()!=1) throw new OntologyException("error in objectAttributeValue type");

                AbsObject slotValue = read((ParseTree)attributeValueNode.getNode(0), ontology);


                if (slotValue != null && slotName != null) {
  				  //System.out.println("\nwrite attribute of: "+slotName);
                  Ontology.setAttribute(abs, slotName, slotValue);
                }
            }

            return abs;
        }catch (OntologyException oe) {
           throw oe;
        }catch (IOException ioe) {
           throw ioe;
        }
    }



  /*
    Writes on an output stream the AbsObject in RDF format
   */
   private synchronized void write(PrintWriter stream,
                                    AbsObject abs) throws IOException {
/*
        int reference = references.indexOf(abs);

        if (reference != -1) {
            stream.print(REFERENCE);
            stream.print(Integer.toString(reference));
            stream.print(END_REFERENCE);
            return;
        }
*/
        if (abs instanceof AbsPrimitive) {
            stream.print(TAG_PRIMITIVE);

            Object obj = ((AbsPrimitive) abs).getObject();

            if (obj instanceof String) {
                stream.print(TAG_OBJECT_TYPE);
                stream.print("String");
                stream.print(END_TAG_OBJECT_TYPE);
                stream.print(TAG_PRIMITIVE_VALUE);
                stream.print(insertCDATASection(obj.toString()));
                stream.print(END_TAG_PRIMITIVE_VALUE);
            }

            if (obj instanceof Boolean) {
                stream.print(TAG_OBJECT_TYPE);
                stream.print("Boolean");
                stream.print(END_TAG_OBJECT_TYPE);
                stream.print(TAG_PRIMITIVE_VALUE);
                stream.print(obj.toString());
                stream.print(END_TAG_PRIMITIVE_VALUE);
            }

            if (obj instanceof Integer) {
                stream.print(TAG_OBJECT_TYPE);
                stream.print("Integer");
                stream.print(END_TAG_OBJECT_TYPE);
                stream.print(TAG_PRIMITIVE_VALUE);
                stream.print(obj.toString());
                stream.print(END_TAG_PRIMITIVE_VALUE);
            }

            if (obj instanceof Float) {
                stream.print(TAG_OBJECT_TYPE);
                stream.print("Float");
                stream.print(END_TAG_OBJECT_TYPE);
                stream.print(TAG_PRIMITIVE_VALUE);
                stream.print(obj.toString());
                stream.print(END_TAG_PRIMITIVE_VALUE);
            }

            stream.print(END_TAG_PRIMITIVE);

            return;
        }

        if (abs instanceof AbsAggregate) {
            stream.print(TAG_AGGREGATE);
            stream.print(TAG_OBJECT_TYPE);
            stream.print(abs.getTypeName());
            stream.print(END_TAG_OBJECT_TYPE);

            AbsAggregate aggregate = (AbsAggregate) abs;

            for (int i = 0; i < aggregate.size(); i++) {
                stream.print(TAG_AGGREGATE_ELEMENT);
                write(stream, aggregate.get(i));
                stream.print(END_TAG_AGGREGATE_ELEMENT);
            }

            stream.print(END_TAG_AGGREGATE);

            return;
        }

        if (abs instanceof AbsContentElementList) {
            stream.print(TAG_CONTENT_ELEMENT_LIST);

            AbsContentElementList acel = (AbsContentElementList) abs;

            for (Iterator i = acel.iterator(); i.hasNext(); ) {
                stream.print(TAG_CONTENT_ELEMENT);
                write(stream, (AbsObject) i.next());
                stream.print(END_TAG_CONTENT_ELEMENT);
            }

            stream.print(END_TAG_CONTENT_ELEMENT_LIST);

            return;
        }

        //references.add(abs);
        if (abs.getAbsObject(RESOURCE_ID)!=null){
           stream.print(TAG_OBJECT_WITH_ID);
           stream.print(((AbsPrimitive)abs.getAbsObject(RESOURCE_ID)).getString());
           stream.print(END_TAG_ABOUT_ID);
	    }else if (abs.getAbsObject(RESOURCE_ABOUT)!=null){
           stream.print(TAG_OBJECT_WITH_ABOUT);
           stream.print(((AbsPrimitive)abs.getAbsObject(RESOURCE_ABOUT)).getString());
           stream.print(END_TAG_ABOUT_ID);
	    }else stream.print(TAG_OBJECT);
        stream.print(TAG_OBJECT_TYPE);
        stream.print(abs.getTypeName());
        stream.print(END_TAG_OBJECT_TYPE);

        String[] names = abs.getNames();

        for (int i = 0; i < abs.getCount(); i++) {
			if(!(names[i].equalsIgnoreCase(RESOURCE_ID)||names[i].equalsIgnoreCase(RESOURCE_ABOUT))){
              stream.print(TAG_ATTRIBUTE);
	          stream.print(TAG_ATTRIBUTE_DESCRIPTION);
              stream.print(TAG_OBJECT_TYPE);
              stream.print(names[i]);
              stream.print(END_TAG_OBJECT_TYPE);
              stream.print(TAG_ATTRIBUTE_VALUE);
              AbsObject child = abs.getAbsObject(names[i]);
              write(stream, child);
              stream.print(END_TAG_ATTRIBUTE_VALUE);
              stream.print(END_TAG_ATTRIBUTE_DESCRIPTION);
              stream.print(END_TAG_ATTRIBUTE);
		   }
        }

        stream.print(END_TAG_OBJECT);
   }

   // Gets a quoted String from RDF string format.
   // @param a string in RDF format to be transformed.
   // @return a quoted string.
   private String insertCDATASection(String rdfString){
     StringBuffer result= new StringBuffer();
     result.append("<![CDATA[");
     result.append(rdfString);
     result.append("]]>");
     return result.toString();
   }

   // Gets RDF from quoted String.
   // @param s quoted string to be transformed.
   // @return a string in RDF format.
   private String removeCDATASection(String quotedString){
     if(quotedString.startsWith("<![CDATA["))
	   quotedString=quotedString.substring(9,quotedString.length()-3);
     return quotedString;
   }

   private String parserName;

   //private Vector    references = null;
   private int         counter = 0;
   
   /**
    * @return the ontology containing the schemas of the operator
    * defined in this language
    */
   public Ontology getInnerOntology() {
   	return RDFOntology.getInstance();
   }
   
}
