/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package jade.security.impl;

import jade.security.*;

import jade.util.leap.Iterator;

import jade.core.AID;
import jade.core.MainContainer;
import jade.core.Profile;

import jade.util.leap.List;

import java.io.BufferedReader;
import java.io.FileReader;

import java.util.Enumeration;
import java.util.Vector;

import java.security.*;
import java.security.cert.*;
import java.security.spec.*;


/**
	The <code>Authority</code> class is an abstract class which represents
	the authorities of the platform. It has methods for signing certificates
	and for verifying their validity.
	b 
	@author Michele Tomaiuolo - Universita` di Parma
	@version $Date$ $Revision$
*/
public class PlatformAuthority extends ContainerAuthority {

	class UserEntry {
		String usr;
		String key;
	}
	
	int serial;
	
	String passwdFile;
	Vector users = null;
	
	PrivateKey privateKey = null;
	
	Profile profile=null;
	
	public void init(Profile profile, MainContainer platform) {
		if (System.getProperty("jade.security.nosign") == null) {
			try {
				KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
				kpg.initialize(1024);
				KeyPair kp = kpg.generateKeyPair();
				privateKey = kp.getPrivate();
				publicKey = kp.getPublic();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (profile != null) {
			this.profile = profile;
			try {

				passwdFile = profile.getParameter(Profile.PASSWD_FILE, null);
				parsePasswdFile();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		serial = 1;
		
		fillChecks();
	}
	
	public void sign(JADECertificate certificate, CertificateFolder certs) throws AuthException {
		verifySubject(certs);

		if (! (certificate instanceof BasicCertificateImpl))
			throw new AuthException("unknown certificate class");

		if (certificate instanceof DelegationCertificate) {
			checkAction(AUTHORITY_SIGN_DC, certificate.getSubject(), certs);
			PermissionCollection perms = collectPermissions(certs);
			for (Iterator i = ((DelegationCertificate)certificate).getPermissions().iterator(); i.hasNext(); )
				if (!perms.implies((Permission)i.next()))
					throw new AuthException("trying to delegate not owned permissions");
		}
		else if (certificate instanceof IdentityCertificate) {
			checkAction(AUTHORITY_SIGN_IC, certificate.getSubject(), certs);
		}
		
		sign((BasicCertificateImpl)certificate);
	}

	public CertificateFolder authenticate(JADEPrincipal principal, byte[] password) throws AuthException {
		final PrincipalImpl p = (PrincipalImpl)principal;
		
		IdentityCertificate identity = createIdentityCertificate();
		identity.setSubject(p);
		DelegationCertificate delegation = createDelegationCertificate();
		delegation.setSubject(p);

		String name = p.getOwnership();

 		//System.out.println("authenticating");
 		//System.out.println("principal=" + principal + ";");
 		//System.out.println("password=" + new String(password) + ";");
 		//System.out.println("hash=" + hash + ";");

		if (users == null)
			return new CertificateFolder(identity, delegation);
		else for (int i = 0; i < users.size(); i++) {
			UserEntry entry = (UserEntry)users.elementAt(i);
			if (entry.usr.equals(name)) {

				// Calculate hash of provided password
			    // using the Digest Algorithm choosen in the Jade profile
				String hash ="";

				String digest_alg = profile.getParameter(Profile.PWD_HASH_ALGORITHM, "DES");
				// default is "DES"

				if (digest_alg.compareTo("DES")==0 ) {
				
					// first two password chars is the salt
					String salt = entry.key.substring(0, 2);
					
					// create the hash of the provided password
					hash = Digest.digest(password, "DES", salt);
				
					//System.out.println("     hash="+hash);
					//System.out.println("entry.key="+entry.key+ "\n");
				} else {
					// other digest algorithms (es. MD5, MD2, SHA-1)
					hash = Digest.digest(password, digest_alg );
				}


				// Compare the hash of provided password
				// to the entry in the password file
				if (! entry.key.equals(hash))
					throw new AuthenticationException("Wrong password");

				// ok: user found + exact password
				// add permissions here
				PermissionCollection perms = null;
				//perms = getPermissions(p);
				try {
					perms = (PermissionCollection)AccessController.doPrivileged(new jade.security.PrivilegedExceptionAction() {
						public Object run() {
							return getPermissions(p);
						}
					});
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
				for (Enumeration e = perms.elements(); e.hasMoreElements();) {
					Permission perm = (Permission)e.nextElement();
					delegation.addPermission(perm);
					//System.out.println("+" + perm);
				}
				sign((BasicCertificateImpl)identity);
				sign((BasicCertificateImpl)delegation);
				//System.out.println("authentication ended");
				return new CertificateFolder(identity, delegation);
			}
		}
		throw new AuthenticationException("Unknown user");
	}
	
	private void sign(BasicCertificateImpl certificate) {
		if (publicKey == null)
			return;
		
		try {
			certificate.setIssuer(new PrincipalImpl(getName()));
			certificate.setSerial(serial++);
			certificate.setSignature(null);
			
			Signature sign = Signature.getInstance("DSA");
			sign.initSign(privateKey);
			sign.update(certificate.encode().getBytes());
			byte[] signature = sign.sign();
			
			certificate.setSignature(signature);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parsePasswdFile() {
		//System.out.println("parsing passwd file " + passwdFile);
		if (passwdFile == null) return;
		users = new Vector();
		try {
			BufferedReader file = new BufferedReader(new FileReader(passwdFile));
			String line = file.readLine();
			while (line != null) {
				line = line.trim();
				if (line.length() > 0) {
					UserEntry entry = new UserEntry();
					int sep = line.indexOf(':');
					int sep2 = line.indexOf(':', sep+1);
					//System.out.println(sep +" , "+ sep2);
					//System.out.println( line );
					// The password can be after a ":" 
					// or in between two ":"
					if (sep != -1) {
						entry.usr = line.substring(0, sep);
						if (sep2 != -1) {
							entry.key = line.substring(sep + 1, sep2 );
						} else {
							entry.key = line.substring(sep + 1, line.length());
						}
					}
					else {
						entry.usr = line;
						entry.key = null;
					}
					//System.out.println("username=" + entry.usr + "; password=" + entry.key + ";");
					users.addElement(entry);
				}
				line = file.readLine();
			}
		}
		catch (Exception e) { e.printStackTrace(); }
	}

	
	private PermissionCollection getPermissions(PrincipalImpl principal) {
		Policy policy = Policy.getPolicy();
		CodeSource source = new CodeSource(null, null);
		
		ProtectionDomain nullDomain = new ProtectionDomain(
				source, null, null, null);
		PermissionCollection nullPerms = policy.getPermissions(nullDomain);
		
		Permissions perms = new Permissions();
		List implied = principal.getAllImplied();
		for (int i = 0; i < implied.size(); i++) {
			ProtectionDomain principalDomain = new ProtectionDomain(
					source, null, getClass().getClassLoader(), new java.security.Principal[] {(PrincipalImpl)implied.get(i)});
			PermissionCollection principalPerms = policy.getPermissions(principalDomain);
			
			for (Enumeration e = principalPerms.elements(); e.hasMoreElements();) {
				Permission p = (Permission)e.nextElement();
				if (p instanceof UnresolvedPermission) {
					//!!! hack
					String str = p.toString();
					p = BasicCertificateImpl.decodePermission(str.substring(str.indexOf(' ') + 1, str.length() - 1));
				}
				if (! nullPerms.implies(p))
					perms.add(p);
			}
		}
		return perms;
	}

}
