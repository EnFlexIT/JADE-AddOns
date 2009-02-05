/*
 * An XML document type.
 * Localname: getAgentInfoResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.GetAgentInfoResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one getAgentInfoResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class GetAgentInfoResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetAgentInfoResponseDocument
{
    
    public GetAgentInfoResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETAGENTINFORESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "getAgentInfoResponse");
    
    
    /**
     * Gets the "getAgentInfoResponse" element
     */
    public test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse getGetAgentInfoResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse target = null;
            target = (test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse)get_store().find_element_user(GETAGENTINFORESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "getAgentInfoResponse" element
     */
    public void setGetAgentInfoResponse(test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse getAgentInfoResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse target = null;
            target = (test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse)get_store().find_element_user(GETAGENTINFORESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse)get_store().add_element_user(GETAGENTINFORESPONSE$0);
            }
            target.set(getAgentInfoResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "getAgentInfoResponse" element
     */
    public test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse addNewGetAgentInfoResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse target = null;
            target = (test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse)get_store().add_element_user(GETAGENTINFORESPONSE$0);
            return target;
        }
    }
    /**
     * An XML getAgentInfoResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class GetAgentInfoResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetAgentInfoResponseDocument.GetAgentInfoResponse
    {
        
        public GetAgentInfoResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETAGENTINFORETURN$0 = 
            new javax.xml.namespace.QName("", "getAgentInfoReturn");
        
        
        /**
         * Gets the "getAgentInfoReturn" element
         */
        public test.webservice.tests.AgentInfo getGetAgentInfoReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.AgentInfo target = null;
                target = (test.webservice.tests.AgentInfo)get_store().find_element_user(GETAGENTINFORETURN$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "getAgentInfoReturn" element
         */
        public void setGetAgentInfoReturn(test.webservice.tests.AgentInfo getAgentInfoReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.AgentInfo target = null;
                target = (test.webservice.tests.AgentInfo)get_store().find_element_user(GETAGENTINFORETURN$0, 0);
                if (target == null)
                {
                    target = (test.webservice.tests.AgentInfo)get_store().add_element_user(GETAGENTINFORETURN$0);
                }
                target.set(getAgentInfoReturn);
            }
        }
        
        /**
         * Appends and returns a new empty "getAgentInfoReturn" element
         */
        public test.webservice.tests.AgentInfo addNewGetAgentInfoReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.AgentInfo target = null;
                target = (test.webservice.tests.AgentInfo)get_store().add_element_user(GETAGENTINFORETURN$0);
                return target;
            }
        }
    }
}
