/**
 * Complex.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Nov 26, 2008 (09:27:08 GMT) WSDL2Java emitter.
 */

package webservice.math;

public class Complex  implements java.io.Serializable {

	private float real;

    private float immaginary;

    public Complex() {
    }

    public Complex(
           float real,
           float immaginary) {
           this.real = real;
           this.immaginary = immaginary;
    }


    /**
     * Gets the real value for this Complex.
     * 
     * @return real
     */
    public float getReal() {
        return real;
    }


    /**
     * Sets the real value for this Complex.
     * 
     * @param real
     */
    public void setReal(float real) {
        this.real = real;
    }


    /**
     * Gets the immaginary value for this Complex.
     * 
     * @return immaginary
     */
    public float getImmaginary() {
        return immaginary;
    }


    /**
     * Sets the immaginary value for this Complex.
     * 
     * @param immaginary
     */
    public void setImmaginary(float immaginary) {
        this.immaginary = immaginary;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Complex)) return false;
        Complex other = (Complex) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.real == other.getReal() &&
            this.immaginary == other.getImmaginary();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += new Float(getReal()).hashCode();
        _hashCode += new Float(getImmaginary()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Complex.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:tests.webservice.test", "complex"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("real");
        elemField.setXmlName(new javax.xml.namespace.QName("", "real"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("immaginary");
        elemField.setXmlName(new javax.xml.namespace.QName("", "immaginary"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

    @Override
	public String toString() {
		return real+"+j"+immaginary;
	}
}
