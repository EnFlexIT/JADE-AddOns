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

import jade.core.AID;
import jade.core.ContainerID;
import jade.core.MainContainer;
import jade.core.Profile;

import jade.util.leap.Iterator;
import jade.util.leap.Map;
import jade.util.leap.HashMap;

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
public class ContainerAuthority implements Authority {

	class CheckEntry {
		String type;
		String actions;
		CheckEntry(String t, String a) { type = t; actions = a; }
		String getType() { return type; }
		String getActions() { return actions; }
	}

	Map checks = new HashMap();

	/**
		The public key for verifying certificates.
	*/
	PublicKey publicKey = null;
	Profile profile;
	MainContainer platform = null;
	String name = null;

	public void init(Profile profile, MainContainer platform) {
		this.profile = profile;
		this.platform = platform;

		try {
			if (System.getSecurityManager() == null) {
				String policyFile = profile.getParameter(Profile.POLICY_FILE, null);
				System.setProperty("java.security.policy", policyFile);
				Policy.setPolicy(new sun.security.provider.PolicyFile());

				//System.out.println("Setting security manager");
				System.setSecurityManager(new SecurityManager());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

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
		
		fillChecks();
	}
	
	public void fillChecks() {
		checks.put(AMS_REGISTER, new CheckEntry("jade.security.impl.AMSPermission", "register"));
		checks.put(AMS_DEREGISTER, new CheckEntry("jade.security.impl.AMSPermission", "deregister"));
		checks.put(AMS_MODIFY, new CheckEntry("jade.security.impl.AMSPermission", "modify"));
		
		checks.put(AGENT_CREATE, new CheckEntry("jade.security.impl.AgentPermission", "create"));
		checks.put(AGENT_KILL, new CheckEntry("jade.security.impl.AgentPermission", "kill"));
		checks.put(AGENT_SUSPEND, new CheckEntry("jade.security.impl.AgentPermission", "suspend"));
		checks.put(AGENT_RESUME, new CheckEntry("jade.security.impl.AgentPermission", "resume"));
		checks.put(AGENT_TAKE, new CheckEntry("jade.security.impl.AgentPermission", "take"));
		checks.put(AGENT_SEND_TO, new CheckEntry("jade.security.impl.AgentPermission", "send-to"));
		checks.put(AGENT_SEND_AS, new CheckEntry("jade.security.impl.AgentPermission", "send-as"));
		checks.put(AGENT_RECEIVE_FROM, new CheckEntry("jade.security.impl.AgentPermission", "receive-from"));
		checks.put(AGENT_MOVE, new CheckEntry("jade.security.impl.AgentPermission", "move"));
		checks.put(AGENT_CLONE, new CheckEntry("jade.security.impl.AgentPermission", "clone"));
		
		checks.put(CONTAINER_CREATE, new CheckEntry("jade.security.impl.ContainerPermission", "create"));
		checks.put(CONTAINER_KILL, new CheckEntry("jade.security.impl.ContainerPermission", "kill"));
		checks.put(CONTAINER_CREATE_IN, new CheckEntry("jade.security.impl.ContainerPermission", "create-in"));
		checks.put(CONTAINER_KILL_IN, new CheckEntry("jade.security.impl.ContainerPermission", "kill-in"));
		checks.put(CONTAINER_MOVE_FROM, new CheckEntry("jade.security.impl.ContainerPermission", "move-from"));
		checks.put(CONTAINER_MOVE_TO, new CheckEntry("jade.security.impl.ContainerPermission", "move-to"));
		checks.put(CONTAINER_CLONE_FROM, new CheckEntry("jade.security.impl.ContainerPermission", "clone-from"));
		checks.put(CONTAINER_CLONE_TO, new CheckEntry("jade.security.impl.ContainerPermission", "clone-to"));
		
		checks.put(PLATFORM_CREATE, new CheckEntry("jade.security.impl.PlatformPermission", "create"));
		checks.put(PLATFORM_KILL, new CheckEntry("jade.security.impl.PlatformPermission", "kill"));
		
		checks.put(AUTHORITY_SIGN_IC, new CheckEntry("jade.security.impl.AuthorityPermission", "sign-ic"));
		checks.put(AUTHORITY_SIGN_DC, new CheckEntry("jade.security.impl.AuthorityPermission", "sign-dc"));
	}

	public void setName(String name) {
		if (this.name == null)
			this.name = name;
	}

	public String getName() {
		return name;
	}

	public byte[] getPublicKey() {
		if (publicKey == null)
			return null;

		return publicKey.getEncoded();
	}

	public void sign(JADECertificate certificate, CertificateFolder certs) throws AuthException {
		try {
			JADECertificate signed = platform.sign(certificate, certs);
			((BasicCertificateImpl)certificate).setSignature(((BasicCertificateImpl)signed).getSignature());
		}
		catch (jade.core.IMTPException e) {
			e.printStackTrace();
		}
	}
	
	public CertificateFolder authenticate(JADEPrincipal principal, byte[] password) throws AuthException {
		throw new AuthorizationException("Authentication is not allowed, here");
	}

	public void verify(JADECertificate certificate) throws AuthException {
		if (publicKey == null)
			return;

		if (certificate == null)
			throw new AuthException("Null certificate");

		if (! (certificate instanceof BasicCertificateImpl))
			throw new AuthException("Unknown certificate class");

		try {
			byte[] signBytes = ((BasicCertificateImpl)certificate).getSignature();
			// We have to delete signature, first
			((BasicCertificateImpl)certificate).setSignature(null);
			byte[] certBytes = certificate.encode().getBytes();
			// Now we can put signature back in its place
			((BasicCertificateImpl)certificate).setSignature(signBytes);
			if (signBytes == null)
				throw new AuthException("Null signature");

			Signature sign = Signature.getInstance("DSA");
			sign.initVerify(publicKey);
			sign.update(certBytes);
			if (! sign.verify(signBytes))
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

	public void verifySubject(CertificateFolder certs) throws AuthException {
		if (certs.getIdentityCertificate() == null)
			throw new AuthException("Null identity");

		verify(certs.getIdentityCertificate());
		for (int d = 0; d < certs.getDelegationCertificates().size() && certs.getDelegationCertificates().get(d) != null; d++) {
			if (! ((PrincipalImpl)certs.getIdentityCertificate().getSubject()).implies((PrincipalImpl)((DelegationCertificate)certs.getDelegationCertificates().get(d)).getSubject()))
				throw new AuthException("Delegation-subject doesn't match identity-subject");
			verify((DelegationCertificate)certs.getDelegationCertificates().get(d));
		}
	}

	public Object doPrivileged(jade.security.PrivilegedExceptionAction action) throws Exception {
		return AccessController.doPrivileged(action);
	}

	public Object doAsPrivileged(jade.security.PrivilegedExceptionAction action, CertificateFolder certs) throws Exception {
		verifySubject(certs);
		ProtectionDomain domain = new ProtectionDomain(
				new CodeSource(null, null), collectPermissions(certs), null, null);
		AccessControlContext acc = new AccessControlContext(new ProtectionDomain[] {domain});
		try {
			return AccessController.doPrivileged(action, acc);
		}
		catch (PrivilegedActionException e) {
			throw e.getException();
		}
	}

	private class CheckAction implements jade.security.PrivilegedExceptionAction {
		Permission p;
		CheckAction(Permission p) {
			this.p = p;
		}
		public Object run() throws AuthException {
			AccessController.checkPermission(p);
			return null;
		}
	}

	public void checkAction(String action, JADEPrincipal target, CertificateFolder certs) throws AuthException {
		Permission p = null;
		
		CheckEntry ce = (CheckEntry)checks.get(action);
		if (ce != null) {
			p = createPermission(ce.getType(), target.getName(), ce.getActions());
			try {
				//System.out.println("permission: "+p);
				if (certs != null) {
				//System.out.println("Checking (with certs)" + action + " on " + target);
					CheckAction ca = new CheckAction(p);
					doAsPrivileged(ca, certs);
				}
				else
					//System.out.println("Checking " + action + " on " + target);
					AccessController.checkPermission(p);
				//System.out.println("ok");
			}
			catch (Exception e) {
				System.out.println("Permissions to execute " + action + " on " + target + " are not owned.");
				throw new AuthorizationException("Permissions to execute " + action + " on " + target + " are not owned.");
			}
		}
	}

	public AgentPrincipal createAgentPrincipal(AID aid, String ownership){
		return new PrincipalImpl(aid, ownership);
	}

	public ContainerPrincipal createContainerPrincipal(ContainerID cid, String ownership) {
		return new PrincipalImpl(cid, ownership);
	}

	public IdentityCertificate createIdentityCertificate() {
		return new IdentityCertificateImpl();
	}

	public DelegationCertificate createDelegationCertificate() {
		return new DelegationCertificateImpl();
	}

	PermissionCollection collectPermissions(CertificateFolder certs) {
		Permissions perms = new Permissions();
		for (int j = 0; j < certs.getDelegationCertificates().size() && certs.getDelegationCertificates().get(j) != null; j++) {
			for (Iterator i = ((DelegationCertificate)certs.getDelegationCertificates().get(j)).getPermissions().iterator(); i.hasNext();) {
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
