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


/**
 * Bitefficient number
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class BinNumber extends BinRep {
        private static final byte TT_INT = (byte)0x00;
        private static final byte TT_FLOAT = (byte)0x01;
        private Integer iNumber;
        private Float fNumber;
        private ByteArray ba = new ByteArray(32);
        byte tag;

        public BinNumber() {}
        /**
	 * Initialize BinNumber from Integer
	 */
        public BinNumber(Integer i) {
                iNumber = i;
                tag = TT_INT;
        }
        /**
	 * Initialize BinNumber from Float
	 */
        public BinNumber(Float f) {
                fNumber = f;
                tag = TT_FLOAT;
        }
        /**
	 * Initialize BinNumber from encoded byte array
	 */
        public BinNumber(byte[] b) {
                String s = fromBin(b);
                tag = TT_INT;
                for (int i = 0; i < s.length() && tag == TT_INT; ++i) {
                        if (s.charAt(i) == 'e' || s.charAt(i) == '.')
                                tag = TT_FLOAT;
                }
                if (tag == TT_INT) iNumber = new Integer(s.trim());
                else fNumber = new Float(s);
        }
        public Object value() {
                return ((tag==TT_INT) ? (Object)iNumber : (Object)fNumber);
        }
        /**
	 * Converts String representation to ByteArray
	 */
        public ByteArray toBin(ByteArray b) {
                int x = b.length();
                byte d;
                byte[] bx = b.get ();
                ba.reset();
                for (int i = 0; i < x; i+=2) {
                        d = (byte) (encode (bx[i]) << 4);
                        if ((i+1)<x) d |= (encode(bx[i+1])&0x0f);
                        else d |= 0x00;
                        ba.add(d);
                }
                if ((x % 2) == 0) { // Even, additional 0x00 to the end.
                        ba.add ((byte) 0x00);
                }
                return ba;
        }
        /**
	 * Converts String representation to ByteArray
	 */
        public ByteArray toBin(String s) {
                int x = s.length(), j = 0;
                byte d;
                ba.reset();
                for (int i = 0; i < x; i+=2) {
                        d = (byte)(encode(s.charAt(i)) << 4);
                        if ((i+1) < x) {
                                d |= (encode(s.charAt(i+1)) & 0x0f);
                        } else d |= 0x00;
                        ba.add(d);
                }
                if ((x % 2) == 0) { // Even, additional 0x00 to the end.
                        ba.add ((byte) 0x00);
                }
                return ba;
        }
        public String fromBin(byte[] b) {
                char[] c = new char[64];
                int i = 0, j = 0;
                while((b[i]&0x0f) != 0x00) {
                        c[j] = (char)decode((b[i]>>4)&0x0f);
                        c[j+1] = (char)decode(b[i]&0x0f);
                        j += 2;
                        ++i;
                }
                if (b[i]!=0x00) c[j] = (char)decode((b[i]>>4)&0x0f);
                return (new String(c).trim());
        }
        public ByteArray fromBin(ByteArray bb) {
                ba.reset();
                byte b[] = bb.get ();
                int i = 0;
                while((b[i]&0x0f) != 0x00) {
                        ba.add(decode((b[i]>>4)&0x0f));
                        ba.add(decode((b[i]&0x0f)));
                        ++i;
                }
                if (b[i]!=0x00) {
                        ba.add(decode((b[i]>>4)&0x0f));
                }
                return ba;
        }
}
