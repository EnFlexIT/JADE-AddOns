<%@ page import="jade.core.*" %>
<%@ page import="jade.wrapper.*" %>
<jsp:useBean id="oldsnooper" class="examples.jsp.Snooper" scope="application">
<% try {
    // Get a hold on JADE runtime
    jade.core.Runtime rt = jade.core.Runtime.instance();
    Profile p = new ProfileImpl(false);
    p.setParameter("port", "2099");
   
    System.out.println("Launching the agent container ..."+p);
	  jade.wrapper.AgentContainer ac = rt.createAgentContainer(p);
    System.out.println("Jade Inited()");
    System.out.println("Start");
    ac.acceptNewAgent("oldsnooper", oldsnooper);
   } catch (Exception ex) {
       out.println(ex);
   }
 %>
</jsp:useBean>

<% oldsnooper.snoop(request.getRemoteHost()+" "+(new java.util.Date())+" "+request.getRequestURI()); %>
<HTML>
<BODY>
It works !!!!
</BODY>
</HTML>