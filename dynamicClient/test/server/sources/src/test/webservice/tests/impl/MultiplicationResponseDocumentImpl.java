/*
 * An XML document type.
 * Localname: multiplicationResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.MultiplicationResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one multiplicationResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class MultiplicationResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.MultiplicationResponseDocument
{
    
    public MultiplicationResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MULTIPLICATIONRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "multiplicationResponse");
    
    
    /**
     * Gets the "multiplicationResponse" element
     */
    public test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse getMultiplicationResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse target = null;
            target = (test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse)get_store().find_element_user(MULTIPLICATIONRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "multiplicationResponse" element
     */
    public void setMultiplicationResponse(test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse multiplicationResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse target = null;
            target = (test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse)get_store().find_element_user(MULTIPLICATIONRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse)get_store().add_element_user(MULTIPLICATIONRESPONSE$0);
            }
            target.set(multiplicationResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "multiplicationResponse" element
     */
    public test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse addNewMultiplicationResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse target = null;
            target = (test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse)get_store().add_element_user(MULTIPLICATIONRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML multiplicationResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class MultiplicationResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.MultiplicationResponseDocument.MultiplicationResponse
    {
        
        public MultiplicationResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName MULTIPLICATIONRETURN$0 = 
            new javax.xml.namespace.QName("", "multiplicationReturn");
        
        
        /**
         * Gets the "multiplicationReturn" element
         */
        public float getMultiplicationReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MULTIPLICATIONRETURN$0, 0);
                if (target == null)
                {
                    return 0.0f;
                }
                return target.getFloatValue();
            }
        }
        
        /**
         * Gets (as xml) the "multiplicationReturn" element
         */
        public org.apache.xmlbeans.XmlFloat xgetMultiplicationReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(MULTIPLICATIONRETURN$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "multiplicationReturn" element
         */
        public void setMultiplicationReturn(float multiplicationReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(MULTIPLICATIONRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(MULTIPLICATIONRETURN$0);
                }
                target.setFloatValue(multiplicationReturn);
            }
        }
        
        /**
         * Sets (as xml) the "multiplicationReturn" element
         */
        public void xsetMultiplicationReturn(org.apache.xmlbeans.XmlFloat multiplicationReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(MULTIPLICATIONRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(MULTIPLICATIONRETURN$0);
                }
                target.set(multiplicationReturn);
            }
        }
    }
}
