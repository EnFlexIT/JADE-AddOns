/*
 * An XML document type.
 * Localname: convertDate
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.ConvertDateDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one convertDate(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class ConvertDateDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.ConvertDateDocument
{
    
    public ConvertDateDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName CONVERTDATE$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "convertDate");
    
    
    /**
     * Gets the "convertDate" element
     */
    public test.webservice.tests.ConvertDateDocument.ConvertDate getConvertDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ConvertDateDocument.ConvertDate target = null;
            target = (test.webservice.tests.ConvertDateDocument.ConvertDate)get_store().find_element_user(CONVERTDATE$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "convertDate" element
     */
    public void setConvertDate(test.webservice.tests.ConvertDateDocument.ConvertDate convertDate)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ConvertDateDocument.ConvertDate target = null;
            target = (test.webservice.tests.ConvertDateDocument.ConvertDate)get_store().find_element_user(CONVERTDATE$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.ConvertDateDocument.ConvertDate)get_store().add_element_user(CONVERTDATE$0);
            }
            target.set(convertDate);
        }
    }
    
    /**
     * Appends and returns a new empty "convertDate" element
     */
    public test.webservice.tests.ConvertDateDocument.ConvertDate addNewConvertDate()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.ConvertDateDocument.ConvertDate target = null;
            target = (test.webservice.tests.ConvertDateDocument.ConvertDate)get_store().add_element_user(CONVERTDATE$0);
            return target;
        }
    }
    /**
     * An XML convertDate(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class ConvertDateImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.ConvertDateDocument.ConvertDate
    {
        
        public ConvertDateImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName DATE$0 = 
            new javax.xml.namespace.QName("", "date");
        
        
        /**
         * Gets the "date" element
         */
        public java.util.Calendar getDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATE$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target.getCalendarValue();
            }
        }
        
        /**
         * Gets (as xml) the "date" element
         */
        public org.apache.xmlbeans.XmlDateTime xgetDate()
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlDateTime target = null;
                target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DATE$0, 0);
                return target;
            }
        }
        
        /**
         * Sets the "date" element
         */
        public void setDate(java.util.Calendar date)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.SimpleValue target = null;
                target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DATE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DATE$0);
                }
                target.setCalendarValue(date);
            }
        }
        
        /**
         * Sets (as xml) the "date" element
         */
        public void xsetDate(org.apache.xmlbeans.XmlDateTime date)
        {
            synchronized (monitor())
            {
                check_orphaned();
                org.apache.xmlbeans.XmlDateTime target = null;
                target = (org.apache.xmlbeans.XmlDateTime)get_store().find_element_user(DATE$0, 0);
                if (target == null)
                {
                    target = (org.apache.xmlbeans.XmlDateTime)get_store().add_element_user(DATE$0);
                }
                target.set(date);
            }
        }
    }
}
