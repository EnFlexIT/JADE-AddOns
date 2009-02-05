/*
 * XML Type:  ArrayOfFloat
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.ArrayOfFloat
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * An XML ArrayOfFloat(@urn:tests.webservice.test).
 *
 * This is a complex type.
 */
public class ArrayOfFloatImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.ArrayOfFloat
{
    
    public ArrayOfFloatImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName FLOAT$0 = 
        new javax.xml.namespace.QName("", "float");
    
    
    /**
     * Gets array of all "float" elements
     */
    public float[] getFloatArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(FLOAT$0, targetList);
            float[] result = new float[targetList.size()];
            for (int i = 0, len = targetList.size() ; i < len ; i++)
                result[i] = ((org.apache.xmlbeans.SimpleValue)targetList.get(i)).getFloatValue();
            return result;
        }
    }
    
    /**
     * Gets ith "float" element
     */
    public float getFloatArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FLOAT$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target.getFloatValue();
        }
    }
    
    /**
     * Gets (as xml) array of all "float" elements
     */
    public org.apache.xmlbeans.XmlFloat[] xgetFloatArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(FLOAT$0, targetList);
            org.apache.xmlbeans.XmlFloat[] result = new org.apache.xmlbeans.XmlFloat[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets (as xml) ith "float" element
     */
    public org.apache.xmlbeans.XmlFloat xgetFloatArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(FLOAT$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return (org.apache.xmlbeans.XmlFloat)target;
        }
    }
    
    /**
     * Returns number of "float" element
     */
    public int sizeOfFloatArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(FLOAT$0);
        }
    }
    
    /**
     * Sets array of all "float" element
     */
    public void setFloatArray(float[] xfloatArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(xfloatArray, FLOAT$0);
        }
    }
    
    /**
     * Sets ith "float" element
     */
    public void setFloatArray(int i, float xfloat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().find_element_user(FLOAT$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.setFloatValue(xfloat);
        }
    }
    
    /**
     * Sets (as xml) array of all "float" element
     */
    public void xsetFloatArray(org.apache.xmlbeans.XmlFloat[]xfloatArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(xfloatArray, FLOAT$0);
        }
    }
    
    /**
     * Sets (as xml) ith "float" element
     */
    public void xsetFloatArray(int i, org.apache.xmlbeans.XmlFloat xfloat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().find_element_user(FLOAT$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(xfloat);
        }
    }
    
    /**
     * Inserts the value as the ith "float" element
     */
    public void insertFloat(int i, float xfloat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = 
                (org.apache.xmlbeans.SimpleValue)get_store().insert_element_user(FLOAT$0, i);
            target.setFloatValue(xfloat);
        }
    }
    
    /**
     * Appends the value as the last "float" element
     */
    public void addFloat(float xfloat)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.SimpleValue target = null;
            target = (org.apache.xmlbeans.SimpleValue)get_store().add_element_user(FLOAT$0);
            target.setFloatValue(xfloat);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "float" element
     */
    public org.apache.xmlbeans.XmlFloat insertNewFloat(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().insert_element_user(FLOAT$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "float" element
     */
    public org.apache.xmlbeans.XmlFloat addNewFloat()
    {
        synchronized (monitor())
        {
            check_orphaned();
            org.apache.xmlbeans.XmlFloat target = null;
            target = (org.apache.xmlbeans.XmlFloat)get_store().add_element_user(FLOAT$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "float" element
     */
    public void removeFloat(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(FLOAT$0, i);
        }
    }
}
