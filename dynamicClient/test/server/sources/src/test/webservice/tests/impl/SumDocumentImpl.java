/*
 * An XML document type.
 * Localname: sum
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.SumDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one sum(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class SumDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumDocument
{
    
    public SumDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SUM$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "sum");
    
    
    /**
     * Gets the "sum" element
     */
    public test.webservice.tests.SumDocument.Sum getSum()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumDocument.Sum target = null;
            target = (test.webservice.tests.SumDocument.Sum)get_store().find_element_user(SUM$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "sum" element
     */
    public void setSum(test.webservice.tests.SumDocument.Sum sum)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumDocument.Sum target = null;
            target = (test.webservice.tests.SumDocument.Sum)get_store().find_element_user(SUM$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.SumDocument.Sum)get_store().add_element_user(SUM$0);
            }
            target.set(sum);
        }
    }
    
    /**
     * Appends and returns a new empty "sum" element
     */
    public test.webservice.tests.SumDocument.Sum addNewSum()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumDocument.Sum target = null;
            target = (test.webservice.tests.SumDocument.Sum)get_store().add_element_user(SUM$0);
            return target;
        }
    }
    /**
     * An XML sum(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class SumImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumDocument.Sum
    {
        
        public SumImpl(org.apache.xmlbeans.SchemaType sType)
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
         * True if has "secondElement" element
         */
        public boolean isSetSecondElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                return get_store().count_elements(SECONDELEMENT$2) != 0;
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
        
        /**
         * Unsets the "secondElement" element
         */
        public void unsetSecondElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                get_store().remove_element(SECONDELEMENT$2, 0);
            }
        }
    }
}
