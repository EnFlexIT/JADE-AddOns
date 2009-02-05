/*
 * An XML document type.
 * Localname: getComponentsResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.GetComponentsResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one getComponentsResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class GetComponentsResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetComponentsResponseDocument
{
    
    public GetComponentsResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETCOMPONENTSRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "getComponentsResponse");
    
    
    /**
     * Gets the "getComponentsResponse" element
     */
    public test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse getGetComponentsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse target = null;
            target = (test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse)get_store().find_element_user(GETCOMPONENTSRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "getComponentsResponse" element
     */
    public void setGetComponentsResponse(test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse getComponentsResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse target = null;
            target = (test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse)get_store().find_element_user(GETCOMPONENTSRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse)get_store().add_element_user(GETCOMPONENTSRESPONSE$0);
            }
            target.set(getComponentsResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "getComponentsResponse" element
     */
    public test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse addNewGetComponentsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse target = null;
            target = (test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse)get_store().add_element_user(GETCOMPONENTSRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML getComponentsResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class GetComponentsResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetComponentsResponseDocument.GetComponentsResponse
    {
        
        public GetComponentsResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName GETCOMPONENTSRETURN$0 = 
            new javax.xml.namespace.QName("", "getComponentsReturn");
        
        
        /**
         * Gets the "getComponentsReturn" element
         */
        public test.webservice.tests.ArrayOfComplex getGetComponentsReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.ArrayOfComplex target = null;
                target = (test.webservice.tests.ArrayOfComplex)get_store().find_element_user(GETCOMPONENTSRETURN$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "getComponentsReturn" element
         */
        public void setGetComponentsReturn(test.webservice.tests.ArrayOfComplex getComponentsReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.ArrayOfComplex target = null;
                target = (test.webservice.tests.ArrayOfComplex)get_store().find_element_user(GETCOMPONENTSRETURN$0, 0);
                if (target == null)
                {
                    target = (test.webservice.tests.ArrayOfComplex)get_store().add_element_user(GETCOMPONENTSRETURN$0);
                }
                target.set(getComponentsReturn);
            }
        }
        
        /**
         * Appends and returns a new empty "getComponentsReturn" element
         */
        public test.webservice.tests.ArrayOfComplex addNewGetComponentsReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.ArrayOfComplex target = null;
                target = (test.webservice.tests.ArrayOfComplex)get_store().add_element_user(GETCOMPONENTSRETURN$0);
                return target;
            }
        }
    }
}
