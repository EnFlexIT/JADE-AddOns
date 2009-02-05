/*
 * An XML document type.
 * Localname: abs
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.AbsDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one abs(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class AbsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.AbsDocument
{
    
    public AbsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ABS$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "abs");
    
    
    /**
     * Gets the "abs" element
     */
    public test.webservice.tests.AbsDocument.Abs getAbs()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsDocument.Abs target = null;
            target = (test.webservice.tests.AbsDocument.Abs)get_store().find_element_user(ABS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "abs" element
     */
    public void setAbs(test.webservice.tests.AbsDocument.Abs abs)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsDocument.Abs target = null;
            target = (test.webservice.tests.AbsDocument.Abs)get_store().find_element_user(ABS$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.AbsDocument.Abs)get_store().add_element_user(ABS$0);
            }
            target.set(abs);
        }
    }
    
    /**
     * Appends and returns a new empty "abs" element
     */
    public test.webservice.tests.AbsDocument.Abs addNewAbs()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsDocument.Abs target = null;
            target = (test.webservice.tests.AbsDocument.Abs)get_store().add_element_user(ABS$0);
            return target;
        }
    }
    /**
     * An XML abs(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class AbsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.AbsDocument.Abs
    {
        
        public AbsImpl(org.apache.xmlbeans.SchemaType sType)
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
