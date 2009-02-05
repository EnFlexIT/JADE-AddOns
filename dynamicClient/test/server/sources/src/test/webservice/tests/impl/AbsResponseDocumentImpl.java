/*
 * An XML document type.
 * Localname: absResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.AbsResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one absResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class AbsResponseDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.AbsResponseDocument
{
    
    public AbsResponseDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ABSRESPONSE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "absResponse");
    
    
    /**
     * Gets the "absResponse" element
     */
    public test.webservice.tests.AbsResponseDocument.AbsResponse getAbsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsResponseDocument.AbsResponse target = null;
            target = (test.webservice.tests.AbsResponseDocument.AbsResponse)get_store().find_element_user(ABSRESPONSE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "absResponse" element
     */
    public void setAbsResponse(test.webservice.tests.AbsResponseDocument.AbsResponse absResponse)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsResponseDocument.AbsResponse target = null;
            target = (test.webservice.tests.AbsResponseDocument.AbsResponse)get_store().find_element_user(ABSRESPONSE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.AbsResponseDocument.AbsResponse)get_store().add_element_user(ABSRESPONSE$0);
            }
            target.set(absResponse);
        }
    }
    
    /**
     * Appends and returns a new empty "absResponse" element
     */
    public test.webservice.tests.AbsResponseDocument.AbsResponse addNewAbsResponse()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsResponseDocument.AbsResponse target = null;
            target = (test.webservice.tests.AbsResponseDocument.AbsResponse)get_store().add_element_user(ABSRESPONSE$0);
            return target;
        }
    }
    /**
     * An XML absResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class AbsResponseImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.AbsResponseDocument.AbsResponse
    {
        
        public AbsResponseImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName ABSRETURN$0 = 
            new javax.xml.namespace.QName("", "absReturn");
        
        
        /**
         * Gets the "absReturn" element
         */
        public float getAbsReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ABSRETURN$0, 0);
                if (target == null)
                {
                    return 0.0f;
                }
                return target.getFloatValue();
            }
        }
        
        /**
         * Gets (as xml) the "absReturn" element
         */
        public org.apache.xmlbeans.XmlFloat xgetAbsReturn()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(ABSRETURN$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "absReturn" element
         */
        public void setAbsReturn(float absReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(ABSRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(ABSRETURN$0);
                }
                target.setFloatValue(absReturn);
            }
        }
        
        /**
         * Sets (as xml) the "absReturn" element
         */
        public void xsetAbsReturn(org.apache.xmlbeans.XmlFloat absReturn)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlFloat target = null;
                target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(ABSRETURN$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(ABSRETURN$0);
                }
                target.set(absReturn);
            }
        }
    }
}
