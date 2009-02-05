
/**
 * MathFunctionsServiceMessageReceiverInOut.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.4.1  Built on : Aug 13, 2008 (05:03:35 LKT)
 */
package test.webservice.tests;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;

/**
 *  MathFunctionsServiceMessageReceiverInOut message receiver
 */

public class MathFunctionsServiceMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver{


	public void invokeBusinessLogic(org.apache.axis2.context.MessageContext msgContext, org.apache.axis2.context.MessageContext newMsgContext)
	throws org.apache.axis2.AxisFault{

		try {

			// get the implementation class for the Web Service
			Object obj = getTheImplementationObject(msgContext);
			MathFunctionsServiceSkeleton skel = (MathFunctionsServiceSkeleton)obj;
			//Out Envelop
			org.apache.axiom.soap.SOAPEnvelope envelope = null;
			//Find the axisOperation that has been set by the Dispatch phase.
			org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
			if (op == null) {
				throw new org.apache.axis2.AxisFault("Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
			}
			
			boolean headerPresents = msgContext.getEnvelope().getHeader() != null;
			
			java.lang.String methodName;
			if((op.getName() != null) && ((methodName = org.apache.axis2.util.JavaUtils.xmlNameToJava(op.getName().getLocalPart())) != null)){


				if("sum".equals(methodName)){

					test.webservice.tests.SumResponseDocument sumResponse34 = null;
					test.webservice.tests.SumDocument wrappedParam =
						(test.webservice.tests.SumDocument)fromOM(
								msgContext.getEnvelope().getBody().getFirstElement(),
								test.webservice.tests.SumDocument.class,
								getEnvelopeNamespaces(msgContext.getEnvelope()));

					int len = 0;
					if (headerPresents) {
						test.webservice.tests.SumHeaderUsernameDocument username =
							(test.webservice.tests.SumHeaderUsernameDocument)fromOM(
									msgContext.getEnvelope().getHeader().getFirstChildWithName(new javax.xml.namespace.QName("urn:tests.webservice.test", "sumHeaderUsername")),
									test.webservice.tests.SumHeaderUsernameDocument.class,
									getEnvelopeNamespaces(msgContext.getEnvelope()));
						test.webservice.tests.SumHeaderPasswordDocument password =
							(test.webservice.tests.SumHeaderPasswordDocument)fromOM(
									msgContext.getEnvelope().getHeader().getFirstChildWithName(new javax.xml.namespace.QName("urn:tests.webservice.test", "sumHeaderPassword")),
									test.webservice.tests.SumHeaderPasswordDocument.class,
									getEnvelopeNamespaces(msgContext.getEnvelope()));
						
						if (username != null && password != null) {
							len = username.getSumHeaderUsername().length() + password.getSumHeaderPassword().length(); 
						}
					}
					
					sumResponse34 = skel.sum(wrappedParam);
					envelope = toEnvelope(getSOAPFactory(msgContext), sumResponse34, false);
					
					SumHeaderIdDocument sumHeaderIdDocument = test.webservice.tests.SumHeaderIdDocument.Factory.newInstance();
					sumHeaderIdDocument.setSumHeaderId(len);
					OMNode element = toOM(sumHeaderIdDocument);
					envelope.getHeader().addChild(element);

				} else 

					if("getComponents".equals(methodName)){

						test.webservice.tests.GetComponentsResponseDocument getComponentsResponse36 = null;
						test.webservice.tests.GetComponentsDocument wrappedParam =
							(test.webservice.tests.GetComponentsDocument)fromOM(
									msgContext.getEnvelope().getBody().getFirstElement(),
									test.webservice.tests.GetComponentsDocument.class,
									getEnvelopeNamespaces(msgContext.getEnvelope()));

						getComponentsResponse36 =


							skel.getComponents(wrappedParam)
							;

						envelope = toEnvelope(getSOAPFactory(msgContext), getComponentsResponse36, false);
					} else 

						if("printTime".equals(methodName)){

							test.webservice.tests.PrintTimeResponseDocument printTimeResponse38 = null;
							test.webservice.tests.PrintTimeDocument wrappedParam =
								(test.webservice.tests.PrintTimeDocument)fromOM(
										msgContext.getEnvelope().getBody().getFirstElement(),
										test.webservice.tests.PrintTimeDocument.class,
										getEnvelopeNamespaces(msgContext.getEnvelope()));

							printTimeResponse38 =


								skel.printTime(wrappedParam)
								;

							envelope = toEnvelope(getSOAPFactory(msgContext), printTimeResponse38, false);
						} else 

							if("sumComplex".equals(methodName)){

								test.webservice.tests.SumComplexResponseDocument sumComplexResponse40 = null;
								test.webservice.tests.SumComplexDocument wrappedParam =
									(test.webservice.tests.SumComplexDocument)fromOM(
											msgContext.getEnvelope().getBody().getFirstElement(),
											test.webservice.tests.SumComplexDocument.class,
											getEnvelopeNamespaces(msgContext.getEnvelope()));

								sumComplexResponse40 =


									skel.sumComplex(wrappedParam)
									;

								envelope = toEnvelope(getSOAPFactory(msgContext), sumComplexResponse40, false);
							} else 

								if("diff".equals(methodName)){

									test.webservice.tests.DiffResponseDocument diffResponse44 = null;
									test.webservice.tests.DiffDocument wrappedParam =
										(test.webservice.tests.DiffDocument)fromOM(
												msgContext.getEnvelope().getBody().getFirstElement(),
												test.webservice.tests.DiffDocument.class,
												getEnvelopeNamespaces(msgContext.getEnvelope()));
									int len = 0;
									if (headerPresents) {
										test.webservice.tests.DiffHeaderUsernameDocument username =
											(test.webservice.tests.DiffHeaderUsernameDocument)fromOM(
													msgContext.getEnvelope().getHeader().getFirstChildWithName(new javax.xml.namespace.QName("urn:tests.webservice.test", "diffHeaderUsername")),
													test.webservice.tests.DiffHeaderUsernameDocument.class,
													getEnvelopeNamespaces(msgContext.getEnvelope()));
										test.webservice.tests.DiffHeaderPasswordDocument password =
											(test.webservice.tests.DiffHeaderPasswordDocument)fromOM(
													msgContext.getEnvelope().getHeader().getFirstChildWithName(new javax.xml.namespace.QName("urn:tests.webservice.test", "diffHeaderPassword")),
													test.webservice.tests.DiffHeaderPasswordDocument.class,
													getEnvelopeNamespaces(msgContext.getEnvelope()));
										
										if (username != null && password != null) {
											len = username.getDiffHeaderUsername().length() + password.getDiffHeaderPassword().length(); 
										}
									}
									
									diffResponse44 =


										skel.diff(wrappedParam)
										;

									envelope = toEnvelope(getSOAPFactory(msgContext), diffResponse44, false);
									
									DiffHeaderIdDocument diffHeaderIdDocument = test.webservice.tests.DiffHeaderIdDocument.Factory.newInstance();
									diffHeaderIdDocument.setDiffHeaderId(len);
									OMNode element = toOM(diffHeaderIdDocument);
									envelope.getHeader().addChild(element);

								} else 

									if("multiplication".equals(methodName)){

										test.webservice.tests.MultiplicationResponseDocument multiplicationResponse47 = null;
										test.webservice.tests.MultiplicationDocument wrappedParam =
											(test.webservice.tests.MultiplicationDocument)fromOM(
													msgContext.getEnvelope().getBody().getFirstElement(),
													test.webservice.tests.MultiplicationDocument.class,
													getEnvelopeNamespaces(msgContext.getEnvelope()));

										test.webservice.tests.MultiplicationHeaderComplexDocument complex = null;
										if (headerPresents) {
											complex =
												(test.webservice.tests.MultiplicationHeaderComplexDocument)fromOM(
														msgContext.getEnvelope().getHeader().getFirstChildWithName(new javax.xml.namespace.QName("urn:tests.webservice.test", "multiplicationHeaderComplex")),
														test.webservice.tests.MultiplicationHeaderComplexDocument.class,
														getEnvelopeNamespaces(msgContext.getEnvelope()));
										}
										
										multiplicationResponse47 = skel.multiplication(wrappedParam);
										envelope = toEnvelope(getSOAPFactory(msgContext), multiplicationResponse47, false);
										
										if (headerPresents) {
											MultiplicationHeaderComplexDocument multiplicationHeaderComplexDocument = test.webservice.tests.MultiplicationHeaderComplexDocument.Factory.newInstance();
											multiplicationHeaderComplexDocument.setMultiplicationHeaderComplex(complex.getMultiplicationHeaderComplex());
											OMNode element = toOM(multiplicationHeaderComplexDocument);
											envelope.getHeader().addChild(element);
										}
										
									} else 

										if("abs".equals(methodName)){

											test.webservice.tests.AbsResponseDocument absResponse50 = null;
											test.webservice.tests.AbsDocument wrappedParam =
												(test.webservice.tests.AbsDocument)fromOM(
														msgContext.getEnvelope().getBody().getFirstElement(),
														test.webservice.tests.AbsDocument.class,
														getEnvelopeNamespaces(msgContext.getEnvelope()));

											
											if (headerPresents) {
												AbsHeaderAggregateDocument absHeader =
													(test.webservice.tests.AbsHeaderAggregateDocument)fromOM(
															msgContext.getEnvelope().getHeader().getFirstChildWithName(new javax.xml.namespace.QName("urn:tests.webservice.test", "absHeaderAggregate")),
															test.webservice.tests.AbsHeaderAggregateDocument.class,
															getEnvelopeNamespaces(msgContext.getEnvelope()));
											}
											
											
											absResponse50 =


												skel.abs(wrappedParam)
												;

											envelope = toEnvelope(getSOAPFactory(msgContext), absResponse50, false);
										} else 

											if("getRandom".equals(methodName)){

												test.webservice.tests.GetRandomResponseDocument getRandomResponse52 = null;
												test.webservice.tests.GetRandomDocument wrappedParam =
													(test.webservice.tests.GetRandomDocument)fromOM(
															msgContext.getEnvelope().getBody().getFirstElement(),
															test.webservice.tests.GetRandomDocument.class,
															getEnvelopeNamespaces(msgContext.getEnvelope()));

												getRandomResponse52 =


													skel.getRandom(wrappedParam)
													;

												envelope = toEnvelope(getSOAPFactory(msgContext), getRandomResponse52, false);
											} else 

												if("printComplex".equals(methodName)){

													test.webservice.tests.PrintComplexResponseDocument printComplexResponse54 = null;
													test.webservice.tests.PrintComplexDocument wrappedParam =
														(test.webservice.tests.PrintComplexDocument)fromOM(
																msgContext.getEnvelope().getBody().getFirstElement(),
																test.webservice.tests.PrintComplexDocument.class,
																getEnvelopeNamespaces(msgContext.getEnvelope()));

													printComplexResponse54 =


														skel.printComplex(wrappedParam)
														;

													envelope = toEnvelope(getSOAPFactory(msgContext), printComplexResponse54, false);
												} else 

													if("compareNumbers".equals(methodName)){

														test.webservice.tests.CompareNumbersResponseDocument compareNumbersResponse56 = null;
														test.webservice.tests.CompareNumbersDocument wrappedParam =
															(test.webservice.tests.CompareNumbersDocument)fromOM(
																	msgContext.getEnvelope().getBody().getFirstElement(),
																	test.webservice.tests.CompareNumbersDocument.class,
																	getEnvelopeNamespaces(msgContext.getEnvelope()));

														compareNumbersResponse56 =


															skel.compareNumbers(wrappedParam)
															;

														envelope = toEnvelope(getSOAPFactory(msgContext), compareNumbersResponse56, false);
													} else 

														if("getAgentInfo".equals(methodName)){

															test.webservice.tests.GetAgentInfoResponseDocument getAgentInfoResponse58 = null;
															test.webservice.tests.GetAgentInfoDocument wrappedParam =
																(test.webservice.tests.GetAgentInfoDocument)fromOM(
																		msgContext.getEnvelope().getBody().getFirstElement(),
																		test.webservice.tests.GetAgentInfoDocument.class,
																		getEnvelopeNamespaces(msgContext.getEnvelope()));

															getAgentInfoResponse58 =


																skel.getAgentInfo(wrappedParam)
																;

															envelope = toEnvelope(getSOAPFactory(msgContext), getAgentInfoResponse58, false);
														} else 

															if("convertDate".equals(methodName)){

																test.webservice.tests.ConvertDateResponseDocument convertDateResponse60 = null;
																test.webservice.tests.ConvertDateDocument wrappedParam =
																	(test.webservice.tests.ConvertDateDocument)fromOM(
																			msgContext.getEnvelope().getBody().getFirstElement(),
																			test.webservice.tests.ConvertDateDocument.class,
																			getEnvelopeNamespaces(msgContext.getEnvelope()));

																convertDateResponse60 =


																	skel.convertDate(wrappedParam)
																	;

																envelope = toEnvelope(getSOAPFactory(msgContext), convertDateResponse60, false);

															} else {
																throw new java.lang.RuntimeException("method not found");
															}


				newMsgContext.setEnvelope(envelope);
			}
		}
		catch (java.lang.Exception e) {
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
	}

	//

	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.SumDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.SumDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.SumResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.SumResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.SumHeaderUsernameDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.SumHeaderUsernameDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.SumHeaderPasswordDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.SumHeaderPasswordDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.SumHeaderIdDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.SumHeaderIdDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.GetComponentsDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.GetComponentsDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.GetComponentsResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.GetComponentsResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.PrintTimeDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.PrintTimeDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.PrintTimeResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.PrintTimeResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.SumComplexDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.SumComplexDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.SumComplexResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.SumComplexResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.DiffDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.DiffDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.DiffResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.DiffResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.DiffHeaderUsernameDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.DiffHeaderUsernameDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.DiffHeaderPasswordDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.DiffHeaderPasswordDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.DiffHeaderIdDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.DiffHeaderIdDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.MultiplicationDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.MultiplicationDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.MultiplicationResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.MultiplicationResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.MultiplicationHeaderComplexDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.MultiplicationHeaderComplexDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.AbsDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.AbsDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.AbsResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.AbsResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.AbsHeaderAggregateDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.AbsHeaderAggregateDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.GetRandomDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.GetRandomDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.GetRandomResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.GetRandomResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.PrintComplexDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.PrintComplexDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.PrintComplexResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.PrintComplexResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.CompareNumbersDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.CompareNumbersDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.CompareNumbersResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.CompareNumbersResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.GetAgentInfoDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.GetAgentInfoDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.GetAgentInfoResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.GetAgentInfoResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.ConvertDateDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.ConvertDateDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}


	private  org.apache.axiom.om.OMElement  toOM(test.webservice.tests.ConvertDateResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault{


		return toOM(param);


	}

	private org.apache.axiom.om.OMElement toOM(final test.webservice.tests.ConvertDateResponseDocument param)
	throws org.apache.axis2.AxisFault {

		final javax.xml.stream.XMLStreamReader xmlReader = param.newXMLStreamReader();
		while (!xmlReader.isStartElement()) {
			try {
				xmlReader.next();
			} catch (javax.xml.stream.XMLStreamException e) {
				throw org.apache.axis2.AxisFault.makeFault(e);
			}
		}

		org.apache.axiom.om.OMDataSource omDataSource = new org.apache.axiom.om.OMDataSource() {

			public void serialize(java.io.OutputStream outputStream, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(outputStream,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(java.io.Writer writer, org.apache.axiom.om.OMOutputFormat omOutputFormat)
			throws javax.xml.stream.XMLStreamException {
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(writer,xmlOptions.setSaveNoXmlDecl());
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document",e);
				}
			}

			public void serialize(javax.xml.stream.XMLStreamWriter xmlStreamWriter)
			throws javax.xml.stream.XMLStreamException {
				org.apache.axiom.om.impl.MTOMXMLStreamWriter mtomxmlStreamWriter =
					(org.apache.axiom.om.impl.MTOMXMLStreamWriter) xmlStreamWriter;
				try {
					org.apache.xmlbeans.XmlOptions xmlOptions = new org.apache.xmlbeans.XmlOptions();
					param.save(mtomxmlStreamWriter.getOutputStream(),xmlOptions.setSaveNoXmlDecl());
					mtomxmlStreamWriter.getOutputStream().flush();
				} catch (java.io.IOException e) {
					throw new javax.xml.stream.XMLStreamException("Problem with saving document", e);
				}
			}

			public javax.xml.stream.XMLStreamReader getReader()
			throws javax.xml.stream.XMLStreamException {
				return param.newXMLStreamReader();
			}
		};

		return  new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(xmlReader.getName(),
				org.apache.axiom.om.OMAbstractFactory.getOMFactory(),
				omDataSource);
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.SumResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.GetComponentsResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.PrintTimeResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.SumComplexResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.DiffResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.MultiplicationResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.AbsResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.GetRandomResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.PrintComplexResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.CompareNumbersResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.GetAgentInfoResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}

	private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory, test.webservice.tests.ConvertDateResponseDocument param, boolean optimizeContent)
	throws org.apache.axis2.AxisFault {
		org.apache.axiom.soap.SOAPEnvelope envelope = factory.getDefaultEnvelope();
		if (param != null){
			envelope.getBody().addChild(toOM(param, optimizeContent));
		}
		return envelope;
	}



	/**
	 *  get the default envelope
	 */
	 private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory){
		return factory.getDefaultEnvelope();
	}

	public org.apache.xmlbeans.XmlObject fromOM(
			org.apache.axiom.om.OMElement param,
			java.lang.Class type,
			java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault{
		try{


			if (test.webservice.tests.SumDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.SumDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.SumDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.SumResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.SumResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.SumResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.SumHeaderUsernameDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.SumHeaderUsernameDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.SumHeaderUsernameDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.SumHeaderPasswordDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.SumHeaderPasswordDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.SumHeaderPasswordDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.SumHeaderIdDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.SumHeaderIdDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.SumHeaderIdDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.GetComponentsDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.GetComponentsDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.GetComponentsDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.GetComponentsResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.GetComponentsResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.GetComponentsResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.PrintTimeDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.PrintTimeDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.PrintTimeDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.PrintTimeResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.PrintTimeResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.PrintTimeResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.SumComplexDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.SumComplexDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.SumComplexDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.SumComplexResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.SumComplexResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.SumComplexResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.DiffDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.DiffDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.DiffDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.DiffResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.DiffResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.DiffResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.DiffHeaderUsernameDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.DiffHeaderUsernameDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.DiffHeaderUsernameDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.DiffHeaderPasswordDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.DiffHeaderPasswordDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.DiffHeaderPasswordDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.DiffHeaderIdDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.DiffHeaderIdDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.DiffHeaderIdDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.MultiplicationDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.MultiplicationDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.MultiplicationDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.MultiplicationResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.MultiplicationResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.MultiplicationResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.MultiplicationHeaderComplexDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.MultiplicationHeaderComplexDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.MultiplicationHeaderComplexDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.MultiplicationHeaderComplexDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.MultiplicationHeaderComplexDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.MultiplicationHeaderComplexDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.AbsDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.AbsDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.AbsDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.AbsResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.AbsResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.AbsResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.AbsHeaderAggregateDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.AbsHeaderAggregateDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.AbsHeaderAggregateDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.GetRandomDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.GetRandomDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.GetRandomDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.GetRandomResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.GetRandomResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.GetRandomResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.PrintComplexDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.PrintComplexDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.PrintComplexDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.PrintComplexResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.PrintComplexResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.PrintComplexResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.CompareNumbersDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.CompareNumbersDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.CompareNumbersDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.CompareNumbersResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.CompareNumbersResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.CompareNumbersResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.GetAgentInfoDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.GetAgentInfoDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.GetAgentInfoDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.GetAgentInfoResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.GetAgentInfoResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.GetAgentInfoResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.ConvertDateDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.ConvertDateDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.ConvertDateDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}



			if (test.webservice.tests.ConvertDateResponseDocument.class.equals(type)){
				if (extraNamespaces!=null){
					return test.webservice.tests.ConvertDateResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching(),
							new org.apache.xmlbeans.XmlOptions().setLoadAdditionalNamespaces(extraNamespaces));
				}else{
					return test.webservice.tests.ConvertDateResponseDocument.Factory.parse(
							param.getXMLStreamReaderWithoutCaching());
				}
			}


		}catch(java.lang.Exception e){
			throw org.apache.axis2.AxisFault.makeFault(e);
		}
		return null;
	}




	/**
	 *  A utility method that copies the namepaces from the SOAPEnvelope
	 */
	 private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env){
		java.util.Map returnMap = new java.util.HashMap();
		java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
		while (namespaceIterator.hasNext()) {
			org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
			returnMap.put(ns.getPrefix(),ns.getNamespaceURI());
		}
		return returnMap;
	}

	private org.apache.axis2.AxisFault createAxisFault(java.lang.Exception e) {
		org.apache.axis2.AxisFault f;
		Throwable cause = e.getCause();
		if (cause != null) {
			f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
		} else {
			f = new org.apache.axis2.AxisFault(e.getMessage());
		}

		return f;
	}

}//end of class
