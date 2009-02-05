/*
 * XML Type:  ArrayOfAgent-identifier
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.ArrayOfAgentIdentifier
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * An XML ArrayOfAgent-identifier(@urn:tests.webservice.test).
 *
 * This is a complex type.
 */
public class ArrayOfAgentIdentifierImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.ArrayOfAgentIdentifier
{
    
    public ArrayOfAgentIdentifierImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName AGENTIDENTIFIER$0 = 
        new javax.xml.namespace.QName("", "agent-identifier");
    
    
    /**
     * Gets array of all "agent-identifier" elements
     */
    public test.webservice.tests.AgentIdentifier[] getAgentIdentifierArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(AGENTIDENTIFIER$0, targetList);
            test.webservice.tests.AgentIdentifier[] result = new test.webservice.tests.AgentIdentifier[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "agent-identifier" element
     */
    public test.webservice.tests.AgentIdentifier getAgentIdentifierArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AgentIdentifier target = null;
            target = (test.webservice.tests.AgentIdentifier)get_store().find_element_user(AGENTIDENTIFIER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "agent-identifier" element
     */
    public int sizeOfAgentIdentifierArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(AGENTIDENTIFIER$0);
        }
    }
    
    /**
     * Sets array of all "agent-identifier" element
     */
    public void setAgentIdentifierArray(test.webservice.tests.AgentIdentifier[] agentIdentifierArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(agentIdentifierArray, AGENTIDENTIFIER$0);
        }
    }
    
    /**
     * Sets ith "agent-identifier" element
     */
    public void setAgentIdentifierArray(int i, test.webservice.tests.AgentIdentifier agentIdentifier)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AgentIdentifier target = null;
            target = (test.webservice.tests.AgentIdentifier)get_store().find_element_user(AGENTIDENTIFIER$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(agentIdentifier);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "agent-identifier" element
     */
    public test.webservice.tests.AgentIdentifier insertNewAgentIdentifier(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AgentIdentifier target = null;
            target = (test.webservice.tests.AgentIdentifier)get_store().insert_element_user(AGENTIDENTIFIER$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "agent-identifier" element
     */
    public test.webservice.tests.AgentIdentifier addNewAgentIdentifier()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AgentIdentifier target = null;
            target = (test.webservice.tests.AgentIdentifier)get_store().add_element_user(AGENTIDENTIFIER$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "agent-identifier" element
     */
    public void removeAgentIdentifier(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(AGENTIDENTIFIER$0, i);
        }
    }
}
