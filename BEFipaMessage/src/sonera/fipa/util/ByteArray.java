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

package sonera.fipa.util;

/**
 * Dynamic byte array.
 */
public class ByteArray {
	private byte[] data;
	private int alloc, used;
	private static final int grow_size = 100;
	private static final int def_initsize = 100;
	/**
	 * Initializes the ByteArray with size 100 
	 */
	public ByteArray() { initialize(def_initsize); }
	/**
	 * Initialized the ByteArray with given size
	 * @param init_size Initial size for the array.
	 */
	public ByteArray(int init_size) { initialize(init_size); }
	/**
	 * Clear this array
	 */
	public void reset() { used = 0; }
	/**
	 * Returns the lenght of this array
	 */
	public int length() { return used; }
	/**
	 * Returns this array
	 */
	public byte[] get() { return data; }
	/**
	 * Add a byte to this array.
	 * @param b byte to add
	 */
	public ByteArray add(byte b) { 
		data[used++] = b;
		if (used == alloc) grow(grow_size);
		return this;
	}

	/**
	 * Add a byte to this array is specified position.
	 * @param b byte to add
	 * @param pos position
	 */
	public ByteArray addToPos(byte b, int pos) {
		if (pos >= alloc) grow(pos+1);
		if (pos > used) used = pos+1;
		data[pos] = b;
		return this;
	}

	/**
	 * Add a array of bytes to this array
	 * @param b Byte array to add
	 * @param len Lenght of byte array to add.
	 */
	public ByteArray add(byte[] b, int len) {
		if (used+len >= alloc) grow(len+grow_size);
		System.arraycopy(b,0,data,used,len);
		used += len;
		return this;
	}
	/**
	 * Add a ByteArray to this array
	 * @param b ByteArray to add
	 */
	public ByteArray add(ByteArray b) { return add(b.get(), b.length()); }

	private void grow(int x) {
		alloc += x;
		byte [] n = new byte[alloc];
		System.arraycopy(data, 0, n, 0, used);
		data = n;
	}
	private void initialize(int sz) {
		data = new byte[sz];
		alloc = sz;
		used = 0;
	}
}
