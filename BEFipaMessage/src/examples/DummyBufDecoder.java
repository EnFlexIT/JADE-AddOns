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
import java.io.*;
import java.util.*;
import sonera.fipa.acl.*;
import sonera.fipa.util.*;
import jade.lang.acl.*;
import jade.util.Logger;

public class DummyBufDecoder {
	
		private static Logger logger = Logger.getMyLogger(DummyBufDecoder.class.getName());
        public static void main (String[] args) {
                /*
		 * Initialize Bit-efficient ACL input stream.
	 	 */
                ACLDecoder ad;
                ByteArray ba = new ByteArray();
                if (args.length!=0) {
                        ad = new ACLDecoder(new Integer(args[0]).intValue());
                } else {
                        ad = new ACLDecoder();
                }
                int c = 0;
                try {
                        byte b = 0;
                        while (b != -1) {
                                b = (byte)System.in.read();
                                if (b != -1)
                                        ba.add(b);
                        }
                } catch (Exception e) {
                       logger.log(Logger.WARNING,"Got exception:" +e.toString());
                }
                try {
                        while (true) {
                                /*
				 * Read a bit-efficiently coded message
				 */
                                ACLMessage m = ad.readMsg(ba.get());
                                /*
				 * And dump it to stdout
				 */
                                System.out.println(m.toString());
                                ++c;
                                System.exit(1);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
               logger.log(Logger.INFO,c+ " message(s) parsed");
        }
}
