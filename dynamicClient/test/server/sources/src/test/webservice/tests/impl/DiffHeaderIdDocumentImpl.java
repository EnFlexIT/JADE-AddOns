/*
 * An XML document type.
 * Localname: diffHeaderId
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.DiffHeaderIdDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one diffHeaderId(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class DiffHeaderIdDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.DiffHeaderIdDocument
{
    
    public DiffHeaderIdDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName DIFFHEADERID$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "diffHeaderId");
    
    
    /**
     * Gets the "diffHeaderId" element
     */
    public int getDiffHeaderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DIFFHEADERID$0, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "diffHeaderId" element
     */
    public org.apache.xmlbeans.XmlInt xgetDiffHeaderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(DIFFHEADERID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "diffHeaderId" element
     */
    public void setDiffHeaderId(int diffHeaderId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(DIFFHEADERID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(DIFFHEADERID$0);
            }
            target.setIntValue(diffHeaderId);
        }
    }
    
    /**
     * Sets (as xml) the "diffHeaderId" element
     */
    public void xsetDiffHeaderId(org.apache.xmlbeans.XmlInt diffHeaderId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(DIFFHEADERID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(DIFFHEADERID$0);
            }
            target.set(diffHeaderId);
        }
    }
}
