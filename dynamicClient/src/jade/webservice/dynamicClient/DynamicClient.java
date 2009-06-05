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

import jade.content.abs.AbsObject;
import jade.content.onto.BasicOntology;
import jade.content.onto.BeanOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.ObjectSchema;
import jade.webservice.utils.CompilerUtils;
import jade.webservice.utils.FileUtils;
import jade.webservice.utils.WSDLUtils;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.wsdl.BindingOperation;
import javax.wsdl.Port;
import javax.wsdl.WSDLElement;
import javax.xml.rpc.holders.Holder;

import org.apache.axis.AxisProperties;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;
import org.apache.axis.message.SOAPHeaderElement;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.GeneratedFileInfo;
import org.apache.axis.wsdl.toJava.GeneratedFileInfo.Entry;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class DynamicClient {

	public static final SimpleDateFormat ISO8601_DATE_FORMAT = new SimpleDateFormat ("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	private static Logger log = Logger.getLogger(DynamicClient.class.getName());
	
	private URL endpoint;
	private String serviceName;
	private String portName;
	private int timeout;
	private String packageName;
	private String tmpDir;
	private boolean noWrap;
	private boolean safeMode;
	private ClassLoader classloader;
	private StringBuilder classPath;
	private String documentation;

	private BeanOntology typeOnto;
	
	private Map<String, ServiceInfo> servicesInfo = new HashMap<String, ServiceInfo>(); 
	

	public DynamicClient() {
		this.tmpDir = System.getProperty("java.io.tmpdir");
		this.noWrap = false;
		this.safeMode = true;

		timeout = -1;
		classloader = Thread.currentThread().getContextClassLoader();
		
		typeOnto = new BeanOntology("WSDL-TYPES");
	}

	public void setClassPath(StringBuilder classPath) {
		this.classPath = classPath;
	}

	public void setEndpoint(URL endpoint) {
		this.endpoint = endpoint;
	}

	public void setService(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setPort(String portName) {
		this.portName = portName;
	}

	public void setTmpDir(String tmpDir) {
		this.tmpDir = tmpDir;
	}

	public void setNoWrap(boolean noWrap) {
		this.noWrap = noWrap;
	}

	public void setSafeMode(boolean safeMode) {
		this.safeMode = safeMode;
	}
	
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	
	public void setTrustStore(String trustStore) {
		System.setProperty("javax.net.ssl.trustStore", trustStore);
	}

	public void setTrustStorePassword(String trustStorePassword) {
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
	}

	public void disableCertificateChecking() {
		AxisProperties.setProperty("axis.socketSecureFactory", "org.apache.axis.components.net.SunFakeTrustSocketFactory");
	}

	public void enableCertificateChecking() {
		AxisProperties.setProperty("axis.socketSecureFactory", "");
	}
	
	public String getDocumentation() {
		return documentation;
	}
	
	public void initClient(URI wsdlUri) throws DynamicClientException {
		boolean localNoWrap = noWrap;
		Exception compilerException = internalInitClient(wsdlUri, localNoWrap);
		if (compilerException != null && safeMode && !localNoWrap) {
			localNoWrap = true;
			compilerException = internalInitClient(wsdlUri, localNoWrap);
		}
		if (compilerException != null) {
			throw new DynamicClientException("Error compiling wsdl-java source files", compilerException);
		}
	}
	
	private Exception internalInitClient(URI wsdlUri, boolean noWrap) throws DynamicClientException {

		File src = null;
		File classes = null;
		try{
			log("Create Dynamic Client for "+wsdlUri);
			log("No-wrap="+noWrap, 1);
			log("Pck-name="+packageName, 1);
			log("Tmp-dir="+tmpDir, 1);
			log("Safe-mode="+safeMode, 1);
			
			// reset service/port
			serviceName = null;
			portName = null;
			
			// Init Axis emitter
			Emitter emitter = new Emitter();
			emitter.setAllWanted(true);
			emitter.setNowrap(noWrap);
			emitter.setPackageName(packageName);
			emitter.setBobMode(true);
	
			// Prepare folders 
			String stem = "DynamicClient-" + System.currentTimeMillis();
			src = new File(tmpDir, stem + "-src");
			if (!src.mkdir()) {
				throw new DynamicClientException("Unable to create working directory " + src.getAbsolutePath());
			}
			classes = new File(tmpDir, stem + "-classes");
			if (!classes.mkdir()) {
				throw new IllegalStateException("Unable to create working directory " + src.getPath());
			}
	
			// Generate webservice classes
			try {
				emitter.setOutputDir(src.getAbsolutePath());
				emitter.run(wsdlUri.toString());
			} catch (SocketException se) {
				throw new DynamicClientException("Wsdl " +wsdlUri.toString()+ " unreachable" ,se);
			} catch (Exception e) {
				throw new DynamicClientException("Error parsing wsdl " +wsdlUri.toString()+ " Cause: " + e.getMessage(), e);
			}
			
			// Prapare classpath
			if(classPath == null) {
				classPath = new StringBuilder();
    			try {
    				CompilerUtils.setupClasspath(classPath, classloader);
    			} catch (Exception e) {
    				throw new DynamicClientException("Unable to create compiler classpath", e);
    			}
			}
	
			// Compile files
			List<File> srcFiles = FileUtils.getFilesRecurse(src, ".+\\.java$"); 
			try {
				CompilerUtils.compileJavaSrc(classPath.toString(), srcFiles, classes.toString());
			} catch (Exception e) {
				return e;
			}
			
			// Create new classloader
			URLClassLoader cl;
			try {
				cl = new URLClassLoader(new URL[] {classes.toURI().toURL()}, classloader);
			} catch (MalformedURLException e) {
				throw new DynamicClientException("Error creating classloader, a directory returns a malformed URL: " + e.getMessage(), e);
			}
	
			// Load generated classes and create schemas
			String className = null;
			try {
				log("Classes loaded in classloader");
				Class clazz;
				Entry fileInfo;
				GeneratedFileInfo generatedFileInfo = emitter.getGeneratedFileInfo();
				for (Object entry: generatedFileInfo.getList()) {
					fileInfo = (Entry)entry;
					
					// Load class in classloader
					className = fileInfo.className; 
					log("("+fileInfo.type+") "+className, 1);
					clazz = cl.loadClass(className);
					
					// If class is of type "complexType" create the schema
					if ("complexType".equals(fileInfo.type)) {
						if (typeOnto.getSchema(clazz) == null) {
							typeOnto.add(clazz);
						}
					}
				}
			} catch (ClassNotFoundException e) {
				throw new DynamicClientException("Error loading class "+className, e);
			} catch (OntologyException e) {
				throw new DynamicClientException("Error creating schema for class "+className, e);
			}
	
			// Set new classloader as default
			Thread.currentThread().setContextClassLoader(cl);	       
			classloader = Thread.currentThread().getContextClassLoader();
			
			// Parse wsdl and populate internal structure
			try {
				parseWsdl(emitter);
			} catch (Exception e) {
				throw new DynamicClientException("Error parsing wsdl", e);
			}
			
			// Log ontology
			logOntology();

			log("Dymanic client ready!");
			
		} 
		catch(DynamicClientException e) {
			log.debug("", e);
			throw e;
		}		
		finally {
			// Remove classes files
			if (classes != null) {
				FileUtils.removeDir(classes);
			}
			
			// Remove src files
			if (src != null) {
				FileUtils.removeDir(src);
			}
		}
		
		return null;
	}	

	public Ontology getOntology() {
		return typeOnto;
	}
	
	public Set<String> getServiceNames() {
		return servicesInfo.keySet();
	}

	public ServiceInfo getService(String serviceName) {
		if (serviceName == null && servicesInfo.values().iterator().hasNext()) {
			return servicesInfo.values().iterator().next();
		}
		
		return servicesInfo.get(serviceName);
	}
	
	private void parseWsdl(Emitter emitter) throws DynamicClientException, OntologyException, ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		// Set wsdl definition documentation
		documentation = getDocumentation(emitter.getSymbolTable().getDefinition());
		
		// Manage services
		servicesInfo.clear();
		List<ServiceEntry> services = WSDLUtils.getServices(emitter);
		for (ServiceEntry serviceEntry : services) {
			String serviceName = serviceEntry.getOriginalServiceName();
			log("Parse service "+serviceName);
			Service locator = getLocator(serviceEntry); 
			
			ServiceInfo serviceInfo = new ServiceInfo(serviceName, locator);
			serviceInfo.setDocumentation(getDocumentation(serviceEntry.getService()));
			servicesInfo.put(serviceName, serviceInfo);

			// Manage ports
			Collection<Port> ports = WSDLUtils.getPorts(serviceEntry);
			for (Port port : ports) {
				
				String portName = port.getName();
				log("port "+portName, 1);
				Stub stub = getStub(emitter, port, locator);
				if (stub != null) {
					PortInfo portInfo = new PortInfo(portName, stub);
					portInfo.setDocumentation(getDocumentation(port));
					serviceInfo.putPort(portName, portInfo);
					
					// Manage operations
					List<BindingOperation> operations = WSDLUtils.getOperations(port);
					for (BindingOperation bindingOperation : operations) {
	
						String operationName = bindingOperation.getName();
						log("operation "+operationName, 2);
						OperationInfo operationInfo = new OperationInfo(operationName);
						
						// Get and add operation documentation from portType and binding 
						String opDoc = getDocumentation(bindingOperation);
						String opDoc2 = getDocumentation(bindingOperation.getOperation());
						if (opDoc2 != null) {
							opDoc = opDoc + (opDoc != null ? " ": "") + opDoc2; 
						}
						operationInfo.setDocumentation(opDoc);

						portInfo.putOperation(operationName, operationInfo);
	
						// Manage parameters & headers
						OperationParser opParser;
						try {
							// From axis information -> create ontology types, parameters and headers list  
							opParser = new OperationParser(bindingOperation, port.getBinding(), emitter, typeOnto, classloader);
						} catch (ClassNotFoundException e) {
							throw new DynamicClientException(e);
						} 
						
						// Get parameters
						List<ParameterInfo> parameters = opParser.getParameters();
						for (ParameterInfo parameterInfo : parameters) {
							operationInfo.putParameter(parameterInfo.getName(), parameterInfo);
							
							log("parameter: "+parameterInfo, 3);
						}
						
						// Get explicit headers
						Collection<HeaderInfo> explicitHeaders = opParser.getExplicitHeaders();
						for (HeaderInfo headerInfo : explicitHeaders) {
							operationInfo.putHeader(headerInfo.getName(), headerInfo);
							
							log("explicit header: "+headerInfo, 3);
						}
						
						// Get implicit headers
						Collection<HeaderInfo> implicitHeaders = opParser.getImplicitHeaders();
						for (HeaderInfo headerInfo : implicitHeaders) {
							operationInfo.putHeader(headerInfo.getName(), headerInfo);
							
							log("implicit header: "+headerInfo, 3);
						}
						
						// Retrieve and save in operationInfo the stub method associated to operation
						operationInfo.manageOperationStubMethod(stub);
					}
				}
			}
		}
	}

	public WSData invoke(String operation, WSData input) throws DynamicClientException, RemoteException {
		
		try {
			// Get operation information
			ServiceInfo serviceInfo = getService(serviceName);
			PortInfo portInfo = serviceInfo.getPort(portName);
			OperationInfo operationInfo = portInfo.getOperation(operation);
			if (operationInfo == null) {
				throw new DynamicClientException("Operation "+operation+" not present in service "+serviceInfo.getName()+", port "+portInfo.getName());
			}
			
			log("Invoke "+serviceInfo.getName()+"->"+portInfo.getName()+"->"+operationInfo.getName());
			log("Input\n"+input, 1);
			
			// Get axis stub
			Stub stub = portInfo.getStub();
			
			// Set webservice endpoint
			if (endpoint != null) {
				stub._setProperty(Stub.ENDPOINT_ADDRESS_PROPERTY, endpoint.toExternalForm());
			}
			
			// Set webservice call timeout
			if (timeout >= 0) {
				stub.setTimeout(timeout);
			}
			
			// Get axis-stub method parameters (mix of params & headers) 
			Vector<ParameterInfo> methodParams = operationInfo.getStubMethodParameters();
			
			// Create vector of axis-stub parameters object
			Object[] methodValuesObj = new Object[methodParams.size()]; 
			
			// Loop all method parameters to create array of values
			for (int index=0; index<methodParams.size(); index++) {
				ParameterInfo methodParam = methodParams.get(index);
				
				// Get abs values from input
				AbsObject methodParamAbs = null;
				if (input != null) {
					if (methodParam instanceof HeaderInfo) {
						methodParamAbs = input.getHeader(methodParam.getName());
					} else {
						methodParamAbs = input.getParameter(methodParam.getName());
					}
				}
				
				// Convert abs into object
				Object methodParamValue = convertAbsToObj(methodParam, methodParamAbs);
				
				// Add method parameter value to array
				methodValuesObj[index] = methodParamValue;
			}
			
			// Loop for all explicit headers and set it in the call
			if (input != null) {
				java.util.Iterator<String> ith = operationInfo.getInputHeaderNames().iterator();
				while(ith.hasNext()) {
					HeaderInfo hi = operationInfo.getInputHeader(ith.next());
					String headerName = hi.getName();
					int signaturePosition = hi.getSignaturePosition();
		
					// If header explicit
					if (signaturePosition == HeaderInfo.EXPLICIT_HEADER) {
						
						// If exist header value -> set it in call
						AbsObject headerAbs = input.getHeader(headerName);
						if (headerAbs != null) {
							stub.setHeader(hi.getNamespace(), headerName, convertAbsToObj(hi, headerAbs));
						}
					}
				}
			}
			
			// Invoke operation with stub method
			Object returnValue = null;
			Method operationMethod = operationInfo.getOperationMethod();			
			try {
				returnValue = operationMethod.invoke(stub, methodValuesObj);
			} catch (InvocationTargetException ie) {
				if (ie.getCause() instanceof RemoteException) {
					throw (RemoteException)ie.getCause();
				} else {
					throw new DynamicClientException("Error invoking operation "+operation+", service "+serviceInfo.getName()+", port "+portInfo.getName(), ie.getCause());
				}
			} catch (Exception e) {
				throw new DynamicClientException("Error invoking operation "+operation+", service "+serviceInfo.getName()+", port "+portInfo.getName(), e);
			}
	
			// Prepare results
			WSData output = new WSData();
	
			// Read explicit headers from webservice call
			java.util.Iterator<String>ith = operationInfo.getOutputHeaderNames().iterator();
			while(ith.hasNext()) {
				HeaderInfo hi = operationInfo.getOutputHeader(ith.next());
				String headerName = hi.getName();
				int signaturePosition = hi.getSignaturePosition();
				
				// If header is explicit
				if (signaturePosition == HeaderInfo.EXPLICIT_HEADER) {
			
					// Get response header value
					AbsObject headerAbs = getHeaderAbsValue(stub, hi);
					if (headerAbs != null) {
						// Insert output values into headers map
						output.setHeader(headerName, headerAbs);
					}
				}
			}
				
			// Loop all method parameters to read method output values
			for (int index=0; index<methodParams.size(); index++) {
				ParameterInfo methodParam = methodParams.get(index);
				String paramName = methodParam.getName();
				int paramMode = methodParam.getMode();
	
				// Elaborate only output params
				if (paramMode == ParameterInfo.OUT ||
					paramMode == ParameterInfo.INOUT) {
	
					// Get holder value
					Holder paramHolderValue = (Holder)methodValuesObj[index];
	
					// Convert holder value in real value
					Class paramValueClass = JavaUtils.getHolderValueType(paramHolderValue.getClass());
					Object methodParamValue = JavaUtils.convert(paramHolderValue, paramValueClass);
					
					// Convert object in relative abs
					AbsObject absValue = convertObjToAbs(methodParam, methodParamValue);
					
					// Set methodParam in actual headers or params
					if (methodParam instanceof HeaderInfo) {
						output.setHeader(paramName, absValue);
					} else {
						output.setParameter(paramName, absValue);
					}
				}			
			}		
			
			// Set return value if present
			ParameterInfo returnParameter = operationInfo.getStubMethodReturnParameter();
			if (returnParameter != null) {
				AbsObject absValue = convertObjToAbs(returnParameter, returnValue);
				output.setParameter(returnParameter.getName(), absValue);
			}
				
			log("Output\n"+output, 1);
			
			return output;
			
		} catch(DynamicClientException e) {
			log.debug("", e);
			throw e;
		}
	}

	String getDocumentation(WSDLElement element) {
		String documentation = null;
		if (element != null) {
			Element documentationElement = element.getDocumentationElement();
			if (documentationElement != null) {
		        Node child = documentationElement.getFirstChild();
		        if (child != null) {
		        	documentation = child.getNodeValue();
		        }
			}
		}
        return documentation;
	}
	
	private Service getLocator(ServiceEntry axisService) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		String locatorClassName = axisService.getName() + "Locator";
		Class locatorClass = classloader.loadClass(locatorClassName);
		return (Service)locatorClass.newInstance();
	}
	
	private Stub getStub(Emitter emitter, Port axisPort, Service locator) {
		Stub stub = null;
		String portNameJavaId = WSDLUtils.buildPortNameJavaId(emitter.getSymbolTable(), axisPort);
		try {
			Method stubMethod = locator.getClass().getMethod("get"+portNameJavaId, new Class[0]);
			stub = (Stub)stubMethod.invoke(locator, new Object[0]);
		} catch(Exception e) {
			log.warn("Port "+portNameJavaId+" not found in locator");
		}
		return stub;
	}
	
	private Object convertAbsToObj(ParameterInfo pi, AbsObject abs) throws DynamicClientException {
		Object value;
		if (pi.getMode() == ParameterInfo.OUT) {
			// Required output value -> create holder object 
			try {
				value = pi.getTypeClass().newInstance();
			} catch (Exception e) {
				throw new DynamicClientException("Parameter "+pi.getName()+" error creating instance of "+pi.getTypeClass());
			}
		} else {
			// Assigned input or in-out value
			try {
				// Convert abs with ontology
				value = typeOnto.toObject(abs);
				value = BasicOntology.adjustPrimitiveValue(value, pi.getTypeClass());
				
				// Check if the parameter is a Calendar class
				if (Calendar.class.isAssignableFrom(pi.getTypeClass())) {

					// Convert from String (W3C-ISO8601)to Date
					if (value instanceof String) {
						value = ISO8601_DATE_FORMAT.parse((String)value);
					}
					
					// Convert from Date to Calendar
					if (value instanceof Date) {
						Calendar calendar = new GregorianCalendar();
						calendar.setTime((Date)value);
						value = calendar;
					}
				}
				
				// Check if value is a jade list and parameter a java array
				else if (value instanceof jade.util.leap.List &&
					pi.getTypeClass().isArray()) {
					
					// Convert the jade list in java Array
					jade.util.leap.List jadeList = (jade.util.leap.ArrayList)value;
					Object javaArray = Array.newInstance(pi.getTypeClass().getComponentType(), jadeList.size());
					for (int i = 0; i < jadeList.size(); i++) {
						Array.set(javaArray, i, jadeList.get(i));
					}
					value = javaArray;
				}

			} catch (Exception e) {
				throw new DynamicClientException("Parameter "+pi.getName()+" error converting from abs "+abs, e);
			}
		}
		
		return value;
	}
	
	private AbsObject convertObjToAbs(ParameterInfo pi, Object value) throws DynamicClientException {
		AbsObject absObject = null;
		if (value != null) {
			try {
				
				// Check if value is a Calendar object and the parameter schema a DATE
				if (value instanceof Calendar &&
					pi.getSchema().getTypeName().equals(BasicOntology.DATE)) {
					
					// Convert the Calendar object into Date object
					value = ((Calendar) value).getTime();
				}
				
				// Check if value is a java Array object and the parameter schema a SEQUENCE
				else if (value.getClass().isArray() &&
					pi.getSchema().getTypeName().equals(BasicOntology.SEQUENCE)) {
					
					// Convert the java Array object into jade list object
					jade.util.leap.List jadeList = new jade.util.leap.ArrayList();
					for (int i = 0; i < Array.getLength(value) ; i++) {
						jadeList.add(Array.get(value, i));
					}
					value = jadeList;
				}
				
				// Convert value with ontology
				absObject = typeOnto.fromObject(value);
				
			} catch (OntologyException e) {
				throw new DynamicClientException("Parameter "+pi.getName()+" error converting to abs "+value, e);
			}
		}
		return absObject;
	}
	
	private AbsObject getHeaderAbsValue(Stub stub, HeaderInfo hi) throws DynamicClientException {

		AbsObject absObject = null;
		String name = hi.getName();
		
		// Try with namespace
		SOAPHeaderElement header = stub.getResponseHeader(hi.getNamespace(), name);
		if (header == null) {
			// Try without namespace
			header = stub.getResponseHeader(null, name);
		}
		Object headerValue = null;
		if (header != null) {
			// Get value
			try {
				headerValue = header.getObjectValue(hi.getTypeClass());
			} catch (Exception e) {
				throw new DynamicClientException("Header "+name+" error getting value");
			}

			// Convert object in relative abs
			absObject = convertObjToAbs(hi, headerValue);
		}
		return absObject;
	}

	private void log(String message) {
		log(message, 0);
	}
	
	private void log(String message, int tabNumber) {
		if (log.isDebugEnabled()) {
			StringBuilder sb = new StringBuilder(); 
			for(int i = 0; i < tabNumber; i++) {
				sb.append("\t");
			}
			sb.append(message);
			log.debug(sb.toString());
		}		
	}

	private void logOntology() {
		if (log.isDebugEnabled()) {
			try {
				StringBuilder sb = new StringBuilder();
				sb.append("Ontology "+typeOnto.getName()+"\n");
				Iterator iter = typeOnto.getConceptNames().iterator();
				String conceptName;
				ObjectSchema os;
				while (iter.hasNext()) {
					conceptName = (String)iter.next();
					os = typeOnto.getSchema(conceptName);
					sb.append("  concept "+conceptName+" ::= {\n");
					String[] names = os.getNames();
					for (int i = 0; i < names.length; i++) {
						sb.append("    "+names[i]+": ");
						boolean mandatory = os.isMandatory(names[i]);
						ObjectSchema schema = os.getSchema(names[i]);
						if (schema == null) {
							sb.append("ERROR: no schema!\n");
						} else {
							sb.append(schema.getTypeName()+ (!mandatory ? " (OPTIONAL)":"") + "\n");
						}
					}
					sb.append("  }\n");
				}
				log(sb.toString());
				
			} catch(Exception e) {
			}
		}
	}
	
}
