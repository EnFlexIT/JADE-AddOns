package jade.security.impl;

import java.security.MessageDigest;
import java.math.BigInteger;
import jade.security.impl.DES.Crypt;

public class Digest {
	
	public static String digest(String message, String algorithm) {
		return digest(message.getBytes(), algorithm);
	}

	/*
	 *  returns the DES digest of 'message' using 'salt'
	 *  'algorithm' here can be only "DES"
	 *
	 */
	public static String digest(byte[] message, String algorithm, String salt) {
 		if (algorithm.compareTo("DES")!=0) { 
			return "only-DES";
		}
		// Calculate DES "hash" with the provided key
		return jade.security.impl.DES.Crypt.crypt( salt, new String(message) ); 

	} // end digest (byte[], String, String) for DES


	/*
	 *  returns the digest of 'message' using a specified 'algorithm'
	 *  The provided algorithm has to be supported in JCE
	 *
	 */
	public static String digest(byte[] message, String algorithm) {
		// Use a hashing function supported in Java
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.reset();
			md.update(message);
			byte[] dgst = md.digest();
			return new BigInteger(+1, dgst).toString(16);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return "###";
	} // end digest()



	public static void main(String[] args) {
		if ( args[0].compareTo("DES")==0 ) {
			System.out.println(digest( args[0].getBytes(),  args[1], "DES"));
		} else { 
			if (args.length > 1) {
				System.out.println( digest(args[1], args[0] ));
			} else {
				System.out.println("\nusage: java jade.security.impl.Digest <algorithm> <message> [<salt>]\n");
				System.out.println(" Examples: ");
				System.out.println("   java jade.security.impl.Digest MD5 password");
				System.out.println("   java jade.security.impl.Digest DES password sa");
			}
		}
	} // end main()
} // end class



