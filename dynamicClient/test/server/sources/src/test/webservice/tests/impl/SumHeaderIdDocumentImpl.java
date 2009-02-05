/*
 * An XML document type.
 * Localname: sumHeaderId
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.SumHeaderIdDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one sumHeaderId(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class SumHeaderIdDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumHeaderIdDocument
{
    
    public SumHeaderIdDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SUMHEADERID$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "sumHeaderId");
    
    
    /**
     * Gets the "sumHeaderId" element
     */
    public int getSumHeaderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUMHEADERID$0, 0);
            if (target == null)
            {
                return 0;
            }
            return target.getIntValue();
        }
    }
    
    /**
     * Gets (as xml) the "sumHeaderId" element
     */
    public org.apache.xmlbeans.XmlInt xgetSumHeaderId()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(SUMHEADERID$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "sumHeaderId" element
     */
    public void setSumHeaderId(int sumHeaderId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(SUMHEADERID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(SUMHEADERID$0);
            }
            target.setIntValue(sumHeaderId);
        }
    }
    
    /**
     * Sets (as xml) the "sumHeaderId" element
     */
    public void xsetSumHeaderId(org.apache.xmlbeans.XmlInt sumHeaderId)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlInt target = null;
            target = (org.apache.xmlbeans.XmlInt)get_store().find_element_user(SUMHEADERID$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlInt)get_store().add_element_user(SUMHEADERID$0);
            }
            target.set(sumHeaderId);
        }
    }
}
