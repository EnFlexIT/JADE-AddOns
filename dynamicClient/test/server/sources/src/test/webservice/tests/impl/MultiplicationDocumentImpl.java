/*
 * An XML document type.
 * Localname: multiplication
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.MultiplicationDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one multiplication(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class MultiplicationDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.MultiplicationDocument
{
    
    public MultiplicationDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MULTIPLICATION$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "multiplication");
    
    
    /**
     * Gets the "multiplication" element
     */
    public test.webservice.tests.MultiplicationDocument.Multiplication getMultiplication()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.MultiplicationDocument.Multiplication target = null;
            target = (test.webservice.tests.MultiplicationDocument.Multiplication)get_store().find_element_user(MULTIPLICATION$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "multiplication" element
     */
    public void setMultiplication(test.webservice.tests.MultiplicationDocument.Multiplication multiplication)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.MultiplicationDocument.Multiplication target = null;
            target = (test.webservice.tests.MultiplicationDocument.Multiplication)get_store().find_element_user(MULTIPLICATION$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.MultiplicationDocument.Multiplication)get_store().add_element_user(MULTIPLICATION$0);
            }
            target.set(multiplication);
        }
    }
    
    /**
     * Appends and returns a new empty "multiplication" element
     */
    public test.webservice.tests.MultiplicationDocument.Multiplication addNewMultiplication()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.MultiplicationDocument.Multiplication target = null;
            target = (test.webservice.tests.MultiplicationDocument.Multiplication)get_store().add_element_user(MULTIPLICATION$0);
            return target;
        }
    }
    /**
     * An XML multiplication(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class MultiplicationImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.MultiplicationDocument.Multiplication
    {
        
        public MultiplicationImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName NUMBERS$0 = 
            new javax.xml.namespace.QName("", "numbers");
        
        
        /**
         * Gets the "numbers" element
         */
        public test.webservice.tests.ArrayOfFloat getNumbers()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.ArrayOfFloat target = null;
                target = (test.webservice.tests.ArrayOfFloat)get_store().find_element_user(NUMBERS$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "numbers" element
         */
        public void setNumbers(test.webservice.tests.ArrayOfFloat numbers)
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.ArrayOfFloat target = null;
                target = (test.webservice.tests.ArrayOfFloat)get_store().find_element_user(NUMBERS$0, 0);
                if (target == null)
                {
                    target = (test.webservice.tests.ArrayOfFloat)get_store().add_element_user(NUMBERS$0);
                }
                target.set(numbers);
            }
        }
        
        /**
         * Appends and returns a new empty "numbers" element
         */
        public test.webservice.tests.ArrayOfFloat addNewNumbers()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.ArrayOfFloat target = null;
                target = (test.webservice.tests.ArrayOfFloat)get_store().add_element_user(NUMBERS$0);
                return target;
            }
        }
    }
}
