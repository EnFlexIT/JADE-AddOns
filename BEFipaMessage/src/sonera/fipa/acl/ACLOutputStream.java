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
import sonera.fipa.util.ByteArray;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import jade.lang.acl.*;
/**
 * OutputStream that writes fipa-bitefficient-std coded messages into stream.
 * 
 * @author Heikki Helin, Mikko Laukkanen
 */
public class ACLOutputStream extends BufferedOutputStream {
        private ACLEncoder e;
        private ByteArray ba;
        /**
	 * Initialise the ACL output stream with given OutputStream.
	 * 
	 * @parameter o OutputStream to which message are written.
	 */
        public ACLOutputStream(OutputStream o) {
                super(o);
                initialize(0);
        }
        /**
	 * Initialise the ACL output stream with given OutputStream and 
  	 * codetable size.
	 *
	 * @parameter o OutputStream to which message are written.
	 * @parameter sz Size for the code table.
	 */
        public ACLOutputStream(OutputStream o, int sz) {
                super(o);
                initialize(sz);
        }
        public EncoderCodetable getCodeTable() {
                return e.getCodeTable();
        }
        public void initialize(int sz) {
                e = new ACLEncoder(sz);
                ba = new ByteArray();
        }
        /**
	 * Writes ACL message to output stream
	 * @param m Message to be written
	 */
        public void write(ACLMessage m) throws Exception {
                ba = e.encode(m);
                super.write(ba.get(),0,ba.length());
                super.flush();
        }
        /**
	 * Writes ACL message output stream using specified coding.
	 * @param m Message to be written 
	 * @param c Coding scheme (ACL_BITEFFICIENT_CODETABLE or
	 *		ACL_BITEFFICIENT_NO_CODETABLE)
	 */
        public void write(ACLMessage m, byte c) throws Exception {
                ba = e.encode(m, c);
                super.write(ba.get(),0,ba.length());
                super.flush();
        }
}
