
/**
 * MathFunctionsServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
package test.webservice.tests;

import java.util.Date;

import test.webservice.tests.impl.SumResponseDocumentImpl;
/**
 *  MathFunctionsServiceSkeleton java skeleton for the axisService
 */
public class MathFunctionsServiceSkeleton{


	/**
	 * Auto generated method signature
	 * 
	 * @param sum
	 */

	public test.webservice.tests.SumResponseDocument sum
	(
			test.webservice.tests.SumDocument sum
	)
	{
		SumResponseDocument sumResponse = SumResponseDocument.Factory.newInstance();
		float result = sum.getSum().getFirstElement() + sum.getSum().getSecondElement();
		sumResponse.addNewSumResponse().setSumReturn(result);
		return sumResponse;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param getComponents
	 */

	public test.webservice.tests.GetComponentsResponseDocument getComponents
	(
			test.webservice.tests.GetComponentsDocument getComponents
	)
	{
		GetComponentsResponseDocument getComponentsResponseDocument = GetComponentsResponseDocument.Factory.newInstance();
		ArrayOfComplex getComponentsReturn = ArrayOfComplex.Factory.newInstance();
		Complex complex1 = getComponentsReturn.addNewComplex();
		complex1.setReal(getComponents.getGetComponents().getComplex().getReal());
		Complex complex2 = getComponentsReturn.addNewComplex();
		complex2.setReal(getComponents.getGetComponents().getComplex().getImmaginary());
		getComponentsResponseDocument.addNewGetComponentsResponse().setGetComponentsReturn(getComponentsReturn);
		return getComponentsResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param printTime
	 */

	public test.webservice.tests.PrintTimeResponseDocument printTime
	(
			test.webservice.tests.PrintTimeDocument printTime
	)
	{
		System.out.println((new Date()).getTime());
		PrintTimeResponseDocument printTimeResponseDocument = PrintTimeResponseDocument.Factory.newInstance();
		printTimeResponseDocument.addNewPrintTimeResponse();
		return printTimeResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param sumComplex
	 */

	public test.webservice.tests.SumComplexResponseDocument sumComplex
	(
			test.webservice.tests.SumComplexDocument sumComplex
	)
	{
		SumComplexResponseDocument complexResponseDocument = SumComplexResponseDocument.Factory.newInstance();
		float r = sumComplex.getSumComplex().getFirstComplexElement().getReal()+sumComplex.getSumComplex().getSecondComplexElement().getReal();
		float i = sumComplex.getSumComplex().getFirstComplexElement().getImmaginary()+sumComplex.getSumComplex().getSecondComplexElement().getImmaginary();
		Complex sumComplexReturn = Complex.Factory.newInstance();
		sumComplexReturn.setReal(r);
		sumComplexReturn.setImmaginary(i);
		complexResponseDocument.addNewSumComplexResponse().setSumComplexReturn(sumComplexReturn);
		return complexResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param diff
	 */

	public test.webservice.tests.DiffResponseDocument diff
	(
			test.webservice.tests.DiffDocument diff
	)
	{
		DiffResponseDocument diffResponseDocument = DiffResponseDocument.Factory.newInstance();
		float diffReturn = diff.getDiff().getFirstElement()-diff.getDiff().getSecondElement();
		diffResponseDocument.addNewDiffResponse().setDiffReturn(diffReturn);
		return diffResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param multiplication
	 */

	public test.webservice.tests.MultiplicationResponseDocument multiplication
	(
			test.webservice.tests.MultiplicationDocument multiplication
	)
	{
		MultiplicationResponseDocument multiplicationResponseDocument = MultiplicationResponseDocument.Factory.newInstance();
		float[] floatArray = multiplication.getMultiplication().getNumbers().getFloatArray();
		float multiplicationReturn = 1;
		for (float f : floatArray) {
			multiplicationReturn *= f;
		}
		multiplicationResponseDocument.addNewMultiplicationResponse().setMultiplicationReturn(multiplicationReturn);
		return multiplicationResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param abs
	 */

	public test.webservice.tests.AbsResponseDocument abs
	(
			test.webservice.tests.AbsDocument abs
	)
	{
		AbsResponseDocument absResponseDocument = AbsResponseDocument.Factory.newInstance();
		float real = abs.getAbs().getComplex().getReal();
		float immaginary = abs.getAbs().getComplex().getImmaginary();
		float absReturn = Float.parseFloat(Double.toString(Math.sqrt(Math.pow(real, 2) + Math.pow(immaginary, 2))));
		absResponseDocument.addNewAbsResponse().setAbsReturn(absReturn);
		return absResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param getRandom
	 */

	public test.webservice.tests.GetRandomResponseDocument getRandom
	(
			test.webservice.tests.GetRandomDocument getRandom
	)
	{
		GetRandomResponseDocument getRandomResponseDocument = GetRandomResponseDocument.Factory.newInstance();
		Complex getRandomReturn = Complex.Factory.newInstance();
		getRandomReturn.setReal((float)Math.random() * 10);
		getRandomReturn.setImmaginary((float)Math.random() * 10);
		getRandomResponseDocument.addNewGetRandomResponse().setGetRandomReturn(getRandomReturn);
		return getRandomResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param printComplex
	 */

	public test.webservice.tests.PrintComplexResponseDocument printComplex
	(
			test.webservice.tests.PrintComplexDocument printComplex
	)
	{
		System.out.println(printComplex.getPrintComplex().getComplex().getReal()+","+printComplex.getPrintComplex().getComplex().getImmaginary());
		PrintComplexResponseDocument printComplexResponseDocument = PrintComplexResponseDocument.Factory.newInstance();
		printComplexResponseDocument.addNewPrintComplexResponse();
		return printComplexResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param compareNumbers
	 */

	public test.webservice.tests.CompareNumbersResponseDocument compareNumbers
	(
			test.webservice.tests.CompareNumbersDocument compareNumbers
	)
	{
		CompareNumbersResponseDocument compareNumbersResponseDocument = CompareNumbersResponseDocument.Factory.newInstance();
		boolean compareNumbersReturn = compareNumbers.getCompareNumbers().getFirstElement() == compareNumbers.getCompareNumbers().getSecondElement();
		if (!compareNumbersReturn) {
			throw new RuntimeException("Numbers are different");			
		}
		compareNumbersResponseDocument.addNewCompareNumbersResponse().setCompareNumbersReturn(compareNumbersReturn);
		return compareNumbersResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param getAgentInfo
	 */

	public test.webservice.tests.GetAgentInfoResponseDocument getAgentInfo
	(
			test.webservice.tests.GetAgentInfoDocument getAgentInfo
	)
	{
		GetAgentInfoResponseDocument getAgentInfoResponseDocument = GetAgentInfoResponseDocument.Factory.newInstance();
		AgentInfo agentInfo = getAgentInfoResponseDocument.addNewGetAgentInfoResponse().addNewGetAgentInfoReturn();
		AgentIdentifier agentIdentifier = agentInfo.addNewAgentAid();
		agentIdentifier.setName("pippo");
		ArrayOfString addresses = agentIdentifier.addNewAddresses();
		addresses.addString("aaa");
		addresses.addString("bbb");
		addresses.addString("ccc");
		return getAgentInfoResponseDocument;
	}


	/**
	 * Auto generated method signature
	 * 
	 * @param convertDate
	 */

	public test.webservice.tests.ConvertDateResponseDocument convertDate
	(
			test.webservice.tests.ConvertDateDocument convertDate
	)
	{
		ConvertDateResponseDocument convertDateResponseDocument = ConvertDateResponseDocument.Factory.newInstance();
		long millis = convertDate.getConvertDate().getDate().getTimeInMillis();
		String convertDateReturn = Long.valueOf(millis).toString();
		convertDateResponseDocument.addNewConvertDateResponse().setConvertDateReturn(convertDateReturn);
		return convertDateResponseDocument;
	}

}
