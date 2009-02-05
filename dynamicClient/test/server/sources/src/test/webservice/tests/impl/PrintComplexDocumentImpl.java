/*
 * An XML document type.
 * Localname: printComplex
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.PrintComplexDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one printComplex(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class PrintComplexDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.PrintComplexDocument
{
    
    public PrintComplexDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName PRINTCOMPLEX$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "printComplex");
    
    
    /**
     * Gets the "printComplex" element
     */
    public test.webservice.tests.PrintComplexDocument.PrintComplex getPrintComplex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintComplexDocument.PrintComplex target = null;
            target = (test.webservice.tests.PrintComplexDocument.PrintComplex)get_store().find_element_user(PRINTCOMPLEX$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "printComplex" element
     */
    public void setPrintComplex(test.webservice.tests.PrintComplexDocument.PrintComplex printComplex)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintComplexDocument.PrintComplex target = null;
            target = (test.webservice.tests.PrintComplexDocument.PrintComplex)get_store().find_element_user(PRINTCOMPLEX$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.PrintComplexDocument.PrintComplex)get_store().add_element_user(PRINTCOMPLEX$0);
            }
            target.set(printComplex);
        }
    }
    
    /**
     * Appends and returns a new empty "printComplex" element
     */
    public test.webservice.tests.PrintComplexDocument.PrintComplex addNewPrintComplex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.PrintComplexDocument.PrintComplex target = null;
            target = (test.webservice.tests.PrintComplexDocument.PrintComplex)get_store().add_element_user(PRINTCOMPLEX$0);
            return target;
        }
    }
    /**
     * An XML printComplex(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class PrintComplexImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.PrintComplexDocument.PrintComplex
    {
        
        public PrintComplexImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName COMPLEX$0 = 
            new javax.xml.namespace.QName("", "complex");
        
        
        /**
         * Gets the "complex" element
         */
        public test.webservice.tests.Complex getComplex()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(COMPLEX$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "complex" element
         */
        public void setComplex(test.webservice.tests.Complex complex)
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(COMPLEX$0, 0);
                if (target == null)
                {
                    target = (test.webservice.tests.Complex)get_store().add_element_user(COMPLEX$0);
                }
                target.set(complex);
            }
        }
        
        /**
         * Appends and returns a new empty "complex" element
         */
        public test.webservice.tests.Complex addNewComplex()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().add_element_user(COMPLEX$0);
                return target;
            }
        }
    }
}
