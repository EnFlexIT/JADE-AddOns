/*
 * An XML document type.
 * Localname: sumComplex
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.SumComplexDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests.impl;
/**
 * A document containing one sumComplex(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public class SumComplexDocumentImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumComplexDocument
{
    
    public SumComplexDocumentImpl(org.apache.xmlbeans.SchemaType sType)
    {
        super(sType);
    }
    
    private static final javax.xml.namespace.QName SUMCOMPLEX$0 = 
        new javax.xml.namespace.QName("urn:tests.webservice.test", "sumComplex");
    
    
    /**
     * Gets the "sumComplex" element
     */
    public test.webservice.tests.SumComplexDocument.SumComplex getSumComplex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumComplexDocument.SumComplex target = null;
            target = (test.webservice.tests.SumComplexDocument.SumComplex)get_store().find_element_user(SUMCOMPLEX$0, 0);
            if (target == null)
            {
                return null;
            }
            return target;
        }
    }
    
    /**
     * Sets the "sumComplex" element
     */
    public void setSumComplex(test.webservice.tests.SumComplexDocument.SumComplex sumComplex)
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumComplexDocument.SumComplex target = null;
            target = (test.webservice.tests.SumComplexDocument.SumComplex)get_store().find_element_user(SUMCOMPLEX$0, 0);
            if (target == null)
            {
                target = (test.webservice.tests.SumComplexDocument.SumComplex)get_store().add_element_user(SUMCOMPLEX$0);
            }
            target.set(sumComplex);
        }
    }
    
    /**
     * Appends and returns a new empty "sumComplex" element
     */
    public test.webservice.tests.SumComplexDocument.SumComplex addNewSumComplex()
    {
        synchronized (monitor())
        {
            check_orphaned();
            test.webservice.tests.SumComplexDocument.SumComplex target = null;
            target = (test.webservice.tests.SumComplexDocument.SumComplex)get_store().add_element_user(SUMCOMPLEX$0);
            return target;
        }
    }
    /**
     * An XML sumComplex(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public static class SumComplexImpl extends org.apache.xmlbeans.impl.values.XmlComplexContentImpl implements test.webservice.tests.SumComplexDocument.SumComplex
    {
        
        public SumComplexImpl(org.apache.xmlbeans.SchemaType sType)
        {
            super(sType);
        }
        
        private static final javax.xml.namespace.QName FIRSTCOMPLEXELEMENT$0 = 
            new javax.xml.namespace.QName("", "firstComplexElement");
        private static final javax.xml.namespace.QName SECONDCOMPLEXELEMENT$2 = 
            new javax.xml.namespace.QName("", "secondComplexElement");
        
        
        /**
         * Gets the "firstComplexElement" element
         */
        public test.webservice.tests.Complex getFirstComplexElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(FIRSTCOMPLEXELEMENT$0, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "firstComplexElement" element
         */
        public void setFirstComplexElement(test.webservice.tests.Complex firstComplexElement)
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(FIRSTCOMPLEXELEMENT$0, 0);
                if (target == null)
                {
                    target = (test.webservice.tests.Complex)get_store().add_element_user(FIRSTCOMPLEXELEMENT$0);
                }
                target.set(firstComplexElement);
            }
        }
        
        /**
         * Appends and returns a new empty "firstComplexElement" element
         */
        public test.webservice.tests.Complex addNewFirstComplexElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().add_element_user(FIRSTCOMPLEXELEMENT$0);
                return target;
            }
        }
        
        /**
         * Gets the "secondComplexElement" element
         */
        public test.webservice.tests.Complex getSecondComplexElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(SECONDCOMPLEXELEMENT$2, 0);
                if (target == null)
                {
                    return null;
                }
                return target;
            }
        }
        
        /**
         * Sets the "secondComplexElement" element
         */
        public void setSecondComplexElement(test.webservice.tests.Complex secondComplexElement)
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().find_element_user(SECONDCOMPLEXELEMENT$2, 0);
                if (target == null)
                {
                    target = (test.webservice.tests.Complex)get_store().add_element_user(SECONDCOMPLEXELEMENT$2);
                }
                target.set(secondComplexElement);
            }
        }
        
        /**
         * Appends and returns a new empty "secondComplexElement" element
         */
        public test.webservice.tests.Complex addNewSecondComplexElement()
        {
            synchronized (monitor())
            {
                check_orphaned();
                test.webservice.tests.Complex target = null;
                target = (test.webservice.tests.Complex)get_store().add_element_user(SECONDCOMPLEXELEMENT$2);
                return target;
            }
        }
    }
}
