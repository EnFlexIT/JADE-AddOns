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
import jade.lang.acl.*;
import jade.util.Logger;

public class DummyDecoder {
        
        private static Logger logger = Logger.getMyLogger(DummyDecoder.class.getName());
        
        public static void main (String[] args) {
                /*
		 * Initialize Bit-efficient ACL input stream.
	 	 */
                ACLInputStream in = null;
                if (args.length!=0) {
                        in = new ACLInputStream(System.in, new Integer(args[0]).intValue());
                } else {
                        in = new ACLInputStream(System.in);
                }
                int c = 0;
                try {
                        while (true) {
                                /*
				 * Read a bit-efficiently coded message
				 */
                                ACLMessage m = in.readMsg();
                                /*
				 * And dump it to stdout
				 */
                                System.out.println(m.toString());
                                ++c;
                        }
                } catch (Exception e) {
                		if(logger.isLoggable(Logger.WARNING))
                			logger.log(Logger.WARNING,e.getMessage());
                        e.printStackTrace();
                }
                if(logger.isLoggable(Logger.INFO))
                	logger.log(Logger.INFO,c+ " message(s) parsed");
        }
}
