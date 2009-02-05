/*
 * An XML document type.
 * Localname: compareNumbersResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.CompareNumbersResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one compareNumbersResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class CompareNumbersResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.CompareNumbersResponseDocument
{
    
    public CompareNumbersResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMPARENUMBERSRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "compareNumbersResponse");
    
    
    /**
     * Gets the "compareNumbersResponse" element
     */
    public test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse getCompareNumbersResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse target = null;
            target = (test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse)get_store().find_element_user(COMPARENUMBERSRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "compareNumbersResponse" element
     */
    public void setCompareNumbersResponse(test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse compareNumbersResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse target = null;
            target = (test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse)get_store().find_element_user(COMPARENUMBERSRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse)get_store().add_element_user(COMPARENUMBERSRESPONSE$0);
            }
            target.set(compareNumbersResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "compareNumbersResponse" element
     */
    public test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse addNewCompareNumbersResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse target = null;
            target = (test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse)get_store().add_element_user(COMPARENUMBERSRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML compareNumbersResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class CompareNumbersResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.CompareNumbersResponseDocument.CompareNumbersResponse
    {
        
        public CompareNumbersResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName COMPARENUMBERSRETURN$0 = 
            new javax.xml.namespace.QName("", "compareNumbersReturn");
        
        
        /**
         * Gets the "compareNumbersReturn" element
         */
        public boolean getCompareNumbersReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMPARENUMBERSRETURN$0, 0);
                if (target == null)
                {
                    return false;
                }
                return target.getBooleanValue();
            }
        }
        
        /**
         * Gets (as xml) the "compareNumbersReturn" element
         */
        public org.apache.xmlbeans.XmlBoolean xgetCompareNumbersReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(COMPARENUMBERSRETURN$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "compareNumbersReturn" element
         */
        public void setCompareNumbersReturn(boolean compareNumbersReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(COMPARENUMBERSRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(COMPARENUMBERSRETURN$0);
                }
                target.setBooleanValue(compareNumbersReturn);
            }
        }
        
        /**
         * Sets (as xml) the "compareNumbersReturn" element
         */
        public void xsetCompareNumbersReturn(org.apache.xmlbeans.XmlBoolean compareNumbersReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlBoolean target = null;
                target = (org.apache.xmlbeans.XmlBoolean)get_store().find_element_user(COMPARENUMBERSRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlBoolean)get_store().add_element_user(COMPARENUMBERSRETURN$0);
                }
                target.set(compareNumbersReturn);
            }
        }
    }
}
