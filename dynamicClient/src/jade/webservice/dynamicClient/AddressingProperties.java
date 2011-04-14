/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2002 TILAB

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/
package jade.webservice.dynamicClient;

import java.util.List;

import org.apache.axis.message.addressing.Constants;
import org.apache.axis.message.addressing.ReferenceParametersType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.message.addressing.RelatesTo;

public class AddressingProperties {

	public static final String VERSION_2003_03 = Constants.NS_URI_ADDRESSING_2003_03;
	public static final String VERSION_2004_03 = Constants.NS_URI_ADDRESSING_2004_03;
	public static final String VERSION_2004_08 = Constants.NS_URI_ADDRESSING_2004_08;
	public static final String VERSION_2005_08 = Constants.NS_URI_ADDRESSING_2005_08;
	
	private Boolean sendDefaultMessageID;
	private Boolean sendDefaultFrom;
	private Boolean sendDefaultTo;
	private String version;
	private String action;
	private String messageID;
	private String faultTo;
	private String from;
	private String to;
	private Boolean mustUnderstand;
	private String replyTo;
	private ReferenceParametersType referenceParametersType;
	private ReferencePropertiesType referencePropertiesType;
	private List<RelatesTo> relatesTo;


	public AddressingProperties() {
	}

	public Boolean isSendDefaultMessageID() {
		return sendDefaultMessageID;
	}

	public void setSendDefaultMessageID(Boolean sendDefaultMessageID) {
		this.sendDefaultMessageID = sendDefaultMessageID;
	}

	public Boolean isSendDefaultFrom() {
		return sendDefaultFrom;
	}

	public void setSendDefaultFrom(Boolean sendDefaultFrom) {
		this.sendDefaultFrom = sendDefaultFrom;
	}

	public Boolean isSendDefaultTo() {
		return sendDefaultTo;
	}

	public void setSendDefaultTo(Boolean sendDefaultTo) {
		this.sendDefaultTo = sendDefaultTo;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMessageID() {
		return messageID;
	}

	public void setMessageID(String messageID) {
		this.messageID = messageID;
	}

	public String getFaultTo() {
		return faultTo;
	}

	public void setFaultTo(String faultTo) {
		this.faultTo = faultTo;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Boolean isMustUnderstand() {
		return mustUnderstand;
	}

	public void setMustUnderstand(Boolean mustUnderstand) {
		this.mustUnderstand = mustUnderstand;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}

	public ReferenceParametersType getReferenceParametersType() {
		return referenceParametersType;
	}

	public void setReferenceParametersType(ReferenceParametersType referenceParametersType) {
		this.referenceParametersType = referenceParametersType;
	}

	public ReferencePropertiesType getReferencePropertiesType() {
		return referencePropertiesType;
	}

	public void setReferencePropertiesType(ReferencePropertiesType referencePropertiesType) {
		this.referencePropertiesType = referencePropertiesType;
	}

	public List<RelatesTo> getRelatesTo() {
		return relatesTo;
	}

	public void setRelatesTo(List<RelatesTo> relatesTo) {
		this.relatesTo = relatesTo;
	}
}
