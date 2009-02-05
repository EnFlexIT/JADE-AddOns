/*
 * An XML document type.
 * Localname: printTimeResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.PrintTimeResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one printTimeResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class PrintTimeResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.PrintTimeResponseDocument
{
    
    public PrintTimeResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PRINTTIMERESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "printTimeResponse");
    
    
    /**
     * Gets the "printTimeResponse" element
     */
    public test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse getPrintTimeResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse target = null;
            target = (test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse)get_store().find_element_user(PRINTTIMERESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "printTimeResponse" element
     */
    public void setPrintTimeResponse(test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse printTimeResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse target = null;
            target = (test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse)get_store().find_element_user(PRINTTIMERESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse)get_store().add_element_user(PRINTTIMERESPONSE$0);
            }
            target.set(printTimeResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "printTimeResponse" element
     */
    public test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse addNewPrintTimeResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse target = null;
            target = (test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse)get_store().add_element_user(PRINTTIMERESPONSE$0);
            return target;
        }
    }
    /**
     * An XML printTimeResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class PrintTimeResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.PrintTimeResponseDocument.PrintTimeResponse
    {
        
        public PrintTimeResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
