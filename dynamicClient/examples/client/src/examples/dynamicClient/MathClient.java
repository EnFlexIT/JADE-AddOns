package examples.dynamicClient;

import jade.content.abs.AbsConcept;
import jade.content.abs.AbsObject;
import jade.webservice.dynamicClient.DynamicClient;
import jade.webservice.dynamicClient.DynamicClientCache;
import jade.webservice.dynamicClient.OperationInfo;
import jade.webservice.dynamicClient.ParameterInfo;
import jade.webservice.dynamicClient.PortInfo;
import jade.webservice.dynamicClient.ServiceInfo;
import jade.webservice.dynamicClient.WSData;

import java.net.URI;

public class MathClient {

	public static void main(String[] args) {
		try {

			// Get an instance of DynamicClientCache
			DynamicClientCache dcc = DynamicClientCache.getInstance();

			// Get the DynamicClient for MathFunctions webservice by file
			DynamicClient dc = dcc.get(new URI("file:./MathFunctions.wsdl"));

			// Get the DynamicClient for MathFunctions webservice by url
			//DynamicClient dc = dcc.get(new URI("http://localhost:2000/axis/services/MathFunctionsPort?wsdl"));
			
			// Example to get wsdl informations
			for (String serviceName : dc.getServiceNames()) {
				ServiceInfo serviceInfo = dc.getService(serviceName);
				System.out.println("Service name = "+serviceName+" - "+serviceInfo.getDocumentation());
				
				for (String portName : serviceInfo.getPortNames()) {
					PortInfo portInfo = serviceInfo.getPort(portName);
					System.out.println("Port name = "+portName+" - "+portInfo.getDocumentation());
					
					for (String opName : portInfo.getOperationNames()) {
						OperationInfo opInfo = portInfo.getOperation(opName);
						System.out.println("Operation name = "+opName+" - "+opInfo.getDocumentation());
						
						for (String inParamName : opInfo.getInputParameterNames()) {
							ParameterInfo inParInfo = opInfo.getInputParameter(inParamName);
							System.out.println("Input parameter name"+inParamName+" - "+inParInfo.getDocumentation());
						}

						for (String outParamName : opInfo.getOutputParameterNames()) {
							ParameterInfo outParInfo = opInfo.getOutputParameter(outParamName);
							System.out.println("Output parameter name"+outParamName+" - "+outParInfo.getDocumentation());
						}
					}
				}
			}
			
			// Example to invoke sum
			WSData input = new WSData();
			input.setParameter("firstElement", 5);
			input.setParameter("secondElement", 3);
			WSData output = dc.invoke("sum", input);
			float sum = output.getParameterFloat("sumReturn");
			
			// Example to invoke abs
			input = new WSData();
			AbsConcept absComplex = new AbsConcept("Complex");
			absComplex.set("real", 4);
			absComplex.set("immaginary", 5);
			input.setParameter("complex", absComplex);
			output = dc.invoke("abs", input);
			float abs = output.getParameterFloat("absReturn");

			// Example to invoke sumComplex
			input = new WSData();
			AbsConcept absComplex1 = new AbsConcept("Complex");
			absComplex1.set("real", 4);
			absComplex1.set("immaginary", 5);
			AbsConcept absComplex2 = new AbsConcept("Complex");
			absComplex2.set("real", 1);
			absComplex2.set("immaginary", 3);
			input.setParameter("firstComplexElement", absComplex1);
			input.setParameter("secondComplexElement", absComplex2);
			output = dc.invoke("sumComplex", input);
			AbsObject sumComplex = output.getParameter("sumComplexReturn");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
