<%@page 
import="com.tilab.wsig.WSIGConfiguration,
		com.tilab.wsig.rest.RestClient,
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
	// Get configuration
	WSIGConfiguration wsigConfig = (WSIGConfiguration)application.getAttribute(WSIGServletBase.WEBAPP_CONFIGURATION_KEY);

	// Get parameters
	String Uri = request.getParameter("restUri");
	if (Uri == null) {
		Uri = wsigConfig.getRESTServicesUrl(request);
	}

	String accept = request.getParameter("accept");
	if (accept == null) {
		accept = "application/xml";
	}
	
	String contentType = request.getParameter("contentType");
	if (contentType == null) {
		contentType = "";
	}
	
	String BodyRequest = request.getParameter("bodyRequest");
	if (BodyRequest == null) {
		BodyRequest = "";
	}

	String BodyResponse = "";
	if (BodyResponse != null && !"".equals(BodyRequest)) {
		BodyResponse = RestClient.sendStringMessage(Uri, BodyRequest, accept, contentType);
	}

%>	



<form method="post" action="testRest.jsp">
<div>
<b>Request Type</b>   
	<select name="contentType">
	  <option value="application/xml">application/xml</option>
	  <option value="application/json">application/json</option>
	</select>					
<b>Response Type</b>
	<select name="accept">
	  <option value="application/xml">application/xml</option>
	  <option value="application/json">application/json</option>
	</select>	
</div>	
	<table width="70%" border="1">
		<tr><td colspan="2" class="head"><h2>Test page</h2></td></tr>
		<tr>
			<td width="20%" class="title">WebService uri:</td>
			<td class="value"><input type="text" name="restUri" size="80" value="<% out.print(Uri); %>"></input> (NOTE: add service name in the path)</td>
		</tr>
		<tr>
			<td width="20%" class="title">Body request:</td>
			<td class="value"><textarea cols=70 rows=10 WRAP="physical" name="bodyRequest"><% out.print(BodyRequest); %></textarea></td>
			
		</tr>
		<tr>
			<td width="20%" class="title">Body response:</td>
			<td class="value"><textarea cols=70 rows=10 WRAP="physical" name="bodyResponse"><% out.print(BodyResponse); %></textarea></td>
		</tr>
		<tr>
			<td width="20%" class="title"></td>
			<td class="value"><input type="submit" value="Send"></input><button type="reset">Reset</button></td>
		</tr>
	</table>
</form>

</body>
</html>