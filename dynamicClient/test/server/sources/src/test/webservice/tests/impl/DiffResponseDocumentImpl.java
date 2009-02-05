/*
 * An XML document type.
 * Localname: diffResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.DiffResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one diffResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class DiffResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.DiffResponseDocument
{
    
    public DiffResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DIFFRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "diffResponse");
    
    
    /**
     * Gets the "diffResponse" element
     */
    public test.webservice.tests.DiffResponseDocument.DiffResponse getDiffResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.DiffResponseDocument.DiffResponse target = null;
            target = (test.webservice.tests.DiffResponseDocument.DiffResponse)get_store().find_element_user(DIFFRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "diffResponse" element
     */
    public void setDiffResponse(test.webservice.tests.DiffResponseDocument.DiffResponse diffResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.DiffResponseDocument.DiffResponse target = null;
            target = (test.webservice.tests.DiffResponseDocument.DiffResponse)get_store().find_element_user(DIFFRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.DiffResponseDocument.DiffResponse)get_store().add_element_user(DIFFRESPONSE$0);
            }
            target.set(diffResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "diffResponse" element
     */
    public test.webservice.tests.DiffResponseDocument.DiffResponse addNewDiffResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.DiffResponseDocument.DiffResponse target = null;
            target = (test.webservice.tests.DiffResponseDocument.DiffResponse)get_store().add_element_user(DIFFRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML diffResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class DiffResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.DiffResponseDocument.DiffResponse
    {
        
        public DiffResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DIFFRETURN$0 = 
            new javax.xml.namespace.QName("", "diffReturn");
        
        
        /**
         * Gets the "diffReturn" element
         */
        public float getDiffReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DIFFRETURN$0, 0);
                if (target == null)
                {
                    return 0.0f;
                }
                return target.getFloatValue();
            }
        }
        
        /**
         * Gets (as xml) the "diffReturn" element
         */
        public org.apache.xmlbeans.XmlFloat xgetDiffReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(DIFFRETURN$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "diffReturn" element
         */
        public void setDiffReturn(float diffReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DIFFRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DIFFRETURN$0);
                }
                target.setFloatValue(diffReturn);
            }
        }
        
        /**
         * Sets (as xml) the "diffReturn" element
         */
        public void xsetDiffReturn(org.apache.xmlbeans.XmlFloat diffReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(DIFFRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(DIFFRETURN$0);
                }
                target.set(diffReturn);
            }
        }
    }
}
