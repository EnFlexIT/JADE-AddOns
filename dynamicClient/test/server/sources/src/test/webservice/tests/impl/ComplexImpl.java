/*
 * XML Type:  complex
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.Complex
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * An XML complex(@urn:tests.webservice.test).
 *
 * This is a complex type.
 */
public class ComplexImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.Complex
{
    
    public ComplexImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName REAL$0 = 
        new javax.xml.namespace.QName("", "real");
    private static final javax.xml.namespace.QName IMMAGINARY$2 = 
        new javax.xml.namespace.QName("", "immaginary");
    
    
    /**
     * Gets the "real" element
     */
    public float getReal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REAL$0, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "real" element
     */
    public org.apache.xmlbeans.XmlFloat xgetReal()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(REAL$0, 0);
            return target;
        }
    }
    
    /**
     * Sets the "real" element
     */
    public void setReal(float real)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(REAL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(REAL$0);
            }
            target.setFloatValue(real);
        }
    }
    
    /**
     * Sets (as xml) the "real" element
     */
    public void xsetReal(org.apache.xmlbeans.XmlFloat real)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(REAL$0, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(REAL$0);
            }
            target.set(real);
        }
    }
    
    /**
     * Gets the "immaginary" element
     */
    public float getImmaginary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IMMAGINARY$2, 0);
            if (target == null)
            {
                return 0.0f;
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) the "immaginary" element
     */
    public org.apache.xmlbeans.XmlFloat xgetImmaginary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(IMMAGINARY$2, 0);
            return target;
        }
    }
    
    /**
     * True if has "immaginary" element
     */
    public boolean isSetImmaginary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(IMMAGINARY$2) != 0;
        }
    }
    
    /**
     * Sets the "immaginary" element
     */
    public void setImmaginary(float immaginary)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(IMMAGINARY$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(IMMAGINARY$2);
            }
            target.setFloatValue(immaginary);
        }
    }
    
    /**
     * Sets (as xml) the "immaginary" element
     */
    public void xsetImmaginary(org.apache.xmlbeans.XmlFloat immaginary)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(IMMAGINARY$2, 0);
            if (target == null)
            {
                target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(IMMAGINARY$2);
            }
            target.set(immaginary);
        }
    }
    
    /**
     * Unsets the "immaginary" element
     */
    public void unsetImmaginary()
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(IMMAGINARY$2, 0);
        }
    }
}
