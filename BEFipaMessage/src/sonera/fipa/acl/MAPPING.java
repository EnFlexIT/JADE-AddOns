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
import jade.lang.acl.ACLMessage;
/**
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public interface MAPPING {
        public static final int ACCEPTPROPOSAL = ACLMessage.ACCEPT_PROPOSAL;
        public static final int AGREE = ACLMessage.AGREE;
        public static final int CANCEL = ACLMessage.CANCEL;
        public static final int CFP = ACLMessage.CFP;
        public static final int CONFIRM = ACLMessage.CONFIRM;
        public static final int DISCONFIRM = ACLMessage.DISCONFIRM;
        public static final int FAILURE = ACLMessage.FAILURE;
        public static final int INFORM = ACLMessage.INFORM;
        public static final int INFORMIF = ACLMessage.INFORM_IF;
        public static final int INFORMREF = ACLMessage.INFORM_REF;
        public static final int NOTUNDERSTOOD = ACLMessage.NOT_UNDERSTOOD;
        public static final int PROPAGATE = ACLMessage.PROPAGATE;
        public static final int PROPOSE = ACLMessage.PROPOSE;
        public static final int PROXY = ACLMessage.PROXY;
        public static final int QUERYIF = ACLMessage.QUERY_IF;
        public static final int QUERYREF = ACLMessage.QUERY_REF;
        public static final int REFUSE = ACLMessage.REFUSE;
        public static final int REJECTPROPOSAL = ACLMessage.REJECT_PROPOSAL;
        public static final int REQUEST = ACLMessage.REQUEST;
        public static final int REQUESTWHEN = ACLMessage.REQUEST_WHEN;
        public static final int REQUESTWHENEVER = ACLMessage.REQUEST_WHENEVER;
        public static final int SUBSCRIBE = ACLMessage.SUBSCRIBE;
}
