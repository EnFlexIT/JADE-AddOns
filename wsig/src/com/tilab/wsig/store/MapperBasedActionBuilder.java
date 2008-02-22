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

import java.lang.reflect.Method;
import java.util.Vector;

import org.apache.log4j.Logger;

public class MapperBasedActionBuilder implements ActionBuilder {
	
	private static Logger log = Logger.getLogger(MapperBasedActionBuilder.class.getName());

	private Method method;
	private Object mapperObj;
	private String ontoActionName;

	/**
	 * MapperBasedActionBuilder
	 * @param mapperObj
	 * @param method
	 */
	public MapperBasedActionBuilder(Object mapperObj, Method method, String ontoActionName) {
		this.method = method;
		this.mapperObj = mapperObj;
		this.ontoActionName = ontoActionName;
	}

	/**
	 * getAgentAction
	 */
	public AgentAction getAgentAction(Vector<ParameterInfo> params) throws Exception {
		
		AgentAction actionObj = null;
		
		// Prepare mapper parameter
		Object[] parameterValues = new Object[params.size()];
		String parameterList = "";
		for (int count = 0; count < params.size(); ++count) {
			ParameterInfo objParamEi = params.get(count);
			parameterValues[count] = objParamEi.getValue();
			parameterList += objParamEi.getType()+",";
		}
		
		try {
			// Invoke mapper method
			actionObj = (AgentAction)method.invoke(mapperObj, parameterValues);
			log.debug("Invoked method "+method.getName()+"("+parameterList.substring(0,parameterList.length()-1)+") in mapper");

		} catch(Exception e) {
			log.error("Method "+method.getName()+"("+parameterList.substring(0,parameterList.length()-1)+") not found in mapper");
			throw e;
		}
		
		return actionObj;
	}
	
	public String getOntoActionName() {
		return ontoActionName;
	}
}
