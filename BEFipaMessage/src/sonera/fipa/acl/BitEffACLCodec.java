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


import java.io.*;
import jade.lang.acl.ACLCodec;
import jade.lang.acl.ACLMessage;

/**
 * This class implements the FIPA Bit-efficient codec for ACLMessages.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class BitEffACLCodec implements ACLCodec {
        public static final String _name = jade.domain.FIPANames.ACLCodec.BITEFFICIENT;

        private ACLEncoder e;
        private ByteArray ba;

        /**
	 * Constructor for the codec.
	 */
        public BitEffACLCodec() {
                initialize(0);
        }
        /**
	 * Constructor for the codec.
	 */
        public BitEffACLCodec(int sz) {
                initialize(sz);
        }
        private void initialize(int sz) {
                e = new ACLEncoder(sz);
                ba = new ByteArray();
        }
        /**
	 * @see ACLCodec#decode(byte[] data)
	 */
        public ACLMessage decode(byte[] data) throws ACLCodec.CodecException {






                InputStream i = new ByteArrayInputStream(data);
                ACLInputStream ai = new ACLInputStream(i);

                try {
                        return (ai.readMsg());
                } catch (IOException e) {
                        throw new ACLCodec.CodecException("IOException:"+e, null);
                } catch (Exception e) {
                        throw new ACLCodec.CodecException("Exception:"+e, null);
                }
        }
        public void write(ACLMessage msg) {

        }
        /**
	 * @see ACLCodec#encode(ACLMessage msg)
	 */
        public byte[] encode(ACLMessage msg) {
                try {
                        ba = e.encode(msg);
                } catch (Exception e) {}
                return(ba.get());
        }
        public ByteArray _encode(ACLMessage msg) {
                try {
                        ba = e.encode(msg);
                } catch (Exception e) {}
                return(ba);
        }

        /**
	 * @return the name of this encoding according to the FIPA 
	 *         specifications
	 */
        public String getName() { return _name; }
}
