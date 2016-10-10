package com.tilab.wsig.servlet;

import java.lang.reflect.Method;
import java.util.logging.Level;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.tilab.wsig.WSIGConfiguration;

import jade.content.lang.sl.SLCodec;
import jade.core.Profile;
import jade.util.Logger;

public class WSIGInitializer implements ServletContextListener {

	protected Logger logger = Logger.getMyLogger(getClass().getName());
	
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		ServletContext servletContext = sce.getServletContext();
		
		// Init configuration
		WSIGConfiguration.init(null, servletContext, null);
		WSIGConfiguration wsigConfiguration = WSIGConfiguration.getInstance();
		
		// Java type preservation
		String preserveJavaType = wsigConfiguration.getPreserveJavaType();
		if (preserveJavaType != null) {
			System.setProperty(SLCodec.PRESERVE_JAVA_TYPES, preserveJavaType);
		}

		// Add services  
		String services = wsigConfiguration.getProperty(Profile.SERVICES);
		if (services == null) {
			wsigConfiguration.setProperty(Profile.SERVICES, Profile.DEFAULT_SERVICES_NOMOBILITY+";jade.core.faultRecovery.FaultRecoveryService");
		}
		
		// Set WSIGConfiguration into context 
		servletContext.setAttribute(WSIGServletBase.WEBAPP_CONFIGURATION_KEY, wsigConfiguration);
		
		// Set startup user status to true (enable automatic restart gateway after a shutdown) 
		servletContext.setAttribute(WSIGServletBase.WEBAPP_USER_STATUS_ACTIVE_KEY, true);

		// Check if custom initializer class is specified 
		String initializerClassName = wsigConfiguration.getInitializerClassName();
		if (initializerClassName != null) {
			logger.log(Level.INFO, "InitializerClassName "+initializerClassName+" specified");

			// Use custom WSIG initializer
			try {
				// Instantiate initializer class
				Class<?> initializerClass = Class.forName(initializerClassName);
				Object initializerObj = initializerClass.newInstance();

				// Call method initialize(String resourcePath) o initializer class
				Method initializeMethod = initializerClass.getMethod("initialize", new Class[]{String.class});
				initializeMethod.invoke(initializerObj, wsigConfiguration.getWsigResourcePath());
				logger.log(Level.INFO, "Method initialize(resourcePath) succesfully called");
			}
			catch (Throwable t) {
				logger.log(Level.SEVERE, "Error calling custom WSIG initializer "+initializerClassName, t);
			}
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}
}
