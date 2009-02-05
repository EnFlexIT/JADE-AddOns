/*
 * An XML document type.
 * Localname: getAgentInfo
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.GetAgentInfoDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one getAgentInfo(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class GetAgentInfoDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetAgentInfoDocument
{
    
    public GetAgentInfoDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETAGENTINFO$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "getAgentInfo");
    
    
    /**
     * Gets the "getAgentInfo" element
     */
    public test.webservice.tests.GetAgentInfoDocument.GetAgentInfo getGetAgentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetAgentInfoDocument.GetAgentInfo target = null;
            target = (test.webservice.tests.GetAgentInfoDocument.GetAgentInfo)get_store().find_element_user(GETAGENTINFO$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "getAgentInfo" element
     */
    public void setGetAgentInfo(test.webservice.tests.GetAgentInfoDocument.GetAgentInfo getAgentInfo)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetAgentInfoDocument.GetAgentInfo target = null;
            target = (test.webservice.tests.GetAgentInfoDocument.GetAgentInfo)get_store().find_element_user(GETAGENTINFO$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.GetAgentInfoDocument.GetAgentInfo)get_store().add_element_user(GETAGENTINFO$0);
            }
            target.set(getAgentInfo);
        }
    }
    
    /**
     * Appends and returns a new empty "getAgentInfo" element
     */
    public test.webservice.tests.GetAgentInfoDocument.GetAgentInfo addNewGetAgentInfo()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetAgentInfoDocument.GetAgentInfo target = null;
            target = (test.webservice.tests.GetAgentInfoDocument.GetAgentInfo)get_store().add_element_user(GETAGENTINFO$0);
            return target;
        }
    }
    /**
     * An XML getAgentInfo(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class GetAgentInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetAgentInfoDocument.GetAgentInfo
    {
        
        public GetAgentInfoImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
