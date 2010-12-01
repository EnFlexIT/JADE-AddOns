package org.logica.ctis.security;

import jade.core.Profile;
import jade.core.ProfileException;
import jade.security.JADESecurityException;

public class OwnerBasedTokenProvider implements ConfigurableTokenProvider {

	private String owner;
	
	public void init(Profile p) throws ProfileException {
		owner = p.getParameter(Profile.OWNER, null);
		if (owner == null) {
			throw new ProfileException("Owner not specified");
		}
	}

	public String getToken(String name) throws JADESecurityException {
		return owner;
	}

}
