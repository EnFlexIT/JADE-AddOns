/*
 * An XML document type.
 * Localname: getRandom
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.GetRandomDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one getRandom(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class GetRandomDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetRandomDocument
{
    
    public GetRandomDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETRANDOM$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "getRandom");
    
    
    /**
     * Gets the "getRandom" element
     */
    public test.webservice.tests.GetRandomDocument.GetRandom getGetRandom()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetRandomDocument.GetRandom target = null;
            target = (test.webservice.tests.GetRandomDocument.GetRandom)get_store().find_element_user(GETRANDOM$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "getRandom" element
     */
    public void setGetRandom(test.webservice.tests.GetRandomDocument.GetRandom getRandom)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetRandomDocument.GetRandom target = null;
            target = (test.webservice.tests.GetRandomDocument.GetRandom)get_store().find_element_user(GETRANDOM$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.GetRandomDocument.GetRandom)get_store().add_element_user(GETRANDOM$0);
            }
            target.set(getRandom);
        }
    }
    
    /**
     * Appends and returns a new empty "getRandom" element
     */
    public test.webservice.tests.GetRandomDocument.GetRandom addNewGetRandom()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetRandomDocument.GetRandom target = null;
            target = (test.webservice.tests.GetRandomDocument.GetRandom)get_store().add_element_user(GETRANDOM$0);
            return target;
        }
    }
    /**
     * An XML getRandom(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class GetRandomImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetRandomDocument.GetRandom
    {
        
        public GetRandomImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
