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
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;


import jade.lang.acl.*;
import jade.core.AID;









/**
 * InputStream that reads fipa-bitefficient-std coded ACL messages from
 * given InputStream.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */

public class ACLInputStream extends BufferedInputStream 
	implements ACLConstants {
	/**
	 * as Conversion between communicative acts (legacy <-> 
	 * bit-efficient) 
	 */
	private static ACLPerformatives as;

	/** ct Codetable */
	private DecoderCodetable ct;

	/** bn Bit-efficient Number */
	private BinNumber bn = new BinNumber();

	/** size Size of the codetable */
	private int size;

	/** ba Buffer for parsing tokens */
	private ByteArray ba = new ByteArray();

	/** bb Buffer for parsing numbers */
	private ByteArray bb = new ByteArray(32);

	/** m ACLMessage to which the parsed message is stored */
	private ACLMessage m;

	/** coding Coding scheme (with or without codetables) */
	private int coding;

	/** ex Expression parser */
	private ExprParser ex = new ExprParser();

	/**
	 * Initialize the ACLInputStream. If this constructor is used,
	 * the stream assumes that all messages are coded without 
	 * codetables.
	 *
	 * @param i The InputStream from where the messages are read.
 	 */
	public ACLInputStream(InputStream i) {
		super(i);
		coding = ACL_BITEFFICIENT;
		initialize(8);
	}
	/**
	 * Initialize the ACLInputStream and associated codetable.
	 *
	 * @param i The InputStream from where the messages are read.
	 * @param sz The size of the codetable (in bits)
	 */
	public ACLInputStream(InputStream i, int sz) {
		super(i);
		coding = ACL_BITEFFICIENT_CODETABLE;
		initialize(sz);
	}
	/**
	 * FIXME: Remove size and add getSize to DecoderCodetable!
	 */
	public ACLInputStream (InputStream i, int sz, DecoderCodetable ct) {
		super (i);
		coding = ACL_BITEFFICIENT_CODETABLE;
		size = sz;
		initialize(size);
		this.ct = ct;
	}

	public void initialize(int sz) {
		ct = (coding==ACL_BITEFFICIENT) ? null : new DecoderCodetable(sz);
		size = sz;
		m = new ACLMessage(0 );
		as = new ACLPerformatives();
	}

	public DecoderCodetable getCodeTable() {
		return ct;
	}
	/**
	 * Reads an ACL message from the input stream.
	 * @returns The ACL message read.
	 */
	public ACLMessage readMsg() throws IOException,ACLCodec.CodecException  {

		m.reset();

		if (getCoding(getByte()) < 0) 
			throw new ACLCodec.CodecException ("Unsupported coding", null);
		if (getVersion(getByte()) < 0) 
			throw new ACLCodec.CodecException ("Unsupported version", null);
		if (getType(getByte()) < 0) 
			throw new ACLCodec.CodecException ("Unsupported type", null);
		while (getMsgParam() != -1);
		return m;
	}
	/**
	 * Check the first byte of the message.
	 * @return 0 if the first byte is supported message type, -1 otherwise.
	 */
	private int getCoding(byte b) {
		/*
	 	 * FIXME: We are not using this...
	 	 */
		return (b < ACL_BITEFFICIENT || 
			b > ACL_BITEFFICIENT_NO_CODETABLE) ? -1 : 0;
	}
	/**
	 * Check the version number of coding scheme. This implementation
	 * supports only version 1.0
	 */
	private int getVersion(byte b) {
		return (b != ACL_VERSION) ? -1: 0;
	}
	/**
	 * Handle message type (communicative act)
	 */
	private int getType(byte b) throws ACLCodec.CodecException  {
		if (b == -1) return -1;
		if (b != ACL_NEW_MSGTYPE_FOLLOWS) {

			m.setPerformative (as.getCA (b));

		} else {
			throw new ACLCodec.CodecException ("Can t handle user defined performatives", null);
		}
		return 0;
	}
	private int getMsgParam() throws IOException, ACLCodec.CodecException  {
		byte b = getByte();
		switch(b) {
		case ACL_END_OF_MSG:
			return -1;
		case ACL_MSG_PARAM_SENDER:
			m. setSender (getAID());
			return 0;
		case ACL_MSG_PARAM_RECEIVER:
			getReceivers();
			return 0;
		case ACL_MSG_PARAM_CONTENT:
			m.setContent((String)getParamX());
			return 0;
		case ACL_MSG_PARAM_REPLY_WITH:
			m.setReplyWith(getParam());
			return 0;
		case ACL_MSG_PARAM_REPLY_BY:
			m.setReplyBy(getParam());
			return 0;
		case ACL_MSG_PARAM_IN_REPLY_TO:
			m.setInReplyTo(getParam());
			return 0;
		case ACL_MSG_PARAM_REPLY_TO:
			getRepliesTo();
			return 0;
		case ACL_MSG_PARAM_LANGUAGE:
			m.setLanguage(getParam());
			return 0;
		case ACL_MSG_PARAM_ONTOLOGY:
			m.setOntology(getParam());
			return 0;
		case ACL_MSG_PARAM_PROTOCOL:
			m.setProtocol(getParam());
			return 0;
		case ACL_MSG_PARAM_ENCODING:

			m.setEncoding(getParam());

			return 0;
		case ACL_MSG_PARAM_CONVERSATION_ID:
			m. setConversationId (getParam());
			return 0;
		case ACL_NEW_MSGPARAM_FOLLOWS:

			m.addUserDefinedParameter(getParam(), getParam());

			return 0;
		}
		throw new ACLCodec.CodecException ("Unknown component or something like that", null);
	}			
	private void getReceivers() throws IOException, ACLCodec.CodecException  {
		AID  _aid;
		while ((_aid=getAID()) != null) {
			m. addReceiver (_aid);
		}
	}
	private void getRepliesTo() throws IOException, ACLCodec.CodecException  {
		AID  _aid;
		while((_aid=getAID()) != null)  {
			m. addReplyTo (_aid);
		}
	}
	private AID  getAID() throws IOException, ACLCodec.CodecException  {
		byte b = getByte();
		return (getAID(b));
	}
	private AID  getAID(byte t) throws IOException, ACLCodec.CodecException  {

		AID  _aid = new AID ();
		byte b;

		if (t != ACL_AID_FOLLOWS) {
			if (t == ACL_END_OF_COLLECTION) return null;
			throw new ACLCodec.CodecException ("not an agent-identifier", null);
		}
		/*
		 * Mandatory part of AID
		 */
		_aid.setName(getString());
		/*
		 * Optional part of AID
		 */
		while ((b=getByte())!=ACL_END_OF_COLLECTION) {
			switch(b) {
			case ACL_AID_ADDRESSES:
				while((b=getByte())!=ACL_END_OF_COLLECTION)

					_aid.addAddresses(getRealString(b));

				break;
			case ACL_AID_RESOLVERS:
				while((b=getByte())!=ACL_END_OF_COLLECTION)

					_aid.addResolvers(getAID(b));

				break;
			case ACL_AID_USERDEFINED:
				String key = getString();
				String value = getString();

				_aid.addUserDefinedSlot(key,value);

				break;
			default:
				throw new ACLCodec.CodecException ("Unexpected stuff in agent-identifier", null);
			}
		}
		return _aid;
	}
	private byte getByte() throws IOException {
		return (byte)super.read();
	}
	byte _b[] = new byte[3];
	private int inputCode() throws IOException {
		int n;
		if (size > 8) {
			super.read(_b, 0, 2);
			n = (int)((_b[1]&0xff)+((_b[0]&0xff)<<8));
		} else {
			byte b0 = (byte)super.read();
			n = (int)(b0&0xff);
		}
		return n;
	}
	private String getParam() throws IOException {
		return ex.toText();
	}
	private Object getParamX() throws IOException {
		return ex.toText();
	}
	private String getString() throws IOException {
		byte type = getByte();
		return getRealString(type);
	}
	private String getRealString(byte type) throws IOException {		
		byte b, t;
		ba.reset();
		String s = null; 
		int i = 0, len = 0;
		switch(type) {
		case ACL_NEW_WORD_FOLLOWS: case ACL_NEW_STRING_FOLLOWS: 
			/* New word (or string) */
			do {
				ba.add(b=getByte());
			} while(b != ACL_END_OF_PARAM);
			s = new String(ba.get(),0,ba.length()).trim();
			if (coding == ACL_BITEFFICIENT_CODETABLE) {
				ct.insert(s);
			}
			break;
		case ACL_CT_WORD_FOLLOWS: case ACL_CT_STRING_FOLLOWS:

			/* Word from codetable */
			s = ct.lookup(inputCode());
			break;
		case ACL_ABS_DATE_FOLLOWS: case ACL_ABS_DATET_FOLLOWS:

			/* DateTimeToken */
			for (i = 0; i < ACL_DATE_LEN; ++i) ba.add(getByte());
			s = new BinDate().fromBin(ba.get());
			if ((type & 0x01) != 0x00) s += (char)getByte();
			break;
		case ACL_DECNUM_FOLLOWS: case ACL_HEXNUM_FOLLOWS:

			/* Number token */
			bb.reset();
			while(((b=getByte()) & 0x0f) != 0x00) bb.add(b);
			if (b != 0x00) bb.add(b);
			s = bn.fromBin(bb.get()); 
			break;
		case ACL_NEW_BLE_STR8_FOLLOWS:
			System.err.println("STR8");
			len = getByte();
			/* FIXME */
			byte [] b1 = new byte[1024];
			super.read(b1,0,len);
			s = new String(b1).trim();
			break;		
		case ACL_NEW_BLE_STR16_FOLLOWS:
			len = inputCode();
/*			super.read(b,0,len); */
			break;			
		case ACL_NEW_BLE_STR32_FOLLOWS:
/*
			for (i = 0; i < 4; ++i) b[i] = getByte();
			len = (int)((b[3]&0xff) + ((b[2]&0xff)<<8)+
				((b[1]&0xff)<<16) + ((b[0]&0xff)<<24));
			super.read(b,0,len);
*/
			break;
		case ACL_CT_BLE_STR_FOLLOWS:
			System.err.println("BLE's NOT IMPELMENTED YET");
			throw new IOException ("BLE's NOT IMPELMENTED YET");
		default:
			throw new IOException ("Unknown field: " +  
				new Integer(0).toHexString(type));
		}
		return (s);
	}
	private class ExprParser {
		int level;
		StringBuffer sb;
		public ExprParser() {
			sb = new StringBuffer(256);
		}
		public String toText() throws IOException {
			sb.delete(0, sb.length());
			level = 0;
			byte b, x; 

			boolean beginOfParameter = true;

			while ((b = getByte())!=-1) {
				if (b >= 0x40 && b < 0x60) {
					/* Level UP */
					sb.append(')');
					sb.append(' ');
					if (--level == 0) return sb.toString();
					b &= ~0x40; /* Clear the level bits */
				} else if (b >= 0x60 && b <0x80) {
					/* Level Down */
					sb.append(' ');
					sb.append('(');
					++level;
					b &= ~0x60; /* Clear the level bits */
				}
				if (b != 0) {
					
					if (!beginOfParameter)
						sb.append(' ');
					else
						beginOfParameter = false;
					sb.append(getRealString(b));
					if (level == 0) return sb.toString();
				}
			}
			return sb.toString();
		}
	}
}
