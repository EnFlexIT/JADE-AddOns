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

import jade.core.MainContainer;
import jade.core.Profile;

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
			try {
				passwdFile = profile.getParameter(Profile.PASSWD_FILE);
				parsePasswdFile();
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				if (System.getSecurityManager() == null) {
					String policyFile = profile.getParameter(Profile.POLICY_FILE);
					System.setProperty("java.security.policy", policyFile);
					System.setSecurityManager(new SecurityManager());
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		serial = 1;
	}
	
	public void sign(JADECertificate certificate, IdentityCertificate identity, DelegationCertificate[] delegations) throws AuthException {
		if (! (certificate instanceof BasicCertificateImpl))
			throw new AuthException("unknown certificate class");
		if (identity == null)
			throw new AuthException("null identity");
		
		verify(identity);
		for (int d = 0; d < delegations.length; d++) {
			if (! ((PrincipalImpl)identity.getSubject()).implies((PrincipalImpl)delegations[d].getSubject()))
				throw new AuthException("delegation-subject doesn't match identity-subject");
			verify(delegations[d]);
		}
		
		if (certificate instanceof DelegationCertificate) {
			checkAction(AUTHORITY_SIGN_DC, certificate.getSubject(), identity, delegations);
			PermissionCollection perms = collectPermissions(delegations);
			for (Iterator i = ((DelegationCertificate)certificate).getPermissions().iterator(); i.hasNext(); )
				if (!perms.implies((Permission)i.next()))
					throw new AuthException("trying to delegate not owned permissions");
		}
		else if (certificate instanceof IdentityCertificate) {
			checkAction(AUTHORITY_SIGN_IC, certificate.getSubject(), identity, delegations);
		}
		
		sign((BasicCertificateImpl)certificate);
	}

	public void authenticate(IdentityCertificate identity, DelegationCertificate delegation, byte[] passwd) throws AuthException {
		JADEPrincipal principal = identity.getSubject();
		UserPrincipal user = null;
		if (principal instanceof AgentPrincipal)
			user = ((AgentPrincipal)principal).getUser();
		else if (principal instanceof ContainerPrincipal)
			user = ((ContainerPrincipal)principal).getUser();
		else if (principal instanceof UserPrincipal)
			user = (UserPrincipal)principal;
		else
			throw new AuthenticationException("Unknown principal type");

		System.out.println("authenticating user");
		System.out.println("username=" + user.getName() + ";");
		System.out.println("password=" + new String(passwd) + ";");
		String name = user.getName();
		if (users == null)
			return;
		else for (int i = 0; i < users.size(); i++) {
			UserEntry entry = (UserEntry)users.elementAt(i);
			if (entry.usr.equals(name)) {
				if (passwd.length != entry.key.length())
					throw new AuthenticationException("Wrong password");
				for (int j = 0; j < entry.key.length(); j++) {
					if (entry.key.charAt(j) != passwd[j])
						throw new AuthenticationException("Wrong password");
				}

				// ok: user found + exact password
				// add permissions here
				PermissionCollection perms = getPermissions(user);
				for (Enumeration e = perms.elements(); e.hasMoreElements();) {
					Permission p = (Permission)e.nextElement();
					System.out.println("+" + p);
					delegation.addPermission(p);
				}
				sign((BasicCertificateImpl)identity);
				sign((BasicCertificateImpl)delegation);
				System.out.println("authentication ended");
				return;
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
		System.out.println("parsing passwd file " + passwdFile);
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
					if (sep != -1) {
						entry.usr = line.substring(0, sep);
						entry.key = line.substring(sep + 1, line.length());
					}
					else {
						entry.usr = line;
						entry.key = null;
					}
					System.out.println("username=" + entry.usr + "; password=" + entry.key + ";");
					users.addElement(entry);
				}
				line = file.readLine();
			}
		}
		catch(Exception e) { e.printStackTrace(); }
	}
	
	private PermissionCollection getPermissions(UserPrincipal user) {
		Policy policy = Policy.getPolicy();
		CodeSource source = new CodeSource(null, null);
		
		ProtectionDomain nullDomain = new ProtectionDomain(
				source, null, null, null);
		PermissionCollection nullPerms = policy.getPermissions(nullDomain);
		
		Permissions perms = new Permissions();

		while (user != null) {
			ProtectionDomain userDomain = new ProtectionDomain(
					source, null, getClass().getClassLoader(), new java.security.Principal[] {user});
			PermissionCollection userPerms = policy.getPermissions(userDomain);

			for (Enumeration e = userPerms.elements(); e.hasMoreElements();) {
				Permission p = (Permission)e.nextElement();
				if (p instanceof UnresolvedPermission) {
					//!!! hack
					String str = p.toString();
					p = BasicCertificateImpl.decodePermission(str.substring(str.indexOf(' ') + 1, str.length() - 1));
				}
				if (!nullPerms.implies(p))
					perms.add(p);
			}
			user = ((PrincipalImpl)user).getParent();
		}

		return perms;
	}
	
}
