/*
 * An XML document type.
 * Localname: getRandomResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.GetRandomResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one getRandomResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class GetRandomResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetRandomResponseDocument
{
    
    public GetRandomResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETRANDOMRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "getRandomResponse");
    
    
    /**
     * Gets the "getRandomResponse" element
     */
    public test.webservice.tests.GetRandomResponseDocument.GetRandomResponse getGetRandomResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetRandomResponseDocument.GetRandomResponse target = null;
            target = (test.webservice.tests.GetRandomResponseDocument.GetRandomResponse)get_store().find_element_user(GETRANDOMRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "getRandomResponse" element
     */
    public void setGetRandomResponse(test.webservice.tests.GetRandomResponseDocument.GetRandomResponse getRandomResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetRandomResponseDocument.GetRandomResponse target = null;
            target = (test.webservice.tests.GetRandomResponseDocument.GetRandomResponse)get_store().find_element_user(GETRANDOMRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.GetRandomResponseDocument.GetRandomResponse)get_store().add_element_user(GETRANDOMRESPONSE$0);
            }
            target.set(getRandomResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "getRandomResponse" element
     */
    public test.webservice.tests.GetRandomResponseDocument.GetRandomResponse addNewGetRandomResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetRandomResponseDocument.GetRandomResponse target = null;
            target = (test.webservice.tests.GetRandomResponseDocument.GetRandomResponse)get_store().add_element_user(GETRANDOMRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML getRandomResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class GetRandomResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetRandomResponseDocument.GetRandomResponse
    {
        
        public GetRandomResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETRANDOMRETURN$0 = 
            new javax.xml.namespace.QName("", "getRandomReturn");
        
        
        /**
         * Gets the "getRandomReturn" element
         */
        public test.webservice.tests.Complex getGetRandomReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(GETRANDOMRETURN$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "getRandomReturn" element
         */
        public void setGetRandomReturn(test.webservice.tests.Complex getRandomReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(GETRANDOMRETURN$0, 0);
                if (target == null)
                {
                    target = (test.webservice.tests.Complex)get_store().add_element_user(GETRANDOMRETURN$0);
                }
                target.set(getRandomReturn);
            }
        }
        
        /**
         * Appends and returns a new empty "getRandomReturn" element
         */
        public test.webservice.tests.Complex addNewGetRandomReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().add_element_user(GETRANDOMRETURN$0);
                return target;
            }
        }
    }
}
