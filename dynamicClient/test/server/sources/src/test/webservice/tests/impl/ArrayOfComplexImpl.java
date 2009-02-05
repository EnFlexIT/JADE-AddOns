/*
 * XML Type:  ArrayOfComplex
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.ArrayOfComplex
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * An XML ArrayOfComplex(@urn:tests.webservice.test).
 *
 * This is a complex type.
 */
public class ArrayOfComplexImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.ArrayOfComplex
{
    
    public ArrayOfComplexImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName COMPLEX$0 = 
        new javax.xml.namespace.QName("", "complex");
    
    
    /**
     * Gets array of all "complex" elements
     */
    public test.webservice.tests.Complex[] getComplexArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            java.util.List targetList = new java.util.ArrayList();
            get_store().find_all_element_users(COMPLEX$0, targetList);
            test.webservice.tests.Complex[] result = new test.webservice.tests.Complex[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    /**
     * Gets ith "complex" element
     */
    public test.webservice.tests.Complex getComplexArray(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.Complex target = null;
            target = (test.webservice.tests.Complex)get_store().find_element_user(COMPLEX$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    /**
     * Returns number of "complex" element
     */
    public int sizeOfComplexArray()
    {
        synchronized (monitor())
        {
            check_orphaned();
            return get_store().count_elements(COMPLEX$0);
        }
    }
    
    /**
     * Sets array of all "complex" element
     */
    public void setComplexArray(test.webservice.tests.Complex[] complexArray)
    {
        synchronized (monitor())
        {
            check_orphaned();
            arraySetterHelper(complexArray, COMPLEX$0);
        }
    }
    
    /**
     * Sets ith "complex" element
     */
    public void setComplexArray(int i, test.webservice.tests.Complex complex)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.Complex target = null;
            target = (test.webservice.tests.Complex)get_store().find_element_user(COMPLEX$0, i);
            if (target == null)
            {
                throw new IndexOutOfBoundsException();
            }
            target.set(complex);
        }
    }
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "complex" element
     */
    public test.webservice.tests.Complex insertNewComplex(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.Complex target = null;
            target = (test.webservice.tests.Complex)get_store().insert_element_user(COMPLEX$0, i);
            return target;
        }
    }
    
    /**
     * Appends and returns a new empty value (as xml) as the last "complex" element
     */
    public test.webservice.tests.Complex addNewComplex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.Complex target = null;
            target = (test.webservice.tests.Complex)get_store().add_element_user(COMPLEX$0);
            return target;
        }
    }
    
    /**
     * Removes the ith "complex" element
     */
    public void removeComplex(int i)
    {
        synchronized (monitor())
        {
            check_orphaned();
            get_store().remove_element(COMPLEX$0, i);
        }
    }
}
