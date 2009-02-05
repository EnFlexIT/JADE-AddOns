/*
 * An XML document type.
 * Localname: sumComplexResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.SumComplexResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one sumComplexResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class SumComplexResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumComplexResponseDocument
{
    
    public SumComplexResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SUMCOMPLEXRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "sumComplexResponse");
    
    
    /**
     * Gets the "sumComplexResponse" element
     */
    public test.webservice.tests.SumComplexResponseDocument.SumComplexResponse getSumComplexResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumComplexResponseDocument.SumComplexResponse target = null;
            target = (test.webservice.tests.SumComplexResponseDocument.SumComplexResponse)get_store().find_element_user(SUMCOMPLEXRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "sumComplexResponse" element
     */
    public void setSumComplexResponse(test.webservice.tests.SumComplexResponseDocument.SumComplexResponse sumComplexResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumComplexResponseDocument.SumComplexResponse target = null;
            target = (test.webservice.tests.SumComplexResponseDocument.SumComplexResponse)get_store().find_element_user(SUMCOMPLEXRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.SumComplexResponseDocument.SumComplexResponse)get_store().add_element_user(SUMCOMPLEXRESPONSE$0);
            }
            target.set(sumComplexResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "sumComplexResponse" element
     */
    public test.webservice.tests.SumComplexResponseDocument.SumComplexResponse addNewSumComplexResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumComplexResponseDocument.SumComplexResponse target = null;
            target = (test.webservice.tests.SumComplexResponseDocument.SumComplexResponse)get_store().add_element_user(SUMCOMPLEXRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML sumComplexResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class SumComplexResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumComplexResponseDocument.SumComplexResponse
    {
        
        public SumComplexResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName SUMCOMPLEXRETURN$0 = 
            new javax.xml.namespace.QName("", "sumComplexReturn");
        
        
        /**
         * Gets the "sumComplexReturn" element
         */
        public test.webservice.tests.Complex getSumComplexReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(SUMCOMPLEXRETURN$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "sumComplexReturn" element
         */
        public void setSumComplexReturn(test.webservice.tests.Complex sumComplexReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(SUMCOMPLEXRETURN$0, 0);
                if (target == null)
                {
                    target = (test.webservice.tests.Complex)get_store().add_element_user(SUMCOMPLEXRETURN$0);
                }
                target.set(sumComplexReturn);
            }
        }
        
        /**
         * Appends and returns a new empty "sumComplexReturn" element
         */
        public test.webservice.tests.Complex addNewSumComplexReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().add_element_user(SUMCOMPLEXRETURN$0);
                return target;
            }
        }
    }
}
