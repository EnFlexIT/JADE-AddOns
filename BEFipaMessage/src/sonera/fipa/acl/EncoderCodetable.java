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
 * Implementation of codetable for fipa-bitefficient-std.
 *
 * @author Heikki Helin, Mikko Laukkanen
 */
public class EncoderCodetable {

	/** ct Hash table */
	private Entry[] ct;

	/* The beginning and the end of LRU list */
	private Entry first, last;

	/** primes Prime numbers for hashtable size */
	private final int primes[]=
		{263, 523, 1049, 2087, 4157, 8291, 16481, 32801, 65677};

	/** codes Available codes for new nodes */
	private int[] codes;
	private int size, current, cleanSize, nextCode, maxCode, hashSize;

	/** 
	 * useTableCodes Indicates whether LRU algorithm has been 
	 * used at least once.
	 */
	private boolean useTableCodes = false;

	/**
 	 * Initialize the code table with given size.
 	 * @param sz Size of the codetable in bits. According to FIPA
	 * specs, this shall be a number between 8 and 16. 
 	 */
	public EncoderCodetable(int sz) {
		/*
		 * Okay, this is not Java. We probably should throw
		 * an exception if the user is so stupid that he cannot
		 * give proper size for the codetable.
		 */
		initialize((sz>16) ? 16 : (((sz)<8) ? 8 : sz));
	}
	/**
	 * Initialize the codetable
	 */
	private void initialize(int sz) {
		/*
		 * Count the actual size (i.e., how many entries we can store)
		 */ 
		size = (2 << (sz-1));
		/*
		 * Count the number of entries we shall remove when the
		 * table is full.
		 */
		cleanSize = (size >> 3);

		/* Update funny pointer */
		current = cleanSize;

		/* First available code */
		nextCode = 0;

		/* Max. code */
		maxCode = size;
		codes = new int[size];
		first = last = null;

		/* Calculate the size of the hash table and initialize it. */
		hashSize = primes[sz-8];
		ct = new Entry[hashSize]; 
		for (int i = 0; i < hashSize; ++i) {
			ct[i] = null;
		}
	}
	/**
	 * Returns next available code. 
	 *
	 * There are basically two cases, either we haven't use LRU ever 
	 * (so we return nextCode) or we have use it (and we return first 
	 * available code from "codes" table.
	 */
	private int nextAvailableCode() {
		if (useTableCodes == false && nextCode <= maxCode) 
			return (nextCode++);
		if (current == cleanSize) doLRU();
		return (codes[current++]);
	}
	/**
	 * Removes (size>>3) entries from codetable. The entries are
	 * removed from the end of LRU list.
	 */
	private void doLRU() {
		Entry p, n;
		int i;
		n = first;
		for (i = 0; i < cleanSize; ++i) {
			codes[i] = n.code;
			n.inUse = false;
			n.str = null;
			p = n;
			n.prev = null;
			n = n.next;
			p.prev = p.next = null;
			first = n;
		}
		useTableCodes = true;
		current = 0;
	}
	/**
	 * Moves an entry to the end of lru list
	 */
	private void moveLast(Entry e) {
		if (e == last) return;
		if (e == first) first = e.next;
		else if (e.prev != null) e.prev.next = e.next;
		if (e.next != null) e.next.prev = e.prev;
		last.next = e;
		e.prev = last;
		e.next = null;
		last = e; 
	}
	/**
	 * Inserts a string to codetable. If the string is already in
	 * code table, its code is returned, and the string is moved
	 * to the end of LRU list.
	 *
	 * @param s String to insert
	 * @return Code number for string.
	 */
	public int insert(String s) {
		int chain = hash(s);
		Entry e;
		if ((e = pLookup(s, chain)) != null) { 
			/* Found... */
			moveLast(e);
			return e.code;
		}
		for (e = ct[chain]; e != null; e = e.hNext) {
			if (e.inUse == false) break;
		}
		if (e == null) {
	                e = new Entry(s, nextAvailableCode());
			e.hNext = ct[chain];
	                ct[chain] = e;
		} else {
			e.code = nextAvailableCode();
			e.str = s;
			e.inUse = true;
			e.prev = e.next = null;
		}
                if (last == null) last = first = e;
                else moveLast(e);
                return e.code;
	}
	/**
 	 * 
	 * @param s The string to lookup
	 * @return The code number for the string if found, -1 otherwise
	 */
	public int lookup(String s) {
		Entry e = pLookup(s, hash(s));
		if (e != null) {
			moveLast(e);
			return e.code;
		}
		return -1;
	}

	private Entry pLookup(String s, int h) {
		Entry e;
		for (e = ct[h]; e != null; e = e.hNext) {
			if (e.inUse == true && s.compareTo(e.str)==0) 
				return e;
		}
		return null;
	}
	/**
	 * Returns the hash value for a string 
	 */
	private int hash(String s) {
        	return (s.hashCode() & 0x7FFFFFFF) % hashSize;
	}
	/*
	 * Linkage node
	 */
	private class Entry {
		int code; 		// The code number for this entry
		boolean inUse; 	// true if this node is really in use
		String str;		// The string in this entry
		Entry next, prev, hNext;  // "pointers" to other nodes

		public Entry(String s, int c) {
			code = c;
			str = s;
			inUse = true;
			next = prev = hNext = null;
		}
	}
}
