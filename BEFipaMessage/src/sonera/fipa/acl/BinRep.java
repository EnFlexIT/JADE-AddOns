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
  * Conversion between ASCII numbers and bit-efficient numbers.
 * The coding is based on following rules:
 * <tt><br>
 * '0' = 0001 (0x01) <br>
 * '1' = 0010 (0x02) <br>
 * '2' = 0011 (0x03) <br>
 * '3' = 0100 (0x04) <br>
 * '4' = 0101 (0x05) <br>
 * '5' = 0110 (0x06) <br>
 * '6' = 0111 (0x07) <br>
 * '7' = 1000 (0x08) <br>
 * '8' = 1001 (0x09) <br>
 * '9' = 1010 (0x0a) <br>
 * '+' = 1100 (0x0c) <br>
 * 'e' = 1101 (0x0d) <br>
 * '-' = 1110 (0x0e) <br>
 * '.' = 1111 (0x0f) <br>
 * </tt>
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class BinRep {
        final static char[] ncodes = {
                ' ', '0', '1', '2', '3', '4', '5', '6',
                '7', '8', '9', '?', '+', 'e', '-', '.'
        };
        /**
	 * Converts one ASCII number to bit-efficient representation.
	 * The ASCII number to convert must be such number that conversion
	 * can be done (check the list above). If the ASCII number is not
	 * in given range, the result of this method is undefined (i.e.,
	 * no validity checks are done).
	 *
	 * @param i ASCII number to convert
 	 * @return Corresponding bit-efficient number
 	 */
        protected static byte encode(int i) {
                return (byte)((i != 'e' && i != 'E') ? (i+1)&0x0f : 0x0d);
        }
        /**
	 * Converts bit-efficient number to ASCII. The number to convert
	 * must be valid bit-efficient number (i.e., number between 
	 * 0x00-0x0f). No validity checks are done. 
	 *
	 * @param i bit-efficient number to convert
	 * @return Corresponding ASCII number
	 */
        protected static byte decode(int i) {
                return (byte)ncodes[i];
        }
}
