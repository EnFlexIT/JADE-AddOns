/*
 * An XML document type.
 * Localname: sumResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.SumResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one sumResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class SumResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumResponseDocument
{
    
    public SumResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SUMRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "sumResponse");
    
    
    /**
     * Gets the "sumResponse" element
     */
    public test.webservice.tests.SumResponseDocument.SumResponse getSumResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumResponseDocument.SumResponse target = null;
            target = (test.webservice.tests.SumResponseDocument.SumResponse)get_store().find_element_user(SUMRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "sumResponse" element
     */
    public void setSumResponse(test.webservice.tests.SumResponseDocument.SumResponse sumResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumResponseDocument.SumResponse target = null;
            target = (test.webservice.tests.SumResponseDocument.SumResponse)get_store().find_element_user(SUMRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.SumResponseDocument.SumResponse)get_store().add_element_user(SUMRESPONSE$0);
            }
            target.set(sumResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "sumResponse" element
     */
    public test.webservice.tests.SumResponseDocument.SumResponse addNewSumResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumResponseDocument.SumResponse target = null;
            target = (test.webservice.tests.SumResponseDocument.SumResponse)get_store().add_element_user(SUMRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML sumResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class SumResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumResponseDocument.SumResponse
    {
        
        public SumResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SUMRETURN$0 = 
            new javax.xml.namespace.QName("", "sumReturn");
        
        
        /**
         * Gets the "sumReturn" element
         */
        public float getSumReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUMRETURN$0, 0);
                if (target == null)
                {
                    return 0.0f;
                }
                return target.getFloatValue();
            }
        }
        
        /**
         * Gets (as xml) the "sumReturn" element
         */
        public org.apache.xmlbeans.XmlFloat xgetSumReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(SUMRETURN$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "sumReturn" element
         */
        public void setSumReturn(float sumReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUMRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUMRETURN$0);
                }
                target.setFloatValue(sumReturn);
            }
        }
        
        /**
         * Sets (as xml) the "sumReturn" element
         */
        public void xsetSumReturn(org.apache.xmlbeans.XmlFloat sumReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(SUMRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(SUMRETURN$0);
                }
                target.set(sumReturn);
            }
        }
    }
}
