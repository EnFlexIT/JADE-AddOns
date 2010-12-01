package org.logica.ctis.security;

import jade.security.JADESecurityException;

public class DummyTokenValidator implements TokenValidator {

	public boolean isValid(String token, String commandName, String objectName) throws JADESecurityException {
		System.out.println("--- Validating token "+token+" associated to agent "+objectName);
		return true;
	}

}
