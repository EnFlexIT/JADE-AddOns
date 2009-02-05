/*
 * XML Type:  ArrayOfComplex
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.ArrayOfComplex
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests;


/**
 * An XML ArrayOfComplex(@urn:tests.webservice.test).
 *
 * This is a complex type.
 */
public interface ArrayOfComplex extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(ArrayOfComplex.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF5E7FC10BAA5B40F53984B63FEAE70E0").resolveHandle("arrayofcomplexc432type");
    
    /**
     * Gets array of all "complex" elements
     */
    test.webservice.tests.Complex[] getComplexArray();
    
    /**
     * Gets ith "complex" element
     */
    test.webservice.tests.Complex getComplexArray(int i);
    
    /**
     * Returns number of "complex" element
     */
    int sizeOfComplexArray();
    
    /**
     * Sets array of all "complex" element
     */
    void setComplexArray(test.webservice.tests.Complex[] complexArray);
    
    /**
     * Sets ith "complex" element
     */
    void setComplexArray(int i, test.webservice.tests.Complex complex);
    
    /**
     * Inserts and returns a new empty value (as xml) as the ith "complex" element
     */
    test.webservice.tests.Complex insertNewComplex(int i);
    
    /**
     * Appends and returns a new empty value (as xml) as the last "complex" element
     */
    test.webservice.tests.Complex addNewComplex();
    
    /**
     * Removes the ith "complex" element
     */
    void removeComplex(int i);
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static test.webservice.tests.ArrayOfComplex newInstance() {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static test.webservice.tests.ArrayOfComplex newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static test.webservice.tests.ArrayOfComplex parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static test.webservice.tests.ArrayOfComplex parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static test.webservice.tests.ArrayOfComplex parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static test.webservice.tests.ArrayOfComplex parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static test.webservice.tests.ArrayOfComplex parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (test.webservice.tests.ArrayOfComplex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
