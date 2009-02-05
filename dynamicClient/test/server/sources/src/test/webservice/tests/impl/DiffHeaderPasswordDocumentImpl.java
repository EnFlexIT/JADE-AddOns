/*
 * An XML document type.
 * Localname: diffHeaderPassword
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.DiffHeaderPasswordDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one diffHeaderPassword(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class DiffHeaderPasswordDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.DiffHeaderPasswordDocument
{
    
    public DiffHeaderPasswordDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DIFFHEADERPASSWORD$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "diffHeaderPassword");
    
    
    /**
     * Gets the "diffHeaderPassword" element
     */
    public java.lang.String getDiffHeaderPassword()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DIFFHEADERPASSWORD$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "diffHeaderPassword" element
     */
    public org.apache.xmlbeans.XmlString xgetDiffHeaderPassword()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DIFFHEADERPASSWORD$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "diffHeaderPassword" element
     */
    public void setDiffHeaderPassword(java.lang.String diffHeaderPassword)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DIFFHEADERPASSWORD$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DIFFHEADERPASSWORD$0);
            }
            target.setStringValue(diffHeaderPassword);
        }
    }
    
    /**
     * Sets (as xml) the "diffHeaderPassword" element
     */
    public void xsetDiffHeaderPassword(org.apache.xmlbeans.XmlString diffHeaderPassword)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DIFFHEADERPASSWORD$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DIFFHEADERPASSWORD$0);
            }
            target.set(diffHeaderPassword);
        }
    }
}
