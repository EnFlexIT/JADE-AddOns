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
/**
 * fipa-bitefficient-std ACL message transport syntax related constants.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public interface ACLConstants {
        /** The version number of implemented coding scheme */
        public static final byte ACL_VERSION = (byte) 0x10;
        /** Message identifier (no codetable used) */
        public static final byte ACL_BITEFFICIENT = (byte) 0xFA;
        /** Message identifier (codetable used) */
        public static final byte ACL_BITEFFICIENT_CODETABLE = (byte) 0xFB;
        /** Message identifier (codetable used, no updates) */
        public static final byte ACL_BITEFFICIENT_NO_CODETABLE = (byte) 0xFC;
        /** End-of-message marker */
        public static final byte ACL_END_OF_MSG = (byte) 0x01;
        /** End-of-parameter marker */
        public static final byte ACL_END_OF_PARAM = (byte) 0x00;
        /** */
        public static final byte ACL_NEW_MSGTYPE_FOLLOWS = (byte) 0x00;
        /** */
        public static final byte ACL_NEW_MSGPARAM_FOLLOWS = (byte) 0x00;
        /** */
        public static final byte ACL_NEW_WORD_FOLLOWS = (byte) 0x10;
        /** */
        public static final byte ACL_CT_WORD_FOLLOWS = (byte) 0x11;
        /** */
        public static final byte ACL_DECNUM_FOLLOWS = (byte) 0x12;
        /** */
        public static final byte ACL_HEXNUM_FOLLOWS = (byte) 0x13;
        public static final byte ACL_NEW_STRING_FOLLOWS = (byte) 0x14;
        public static final byte ACL_CT_STRING_FOLLOWS = (byte) 0x15;
        public static final byte ACL_NEW_BLE_STR8_FOLLOWS = (byte) 0x16;
        public static final byte ACL_NEW_BLE_STR16_FOLLOWS = (byte) 0x17;
        public static final byte ACL_NEW_BLE_STR32_FOLLOWS = (byte) 0x19;
        public static final byte ACL_CT_BLE_STR_FOLLOWS = (byte) 0x18;
        /*
	 * Relative time (w/ +)
	 */
        public static final byte ACL_DATE_POS_FOLLOWS = (byte) 0x21;
        public static final byte ACL_DATET_POS_FOLLOWS = (byte) 0x25;
        /*
	 * Relative time (w/ -)
	 */
        public static final byte ACL_DATE_NEG_FOLLOWS = (byte) 0x22;
        public static final byte ACL_DATET_NEG_FOLLOWS = (byte) 0x26;
        /*
	 * Absolute time
	 */
        public static final byte ACL_ABS_DATE_FOLLOWS = (byte) 0x20;
        public static final byte ACL_ABS_DATET_FOLLOWS = (byte) 0x24;
        public static final byte ACL_DATE_LEN = 9;
        public static final byte ACL_AID_FOLLOWS = (byte) 0x02;
        public static final byte ACL_END_OF_COLLECTION = (byte) 0x01;
        public static final byte ACL_AID_ADDRESSES = (byte) 0x02;
        public static final byte ACL_AID_RESOLVERS = (byte) 0x03;
        public static final byte ACL_AID_USERDEFINED = (byte) 0x04;
        public static final byte ACL_EXPR_LEVEL_DOWN = (byte) 0x60;
        public static final byte ACL_EXPR_LEVEL_UP = (byte) 0x40;
        /*
	 * Bit-efficient ACL communicative acts
 	 */
        public static final byte ACL_ACCEPTPROPOSAL = (byte) 0x01;
        public static final byte ACL_AGREE = (byte) 0x02;
        public static final byte ACL_CANCEL = (byte) 0x03;
        public static final byte ACL_CFP = (byte) 0x04;
        public static final byte ACL_CONFIRM = (byte) 0x05;
        public static final byte ACL_DISCONFIRM = (byte) 0x06;
        public static final byte ACL_FAILURE = (byte) 0x07;
        public static final byte ACL_INFORM = (byte) 0x08;
        public static final byte ACL_INFORMIF = (byte) 0x09;
        public static final byte ACL_INFORMREF = (byte) 0x0a;
        public static final byte ACL_NOTUNDERSTOOD = (byte) 0x0b;
        public static final byte ACL_PROPAGATE = (byte) 0x0c;
        public static final byte ACL_PROPOSE = (byte) 0x0d;
        public static final byte ACL_PROXY = (byte) 0x0e;
        public static final byte ACL_QUERYIF = (byte) 0x0f;
        public static final byte ACL_QUERYREF = (byte) 0x10;
        public static final byte ACL_REFUSE = (byte) 0x11;
        public static final byte ACL_REJECTPROPOSAL = (byte) 0x12;
        public static final byte ACL_REQUEST = (byte) 0x13;
        public static final byte ACL_REQUESTWHEN = (byte) 0x14;
        public static final byte ACL_REQUESTWHENEVER = (byte) 0x15;
        public static final byte ACL_SUBSCRIBE = (byte) 0x16;
        /*
	 * Bit-efficient ACL message parameters
	 */
        public static final byte ACL_MSG_PARAM_SENDER = (byte) 0x02;
        public static final byte ACL_MSG_PARAM_RECEIVER = (byte) 0x03;
        public static final byte ACL_MSG_PARAM_CONTENT = (byte) 0x04;
        public static final byte ACL_MSG_PARAM_REPLY_WITH = (byte) 0x05;
        public static final byte ACL_MSG_PARAM_REPLY_BY = (byte) 0x06;
        public static final byte ACL_MSG_PARAM_IN_REPLY_TO = (byte) 0x07;
        public static final byte ACL_MSG_PARAM_REPLY_TO = (byte) 0x08;
        public static final byte ACL_MSG_PARAM_LANGUAGE = (byte) 0x09;
        public static final byte ACL_MSG_PARAM_ENCODING = (byte) 0x0a;
        public static final byte ACL_MSG_PARAM_ONTOLOGY = (byte) 0x0b;
        public static final byte ACL_MSG_PARAM_PROTOCOL = (byte) 0x0c;
        public static final byte ACL_MSG_PARAM_CONVERSATION_ID = (byte) 0x0d;
        /*
	 *
	 */
        public static final byte ACL_MSG_CONTENT_TYPE_STRING = (byte)0x00;
        public static final byte ACL_MSG_CONTENT_TYPE_OBJECT = (byte)0x01;
}
