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
 * Implementation of decoder codetable for fipa-bitefficient-std messages
 * 
 * @author Heikki Helin, Mikko Laukkanen
 */
public class DecoderCodetable {
        /* */
        private int first, last, nextFree, nextCode;
        /**
	 * size Size of the codetable (The actual number how many entries
	 * the codetable can handle.
	 */
        private int size;
        /** 
	 * clean_size Number of entries to remove from the codetable when 
 	 * it is full. The clean_size depends on the size of the codetable,
	 * and is always (size >> 3).
	 */
        private int cleanSize;
        /**
	 * used Number of free entries in the codetable (kind of a misleading
	 * variable name?), whenever LRU algorithm is used at least once
	 * (i.e., if LRU algorithm has not been used yet, this variable
	 * has nothing to do with the number of free entries).
	 */
        private int used;
        /**
	 * use_table_codes Indicates whether LRU algorithm has been 
	 * used at least once.
	 */
        private boolean useTableCodes;
        /**
	 * use_lru Indicates whether we must remove something from the 
	 * codetable before adding any new entries. use_lru is set to true 
	 * when the last free entry is filled in the codetable. In this 
	 * phase, we cannot (yet) delete anything from the codetable, but 
	 * must remove as soon as we have add something new to it.
	 */
        private boolean useLRU;
        /** dict Actual codetable */
        private Entry[] dict;
        /**
 	 * Initialize the codetable with given size.
 	 * @param sz 	Size of the codetable in bits. According to FIPA
 	 *		specs, this shall be a number between 8 and 16.
	 */
        public DecoderCodetable(int sz) {
                initialize((sz>16) ? 16 : (((sz)<8) ? 8 : sz));
        }
        /**
	 * Returns the size of the codetable
	 */
        public int getSize() { return size; }
        /**
	 * Actual initialization of the codetable
	 */
        private void initialize(int sz) {
                size = (2 << (sz-1));
                first = last = nextCode = 0;
                nextFree = -1;
                useTableCodes = false;
                cleanSize = (size >> 3);
                used = cleanSize;
                dict = new Entry[size+1];
                for (int i = 0; i < size+1; ++i) dict[i] = new Entry();
        }
        /**
	 * Removes (size>>3) entries from codetable. The entries are
	 * removed from the end of LRU list.
	 */
        private void doLRU() {
                nextFree = first;
                int n = first;
                for (int i = 0; i < cleanSize; ++i) {
                        dict[n].str = null;
                        dict[n].bstr = null;
                        n = dict[n].next;
                        first = n;
                }
                used = cleanSize;
                nextCode = nextFree;
                nextFree = dict[nextFree].next;
                useLRU = false;
        }
        /**
	 * Returns the bytearray to which the specified code is mapped in this 
	 * codetable. The entry in the codetable is moved to the end of
	 * LRU list.
	 * @param code The code to lookup
	 * @return The bytearray to which the code is mapped in this codetable; 
	 * 	   null if the code is not mapped to any bytearray in this 
	 * 	   hashtable.
	 */
        public byte [] lookupBytes(int code) {
                byte [] b = dict[code].bstr;
                moveCode(code);
                return b;
        }
        /**
	 * Returns the String to which the specified code is mapped in this 
	 * codetable. The entry in the codetable is moved to the end of
	 * LRU list.
	 * @param code The code to lookup
	 * @return The String to which the code is mapped in this codetable; 
	 * 	   null if the code is not mapped to any String in this 
	 * 	   hashtable.
	 */
        public String lookupStr(int code) {
                String s = dict[code].str;
                moveCode(code);
                return s;
        }
        private void moveCode(int code) {
                /*
		 * If this entry is the last one in LRU list, we can just
		 * return the content.
		 */
                if (code == last) return;
                /*
		 * Okay, it wasn't the last one, so we have to move this 
		 * code to the end of LRU list.
		 */
                if (code == first) {
                        if (dict[first].prev != -1)
                                dict[dict[first].prev].next = dict[first].next;
                        if (dict[first].next != -1)
                                dict[dict[first].next].prev = dict[first].prev;
                        first = dict[code].next;
                }
                if (dict[code].prev != -1)
                        dict[dict[code].prev].next = dict[code].next;
                if (dict[code].next != -1)
                        dict[dict[code].next].prev = dict[code].prev;
                dict[last].next = code;
                dict[code].prev = last;
                last = code;
        }
        /**
	 * Insert a new String to codetable. If the code table is full,
	 * (size>>3) entries are removed from the end of LRU list.
	 * 
	 * @param s String to insert
	 */
        public int insert(String s) {
                return (insert(s, null, true));
        }
        /**
	 * Insert a new byte array to codetable. If the code table is full,
	 * (size>>3) entries are removed from the end of LRU list.
	 * 
	 * @param s byte array to insert
	 */
        public int insert(byte [] b) {
                return (insert(null, b, false));
        }
        private int insert(String s, byte [] b, boolean str) {
                /*
		 * First check if the codetable is full. The useLRU is 
		 * set to 'true' iff we filled the last available item
		 * of codetable when we added something previous time.
		 */
                if (useLRU) doLRU();
                /* Okay, now there's room to add new string */
                if (str) dict[nextCode].str = s;
                else dict[nextCode].bstr = b;
                if (useTableCodes) --used;
                if (nextCode != last) {
                        /*
			 * If we added this new string to somewhere else
			 * than beginning of LRU, we have to move it.
		 	 */
                        dict[last].next = nextCode;
                        dict[nextCode].next = 0;
                        dict[nextCode].prev = last;
                        last = nextCode;
                }
                if (first == 0) first = last;
                if (nextFree == -1) ++nextCode;
                if (!useTableCodes && nextCode <= size) return last;
                useLRU = false;
                if (used == 0 || nextFree == -1) {
                        /*
			 * All codes freed in previous LRU are now used,
			 * so we have to use LRU again (when we add something
			 * new, NOT yet!)
			 */
                        useTableCodes = useLRU = true;
                }
                nextCode = nextFree;
                nextFree = (useLRU) ? -1 : dict[nextFree].next;
                return last;
        }
        private class Entry {
                String str;
                byte [] bstr;
                int prev, next;
                public Entry() {
                        str = null;
                        bstr = null;
                        prev = next = -1;
                }
        }
}
