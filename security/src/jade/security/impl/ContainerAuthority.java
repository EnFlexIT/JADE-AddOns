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
	PublicKey publicKey;
	
	public void init(Profile profile, MainContainer platform) {
		byte[] bytes = null;
		try {
			bytes = platform.getPublicKey();
			X509EncodedKeySpec keyspec = new X509EncodedKeySpec(bytes);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA");
			publicKey = keyFactory.generatePublic(keyspec);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void verify(IdentityCertificate cert) throws AuthException {
		verify((BasicCertificateImpl)cert);
	}
	
	public void verify(DelegationCertificate cert) throws AuthException {
		verify((BasicCertificateImpl)cert);
	}
	
	public void verify(BasicCertificateImpl cert) throws AuthException {
		try {
			if (cert == null)
				throw new AuthException("null certificate");
			byte[] certBytes = cert.encode().getBytes();
			byte[] signBytes = cert.getSignature();
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
			e3.printStackTrace();
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
		if (!action.startsWith("agent-"))
			return;
		PermissionCollection perms = collectPermissions(delegations);
		String type = "jade.security.impl.AgentPermission";
		Permission p = createPermission(type, target.getName(), action.substring(6, action.length()));
		if (!perms.implies(p))
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
