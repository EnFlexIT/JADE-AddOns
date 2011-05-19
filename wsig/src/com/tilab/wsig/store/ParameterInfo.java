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

import java.lang.reflect.Method;

import com.tilab.wsig.wsdl.JadeToWSDL;

import jade.content.abs.AbsObject;
import jade.content.schema.ObjectSchema;
import jade.content.schema.TermSchema;

public class ParameterInfo {
	
	private String name;
	private TermSchema schema;
	private TermSchema elementsSchema;
	private AbsObject value;
	private Method mapperMethod;
	private Integer minCard;
	private Integer maxCard;
	
	public ParameterInfo(String name, TermSchema schema) {
		this.name = name;
		this.schema = schema;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public TermSchema getSchema() {
		return schema;
	}
	
	public void setSchema(TermSchema schema) {
		this.schema = schema;
	}
	
	public AbsObject getValue() {
		return value;
	}
	
	public void setValue(AbsObject value) {
		this.value = value;
	}
	
	public void setMapperMethod(Method mapperMethod) {
		this.mapperMethod = mapperMethod;
	}

	public Method getMapperMethod() {
		return mapperMethod;
	}

	public void setMinCard(Integer minCard) {
		this.minCard = minCard;
	}

	public int getMinCard() {
		// null -> mandatory -> 1
		if (minCard == JadeToWSDL.MANDATORY) {
			return 1;
		}
		return minCard.intValue();
	}

	public void setMaxCard(Integer maxCard) {
		this.maxCard = maxCard;
	}

	public int getMaxCard() {
		if (maxCard == null) {
			return Integer.valueOf(ObjectSchema.UNLIMITED).intValue();
		}
		return maxCard.intValue();
	}
	
	public boolean isMandatory() {
		return getMinCard() > 0;
	}

	public void setElementsSchema(TermSchema elementsSchema) {
		this.elementsSchema = elementsSchema;
	}

	public TermSchema getElementsSchema() {
		return elementsSchema;
	}
}
