/*
 * XML Type:  agent-identifier
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.AgentIdentifier
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * An XML agent-identifier(@urn:tests.webservice.test).
 *
 * This is a complex type.
 */
public class AgentIdentifierImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.AgentIdentifier
{
    
    public AgentIdentifierImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName NAME$0 = 
        new javax.xml.namespace.QName("", "name");
    private static final javax.xml.namespace.QName ADDRESSES$2 = 
        new javax.xml.namespace.QName("", "addresses");
    private static final javax.xml.namespace.QName RESOLVERS$4 = 
        new javax.xml.namespace.QName("", "resolvers");
    
    
    /**
     * Gets the "name" element
     */
    public java.lang.String getName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "name" element
     */
    public org.apache.xmlbeans.XmlString xgetName()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "name" element
     */
    public void setName(java.lang.String name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(NAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(NAME$0);
            }
            target.setStringValue(name);
        }
    }
    
    /**
     * Sets (as xml) the "name" element
     */
    public void xsetName(org.apache.xmlbeans.XmlString name)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(NAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(NAME$0);
            }
            target.set(name);
        }
    }
    
    /**
     * Gets the "addresses" element
     */
    public test.webservice.tests.ArrayOfString getAddresses()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ArrayOfString target = null;
            target = (test.webservice.tests.ArrayOfString)get_store().find_element_user(ADDRESSES$2, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "addresses" element
     */
    public void setAddresses(test.webservice.tests.ArrayOfString addresses)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ArrayOfString target = null;
            target = (test.webservice.tests.ArrayOfString)get_store().find_element_user(ADDRESSES$2, 0);
            if (target == null)
            {
                target = (test.webservice.tests.ArrayOfString)get_store().add_element_user(ADDRESSES$2);
            }
            target.set(addresses);
        }
    }
    
    /**
     * Appends and returns a new empty "addresses" element
     */
    public test.webservice.tests.ArrayOfString addNewAddresses()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ArrayOfString target = null;
            target = (test.webservice.tests.ArrayOfString)get_store().add_element_user(ADDRESSES$2);
            return target;
        }
    }
    
    /**
     * Gets the "resolvers" element
     */
    public test.webservice.tests.ArrayOfAgentIdentifier getResolvers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ArrayOfAgentIdentifier target = null;
            target = (test.webservice.tests.ArrayOfAgentIdentifier)get_store().find_element_user(RESOLVERS$4, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "resolvers" element
     */
    public void setResolvers(test.webservice.tests.ArrayOfAgentIdentifier resolvers)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ArrayOfAgentIdentifier target = null;
            target = (test.webservice.tests.ArrayOfAgentIdentifier)get_store().find_element_user(RESOLVERS$4, 0);
            if (target == null)
            {
                target = (test.webservice.tests.ArrayOfAgentIdentifier)get_store().add_element_user(RESOLVERS$4);
            }
            target.set(resolvers);
        }
    }
    
    /**
     * Appends and returns a new empty "resolvers" element
     */
    public test.webservice.tests.ArrayOfAgentIdentifier addNewResolvers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ArrayOfAgentIdentifier target = null;
            target = (test.webservice.tests.ArrayOfAgentIdentifier)get_store().add_element_user(RESOLVERS$4);
            return target;
        }
    }
}
