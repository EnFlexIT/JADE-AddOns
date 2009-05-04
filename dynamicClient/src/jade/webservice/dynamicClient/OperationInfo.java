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
package jade.webservice.dynamicClient;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.axis.client.Stub;
import org.apache.axis.wsdl.toJava.Utils;

public class OperationInfo {

	private String name;
	private Map<String, ParameterInfo> parametersInfoMap = new HashMap<String, ParameterInfo>();
	private Map<String, HeaderInfo> headersInfoMap = new HashMap<String, HeaderInfo>();
	private List<ParameterInfo> parametersInfoList = new ArrayList<ParameterInfo>();
	private List<HeaderInfo> headersInfoList = new ArrayList<HeaderInfo>();
	
	private Method operationMethod;

	public OperationInfo(String operationName) {
		this.name = operationName;
	}

	public String getName() {
		return name;
	}

	public Set<String> getParameterNames() {
		return parametersInfoMap.keySet();
	}

	public ParameterInfo getParameter(String parameterName) {
		return parametersInfoMap.get(parameterName);
	}
	
	void putParameter(String parameterName, ParameterInfo parameterInfo) {
		parametersInfoList.add(parameterInfo);
		parametersInfoMap.put(parameterName, parameterInfo);
	}
	
	public Set<String> getHeaderNames() {
		return headersInfoMap.keySet();
	}

	public HeaderInfo getHeader(String headerName) {
		return headersInfoMap.get(headerName);
	}
	
	void putHeader(String headerName, HeaderInfo headerInfo) {
		headersInfoList.add(headerInfo);
		headersInfoMap.put(headerName, headerInfo);
	}

	Method getOperationMethod() {
		return operationMethod;
	}
	
	void manageOperationStubMethod(Stub stub) throws SecurityException, NoSuchMethodException {
		Class[] stubMethodClasses = getStubMethodClasses();
		String methodName = Utils.xmlNameToJava(name);
		operationMethod = stub.getClass().getMethod(methodName, stubMethodClasses);
	}

	ParameterInfo getStubMethodReturnParameter() {
		
		ParameterInfo returnParamater = null;
		Iterator<ParameterInfo> itp = parametersInfoList.iterator();
		while(itp.hasNext()) {
			ParameterInfo pi = itp.next();
			if (pi.getMode() == ParameterInfo.RETURN) {
				returnParamater = pi;
				break;
			}
		}		
		return returnParamater;
	}
	
	Class[] getStubMethodClasses() {
		Vector<ParameterInfo> methodParameters = getStubMethodParameters();
		Class[] stubMethodClasses = new Class[methodParameters.size()];
		int index = 0;
		for (ParameterInfo parameterInfo : methodParameters) {
			stubMethodClasses[index] = parameterInfo.getTypeClass(); 
			index++;
		}
		return stubMethodClasses;
	}
	
	Vector<ParameterInfo> getStubMethodParameters() {
		Vector<ParameterInfo> methodParams = new Vector<ParameterInfo>();
		int methodParamsSize = 0;
		
		// Fill methodParams with header that go in ws method call
		
		Iterator<HeaderInfo> ith = headersInfoList.iterator();
		while(ith.hasNext()) {
			HeaderInfo hi = ith.next();
			int signaturePosition = hi.getSignaturePosition();
			// Current header is in method -> add it
			if (signaturePosition != HeaderInfo.EXPLICIT_HEADER) {
				// Check if methodParams size is sufficient 
				if (methodParams.size() <= signaturePosition) {
					// Increase methodParams size
					methodParams.setSize(signaturePosition+1);
				}
				
				methodParams.set(signaturePosition, hi);
				methodParamsSize++;
			}
		}
		
		// Calculate total size of methodParams -> set it
		methodParamsSize = methodParamsSize + parametersInfoList.size();
		ParameterInfo returnParameter = getStubMethodReturnParameter();
		if (returnParameter != null) {
			methodParamsSize--;
		}
		methodParams.setSize(methodParamsSize);
		
		Iterator<ParameterInfo> itp = parametersInfoList.iterator();
		for(int i=0; i<methodParamsSize; i++) {
			if (methodParams.get(i) == null) {
				ParameterInfo pi = itp.next();
				if (pi == returnParameter) {
					pi = itp.next();
				}
				methodParams.set(i, pi);
			}
		}		
		
		return methodParams;
	}
	
}
