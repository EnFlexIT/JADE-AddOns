/*====================================================================
 
    Open Source Copyright Notice and License: FIPA Message 
    Representation in Bit-Efficient Encoding
 
     1.   The  programs and other works made available to you in
          these files ("the  Programs")  are  Copyright (c) 2001
          Sonera  Corporation,  Teollisuuskatu 15, P.O. Box 970,
          FIN-00051 SONERA, Finland.  
          All rights reserved.

    2.    Your   rights  to  copy,  distribute  and  modify  the
          Programs are as set out in the Sonera Public  License,
          a  copy  of  which  can be found in file "LICENSE". By
          downloading the  files  containing  the  Programs  you
          accept the terms and conditions of the Public License.
          You do not have to accept these terms and  conditions,
          but  unless  you  do  so you have no rights to use the
          Programs.

    The  Original  Code is an implementation of the FIPA Message
    Representation in Bit-Efficient Encoding.

    The  Initial  Developer  of  the  Original  Code  is  Sonera
    Corporation. Portions created by Sonera Corporation  or  its
    subsidiaries  are  Copyright  (c)  Sonera  Corporation.  
    All Rights Reserved.

    Contributor(s):
     (add contributor names here)

====================================================================*/

package sonera.fipa.acl;

import sonera.fipa.util.ByteArray;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.util.Enumeration;

import jade.core.AID;
import jade.lang.acl.*;


/**
 * ACLEncoder implements an encoder for bit-efficient ACLMessages.
 * 
 * @author Heikki Helin, Mikko Laukkanen
 */
public class ACLEncoder implements ACLConstants {
	/** 
	 * as Conversion between communicative acts (legacy <-> 
	 * bit-efficient) 
	 */
	private static ACLPerformatives as;

	/** buf Buffer to which the bit-efficient message is generated */
	private ByteArray buf = new ByteArray(1024);

	/** bd Bit-efficient date */
	private BinDate bd = new BinDate(); 

	/** bn Bit-efficient Number */
	private BinNumber bn = new BinNumber();

	/** ct Codetable */
	private EncoderCodetable ct;

	/** size Size of the code table */
	private int size;

	/** baseCoding  Default coding scheme (with or without codetables) */
	private byte baseCoding;

	/**
 	 * currCoding Coding scheme for current message (might be 
	 * different than base_coding, if "0xFC" coding is used 
	 */
	private byte currCoding;

	/** ex Expression parser  */
	private ExprParser ex = new ExprParser();

	/**
	 * Constructor for the encoder. 
         * Initialises the ACL encoder with no codetable coding scheme.
	 */
	public ACLEncoder() {
		baseCoding = ACL_BITEFFICIENT;
		initialize(0);
	}

	/**
	 * Constructor for the encoder.
	 * Initializes the ACL encoder with a codetable.
	 * @parameter sz the size for the codetable in bits 
         *		(between 8 and 16)
	 */
	public ACLEncoder(int sz) {
		baseCoding = (sz>0)
			      ? ACL_BITEFFICIENT_CODETABLE
			      : ACL_BITEFFICIENT;
		initialize(sz);
	}

	/**
	 * Constructor for the encoder.
	 * Initializes the ACL encoder with a codetable
	 *  @parameter sz the size for the codetable in bits.
	 *  @parameter ct the codetable to be used in encoding process.
	 * FIXME: remove sz parameter, and add getSize to EncoderCodetable!
	 */
	public ACLEncoder(int sz, EncoderCodetable ct) {
		this.ct = ct;
		size = sz;
		as = new ACLPerformatives();
	}

	private void initialize(int sz) {
		ct = (baseCoding == ACL_BITEFFICIENT || sz == 0) 
			? null
			: new EncoderCodetable(sz);
		size = sz;
		as = new ACLPerformatives();
	}

	/**
	 * Returns the codetable associated with this encoder
	 */
	public EncoderCodetable getCodeTable() {
		return ct;
	}

	/**
	 * Encodes an ACL message.
	 * @param m Message to encode
	 */
	public ByteArray encode(ACLMessage m) {
		currCoding = baseCoding;
		return outputMessage(m);
	}

	/**
	 * Encodes an ACL message.
	 * @param m Message to encode
	 * @param c Coding scheme (ACL_BITEFFICIENT_CODETABLE or
	 *		ACL_BITEFFICIENT_NO_CODETABLE)
	 */
	public ByteArray encode(ACLMessage m, byte c) {
		currCoding = c;
		return outputMessage(m);
	}
	/*
	 *
	 */
	private ByteArray outputMessage(ACLMessage m) {
		return (outputMessage(m, ACL_MSG_CONTENT_TYPE_STRING));
	}
	/*
	 *
	 */
	private ByteArray outputMessage(ACLMessage m, int content_type) {
		String s;
	 	/* Output header */
		buf.reset();
		buf.add(currCoding);
		buf.add(ACL_VERSION);
		
		/* Output message type (communicative act) */
		dumpMsgType(m.getPerformative());

		/* Output message parameters */

		dumpSender(m.getSender(), ACL_MSG_PARAM_SENDER);
		dumpAIDList(m.getAllReceiver(), ACL_MSG_PARAM_RECEIVER); 
		if ((s=m.getEncoding())!=null && s.length() > 0)
			dumpParam(s, ACL_MSG_PARAM_ENCODING);
		if ((s=m.getConversationId())!=null && s.length() > 0)
			dumpParam(s, ACL_MSG_PARAM_CONVERSATION_ID);
		dumpAIDList(m.getAllReplyTo(), ACL_MSG_PARAM_REPLY_TO); 
		dumpDate(m.getReplyBy());

		dumpContent(m, content_type);
		if ((s=m.getInReplyTo())!=null && s.length() > 0)
			dumpParam(s, ACL_MSG_PARAM_IN_REPLY_TO);
		if ((s=m.getReplyWith())!=null && s.length() > 0)
			dumpParam(s, ACL_MSG_PARAM_REPLY_WITH);
		if ((s=m.getLanguage())!=null && s.length() > 0)
			dumpParam(m.getLanguage(), ACL_MSG_PARAM_LANGUAGE);
		if ((s=m.getOntology())!=null && s.length() > 0)
			dumpParam(s, ACL_MSG_PARAM_ONTOLOGY);
		if ((s=m.getProtocol())!=null && s.length() > 0)
			dumpWordParam(s, ACL_MSG_PARAM_PROTOCOL);


		/* Possible user defined message parameters */
		dumpAllUserDefinedParameters(m);

		/* Write end-of-message marker */
		buf.add(ACL_END_OF_MSG);
		return buf;
	}
	/*
	 * Output message type (performative)
	 * FIXME: This routine cannot handle userdefined performatives.
	 */

	private void dumpMsgType(int p) {

		byte b = as.getCACode(p);
		buf.add(b);
	}

	/*
	 * Output DateTimeToken
	 */
	private void dumpDate(String s) {
		if (s == null || s.length() < 1) {
			return;
		}
		byte typeDG = (byte) (bd.containsTypeDg(s) ? 0x01 : 0x00);

		/* Output message parameter code */
		buf.add(ACL_MSG_PARAM_REPLY_BY);

		/* Output DateTimeToken id field (0x20-0x21) */
		buf.add((byte) (ACL_DATE_FOLLOWS + typeDG));	

		/* Output the Date */
		buf.add(bd.toBin (s), ACL_DATE_LEN);

		/* Output (possible) type designator */
		if (typeDG == 0x01) {
			buf.add ((byte) s.charAt(s.length()-1));
		}
	}
	/*
	 * Output a number
	 */
	private void dumpNumber(ByteArray _bx, byte h) {
		if (_bx.length() < 1) {
			return;
		}
		buf.add((byte) (ACL_DECNUM_FOLLOWS | h));
		ByteArray bx = bn.toBin(_bx);
		buf.add(bx.get(), bx.length());			
	} 

	private void dumpString(String s) {
		dumpString(s, (byte) 0);
	}

	private void dumpString(String s, byte h) {
		if (s == null || s.length() < 1) {
			return;
		}

		/* Check if the string is in codetable */
		int code = (currCoding == ACL_BITEFFICIENT) 
			   ? -1 
			   : ct.lookup(s);
		if (code == -1) {
			/*
			 * A new string. We add this to the codetable,
		 	 * iff we are using ACL_BITEFFICIENT_CODETABLE
			 * coding scheme.
			 */
			if (currCoding == ACL_BITEFFICIENT_CODETABLE) {
				ct.insert(s);
			}
			buf.add((byte) (ACL_NEW_WORD_FOLLOWS | h));
			buf.add(s.getBytes(), s.length());
			buf.add(ACL_END_OF_PARAM);
		} else {
			/* Found from codetable */
			buf.add((byte) (ACL_CT_WORD_FOLLOWS | h));
			outputCode(code);
		}
	}
	/**
	 * Output index to codetable. If the size of the codetable is 2^8,
	 * we output only one byte, otherwise two bytes.
	 * @param n Index to output
	 */
	private void outputCode(int n) {
		if (size > 8) {
			buf.add((byte) ((n >> 8) & 0xff));
			buf.add((byte) (n & 0xff));
		} else { 
			buf.add((byte) n);
		}
	}

	private void outputLong(int n) {
		buf.add((byte) ((n >> 24) & 0xff));
		buf.add((byte) ((n >> 16) & 0xff));
		buf.add((byte) ((n >> 8) & 0xff));
		buf.add((byte) (n & 0xff));
	}

	/**
	 * Output message sender (:sender)
	 * @param s Sender as FIPA97 agent name (i.e., foo@somewhwere)
	 * @param t Code for sender message parameter
	 */
	private void dumpSender(AID  _aid, byte t) {
		if (_aid == null) {
			return;
		}
		buf.add(t);
		dumpAID(_aid);
	}

	/**
	 * Output message receiver (:receiver)
	 * @param ag Group of agent names
	 * @param t Code for receiver message parameter
	 */

	private void dumpAIDList(java.util.Iterator i, byte t) {
		if (i != null && i.hasNext()) {
			buf.add(t);
			while(i.hasNext()) 
				dumpAID((AID)i.next());
			buf.add(ACL_END_OF_COLLECTION);
		}
	}

	/**
	 * Output one agent name
	 */

	private void dumpAID(AID aid) {
		Iterator addrs = aid.getAllAddresses();
		Iterator rslvrs = aid.getAllResolvers();

		/* Start of agent-identifier */
		buf.add(ACL_AID_FOLLOWS);

		/* Agent name (i.e., :name parameter) */
		dumpString(aid.getName());

		/* (Optional) addresses (UrlCollection) */
		if (addrs != null && addrs.hasNext()) {
			buf.add(ACL_AID_ADDRESSES);
			while(addrs.hasNext()) 
				dumpString((String)addrs.next());
			buf.add(ACL_END_OF_COLLECTION);
		}

		/* (Optional) resolvers (AgentIdentifierCollection) */
		if (rslvrs != null && rslvrs.hasNext()) {
			buf.add(ACL_AID_RESOLVERS);
			while(rslvrs.hasNext()) {
				dumpAID((AID)rslvrs.next());
			}
			buf.add(ACL_END_OF_COLLECTION);
		}

		/* (optional) UserDefinedSlots */
		java.util.Properties uds = aid.getAllUserDefinedSlot();
		Enumeration e = uds.propertyNames();
		String t;
		while(e.hasMoreElements()) {
			t = (String)e.nextElement();
			buf.add(ACL_AID_USERDEFINED);
			dumpString(t);
			dumpString(uds.getProperty(t));
		}
		buf.add(ACL_END_OF_COLLECTION);
	}


	private void dumpAllUserDefinedParameters(ACLMessage m) {
		java.util.Properties u = m.getAllUserDefinedParameters();
		if (u == null) return;
		Enumeration e = u.propertyNames();
		String t;
		while (e.hasMoreElements()) {
			buf.add(ACL_NEW_MSGPARAM_FOLLOWS);
			t = (String)e.nextElement();
			dumpString(t);
			dumpString(u.getProperty(t));
		}
	}

	/**
	 * Dump <tt>:protocol</tt> message parameter. 
	 * <tt>:protocol</tt> is the only message parameter (currently)
	 * that cannot take expression as a value, only Word.
	 *
	 * @param s Message parameter to output
	 * @param t Code for message parameter name (:ontology, :language,...)
	 */
	private void dumpWordParam(String s, byte t) {
		if (s == null || s.length() < 1) { return; }
		/* Output appropriate message parameter code */
		buf.add(t);
		dumpString(s);
	}
	/**
	 * Dump message parameter
	 * @param s Message parameter to output
	 * @param t Code for message parameter name (:ontology, :language,...)
	 */
	private void dumpParam(String s, byte t) {

		/* Output appropriate message parameter code */
		buf.add(t);

		/* Check if the value is expression */
		if (false && s.charAt(0) == '(') {
			ex.fromString(s);
		} else { 
			dumpString(s);
		}
	}

	private void dumpContent(ACLMessage m, int cntType) {
		if (m == null) return;
		switch(cntType) {
		case ACL_MSG_CONTENT_TYPE_STRING:
			if (m.getContent() == null) return;
			buf.add(ACL_MSG_PARAM_CONTENT);
			dumpString((String)m.getContent());
			break;
		case ACL_MSG_CONTENT_TYPE_OBJECT:
			buf.add(ACL_MSG_PARAM_CONTENT);

			try {
				if (m.getContentObject() == null) return;
				dumpString((String)m.getContentObject());
			} catch (Exception e) {
			}

		default:
			return;
		}

	}

	private void dumpBLE(ByteArray b) {
		int len = b.length();
		if (len < 256) {
			buf.add(ACL_NEW_BLE_STR8_FOLLOWS);
			buf.add((byte) (len & 0xff));
		} else if (len < 65536) {
			buf.add(ACL_NEW_BLE_STR16_FOLLOWS);
			outputCode(len);
		} else { // len < 2^32...hopefully...
			buf.add(ACL_NEW_BLE_STR32_FOLLOWS);
			outputLong(len);
		}
		buf.add (b);
	}

	private class ExprParser {
		private int level;
		private int index;
		private int len;
		private ByteArray ba;
		private String _str;
 		byte l;

		public ExprParser() {
			ba = new ByteArray();
		}

		public void fromString(String str) {
			byte b;
			level = index = 0; 
			l = 0;
			_str = str;
			len = _str.length();
			while ((b = getChar()) != -1) {				
				parse(b);
			}
			if (level > 0) {
				System.err.println ("Invalid expression");
			}
			if (l > 0) {
				buf.add(l);
			}
		}

		private void parse(byte b) {
			if (b == '(') {
				if (l == ACL_EXPR_LEVEL_DOWN || 
                                    l == ACL_EXPR_LEVEL_UP) {
					buf.add(l);
				}
				l = ACL_EXPR_LEVEL_DOWN;
				++level;
			} else if (b == ')') {
				if (l == ACL_EXPR_LEVEL_DOWN || 
 				    l == ACL_EXPR_LEVEL_UP) {
					buf.add(l);
				}
				l = ACL_EXPR_LEVEL_UP;
				--level;
			} else if (b <= ' ') {
				/*
				 * Skip white space
				 */
			} else if (b == '\"') {
				/*
				 * StringLiteral
				 */
				System.out.println("l = " + Integer.toHexString (l));
				byte temp = l; 
				String s = getString(b);
				System.out.println("l = " + Integer.toHexString (l));
				if (l != temp){
					dumpString(s, temp);
				} else {
					dumpString(s, l);
				}

				l = 0;
			} else if (b == '#') {
				/*
				 * ByteLenghtEncoded String
				 */
				System.err.println("ByteLenEncoded not implemented");
//				System.exit(1);
			} else if ((b >= '0' && b <= '9') || b == '-') {
				/*
				 * Number
				 */
				dumpNumber(getNumber(b), l);
				l = 0;
			} else if (b > 0x20) {
				/*
				 * Word
				 */
				dumpString(getWord(b), l);
				l = 0;
			} else {
				// FIXME
				System.err.println("Unknown char " + b);
			}
		}

		private ByteArray getNumber(byte b) {
			ba.reset();
			ba.add(b);
			while ((b = getChar()) != -1) {
				if (b >= '0' && b <= '9' || b == 'e' || 
				    b == '.' || b == 'E' || b == '+' || 
				    b == '-')  {
					ba.add(b);
				} else { 
					--index; 
					break; 
				}
			}
			return (ba);
		}

		private String getWord(byte b) {
			ba.reset();
			ba.add(b);
			while ((b = getChar()) != -1) {
				if (b > 0x020 && b != ')' && b != '(' ) {
					ba.add(b);
				} else { 
					--index; 
					break; 
				}
			}
			return new String (ba.get(), 0, ba.length());
		}

		/**
		 * Parse a StringLiteral
		 * Syntax for StringLiteral: <br>
	 	 * StringLiteral = "\"" ([~"\""]|"\\\"")* "\""
		 *
		 * @param b Previously parsed byte
		 * @returns Parsed StringLiteral
		 */
		private String getString(byte b) {
			ba.reset();
			byte prev = 0;
			ba.add(b);
			while ((b = getChar()) != -1) {
				if (b == '\\' && prev == '\\') { 
					ba.add(b); 
					prev = 0; 
					continue;
				} else if (b == '"' && prev == '\\') {
					ba.add(b); 
				} else if (b != '"') {
					ba.add(b);
				} else { 
					ba.add(b); 
					break; 
				} 
				prev = b;
			}
			if (b == -1){
				System.out.println 
					("String not ended correctly, treating as word");
				index--;
				l = ACL_EXPR_LEVEL_DOWN;
				return new String (ba.get(), 0, ba.length()-1);
			}
			return new String (ba.get(), 0, ba.length());
		}	

		private byte getChar() {
			return (index < len) 
				? (byte) _str.charAt(index++) 
				: -1;
		}
	}
}
