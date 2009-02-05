/*
 * XML Type:  complex
 * Namespace: urn:tests.webservice.test
 * Java type: test.webservice.tests.Complex
 *
 * Automatically generated - do not modify.
 */
package test.webservice.tests;


/**
 * An XML complex(@urn:tests.webservice.test).
 *
 * This is a complex type.
 */
public interface Complex extends org.apache.xmlbeans.XmlObject
{
    public static final org.apache.xmlbeans.SchemaType type = (org.apache.xmlbeans.SchemaType)
        org.apache.xmlbeans.XmlBeans.typeSystemForClassLoader(Complex.class.getClassLoader(), "schemaorg_apache_xmlbeans.system.sF5E7FC10BAA5B40F53984B63FEAE70E0").resolveHandle("complex2190type");
    
    /**
     * Gets the "real" element
     */
    float getReal();
    
    /**
     * Gets (as xml) the "real" element
     */
    org.apache.xmlbeans.XmlFloat xgetReal();
    
    /**
     * Sets the "real" element
     */
    void setReal(float real);
    
    /**
     * Sets (as xml) the "real" element
     */
    void xsetReal(org.apache.xmlbeans.XmlFloat real);
    
    /**
     * Gets the "immaginary" element
     */
    float getImmaginary();
    
    /**
     * Gets (as xml) the "immaginary" element
     */
    org.apache.xmlbeans.XmlFloat xgetImmaginary();
    
    /**
     * True if has "immaginary" element
     */
    boolean isSetImmaginary();
    
    /**
     * Sets the "immaginary" element
     */
    void setImmaginary(float immaginary);
    
    /**
     * Sets (as xml) the "immaginary" element
     */
    void xsetImmaginary(org.apache.xmlbeans.XmlFloat immaginary);
    
    /**
     * Unsets the "immaginary" element
     */
    void unsetImmaginary();
    
    /**
     * A factory class with static methods for creating instances
     * of this type.
     */
    
    public static final class Factory
    {
        public static test.webservice.tests.Complex newInstance() {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, null ); }
        
        public static test.webservice.tests.Complex newInstance(org.apache.xmlbeans.XmlOptions options) {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newInstance( type, options ); }
        
        /** @param xmlAsString the string value to parse */
        public static test.webservice.tests.Complex parse(java.lang.String xmlAsString) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, null ); }
        
        public static test.webservice.tests.Complex parse(java.lang.String xmlAsString, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xmlAsString, type, options ); }
        
        /** @param file the file from which to load an xml document */
        public static test.webservice.tests.Complex parse(java.io.File file) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, null ); }
        
        public static test.webservice.tests.Complex parse(java.io.File file, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( file, type, options ); }
        
        public static test.webservice.tests.Complex parse(java.net.URL u) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, null ); }
        
        public static test.webservice.tests.Complex parse(java.net.URL u, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( u, type, options ); }
        
        public static test.webservice.tests.Complex parse(java.io.InputStream is) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, null ); }
        
        public static test.webservice.tests.Complex parse(java.io.InputStream is, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( is, type, options ); }
        
        public static test.webservice.tests.Complex parse(java.io.Reader r) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, null ); }
        
        public static test.webservice.tests.Complex parse(java.io.Reader r, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, java.io.IOException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( r, type, options ); }
        
        public static test.webservice.tests.Complex parse(javax.xml.stream.XMLStreamReader sr) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, null ); }
        
        public static test.webservice.tests.Complex parse(javax.xml.stream.XMLStreamReader sr, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( sr, type, options ); }
        
        public static test.webservice.tests.Complex parse(org.w3c.dom.Node node) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, null ); }
        
        public static test.webservice.tests.Complex parse(org.w3c.dom.Node node, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( node, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static test.webservice.tests.Complex parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static test.webservice.tests.Complex parse(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return (test.webservice.tests.Complex) org.apache.xmlbeans.XmlBeans.getContextTypeLoader().parse( xis, type, options ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, null ); }
        
        /** @deprecated {@link org.apache.xmlbeans.xml.stream.XMLInputStream} */
        public static org.apache.xmlbeans.xml.stream.XMLInputStream newValidatingXMLInputStream(org.apache.xmlbeans.xml.stream.XMLInputStream xis, org.apache.xmlbeans.XmlOptions options) throws org.apache.xmlbeans.XmlException, org.apache.xmlbeans.xml.stream.XMLStreamException {
          return org.apache.xmlbeans.XmlBeans.getContextTypeLoader().newValidatingXMLInputStream( xis, type, options ); }
        
        private Factory() { } // No instance of this class allowed
    }
}
