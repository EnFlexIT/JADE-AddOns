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
import java.util.Date;

import jade.lang.acl.*;
import jade.core.AID;









/**
 *
 *
 * @author Heikki Helin, Mikko Laukkanen
 */

public class ACLDecoder implements ACLConstants {
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

	private int current;

	/**
	 * Initialize the ACLDecoder. If this constructor is used,
	 * all messages are decoded without codetables.
 	 */
	public ACLDecoder() {
		coding = ACL_BITEFFICIENT;
		initialize(8);
	}
	/**
	 * Initialize the ACLDecoder with a given codetable size
	 *
	 * @param sz The size of the codetable (in bits)
	 */
	public ACLDecoder(int sz) {
		coding = ACL_BITEFFICIENT_CODETABLE;
		initialize(sz);
	}
	/**
	 * Initialize the ACLDecoder with a given codetable
	 */
	public ACLDecoder (DecoderCodetable ct) {
		coding = ACL_BITEFFICIENT_CODETABLE;
		initialize(ct.getSize());
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
	 * Parses an ACL message from byte array
	 * @returns The ACL message read.
	 */
	public ACLMessage readMsg(byte [] inb) throws IOException,ACLCodec.CodecException  {

		m.reset();

		current = 0;
		if (getCoding(getByte(inb)) < 0) 
			throw new ACLCodec.CodecException ("Unsupported coding", null);
		if (getVersion(getByte(inb)) < 0) 
			throw new ACLCodec.CodecException ("Unsupported version", null);
		if (getType(getByte(inb)) < 0) 
			throw new ACLCodec.CodecException ("Unsupported type", null);
		while (getMsgParam(inb) != -1);
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
	private int getMsgParam(byte [] inb) throws IOException, ACLCodec.CodecException  {
		byte b = getByte(inb);
		switch(b) {
		case ACL_END_OF_MSG:
			return -1;
		case ACL_MSG_PARAM_SENDER:
			m. setSender (getAID(inb));
			return 0;
		case ACL_MSG_PARAM_RECEIVER:
			getReceivers(inb);
			return 0;
		case ACL_MSG_PARAM_CONTENT:
			m.setContent((String)getParamX(inb));
			return 0;
		case ACL_MSG_PARAM_REPLY_WITH:
			m.setReplyWith(getParam(inb));
			return 0;
		case ACL_MSG_PARAM_REPLY_BY:
			m.setReplyByDate(getDate(inb));
			return 0;
		case ACL_MSG_PARAM_IN_REPLY_TO:
			m.setInReplyTo(getParam(inb));
			return 0;
		case ACL_MSG_PARAM_REPLY_TO:
			getRepliesTo(inb);
			return 0;
		case ACL_MSG_PARAM_LANGUAGE:
			m.setLanguage(getParam(inb));
			return 0;
		case ACL_MSG_PARAM_ONTOLOGY:
			m.setOntology(getParam(inb));
			return 0;
		case ACL_MSG_PARAM_PROTOCOL:
			m.setProtocol(getParam(inb));
			return 0;
		case ACL_MSG_PARAM_ENCODING:

			m.setEncoding(getParam(inb));

			return 0;
		case ACL_MSG_PARAM_CONVERSATION_ID:
			m. setConversationId (getParam(inb));
			return 0;
		case ACL_NEW_MSGPARAM_FOLLOWS:

			m.addUserDefinedParameter(getParam(inb), getParam(inb));

			return 0;
		}
		throw new ACLCodec.CodecException ("Unknown component or something like that", null);
	}			
	private void getReceivers(byte [] inb) throws IOException, ACLCodec.CodecException  {
		AID  _aid;
		while ((_aid=getAID(inb)) != null) {
			m. addReceiver (_aid);
		}
	}
	private void getRepliesTo(byte [] inb) throws IOException, ACLCodec.CodecException  {
		AID  _aid;
		while((_aid=getAID(inb)) != null)  {
			m. addReplyTo (_aid);
		}
	}
	private AID  getAID(byte [] inb) throws IOException, ACLCodec.CodecException  {
		byte b = getByte(inb);
		return (getAID(b, inb));
	}
	private AID  getAID(byte t, byte [] inb) throws IOException, ACLCodec.CodecException  {

		AID  _aid = new AID ();
		byte b;

		if (t != ACL_AID_FOLLOWS) {
			if (t == ACL_END_OF_COLLECTION) return null;
			throw new ACLCodec.CodecException ("not an agent-identifier", null);
		}
		/*
		 * Mandatory part of AID
		 */
		_aid.setName(getString(inb));
		/*
		 * Optional part of AID
		 */
		while ((b=getByte(inb))!=ACL_END_OF_COLLECTION) {
			switch(b) {
			case ACL_AID_ADDRESSES:
				while((b=getByte(inb))!=ACL_END_OF_COLLECTION)

					_aid.addAddresses(getRealString(b, inb));

				break;
			case ACL_AID_RESOLVERS:
				while((b=getByte(inb))!=ACL_END_OF_COLLECTION)

					_aid.addResolvers(getAID(b, inb));

				break;
			case ACL_AID_USERDEFINED:
				String key = getString(inb);
				String value = getString(inb);

				_aid.addUserDefinedSlot(key,value);

				break;
			default:
				throw new ACLCodec.CodecException ("Unexpected stuff in agent-identifier", null);
			}
		}
		return _aid;
	}
	private byte getByte(byte [] inb) throws IOException {
		return (byte)inb[current++];
	}
	byte _b[] = new byte[3];
	private int inputCode(byte [] inb) throws IOException {
		int n;
		if (size > 8) {
			_b[0] = getByte(inb);
			_b[1] = getByte(inb);
			n = (int)((_b[1]&0xff)+((_b[0]&0xff)<<8));
		} else {
			byte b0 = (byte)getByte(inb);
			n = (int)(b0&0xff);
		}
		return n;
	}
	private String getParam(byte [] inb) throws IOException {
		return ex.toText(inb);
	}
	private Date getDate(byte [] inb) throws IOException {
		byte type = getByte(inb);
		ba.reset();
		for (int i = 0; i < ACL_DATE_LEN; ++i) ba.add(getByte(inb));
		String s = new BinDate().fromBin(ba.get());
		if ((type & 0x01) != 0x00) s += (char)getByte(inb);
		Date d = null;
		try {
			d = ISO8601.toDate(s);
		} catch (Exception e) {
			/* FIXME */
		}
		return d;
	}
	private Object getParamX(byte [] inb) throws IOException {
		return ex.toText(inb);
	}
	private String getString(byte [] inb) throws IOException {
		byte type = getByte(inb);
		return getRealString(type, inb);
	}
	private String getRealString(byte type, byte [] inb) throws IOException {		
		byte b, t;
		ba.reset();
		String s = null; 
		int i = 0, len = 0;
		switch(type) {
		case ACL_NEW_WORD_FOLLOWS: case ACL_NEW_STRING_FOLLOWS: 
			/* New word (or string) */
			do {
				ba.add(b=getByte(inb));
			} while(b != ACL_END_OF_PARAM);
			s = new String(ba.get(),0,ba.length()).trim();
			if (coding == ACL_BITEFFICIENT_CODETABLE) {
				ct.insert(s);
			}
			break;
		case ACL_CT_WORD_FOLLOWS: case ACL_CT_STRING_FOLLOWS:

			/* Word from codetable */
			s = ct.lookup(inputCode(inb));
			break;
		case ACL_ABS_DATE_FOLLOWS: case ACL_ABS_DATET_FOLLOWS:

			/* DateTimeToken */
			for (i = 0; i < ACL_DATE_LEN; ++i) ba.add(getByte(inb));
			s = new BinDate().fromBin(ba.get());
			if ((type & 0x01) != 0x00) s += (char)getByte(inb);
			break;
		case ACL_DECNUM_FOLLOWS: case ACL_HEXNUM_FOLLOWS:

			/* Number token */
			bb.reset();
			while(((b=getByte(inb)) & 0x0f) != 0x00) bb.add(b);
			if (b != 0x00) bb.add(b);
			s = bn.fromBin(bb.get()); 
			break;
		case ACL_NEW_BLE_STR8_FOLLOWS:
			System.err.println("STR8");
			len = getByte(inb);
			/* FIXME */
			byte [] b1 = new byte[1024];
/*			super.read(b1,0,len); */
			s = new String(b1).trim();
			break;		
		case ACL_NEW_BLE_STR16_FOLLOWS:
			len = inputCode(inb);
/*			super.read(b,0,len); */
			break;			
		case ACL_NEW_BLE_STR32_FOLLOWS:
/*
			for (i = 0; i < 4; ++i) b[i] = getByte(inb);
			len = (int)((b[3]&0xff) + ((b[2]&0xff)<<8)+
				((b[1]&0xff)<<16) + ((b[0]&0xff)<<24));
//			super.read(b,0,len);
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
		String s;
		ByteArray ba;
		public ExprParser() {
			ba = new ByteArray(512);
		}
		public String toText(byte [] inb) throws IOException {
			ba.reset();
			level = 0;
			byte b, x; 

			boolean beginOfParameter = true;

			while ((b = getByte(inb))!=-1) {
				if (b >= 0x40 && b < 0x60) {
					/* Level UP */
					ba.add((byte)')');
					ba.add((byte)' ');
					if (--level == 0) 
						return new String(ba.get(),0,ba.length()).trim();
					b &= ~0x40; /* Clear the level bits */
				} else if (b >= 0x60 && b <0x80) {
					/* Level Down */
					ba.add((byte)' ');
					ba.add((byte)'(');
					++level;
					b &= ~0x60; /* Clear the level bits */
				}
				if (b != 0) {
					
					if (!beginOfParameter)
						ba.add((byte)' ');
					else
						beginOfParameter = false;
					s = getRealString(b, inb);
					ba.add(s.getBytes(),s.length());
					if (level == 0) 
						return new String(ba.get(),0,ba.length()).trim();
				}
			}
			return new String(ba.get(),0,ba.length()).trim();
		}
	}
}
