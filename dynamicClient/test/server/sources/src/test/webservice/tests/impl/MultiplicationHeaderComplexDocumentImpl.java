/*
 * An XML document type.
 * Localname: multiplicationHeaderComplex
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.MultiplicationHeaderComplexDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one multiplicationHeaderComplex(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class MultiplicationHeaderComplexDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.MultiplicationHeaderComplexDocument
{
    
    public MultiplicationHeaderComplexDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName MULTIPLICATIONHEADERCOMPLEX$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "multiplicationHeaderComplex");
    
    
    /**
     * Gets the "multiplicationHeaderComplex" element
     */
    public test.webservice.tests.Complex getMultiplicationHeaderComplex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.Complex target = null;
            target = (test.webservice.tests.Complex)get_store().find_element_user(MULTIPLICATIONHEADERCOMPLEX$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "multiplicationHeaderComplex" element
     */
    public void setMultiplicationHeaderComplex(test.webservice.tests.Complex multiplicationHeaderComplex)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.Complex target = null;
            target = (test.webservice.tests.Complex)get_store().find_element_user(MULTIPLICATIONHEADERCOMPLEX$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.Complex)get_store().add_element_user(MULTIPLICATIONHEADERCOMPLEX$0);
            }
            target.set(multiplicationHeaderComplex);
        }
    }
    
    /**
     * Appends and returns a new empty "multiplicationHeaderComplex" element
     */
    public test.webservice.tests.Complex addNewMultiplicationHeaderComplex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.Complex target = null;
            target = (test.webservice.tests.Complex)get_store().add_element_user(MULTIPLICATIONHEADERCOMPLEX$0);
            return target;
        }
    }
}
