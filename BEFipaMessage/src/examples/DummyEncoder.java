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
import sonera.fipa.acl.*;
import jade.lang.acl.*;
import jade.core.*;
import jade.util.Logger;

public class DummyEncoder {
        
        private static Logger logger = Logger.getMyLogger(DummyEncoder.class.getName());
        
        public static void main (String[] args) {
                int c = 0;
                ACLMessage aclMessage[] = new ACLMessage[100];
                /*
		 * We are reading from standard input
		 */
                BufferedReader in =
                        new BufferedReader(new InputStreamReader(System.in));
                String line = null;
                /*
	 	 * Initialize the ACL OutputStream
	 	 */
                ACLOutputStream os = null;
                if (args.length!=0) {
                        os = new ACLOutputStream(System.out,
                                new Integer(args[0]).intValue());
                } else {
                        os = new ACLOutputStream(System.out);
                }
                StringACLCodec sc = new StringACLCodec(in, null);
                try {
                        while(true) {
                                ACLMessage m = sc.decode();
//				byte [] __o = new byte[1800];
//				m.setContentObject(__o);
                                if(logger.isLoggable(Logger.INFO))
                                	logger.log(Logger.INFO,m.toString());
                                os.write(m);
                                ++c;
                        }
                } catch (Exception e) {
                        if(logger.isLoggable(Logger.WARNING))
                        	logger.log(Logger.WARNING,"-- Error: "+e);
                }
                                /*
				 * Here we assume that whole message is
				 * given in one line; stupid assumption,
				 * but better than nothing...
				 */
                                /*
				 * If we succesfully parsed the message,
				 * then we can write that to stdout
				 * using bit-efficient encoding.
				 */
                if(logger.isLoggable(Logger.INFO))
                	logger.log(Logger.INFO,c+" message(s) written to stdout");
        }
}
