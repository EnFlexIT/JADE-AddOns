/*
 * An XML document type.
 * Localname: sumComplex
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.SumComplexDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests;


/**
 * A document containing one sumComplex(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public interface SumComplexDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SumComplexDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF5E7FC10BAA5B40F53984B63FEAE70E0").resolveHandle("sumcomplex71c7doctype");
    
    /**
     * Gets the "sumComplex" element
     */
    test.webservice.tests.SumComplexDocument.SumComplex getSumComplex();
    
    /**
     * Sets the "sumComplex" element
     */
    void setSumComplex(test.webservice.tests.SumComplexDocument.SumComplex sumComplex);
    
    /**
     * Appends and returns a new empty "sumComplex" element
     */
    test.webservice.tests.SumComplexDocument.SumComplex addNewSumComplex();
    
    /**
     * An XML sumComplex(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public interface SumComplex extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(SumComplex.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF5E7FC10BAA5B40F53984B63FEAE70E0").resolveHandle("sumcomplex4426elemtype");
        
        /**
         * Gets the "firstComplexElement" element
         */
        test.webservice.tests.Complex getFirstComplexElement();
        
        /**
         * Sets the "firstComplexElement" element
         */
        void setFirstComplexElement(test.webservice.tests.Complex firstComplexElement);
        
        /**
         * Appends and returns a new empty "firstComplexElement" element
         */
        test.webservice.tests.Complex addNewFirstComplexElement();
        
        /**
         * Gets the "secondComplexElement" element
         */
        test.webservice.tests.Complex getSecondComplexElement();
        
        /**
         * Sets the "secondComplexElement" element
         */
        void setSecondComplexElement(test.webservice.tests.Complex secondComplexElement);
        
        /**
         * Appends and returns a new empty "secondComplexElement" element
         */
        test.webservice.tests.Complex addNewSecondComplexElement();
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static test.webservice.tests.SumComplexDocument.SumComplex newInstance() {
              return (test.webservice.tests.SumComplexDocument.SumComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static test.webservice.tests.SumComplexDocument.SumComplex newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (test.webservice.tests.SumComplexDocument.SumComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static test.webservice.tests.SumComplexDocument newInstance() {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static test.webservice.tests.SumComplexDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static test.webservice.tests.SumComplexDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static test.webservice.tests.SumComplexDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static test.webservice.tests.SumComplexDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static test.webservice.tests.SumComplexDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static test.webservice.tests.SumComplexDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static test.webservice.tests.SumComplexDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static test.webservice.tests.SumComplexDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static test.webservice.tests.SumComplexDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static test.webservice.tests.SumComplexDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static test.webservice.tests.SumComplexDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static test.webservice.tests.SumComplexDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static test.webservice.tests.SumComplexDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static test.webservice.tests.SumComplexDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static test.webservice.tests.SumComplexDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static test.webservice.tests.SumComplexDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static test.webservice.tests.SumComplexDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (test.webservice.tests.SumComplexDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
