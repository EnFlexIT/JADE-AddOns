/*
 * An XML document type.
 * Localname: absHeaderAggregate
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.AbsHeaderAggregateDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one absHeaderAggregate(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class AbsHeaderAggregateDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.AbsHeaderAggregateDocument
{
    
    public AbsHeaderAggregateDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName ABSHEADERAGGREGATE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "absHeaderAggregate");
    
    
    /**
     * Gets the "absHeaderAggregate" element
     */
    public test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate getAbsHeaderAggregate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate target = null;
            target = (test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate)get_store().find_element_user(ABSHEADERAGGREGATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "absHeaderAggregate" element
     */
    public void setAbsHeaderAggregate(test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate absHeaderAggregate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate target = null;
            target = (test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate)get_store().find_element_user(ABSHEADERAGGREGATE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate)get_store().add_element_user(ABSHEADERAGGREGATE$0);
            }
            target.set(absHeaderAggregate);
        }
    }
    
    /**
     * Appends and returns a new empty "absHeaderAggregate" element
     */
    public test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate addNewAbsHeaderAggregate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate target = null;
            target = (test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate)get_store().add_element_user(ABSHEADERAGGREGATE$0);
            return target;
        }
    }
    /**
     * An XML absHeaderAggregate(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class AbsHeaderAggregateImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.AbsHeaderAggregateDocument.AbsHeaderAggregate
    {
        
        public AbsHeaderAggregateImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName USERNAME$0 = 
            new javax.xml.namespace.QName("", "username");
        private static final javax.xml.namespace.QName PASSWORD$2 = 
            new javax.xml.namespace.QName("", "password");
        
        
        /**
         * Gets the "username" element
         */
        public java.lang.String getUsername()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(USERNAME$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "username" element
         */
        public org.apache.xmlbeans.XmlString xgetUsername()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(USERNAME$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "username" element
         */
        public void setUsername(java.lang.String username)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(USERNAME$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(USERNAME$0);
                }
                target.setStringValue(username);
            }
        }
        
        /**
         * Sets (as xml) the "username" element
         */
        public void xsetUsername(org.apache.xmlbeans.XmlString username)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(USERNAME$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(USERNAME$0);
                }
                target.set(username);
            }
        }
        
        /**
         * Gets the "password" element
         */
        public java.lang.String getPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PASSWORD$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        /**
         * Gets (as xml) the "password" element
         */
        public org.apache.xmlbeans.XmlString xgetPassword()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PASSWORD$2, 0);
                return target;
            }
        }
        
        /**
         * Sets the "password" element
         */
        public void setPassword(java.lang.String password)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(PASSWORD$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(PASSWORD$2);
                }
                target.setStringValue(password);
            }
        }
        
        /**
         * Sets (as xml) the "password" element
         */
        public void xsetPassword(org.apache.xmlbeans.XmlString password)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlString target = null;
                target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(PASSWORD$2, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(PASSWORD$2);
                }
                target.set(password);
            }
        }
    }
}
