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

import jade.security.dummy.*;

import jade.core.MainContainer;
import jade.core.Profile;

import jade.util.leap.Iterator;

import java.security.*;
import java.security.cert.*;
import java.security.spec.*;


/**
	The <code>Authority</code> class is an abstract class which represents
	the authorities of the platform. It has methods for signing certificates
	and for verifying their validity.
	
	@author Michele Tomaiuolo - Universita` di Parma
	@version $Date$ $Revision$
*/
public class ContainerAuthority extends DummyAuthority {
	
	/**
		The public key for verifying certificates.
	*/
	PublicKey publicKey = null;
	
	public void init(Profile profile, MainContainer platform) {
		try {
			byte[] bytes = platform.getPublicKey();
			if (bytes != null) {
				X509EncodedKeySpec keyspec = new X509EncodedKeySpec(bytes);
				KeyFactory keyFactory = KeyFactory.getInstance("DSA");
				publicKey = keyFactory.generatePublic(keyspec);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getPublicKey() {
		if (publicKey == null)
			return null;
			
		return publicKey.getEncoded();
	}
	
	public void verify(JADECertificate certificate) throws AuthException {
		if (publicKey == null)
			return;

		if (certificate == null)
			throw new AuthException("null certificate");

		if (! (certificate instanceof BasicCertificateImpl))
			throw new AuthException("unknown certificate class");

		try {
			byte[] signBytes = ((BasicCertificateImpl)certificate).getSignature();
			// we have to delete signature, first
			((BasicCertificateImpl)certificate).setSignature(null);
			byte[] certBytes = certificate.encode().getBytes();
			// now we can put signature back to its place
			((BasicCertificateImpl)certificate).setSignature(signBytes);
			if (signBytes == null)
				throw new AuthException("null signature");
			
			Signature sign = Signature.getInstance("DSA");
			sign.initVerify(publicKey);
			sign.update(certBytes);
			if (!sign.verify(signBytes))
				throw new AuthException("Corrupted certificate");
		}
		catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		catch (InvalidKeyException e2) {
			e2.printStackTrace();
		}
		catch (SignatureException e3) {
			throw new AuthException(e3.getMessage());
		}
		catch (ClassCastException e4) {
			throw new AuthException(e4.getMessage());
		}
	}
	
	public void checkPermission(String type, String name, String actions) throws AuthException {
		Permission p = createPermission(type, name, actions);
		if (p != null)
			AccessController.checkPermission(p);
	}
	
	public Object doAs(jade.security.leap.PrivilegedAction action, IdentityCertificate identity, DelegationCertificate[] delegations) throws Exception {
		ProtectionDomain domain = new ProtectionDomain(
				new CodeSource(null, null), collectPermissions(delegations), null, null);
		AccessControlContext acc = new AccessControlContext(new ProtectionDomain[] {domain});
		try {
			return AccessController.doPrivileged(action, acc);
		}
		catch (PrivilegedActionException e) {
			throw e.getException();
		}
	}

	public void checkAction(String action, JADEPrincipal target, IdentityCertificate identity, DelegationCertificate[] delegations) throws AuthException {
		Permission p= null;
		if (action.startsWith("ams-")) {
			p = createPermission("jade.security.impl.AmsPermission", target.getName(), action.substring(4, action.length()));
		}
		if (action.startsWith("agent-")) {
			p = createPermission("jade.security.impl.AgentPermission", target.getName(), action.substring(6, action.length()));
		}
		else if (action.startsWith("container-")) {
			p = createPermission("jade.security.impl.ContainerPermission", target.getName(), action.substring(10, action.length()));
		}
		else if (action.startsWith("platform-")) {
			p = createPermission("jade.security.impl.PlatformPermission", target.getName(), action.substring(9, action.length()));
		}
		else if (action.startsWith("authority-")) {
			p = createPermission("jade.security.impl.AuthorityPermission", target.getName(), action.substring(10, action.length()));
		}
		PermissionCollection perms = collectPermissions(delegations);
		if (p != null && !perms.implies(p))
			throw new AuthException(action);
	}

	public AgentPrincipal createAgentPrincipal(){
		return new PrincipalImpl();
	}

	public ContainerPrincipal createContainerPrincipal() {
		return new PrincipalImpl();
	}
	
	public UserPrincipal createUserPrincipal() {
		return new PrincipalImpl();
	}
	
	public IdentityCertificate createIdentityCertificate() {
		return new IdentityCertificateImpl();
	}
	
	public DelegationCertificate createDelegationCertificate() {
		return new DelegationCertificateImpl();
	}
	
	PermissionCollection collectPermissions(DelegationCertificate[] delegations) {
		Permissions perms = new Permissions();
		for (int j = 0; j < delegations.length; j++) {
			for (Iterator i = delegations[j].getPermissions().iterator(); i.hasNext();) {
				Permission p = (Permission)i.next();
				perms.add(p);
			}
		}
		return perms;
	}
	
	Permission createPermission(String type, String name, String actions) {
		Permission p = null;
		try {
			if (actions != null)
				p = (Permission)Class.forName(type).getConstructor(new Class[] {String.class, String.class}).newInstance(new Object[] {name, actions});
			else if (name != null)
				p = (Permission)Class.forName(type).getConstructor(new Class[] {String.class}).newInstance(new Object[] {name});
			else
				p = (Permission)Class.forName(type).newInstance();
		}
		catch (Exception e) { e.printStackTrace(); }
		return p;
	}
}
