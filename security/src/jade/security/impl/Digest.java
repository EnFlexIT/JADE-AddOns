package jade.security.impl;

import java.security.MessageDigest;
import java.math.BigInteger;


public class Digest {
	
	public static String digest(String message, String algorithm) {
		return digest(message.getBytes(), algorithm);
	}

	public static String digest(byte[] message, String algorithm) {
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
	}

	public static void main(String[] args) {
		if (args.length > 1)
			System.out.println(digest(args[0], args[1]));
		else if (args.length > 0)
			System.out.println(digest(args[0], "MD5"));
		else
			System.out.println("usage: java jade.security.impl.Digest <message> [<algorithm>]");
	}
}