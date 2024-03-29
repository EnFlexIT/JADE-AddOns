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
	// Get WSIG Store
	WSIGStore wsigStore = (WSIGStore)application.getAttribute(WSIGServletBase.WEBAPP_STORE_KEY);
	WSIGConfiguration wsigConfig = (WSIGConfiguration)application.getAttribute(WSIGServletBase.WEBAPP_CONFIGURATION_KEY);

	// WSIG Agent status
	String wsigAgentStatus;
	Boolean wsigActive = (Boolean)application.getAttribute(WSIGServletBase.WEBAPP_ACTIVE_KEY);
	if (wsigActive != null && wsigActive.booleanValue() == true) {
		wsigAgentStatus = "<font color='green'>Active</font>";
		wsigAgentStatus += " ( <a href='"+wsigConfig.getAdminServicesUrl(request)+"/platform/disconnect'>STOP</a> )";
	} else {
		wsigAgentStatus = "<font color='red'><b>Down</b></font>";
		wsigAgentStatus += " ( <a href='"+wsigConfig.getAdminServicesUrl(request)+"/platform/connect'>START</a> )";
	}

	// WSDL style
	String wsdlStyle;
	if ("rpc".equals(wsigConfig.getWsdlStyle())) {
		wsdlStyle = "rpc/encoded";
	} else {
		wsdlStyle = "document/literal (wrapped)";
	}
	
	// Get services list
	WSIGService service;
	Collection services = wsigStore.getAllServices();
	Iterator itServices = services.iterator();

%>	

<table width="80%" border="1">
		<tr><td class="head"><h2>WSIG Services List</h2></td></tr>

<%	
	// For all services
	while(itServices.hasNext()) {
		service = (WSIGService)itServices.next();
		
		// Get service properties
		String serviceName = service.getServiceName();
%>		
		
		<tr>
			<td class="value"><a href="detail.jsp?service=<% out.print(serviceName); %>"><% out.print(serviceName); %></a></td>
		</tr>

<%		
	}
%>

</table>

<br/>

<table width="80%" border="1">
	<tr><td class="head" colspan="2"><h2>WSIG Configuration</h2></td></tr>
	<tr>
		<td width="30%" class="title">JADE WSIG agent status:</td>
		<td class="value"><% out.print(wsigAgentStatus); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">JADE main host:</td>
		<td class="value"><% out.print(wsigConfig.getMainHost()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">JADE main port:</td>
		<td class="value"><% out.print(wsigConfig.getMainPort()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">JADE container name:</td>
		<td class="value"><% out.print(wsigConfig.getContainerName()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">JADE container local port:</td>
		<td class="value"><% out.print(wsigConfig.getLocalPort()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">JADE WSIG agent name:</td>
		<td class="value"><% out.print(wsigConfig.getAgentName()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">JADE WSIG agent class:</td>
		<td class="value"><% out.print(wsigConfig.getAgentClassName()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG version:</td>
		<td class="value"><% out.print(wsigConfig.getWsigVersion()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG resource path:</td>
		<td class="value"><% out.print(wsigConfig.getWsigResourcePath()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG configuration path:</td>
		<td class="value"><% out.print(wsigConfig.getWsigConfPath()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG initializer class:</td>
		<td class="value"><% out.print(wsigConfig.getInitializerClassName()!=null?wsigConfig.getInitializerClassName():""); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG services url:</td>
		<td class="value"><% out.print(wsigConfig.getServicesUrl(request)); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG REST services url:</td>
		<td class="value"><% out.print(wsigConfig.getRESTServicesUrl(request)); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG admin url:</td>
		<td class="value"><% out.print(wsigConfig.getAdminServicesUrl(request)); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG console url:</td>
		<td class="value"><% out.print(wsigConfig.getConsoleUrl(request)); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG timeout (ms):</td>
		<td class="value"><% out.print(wsigConfig.getWsigTimeout()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSIG java type preservation:</td>
		<td class="value"><% out.print(wsigConfig.getPreserveJavaType()!=null?wsigConfig.getPreserveJavaType():""); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSDL hierarchical complex type:</td>
		<td class="value"><% out.print(wsigConfig.isHierarchicalComplexTypeEnable()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSDL local namespace:</td>
		<td class="value"><% out.print(wsigConfig.getLocalNamespacePrefix()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSDL style/use:</td>
		<td class="value"><% out.print(wsdlStyle); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSDL write enable:</td>
		<td class="value"><% out.print(wsigConfig.isWsdlWriteEnable()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">WSDL write path:</td>
		<td class="value"><% out.print(wsigConfig.getWsdlDirectory()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">UDDI enable:</td>
		<td class="value"><% out.print(wsigConfig.isUddiEnable()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">UDDI query manager:</td>
		<td class="value"><% out.print(wsigConfig.getQueryManagerURL()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">UDDI life cycle manager:</td>
		<td class="value"><% out.print(wsigConfig.getLifeCycleManagerURL()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">UDDI business key:</td>
		<td class="value"><% out.print(wsigConfig.getBusinessKey()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">UDDI username:</td>
		<td class="value"><% out.print(wsigConfig.getUserName()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">UDDI password:</td>
		<td class="value"><% out.print(wsigConfig.getUserPassword()); %></td>
	</tr>
	<tr>
		<td width="30%" class="title">UDDI TModel:</td>
		<td class="value"><% out.print(wsigConfig.getTModel()); %></td>
	</tr>
<%
	if (wsigConfig.isJadeMiscPresent()) {
%>	
		<tr>
			<td width="30%" class="title">LOG-MANAGER enable:</td>
			<td class="value"><% out.print(wsigConfig.isLogManagerEnable()); %></td>
		</tr>
		<tr>
			<td width="30%" class="title">LOG-MANAGER name:</td>
			<td class="value"><% out.print(wsigConfig.getLogManagerName()); %></td>
		</tr>
		<tr>
			<td width="30%" class="title">LOG-MANAGER root path:</td>
			<td class="value"><% out.print(wsigConfig.getLogManagerRoot()); %></td>
		</tr>
<%
	}
%>	
</table>

</body>
</html>