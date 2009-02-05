/*
 * An XML document type.
 * Localname: compareNumbers
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.CompareNumbersDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one compareNumbers(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class CompareNumbersDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.CompareNumbersDocument
{
    
    public CompareNumbersDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMPARENUMBERS$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "compareNumbers");
    
    
    /**
     * Gets the "compareNumbers" element
     */
    public test.webservice.tests.CompareNumbersDocument.CompareNumbers getCompareNumbers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.CompareNumbersDocument.CompareNumbers target = null;
            target = (test.webservice.tests.CompareNumbersDocument.CompareNumbers)get_store().find_element_user(COMPARENUMBERS$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "compareNumbers" element
     */
    public void setCompareNumbers(test.webservice.tests.CompareNumbersDocument.CompareNumbers compareNumbers)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.CompareNumbersDocument.CompareNumbers target = null;
            target = (test.webservice.tests.CompareNumbersDocument.CompareNumbers)get_store().find_element_user(COMPARENUMBERS$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.CompareNumbersDocument.CompareNumbers)get_store().add_element_user(COMPARENUMBERS$0);
            }
            target.set(compareNumbers);
        }
    }
    
    /**
     * Appends and returns a new empty "compareNumbers" element
     */
    public test.webservice.tests.CompareNumbersDocument.CompareNumbers addNewCompareNumbers()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.CompareNumbersDocument.CompareNumbers target = null;
            target = (test.webservice.tests.CompareNumbersDocument.CompareNumbers)get_store().add_element_user(COMPARENUMBERS$0);
            return target;
        }
    }
    /**
     * An XML compareNumbers(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class CompareNumbersImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.CompareNumbersDocument.CompareNumbers
    {
        
        public CompareNumbersImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName FIRSTELEMENT$0 = 
            new javax.xml.namespace.QName("", "firstElement");
        private static final javax.xml.namespace.QName SECONDELEMENT$2 = 
            new javax.xml.namespace.QName("", "secondElement");
        
        
        /**
         * Gets the "firstElement" element
         */
        public float getFirstElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTELEMENT$0, 0);
                if (target == null)
                {
                    return 0.0f;
                }
                return target.getFloatValue();
            }
        }
        
        /**
         * Gets (as xml) the "firstElement" element
         */
        public org.apache.xmlbeans.XmlFloat xgetFirstElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(FIRSTELEMENT$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "firstElement" element
         */
        public void setFirstElement(float firstElement)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FIRSTELEMENT$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FIRSTELEMENT$0);
                }
                target.setFloatValue(firstElement);
            }
        }
        
        /**
         * Sets (as xml) the "firstElement" element
         */
        public void xsetFirstElement(org.apache.xmlbeans.XmlFloat firstElement)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(FIRSTELEMENT$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(FIRSTELEMENT$0);
                }
                target.set(firstElement);
            }
        }
        
        /**
         * Gets the "secondElement" element
         */
        public float getSecondElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SECONDELEMENT$2, 0);
                if (target == null)
                {
                    return 0.0f;
                }
                return target.getFloatValue();
            }
        }
        
        /**
         * Gets (as xml) the "secondElement" element
         */
        public org.apache.xmlbeans.XmlFloat xgetSecondElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(SECONDELEMENT$2, 0);
                return target;
            }
        }
        
        /**
         * Sets the "secondElement" element
         */
        public void setSecondElement(float secondElement)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SECONDELEMENT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SECONDELEMENT$2);
                }
                target.setFloatValue(secondElement);
            }
        }
        
        /**
         * Sets (as xml) the "secondElement" element
         */
        public void xsetSecondElement(org.apache.xmlbeans.XmlFloat secondElement)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(SECONDELEMENT$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(SECONDELEMENT$2);
                }
                target.set(secondElement);
            }
        }
    }
}
