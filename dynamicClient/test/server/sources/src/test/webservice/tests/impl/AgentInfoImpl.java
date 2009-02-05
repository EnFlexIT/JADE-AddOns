/*
 * XML Type:  agentInfo
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.AgentInfo
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * An XML agentInfo(@urn:tests.webservice.test).
 *
 * This is a complex type.
 */
public class AgentInfoImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.AgentInfo
{
    
    public AgentInfoImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AGENTAID$0 = 
        new javax.xml.namespace.QName("", "agentAid");
    private static final javax.xml.namespace.QName STARTDATE$2 = 
        new javax.xml.namespace.QName("", "startDate");
    
    
    /**
     * Gets the "agentAid" element
     */
    public test.webservice.tests.AgentIdentifier getAgentAid()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AgentIdentifier target = null;
            target = (test.webservice.tests.AgentIdentifier)get_store().find_element_user(AGENTAID$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "agentAid" element
     */
    public void setAgentAid(test.webservice.tests.AgentIdentifier agentAid)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AgentIdentifier target = null;
            target = (test.webservice.tests.AgentIdentifier)get_store().find_element_user(AGENTAID$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.AgentIdentifier)get_store().add_element_user(AGENTAID$0);
            }
            target.set(agentAid);
        }
    }
    
    /**
     * Appends and returns a new empty "agentAid" element
     */
    public test.webservice.tests.AgentIdentifier addNewAgentAid()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AgentIdentifier target = null;
            target = (test.webservice.tests.AgentIdentifier)get_store().add_element_user(AGENTAID$0);
            return target;
        }
    }
    
    /**
     * Gets the "startDate" element
     */
    public java.util.Calendar getStartDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STARTDATE$2, 0);
            if (target == null)
            {
                return null;
            }
            return target.getCalendarValue();
        }
    }
    
    /**
     * Gets (as xml) the "startDate" element
     */
    public org.apache.xmlbeans.XmlDateTime xgetStartDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(STARTDATE$2, 0);
            return target;
        }
    }
    
    /**
     * Sets the "startDate" element
     */
    public void setStartDate(java.util.Calendar startDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(STARTDATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(STARTDATE$2);
            }
            target.setCalendarValue(startDate);
        }
    }
    
    /**
     * Sets (as xml) the "startDate" element
     */
    public void xsetStartDate(org.apache.xmlbeans.XmlDateTime startDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlDateTime target = null;
            target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(STARTDATE$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(STARTDATE$2);
            }
            target.set(startDate);
        }
    }
}
