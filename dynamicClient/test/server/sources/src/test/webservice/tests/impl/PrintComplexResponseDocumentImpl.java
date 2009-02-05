/*
 * An XML document type.
 * Localname: printComplexResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.PrintComplexResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one printComplexResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class PrintComplexResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.PrintComplexResponseDocument
{
    
    public PrintComplexResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PRINTCOMPLEXRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "printComplexResponse");
    
    
    /**
     * Gets the "printComplexResponse" element
     */
    public test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse getPrintComplexResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse target = null;
            target = (test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse)get_store().find_element_user(PRINTCOMPLEXRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "printComplexResponse" element
     */
    public void setPrintComplexResponse(test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse printComplexResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse target = null;
            target = (test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse)get_store().find_element_user(PRINTCOMPLEXRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse)get_store().add_element_user(PRINTCOMPLEXRESPONSE$0);
            }
            target.set(printComplexResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "printComplexResponse" element
     */
    public test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse addNewPrintComplexResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse target = null;
            target = (test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse)get_store().add_element_user(PRINTCOMPLEXRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML printComplexResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class PrintComplexResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse
    {
        
        public PrintComplexResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
