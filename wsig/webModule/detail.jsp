<%@page 
import="jade.content.onto.Ontology,
	jade.core.AID,
	java.net.URL,
	java.util.Collection,
	java.util.Iterator,
	java.util.List,
	org.uddi4j.util.ServiceKey,
	com.tilab.wsig.store.WSIGService,
	com.tilab.wsig.store.WSIGStore,
	com.tilab.wsig.wsdl.WSDLUtils,
	com.tilab.wsig.WSIGConfiguration,
	com.tilab.wsig.servlet.WSIGServletBase"
%>


<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">
<head>
<title>.: WSIG Console :.</title>
<link rel="stylesheet" href="wsig.css"/>
</head>
<body>
<div class="nav" align="right"><font size="-2"><a href="http://jade.tilab.com/" target="_top">Jade - Java Agent DEvelopment Framework</a></font></div>
<h1>.: WSIG Console :.</h1>
<h3> <a href="index.jsp" class="title">Home</a> - <a href="test.jsp" class="title">SOAP Test</a> - <a href="testRest.jsp" class="title">REST Test</a></h3>

<%

	WSIGConfiguration wsigConfig = (WSIGConfiguration)application.getAttribute(WSIGServletBase.WEBAPP_CONFIGURATION_KEY);
    
	// Get parameter
	String serviceName = request.getParameter("service");

	// Get WSIG Store
	WSIGStore wsigStore = (WSIGStore)application.getAttribute(WSIGServletBase.WEBAPP_STORE_KEY);

	// Get service
	WSIGService service = wsigStore.getService(serviceName);
	
	AID aid = service.getAid();
	String agentName = aid.getName();
	
	Class mapperClass = service.getMapperClass();
	String mapperClassName = "-";
	if (mapperClass != null) {
		mapperClassName = mapperClass.getCanonicalName();
	}
	
	Ontology onto = service.getServiceOntology();
	String ontoName = onto.getName();
	
	String servicePrefix = service.getServicePrefix();
	String servicePrefixName = servicePrefix;
	if (servicePrefix == null || "".equals(servicePrefix)) {
		servicePrefixName = "-";
	} else {
		servicePrefixName = servicePrefixName.substring(0, servicePrefixName.length()-1);
	}
	
	boolean hierarchicalComplexType = service.isHierarchicalComplexType();
	
	String uddiKeyName = "-";
	ServiceKey uddiServiceKey = service.getUddiServiceKey();
	if (uddiServiceKey != null) {
		uddiKeyName = uddiServiceKey.getText();
	}
	
	String wsdlUrl = WSDLUtils.getWsdlUrl(serviceName, request);
%>	

<table width="80%" border="1">
	
	<tr><td colspan="2" class="head"><h2><% out.print(serviceName); %></h2></td></tr>
	<tr>
		<td width="20%" class="title">Name:</td>
		<td class="value"><% out.print(serviceName); %></td>
	</tr>
	<tr>
		<td width="20%" class="title">Prefix:</td>
		<td class="value"><% out.print(servicePrefixName); %></td>
	</tr>
	<tr>
		<td width="20%" class="title">Mapper class:</td>
		<td class="value"><% out.print(mapperClassName); %></td>
	</tr>
	<tr>
		<td width="20%" class="title">Hierarchical complex type:</td>
		<td class="value"><% out.print(hierarchicalComplexType); %></td>
	</tr>
	<tr>
		<td width="20%" class="title">Jade ontology:</td>
		<td class="value"><% out.print(ontoName); %></td>
	</tr>
	<tr>
		<td width="20%" class="title">Jade agent:</td>
		<td class="value"><% out.print(agentName); %></td>
	</tr>
	<tr>
		<td width="20%" class="title">UDDI service key:</td>
		<td class="value"><% out.print(uddiKeyName); %></td>
	</tr>
	<tr>
		<td width="20%" class="title">WSDL url:</td>
		<td class="value"><a href="<%=wsdlUrl%>"><% out.print(wsdlUrl); %></a></td>
	</tr>
	<tr>
		<td width="20%" class="title" valign="top">Operations:</td>
		<td class="value">
			
<%		
			String operationName;
			Iterator itOperations = service.getOperations().iterator();
			while(itOperations.hasNext()) {
				operationName = (String)itOperations.next();
%>				
				<% out.print(operationName); %><br/>
<%				
			}
%>		
			
		</td>
	</tr>

</table>

</body>
</html>