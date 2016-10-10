package com.tilab.wsig.examples;

import java.util.logging.Level;

import jade.util.Logger;

public class MyInitializer {

	protected Logger logger = Logger.getMyLogger(getClass().getName());
	
	public void initialize(String resourcesPath) {
		logger.log(Level.INFO, "Called initialize method, resourcesPath="+resourcesPath);
	}
}
