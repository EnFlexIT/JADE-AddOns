/**
 * AgentIdentifier.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Jan 16, 2009 (03:26:47 CET) WSDL2Java emitter.
 */

package test.webservice.tests;

public class AgentIdentifier  implements java.io.Serializable {
    private java.lang.String name;

    private java.lang.String[] addresses;

    private test.webservice.tests.AgentIdentifier[] resolvers;

    public AgentIdentifier() {
    }

    public AgentIdentifier(
           java.lang.String name,
           java.lang.String[] addresses,
           test.webservice.tests.AgentIdentifier[] resolvers) {
           this.name = name;
           this.addresses = addresses;
           this.resolvers = resolvers;
    }


    /**
     * Gets the name value for this AgentIdentifier.
     * 
     * @return name
     */
    public java.lang.String getName() {
        return name;
    }


    /**
     * Sets the name value for this AgentIdentifier.
     * 
     * @param name
     */
    public void setName(java.lang.String name) {
        this.name = name;
    }


    /**
     * Gets the addresses value for this AgentIdentifier.
     * 
     * @return addresses
     */
    public java.lang.String[] getAddresses() {
        return addresses;
    }


    /**
     * Sets the addresses value for this AgentIdentifier.
     * 
     * @param addresses
     */
    public void setAddresses(java.lang.String[] addresses) {
        this.addresses = addresses;
    }


    /**
     * Gets the resolvers value for this AgentIdentifier.
     * 
     * @return resolvers
     */
    public test.webservice.tests.AgentIdentifier[] getResolvers() {
        return resolvers;
    }


    /**
     * Sets the resolvers value for this AgentIdentifier.
     * 
     * @param resolvers
     */
    public void setResolvers(test.webservice.tests.AgentIdentifier[] resolvers) {
        this.resolvers = resolvers;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AgentIdentifier)) return false;
        AgentIdentifier other = (AgentIdentifier) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.name==null && other.getName()==null) || 
             (this.name!=null &&
              this.name.equals(other.getName()))) &&
            ((this.addresses==null && other.getAddresses()==null) || 
             (this.addresses!=null &&
              java.util.Arrays.equals(this.addresses, other.getAddresses()))) &&
            ((this.resolvers==null && other.getResolvers()==null) || 
             (this.resolvers!=null &&
              java.util.Arrays.equals(this.resolvers, other.getResolvers())));
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
        if (getName() != null) {
            _hashCode += getName().hashCode();
        }
        if (getAddresses() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getAddresses());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getAddresses(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getResolvers() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getResolvers());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getResolvers(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AgentIdentifier.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:tests.webservice.test", "agent-identifier"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("name");
        elemField.setXmlName(new javax.xml.namespace.QName("", "name"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("addresses");
        elemField.setXmlName(new javax.xml.namespace.QName("", "addresses"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "string"));
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("resolvers");
        elemField.setXmlName(new javax.xml.namespace.QName("", "resolvers"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:tests.webservice.test", "agent-identifier"));
        elemField.setNillable(false);
        elemField.setItemQName(new javax.xml.namespace.QName("", "agent-identifier"));
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
    	StringBuilder sb = new StringBuilder("AgentIdentifier {name=");
    	sb.append(name);
    	sb.append(" addresses=");
    	if (addresses == null) {
    		sb.append("(null)");
    	} else {
    		sb.append('[');
    		for (String address: addresses) {
    			sb.append(address);
    			sb.append(' ');
    		}
    		sb.append(']');
    	}
    	sb.append(" resolvers=");
    	if (resolvers == null) {
    		sb.append("(null)");
    	} else {
    		sb.append('[');
    		for (AgentIdentifier resolver: resolvers) {
    			if (equals(resolver)) {
    				sb.append("(self)");
    			} else {
    				sb.append(resolver);
    			}
    			sb.append(' ');
    		}
    		sb.append(']');
    	}
    	return sb.toString();
    }
}
