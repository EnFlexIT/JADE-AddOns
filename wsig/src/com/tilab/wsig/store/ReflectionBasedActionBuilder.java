/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

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

package com.tilab.wsig.store;

import jade.content.AgentAction;

import java.lang.reflect.Field;
import java.util.Vector;

import org.apache.log4j.Logger;

public class ReflectionBasedActionBuilder implements ActionBuilder {

	private static Logger log = Logger.getLogger(ReflectionBasedActionBuilder.class.getName());
	
	private Class actionClass;
	

	/**
	 * ReflectionBasedActionBuilder
	 * @param actionClass
	 */
	public ReflectionBasedActionBuilder(Class actionClass) {
		this.actionClass = actionClass;
	}

	/**
	 * getAgentAction
	 */
	public AgentAction getAgentAction(Vector<ParameterInfo> params) throws Exception {

		AgentAction actionObj = null;
		
		try {
			actionObj = (AgentAction)actionClass.newInstance();
			log.debug("Create jade action "+actionClass.getName()+" via ontology");
			
			// Set parameters
			if (params != null) {
				Field[] dfs = actionClass.getDeclaredFields();
	
				// Set value to every fields
				for (int count = 0; count < dfs.length; ++count) {
					
					// Set reflection accessiable method
					dfs[count].setAccessible(true);
	
					// Get parameter and verify...
					String paramName = dfs[count].getName();
					ParameterInfo paramEi = params.get(count);
					if (paramEi.getName() != paramName) {
						throw new RuntimeException("Parameter "+paramName+" not match with parameter in store ("+paramEi.getName()+")");
					}
	
					// Set parameter value
					dfs[count].set(actionObj, paramEi.getValue());
					log.debug("Set action parameter "+paramName+" with "+paramEi.getValue());
				}
			}
		} catch(Exception e) {
			log.error("Error creating jade action "+actionClass.getName()+" via ontology", e);
			throw e;
		}
		
		return actionObj;
	}
}
