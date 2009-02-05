/*
 * An XML document type.
 * Localname: convertDateResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.ConvertDateResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one convertDateResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class ConvertDateResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.ConvertDateResponseDocument
{
    
    public ConvertDateResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CONVERTDATERESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "convertDateResponse");
    
    
    /**
     * Gets the "convertDateResponse" element
     */
    public test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse getConvertDateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse target = null;
            target = (test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse)get_store().find_element_user(CONVERTDATERESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "convertDateResponse" element
     */
    public void setConvertDateResponse(test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse convertDateResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse target = null;
            target = (test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse)get_store().find_element_user(CONVERTDATERESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse)get_store().add_element_user(CONVERTDATERESPONSE$0);
            }
            target.set(convertDateResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "convertDateResponse" element
     */
    public test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse addNewConvertDateResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse target = null;
            target = (test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse)get_store().add_element_user(CONVERTDATERESPONSE$0);
            return target;
        }
    }
    /**
     * An XML convertDateResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class ConvertDateResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.ConvertDateResponseDocument.ConvertDateResponse
    {
        
        public ConvertDateResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName CONVERTDATERETURN$0 = 
            new javax.xml.namespace.QName("", "convertDateReturn");
        
        
        /**
         * Gets the "convertDateReturn" element
         */
        public java.lang.String getConvertDateReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONVERTDATERETURN$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "convertDateReturn" element
         */
        public org.apache.xmlbeans.XmlString xgetConvertDateReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CONVERTDATERETURN$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "convertDateReturn" element
         */
        public void setConvertDateReturn(java.lang.String convertDateReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(CONVERTDATERETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(CONVERTDATERETURN$0);
                }
                target.setStringValue(convertDateReturn);
            }
        }
        
        /**
         * Sets (as xml) the "convertDateReturn" element
         */
        public void xsetConvertDateReturn(org.apache.xmlbeans.XmlString convertDateReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(CONVERTDATERETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(CONVERTDATERETURN$0);
                }
                target.set(convertDateReturn);
            }
        }
    }
}
