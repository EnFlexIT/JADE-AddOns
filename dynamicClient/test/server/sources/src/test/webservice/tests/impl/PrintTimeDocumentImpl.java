/*
 * An XML document type.
 * Localname: printTime
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.PrintTimeDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one printTime(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class PrintTimeDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.PrintTimeDocument
{
    
    public PrintTimeDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PRINTTIME$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "printTime");
    
    
    /**
     * Gets the "printTime" element
     */
    public test.webservice.tests.PrintTimeDocument.PrintTime getPrintTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintTimeDocument.PrintTime target = null;
            target = (test.webservice.tests.PrintTimeDocument.PrintTime)get_store().find_element_user(PRINTTIME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "printTime" element
     */
    public void setPrintTime(test.webservice.tests.PrintTimeDocument.PrintTime printTime)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintTimeDocument.PrintTime target = null;
            target = (test.webservice.tests.PrintTimeDocument.PrintTime)get_store().find_element_user(PRINTTIME$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.PrintTimeDocument.PrintTime)get_store().add_element_user(PRINTTIME$0);
            }
            target.set(printTime);
        }
    }
    
    /**
     * Appends and returns a new empty "printTime" element
     */
    public test.webservice.tests.PrintTimeDocument.PrintTime addNewPrintTime()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintTimeDocument.PrintTime target = null;
            target = (test.webservice.tests.PrintTimeDocument.PrintTime)get_store().add_element_user(PRINTTIME$0);
            return target;
        }
    }
    /**
     * An XML printTime(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class PrintTimeImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.PrintTimeDocument.PrintTime
    {
        
        public PrintTimeImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        
    }
}
