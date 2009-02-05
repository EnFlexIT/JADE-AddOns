/*
 * An XML document type.
 * Localname: sumHeaderPassword
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.SumHeaderPasswordDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one sumHeaderPassword(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class SumHeaderPasswordDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumHeaderPasswordDocument
{
    
    public SumHeaderPasswordDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SUMHEADERPASSWORD$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "sumHeaderPassword");
    
    
    /**
     * Gets the "sumHeaderPassword" element
     */
    public java.lang.String getSumHeaderPassword()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUMHEADERPASSWORD$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "sumHeaderPassword" element
     */
    public org.apache.xmlbeans.XmlString xgetSumHeaderPassword()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUMHEADERPASSWORD$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "sumHeaderPassword" element
     */
    public void setSumHeaderPassword(java.lang.String sumHeaderPassword)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUMHEADERPASSWORD$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUMHEADERPASSWORD$0);
            }
            target.setStringValue(sumHeaderPassword);
        }
    }
    
    /**
     * Sets (as xml) the "sumHeaderPassword" element
     */
    public void xsetSumHeaderPassword(org.apache.xmlbeans.XmlString sumHeaderPassword)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(SUMHEADERPASSWORD$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(SUMHEADERPASSWORD$0);
            }
            target.set(sumHeaderPassword);
        }
    }
}
