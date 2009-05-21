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
package jade.webservice.tests;

import jade.content.abs.AbsAggregate;
import jade.content.abs.AbsHelper;
import jade.content.abs.AbsObject;
import jade.content.abs.AbsPrimitive;
import jade.content.onto.OntologyException;
import jade.webservice.dynamicClient.DynamicClient;
import jade.webservice.dynamicClient.DynamicClientException;
import jade.webservice.dynamicClient.OperationInfo;
import jade.webservice.dynamicClient.WSData;

import java.net.URI;
import java.util.Date;


import junit.framework.Assert;
import junit.framework.TestCase;

public class DynamicClientTestCase extends TestCase {

	private static DynamicClient dynamicClient;

	@Override
	protected void setUp() throws Exception {
		
		if (dynamicClient == null) {
			// Create dynamic client
			try {
				dynamicClient = new DynamicClient();
				dynamicClient.initClient(new URI("http://localhost:8080/axis2/services/MathFunctionsService?wsdl"));
				
			} catch(DynamicClientException e) {
				e.printStackTrace();
				throw e;
			}
		}
		
		super.setUp();
	}

	private static AbsObject putInputHeader(WSData input, OperationInfo oi, String name) throws OntologyException {
		AbsObject abs = oi.getInputHeader(name).getSchema().newInstance();
		input.setHeader(name, abs);
		return abs; 
	}
	
	private static AbsObject putInputParameter(WSData input, OperationInfo oi, String name) throws OntologyException {
		AbsObject abs = oi.getInputParameter(name).getSchema().newInstance();
		input.setParameter(name, abs);
		return abs; 
	}

	private static OperationInfo getOperation(String operation) {
		return dynamicClient.getService(null).getPort(null).getOperation(operation);
	}
	
	public void testMultiplication() throws Exception {
		String operation = "multiplication";
		WSData input = new WSData();
		OperationInfo oi = getOperation(operation);
		
		AbsAggregate numbers = (AbsAggregate)DynamicClientTestCase.putInputParameter(input, oi, "numbers");
		numbers.add(AbsPrimitive.wrap(2.0f));
		numbers.add(AbsPrimitive.wrap(3.0f));
		AbsObject headerComplex = DynamicClientTestCase.putInputHeader(input, oi, "multiplicationHeaderComplex");
		AbsHelper.setAttribute(headerComplex, "real", AbsPrimitive.wrap(2.0f));
		AbsHelper.setAttribute(headerComplex, "immaginary", AbsPrimitive.wrap(3.0f));

		WSData output = dynamicClient.invoke(operation, input);
		
		Float mulResult = output.getParameterFloat("multiplicationReturn");
		Assert.assertTrue(mulResult == 6.0f);
	}

	public void testSumComplex() throws Exception {
		String operation = "sumComplex";
		
		AbsObject complex1 = dynamicClient.getOntology().getSchema("Complex").newInstance();
		AbsHelper.setAttribute(complex1, "real", AbsPrimitive.wrap(2.0f));
		AbsHelper.setAttribute(complex1, "immaginary", AbsPrimitive.wrap(3.0f));
		AbsObject complex2 = dynamicClient.getOntology().getSchema("Complex").newInstance();
		AbsHelper.setAttribute(complex2, "real", AbsPrimitive.wrap(4.0f));
		AbsHelper.setAttribute(complex2, "immaginary", AbsPrimitive.wrap(5.0f));

		WSData input = new WSData();
		input.setParameter("secondComplexElement", complex2);
		input.setParameter("firstComplexElement", complex1);

		WSData output = dynamicClient.invoke(operation, input);
		
		AbsObject sumResult = (AbsObject)output.getParameter("sumComplexReturn");
		Assert.assertTrue(((AbsPrimitive)sumResult.getAbsObject("real")).getFloat() == 6.0f);
		Assert.assertTrue(((AbsPrimitive)sumResult.getAbsObject("immaginary")).getFloat() == 8.0f);
	}
	
	public void testAbs() throws Exception {
		String operation = "abs";
		WSData input = new WSData();
		OperationInfo oi = getOperation(operation);
		
		AbsObject complex = DynamicClientTestCase.putInputParameter(input, oi, "complex");
		AbsHelper.setAttribute(complex, "real", AbsPrimitive.wrap(2.0f));
		AbsHelper.setAttribute(complex, "immaginary", AbsPrimitive.wrap(3.0f));
		AbsObject headerAggregate = DynamicClientTestCase.putInputHeader(input, oi, "absHeaderAggregate");
		AbsHelper.setAttribute(headerAggregate, "username", AbsPrimitive.wrap("aaa"));
		AbsHelper.setAttribute(headerAggregate, "password", AbsPrimitive.wrap("bbb"));

		WSData output = dynamicClient.invoke(operation, input);
		
		Float absResult = output.getParameterFloat("absReturn");
		Assert.assertTrue(absResult == Float.parseFloat(Double.toString(Math.sqrt(Math.pow(2, 2) + Math.pow(3, 2)))));
	}

	public void testGetRandom() throws Exception {
		String operation = "getRandom";

		WSData output = dynamicClient.invoke(operation, null);
		
		AbsObject randomResult = output.getParameter("getRandomReturn");
		Assert.assertTrue("Complex".equals(randomResult.getTypeName()));
	}

	public void testGetAgentInfo() throws Exception {
		String operation = "getAgentInfo";

		WSData output = dynamicClient.invoke(operation, null);
		
		AbsObject randomResult = output.getParameter("getAgentInfoReturn");
		Assert.assertTrue("AgentInfo".equals(randomResult.getTypeName()));
	}
	
	public void testPrintTime() throws Exception {
		String operation = "printTime";

		dynamicClient.invoke(operation, null);
	}

	public void testPrintComplex() throws Exception {
		String operation = "printComplex";
		WSData input = new WSData();
		OperationInfo oi = getOperation(operation);
		
		AbsObject complex = DynamicClientTestCase.putInputParameter(input, oi, "complex");
		AbsHelper.setAttribute(complex, "real", AbsPrimitive.wrap(2.0f));
		AbsHelper.setAttribute(complex, "immaginary", AbsPrimitive.wrap(3.0f));

		dynamicClient.invoke(operation, input);
	}
	
	public void testCompareNumbers() throws Exception {
		String operation = "compareNumbers";

		WSData input = new WSData();
		input.setParameter("firstElement", 2.0f);
		input.setParameter("secondElement", 2.0f);

		WSData output = dynamicClient.invoke(operation, input);

		boolean compareResult = output.getParameterBoolean("compareNumbersReturn");
		Assert.assertTrue(compareResult);
	}
	
	public void testGetComponents() throws Exception {
		String operation = "getComponents";
		WSData input = new WSData();
		OperationInfo oi = getOperation(operation);
		
		AbsObject complex = DynamicClientTestCase.putInputParameter(input, oi, "complex");
		AbsHelper.setAttribute(complex, "real", AbsPrimitive.wrap(2.0f));
		AbsHelper.setAttribute(complex, "immaginary", AbsPrimitive.wrap(3.0f));

		WSData output = dynamicClient.invoke(operation, input);
		
		AbsAggregate mulResult = (AbsAggregate)output.getParameter("getComponentsReturn");
		Assert.assertTrue(((AbsPrimitive)mulResult.get(0).getAbsObject("real")).getFloat() == 2);
		Assert.assertTrue(((AbsPrimitive)mulResult.get(1).getAbsObject("real")).getFloat() == 3);
	}

	public void testConvertDate() throws Exception {
		String operation = "convertDate";

		WSData input = new WSData();
		input.setParameter("date", new Date());

		WSData output = dynamicClient.invoke(operation, input);
		
		String dateResult = output.getParameterString("convertDateReturn");
		Assert.assertTrue(!dateResult.equals(""));
	}
	
	public void testSum() throws Exception {
		String operation = "sum";

		WSData input = new WSData();
		input.setParameter("firstElement", 2.0f);
		input.setParameter("secondElement", 3.0f);
		input.setHeader("sumHeaderUsername", "aaa");
		input.setHeader("sumHeaderPassword", "bbb");

		WSData output = dynamicClient.invoke(operation, input);
		
		float absResult = output.getParameterFloat("sumReturn");
		Assert.assertTrue(absResult == 5.0f);
		int sumHeaderId = output.getHeaderInteger("sumHeaderId");
		Assert.assertTrue(sumHeaderId == 6);
	}

	public void testDiff() throws Exception {
		String operation = "diff";

		WSData input = new WSData();
		input.setParameter("firstElement", 3.0f);
		input.setParameter("secondElement", 2.0f);
		input.setHeader("diffHeaderUsername", "aaa");
		input.setHeader("diffHeaderPassword", "bbb");
		
		WSData output = dynamicClient.invoke(operation, input);
		
		float absResult = output.getParameterFloat("diffReturn");
		Assert.assertTrue(absResult == 1.0f);
		int sumHeaderId = output.getHeaderInteger("diffHeaderId");
		Assert.assertTrue(sumHeaderId == 6);
	}
	
}
