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
 * Mapping between JADE performative codes and bitefficient ones.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class ACLPerformatives implements ACLConstants {
        private static class pair {
                public byte be;
                public int orig;
                public String name;
                public pair(byte a, int b, String n) { be = a; orig = b; name = n;}
        }
        private static final pair x[] = {
                new pair((byte)0,-1, ""),
                new pair(ACL_ACCEPTPROPOSAL, MAPPING.ACCEPTPROPOSAL, "accept-proposal"),
                new pair(ACL_AGREE, MAPPING.AGREE, "agree"),
                new pair(ACL_CANCEL, MAPPING.CANCEL, "cancel"),
                new pair(ACL_CFP, MAPPING.CFP, "cfp"),
                new pair(ACL_CONFIRM, MAPPING.CONFIRM, "confirm"),
                new pair(ACL_DISCONFIRM, MAPPING.DISCONFIRM, "disconfirm"),
                new pair(ACL_FAILURE, MAPPING.FAILURE, "failure"),
                new pair(ACL_INFORM, MAPPING.INFORM, "inform"),
                new pair(ACL_INFORMIF, MAPPING.INFORMIF, "inform-if"),
                new pair(ACL_INFORMREF, MAPPING.INFORMREF, "inform-ref"),
                new pair(ACL_NOTUNDERSTOOD, MAPPING.NOTUNDERSTOOD, "not-understood"),
                new pair(ACL_PROPAGATE, MAPPING.PROPAGATE, "propagate"),
                new pair(ACL_PROPOSE, MAPPING.PROPOSE, "propose"),
                new pair(ACL_PROXY, MAPPING.PROXY, "proxy"),
                new pair(ACL_QUERYIF, MAPPING.QUERYIF, "query-if"),
                new pair(ACL_QUERYREF, MAPPING.QUERYREF, "query-ref"),
                new pair(ACL_REFUSE, MAPPING.REFUSE, "refuse"),
                new pair(ACL_REJECTPROPOSAL, MAPPING.REJECTPROPOSAL, "reject-proposal"),
                new pair(ACL_REQUEST, MAPPING.REQUEST, "request"),
                new pair(ACL_REQUESTWHEN, MAPPING.REQUESTWHEN, "request-when"),
                new pair(ACL_REQUESTWHENEVER,MAPPING.REQUESTWHENEVER, "request-whenever"),
                new pair(ACL_SUBSCRIBE, MAPPING.SUBSCRIBE, "subscribe"),
        };
        /**
	 * Returns FIPAOS code for performative
	 */
        public int getCA(byte b) {
                return (b < ACL_ACCEPTPROPOSAL || b > ACL_SUBSCRIBE)
                        ? -1 : (x[b].orig);
        }
        public int getCA(String b) {
                for (int i = 1; i <= ACL_SUBSCRIBE; ++i) {
                        if (x[i].name.equals(b)) {
                                return x[i].orig;
                        }
                }
                return -1;
        }
        /**
	 * Returns performative string
	 */
        public String getCAString (byte b) {
                return (b < ACL_ACCEPTPROPOSAL || b > ACL_SUBSCRIBE)
                        ? "" : (x[b].name);
        }
        /**
	 * Returns bit-efficient code for performative
	 * @param b JADE code for performative
	 */
        public byte getCACode(int b) {
                for (int i = 1; i <= ACL_SUBSCRIBE; ++i) {
                        if (x[i].orig == b) return x[i].be;
                }
                return (byte)-1;
        }
        public byte getCACode (String b) {
                for (int i = 1; i <= ACL_SUBSCRIBE; ++i) {
                        if (x[i].name.equals(b)) {
                                return x[i].be;
                        }
                }
                return (byte) -1;
        }
}
