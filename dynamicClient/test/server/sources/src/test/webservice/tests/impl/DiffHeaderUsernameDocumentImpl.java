/*
 * An XML document type.
 * Localname: diffHeaderUsername
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.DiffHeaderUsernameDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one diffHeaderUsername(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class DiffHeaderUsernameDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.DiffHeaderUsernameDocument
{
    
    public DiffHeaderUsernameDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DIFFHEADERUSERNAME$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "diffHeaderUsername");
    
    
    /**
     * Gets the "diffHeaderUsername" element
     */
    public java.lang.String getDiffHeaderUsername()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DIFFHEADERUSERNAME$0, 0);
            if (target == null)
            {
                return null;
            }
            return target.getStringValue();
        }
    }
    
    /**
     * Gets (as xml) the "diffHeaderUsername" element
     */
    public org.apache.xmlbeans.XmlString xgetDiffHeaderUsername()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DIFFHEADERUSERNAME$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "diffHeaderUsername" element
     */
    public void setDiffHeaderUsername(java.lang.String diffHeaderUsername)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DIFFHEADERUSERNAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DIFFHEADERUSERNAME$0);
            }
            target.setStringValue(diffHeaderUsername);
        }
    }
    
    /**
     * Sets (as xml) the "diffHeaderUsername" element
     */
    public void xsetDiffHeaderUsername(org.apache.xmlbeans.XmlString diffHeaderUsername)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlString target = null;
            target = (org.apache.xmlbeans.XmlString)get_store().find_element_user(DIFFHEADERUSERNAME$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlString)get_store().add_element_user(DIFFHEADERUSERNAME$0);
            }
            target.set(diffHeaderUsername);
        }
    }
}
