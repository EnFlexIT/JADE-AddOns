/*
 * An XML document type.
 * Localname: sumHeaderUsername
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.SumHeaderUsernameDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one sumHeaderUsername(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class SumHeaderUsernameDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumHeaderUsernameDocument
{
    
    public SumHeaderUsernameDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SUMHEADERUSERNAME$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "sumHeaderUsername");
    
    
    /**
     * Gets the "sumHeaderUsername" element
     */
    public java.lang.String getSumHeaderUsername()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUMHEADERUSERNAME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "sumHeaderUsername" element
     */
    public org.apache.xmlbeans.XmlString xgetSumHeaderUsername()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUMHEADERUSERNAME$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "sumHeaderUsername" element
     */
    public void setSumHeaderUsername(java.lang.String sumHeaderUsername)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUMHEADERUSERNAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUMHEADERUSERNAME$0);
            }
            target.setStringValue(sumHeaderUsername);
        }
    }
    
    /**
     * Sets (as xml) the "sumHeaderUsername" element
     */
    public void xsetSumHeaderUsername(org.apache.xmlbeans.XmlString sumHeaderUsername)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUMHEADERUSERNAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SUMHEADERUSERNAME$0);
            }
            target.set(sumHeaderUsername);
        }
    }
}
