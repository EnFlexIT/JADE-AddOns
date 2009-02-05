/*
 * An XML document type.
 * Localname: diff
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.DiffDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one diff(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class DiffDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.DiffDocument
{
    
    public DiffDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DIFF$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "diff");
    
    
    /**
     * Gets the "diff" element
     */
    public test.webservice.tests.DiffDocument.Diff getDiff()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.DiffDocument.Diff target = null;
            target = (test.webservice.tests.DiffDocument.Diff)get_store().find_element_user(DIFF$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "diff" element
     */
    public void setDiff(test.webservice.tests.DiffDocument.Diff diff)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.DiffDocument.Diff target = null;
            target = (test.webservice.tests.DiffDocument.Diff)get_store().find_element_user(DIFF$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.DiffDocument.Diff)get_store().add_element_user(DIFF$0);
            }
            target.set(diff);
        }
    }
    
    /**
     * Appends and returns a new empty "diff" element
     */
    public test.webservice.tests.DiffDocument.Diff addNewDiff()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.DiffDocument.Diff target = null;
            target = (test.webservice.tests.DiffDocument.Diff)get_store().add_element_user(DIFF$0);
            return target;
        }
    }
    /**
     * An XML diff(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class DiffImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.DiffDocument.Diff
    {
        
        public DiffImpl(org.apache.xmlbeans.SchemaType sType)
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
