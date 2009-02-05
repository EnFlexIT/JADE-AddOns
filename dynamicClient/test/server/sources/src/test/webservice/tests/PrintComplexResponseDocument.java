/*
 * An XML document type.
 * Localname: printComplexResponse
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.PrintComplexResponseDocument
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests;


/**
 * A document containing one printComplexResponse(@urn:tests.webservice.test) element.
 *
 * This is a complex type.
 */
public interface PrintComplexResponseDocument extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(PrintComplexResponseDocument.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF5E7FC10BAA5B40F53984B63FEAE70E0").resolveHandle("printcomplexresponsecc86doctype");
    
    /**
     * Gets the "printComplexResponse" element
     */
    test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse getPrintComplexResponse();
    
    /**
     * Sets the "printComplexResponse" element
     */
    void setPrintComplexResponse(test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse printComplexResponse);
    
    /**
     * Appends and returns a new empty "printComplexResponse" element
     */
    test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse addNewPrintComplexResponse();
    
    /**
     * An XML printComplexResponse(@urn:tests.webservice.test).
     *
     * This is a complex type.
     */
    public interface PrintComplexResponse extends org.apache.xmlbeans.XmlObject
    {
        public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
            org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(PrintComplexResponse.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF5E7FC10BAA5B40F53984B63FEAE70E0").resolveHandle("printcomplexresponse8546elemtype");
        
        /**
         * A factory class with static methods for creating instances
         * of this type.
         */
        
        public static final class Factory
        {
            public static test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse newInstance() {
              return (test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
            
            public static test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse newInstance(org.apache.xmlbeans.XmlOptions options) {
              return (test.webservice.tests.PrintComplexResponseDocument.PrintComplexResponse) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
            
            private Factory() { } // No instance of this class allowed
        }
    }
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static test.webservice.tests.PrintComplexResponseDocument newInstance() {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static test.webservice.tests.PrintComplexResponseDocument parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static test.webservice.tests.PrintComplexResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static test.webservice.tests.PrintComplexResponseDocument parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (test.webservice.tests.PrintComplexResponseDocument) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
