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

import java.util.Date;

/**
 * BinDate implements Bitefficient representation of "DateTimeToken"
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class BinDate extends BinRep implements ACLConstants {
	private static final byte T_POS = 8;
	private static final int MAX_DATE_LEN = 32;
	public BinDate() {}

	/**
	 * Converts ASCII representation of Date to bit-efficient 
	 * representation.
	 *
	 * @parameter s String containing ASCII representation of date
	 *
	 * @returns Bit-efficient representation of supplied date.
	 */
	public byte[] toBin(String s) {
		byte[] b = new byte[MAX_DATE_LEN];
		byte d;
		int x = s.length(), j = 0;
		if (s.charAt(T_POS) != 'T' && s.charAt(T_POS) != 't') 
			return null;

		/* First year, month, & day */
		for (int i = 0; i < 8; i+=2) {
			d = (byte)(encode(s.charAt(i)) << 4);
			d |= (encode(s.charAt(i+1)) & 0x0f);
			b[j++] = d;
		}

		/* Then Hours, Minutes, Seconds, and Milliseconds */
		for (int i = 9; i < 17; i+=2) {
			d = (byte)(encode(s.charAt(i)) << 4);
			d |= (encode(s.charAt(i+1)) & 0x0f);
			b[j++] = d;
		}
		b[j++] = (byte)(encode(s.charAt(17)) << 4);
		return b;
	}		
	/**
	 *
	 */
	private char[] c = new char[ACL_DATE_LEN*2+2];

	/**
	 * Converts bit-efficient Date to String.
	 * 
	 * @parameter b Bit-efficient date
	 * @returns String containing ASCII representation of supplied date.
	 */
	public String fromBin(byte[] b) {
		int i = 0, j = 0;
		for (i = 0; i < ACL_DATE_LEN; ++i) {
			if (j == T_POS) c[j++] = 'T';
			c[j] = (char)decode((b[i]>>4)&0x0f);
			c[j+1] = (char)decode(b[i]&0x0f);
			j += 2;
		}
		c[j]=0;
		return (new String(c).trim());
	}
	/**
	 * Checks whether there's type designator in Date String
	 * @parameter s String date to check
	 * @returns true if there's type designator present, false otherwise
	 */
	public static boolean containsTypeDg(String s) {
		char a = s.charAt(s.length()-1);
		return ((a >= 'a' && a <= 'z') || (a >= 'A' && a <= 'Z'));
	}
}
