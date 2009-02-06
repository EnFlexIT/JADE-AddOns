/**
 * AgentInfo.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Jan 16, 2009 (03:26:47 CET) WSDL2Java emitter.
 */

package test.webservice.tests;

public class AgentInfo  implements java.io.Serializable {
    private test.webservice.tests.AgentIdentifier agentAid;

    private java.util.Calendar startDate;

    public AgentInfo() {
    }

    public AgentInfo(
           test.webservice.tests.AgentIdentifier agentAid,
           java.util.Calendar startDate) {
           this.agentAid = agentAid;
           this.startDate = startDate;
    }


    /**
     * Gets the agentAid value for this AgentInfo.
     * 
     * @return agentAid
     */
    public test.webservice.tests.AgentIdentifier getAgentAid() {
        return agentAid;
    }


    /**
     * Sets the agentAid value for this AgentInfo.
     * 
     * @param agentAid
     */
    public void setAgentAid(test.webservice.tests.AgentIdentifier agentAid) {
        this.agentAid = agentAid;
    }


    /**
     * Gets the startDate value for this AgentInfo.
     * 
     * @return startDate
     */
    public java.util.Calendar getStartDate() {
        return startDate;
    }


    /**
     * Sets the startDate value for this AgentInfo.
     * 
     * @param startDate
     */
    public void setStartDate(java.util.Calendar startDate) {
        this.startDate = startDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AgentInfo)) return false;
        AgentInfo other = (AgentInfo) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.agentAid==null && other.getAgentAid()==null) || 
             (this.agentAid!=null &&
              this.agentAid.equals(other.getAgentAid()))) &&
            ((this.startDate==null && other.getStartDate()==null) || 
             (this.startDate!=null &&
              this.startDate.equals(other.getStartDate())));
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
        if (getAgentAid() != null) {
            _hashCode += getAgentAid().hashCode();
        }
        if (getStartDate() != null) {
            _hashCode += getStartDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AgentInfo.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("urn:tests.webservice.test", "agentInfo"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("agentAid");
        elemField.setXmlName(new javax.xml.namespace.QName("", "agentAid"));
        elemField.setXmlType(new javax.xml.namespace.QName("urn:tests.webservice.test", "agent-identifier"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("startDate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "startDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"));
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
    	StringBuilder sb = new StringBuilder("AgentInfo {agentAid=");
    	sb.append(agentAid);
    	sb.append(" startDate=");
    	sb.append(startDate);
    	sb.append('}');
    	return sb.toString();
    }
}
