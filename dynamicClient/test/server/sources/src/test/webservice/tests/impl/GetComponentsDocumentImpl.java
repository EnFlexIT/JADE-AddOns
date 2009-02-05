/*
 * An XML document type.
 * Localname: getComponents
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.GetComponentsDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one getComponents(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class GetComponentsDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetComponentsDocument
{
    
    public GetComponentsDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName GETCOMPONENTS$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "getComponents");
    
    
    /**
     * Gets the "getComponents" element
     */
    public test.webservice.tests.GetComponentsDocument.GetComponents getGetComponents()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetComponentsDocument.GetComponents target = null;
            target = (test.webservice.tests.GetComponentsDocument.GetComponents)get_store().find_element_user(GETCOMPONENTS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "getComponents" element
     */
    public void setGetComponents(test.webservice.tests.GetComponentsDocument.GetComponents getComponents)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetComponentsDocument.GetComponents target = null;
            target = (test.webservice.tests.GetComponentsDocument.GetComponents)get_store().find_element_user(GETCOMPONENTS$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.GetComponentsDocument.GetComponents)get_store().add_element_user(GETCOMPONENTS$0);
            }
            target.set(getComponents);
        }
    }
    
    /**
     * Appends and returns a new empty "getComponents" element
     */
    public test.webservice.tests.GetComponentsDocument.GetComponents addNewGetComponents()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.GetComponentsDocument.GetComponents target = null;
            target = (test.webservice.tests.GetComponentsDocument.GetComponents)get_store().add_element_user(GETCOMPONENTS$0);
            return target;
        }
    }
    /**
     * An XML getComponents(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class GetComponentsImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.GetComponentsDocument.GetComponents
    {
        
        public GetComponentsImpl(org.apache.xmlbeans.SchemaType sType)
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
