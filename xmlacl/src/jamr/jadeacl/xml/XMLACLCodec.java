/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

Copyright (C) 2000, 2001 Laboratoire d'Intelligence
Artificielle, Ecole Polytechnique Federale de Lausanne

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

package jamr.jadeacl.xml;

/**
   Class used to convert between the XML ACL representation as
   defined in FIPA spec 00071 and JADE's ACLMessage object.

   @author Ion Constantinescu - EPFL
   @version $Date$ $Revision$

 */
import java.util.Vector;
import java.util.Iterator;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;
import jade.core.AID;
import jade.lang.acl.ACLCodec.CodecException;

public class XMLACLCodec extends DefaultHandler implements ACLCodec {

    public static final String DEFAULT_PARSER_NAME= "org.apache.crimson.parser.XMLReaderImpl";    

    //    public static final String DEFAULT_PARSER_NAME= "org.apache.xerces.parsers.SAXParser";

    static String parserClassName;

    static {
	parserClassName=System.getProperty("org.xml.sax.parser");
	if( (parserClassName == null) || parserClassName.equals("") ) {
	    parserClassName=DEFAULT_PARSER_NAME;
	}
    }

    public static final String ACL_REPRESENTATION_NAME = "fipa.acl.rep.xml.std"; 



    public static final String HREF_ATTR="href";
    
    public static final String FIPA_MESSAGE_TAG="fipa-message";
    public static final String ACT_ATTR="act";

    public static final String CONVERSATION_ID_ATTR="conversation-id";

    public static final String SENDER_TAG="sender";
    public static final String RECEIVER_TAG="receiver";

    public static final String CONTENT_TAG="content";
    public static final String LANGUAGE_TAG="language";
    public static final String CONTENT_LANGUAGE_ENCODING_TAG="content-language-encoding";
    public static final String ONTOLOGY_TAG="ontology";
    public static final String PROTOCOL_TAG="protocol";
    public static final String REPLY_WITH_TAG="reply-with";
    public static final String IN_REPLY_TO_TAG="in-reply-to";
    public static final String REPLY_BY_TAG="reply-by";
    public static final String TIME_ATTR="time";
    public static final String REPLY_TO_TAG="reply-to";

    public static final String CONVERSATION_ID_TAG="conversation-id";

    public static final String AID_TAG="agent-identifier";
    public static final String NAME_TAG="name";
    public static final String ID_ATTR="id";
    public static final String ADDRESSES_TAG="addresses";
    public static final String RESOLVERS_TAG="resolvers";
    public static final String UD_TAG="user-defined";
    public static final String URL_TAG="url";

    XMLReader parser;

    boolean pcdata_accumulate=false;

    String pcdata_buf=null;


    public XMLACLCodec() throws CodecException {

	try {
	    parser = (XMLReader)Class.forName(parserClassName).newInstance();
	    parser.setContentHandler(this);
	} catch( ClassNotFoundException cexc ) {
	    throw new CodecException("While creating parser got ",cexc);
	} catch( InstantiationException iexc ) {
	    throw new CodecException("While creating parser got ",iexc);
	} catch( IllegalAccessException ilexc ) {
	    throw new CodecException("While creating parser got ",ilexc);
	}
    }

    public String getValueByLocalName( Attributes attributes, 
				       String localName ) {

	for( int i=0 ;  i < attributes.getLength() ; i++ ) {

	    if( attributes.getLocalName(i).equalsIgnoreCase(localName) ) {	    
		return attributes.getValue(i);
	    }
 
	}
	return null;
    }

    Object current;

    Vector stack=new Vector();

    ACLMessage msg=null; 

    public void startElement( String uri,
			      String localName,
			      String qName,
			      Attributes attributes) throws SAXException {

	String tmp=null;

	if( localName.equalsIgnoreCase(FIPA_MESSAGE_TAG) ) {

	    tmp=getValueByLocalName(attributes, ACT_ATTR);
		    
	    if( tmp == null ) {
		throw new SAXException("Could not create fipa message with empty communicative act !");
	    }
		    
	    msg=new ACLMessage(ACLMessage.getAllPerformatives().indexOf(tmp.toUpperCase()));
	    
	    stack.clear();
	}
	
	if( localName.equalsIgnoreCase(SENDER_TAG) ||
		localName.equalsIgnoreCase(RECEIVER_TAG) ||
		localName.equalsIgnoreCase(REPLY_TO_TAG) ||
		localName.equalsIgnoreCase(RESOLVERS_TAG) ) {
		current=new AID("");
		stack.addElement(current);
	    }

	    if( localName.equalsIgnoreCase(NAME_TAG) ){
		tmp=getValueByLocalName(attributes,ID_ATTR);
		if( tmp != null ) {
		    ((AID)current).setName(tmp);
		} else {
		    throw new SAXException("Empty name value not allowed !");
		}
	    }

	    if( localName.equalsIgnoreCase(URL_TAG) ){
		tmp=getValueByLocalName(attributes,HREF_ATTR);
		if( tmp != null ) {
		    ((AID)current).addAddresses(tmp);
		} else {
		    throw new SAXException("Empty url value not allowed !");
		}
	    }

	    if( localName.equalsIgnoreCase(REPLY_BY_TAG) ) {
		tmp=getValueByLocalName(attributes,TIME_ATTR);
		
		if( (tmp != null) && (!tmp.equals("")) ) {
		    msg.setReplyBy(tmp);
		} else {
		    throw new SAXException("Empty reply by value not allowed !");
		}
	    }
	
	    tmp=getValueByLocalName(attributes,HREF_ATTR);

	    if( tmp != null ) {
		if( localName.equalsIgnoreCase(CONTENT_TAG) ) {
		    msg.setContent(tmp);
		} else if( localName.equalsIgnoreCase(LANGUAGE_TAG) ) {
		    msg.setLanguage(tmp);
		} else if( localName.equalsIgnoreCase(CONTENT_LANGUAGE_ENCODING_TAG) ) {
		    msg.setEncoding(tmp);
		} else if( localName.equalsIgnoreCase(ONTOLOGY_TAG) ) {
		    msg.setOntology(tmp);
		} else if( localName.equalsIgnoreCase(PROTOCOL_TAG) ) {
		    msg.setProtocol(tmp);
		} else if( localName.equalsIgnoreCase(REPLY_WITH_TAG) ) {
		    msg.setReplyWith(tmp);
		} else if( localName.equalsIgnoreCase(IN_REPLY_TO_TAG) ) {
		    msg.setInReplyTo(tmp);
		} else if( localName.equalsIgnoreCase(CONVERSATION_ID_TAG) ) {
		    msg.setConversationId(tmp);
		}
	    } else {
		if( localName.equalsIgnoreCase(CONTENT_TAG) ||
		    localName.equalsIgnoreCase(LANGUAGE_TAG) ||
		    localName.equalsIgnoreCase(CONTENT_LANGUAGE_ENCODING_TAG) ||
		    localName.equalsIgnoreCase(ONTOLOGY_TAG) ||
		    localName.equalsIgnoreCase(PROTOCOL_TAG) ||
		    localName.equalsIgnoreCase(REPLY_WITH_TAG) ||
		    localName.equalsIgnoreCase(IN_REPLY_TO_TAG) ||
		    localName.equalsIgnoreCase(CONVERSATION_ID_TAG) ) {
		    pcdata_accumulate=true;
		}
	    }

    }
    
    public void endElement( String uri,
			    String localName,
			    String qName)
	throws SAXException {

	if( pcdata_accumulate ) {
	    if( localName.equalsIgnoreCase(CONTENT_TAG) ) {
		msg.setContent(pcdata_buf);
	    } else if( localName.equalsIgnoreCase(LANGUAGE_TAG) ) {
		msg.setLanguage(pcdata_buf);
	    } else if( localName.equalsIgnoreCase(CONTENT_LANGUAGE_ENCODING_TAG) ) {
		msg.setEncoding(pcdata_buf);
	    } else if( localName.equalsIgnoreCase(ONTOLOGY_TAG) ) {
		msg.setOntology(pcdata_buf);
	    } else if( localName.equalsIgnoreCase(PROTOCOL_TAG) ) {
		msg.setProtocol(pcdata_buf);
	    } else if( localName.equalsIgnoreCase(REPLY_WITH_TAG) ) {
		msg.setReplyWith(pcdata_buf);
	    } else if( localName.equalsIgnoreCase(IN_REPLY_TO_TAG) ) {
		msg.setInReplyTo(pcdata_buf);
	    } else if( localName.equalsIgnoreCase(REPLY_BY_TAG) ) {
		msg.setReplyBy(pcdata_buf);
	    } else if( localName.equalsIgnoreCase(CONVERSATION_ID_TAG) ) {
		msg.setConversationId(pcdata_buf);
	    }
	    pcdata_accumulate=false;
	    pcdata_buf=null;
	} else {
	    if( localName.equalsIgnoreCase(SENDER_TAG) ) {
		msg.setSender((AID)current);
		stack.removeElementAt(stack.size()-1);
	    } else if( localName.equalsIgnoreCase(RECEIVER_TAG) ) {
		msg.addReceiver((AID)current);
		stack.removeElementAt(stack.size()-1);
	    } else if( localName.equalsIgnoreCase(REPLY_TO_TAG) ) {
		msg.addReplyTo((AID)current);
		stack.removeElementAt(stack.size()-1);
	    } else if( localName.equalsIgnoreCase(RESOLVERS_TAG) ) {
		AID tmpaid=(AID)current;
		stack.removeElementAt(stack.size()-1);
		current=stack.elementAt(stack.size()-1);
		((AID)current).addResolvers(tmpaid);
	    }
	}	
    }


    public void characters( char[] chars, int pos,  int len ) {	
	if( pcdata_accumulate ) {
	    String str=(new String(chars,pos,len)).trim();
	    if( ! str.equals("") ) {
		if( (pcdata_buf == null) || pcdata_buf.equals("") ) {
		    pcdata_buf=str;
		} else {
		    pcdata_buf=pcdata_buf+" "+str;
		}
	    }
	}
    }

    /** Ignorable whitespace. */
    public void ignorableWhitespace(char ch[], int start, int length) {
    }

    private void encodeAID( StringBuffer sb, String prefix, AID aid ) {
	sb.append(prefix);
	sb.append("<");
	sb.append(AID_TAG);
	sb.append(">\n");

	sb.append(prefix);
	sb.append("\t<");
	sb.append(NAME_TAG);
	sb.append(" ");
	sb.append(ID_ATTR);
	sb.append("=\"");
	sb.append(aid.getName());
	sb.append("\" />\n");
	
	String addrs[]=aid.getAddressesArray();
	if( (addrs != null) && (addrs.length>0) ) {
	    sb.append(prefix);
	    sb.append("\t<");
	    sb.append(ADDRESSES_TAG);
	    sb.append(">\n");

	    for( int i=0 ; i < addrs.length ; i++ ) {
		sb.append(prefix);
		sb.append("\t\t<");
		sb.append(URL_TAG);
		sb.append(" ");
		sb.append(HREF_ATTR);
		sb.append("=\"");
		sb.append(addrs[i]);
		sb.append("\" />\n");
	    }

	    sb.append(prefix);
	    sb.append("\t</");
	    sb.append(ADDRESSES_TAG);
	    sb.append(">\n");
	}


	sb.append(prefix);
	sb.append("</");
	sb.append(AID_TAG);
	sb.append(">\n");
    }

    /**
       Encodes an <code>ACLMessage</code> object into a byte 
       sequence, according to the specific message representation.
       @param msg The ACL message to encode.
       @return a byte array, containing the encoded message.
    */
    public byte[] encode(ACLMessage msg) {       
	StringBuffer sb=new StringBuffer("<");
	sb.append(FIPA_MESSAGE_TAG);
	sb.append(" ");
	sb.append(ACT_ATTR);
	sb.append("=\"");
	sb.append(ACLMessage.getAllPerformatives().get(msg.getPerformative()));
	sb.append("\" >\n");
	String tmp=null;

	if( msg.getSender() != null ) {
	    sb.append("\t<");
	    sb.append(SENDER_TAG);
	    sb.append(">\n");
	    encodeAID( sb, "\t\t", msg.getSender() );
	    sb.append("\t</");
	    sb.append(SENDER_TAG);
	    sb.append(">\n");
	}


	for( Iterator it=msg.getAllReceiver() ; it.hasNext() ; ) {
	    sb.append("\t<");
	    sb.append(RECEIVER_TAG);
	    sb.append(">\n");
	    encodeAID( sb, "\t\t", (AID)it.next() );
	    sb.append("\t</");
	    sb.append(RECEIVER_TAG);
	    sb.append(">\n");
	}

	for( Iterator it=msg.getAllReplyTo() ; it.hasNext() ; ) {
	    sb.append("\t<");
	    sb.append(REPLY_TO_TAG);
	    sb.append(">\n");
	    encodeAID( sb, "\t\t", (AID)it.next() );
	    sb.append("\t</");
	    sb.append(REPLY_TO_TAG);
	    sb.append(">\n");

	}


	tmp=msg.getContent();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(CONTENT_TAG);
	    sb.append(">");
	    sb.append(tmp);
	    sb.append("</");
	    sb.append(CONTENT_TAG);
	    sb.append(">\n");
	}

	tmp=msg.getLanguage();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(LANGUAGE_TAG);
	    sb.append(">");
	    sb.append(tmp);
	    sb.append("</");
	    sb.append(LANGUAGE_TAG);
	    sb.append(">\n");
	}

	tmp=msg.getEncoding();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(CONTENT_LANGUAGE_ENCODING_TAG);
	    sb.append(">");
	    sb.append(tmp);
	    sb.append("</");
	    sb.append(CONTENT_LANGUAGE_ENCODING_TAG);
	    sb.append(">\n");
	}
	
	tmp=msg.getOntology();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(ONTOLOGY_TAG);
	    sb.append(">");
	    sb.append(tmp);
	    sb.append("</");
	    sb.append(ONTOLOGY_TAG);
	    sb.append(">\n");
	}
	
	tmp=msg.getProtocol();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(PROTOCOL_TAG);
	    sb.append(">");
	    sb.append(tmp);
	    sb.append("</");
	    sb.append(PROTOCOL_TAG);
	    sb.append(">\n");
	}
	
	tmp=msg.getConversationId();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(CONVERSATION_ID_TAG);
	    sb.append(">");
	    sb.append(tmp);
	    sb.append("</");
	    sb.append(CONVERSATION_ID_TAG);
	    sb.append(">\n");
	}
	
	tmp=msg.getReplyWith();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(REPLY_WITH_TAG);
	    sb.append(">");
	    sb.append(tmp);
	    sb.append("</");
	    sb.append(REPLY_WITH_TAG);
	    sb.append(">\n");
	}
	
	tmp=msg.getInReplyTo();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(IN_REPLY_TO_TAG);
	    sb.append(">");
	    sb.append(tmp);
	    sb.append("</");
	    sb.append(IN_REPLY_TO_TAG);
	    sb.append(">\n");
	}
	
	tmp=msg.getReplyBy();
	if( (tmp != null) && (!tmp.equals("")) ) {
	    sb.append("\t<");
	    sb.append(REPLY_BY_TAG);
	    sb.append(" time=\"");
	    sb.append(tmp);
	    sb.append("\" />\n");
	}
	
	sb.append("</");
	sb.append(FIPA_MESSAGE_TAG);
	sb.append(">\n");
	return sb.toString().getBytes();
    }

    /**
       Recovers an <code>ACLMessage</code> object back from raw data,
       using the specific message representation to interpret the 
       byte sequence.
       @param data The byte sequence containing the encoded message.
       @return A new <code>ACLMessage</code> object, built from the 
       raw data.
       @exception CodecException If some kind of syntax error occurs.
    */
    public ACLMessage decode(byte[] data) throws CodecException {
	try {
	    parser.parse(new InputSource(new ByteArrayInputStream(data)));

	    return msg;

	} catch( IOException iexc ) {
	    throw new CodecException("While decoding got ",iexc);
	} catch( SAXException sexc ) {
	    throw new CodecException("While decoding got ",sexc);
	}
    }

    /**
       Query the name of the message representation handled by this
       <code>Codec</code> object. The FIPA standard representations
       have a name starting with <code><b>"fipa.acl.rep."</b></code>.
       @return The name of the handled ACL message representation.
    */
    public String getName() {
	return ACL_REPRESENTATION_NAME;
    }

    public static void main( String args[] ) {
	if( args.length <= 0 ) {
	    System.out.println("usage: XMLACLCodec <acl file>");
	    System.exit(-1);
	}
	
	try {

	    java.io.BufferedReader r=new BufferedReader(new FileReader(args[0]));

	    StringBuffer sb=new StringBuffer();
	    String tmp;

	    while( (tmp=r.readLine()) != null ) {
		sb.append(tmp);
	    }

	    r.close();

	    System.out.println("got:\n"+sb.toString());

	    XMLACLCodec codec=new XMLACLCodec();
	    
	    ACLMessage msg=codec.decode(sb.toString().getBytes());

	    System.out.println("msg:\n"+msg);

	    System.out.println("xml:\n"+new String(codec.encode(msg)));

	} catch( Exception exc ){
	    exc.printStackTrace();
	}

    }

}
